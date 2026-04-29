package net.portalmod.mixins.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityRenderer.class})
public abstract class EntityRendererMixin {
   @Shadow
   protected abstract int func_225624_a_(Entity var1, BlockPos var2);

   @Shadow
   protected abstract int func_239381_b_(Entity var1, BlockPos var2);

   @Inject(
      method = {"getPackedLightCoords(Lnet/minecraft/entity/Entity;F)I"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void pmDeceiveLightEngine(Entity entity, float partialTicks, CallbackInfoReturnable<Integer> info) {
      Minecraft mc = Minecraft.func_71410_x();
      if (entity == mc.field_175622_Z) {
         BlockPos pos = mc.field_71460_t.func_215316_n().func_216780_d();
         info.setReturnValue(LightTexture.func_228451_a_(this.func_225624_a_(entity, pos), this.func_239381_b_(entity, pos)));
      }
   }
}
