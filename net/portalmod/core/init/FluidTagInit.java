package net.portalmod.core.init;

import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class FluidTagInit {
   public static final Tags.IOptionalNamedTag<Fluid> GOO = FluidTags.createOptional(new ResourceLocation("portalmod", "goo"));

   private FluidTagInit() {
   }

   public static void init() {
   }
}
