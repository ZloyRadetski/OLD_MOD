package net.portalmod.common.sorted.portal;

import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class OrthonormalBasis {
   private Vec3 x;
   private Vec3 y;
   private Vec3 z;
   public static final OrthonormalBasis CANONICAL = new OrthonormalBasis(Vec3.xAxis(), Vec3.yAxis());

   public OrthonormalBasis(Vec3 x, Vec3 y) {
      this.x = x.clone();
      this.y = y.clone();
      this.z = x.clone().cross(y);
   }

   public Mat4 getChangeOfBasisMatrix(OrthonormalBasis destination) {
      return destination.getChangeOfBasisFromCanonicalMatrix().mul(this.getChangeOfBasisToCanonicalMatrix());
   }

   public Mat4 getChangeOfBasisToCanonicalMatrix() {
      return new Mat4(this.x.x, this.x.y, this.x.z, (double)0.0F, this.y.x, this.y.y, this.y.z, (double)0.0F, this.z.x, this.z.y, this.z.z, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
   }

   public Mat4 getChangeOfBasisFromCanonicalMatrix() {
      return new Mat4(this.x.x, this.y.x, this.z.x, (double)0.0F, this.x.y, this.y.y, this.z.y, (double)0.0F, this.x.z, this.y.z, this.z.z, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
   }

   public OrthonormalBasis transform(Mat4 matrix) {
      this.x = this.x.transform(matrix);
      this.y = this.y.transform(matrix);
      this.z = this.z.transform(matrix);
      return this;
   }

   public Vec3 getX() {
      return this.x;
   }

   public Vec3 getY() {
      return this.y;
   }

   public Vec3 getZ() {
      return this.z;
   }
}
