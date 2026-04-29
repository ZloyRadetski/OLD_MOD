package net.portalmod.mixins.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalRenderer;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.core.math.AABBUtil;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {
   @Shadow
   private float field_78503_V;
   @Shadow
   private float field_228377_x_;
   @Shadow
   private float field_228376_w_;
   @Shadow
   @Final
   private Minecraft field_78531_r;
   @Shadow
   private float field_78530_s;

   @Shadow
   protected abstract double func_215311_a(ActiveRenderInfo var1, float var2, boolean var3);

   @Inject(
      method = {"getProjectionMatrix"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmGetProjectionMatrix(ActiveRenderInfo camera, float partialTicks, boolean b, CallbackInfoReturnable<Matrix4f> info) {
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.func_227866_c_().func_227870_a_().func_226591_a_();
      if (this.field_78503_V != 1.0F) {
         matrixstack.func_227861_a_((double)this.field_228376_w_, (double)(-this.field_228377_x_), (double)0.0F);
         matrixstack.func_227862_a_(this.field_78503_V, this.field_78503_V, 1.0F);
      }

      matrixstack.func_227866_c_().func_227870_a_().func_226595_a_(Matrix4f.func_195876_a(this.func_215311_a(camera, partialTicks, b), (float)this.field_78531_r.func_147110_a().field_147622_a / (float)this.field_78531_r.func_147110_a().field_147620_b, 0.05F, this.field_78530_s * 4.0F));
      info.setReturnValue(matrixstack.func_227866_c_().func_227870_a_());
   }

   @Redirect(
      method = {"pick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/projectile/ProjectileHelper;getEntityHitResult(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;"
)
   )
   private EntityRayTraceResult pmCustomEntityPick(Entity entity, Vector3d from, Vector3d to, AxisAlignedBB aabb, Predicate<Entity> predicate, double d) {
      EntityRayTraceResult rayResult = ProjectileHelper.func_221273_a(entity, from, to, aabb, (target) -> {
         if (!(target instanceof PortalEntity) || !(entity instanceof LivingEntity) || !WrenchItem.hitWithWrench((LivingEntity)entity) && ((PortalEntity)target).isOpen()) {
            return !target.func_175149_v() && target.func_70067_L();
         } else {
            return true;
         }
      }, d);
      PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (player == null) {
         return rayResult;
      } else {
         List<PortalEntity> portalChain = ModUtil.getPortalsAlongRay(player.field_70170_p, new Vec3(from), new Vec3(to), (portal) -> true);
         Mat4 portalMatrix = ModUtil.getMatrixFromPortalChain(portalChain);
         Pair<Vector3d, Vector3d> ray = ModUtil.teleportRay(portalChain, from, to);
         aabb = AABBUtil.transform(aabb, portalMatrix);
         EntityRayTraceResult teleportedRayResult = ProjectileHelper.func_221273_a(entity, (Vector3d)ray.getFirst(), (Vector3d)ray.getSecond(), aabb, (target) -> {
            if (!(target instanceof PortalEntity) || !(entity instanceof LivingEntity) || !WrenchItem.hitWithWrench((LivingEntity)entity) && ((PortalEntity)target).isOpen()) {
               return !target.func_175149_v() && target.func_70067_L();
            } else {
               return true;
            }
         }, d);
         if (teleportedRayResult == null) {
            return rayResult;
         } else if (rayResult == null) {
            return teleportedRayResult;
         } else {
            Vector3d teleportedFrom = (new Vec3(from)).transform(portalMatrix).to3d();
            double realWorldDistance = rayResult.func_216347_e().func_178788_d(from).func_189985_c();
            double teleportedDistance = teleportedRayResult.func_216347_e().func_178788_d(teleportedFrom).func_189985_c();
            return realWorldDistance <= teleportedDistance ? rayResult : teleportedRayResult;
         }
      }
   }

   @Inject(
      method = {"shouldRenderBlockOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> info) {
      if (Minecraft.func_71410_x().field_71439_g != null) {
         Item mainHandItem = Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b();
         if (mainHandItem instanceof PortalGun) {
            info.setReturnValue(false);
            return;
         }
      }

      if (!PortalRenderer.getInstance().shouldRenderOutline((Deque)null)) {
         info.setReturnValue(false);
      }

   }
}
