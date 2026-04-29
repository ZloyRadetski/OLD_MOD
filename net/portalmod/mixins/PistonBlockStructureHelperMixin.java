package net.portalmod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlockStructureHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.portalmod.common.blocks.CustomPushBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({PistonBlockStructureHelper.class})
public class PistonBlockStructureHelperMixin {
   @Shadow
   @Final
   private World field_177261_a;
   @Shadow
   @Final
   private Direction field_177257_d;
   @Unique
   private BlockPos pos_addBlockLine;
   @Unique
   private BlockPos behindPos_addBlockLinea;
   @Unique
   private Direction dir_addBranchingBlocks;
   @Unique
   private BlockPos neighborPos_addBranchingBlocks;

   @Inject(
      method = {"addBlockLine"},
      locals = LocalCapture.CAPTURE_FAILHARD,
      at = {@At(
   value = "INVOKE",
   ordinal = 1,
   target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
)}
   )
   private void captureBlockLinePositions(BlockPos p_177251_1_, Direction p_177251_2_, CallbackInfoReturnable<Boolean> cir, BlockState blockstate, int i, BlockState oldState, BlockPos blockpos) {
      this.pos_addBlockLine = blockpos.func_177972_a(this.field_177257_d);
      this.behindPos_addBlockLinea = blockpos;
   }

   @Redirect(
      method = {"addBlockLine"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;canStickTo(Lnet/minecraft/block/BlockState;)Z"
)
   )
   private boolean onAddBlockLineCanStickToEachOther(BlockState state, BlockState behindState) {
      Block block = state.func_177230_c();
      return block instanceof CustomPushBehavior ? ((CustomPushBehavior)block).isStickyToNeighbor(this.field_177261_a, this.pos_addBlockLine, state, this.behindPos_addBlockLinea, behindState, this.field_177257_d.func_176734_d(), this.field_177257_d) : state.canStickTo(behindState);
   }

   @Inject(
      method = {"addBranchingBlocks"},
      locals = LocalCapture.CAPTURE_FAILHARD,
      at = {@At(
   value = "INVOKE",
   ordinal = 1,
   target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
)}
   )
   private void captureNeighborPositions(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, Direction[] dirs, int i, int j, Direction dir, BlockPos neighborPos) {
      this.dir_addBranchingBlocks = dir;
      this.neighborPos_addBranchingBlocks = neighborPos;
   }

   @Redirect(
      method = {"addBranchingBlocks"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;canStickTo(Lnet/minecraft/block/BlockState;)Z"
)
   )
   private boolean onAddBranchingBlocksCanStickToEachOther(BlockState neighborState, BlockState state, BlockPos pos) {
      Block block = state.func_177230_c();
      return block instanceof CustomPushBehavior ? ((CustomPushBehavior)block).isStickyToNeighbor(this.field_177261_a, pos, state, this.neighborPos_addBranchingBlocks, neighborState, this.dir_addBranchingBlocks, this.field_177257_d) : neighborState.canStickTo(state);
   }
}
