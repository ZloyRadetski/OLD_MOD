package net.portalmod.core.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.portalmod.PMState;
import net.portalmod.client.render.PortalCamera;
import net.portalmod.client.render.Shader;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.core.math.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
   public static void bindTexture(Shader shader, String uniform, String path, int index) {
      RenderSystem.activeTexture('蓀' + index);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(new ResourceLocation("portalmod", path));
      shader.setInt(uniform, index);
   }

   public static void setClipPlane(int index, Matrix4f matrix, Vec3 normal) {
      GL11.glEnable(12288 + index);
      RenderSystem.matrixMode(5888);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(matrix);
      GL11.glClipPlane(12288 + index, new double[]{normal.x, normal.y, normal.z, (double)0.0F});
      RenderSystem.popMatrix();
   }

   public static void setStandardClipPlane(Matrix4f matrix) {
      setClipPlane(0, matrix, new Vec3((double)0.0F, (double)0.0F, (double)-1.0F));
   }

   public static void setupClipPlane(MatrixStack clipMatrix, PortalEntity portal, ActiveRenderInfo camera, float offset, boolean reversed) {
      Vector3i portalNormal = portal.func_174811_aO().func_176730_m();
      Vec3 offsetNormal = (new Vec3(camera.func_216785_c())).sub(portal.func_213303_ch()).normalize().mul((double)offset);
      Vector3d cameraPos = camera.func_216785_c();
      float roll = camera instanceof PortalCamera ? ((PortalCamera)camera).getRoll() : PMState.cameraRoll;
      clipMatrix.func_227866_c_().func_227870_a_().func_226591_a_();
      clipMatrix.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(roll));
      clipMatrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(camera.func_216777_e()));
      clipMatrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(camera.func_216778_f() + 180.0F));
      clipMatrix.func_227861_a_(offsetNormal.x, offsetNormal.y, offsetNormal.z);
      clipMatrix.func_227861_a_((double)((float)portalNormal.func_177958_n() * 1.0E-4F), (double)((float)portalNormal.func_177956_o() * 1.0E-4F), (double)((float)portalNormal.func_177952_p() * 1.0E-4F));
      clipMatrix.func_227861_a_(-cameraPos.field_72450_a, -cameraPos.field_72448_b, -cameraPos.field_72449_c);
      PortalEntity.setupMatrix(clipMatrix, portal.func_174811_aO(), portal.getUpVector(), portal.getPivotPoint());
      setClipPlane(0, clipMatrix.func_227866_c_().func_227870_a_(), new Vec3((double)0.0F, (double)0.0F, reversed ? (double)1.0F : (double)-1.0F));
   }
}
