package net.portalmod.common.sorted.faithplate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.portalmod.client.animation.AnimatedModel;
import net.portalmod.core.init.AnimationInit;

public class FaithPlatePlateModel extends AnimatedModel {
   protected final ModelRenderer bone = new ModelRenderer(this);
   protected final ModelRenderer bb_main;

   public FaithPlatePlateModel() {
      super(64, 64);
      this.bone.func_78793_a(-2.5F, 29.5F, -1.5F);
      this.setRotationAngle(this.bone, -0.2182F, 0.0F, 0.0F);
      this.attachAnimation("launch", this.bone, XROT, AnimationInit.FAITHPLATE_BONE);
      ModelRenderer handles_r1 = new ModelRenderer(this);
      handles_r1.func_78793_a(3.5F, 1.1778F, -2.9707F);
      this.bone.func_78792_a(handles_r1);
      this.setRotationAngle(handles_r1, 0.2182F, 0.0F, 0.0F);
      handles_r1.func_78784_a(10, 0).func_228303_a_(-5.0F, -4.6778F, -3.0293F, 8.0F, 7.0F, 18.0F, 0.0F, false);
      ModelRenderer arm = new ModelRenderer(this);
      arm.func_78793_a(2.5F, -2.4837F, 8.3688F);
      this.bone.func_78792_a(arm);
      this.setRotationAngle(arm, 0.3491F, 0.0F, 0.0F);
      this.attachAnimation("launch", arm, XROT, AnimationInit.FAITHPLATE_ARM);
      ModelRenderer weight_r1 = new ModelRenderer(this);
      weight_r1.func_78793_a(0.0F, -0.1033F, 0.1286F);
      arm.func_78792_a(weight_r1);
      this.setRotationAngle(weight_r1, -0.1309F, 0.0F, 0.0F);
      weight_r1.func_78784_a(0, 0).func_228303_a_(-3.0F, -3.87F, -4.114F, 6.0F, 8.0F, 8.0F, 0.0F, false);
      ModelRenderer lock = new ModelRenderer(this);
      lock.func_78793_a(-2.5F, -1.0671F, 0.2114F);
      arm.func_78792_a(lock);
      this.setRotationAngle(lock, -0.1309F, 0.0F, 0.0F);
      lock.func_78784_a(44, 0).func_228303_a_(0.0F, -5.3019F, -0.2783F, 5.0F, 2.0F, 4.0F, 0.0F, false);
      this.attachAnimation("launch", lock, XROT, AnimationInit.FAITHPLATE_LOCK);
      ModelRenderer plate = new ModelRenderer(this);
      plate.func_78793_a(-2.0F, -3.0013F, 2.2893F);
      arm.func_78792_a(plate);
      this.setRotationAngle(plate, 0.2618F, 0.0F, 0.0F);
      this.attachAnimation("launch", plate, XROT, AnimationInit.FAITHPLATE_PLATE);
      ModelRenderer plate_beam_r1 = new ModelRenderer(this);
      plate_beam_r1.func_78793_a(2.5F, -5.3086F, -3.081F);
      plate.func_78792_a(plate_beam_r1);
      this.setRotationAngle(plate_beam_r1, -0.3927F, 0.0F, 0.0F);
      plate_beam_r1.func_78784_a(0, 25).func_228303_a_(-2.0F, 3.01F, -10.0F, 3.0F, 3.0F, 9.0F, 0.0F, false);
      plate_beam_r1.func_78784_a(4, 25).func_228303_a_(-5.0F, 1.0F, -16.0F, 9.0F, 2.0F, 20.0F, 0.0F, false);
      this.bb_main = new ModelRenderer(this);
      this.bb_main.func_78793_a(0.0F, 24.0F, 0.0F);
      handles_r1 = new ModelRenderer(this);
      handles_r1.func_78793_a(0.0F, 0.0F, 0.0F);
      this.bb_main.func_78792_a(handles_r1);
      this.setRotationAngle(handles_r1, 0.0F, 1.5708F, 0.0F);
      handles_r1.func_78784_a(-14, 47).func_228303_a_(-15.0F, 0.0F, -7.0F, 30.0F, 0.0F, 14.0F, 0.0F, false);
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.bone.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
      this.bb_main.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
