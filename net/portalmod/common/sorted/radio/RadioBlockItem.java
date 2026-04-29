package net.portalmod.common.sorted.radio;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;

public class RadioBlockItem extends BlockItem {
   public RadioBlockItem(Block block, Item.Properties properties) {
      super(block, properties);
   }

   protected boolean func_195941_b(BlockItemUseContext context, BlockState state) {
      boolean b = super.func_195941_b(context, state);
      if (b && !context.func_195991_k().func_201670_d()) {
         RadioBlockTileEntity radio = (RadioBlockTileEntity)context.func_195991_k().func_175625_s(context.func_195995_a());
         radio.setInitialized();
         radio.sendUpdatePacket();
      }

      return b;
   }
}
