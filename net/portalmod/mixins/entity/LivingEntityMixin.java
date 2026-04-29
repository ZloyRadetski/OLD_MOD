package net.portalmod.mixins.entity;

import java.util.Deque;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.common.entities.Fizzleable;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.common.sorted.gel.AbstractGelBlock;
import net.portalmod.common.sorted.gel.IGelAffected;
import net.portalmod.common.sorted.goo.GooBlock;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.turret.TurretEntity;
import net.portalmod.core.init.FluidInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.injectors.LivingEntityInjector;
import net.portalmod.core.interfaces.IDragCancelable;
import net.portalmod.core.interfaces.ITeleportLerpable;
import net.portalmod.core.util.ModUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity implements Flingable, IDragCancelable, IGelAffected {
   private boolean pmLaunched = false;
   @Shadow
   protected double field_184623_bh;
   @Shadow
   protected double field_184624_bi;
   @Shadow
   protected double field_184625_bj;
   @Shadow
   protected int field_70716_bi;
   @Unique
   private boolean pmCancelDrag = false;
   private float lastNeutralHeight = 0.0F;
   private boolean bounced = false;
   private boolean horizontalBounced = false;
   private boolean wasOnGround = true;
   private boolean leftGround = false;
   private Vector3d lastDeltaMovement;
   private Vector3d lastLastDeltaMovement;
   private int propulsionTicks;

   public LivingEntityMixin(EntityType<?> entityType, World level) {
      super(entityType, level);
      this.lastDeltaMovement = Vector3d.field_186680_a;
      this.lastLastDeltaMovement = Vector3d.field_186680_a;
      this.propulsionTicks = 0;
   }

   @Shadow
   protected abstract SoundEvent func_184588_d(int var1);

   @Shadow
   protected abstract boolean func_241208_cS_();

   @Shadow
   public abstract boolean func_230285_a_(Fluid var1);

   @Shadow
   public abstract Iterable<ItemStack> func_184193_aE();

   @Shadow
   public abstract EntitySize func_213305_a(Pose var1);

   @Shadow
   public abstract IPacket<?> func_213297_N();

   @Shadow
   public abstract void func_184210_p();

   @Shadow
   protected abstract void func_184231_a(double var1, boolean var3, BlockState var4, BlockPos var5);

   @Inject(
      method = {"aiStep"},
      at = {@At("HEAD")}
   )
   private void pmLerpPosWithPortal(CallbackInfo info) {
      Deque<Tuple<Vector3d, Vector3d>> lerpPositions = ((ITeleportLerpable)this).getLerpPositions();
      if (!lerpPositions.isEmpty() && this.field_70170_p.field_72995_K) {
         Tuple<Vector3d, Vector3d> currentLerpPos = (Tuple)lerpPositions.pop();
         this.func_70107_b(((Vector3d)currentLerpPos.func_76340_b()).field_72450_a, ((Vector3d)currentLerpPos.func_76340_b()).field_72448_b, ((Vector3d)currentLerpPos.func_76340_b()).field_72449_c);
         this.field_70169_q = ((Vector3d)currentLerpPos.func_76341_a()).field_72450_a;
         this.field_70167_r = ((Vector3d)currentLerpPos.func_76341_a()).field_72448_b;
         this.field_70166_s = ((Vector3d)currentLerpPos.func_76341_a()).field_72449_c;
         this.field_70142_S = ((Vector3d)currentLerpPos.func_76341_a()).field_72450_a;
         this.field_70137_T = ((Vector3d)currentLerpPos.func_76341_a()).field_72448_b;
         this.field_70136_U = ((Vector3d)currentLerpPos.func_76341_a()).field_72449_c;
         this.field_70716_bi = 0;
      }
   }

   @Redirect(
      method = {"baseTick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;isInWall()Z"
)
   )
   private boolean pmAvoidSuffocationInPortal(LivingEntity entity) {
      return !this.pmHasNearbyPortals(entity) && entity.func_70094_T();
   }

   @Redirect(
      method = {"checkFallDamage"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;checkFallDamage(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V"
)
   )
   private void pmCheckPortalFallDamage(Entity entity, double heightDelta, boolean onGround, BlockState state, BlockPos pos) {
      if (this.pmHasNearbyPortals(entity)) {
         heightDelta = entity.func_213303_ch().field_72448_b - entity.field_70167_r;
      }

      super.func_184231_a(heightDelta, onGround, state, pos);
   }

   private boolean pmHasNearbyPortals(Entity entity) {
      Vector3d delta = (new Vector3d(entity.field_70169_q, entity.field_70167_r, entity.field_70166_s)).func_178788_d(entity.func_213303_ch());
      AxisAlignedBB aabb = entity.func_174813_aQ().func_216361_a(delta).func_186662_g((double)2.0F);
      return !PortalEntity.getPortals(entity.field_70170_p, aabb, (portal) -> true).isEmpty();
   }

   @Redirect(
      method = {"aiStep"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;setPos(DDD)V",
   ordinal = 0
)
   )
   private void pmClientTeleport(LivingEntity instance, double x, double y, double z) {
      instance.func_70107_b(x, y, z);
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   public void checkFizzlers(CallbackInfo ci) {
      if (this instanceof Fizzleable) {
         ((Fizzleable)this).checkForFizzlers(this);
      }

   }

   public void pmSetCancelDrag(boolean cancelDrag) {
      this.pmCancelDrag = cancelDrag;
   }

   public boolean pmIsCancelDrag() {
      return this.pmCancelDrag;
   }

   @Inject(
      method = {"travel"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;setDeltaMovement(DDD)V",
   ordinal = 2,
   shift = Shift.AFTER
)},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void pmCancelApplyDrag(Vector3d delta, CallbackInfo info, double d0, ModifiableAttributeInstance gravity, boolean flag, FluidState fluidstate, BlockPos blockpos, float f3, float f4, Vector3d vector3d5, double d2) {
      if (this.pmCancelDrag) {
         ((LivingEntity)this).func_213293_j(vector3d5.field_72450_a * (double)f4, d2, vector3d5.field_72449_c * (double)f4);
      }

   }

   @Redirect(
      method = {"travel(Lnet/minecraft/util/math/vector/Vector3d;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;setDeltaMovement(DDD)V"
),
      slice = @Slice(
   from = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/util/math/vector/Vector3d;F)Lnet/minecraft/util/math/vector/Vector3d;"
)
)
   )
   private void pmDoLaunchedMovement(LivingEntity entity, double x, double y, double z) {
      Vector3d velocity = entity.func_213322_ci();
      entity.func_213293_j(x, velocity.field_72448_b - 0.08, z);
      if (!this.pmLaunched) {
         entity.func_213293_j(x, y, z);
      } else {
         LivingEntity thiss = (LivingEntity)this;
         Vector3d deltaMovementFromLastTick = entity.func_213303_ch().func_178788_d(new Vector3d(entity.field_70169_q, entity.field_70167_r, entity.field_70166_s));
         double momentumFromLastTick = Math.sqrt(LivingEntity.func_213296_b(deltaMovementFromLastTick));
         if (!entity.field_70123_F && !entity.field_70124_G) {
            entity.func_213293_j(velocity.field_72450_a, velocity.field_72448_b - 0.08, velocity.field_72449_c);
         } else {
            entity.func_213293_j(x, y, z);
         }

         if (entity.field_70123_F && !entity.field_70124_G && !entity.field_70170_p.field_72995_K && thiss.func_184582_a(EquipmentSlotType.FEET).func_77973_b() != ItemInit.LONGFALL_BOOTS.get()) {
            float slamDamage = (float)(momentumFromLastTick * (double)3.0F - (double)10.0F);
            if (slamDamage > 0.1F) {
               entity.func_184185_a(this.func_184588_d((int)slamDamage), 1.0F, ModUtil.randomSlightSoundPitch());
               entity.func_70097_a(new DamageSource("faithplate_wall"), slamDamage);
            }
         }

      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"tick()V"}
   )
   private void pmOnPostTick(CallbackInfo info) {
      LivingEntityInjector.onPostTick((LivingEntity)this);
   }

   public void setFlinging(boolean launched) {
      this.pmLaunched = launched;
   }

   public boolean isFlinging() {
      return this.pmLaunched;
   }

   public int getPropulsionTicks() {
      return this.propulsionTicks;
   }

   public void setPropulsionTicks(int ticks) {
      this.propulsionTicks = ticks;
   }

   public void incrementPropulsionTicks() {
      this.propulsionTicks = Math.min(30, this.propulsionTicks + 1);
   }

   public void decrementPropulsionTicks() {
      this.propulsionTicks = Math.max(0, this.propulsionTicks - 2);
   }

   public void setLastNeurtalHeight(float distance) {
      this.lastNeutralHeight = distance;
   }

   public float getLastNeutralHeight() {
      return this.lastNeutralHeight;
   }

   public void setBounced(boolean newBounced) {
      this.bounced = newBounced;
   }

   public boolean getBounced() {
      return this.bounced;
   }

   public void setHorizontalBounced(boolean newHorizontalBounced) {
      this.horizontalBounced = newHorizontalBounced;
   }

   public boolean getHorizontalBounced() {
      return this.horizontalBounced;
   }

   public void setWasOnGround(boolean newWasOnGround) {
      this.wasOnGround = newWasOnGround;
   }

   public boolean getWasOnGround() {
      return this.wasOnGround;
   }

   public void setLastDeltaMovement(Vector3d newLastDeltaMovement) {
      this.lastDeltaMovement = newLastDeltaMovement;
   }

   public Vector3d getLastDeltaMovement() {
      return this.lastDeltaMovement;
   }

   public void setLastLastDeltaMovement(Vector3d newLastLastDeltaMovement) {
      this.lastLastDeltaMovement = newLastLastDeltaMovement;
   }

   public Vector3d getLastLastDeltaMovement() {
      return this.lastLastDeltaMovement;
   }

   public boolean getLeftGround() {
      return this.leftGround;
   }

   public void setLeftGround(boolean leftGround) {
      this.leftGround = leftGround;
   }

   protected void func_233569_aL_() {
      World level = this.field_70170_p;
      BlockPos pos = new BlockPos(this.func_213303_ch());
      BlockState state = level.func_180495_p(pos);
      if (!(state.func_177230_c() instanceof AbstractGelBlock)) {
         super.func_233569_aL_();
      } else {
         int i = MathHelper.func_76128_c(this.func_226277_ct_());
         int j = MathHelper.func_76128_c(this.func_226278_cu_() + (double)0.8F);
         int k = MathHelper.func_76128_c(this.func_226281_cx_());
         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = this.field_70170_p.func_180495_p(blockpos);
         if (!blockstate.addRunningEffects(level, blockpos, this) && blockstate.func_185901_i() != BlockRenderType.INVISIBLE) {
            Vector3d vector3d = this.func_213322_ci();
            this.field_70170_p.func_195594_a((new BlockParticleData(ParticleTypes.field_197611_d, blockstate)).setPos(blockpos), this.func_226277_ct_() + (this.field_70146_Z.nextDouble() - (double)0.5F) * (double)this.func_213305_a(this.func_213283_Z()).field_220315_a, this.func_226278_cu_() + 0.1, this.func_226281_cx_() + (this.field_70146_Z.nextDouble() - (double)0.5F) * (double)this.func_213305_a(this.func_213283_Z()).field_220315_a, vector3d.field_72450_a * (double)-4.0F, (double)1.5F, vector3d.field_72449_c * (double)-4.0F);
         }
      }

   }

   protected void func_180429_a(BlockPos pos, BlockState state) {
      World level = this.field_70170_p;
      BlockPos nPos = new BlockPos(this.func_213303_ch());
      BlockState nState = level.func_180495_p(nPos);
      if (!(nState.func_177230_c() instanceof AbstractGelBlock)) {
         super.func_180429_a(pos, state);
      } else {
         SoundType soundtype = nState.getSoundType(level, nPos, this);
         this.func_184185_a(soundtype.func_185844_d(), soundtype.func_185843_a() * 0.15F, ModUtil.randomSoundPitch());
      }

   }

   @Inject(
      method = {"travel"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"
)}
   )
   public void pmAddGooResistance(Vector3d p_213352_1_, CallbackInfo ci) {
      FluidState fluidState = this.field_70170_p.func_204610_c(this.func_233580_cy_());
      if (GooBlock.isInGoo(this) && this.func_241208_cS_() && !this.func_230285_a_(fluidState.func_206886_c())) {
         GooBlock.applyGooResistance(this);
      }

   }

   @Inject(
      method = {"baseTick"},
      at = {@At("HEAD")}
   )
   public void pmAddGooDamage(CallbackInfo ci) {
      if (GooBlock.isInGoo(this)) {
         GooBlock.addGooDamage(this);
      }

   }

   @Redirect(
      method = {"hurt"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;broadcastEntityEvent(Lnet/minecraft/entity/Entity;B)V",
   ordinal = 2
)
   )
   public void pmHandleGooDamage(World world, Entity entity, byte b, DamageSource damageSource) {
      if (damageSource == FluidInit.GOO_DAMAGE && b == 2 && entity instanceof LivingEntity) {
         GooBlock.handleGooDamage((LivingEntity)entity, damageSource);
      } else {
         this.field_70170_p.func_72960_a(this, b);
      }

   }

   @Inject(
      method = {"aiStep"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;getFluidJumpThreshold()D"
)}
   )
   public void pmJumpInGoo(CallbackInfo ci) {
      if (GooBlock.isInGoo(this) && WrenchItem.holdingWrench(this)) {
         GooBlock.applyVerticalSwimSpeed((LivingEntity)this);
      }

   }

   @Redirect(
      method = {"baseTick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;stopRiding()V"
)
   )
   public void avoidTestElementEntityDismount(LivingEntity instance) {
      if (!(instance instanceof TestElementEntity)) {
         this.func_184210_p();
      }

   }

   @Inject(
      method = {"canSee"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pmCanSee(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof TurretEntity && !((TurretEntity)entity).getState().isStanding()) {
         cir.setReturnValue(false);
      }

   }
}
