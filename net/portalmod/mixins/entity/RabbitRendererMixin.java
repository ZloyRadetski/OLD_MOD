package net.portalmod.mixins.entity;

import java.util.Objects;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RabbitRenderer.class})
public class RabbitRendererMixin {
   @Inject(
      method = {"getTextureLocation(Lnet/minecraft/entity/passive/RabbitEntity;)Lnet/minecraft/util/ResourceLocation;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   void getTextureLocation(RabbitEntity rabbit, CallbackInfoReturnable<ResourceLocation> cir) {
      String name = TextFormatting.func_110646_a(rabbit.func_200200_C_().getString());
      if (rabbit.func_145818_k_() && (Objects.equals(name, "bun") || Objects.equals(name, "niko"))) {
         cir.setReturnValue(new ResourceLocation("portalmod", "textures/entity/rabbit/bun.png"));
      }

   }
}
