package net.portalmod.mixins;

import net.minecraft.client.settings.KeyBinding;
import net.portalmod.core.init.KeyInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyBinding.class})
public abstract class KeyBindingMixin {
   @Shadow
   public abstract boolean func_197985_l();

   @Inject(
      method = {"same"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pmMakeEKeyNotConflict(KeyBinding keyBinding, CallbackInfoReturnable<Boolean> cir) {
      KeyBinding self = (KeyBinding)this;
      if (self == KeyInit.PORTALGUN_INTERACT && self.func_197985_l() || keyBinding == KeyInit.PORTALGUN_INTERACT && keyBinding.func_197985_l()) {
         cir.setReturnValue(false);
      }

   }
}
