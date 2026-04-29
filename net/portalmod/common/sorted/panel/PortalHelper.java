package net.portalmod.common.sorted.panel;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.portalmod.core.math.Vec3;

public interface PortalHelper {
   boolean containsBlock(BlockState var1, BlockPos var2, BlockPos var3, World var4);

   boolean willHelpPortal(Direction var1, Direction var2, BlockState var3, World var4);

   Pair<Vec3, Direction> helpPortal(Vec3 var1, Direction var2, Direction var3, Direction[] var4, BlockState var5, World var6);
}
