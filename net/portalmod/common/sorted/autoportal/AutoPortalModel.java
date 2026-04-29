package net.portalmod.common.sorted.autoportal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class AutoPortalModel extends EntityModel<Entity> {
   public final ModelRenderer wawas;
   public final ModelRenderer frame;

   public AutoPortalModel() {
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.wawas = new ModelRenderer(this);
      this.wawas.func_78793_a(0.0F, 24.0F, 0.0F);
      this.wawas.func_78784_a(17, 1).func_228303_a_(7.0F, -14.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, true);
      this.wawas.func_78784_a(17, 4).func_228303_a_(7.0F, -9.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, true);
      this.wawas.func_78784_a(17, 7).func_228303_a_(7.0F, -4.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, true);
      this.wawas.func_78784_a(17, 7).func_228303_a_(7.0F, 2.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, true);
      this.wawas.func_78784_a(17, 10).func_228303_a_(7.0F, 7.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, true);
      this.wawas.func_78784_a(17, 13).func_228303_a_(7.0F, 12.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, true);
      this.wawas.func_78784_a(17, 1).func_228303_a_(-11.0F, -14.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, false);
      this.wawas.func_78784_a(17, 4).func_228303_a_(-11.0F, -9.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, false);
      this.wawas.func_78784_a(17, 7).func_228303_a_(-11.0F, -4.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, false);
      this.wawas.func_78784_a(17, 7).func_228303_a_(-11.0F, 2.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, false);
      this.wawas.func_78784_a(17, 10).func_228303_a_(-11.0F, 7.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, false);
      this.wawas.func_78784_a(17, 13).func_228303_a_(-11.0F, 12.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, false);
      this.frame = new ModelRenderer(this);
      this.frame.func_78793_a(-11.0F, 40.0F, 1.05F);
      this.frame.func_78784_a(1, 1).func_228303_a_(-2.0F, -32.0F, -1.05F, 2.0F, 32.0F, 1.0F, 0.0F, false);
      this.frame.func_78784_a(8, 1).func_228303_a_(0.0F, -32.0F, -1.05F, 1.0F, 32.0F, 1.0F, 0.0F, false);
      this.frame.func_78784_a(1, 1).func_228303_a_(22.0F, -32.0F, -1.05F, 2.0F, 32.0F, 1.0F, 0.0F, true);
      this.frame.func_78784_a(8, 1).func_228303_a_(21.0F, -32.0F, -1.05F, 1.0F, 32.0F, 1.0F, 0.0F, true);
   }

   public void func_225597_a_(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      this.wawas.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
      this.frame.func_228309_a_(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
