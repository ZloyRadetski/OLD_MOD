package net.portalmod.mixins.entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.common.entities.Fizzleable;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.common.sorted.portal.DiscontinuousLerpPos;
import net.portalmod.common.sorted.portal.ITeleportable;
import net.portalmod.common.sorted.portal.ITeleportable2;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalRenderer;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.core.init.FluidTagInit;
import net.portalmod.core.interfaces.IDiscontinuouslyLerpable;
import net.portalmod.core.interfaces.IDragCancelable;
import net.portalmod.core.interfaces.ITeleportLerpable;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;
import net.portalmod.core.util.RayTraceContextWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({Entity.class})
public abstract class EntityMixin implements ITeleportable, ITeleportable2, IDiscontinuouslyLerpable, ITeleportLerpable {
   @Shadow
   public World field_70170_p;
   @Shadow
   protected boolean field_70122_E;
   private int lastUsedPortal = -1;
   private int justUsedPortal = -1;
   @Unique
   private final Deque<DiscontinuousLerpPos> lerpPosQueue = new ArrayDeque();
   @Unique
   private final Deque<Tuple<Vector3d, Vector3d>> lerpPositions = new ArrayDeque();
   @Unique
   private boolean hasUsedPortal = false;
   @Unique
   Vector3d capturedDelta;

   @Shadow
   protected abstract Vector3d func_225514_a_(Vector3d var1, MoverType var2);

   @Shadow
   public abstract EntitySize func_213305_a(Pose var1);

   @Shadow
   public abstract boolean func_70090_H();

   @Shadow
   public abstract boolean func_210500_b(ITag<Fluid> var1, double var2);

   public void setLastUsedPortal(int lastUsedPortal) {
      this.lastUsedPortal = lastUsedPortal;
   }

   public int getLastUsedPortal() {
      return this.lastUsedPortal;
   }

   public boolean hasLastUsedPortal() {
      return this.lastUsedPortal != -1;
   }

   public void removeLastUsedPortal() {
      this.lastUsedPortal = -1;
   }

   public Deque<DiscontinuousLerpPos> getLerpPosQueue() {
      return this.lerpPosQueue;
   }

   public Deque<Tuple<Vector3d, Vector3d>> getLerpPositions() {
      return this.lerpPositions;
   }

   public boolean hasUsedPortal() {
      return this.hasUsedPortal;
   }

   public void setHasUsedPortal(boolean hasUsedPortal) {
      this.hasUsedPortal = hasUsedPortal;
   }

   public void setJustUsedPortal(int justUsedPortal) {
      this.justUsedPortal = justUsedPortal;
   }

   public int getJustUsedPortal() {
      return this.justUsedPortal;
   }

   public boolean hasJustUsedPortal() {
      return this.justUsedPortal != -1;
   }

   public void removeJustUsedPortal() {
      this.justUsedPortal = -1;
   }

   @Redirect(
      method = {"move"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;maybeBackOffFromEdge(Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/entity/MoverType;)Lnet/minecraft/util/math/vector/Vector3d;"
)
   )
   private Vector3d pmTeleport(Entity instance, Vector3d delta, MoverType moverType) {
      Entity thiss = (Entity)this;
      return this.func_225514_a_(PortalEntity.teleportEntity(thiss, PortalEntity.doFunneling(thiss, delta)), moverType);
   }

