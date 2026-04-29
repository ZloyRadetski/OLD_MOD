package net.portalmod.mixins.fluid;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.portalmod.core.init.FluidTagInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FlowingFluidBlock.class})
public abstract class FlowingFluidBlockMixin extends Block implements IBucketPickupHandler {
   @Shadow
   protected abstract void func_180688_d(IWorld var1, BlockPos var2);

   @Shadow
   public abstract FluidState func_204507_t(BlockState var1);

   @Shadow
   public abstract FlowingFluid getFluid();

   public FlowingFluidBlockMixin(AbstractBlock.Properties p_i48440_1_) {
      super(p_i48440_1_);
   }

   @Inject(
      method = {"shouldSpreadLiquid(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pmHandleHorizontalGooReactions(World world, BlockPos pos, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
      for(Direction direction : Direction.values()) {
         if (direction != Direction.UP) {
            BlockPos relativePos = pos.func_177972_a(direction);
            if (this.getFluid().func_207185_a(FluidTagInit.GOO) && (this.portalMod$handleFluidInteraction(FluidTags.field_206959_a, Blocks.field_150425_aM, Blocks.field_235336_cN_, world, relativePos) || this.portalMod$handleFluidInteraction(FluidTags.field_206960_b, Blocks.field_150424_aL, Blocks.field_150343_Z, world, relativePos))) {
               cir.setReturnValue(false);
               return;
            }

            if (this.getFluid().func_207185_a(FluidTags.field_206959_a) && this.portalMod$handleFluidInteraction(FluidTagInit.GOO, Blocks.field_150425_aM, Blocks.field_150425_aM, world, relativePos)) {
               cir.setReturnValue(false);
               return;
            }

            if (this.getFluid().func_207185_a(FluidTags.field_206960_b) && this.portalMod$handleFluidInteraction(FluidTagInit.GOO, Blocks.field_150424_aL, Blocks.field_150424_aL, world, relativePos)) {
               cir.setReturnValue(false);
               return;
            }
         }
      }

   }

   @Unique
   public boolean portalMod$handleFluidInteraction(ITag<Fluid> fluid1, Block flowingBlock, Block sourceBlock, World world, BlockPos pos) {
      if (world.func_204610_c(pos).func_206884_a(fluid1)) {
         world.func_175656_a(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, pos, pos, (world.func_204610_c(pos).func_206889_d() ? sourceBlock : flowingBlock).func_176223_P()));
         this.func_180688_d(world, pos);
         return true;
      } else {
         return false;
      }
   }
}
