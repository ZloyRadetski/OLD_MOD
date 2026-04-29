package net.portalmod.core.math;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public class AABBUtil {
   public static AxisAlignedBB forEachVertex(AxisAlignedBB aabb, Function<Vec3, Vec3> func) {
      Vec3 min = new Vec3(aabb.field_72340_a, aabb.field_72338_b, aabb.field_72339_c);
      Vec3 max = new Vec3(aabb.field_72336_d, aabb.field_72337_e, aabb.field_72334_f);
      return new EnhancedAABB((Vec3)func.apply(min), (Vec3)func.apply(max));
   }

   public static AxisAlignedBB translate(AxisAlignedBB aabb, Vec3 offset) {
      return forEachVertex(aabb, (vec) -> vec.add(offset));
   }

   public static AxisAlignedBB scale(AxisAlignedBB aabb, Vec3 factor) {
      return forEachVertex(aabb, (vec) -> vec.mul(factor));
   }

   public static AxisAlignedBB transform(AxisAlignedBB aabb, Mat4 matrix) {
      return forEachVertex(aabb, (vec) -> vec.transform(matrix));
   }

   public static VoxelShape forEachBox(VoxelShape shape, Function<AxisAlignedBB, AxisAlignedBB> operation) {
      VoxelShape[] result = new VoxelShape[]{VoxelShapes.func_197880_a()};
      shape.func_197755_b((minX, minY, minZ, maxX, maxY, maxZ) -> result[0] = VoxelShapes.func_197872_a(result[0], VoxelShapes.func_197881_a((AxisAlignedBB)operation.apply(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)))));
      return result[0];
   }

   public static double getSide(AxisAlignedBB aabb, Direction normal) {
      return normal.func_176743_c() == AxisDirection.POSITIVE ? aabb.func_197742_b(normal.func_176740_k()) : aabb.func_197745_a(normal.func_176740_k());
   }

   public static List<BlockPos> getBlocksWithin(AxisAlignedBB aabb) {
      List<BlockPos> blocks = Lists.newArrayList();

      for(int z = (int)Math.floor(aabb.field_72339_c); z <= (int)Math.floor(aabb.field_72334_f); ++z) {
         for(int y = (int)Math.floor(aabb.field_72338_b); y <= (int)Math.floor(aabb.field_72337_e); ++y) {
            for(int x = (int)Math.floor(aabb.field_72340_a); x <= (int)Math.floor(aabb.field_72336_d); ++x) {
               blocks.add(new BlockPos(x, y, z));
            }
         }
      }

      return blocks;
   }

   public static boolean checkBlocksWithin(World level, AxisAlignedBB aabb, BiPredicate<BlockPos, BlockState> condition) {
      for(int z = (int)Math.floor(aabb.field_72339_c); z <= (int)Math.floor(aabb.field_72334_f); ++z) {
         for(int y = (int)Math.floor(aabb.field_72338_b); y <= (int)Math.floor(aabb.field_72337_e); ++y) {
            for(int x = (int)Math.floor(aabb.field_72340_a); x <= (int)Math.floor(aabb.field_72336_d); ++x) {
               if (!condition.test(new BlockPos(x, y, z), level.func_180495_p(new BlockPos(x, y, z)))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static VoxelShape addBoxesToVoxelShape(VoxelShape voxelShape, List<AxisAlignedBB> boxes) {
      for(AxisAlignedBB aabb : boxes) {
         voxelShape = VoxelShapes.func_197882_b(voxelShape, VoxelShapes.func_197881_a(aabb), IBooleanFunction.field_223244_o_);
      }

      return voxelShape;
   }

   public static VoxelShape boxesToVoxelShape(List<AxisAlignedBB> boxes) {
      return addBoxesToVoxelShape(VoxelShapes.func_197880_a(), boxes);
   }
}
