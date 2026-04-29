package net.portalmod.mixins.renderer;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ActiveRenderInfo.class})
public abstract class ActiveRenderInfoMixin {
   @Inject(
      method = {"getMaxZoom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmTeleportCameraRay(double zoom, CallbackInfoReturnable<Double> info) {
      ActiveRenderInfo thiss = (ActiveRenderInfo)this;
      Entity entity = thiss.func_216773_g();
      Vec3 from = new Vec3(thiss.func_216785_c());
      Vec3 to = from.clone().sub((new Vec3(thiss.func_227996_l_())).mul(zoom));
      List<PortalEntity> portalChain = ModUtil.getPortalsAlongRay(entity.field_70170_p, from, to, (portal) -> true);
      if (portalChain.isEmpty()) {
         List<PortalEntity> portals = PortalEntity.getOpenPortals(entity.field_70170_p, entity.func_174813_aQ(), (portal) -> true);
         if (!portals.isEmpty()) {
            Vec3 from2 = from.clone().sub((new Vec3(thiss.func_227996_l_())).mul(0.2));
            RayTraceContext context = new RayTraceContext(from2.to3d(), to.to3d(), BlockMode.VISUAL, FluidMode.NONE, thiss.func_216773_g());
            RayTraceResult result = entity.field_70170_p.func_217299_a(context);
            double maxZoom = Math.min(zoom, result.func_216347_e().func_72438_d(from.to3d()) - 0.1);
            info.setReturnValue(maxZoom);
         }
      } else {
         Mat4 portalMatrix = ModUtil.getMatrixFromPortalChain(portalChain);
         Vec3 teleportedEyePos = from.clone().transform(portalMatrix);
         Pair<Vector3d, Vector3d> ray = ModUtil.teleportRay(portalChain, from.to3d(), to.to3d());
         RayTraceContext context = new RayTraceContext((Vector3d)ray.getFirst(), (Vector3d)ray.getSecond(), BlockMode.VISUAL, FluidMode.NONE, thiss.func_216773_g());
         RayTraceResult result = entity.field_70170_p.func_217299_a(context);
         double maxZoom = Math.min(zoom, result.func_216347_e().func_72438_d(teleportedEyePos.to3d()) - 0.1);
         info.setReturnValue(maxZoom);
      }
   }
}
