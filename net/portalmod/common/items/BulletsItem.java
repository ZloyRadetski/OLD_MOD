package net.portalmod.common.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.portalmod.core.util.ModUtil;

public class BulletsItem extends Item {
   public BulletsItem(Item.Properties properties) {
      super(properties);
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World p_77624_2_, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("bullets", list);
   }
}
