package net.portalmod.common.sorted.portal;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.portalmod.PMState;
import net.portalmod.client.render.PortalCamera;
import net.portalmod.core.interfaces.ITeleportLerpable;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;
import net.portalmod.mixins.accessors.ActiveRenderInfoAccessor;

public class PortalEntityClient {
   public static PortalCamera teleportCamera(EntityViewRenderEvent.CameraSetup event, boolean useBobbing) {
      ActiveRenderInfo camera = event.getInfo();
      float partialTicks = (float)event.getRenderPartialTicks();
      Minecraft minecraft = Minecraft.func_71410_x();
      PlayerEntity player = minecraft.field_71439_g;
      World level = minecraft.field_71441_e;
      if (player != null && level != null) {
         PortalCamera newCamera = new PortalCamera(camera, (float)event.getRenderPartialTicks());
         newCamera.setPitch(event.getPitch());
         newCamera.setYaw(event.getYaw());
         newCamera.setRoll(event.getRoll());
         AxisAlignedBB playerAABB = player.func_174813_aQ().func_191194_a((new Vec3(player.func_213303_ch())).negate().to3d()).func_72317_d(player.field_70169_q, player.field_70167_r, player.field_70166_s).func_216361_a((new Vec3(player.func_213303_ch())).sub(player.field_70169_q, player.field_70167_r, player.field_70166_s).to3d());
         if (PMState.cameraPosOverrideForRenderingSelf != null) {
            playerAABB = playerAABB.func_186662_g((double)1.0F);
         }

         List<PortalEntity> entities = PortalEntity.getOpenPortals(level, playerAABB.func_186664_h(0.001), (portalx) -> portalx.arePointsAlignedToPortal(new Vec3(newCamera.func_216785_c())));
         Optional<PortalEntity> optionalPortal = entities.stream().reduce((o, n) -> (new Vec3(player.func_174813_aQ().func_189972_c())).sub(n.func_213303_ch()).magnitudeSqr() < (new Vec3(player.func_174813_aQ().func_189972_c())).sub(o.func_213303_ch()).magnitudeSqr() ? n : o);
         if (!optionalPortal.isPresent()) {
            return newCamera;
         } else {
            PortalEntity portal = (PortalEntity)optionalPortal.get();
            if (portal.isOpen() && portal.getOtherPortal().isPresent()) {
               float affinity = (float)(new Vec3(player.func_213322_ci())).normalize().dot(portal.func_174811_aO().func_176730_m());
               if (PMState.cameraPosOverrideForRenderingSelf != null && (double)affinity < (double)0.5F) {
                  return newCamera;
               } else {
                  PortalEntity targetPortal = (PortalEntity)portal.getOtherPortal().get();
                  Vector3d portalPos = portal.func_213303_ch();
                  Vector3d targetPortalPos = targetPortal.func_213303_ch();
                  Vector3d cameraPos = camera.func_216785_c();
                  MatrixStack bob = new MatrixStack();
                  minecraft.field_71460_t.func_228380_a_(bob, partialTicks);
                  if (minecraft.field_71474_y.field_74336_f) {
                     minecraft.field_71460_t.func_228383_b_(bob, partialTicks);
                  }

                  Mat4 bobMat = new Mat4(bob.func_227866_c_().func_227870_a_());
                  MatrixStack view = new MatrixStack();
                  view.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(newCamera.getRoll()));
                  view.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(newCamera.func_216777_e()));
                  view.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(newCamera.func_216778_f() + 180.0F));
                  view.func_227861_a_(-cameraPos.field_72450_a, -cameraPos.field_72448_b, -cameraPos.field_72449_c);
                  Mat4 viewMat = new Mat4(view.func_227866_c_().func_227870_a_());
                  Vec3 portalPosBobbed = (new Vec3(portalPos)).transform(viewMat);
                  if (useBobbing) {
                     portalPosBobbed.transform(bobMat);
                  }

                  Vec3 normalBobbed = (new Vec3(portal.func_174811_aO().func_176734_d())).add(portalPos).transform(viewMat);
                  if (useBobbing) {
                     normalBobbed.transform(bobMat);
                  }

                  normalBobbed.sub(portalPosBobbed);
                  OrthonormalBasis portalBasis = portal.getSourceBasis();
                  OrthonormalBasis targetPortalBasis = targetPortal.getDestinationBasis();
                  Mat4 changeOfBasisMatrix = portalBasis.getChangeOfBasisMatrix(targetPortalBasis);
                  if (portalPosBobbed.dot(normalBobbed) < (double)0.0F) {
                     Vec3 newCameraPos = (new Vec3(cameraPos)).sub(portalPos.func_178787_e((new Vec3(portal.getNormal())).mul((double)5.0E-4F).to3d())).transform(changeOfBasisMatrix).add(targetPortalPos.func_178787_e((new Vec3(targetPortal.getNormal())).mul((double)5.0E-4F).to3d()));
                     ((ActiveRenderInfoAccessor)newCamera).pmSetPosition(newCameraPos.to3d());
                     if (useBobbing && PMState.cameraPosOverrideForRenderingSelf != null) {
                        PMState.cameraPosOverrideForRenderingSelf = PMState.cameraPosOverrideForRenderingSelf.clone().sub(portalPos.func_178787_e((new Vec3(portal.getNormal())).mul((double)5.0E-4F).to3d())).transform(changeOfBasisMatrix).add(targetPortalPos.func_178787_e((new Vec3(targetPortal.getNormal())).mul((double)5.0E-4F).to3d()));
                     }

                     float xRot = newCamera.func_216777_e();
                     float yRot = newCamera.func_216778_f();
                     float roll = newCamera.getRoll();
                     OrthonormalBasis cameraBasis = EulerConverter.toVectors(xRot, yRot, roll).transform(changeOfBasisMatrix);
                     EulerConverter.EulerAngles angles = EulerConverter.toEulerAngles(cameraBasis);
                     ((ActiveRenderInfoAccessor)newCamera).pmSetRotation(angles.getYaw(), angles.getPitch());
                     newCamera.setRoll(angles.getRoll());
                  }

                  return newCamera;
               }
            } else {
               return newCamera;
            }
         }
      } else {
         return null;
      }
   }

   private static PortalCamera teleportThirdPersonCamera(EntityViewRenderEvent.CameraSetup event) {
      ActiveRenderInfo camera = event.getInfo();
      Minecraft minecraft = Minecraft.func_71410_x();
      PlayerEntity player = minecraft.field_71439_g;
      World level = minecraft.field_71441_e;
      if (player != null && level != null) {
         PortalCamera newCamera = new PortalCamera(camera, (float)event.getRenderPartialTicks());
         newCamera.setPitch(event.getPitch());
         newCamera.setYaw(event.getYaw());
         newCamera.setRoll(event.getRoll());
         Vec3 from = new Vec3(event.getInfo().func_216773_g().func_174824_e((float)event.getRenderPartialTicks()));
         Vec3 to = new Vec3(newCamera.func_216785_c());
         List<PortalEntity> portalChain = ModUtil.getPortalsAlongRay(Minecraft.func_71410_x().field_71441_e, from, to, (portal) -> true);
         Mat4 matrix = ModUtil.getMatrixFromPortalChain(portalChain);
         Mat4 rotationMatrix = ModUtil.getRotationMatrixFromPortalChain(portalChain);
         Vec3 eyePos = new Vec3(camera.func_216773_g().func_174824_e((float)event.getRenderPartialTicks()));
         Vec3 newEyePos = eyePos.clone().transform(matrix);
         Vec3 newCameraPos = (new Vec3(camera.func_216785_c())).sub(eyePos).transform(rotationMatrix).add(newEyePos);
         ((ActiveRenderInfoAccessor)newCamera).pmSetPosition(newCameraPos.to3d());
         float xRot = newCamera.func_216777_e();
         float yRot = newCamera.func_216778_f();
         float roll = newCamera.getRoll();
         OrthonormalBasis cameraBasis = EulerConverter.toVectors(xRot, yRot, roll).transform(rotationMatrix);
         EulerConverter.EulerAngles angles = EulerConverter.toEulerAngles(cameraBasis);
         ((ActiveRenderInfoAccessor)newCamera).pmSetRotation(angles.getYaw(), angles.getPitch());
         newCamera.setRoll(angles.getRoll());
         return newCamera;
      } else {
         return null;
      }
   }

   public static void teleportCameraAndApply(EntityViewRenderEvent.CameraSetup event) {
      ActiveRenderInfo camera = event.getInfo();
      boolean thirdPerson = camera.func_216770_i();
      PortalCamera newCamera = thirdPerson ? teleportThirdPersonCamera(event) : teleportCamera(event, true);
      if (!thirdPerson) {
         PortalCamera cameraForLight = teleportCamera(event, false);
         if (cameraForLight != null) {
            BlockPos lightPos = new BlockPos((new Vec3(cameraForLight.func_216785_c())).to3i());
            ((ActiveRenderInfoAccessor)camera).pmGetBlockPosition().func_189533_g(lightPos);
         }
      } else if (newCamera != null) {
         BlockPos lightPos = new BlockPos((new Vec3(newCamera.func_216785_c())).to3i());
         ((ActiveRenderInfoAccessor)camera).pmGetBlockPosition().func_189533_g(lightPos);
      }

      if (newCamera != null) {
         ((ActiveRenderInfoAccessor)camera).pmSetPosition((new Vec3(newCamera.func_216785_c())).to3d());
         ((ActiveRenderInfoAccessor)camera).pmSetRotation(newCamera.func_216778_f(), newCamera.func_216777_e());
         event.setPitch(newCamera.func_216777_e());
         event.setYaw(newCamera.func_216778_f());
         event.setRoll(newCamera.getRoll());
         if (((ITeleportLerpable)Minecraft.func_71410_x().field_71439_g).hasUsedPortal()) {
            Minecraft.func_71410_x().func_213239_aq().func_76320_a("pm_translucent_sort");
            PortalTransparencyHandler.resortMainTransparency(new Vec3(newCamera.func_216785_c()));
            Minecraft.func_71410_x().func_213239_aq().func_76319_b();
         }
      }

   }

   public static boolean isLocalPlayerMoving() {
      GameSettings options = Minecraft.func_71410_x().field_71474_y;
      return options.field_74370_x.func_151470_d() || options.field_74366_z.func_151470_d() || options.field_74351_w.func_151470_d() || options.field_74368_y.func_151470_d();
   }
}
