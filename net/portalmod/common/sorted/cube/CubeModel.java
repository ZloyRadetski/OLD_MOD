package net.portalmod.common.sorted.cube;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CubeModel<T extends Cube> extends EntityModel<T> {
   private final ModelRenderer cube;

   public CubeModel() {
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.cube = new ModelRenderer(this);
      this.cube.func_78793_a(0.0F, 24.0F, 0.0F);
      this.cube.func_78784_a(0, 26).func_228303_a_(-6.0F, -12.5F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);
      this.cube.func_78784_a(0, 0).func_228303_a_(-6.5F, -13.0F, -6.5F, 13.0F, 13.0F, 13.0F, 0.0F, false);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.cube.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