   @Inject(
      method = {"move"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/entity/Entity;onGround:Z",
   opcode = 181,
   ordinal = 0,
   shift = Shift.AFTER
)}
   )
   private void pmResetDragOnLand(MoverType moverType, Vector3d delta, CallbackInfo info) {
      if (this.field_70122_E && this instanceof IDragCancelable) {
         ((IDragCancelable)this).pmSetCancelDrag(false);
      }

   }

   @Inject(
      method = {"collide"},
      at = {@At("HEAD")}
   )
   private void pmCaptureCollideDelta(Vector3d delta, CallbackInfoReturnable<Vector3d> info) {
      this.capturedDelta = delta;
   }

   @Redirect(
      method = {"collide"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/AxisAlignedBB;"
)
   )
   private AxisAlignedBB pmGetSquashedHitbox(Entity entity) {
      AxisAlignedBB hitbox = entity.func_174813_aQ();
      if (!(entity instanceof LivingEntity)) {
         return hitbox;
      } else {
         AxisAlignedBB travelAABB = hitbox.func_216361_a(this.capturedDelta);
         boolean tall = hitbox.func_216360_c() > hitbox.func_216364_b();
         boolean flinging = ((Flingable)entity).isFlinging();
         List<PortalEntity> portalsInside = PortalEntity.getPortals(entity.field_70170_p, travelAABB, (portal) -> portal.func_174811_aO().func_176740_k().func_176722_c() && portal.isOpen() && portal.isEntityAlignedToPortal(entity));
         if (tall && flinging && !portalsInside.isEmpty()) {
            double shrink = hitbox.func_216360_c() / (double)2.0F - hitbox.func_216364_b() / (double)2.0F;
            return hitbox.func_72314_b((double)0.0F, -shrink, (double)0.0F);
         } else {
            return hitbox;
         }
      }
   }

   private AxisAlignedBB getBBForPoseAndPos(Pose pose, Vec3 pos) {
      EntitySize entitysize = this.func_213305_a(pose);
      float radius = entitysize.field_220315_a / 2.0F;
      Vector3d min = new Vector3d(pos.x - (double)radius, pos.y, pos.z - (double)radius);
      Vector3d max = new Vector3d(pos.x + (double)radius, pos.y + (double)entitysize.field_220316_b, pos.z + (double)radius);
      return new AxisAlignedBB(min, max);
   }

   @Inject(
      method = {"canEnterPose"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void pmCanEnterPose(Pose newPose, CallbackInfoReturnable<Boolean> info) {
      if (info.getReturnValueZ()) {
         Entity thiss = (Entity)this;
         World level = thiss.field_70170_p;
         Vector3d pos = thiss.func_213303_ch();
         if (pos.equals(ModUtil.getOldPos(thiss))) {
            pos = pos.func_178787_e(thiss.func_213322_ci());
         }

         Pose currentPose = thiss.func_213283_Z();
         AxisAlignedBB currentBB = this.getBBForPoseAndPos(currentPose, new Vec3(pos));
         AxisAlignedBB newBB = this.getBBForPoseAndPos(newPose, new Vec3(pos));
         Vec3 delta = (new Vec3(newBB.func_189972_c())).sub(currentBB.func_189972_c());
         AxisAlignedBB travelAABB = currentBB.func_216361_a(delta.to3d());
         List<PortalEntity> portals = PortalEntity.getOpenPortals(level, travelAABB, (portal) -> {
            Vec3 entityOldPos = new Vec3(currentBB.func_189972_c());
            Vec3 entityPos = entityOldPos.clone().add(delta);
            return portal.isEntityAlignedToPortal(thiss) && !portal.canPointEnter(entityOldPos, false) && portal.canPointEnter(entityPos, false);
         });
         info.setReturnValue(portals.isEmpty());
      }
   }

   @ModifyVariable(
      method = {"collide(Lnet/minecraft/util/math/vector/Vector3d;)Lnet/minecraft/util/math/vector/Vector3d;"},
      at = @At(
   value = "STORE",
   ordinal = 0
)
   )
   private ReuseableStream<VoxelShape> pmAddAdditionalCollisions(ReuseableStream<VoxelShape> reuseablestream, Vector3d vector) {
      Entity thiss = (Entity)this;
      AxisAlignedBB axisalignedbb = thiss.func_174813_aQ();
      Stream<VoxelShape> stream2 = Stream.empty();
      if (axisalignedbb.func_216361_a(vector).func_72320_b() >= 1.0E-7) {
         AxisAlignedBB aabb = axisalignedbb.func_216361_a(vector).func_186662_g(1.0E-7);
         stream2 = thiss.field_70170_p.func_175674_a(thiss, aabb, (entity) -> entity instanceof Cube && !entity.func_184218_aH() && axisalignedbb.field_72338_b > entity.func_174813_aQ().field_72337_e - 0.001).stream().map(Entity::func_174813_aQ).map(VoxelShapes::func_197881_a);
      }

      return new ReuseableStream(Stream.concat(Stream.concat(reuseablestream.func_212761_a(), stream2), Stream.of(PortalEntity.getCollisionShape(thiss))));
   }

   @Redirect(
      method = {"pick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;clip(Lnet/minecraft/util/math/RayTraceContext;)Lnet/minecraft/util/math/BlockRayTraceResult;"
)
   )
   private BlockRayTraceResult pmPickThroughPortal(World level, RayTraceContext context) {
      List<PortalEntity> portalChain = ModUtil.getPortalsAlongRay(level, new Vec3(context.func_222253_b()), new Vec3(context.func_222250_a()), (portalx) -> true);
      Entity thiss = (Entity)this;
      boolean holdingSpecialItem = thiss instanceof LivingEntity && (WrenchItem.hitWithWrench((LivingEntity)thiss) || ((LivingEntity)thiss).func_184614_ca().func_77973_b() instanceof PortalGun);
      PortalRenderer.getInstance().outlineRenderingPortalChain = null;
      if (!portalChain.isEmpty() && !holdingSpecialItem) {
         BlockRayTraceResult normalRay = level.func_217299_a(context);
         Vector3d positionBeforePortal = normalRay.func_216347_e();
         Optional<Vector3d> optionalPositionOnPortal = ((PortalEntity)portalChain.get(0)).func_174813_aQ().func_216365_b(context.func_222253_b(), context.func_222250_a());
         if (optionalPositionOnPortal.isPresent()) {
            double distanceBeforePortal = positionBeforePortal.func_178788_d(context.func_222253_b()).func_72433_c();
            double distanceOnPortal = ((Vector3d)optionalPositionOnPortal.get()).func_178788_d(context.func_222253_b()).func_72433_c();
            if (distanceBeforePortal < distanceOnPortal) {
               return normalRay;
            }
         }

         PortalRenderer.getInstance().outlineRenderingPortalChain = portalChain;
         Mat4 portalMatrix = Mat4.identity();

         for(PortalEntity portal : portalChain) {
            if (!portal.getOtherPortal().isPresent()) {
               break;
            }

            Mat4 matrix = portal.getSourceBasis().getChangeOfBasisMatrix(((PortalEntity)portal.getOtherPortal().get()).getDestinationBasis());
            portalMatrix = Mat4.identity().translate(new Vec3(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch())).mul(matrix).translate((new Vec3(portal.func_213303_ch())).negate()).mul(portalMatrix);
         }

         Vector3d to = (new Vec3(context.func_222250_a())).transform(portalMatrix).to3d();
         Vector3d from = (new Vec3(context.func_222253_b())).transform(portalMatrix).to3d();
         PortalEntity last = (PortalEntity)portalChain.get(portalChain.size() - 1);
         Optional<Vector3d> intersection = ((PortalEntity)last.getOtherPortal().get()).func_174813_aQ().func_216365_b(from, to);
         if (intersection.isPresent()) {
            from = (Vector3d)intersection.get();
         }

         context = new RayTraceContextWrapper(context);
         ((RayTraceContextWrapper)context).setTo(to);
         ((RayTraceContextWrapper)context).setFrom(from);
      }

      return level.func_217299_a(context);
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   public void checkFizzlers(CallbackInfo ci) {
      if (this instanceof Fizzleable) {
         ((Fizzleable)this).checkForFizzlers((Entity)this);
      }

   }

   @Inject(
      method = {"updateInWaterStateAndDoFluidPushing"},
      at = {@At("RETURN")},
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   public void pmAddFlowingGooPhysics(CallbackInfoReturnable<Boolean> cir, double d0, boolean flag) {
      cir.setReturnValue(this.func_70090_H() || flag || this.func_210500_b(FluidTagInit.GOO, 0.05));
   }
}
