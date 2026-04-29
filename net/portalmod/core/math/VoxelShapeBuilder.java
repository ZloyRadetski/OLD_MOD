package net.portalmod.core.math;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class VoxelShapeBuilder {
   private VoxelShape shape = VoxelShapes.func_197880_a();

   public VoxelShapeBuilder add(VoxelShape shape) {
      this.shape = VoxelShapes.func_197872_a(this.shape, shape);
      return this;
   }

   public VoxelShapeBuilder add(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return this.add(VoxelShapes.func_197873_a(minX / (double)16.0F, minY / (double)16.0F, minZ / (double)16.0F, maxX / (double)16.0F, maxY / (double)16.0F, maxZ / (double)16.0F));
   }

   public VoxelShape build() {
      return this.shape;
   }
}
