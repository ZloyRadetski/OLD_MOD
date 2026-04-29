package net.portalmod.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.portalmod.core.init.BlockTagInit;
import net.portalmod.core.init.GameRuleInit;

public interface PortalableBlock {
   boolean isPortalableOnFace(BlockState var1, Direction var2);

   static boolean isPortalable(BlockState state, Direction face, World world) {
      if (world.func_82736_K().func_223586_b(GameRuleInit.USE_PORTALABLE_BLACKLIST)) {
         return !state.func_235714_a_(BlockTagInit.UNPORTALABLE);
      } else {
         boolean inTag = state.func_235714_a_(BlockTagInit.PORTALABLE);
         if (!(state.func_177230_c() instanceof PortalableBlock)) {
            return inTag;
         } else {
            return inTag && ((PortalableBlock)state.func_177230_c()).isPortalableOnFace(state, face);
         }
      }
   }
}
