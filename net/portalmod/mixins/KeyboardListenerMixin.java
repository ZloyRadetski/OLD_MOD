package net.portalmod.mixins;

import java.io.IOException;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.portalmod.client.render.Shader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyboardListener.class})
public abstract class KeyboardListenerMixin {
   @Shadow
   protected abstract void func_197964_a(String var1, Object... var2);

   @Inject(
      at = {@At("RETURN")},
      slice = {@Slice(
   from = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/WorldRenderer;allChanged()V"
)
)},
      method = {"handleDebugKeys(I)Z"},
      cancellable = true
   )
   private void pmHandleDebugKeys(int key, CallbackInfoReturnable<Boolean> info) {
      if (!(Boolean)info.getReturnValue() && key == 90) {
         boolean error = false;

         try {
            Shader.reloadAll();
         } catch (IOException e) {
            error = true;
            e.printStackTrace();
            this.func_197964_a("debug.portalmod.reload_shaders.error");
         }

         if (!error) {
            this.func_197964_a("debug.portalmod.reload_shaders.message");
         }

         info.setReturnValue(true);
      } else if (key == 81) {
         Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a(new TranslationTextComponent("debug.portalmod.reload_shaders.help"));
      }

   }
}
