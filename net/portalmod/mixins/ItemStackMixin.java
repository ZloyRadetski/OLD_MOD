package net.portalmod.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.portalmod.core.init.ItemInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public class ItemStackMixin {
   @Inject(
      method = {"tagMatches"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void pmPortalGunMatches(ItemStack item, ItemStack otherItem, CallbackInfoReturnable<Boolean> info) {
      if (item.func_77973_b() == ItemInit.PORTALGUN.get() && otherItem.func_77973_b() == ItemInit.PORTALGUN.get() && item.func_77942_o() && otherItem.func_77942_o()) {
         CompoundNBT nbt1 = item.func_77978_p();
         CompoundNBT nbt2 = otherItem.func_77978_p();
         if (nbt1 != null && nbt2 != null && nbt1.func_74764_b("gunUUID") && nbt2.func_74764_b("gunUUID") && nbt1.func_186857_a("gunUUID").equals(nbt2.func_186857_a("gunUUID"))) {
            info.setReturnValue(true);
         }
      }

   }
}
