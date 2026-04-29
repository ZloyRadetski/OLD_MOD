package net.portalmod.common.sorted.longfallboots;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class LongFallBootsModel extends BipedModel<LivingEntity> {
   private final ModelRenderer left_shoe;
   private final ModelRenderer right_shoe;
   private final LivingEntity entity;

   public LongFallBootsModel(LivingEntity entity) {
      super(0.0F);
      this.field_78090_t = 32;
      this.field_78089_u = 16;
      this.left_shoe = new ModelRenderer(this);
      this.left_shoe.func_78793_a(2.0F, 12.0F, 0.0F);
      this.left_shoe.func_78784_a(0, 0).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.75F, false);
      this.left_shoe.func_78784_a(16, 7).func_228303_a_(-2.0F, 4.0F, 3.5F, 4.0F, 7.0F, 2.0F, 0.75F, false);
      this.right_shoe = new ModelRenderer(this);
      this.right_shoe.func_78793_a(-2.0F, 12.0F, 0.0F);
      this.right_shoe.func_78784_a(0, 0).func_228303_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.75F, true);
      this.right_shoe.func_78784_a(16, 7).func_228303_a_(-2.0F, 4.0F, 3.5F, 4.0F, 7.0F, 2.0F, 0.75F, true);
      this.entity = entity;
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.func_225597_a_(this.entity, (float)packedOverlay, red, green, blue, alpha);
      this.left_shoe.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.right_shoe.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
   }

   public void func_225597_a_(LivingEntity entity, float f1, float f2, float f3, float f4, float f5) {
      super.func_225597_a_(entity, f1, f2, f3, f4, f5);
      EntityRendererManager manager = Minecraft.func_71410_x().func_175598_ae();
      LivingRenderer<?, ?> renderer = (LivingRenderer)manager.func_78713_a(entity);
      if (renderer.func_217764_d() instanceof BipedModel) {
         BipedModel<?> model = (BipedModel)renderer.func_217764_d();
         this.left_shoe.func_217177_a(model.field_178722_k);
         this.right_shoe.func_217177_a(model.field_178721_j);
      }

   }
}
