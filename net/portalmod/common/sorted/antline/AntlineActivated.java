package net.portalmod.common.sorted.antline;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface AntlineActivated extends AntlineConnector {
   void onAntlineActivation(boolean var1, BlockState var2, World var3, BlockPos var4);

   default boolean ignoreAntlineActivationFromBlock(BlockState state) {
      return false;
   }

   default void updateAntlineActivation(BlockState state, World world, BlockPos pos) {
      Direction horsedOn = this.getHorsedOn(state);

      for(Direction direction : Direction.values()) {
         if (this.antlineConnectsInDirection(direction, state)) {
            BlockPos neighborPos = pos.func_177972_a(direction);
            BlockState neighborState = world.func_180495_p(neighborPos);
            Block neighborBlock = neighborState.func_177230_c();
            if (neighborBlock instanceof AntlineActivator && ((AntlineActivator)neighborBlock).antlineConnectsInDirection(direction.func_176734_d(), neighborState) && ((AntlineActivator)neighborBlock).getHorsedOn(neighborState) == horsedOn && ((AntlineActivator)neighborBlock).isAntlineActive(neighborState) && !this.ignoreAntlineActivationFromBlock(neighborState)) {
               this.onAntlineActivation(true, state, world, pos);
               return;
            }

            if (neighborBlock instanceof AntlineBlock && !this.ignoreAntlineActivationFromBlock(neighborState)) {
               AntlineTileEntity tileEntity = (AntlineTileEntity)world.func_175625_s(neighborPos);
               if (tileEntity != null) {
                  AntlineTileEntity.Side side = (AntlineTileEntity.Side)tileEntity.getSideMap().get(horsedOn);
                  if (side.hasConnection(direction.func_176734_d()) && side.isActive()) {
                     this.onAntlineActivation(true, state, world, pos);
                     return;
                  }
               }
            }
         }
      }

      this.onAntlineActivation(false, state, world, pos);
   }
}
