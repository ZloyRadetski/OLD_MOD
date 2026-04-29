package net.portalmod.core.math;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class VoxelShapeGroup {
   private VoxelShape shape;
   private final Map<String, VoxelShape> parts;

   private VoxelShapeGroup(VoxelShape shape, Map<String, VoxelShape> parts) {
      this.parts = new HashMap();
      this.shape = VoxelShapes.func_216384_a(shape, new VoxelShape[0]);
      parts.forEach((name, part) -> this.parts.put(name, VoxelShapes.func_216384_a(part, new VoxelShape[0])));
   }

   public VoxelShapeGroup clone() {
      return new VoxelShapeGroup(this.shape, this.parts);
   }

   public VoxelShapeGroup transform(Mat4 matrix, boolean transformVariants) {
      this.shape = this.transformShape(this.shape, matrix);
      if (transformVariants) {
         this.parts.forEach((key, variant) -> {
            VoxelShape var10000 = (VoxelShape)this.parts.put(key, this.transformShape(variant, matrix));
         });
      }

      return this;
   }

   public VoxelShapeGroup transform(Mat4 matrix) {
      return this.transform(matrix, true);
   }

   private VoxelShape transformShape(VoxelShape shape, Mat4 matrix) {
      VoxelShape[] transformed = new VoxelShape[]{VoxelShapes.func_197880_a()};
      shape.func_197755_b((minX, minY, minZ, maxX, maxY, maxZ) -> {
         Vec3 min = (new Vec3(minX, minY, minZ)).transform(matrix);
         Vec3 max = (new Vec3(maxX, maxY, maxZ)).transform(matrix);
         VoxelShape box = VoxelShapes.func_197873_a(min.x, min.y, min.z, max.x, max.y, max.z);
         transformed[0] = VoxelShapes.func_197872_a(transformed[0], box);
      });
      return transformed[0];
   }

   public VoxelShape getVariant(String key) {
      return VoxelShapes.func_197872_a(this.shape, this.getPart(key));
   }

   public VoxelShape getPart(String key) {
      return this.parts.containsKey(key) ? VoxelShapes.func_216384_a((VoxelShape)this.parts.get(key), new VoxelShape[0]) : VoxelShapes.func_197880_a();
   }

   public VoxelShape getShape() {
      return this.shape;
   }

   public static class Builder {
      private VoxelShape shape = VoxelShapes.func_197880_a();
      private Map<String, VoxelShape> parts = new HashMap();

      public Builder() {
      }

      public Builder(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
         this.add(VoxelShapes.func_197873_a(minX / (double)16.0F, minY / (double)16.0F, minZ / (double)16.0F, maxX / (double)16.0F, maxY / (double)16.0F, maxZ / (double)16.0F));
      }

      public Builder add(VoxelShape shape) {
         this.shape = VoxelShapes.func_197872_a(this.shape, shape);
         return this;
      }

      public Builder add(VoxelShapeGroup shape) {
         return this.add(shape.shape);
      }

      public Builder add(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
         return this.add(VoxelShapes.func_197873_a(minX / (double)16.0F, minY / (double)16.0F, minZ / (double)16.0F, maxX / (double)16.0F, maxY / (double)16.0F, maxZ / (double)16.0F));
      }

      public Builder addPart(String key, VoxelShape part) {
         this.parts.put(key, part);
         return this;
      }

      public Builder addPart(String key, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
         return this.addPart(key, VoxelShapes.func_197873_a(minX / (double)16.0F, minY / (double)16.0F, minZ / (double)16.0F, maxX / (double)16.0F, maxY / (double)16.0F, maxZ / (double)16.0F));
      }

      public VoxelShapeGroup build() {
         return new VoxelShapeGroup(this.shape, this.parts);
      }
   }
}
