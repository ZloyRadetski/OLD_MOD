package net.portalmod.common.sorted.portal;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.portalmod.PMState;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.RenderUtil;
import net.portalmod.mixins.accessors.ActiveRenderInfoAccessor;
import net.portalmod.mixins.accessors.MinecraftAccessor;
import org.lwjgl.opengl.GL11;

public class DuplicateEntityRenderer {
   public static Vec3 entityPosOverride = null;
   public static Matrix4f entityShadowTransformOverride = null;
   public static boolean shouldRenderShadow = true;

   public static void renderDuplicateEntities(ClippingHelper clippinghelper, double d0, double d1, double d2, float partialTicks, MatrixStack matrixStack, ActiveRenderInfo camera) {
      Minecraft mc = Minecraft.func_71410_x();
      WorldRenderer lr = Minecraft.func_71410_x().field_71438_f;
      if (mc.field_71439_g != null) {
         Minecraft.func_71410_x().field_71438_f.field_228415_m_.func_228487_b_().func_228461_a_();

         for(Entity entity : lr.field_72769_h.func_217416_b()) {
            boolean shouldRender = !(entity instanceof ClientPlayerEntity) || camera.func_216773_g() == entity || entity == mc.field_71439_g && !mc.field_71439_g.func_175149_v();
            if (!(Boolean)PortalModConfigManager.RENDER_SELF.get()) {
               shouldRender &= entity != camera.func_216773_g() || camera.func_216770_i() || camera.func_216773_g() instanceof LivingEntity && ((LivingEntity)camera.func_216773_g()).func_70608_bn();
            }

            if (shouldRender) {
               ++lr.field_72749_I;
               if (entity.field_70173_aa == 0) {
                  entity.field_70142_S = entity.func_226277_ct_();
                  entity.field_70137_T = entity.func_226278_cu_();
                  entity.field_70136_U = entity.func_226281_cx_();
               }

               renderDuplicateEntity(entity, d0, d1, d2, partialTicks, matrixStack, clippinghelper);
            }
         }

      }
   }

   public static boolean shouldRenderSelf(Entity entity, ClippingHelper clippingHelper) {
      return shouldRender(entity, Mat4.identity(), clippingHelper);
   }

   private static boolean shouldRenderEntity(Entity entity, ClippingHelper clippingHelper, Vec3 camPos, Mat4 matrix) {
      if (!entity.func_145770_h(camPos.x, camPos.y, camPos.z)) {
         return false;
      } else {
         return entity.field_70158_ak ? true : shouldRender(entity, matrix, clippingHelper);
      }
   }

   private static boolean shouldRender(Entity entity, Mat4 matrix, ClippingHelper clippingHelper) {
      ActiveRenderInfo currentCamera = PortalRenderer.getInstance().getCurrentCamera();
      Vec3 cameraPos = PMState.cameraPosOverrideForRenderingSelf != null ? PMState.cameraPosOverrideForRenderingSelf : new Vec3(currentCamera.func_216785_c());
      Minecraft mc = Minecraft.func_71410_x();
      ActiveRenderInfoAccessor mainCameraAccessor = (ActiveRenderInfoAccessor)mc.field_71460_t.func_215316_n();
      float partialTicks = mc.func_147113_T() ? ((MinecraftAccessor)mc).pmGetPausePartialTick() : mc.func_184121_ak();
      float eyeHeight = MathHelper.func_219799_g(partialTicks, mainCameraAccessor.pmGetEyeHeightOld(), mainCameraAccessor.pmGetEyeHeight());
      Vec3 partialTickedOffset = (new Vec3(entity.func_242282_l(partialTicks))).sub(entity.func_213303_ch());
      AxisAlignedBB aabb = entity.func_174813_aQ().func_191194_a(partialTickedOffset.to3d());
      aabb = transformAABB(aabb, matrix);
      AxisAlignedBB aabbCull = entity.func_184177_bl().func_191194_a(partialTickedOffset.to3d()).func_186662_g((double)0.5F);
      aabbCull = transformAABB(aabbCull, matrix);
      if (entity != mc.field_175622_Z) {
         return clippingHelper.func_228957_a_(aabbCull);
      } else if (aabb.func_72318_a(cameraPos.to3d()) ^ aabb.func_72318_a(cameraPos.clone().sub((double)0.0F, (double)eyeHeight, (double)0.0F).to3d())) {
         return false;
      } else {
         Vec3 duplicateEntityPos = (new Vec3(entity.func_242282_l(partialTicks))).add((double)0.0F, (double)eyeHeight, (double)0.0F).transform(matrix);
         float distance = (float)cameraPos.clone().sub(duplicateEntityPos).magnitudeSqr();
         return (double)distance > 0.001 && clippingHelper.func_228957_a_(aabbCull);
      }
   }

