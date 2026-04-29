package net.portalmod.common.sorted.fizzler;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;

public interface Fizzler {
   boolean isInsideField(AxisAlignedBB var1, BlockPos var2, BlockState var3);

   boolean isActive(BlockState var1);

   VoxelShape getFieldShape(BlockState var1);

   static boolean isActiveFizzler(BlockState state) {
      return state.func_177230_c() instanceof Fizzler && ((Fizzler)state.func_177230_c()).isActive(state);
   }
}
