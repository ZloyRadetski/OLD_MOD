package net.portalmod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.portalmod.common.sorted.panel.PanelBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public class BlockMixin {
   @Inject(
      method = {"canSustainPlant"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void allowArboredPlains(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable, CallbackInfoReturnable<Boolean> cir) {
      if (plantable.getPlantType(world, pos.func_177972_a(facing)) == PlantType.PLAINS) {
         ResourceLocation registryName = state.func_177230_c().getRegistryName();
         if (registryName != null && state.func_177230_c() instanceof PanelBlock && registryName.func_110623_a().startsWith("arbored")) {
            cir.setReturnValue(state.func_224755_d(world, pos.func_177977_b(), Direction.UP));
         }
      }

   }
}