   private static void renderDuplicateEntity(Entity entity, double camX, double camY, double camZ, float partialTicks, MatrixStack matrixStack, ClippingHelper clippingHelper) {
      double d0 = MathHelper.func_219803_d((double)partialTicks, entity.field_70142_S, entity.func_226277_ct_());
      double d1 = MathHelper.func_219803_d((double)partialTicks, entity.field_70137_T, entity.func_226278_cu_());
      double d2 = MathHelper.func_219803_d((double)partialTicks, entity.field_70136_U, entity.func_226281_cx_());
      float f = MathHelper.func_219799_g(partialTicks, entity.field_70126_B, entity.field_70177_z);
      if (!(entity instanceof PortalEntity) && Minecraft.func_71410_x().field_71439_g != null) {
         for(PortalEntity portal : PortalEntity.getOpenPortals(entity.field_70170_p, entity.func_174813_aQ().func_186662_g(0.2), (portalx) -> true)) {
            if (portal.isOpen() && portal.getOtherPortal().isPresent() && portal.isEntityAlignedToPortal(entity)) {
               PortalEntity otherPortal = (PortalEntity)portal.getOtherPortal().get();
               ActiveRenderInfo camera = PortalRenderer.getInstance().getCurrentCamera();
               EntityRendererManager erm = Minecraft.func_71410_x().field_71438_f.field_175010_j;
               Mat4 changeOfBasisMatrix = PortalEntity.getPortalToPortalRotationMatrix(portal, otherPortal);
               Mat4 portalToPortalMatrix = PortalEntity.getPortalToPortalMatrix(portal, otherPortal);
               Vec3 cameraPos = (new Vec3(camera.func_216785_c())).transform(portalToPortalMatrix);
               boolean shouldRender = shouldRenderEntity(entity, clippingHelper, cameraPos, portalToPortalMatrix) || entity.func_184215_y(Minecraft.func_71410_x().field_71439_g);
               if (shouldRender) {
                  matrixStack.func_227860_a_();
                  matrixStack.func_227861_a_(-camX, -camY, -camZ);
                  matrixStack.func_227866_c_().func_227870_a_().func_226595_a_(portalToPortalMatrix.to4f());
                  matrixStack.func_227860_a_();
                  matrixStack.func_227861_a_(d0, d1, d2);
                  matrixStack.func_227866_c_().func_227870_a_().func_226595_a_(changeOfBasisMatrix.transpose().to4f());
                  entityPosOverride = (new Vec3(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).lerp(entity.func_213303_ch(), (double)partialTicks).transform(portalToPortalMatrix);
                  entityShadowTransformOverride = matrixStack.func_227866_c_().func_227870_a_();
                  shouldRenderShadow = portal.func_174811_aO().func_176740_k().func_176722_c() && otherPortal.func_174811_aO().func_176740_k().func_176722_c();
                  matrixStack.func_227865_b_();
                  RenderUtil.setupClipPlane(new MatrixStack(), otherPortal, camera, 0.0F, true);
                  erm.func_229084_a_(entity, d0, d1, d2, f, partialTicks, matrixStack, Minecraft.func_71410_x().field_71438_f.field_228415_m_.func_228487_b_(), erm.func_229085_a_(entity, partialTicks));
                  Minecraft.func_71410_x().field_71438_f.field_228415_m_.func_228487_b_().func_228461_a_();
                  if (PortalRenderer.getInstance().recursion >= 1) {
                     RenderUtil.setStandardClipPlane(PortalRenderer.getInstance().clipMatrix.func_227866_c_().func_227870_a_());
                  } else {
                     GL11.glDisable(12288);
                  }

                  matrixStack.func_227865_b_();
                  entityPosOverride = null;
                  entityShadowTransformOverride = null;
                  shouldRenderShadow = true;
               }
            }
         }

      }
   }

   private static AxisAlignedBB transformAABB(AxisAlignedBB aabb, Mat4 matrix) {
      Vec3 min = (new Vec3(aabb.field_72340_a, aabb.field_72338_b, aabb.field_72339_c)).transform(matrix);
      Vec3 max = (new Vec3(aabb.field_72336_d, aabb.field_72337_e, aabb.field_72334_f)).transform(matrix);
      return new AxisAlignedBB(min.to3d(), max.to3d());
   }
}
