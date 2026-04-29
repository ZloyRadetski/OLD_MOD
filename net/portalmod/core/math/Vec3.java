package net.portalmod.core.math;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;

public class Vec3 {
   public double x;
   public double y;
   public double z;

   public Vec3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3(double n) {
      this(n, n, n);
   }

   public Vec3(Vec3 v) {
      this(v.x, v.y, v.z);
   }

   public Vec3(Vector3d v) {
      this(v.field_72450_a, v.field_72448_b, v.field_72449_c);
   }

   public Vec3(Vector3f v) {
      this((double)v.func_195899_a(), (double)v.func_195900_b(), (double)v.func_195902_c());
   }

   public Vec3(Vector3i v) {
      this((double)v.func_177958_n(), (double)v.func_177956_o(), (double)v.func_177952_p());
   }

   public Vec3(Direction d) {
      this((double)d.func_176730_m().func_177958_n(), (double)d.func_176730_m().func_177956_o(), (double)d.func_176730_m().func_177952_p());
   }

   public static Vec3 origin() {
      return new Vec3((double)0.0F);
   }

   public static Vec3 infinity() {
      return new Vec3(Double.POSITIVE_INFINITY);
   }

   public static Vec3 xAxis() {
      return new Vec3((double)1.0F, (double)0.0F, (double)0.0F);
   }

   public static Vec3 yAxis() {
      return new Vec3((double)0.0F, (double)1.0F, (double)0.0F);
   }

   public static Vec3 zAxis() {
      return new Vec3((double)0.0F, (double)0.0F, (double)1.0F);
   }

   public static Vec3 fromAxis(Direction.Axis axis) {
      switch (axis) {
         case X:
            return xAxis();
         case Y:
            return yAxis();
         case Z:
            return zAxis();
         default:
            return new Vec3((double)0.0F);
      }
   }

   public double choose(Direction.Axis axis) {
      switch (axis) {
         case X:
            return this.x;
         case Y:
            return this.y;
         case Z:
            return this.z;
         default:
            return (double)0.0F;
      }
   }

   public void set(Direction.Axis axis, double value) {
      switch (axis) {
         case X:
            this.x = value;
            break;
         case Y:
            this.y = value;
            break;
         case Z:
            this.z = value;
      }

   }

   public Vec3 clone() {
      return new Vec3(this);
   }

