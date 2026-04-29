package net.portalmod.common.sorted.goo;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.portalmod.core.util.ModUtil;

public class GooBucketItem extends BucketItem {
   public GooBucketItem(Supplier<? extends Fluid> supplier, Item.Properties builder) {
      super(supplier, builder);
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("goo_bucket", list);
   }
}
