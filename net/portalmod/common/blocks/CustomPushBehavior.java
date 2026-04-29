package net.portalmod.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CustomPushBehavior {
   boolean isStickyToNeighbor(World var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5, Direction var6, Direction var7);
}
