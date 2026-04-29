package net.portalmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.core.init.KeyInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MinecraftMixin {
   @Shadow
   protected int field_71429_W;

   @Inject(
      method = {"continueAttack(Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmContinueAttack(CallbackInfo info) {
      if (((Minecraft)this).field_71439_g.func_184614_ca().func_77973_b() instanceof PortalGun) {
         this.field_71429_W = 0;
         info.cancel();
      }

   }

   @Redirect(
      method = {"handleKeybinds"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
   ordinal = 1
)
   )
   private void pmCancelInventoryOpen(Minecraft minecraft, Screen screen) {
      if (minecraft.field_71439_g == null || !(minecraft.field_71439_g.func_184614_ca().func_77973_b() instanceof PortalGun) || KeyInit.PORTALGUN_INTERACT.getKey() != minecraft.field_71474_y.field_151445_Q.getKey()) {
         minecraft.func_147108_a(screen);
      }
   }
}
