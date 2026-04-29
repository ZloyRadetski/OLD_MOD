package net.portalmod.mixins.entity;

import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CatRenderer.class})
public class CatRendererMixin {
   @Inject(
      method = {"getTextureLocation(Lnet/minecraft/entity/passive/CatEntity;)Lnet/minecraft/util/ResourceLocation;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   void getTextureLocation(CatEntity cat, CallbackInfoReturnable<ResourceLocation> info) {
      if (cat.func_145818_k_() && TextFormatting.func_110646_a(cat.func_200200_C_().getString()).equalsIgnoreCase("qubit")) {
         info.setReturnValue(new ResourceLocation("portalmod", "textures/entity/cat/qubit.png"));
      }

   }
}
