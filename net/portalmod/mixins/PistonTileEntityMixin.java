package net.portalmod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.PistonTileEntity;
import net.portalmod.common.sorted.platform.PlatformBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PistonTileEntity.class})
public class PistonTileEntityMixin {
   @Shadow
   private BlockState field_200231_a;

   @Inject(
      method = {"isStickyForEntities"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void makePlatformSticky(CallbackInfoReturnable<Boolean> cir) {
      if (this.field_200231_a.func_177230_c() instanceof PlatformBlock) {
         cir.setReturnValue(true);
      }

   }
}
