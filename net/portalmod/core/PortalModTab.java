package net.portalmod.core;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.portalmod.core.init.ItemInit;

public class PortalModTab extends ItemGroup {
   public static final ItemGroup INSTANCE = new PortalModTab();

   private PortalModTab() {
      super("portalmod");
   }

   public ItemStack func_78016_d() {
      return new ItemStack((IItemProvider)ItemInit.RADIO.get());
   }
}
