package net.portalmod.common.sorted.antline;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public interface AntlineConnector {
   Direction getHorsedOn(BlockState var1);

   boolean antlineConnectsInDirection(Direction var1, BlockState var2);
}
