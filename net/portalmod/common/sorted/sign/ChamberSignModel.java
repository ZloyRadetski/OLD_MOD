package net.portalmod.common.sorted.sign;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ChamberSignModel extends EntityModel<Entity> {
   public ModelRenderer main;

   public ChamberSignModel() {
      this.field_78090_t = 128;
      this.field_78089_u = 64;
   }

   public void changeModel(ChamberSignEntity sign) {
      this.main = new ModelRenderer(this);
      this.main.func_78793_a(0.0F, 24.0F, 0.0F);
      this.main.func_78784_a(1, 1).func_228303_a_(-12.0F, -24.0F, -1.0F, 24.0F, 48.0F, 2.0F, 0.0F, false);
      this.main.func_78784_a(8, 51).func_228303_a_(-9.0F, 4.0F, -1.01F, (float)(1 + MathHelper.func_76125_a(sign.getProgress(), -1, 8) * 2), 2.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(53 + sign.getLeftDigit() % 5 * 9, sign.getLeftDigit() > 4 ? 20 : 0).func_228303_a_(-9.0F, -17.0F, -1.01F, 8.0F, 18.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(53 + sign.getRightDigit() % 5 * 9, sign.getRightDigit() > 4 ? 20 : 0).func_228303_a_(-1.0F, -17.0F, -1.01F, 8.0F, 18.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon1() ? 4 : 0)).func_228303_a_(-8.0F, 11.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon2() ? 4 : 0)).func_228303_a_(-4.0F, 11.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon3() ? 4 : 0)).func_228303_a_(1.0F, 11.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon4() ? 4 : 0)).func_228303_a_(5.0F, 11.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon5() ? 4 : 0)).func_228303_a_(-8.0F, 15.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon6() ? 4 : 0)).func_228303_a_(-4.0F, 15.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon7() ? 4 : 0)).func_228303_a_(1.0F, 15.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
      this.main.func_78784_a(1, 51 + (sign.getIcon8() ? 4 : 0)).func_228303_a_(5.0F, 15.0F, -1.01F, 3.0F, 3.0F, 1.0F, 0.0F, false);
   }

   public void func_225597_a_(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.main.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.field_78795_f = x;
      modelRenderer.field_78796_g = y;
      modelRenderer.field_78808_h = z;
   }
}
