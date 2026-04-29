package net.portalmod.mixins.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.portalmod.common.sorted.faithplate.Flingable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyBinding.class})
public class KeyBindingMixin {
   @Inject(
      method = {"isDown"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsDown(CallbackInfoReturnable<Boolean> cir) {
      PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (player instanceof Flingable) {
         if (((Flingable)player).isFlinging()) {
            KeyBinding self = (KeyBinding)this;
            boolean cancel = false;
            if (self.equals(Minecraft.func_71410_x().field_71474_y.field_74370_x)) {
               cancel = true;
            }

            if (self.equals(Minecraft.func_71410_x().field_71474_y.field_74366_z)) {
               cancel = true;
            }

            if (self.equals(Minecraft.func_71410_x().field_71474_y.field_74351_w)) {
               cancel = true;
            }

            if (self.equals(Minecraft.func_71410_x().field_71474_y.field_74368_y)) {
               cancel = true;
            }

            if (cancel) {
               cir.setReturnValue(false);
            }

         }
      }
   }
}
