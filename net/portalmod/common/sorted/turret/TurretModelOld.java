package net.portalmod.common.sorted.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class TurretModelOld<T extends TurretEntity> extends EntityModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer wing_left;
   private final ModelRenderer wing_right;
   private final ModelRenderer leg_back;
   private final ModelRenderer cube_r1;
   private final ModelRenderer cube_r2;
   private final ModelRenderer cube_r3;
   private final ModelRenderer leg_left;
   private final ModelRenderer leg_right;

   public TurretModelOld() {
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      this.head = new ModelRenderer(this);
      this.head.func_78793_a(0.0F, 14.5F, 0.0F);
      this.head.func_78784_a(4, 18).func_228303_a_(-2.5F, -9.25F, -2.5F, 5.0F, 9.75F, 5.0F, 0.0F, false);
      this.head.func_78784_a(0, 0).func_228303_a_(-2.75F, -9.25F, -2.75F, 5.5F, 11.0F, 5.5F, 0.0F, false);
      this.head.func_78784_a(0, 1).func_228303_a_(1.75F, -12.5F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, false);
      this.wing_left = new ModelRenderer(this);
      this.wing_left.func_78793_a(2.5F, 12.0F, 0.0F);
      this.wing_left.func_78784_a(20, 0).func_228303_a_(1.25F, -5.0F, -2.0F, 2.0F, 10.0F, 4.0F, 0.0F, false);
      this.wing_left.func_78784_a(24, 14).func_228303_a_(1.25F, -1.5F, -1.5F, 1.0F, 4.0F, 3.0F, 0.0F, false);
      this.wing_left.func_78784_a(8, 16).func_228303_a_(-0.85F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
      this.wing_right = new ModelRenderer(this);
      this.wing_right.func_78793_a(-2.5F, 12.0F, 0.0F);
      this.wing_right.func_78784_a(20, 0).func_228303_a_(-3.25F, -5.0F, -2.0F, 2.0F, 10.0F, 4.0F, 0.0F, true);
      this.wing_right.func_78784_a(24, 14).func_228303_a_(-2.25F, -1.5F, -1.5F, 1.0F, 4.0F, 3.0F, 0.0F, true);
      this.wing_right.func_78784_a(8, 16).func_228303_a_(-2.15F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, true);
      this.leg_back = new ModelRenderer(this);
      this.leg_back.func_78793_a(0.0F, 16.0F, 6.0F);
      this.setRotationAngle(this.leg_back, 0.3927F, 0.0F, 0.0F);
      this.cube_r1 = new ModelRenderer(this);
      this.cube_r1.func_78793_a(0.0F, 0.4807F, -4.9875F);
      this.leg_back.func_78792_a(this.cube_r1);
      this.setRotationAngle(this.cube_r1, -1.6581F, 0.0F, 0.0F);
      this.cube_r1.func_78784_a(26, 20).func_228303_a_(0.0F, -4.7059F, -3.0196F, 0.0F, 5.0F, 3.0F, 0.0F, false);
      this.cube_r2 = new ModelRenderer(this);
      this.cube_r2.func_78793_a(0.5F, 1.75F, -0.5F);
      this.leg_back.func_78792_a(this.cube_r2);
      this.setRotationAngle(this.cube_r2, -0.2618F, 0.0F, 0.0F);
      this.cube_r2.func_78784_a(20, 14).func_228303_a_(-1.0F, -1.7741F, -0.1173F, 1.0F, 8.0F, 1.0F, 0.0F, false);
      this.cube_r3 = new ModelRenderer(this);
      this.cube_r3.func_78793_a(1.0F, 1.0F, -1.0F);
      this.leg_back.func_78792_a(this.cube_r3);
      this.setRotationAngle(this.cube_r3, -0.2618F, 0.0F, 0.0F);
      this.cube_r3.func_78784_a(16, 1).func_228303_a_(-2.0F, -3.0241F, -0.1173F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      this.leg_left = new ModelRenderer(this);
      this.leg_left.func_78793_a(2.5F, 19.0F, -5.0F);
      this.setRotationAngle(this.leg_left, -0.3927F, -0.3927F, 0.0F);
      this.leg_left.func_78784_a(0, 17).func_228303_a_(-1.0F, -2.3858F, -1.574F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      this.leg_left.func_78784_a(0, 22).func_228303_a_(-0.5F, -0.3858F, -1.074F, 1.0F, 6.0F, 1.0F, 0.0F, false);
      this.leg_left.func_78784_a(26, 21).func_228303_a_(0.0F, -4.8858F, 0.426F, 0.0F, 4.0F, 3.0F, 0.0F, false);
      this.leg_right = new ModelRenderer(this);
      this.leg_right.func_78793_a(-2.5F, 19.0F, -5.0F);
      this.setRotationAngle(this.leg_right, -0.3927F, 0.3927F, 0.0F);
      this.leg_right.func_78784_a(0, 17).func_228303_a_(-1.0F, -2.3858F, -1.574F, 2.0F, 2.0F, 2.0F, 0.0F, true);
      this.leg_right.func_78784_a(0, 22).func_228303_a_(-0.5F, -0.3858F, -1.074F, 1.0F, 6.0F, 1.0F, 0.0F, true);
      this.leg_right.func_78784_a(26, 21).func_228303_a_(0.0F, -4.8858F, 0.426F, 0.0F, 4.0F, 3.0F, 0.0F, true);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.head.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.wing_left.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.wing_right.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.leg_back.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.leg_left.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.leg_right.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
