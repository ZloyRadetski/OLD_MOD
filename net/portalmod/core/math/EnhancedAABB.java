package net.portalmod.core.math;

import java.util.function.Function;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EnhancedAABB extends AxisAlignedBB {
   public EnhancedAABB(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
      super(xMin, yMin, zMin, xMax, yMax, zMax);
   }

   public EnhancedAABB(BlockPos blockPos) {
      super(blockPos);
   }

   public EnhancedAABB(BlockPos blockPos1, BlockPos blockPos2) {
      super(blockPos1, blockPos2);
   }

   public EnhancedAABB(Vec3 vec1, Vec3 vec2) {
      super(vec1.to3d(), vec2.to3d());
   }

   public EnhancedAABB forEachVertex(Function<Vec3, Vec3> func) {
      Vec3 min = new Vec3(this.field_72340_a, this.field_72338_b, this.field_72339_c);
      Vec3 max = new Vec3(this.field_72336_d, this.field_72337_e, this.field_72334_f);
      return new EnhancedAABB((Vec3)func.apply(min), (Vec3)func.apply(max));
   }

   public EnhancedAABB translate(Vec3 offset) {
      return this.forEachVertex((vec) -> vec.add(offset));
   }

   public EnhancedAABB scale(Vec3 factor) {
      return this.forEachVertex((vec) -> vec.mul(factor));
   }

   public EnhancedAABB transform(Mat4 matrix) {
      return this.forEachVertex((vec) -> vec.transform(matrix));
   }
}
