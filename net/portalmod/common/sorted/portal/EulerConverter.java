package net.portalmod.common.sorted.portal;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class EulerConverter {
   public static EulerAngles toEulerAngles(OrthonormalBasis basis) {
      Vec3 look = basis.getZ();
      Vec3 up = basis.getY();
      float pitch = (float)(Math.acos(MathHelper.func_151237_a(look.dot(Vec3.yAxis()), (double)-1.0F, (double)1.0F)) * (double)180.0F / Math.PI - (double)90.0F);
      Vec3 yawVec = Vec3.yAxis().cross(look.clone().cross(Vec3.yAxis())).normalize();
      float yawSin = (float)Vec3.zAxis().cross(yawVec).y;
      float yawCos = (float)Vec3.zAxis().dot(yawVec);
      float yaw = -((float)(Math.atan2((double)yawSin, (double)yawCos) * (double)180.0F / Math.PI));
      Vec3 upUnrotated = up.clone().transform(new Mat4(Vector3f.field_229180_c_.func_229187_a_(-yaw))).transform(new Mat4(Vector3f.field_229179_b_.func_229187_a_(-pitch)));
      float rollSin = (float)Vec3.yAxis().cross(upUnrotated).dot(Vec3.zAxis());
      float rollCos = (float)Vec3.yAxis().dot(upUnrotated);
      float roll = (float)(Math.atan2((double)rollSin, (double)rollCos) * (double)180.0F / Math.PI);
      return new EulerAngles(pitch, yaw, roll);
   }

   public static EulerAngles toEulerAnglesLeastRoll(OrthonormalBasis basis) {
      EulerAngles angles = toEulerAngles(basis);
      return !(angles.roll > 90.0F) && !(angles.roll < -90.0F) ? angles : new EulerAngles(180.0F - MathHelper.func_188207_b(angles.pitch, 360.0F), MathHelper.func_188207_b(angles.yaw, 360.0F) + 180.0F, MathHelper.func_188207_b(angles.roll, 360.0F) - 180.0F);
   }

   public static EulerAngles toEulerAngles(Vec3 forward, Vec3 up) {
      return toEulerAngles(new OrthonormalBasis(up.clone().cross(forward), up));
   }

   public static EulerAngles toEulerAnglesLeastRoll(Vec3 forward, Vec3 up) {
      return toEulerAnglesLeastRoll(new OrthonormalBasis(up.clone().cross(forward), up));
   }

   public static OrthonormalBasis toVectors(Vec3 orientation) {
      Mat4 rotation = Mat4.identity().mul(new Mat4(Vector3f.field_229181_d_.func_229187_a_(-((float)orientation.y)))).mul(new Mat4(Vector3f.field_229179_b_.func_229187_a_((float)orientation.x))).mul(new Mat4(Vector3f.field_229183_f_.func_229187_a_((float)orientation.z)));
      return new OrthonormalBasis(Vec3.xAxis().transform(rotation), Vec3.yAxis().transform(rotation));
   }

   public static OrthonormalBasis toVectors(float pitch, float yaw, float roll) {
      return toVectors(new Vec3((double)pitch, (double)yaw, (double)roll));
   }

   public static class EulerAngles {
      private final float pitch;
      private final float yaw;
      private final float roll;

      protected EulerAngles(float pitch, float yaw, float roll) {
         this.pitch = pitch;
         this.yaw = yaw;
         this.roll = roll;
      }

      public float getPitch() {
         return this.pitch;
      }

      public float getYaw() {
         return this.yaw;
      }

      public float getRoll() {
         return this.roll;
      }
   }
}
