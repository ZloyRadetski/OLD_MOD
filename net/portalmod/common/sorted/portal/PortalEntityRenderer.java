package net.portalmod.common.sorted.portal;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.portalmod.core.math.Vec3;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {
   public static final float OFFSET = 1.0E-4F;

   public PortalEntityRenderer(EntityRendererManager p_i46166_1_) {
      super(p_i46166_1_);
   }

   public void render(PortalEntity portal, float a, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int b) {
      super.func_225623_a_(portal, a, partialTicks, matrixStack, renderBuffer, b);
   }

   public ResourceLocation getTextureLocation(PortalEntity p_110775_1_) {
      return AtlasTexture.field_110575_b;
   }

   protected boolean shouldShowName(PortalEntity portal) {
      return portal.func_145818_k_();
   }

   protected void renderNameTag(PortalEntity portal, ITextComponent text, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int light) {
      matrixStack.func_227860_a_();
      Vector3d down = (new Vec3(portal.getUpVector().func_176730_m())).mul((double)-1.0F).to3d();
      matrixStack.func_227861_a_(down.field_72450_a, down.field_72448_b, down.field_72449_c);
      Vector3d forwards = (new Vec3(portal.func_174811_aO().func_176730_m())).mul(0.2).to3d();
      matrixStack.func_227861_a_(forwards.field_72450_a, forwards.field_72448_b, forwards.field_72449_c);
      super.func_225629_a_(portal, text, matrixStack, renderBuffer, light);
      matrixStack.func_227865_b_();
   }
}
