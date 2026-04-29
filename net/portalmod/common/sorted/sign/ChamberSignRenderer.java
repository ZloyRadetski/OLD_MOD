package net.portalmod.common.sorted.sign;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ChamberSignRenderer extends EntityRenderer<ChamberSignEntity> {
   public static final ResourceLocation TEXTURE_ON = new ResourceLocation("portalmod", "textures/entity/chamber_sign/chamber_sign_on.png");
   public static final ResourceLocation TEXTURE_OFF = new ResourceLocation("portalmod", "textures/entity/chamber_sign/chamber_sign_off.png");
   public final ChamberSignModel model = new ChamberSignModel();

   public ChamberSignRenderer(EntityRendererManager manager) {
      super(manager);
   }

   public void render(ChamberSignEntity entity, float rotation, float b, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int i) {
      boolean enabled = entity.getEnabled();
      matrixStack.func_227860_a_();
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(rotation + 180.0F));
      matrixStack.func_227861_a_((double)0.0F, -1.505, -0.005);
      int light = enabled ? LightTexture.func_228451_a_(15, 15) : WorldRenderer.func_228421_a_(entity.field_70170_p, entity.func_174857_n().func_177984_a());
      this.model.changeModel(entity);
      this.model.func_225598_a_(matrixStack, renderTypeBuffer.getBuffer(this.model.func_228282_a_(enabled ? TEXTURE_ON : TEXTURE_OFF)), light, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.func_227865_b_();
      super.func_225623_a_(entity, rotation, b, matrixStack, renderTypeBuffer, i);
   }

   public ResourceLocation getTextureLocation(ChamberSignEntity entity) {
      return TEXTURE_ON;
   }
}
