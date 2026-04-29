package net.portalmod.common.sorted.portalgun;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class PortalGunItemColor implements IItemColor {
   private static final int BLUE = 3902463;
   private static final int ORANGE = 16745275;

   public int getColor(ItemStack itemStack, int layer) {
      if (layer == 0 && itemStack.func_77942_o()) {
         CompoundNBT nbt = itemStack.func_77978_p();
         if (!nbt.func_74764_b("color")) {
            return -1;
         } else {
            byte color = nbt.func_74771_c("color");
            if (color == 1) {
               return 3902463;
            } else {
               return color == 2 ? 16745275 : -1;
            }
         }
      } else {
         return -1;
      }
   }
}
