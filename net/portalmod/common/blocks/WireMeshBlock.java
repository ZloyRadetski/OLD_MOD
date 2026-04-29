package net.portalmod.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class WireMeshBlock extends BreakableBlock {
   public WireMeshBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public float func_220080_a(BlockState state, IBlockReader level, BlockPos pos) {
      return 1.0F;
   }

   public boolean func_200123_i(BlockState state, IBlockReader level, BlockPos pos) {
      return true;
   }
}
