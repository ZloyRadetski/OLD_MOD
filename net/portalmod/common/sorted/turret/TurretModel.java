package net.portalmod.common.sorted.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3d;

public class TurretModel<T extends TurretEntity> extends EntityModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer wing_left;
   private final ModelRenderer wing_right;
   private final ModelRenderer leg_back;
   private final ModelRenderer leg_left;
   private final ModelRenderer leg_right;

   public TurretModel() {
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      this.head = new ModelRenderer(this);
      this.head.func_78793_a(0.0F, 14.5F, 0.0F);
      this.head.func_78784_a(4, 18).func_228303_a_(-2.5F, -8.5F, -2.5F, 5.0F, 9.0F, 5.0F, 0.0F, false);
      this.head.func_78784_a(0, 0).func_228303_a_(-2.5F, -9.25F, -2.5F, 5.0F, 11.0F, 5.0F, 0.25F, false);
      this.head.func_78784_a(0, 1).func_228303_a_(1.75F, -12.5F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F, false);
      this.wing_left = new ModelRenderer(this);
      this.wing_left.func_78784_a(20, 0).func_228303_a_(1.25F, -5.0F, -2.0F, 2.0F, 10.0F, 4.0F, 0.0F, false);
      this.wing_left.func_78784_a(24, 14).func_228303_a_(1.25F, -1.5F, -1.5F, 1.0F, 4.0F, 3.0F, 0.0F, false);
      this.wing_left.func_78784_a(8, 16).func_228303_a_(-0.85F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
      this.wing_right = new ModelRenderer(this);
      this.wing_right.func_78784_a(20, 0).func_228303_a_(-3.25F, -5.0F, -2.0F, 2.0F, 10.0F, 4.0F, 0.0F, true);
      this.wing_right.func_78784_a(24, 14).func_228303_a_(-2.25F, -1.5F, -1.5F, 1.0F, 4.0F, 3.0F, 0.0F, true);
      this.wing_right.func_78784_a(8, 16).func_228303_a_(-2.15F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, true);
      this.leg_back = new ModelRenderer(this);
      this.leg_back.func_78793_a(0.0F, 16.0F, 6.0F);
      this.setRotationAngle(this.leg_back, 0.3927F, 0.0F, 0.0F);
      ModelRenderer cube_r1 = new ModelRenderer(this);
      cube_r1.func_78793_a(0.0F, 0.4807F, -4.9875F);
      this.leg_back.func_78792_a(cube_r1);
      this.setRotationAngle(cube_r1, -1.6581F, 0.0F, 0.0F);
      cube_r1.func_78784_a(26, 20).func_228303_a_(0.0F, -4.7059F, -3.0196F, 0.0F, 5.0F, 3.0F, 0.0F, false);
      ModelRenderer cube_r2 = new ModelRenderer(this);
      cube_r2.func_78793_a(0.5F, 1.75F, -0.5F);
      this.leg_back.func_78792_a(cube_r2);
      this.setRotationAngle(cube_r2, -0.2618F, 0.0F, 0.0F);
      cube_r2.func_78784_a(20, 14).func_228303_a_(-1.0F, -1.7741F, -0.1173F, 1.0F, 8.0F, 1.0F, 0.0F, false);
      ModelRenderer cube_r3 = new ModelRenderer(this);
      cube_r3.func_78793_a(1.0F, 1.0F, -1.0F);
      this.leg_back.func_78792_a(cube_r3);
      this.setRotationAngle(cube_r3, -0.2618F, 0.0F, 0.0F);
      cube_r3.func_78784_a(16, 1).func_228303_a_(-2.0F, -3.0241F, -0.1173F, 2.0F, 2.0F, 2.0F, 0.0F, false);
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
      float yRod = -((float)Math.toRadians((double)entity.field_70177_z));
      Vector3d laserDir = entity.turretToTarget.func_72432_b();
      float rotatedX = (float)laserDir.field_72450_a * (float)Math.cos((double)yRod) - (float)laserDir.field_72449_c * (float)Math.sin((double)yRod);
      float rotatedZ = (float)laserDir.field_72450_a * (float)Math.sin((double)yRod) + (float)laserDir.field_72449_c * (float)Math.cos((double)yRod);
      Vector3d laserDirR = new Vector3d((double)rotatedX, laserDir.field_72448_b, (double)rotatedZ);
      float yaw = -((float)Math.atan2(laserDirR.field_72450_a, laserDirR.field_72449_c));
      float pitch = -((float)Math.asin(laserDirR.field_72448_b));
      TurretState state = entity.getState();
      float wingOffset = state.wingsOpen() ? 2.0F : 0.0F;
      int tickTime = entity.field_70173_aa + entity.func_145782_y();
      float recoil = state == TurretState.SHOOTING && entity.shouldShoot() ? ((float)(tickTime % 2) + Minecraft.func_71410_x().func_184121_ak()) * 0.1F - 0.07F : 0.0F;
      this.setRotationAngle(this.wing_left, pitch + recoil, yaw, 0.0F);
      this.setRotationAngle(this.wing_right, pitch + recoil, yaw, 0.0F);
      this.wing_left.func_78793_a(0.5F + wingOffset, 12.0F, 0.0F);
      this.wing_right.func_78793_a(-0.5F - wingOffset, 12.0F, 0.0F);
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
