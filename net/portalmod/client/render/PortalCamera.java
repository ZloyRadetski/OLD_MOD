package net.portalmod.client.render;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import net.portalmod.core.math.Vec3;

public class PortalCamera extends ActiveRenderInfo {
   private float roll;

   public PortalCamera(IBlockReader level, Entity entity, Vec3 position, float xRot, float yRot, float roll, float partialTicks) {
      super.func_216772_a(level, entity, false, false, partialTicks);
      this.func_216776_a(yRot, xRot);
      this.func_216775_b(position.x, position.y, position.z);
      this.roll = roll;
   }

   public PortalCamera(ActiveRenderInfo camera, float partialTicks) {
      this(camera.func_216773_g().field_70170_p, camera.func_216773_g(), new Vec3(camera.func_216785_c()), camera.func_216777_e(), camera.func_216778_f(), camera instanceof PortalCamera ? ((PortalCamera)camera).getRoll() : 0.0F, partialTicks);
   }

   public boolean func_216770_i() {
      return false;
   }

   public void setPitch(float pitch) {
      this.setAnglesInternal(this.func_216778_f(), pitch);
   }

   public void setYaw(float yaw) {
      this.setAnglesInternal(yaw, this.func_216777_e());
   }

   public void setRoll(float roll) {
      this.roll = roll;
   }

   public float getRoll() {
      return this.roll;
   }
}
