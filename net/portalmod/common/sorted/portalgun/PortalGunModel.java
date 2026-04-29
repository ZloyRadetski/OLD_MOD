package net.portalmod.common.sorted.portalgun;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.portalmod.client.animation.AnimatedModel;
import net.portalmod.core.init.AnimationInit;

public class PortalGunModel extends AnimatedModel {
   protected final ModelRenderer gun = new ModelRenderer(this);
   protected final ModelRenderer colour;
   protected final ModelRenderer stripes;

   public PortalGunModel() {
      super(32, 32);
      ModelRenderer gun_front = new ModelRenderer(this);
      gun_front.func_78793_a(0.0F, 24.0F, 0.0F);
      gun_front.func_78784_a(16, 13).func_228303_a_(-2.0F, -3.5F, -3.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
      gun_front.func_78784_a(18, 22).func_228303_a_(-1.0F, -4.0F, -4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
      gun_front.func_78784_a(11, 12).func_228303_a_(-1.5F, -4.5F, -2.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      gun_front.func_78784_a(21, 20).func_228303_a_(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 0.0F, 0.0F, false);
      ModelRenderer prong_right = new ModelRenderer(this);
      prong_right.func_78793_a(-1.2985F, -2.2491F, -2.5F);
      gun_front.func_78792_a(prong_right);
      this.setRotationAngle(prong_right, -1.5708F, 0.0F, -2.0944F);
      prong_right.func_78784_a(11, 3).func_228303_a_(0.067F, -0.5F, -2.616F, 0.0F, 5.0F, 3.0F, 0.0F, true);
      ModelRenderer prong_left = new ModelRenderer(this);
      prong_left.func_78793_a(1.2985F, -2.2491F, -2.5F);
      gun_front.func_78792_a(prong_left);
      this.setRotationAngle(prong_left, -1.5708F, 0.0F, 2.0944F);
      prong_left.func_78784_a(18, 3).func_228303_a_(-0.067F, -0.5F, -2.616F, 0.0F, 5.0F, 3.0F, 0.0F, false);
      ModelRenderer prong_top = new ModelRenderer(this);
      prong_top.func_78793_a(0.0F, -4.0F, -2.0F);
      gun_front.func_78792_a(prong_top);
      this.setRotationAngle(prong_top, -1.5708F, 0.0F, 0.0F);
      prong_top.func_78784_a(25, 3).func_228303_a_(0.0F, -0.5F, -3.0F, 0.0F, 5.0F, 3.0F, 0.0F, false);
      ModelRenderer potatoParent = new ModelRenderer(this);
      potatoParent.func_78793_a(0.0F, 2.0F, -3.0F);
      prong_top.func_78792_a(potatoParent);
      this.setRotationAngle(potatoParent, 1.7F, 0.0F, 0.4F);
      ModelRenderer potato = new ModelRenderer(this);
      potato.func_78793_a(-0.2206F, -0.0222F, -0.3924F);
      potatoParent.func_78792_a(potato);
      this.setRotationAngle(potato, -0.8248F, -0.6669F, 0.9374F);
      potato.func_78784_a(21, 1).func_228303_a_(-1.3119F, -1.0513F, -1.1565F, 3.0F, 2.0F, 2.0F, 0.0F, false);
      ModelRenderer potatoWires = new ModelRenderer(this);
      potatoWires.func_78793_a(-0.2018F, -0.067F, -0.7244F);
      potatoParent.func_78792_a(potatoWires);
      this.setRotationAngle(potatoWires, 2.5876F, -0.0168F, -2.7786F);
      potatoWires.func_78784_a(12, 1).func_228303_a_(-2.3359F, -1.9776F, -0.3457F, 4.0F, 4.0F, 0.0F, 0.0F, false);
      ModelRenderer gun_base = new ModelRenderer(this);
      gun_base.func_78793_a(0.0F, 24.0F, 0.0F);
      gun_base.func_78784_a(1, 21).func_228303_a_(-2.5F, -5.5F, 2.0F, 5.0F, 4.0F, 6.0F, 0.0F, false);
      gun_base.func_78784_a(1, 14).func_228303_a_(-1.5F, -3.0F, 4.0F, 3.0F, 2.0F, 3.0F, 0.0F, false);
      ModelRenderer bottom = new ModelRenderer(this);
      bottom.func_78793_a(0.0F, -2.0F, 2.5F);
      gun_base.func_78792_a(bottom);
      this.setRotationAngle(bottom, 0.0F, 3.1416F, 0.0F);
      bottom.func_78784_a(1, 14).func_228303_a_(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, 0.0F, false);
      this.gun.func_78792_a(gun_front);
      this.gun.func_78792_a(gun_base);
      this.attachAnimation("shoot", gun_front, Z, AnimationInit.COMPRESSION);
      this.attachAnimation("shoot", prong_right, XROT, AnimationInit.CLAWS);
      this.attachAnimation("shoot", prong_left, XROT, AnimationInit.CLAWS);
      this.attachAnimation("shoot", prong_top, XROT, AnimationInit.CLAWS);
      this.attachAnimation("lift", gun_front, Z, AnimationInit.COMPRESSION_START);
      this.attachAnimation("lift", prong_right, XROT, AnimationInit.CLAWS_OPEN);
      this.attachAnimation("lift", prong_left, XROT, AnimationInit.CLAWS_OPEN);
      this.attachAnimation("lift", prong_top, XROT, AnimationInit.CLAWS_OPEN);
      this.attachAnimation("drop", gun_front, Z, AnimationInit.COMPRESSION_STOP);
      this.attachAnimation("drop", prong_right, XROT, AnimationInit.CLAWS_CLOSE);
      this.attachAnimation("drop", prong_left, XROT, AnimationInit.CLAWS_CLOSE);
      this.attachAnimation("drop", prong_top, XROT, AnimationInit.CLAWS_CLOSE);
      this.colour = new ModelRenderer(this);
      gun_front = new ModelRenderer(this);
      gun_front.func_78793_a(0.0F, 24.0F, 0.0F);
      gun_front.func_78784_a(1, 6).func_228303_a_(-1.0F, -4.0F, -1.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
      gun_front.func_78784_a(9, 7).func_228303_a_(-0.5F, -3.5F, -2.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
      prong_right = new ModelRenderer(this);
      prong_right.func_78793_a(0.0F, 24.0F, 0.0F);
      prong_right.func_78784_a(-1, 12).func_228303_a_(-0.5F, -5.5F, 3.0F, 1.0F, 0.0F, 2.0F, 0.0F, false);
      this.colour.func_78792_a(gun_front);
      this.colour.func_78792_a(prong_right);
      this.attachAnimation("shoot", gun_front, Z, AnimationInit.COMPRESSION);
      this.attachAnimation("lift", gun_front, Z, AnimationInit.COMPRESSION_START);
      this.attachAnimation("drop", gun_front, Z, AnimationInit.COMPRESSION_STOP);
      this.stripes = new ModelRenderer(this);
      gun_front = new ModelRenderer(this);
      gun_front.func_78793_a(0.0F, 24.0F, 0.0F);
      prong_right = new ModelRenderer(this);
      prong_right.func_78793_a(-2.0F, -4.0F, 5.0F);
      gun_front.func_78792_a(prong_right);
      this.setRotationAngle(prong_right, 0.0F, 1.5708F, 0.0F);
      prong_right.func_78784_a(1, 1).func_228303_a_(-3.0F, -1.5F, -0.5F, 6.0F, 3.0F, 1.0F, 0.004F, true);
      prong_left = new ModelRenderer(this);
      prong_left.func_78793_a(2.0F, -4.0F, 5.0F);
      gun_front.func_78792_a(prong_left);
      this.setRotationAngle(prong_left, 0.0F, -1.5708F, 0.0F);
      prong_left.func_78784_a(1, 1).func_228303_a_(-3.0F, -1.5F, -0.5F, 6.0F, 3.0F, 1.0F, 0.004F, false);
      this.stripes.func_78792_a(gun_front);
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.gun.func_228308_a_(matrixStack, buffer, packedLight, packedOverlay);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
