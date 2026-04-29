package net.portalmod.common.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;

public abstract class TestElementEntityRenderer<T extends TestElementEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
   public TestElementEntityRenderer(EntityRendererManager manager, M model, float v) {
      super(manager, model, v);
   }

   public void render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer p_225623_5_, int light) {
      float f5 = (float)entity.getHurtTime() - p_225623_3_;
      float f6 = Math.max(0.0F, entity.getDamage() - p_225623_3_);
      light = LightTexture.func_228451_a_(entity.field_70170_p.func_226658_a_(LightType.BLOCK, entity.func_233580_cy_()), entity.field_70170_p.func_226658_a_(LightType.SKY, entity.func_233580_cy_()));
      matrixStack.func_227860_a_();
      if (f5 > 0.0F) {
         matrixStack.func_227863_a_((new Vector3f(entity.func_70040_Z().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72432_b())).func_229187_a_(MathHelper.func_76126_a(f5) * f5 * f6 / 10.0F * (float)entity.getHurtDir()));
      }

      float wiggle = (float)entity.getWiggle() - p_225623_3_;
      if (wiggle > 0.0F) {
         matrixStack.func_227863_a_((new Vector3f(entity.func_70040_Z().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72432_b())).func_229187_a_(MathHelper.func_76126_a(wiggle * 1.5F) * wiggle * 0.5F * (float)entity.getHurtDir()));
      }

      super.func_225623_a_(entity, p_225623_2_, p_225623_3_, matrixStack, p_225623_5_, entity.getFizzleLight(light));
      matrixStack.func_227865_b_();
   }

   protected boolean shouldShowName(T entity) {
      return Minecraft.func_71382_s() && entity.func_145818_k_() && this.field_76990_c.field_147941_i == entity;
   }
}
