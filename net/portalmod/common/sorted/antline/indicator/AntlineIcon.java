package net.portalmod.common.sorted.antline.indicator;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.portalmod.common.items.WrenchItem;

public abstract class AntlineIcon extends AntlineDevice {
   public static final int ICON_COUNT = 8;
   public static final IntegerProperty ICON = IntegerProperty.func_177719_a("icon", 0, 8);

   public AntlineIcon(AbstractBlock.Properties properties) {
      super(properties);
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
      if (!WrenchItem.usedWrench(player, hand)) {
         return ActionResultType.PASS;
      } else {
         BlockState newState = (BlockState)state.func_235896_a_(ICON);
         if (player.func_225608_bj_()) {
            for(int i = 0; i < 7; ++i) {
               newState = (BlockState)newState.func_235896_a_(ICON);
            }
         }

         world.func_175656_a(pos, newState);
         WrenchItem.playUseSound(world, result.func_216347_e());
         return ActionResultType.SUCCESS;
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{ICON});
   }
}
