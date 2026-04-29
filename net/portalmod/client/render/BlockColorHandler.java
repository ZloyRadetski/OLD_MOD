package net.portalmod.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.BlockItem;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.portalmod.core.init.BlockInit;

public class BlockColorHandler {
   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public void registerBlockColorHandlers(ColorHandlerEvent.Block event) {
      IBlockColor blockColorHandler = (state, blockAccess, pos, tintIndex) -> blockAccess != null && pos != null ? BiomeColors.func_228361_b_(blockAccess, pos) : GrassColors.func_77480_a((double)0.5F, (double)1.0F);
      event.getBlockColors().func_186722_a(blockColorHandler, new Block[]{(Block)BlockInit.ARBORED_BLACKPLATE.get(), (Block)BlockInit.ARBORED_BLACKPLATE_SLAB.get(), (Block)BlockInit.ARBORED_BLACKPLATE_STAIRS.get(), (Block)BlockInit.ARBORED_BLACKPLATE_PLATFORM.get()});
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public void registerItemColorHandlers(ColorHandlerEvent.Item event) {
      IItemColor blockItemColorHandler = (stack, tintIndex) -> {
         BlockState blockState = ((BlockItem)stack.func_77973_b()).func_179223_d().func_176223_P();
         return event.getBlockColors().func_228054_a_(blockState, (IBlockDisplayReader)null, (BlockPos)null, tintIndex);
      };
      event.getItemColors().func_199877_a(blockItemColorHandler, new IItemProvider[]{(IItemProvider)BlockInit.ARBORED_BLACKPLATE.get(), (IItemProvider)BlockInit.ARBORED_BLACKPLATE_SLAB.get(), (IItemProvider)BlockInit.ARBORED_BLACKPLATE_STAIRS.get(), (IItemProvider)BlockInit.ARBORED_BLACKPLATE_PLATFORM.get()});
   }
}
