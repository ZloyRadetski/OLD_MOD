package net.portalmod.mixins.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.portalmod.common.sorted.portal.ITeleportable;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalPair;
import net.portalmod.common.sorted.portal.PortalPairCache;
import net.portalmod.core.math.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractMinecartEntity.class})
public class AbstractMinecartMixin {
   @Shadow
   private double field_70511_i;
   @Shadow
   private double field_70509_j;
   @Shadow
   private double field_70514_an;

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;floor(D)I",
   ordinal = 0
)}
   )
   private void pmTeleportMinecart(CallbackInfo info) {
      PortalEntity.teleportEntity((Entity)this, ((Entity)this).func_213322_ci());
   }

   @Redirect(
      method = {"tick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;setPos(DDD)V",
   ordinal = 0
)
   )
   private void pmClientTeleport(AbstractMinecartEntity instance, double x, double y, double z) {
      if (!((ITeleportable)instance).hasLastUsedPortal()) {
         instance.func_70107_b(x, y, z);
      } else {
         PortalEntity portal = (PortalEntity)instance.field_70170_p.func_73045_a(((ITeleportable)instance).getLastUsedPortal());
         PortalPair pair = (PortalPair)PortalPairCache.CLIENT.get(portal.getGunUUID());
         if (pair == null) {
            instance.func_70107_b(x, y, z);
         } else {
            PortalEntity targetPortal = pair.get(portal.getEnd().other());
            if (targetPortal == null) {
               instance.func_70107_b(x, y, z);
            } else {
               Vector3f normal = (new Vec3(portal.func_174811_aO().func_176730_m())).to3f();
               Vector3f normal2 = (new Vec3(portal.func_174811_aO().func_176730_m())).to3f();
               normal.func_195898_a(0.5F);
               normal2.func_195898_a(9.999999E-4F);
               Vector3d portalPos = Vector3d.func_237489_a_(portal.func_233580_cy_()).func_178788_d(new Vector3d(normal)).func_178787_e(new Vector3d(normal2));
               Vector3f targetnormal = (new Vec3(targetPortal.func_174811_aO().func_176730_m())).to3f();
               Vector3f targetnormal2 = (new Vec3(targetPortal.func_174811_aO().func_176730_m())).to3f();
               targetnormal.func_195898_a(0.5F);
               targetnormal2.func_195898_a(9.999999E-4F);
               Vector3d targetPortalPos = Vector3d.func_237489_a_(targetPortal.func_233580_cy_()).func_178788_d(new Vector3d(targetnormal)).func_178787_e(new Vector3d(targetnormal2));
               Vector3d offset = targetPortalPos.func_178788_d(portalPos);
               Vector3d portalToEntity = (new Vector3d(x, y, z)).func_178788_d(portalPos);
               if (portalToEntity.func_72430_b((new Vec3(portal.func_174811_aO().func_176730_m())).to3d()) >= (double)0.0F) {
                  instance.func_70107_b(x, y, z);
               } else {
                  AxisAlignedBB bb = instance.func_174813_aQ();
                  Vector3d delta = (new Vector3d(x, y, z)).func_178786_a(instance.field_70169_q, instance.field_70167_r, instance.field_70166_s);
                  AxisAlignedBB travelAABB = bb.func_216361_a(delta);
                  Vec3 projected = portal.projectPointOnPortalSurface(new Vec3(bb.func_189972_c().func_178787_e(delta)));
                  boolean isInside = projected.x > (double)-0.5F && projected.x < (double)0.5F && projected.y > (double)-1.0F && projected.y < (double)1.0F;
                  if (portal.func_174813_aQ().func_72326_a(travelAABB) && isInside) {
                     instance.func_70107_b(x + offset.field_72450_a, y + offset.field_72448_b, z + offset.field_72449_c);
                     instance.field_70169_q += offset.field_72450_a;
                     instance.field_70167_r += offset.field_72448_b;
                     instance.field_70166_s += offset.field_72449_c;
                     instance.field_70142_S += offset.field_72450_a;
                     instance.field_70137_T += offset.field_72448_b;
                     instance.field_70136_U += offset.field_72449_c;
                     this.field_70511_i += offset.field_72450_a;
                     this.field_70509_j += offset.field_72448_b;
                     this.field_70514_an += offset.field_72449_c;
                     ((ITeleportable)instance).removeLastUsedPortal();
                  } else {
                     instance.func_70107_b(x, y, z);
                  }
               }
            }
         }
      }
   }
}
