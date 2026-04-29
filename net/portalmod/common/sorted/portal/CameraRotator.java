package net.portalmod.common.sorted.portal;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class CameraRotator {
   public static void rotate(Entity entity, PortalEntity portal, PortalEntity targetPortal) {
      boolean isLocalPlayer = entity instanceof ClientPlayerEntity;
      float xRotOld = entity.field_70125_A;
      float yRotOld = entity.field_70177_z;
      float pitch = entity.field_70125_A;
      float yaw = entity.field_70177_z;
      OrthonormalBasis portalBasis = portal.getSourceBasis();
      OrthonormalBasis targetPortalBasis = targetPortal.getDestinationBasis();
      Mat4 changeOfBasisMatrix = portalBasis.getChangeOfBasisMatrix(targetPortalBasis);
      OrthonormalBasis cameraBasis = EulerConverter.toVectors(pitch, yaw, 0.0F).transform(changeOfBasisMatrix);
      EulerConverter.EulerAngles angles = EulerConverter.toEulerAngles(cameraBasis);
      EulerConverter.EulerAngles anglesUnbound = EulerConverter.toEulerAnglesLeastRoll(cameraBasis);
      float absPitch = Math.abs(anglesUnbound.getPitch());
      if (absPitch >= 90.0F && absPitch <= 135.0F) {
         pitch = MathHelper.func_76131_a(anglesUnbound.getPitch(), -90.0F, 90.0F);
         yaw = anglesUnbound.getYaw();
         if (targetPortal.func_174811_aO().func_176740_k().func_176722_c()) {
            Vec3 yawVec = (new Vec3(targetPortal.func_174811_aO())).mul((double)(-Math.signum(anglesUnbound.getPitch())));
            float yawSin = (float)Vec3.zAxis().cross(yawVec).y;
            float yawCos = (float)Vec3.zAxis().dot(yawVec);
            yaw = -((float)(Math.atan2((double)yawSin, (double)yawCos) * (double)180.0F / Math.PI));
            if (isLocalPlayer) {
               CameraAnimator.getInstance().startYawAnimation(anglesUnbound.getYaw(), yaw, 700, true);
            }
         }

         if (isLocalPlayer) {
            CameraAnimator.getInstance().startPitchAnimation(anglesUnbound.getPitch(), pitch, 500, true);
            CameraAnimator.getInstance().startRollAnimation(anglesUnbound.getRoll(), 0.0F, 700, true);
         }
      } else {
         pitch = angles.getPitch();
         yaw = angles.getYaw();
         float startRoll = angles.getRoll();
         float endRoll = 0.0F;
         if (portal.func_174811_aO() == Direction.UP && targetPortal.func_174811_aO() == Direction.UP) {
            endRoll = portal.getEnd() == PortalEnd.PRIMARY ? 360.0F : 0.0F;
            startRoll = CameraAnimator.normalizeAngle(startRoll);
         }

         if (isLocalPlayer) {
            CameraAnimator.getInstance().startRollAnimation(startRoll, endRoll, 500, false);
         }
      }

      entity.field_70125_A = entity.field_70127_C = pitch;
      entity.field_70177_z = entity.field_70126_B = yaw;
      if (isLocalPlayer) {
         ClientPlayerEntity player = (ClientPlayerEntity)entity;
         float xRotDelta = player.field_70125_A - xRotOld;
         float yRotDelta = player.field_70177_z - yRotOld;
         player.field_71155_g += xRotDelta;
         player.field_71164_i += xRotDelta;
         player.field_71154_f += yRotDelta;
         player.field_71163_h += yRotDelta;
         player.field_70761_aq += yRotDelta;
         player.field_70760_ar += yRotDelta;
         player.field_70759_as += yRotDelta;
         player.field_70758_at += yRotDelta;
      }

   }
}
