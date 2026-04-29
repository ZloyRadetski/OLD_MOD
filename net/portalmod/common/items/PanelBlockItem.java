package net.portalmod.common.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.portalmod.core.util.ModUtil;

public class PanelBlockItem extends BlockItem {
   public PanelBlockItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
      super(p_i48527_1_, p_i48527_2_);
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("panel", list);
   }
}
