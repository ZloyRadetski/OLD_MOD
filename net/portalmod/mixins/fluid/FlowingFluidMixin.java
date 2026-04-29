package net.portalmod.mixins.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.portalmod.core.init.FluidTagInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({FlowingFluid.class})
public abstract class FlowingFluidMixin {
   @Shadow
   public abstract Fluid func_210198_f();

   @Shadow
   public abstract FluidState func_207207_a(int var1, boolean var2);

   @Shadow
   protected abstract int func_204528_b(IWorldReader var1);

   @Inject(
      method = {"getNewLiquid(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/fluid/FluidState;"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/fluid/FlowingFluid;getDropOff(Lnet/minecraft/world/IWorldReader;)I"
)},
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   public void pmSpread5Blocks(IWorldReader p_205576_1_, BlockPos p_205576_2_, BlockState p_205576_3_, CallbackInfoReturnable<FluidState> cir, int i) {
      int k = i - this.func_204528_b(p_205576_1_);
      if (this.func_210198_f().func_207185_a(FluidTagInit.GOO)) {
         if (i != 4 && i != 2) {
            k = i - 1;
         } else {
            k = i - 2;
         }
      }

      cir.setReturnValue(k <= 0 ? Fluids.field_204541_a.func_207188_f() : this.func_207207_a(k, false));
   }
}
