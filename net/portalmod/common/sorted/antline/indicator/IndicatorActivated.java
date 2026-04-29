package net.portalmod.common.sorted.antline.indicator;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IndicatorActivated {
   List<BlockPos> getIndicatorPositions(BlockState var1, World var2, BlockPos var3);

   default IndicatorInfo checkIndicators(BlockState blockState, World world, BlockPos pos) {
      return checkPositions(world, this.getIndicatorPositions(blockState, world, pos));
   }

   static IndicatorInfo checkPositions(World world, List<BlockPos> positions) {
      int totalIndicators = 0;
      int activeIndicators = 0;

      for(BlockPos indicatorPos : positions) {
         BlockState currentState = world.func_180495_p(indicatorPos);
         Block block = currentState.func_177230_c();
         if (block instanceof TestElementActivator) {
            ++totalIndicators;
            if (((TestElementActivator)block).isActive(currentState)) {
               ++activeIndicators;
            }
         }
      }

      return new IndicatorInfo(totalIndicators, activeIndicators);
   }
}