   public boolean equals(Object o) {
      if (!(o instanceof Vec3)) {
         return false;
      } else {
         Vec3 v = (Vec3)o;
         return this.x == v.x && this.y == v.y && this.z == v.z;
      }
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   public Vec3 add(Vec3 v) {
      this.x += v.x;
      this.y += v.y;
      this.z += v.z;
      return this;
   }

   public Vec3 add(double x, double y, double z) {
      return this.add(new Vec3(x, y, z));
   }

   public Vec3 add(double n) {
      return this.add(new Vec3(n));
   }

   public Vec3 add(Vector3d v) {
      return this.add(new Vec3(v));
   }

   public Vec3 add(Vector3f v) {
      return this.add(new Vec3(v));
   }

   public Vec3 add(Vector3i v) {
      return this.add(new Vec3(v));
   }

   public Vec3 sub(Vec3 v) {
      this.x -= v.x;
      this.y -= v.y;
      this.z -= v.z;
      return this;
   }

   public Vec3 sub(double x, double y, double z) {
      return this.sub(new Vec3(x, y, z));
   }

   public Vec3 sub(double n) {
      return this.sub(new Vec3(n));
   }

   public Vec3 sub(Vector3d v) {
      return this.sub(new Vec3(v));
   }

   public Vec3 sub(Vector3f v) {
      return this.sub(new Vec3(v));
   }

   public Vec3 sub(Vector3i v) {
      return this.sub(new Vec3(v));
   }

   public Vec3 mul(Vec3 v) {
      this.x *= v.x;
      this.y *= v.y;
      this.z *= v.z;
      return this;
   }

   public Vec3 mul(double x, double y, double z) {
      return this.mul(new Vec3(x, y, z));
   }

   public Vec3 mul(double n) {
      return this.mul(new Vec3(n));
   }

   public Vec3 mul(Vector3d v) {
      return this.mul(new Vec3(v));
   }

   public Vec3 mul(Vector3f v) {
      return this.mul(new Vec3(v));
   }

   public Vec3 mul(Vector3i v) {
      return this.mul(new Vec3(v));
   }

   public Vec3 negate() {
      return this.mul((double)-1.0F);
   }

   public Vec3 normalComplement() {
      return this.negate().add((double)1.0F);
   }

   public Vec3 div(Vec3 v) {
      this.x /= v.x;
      this.y /= v.y;
      this.z /= v.z;
      return this;
   }

   public Vec3 div(double x, double y, double z) {
      return this.div(new Vec3(x, y, z));
   }

   public Vec3 div(double n) {
      return this.div(new Vec3(n));
   }

   public Vec3 div(Vector3d v) {
      return this.div(new Vec3(v));
   }

   public Vec3 div(Vector3f v) {
      return this.div(new Vec3(v));
   }

   public Vec3 div(Vector3i v) {
      return this.div(new Vec3(v));
   }

   public double dot(Vec3 v) {
      return this.x * v.x + this.y * v.y + this.z * v.z;
   }

   public double dot(double x, double y, double z) {
      return this.dot(new Vec3(x, y, z));
   }

   public double dot(Vector3d v) {
      return this.dot(new Vec3(v));
   }

   public double dot(Vector3f v) {
      return this.dot(new Vec3(v));
   }

   public double dot(Vector3i v) {
      return this.dot(new Vec3(v));
   }

   public Vec3 cross(Vec3 v) {
      double x = this.y * v.z - this.z * v.y;
      double y = this.z * v.x - this.x * v.z;
      double z = this.x * v.y - this.y * v.x;
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public Vec3 cross(double x, double y, double z) {
      return this.cross(new Vec3(x, y, z));
   }

   public Vec3 cross(Vector3d v) {
      return this.cross(new Vec3(v));
   }

   public Vec3 cross(Vector3f v) {
      return this.cross(new Vec3(v));
   }

   public Vec3 cross(Vector3i v) {
      return this.cross(new Vec3(v));
   }

   public double magnitudeSqr() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public double magnitudeInv() {
      return MathHelper.func_181161_i(this.magnitudeSqr());
   }

   public double magnitude() {
      return Math.sqrt(this.magnitudeSqr());
   }

   public Vec3 normalize() {
      double magnitude = this.magnitudeInv();
      this.x *= magnitude;
      this.y *= magnitude;
      this.z *= magnitude;
      return this;
   }

   public Vec3 compute(Function<Double, Number> f) {
      this.x = ((Number)f.apply(this.x)).doubleValue();
      this.y = ((Number)f.apply(this.y)).doubleValue();
      this.z = ((Number)f.apply(this.z)).doubleValue();
      return this;
   }

   public Vec3 round() {
      return this.compute(Math::round);
   }

   public Vec3 floor() {
      return this.compute(Math::floor);
   }

   public Vec3 ceil() {
      return this.compute(Math::ceil);
   }

   public Vec3 abs() {
      return this.compute(Math::abs);
   }

   public Vec3 blockCenter() {
      return this.floor().add((double)0.5F);
   }

   public Vec3 lerp(Vec3 v, double factor) {
      this.x = this.x * ((double)1.0F - factor) + v.x * factor;
      this.y = this.y * ((double)1.0F - factor) + v.y * factor;
      this.z = this.z * ((double)1.0F - factor) + v.z * factor;
      return this;
   }

   public Vec3 lerp(Vector3d v, double factor) {
      return this.lerp(new Vec3(v), factor);
   }

   public Vec3 lerp(Vector3f v, double factor) {
      return this.lerp(new Vec3(v), factor);
   }

   public Vec3 lerp(Vector3i v, double factor) {
      return this.lerp(new Vec3(v), factor);
   }

   public Vec3 transform(Mat4 m) {
      double x = m.m00 * this.x + m.m01 * this.y + m.m02 * this.z + m.m03;
      double y = m.m10 * this.x + m.m11 * this.y + m.m12 * this.z + m.m13;
      double z = m.m20 * this.x + m.m21 * this.y + m.m22 * this.z + m.m23;
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public Vector3d to3d() {
      return new Vector3d(this.x, this.y, this.z);
   }

   public Vector3f to3f() {
      return new Vector3f((float)this.x, (float)this.y, (float)this.z);
   }

   public Vector3i to3i() {
      return new Vector3i(this.x, this.y, this.z);
   }

   @Nullable
   public Direction toDirection() {
      return Direction.func_218383_a((int)this.x, (int)this.y, (int)this.z);
   }

   public BlockPos toBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }
}
