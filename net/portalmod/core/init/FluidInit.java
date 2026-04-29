package net.portalmod.core.init;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidInit {
   public static final DeferredRegister<Fluid> FLUIDS;
   public static final RegistryObject<FlowingFluid> GOO_FLUID;
   public static final RegistryObject<FlowingFluid> GOO_FLOWING;
   public static final ForgeFlowingFluid.Properties GOO_PROPERTIES;
   public static DamageSource GOO_DAMAGE;

   private FluidInit() {
   }

   static {
      FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, "portalmod");
      GOO_FLUID = FLUIDS.register("goo_fluid", () -> new ForgeFlowingFluid.Source(GOO_PROPERTIES));
      GOO_FLOWING = FLUIDS.register("goo_flowing", () -> new ForgeFlowingFluid.Flowing(GOO_PROPERTIES));
      GOO_PROPERTIES = (new ForgeFlowingFluid.Properties(GOO_FLUID, GOO_FLOWING, FluidAttributes.builder(new ResourceLocation("portalmod", "block/goo_still"), new ResourceLocation("portalmod", "block/goo_flow")).translationKey("block.portalmod.goo").sound(new SoundEvent(new ResourceLocation("portalmod", "item.bucket.fill_goo")), new SoundEvent(new ResourceLocation("portalmod", "item.bucket.empty_goo"))))).tickRate(20).levelDecreasePerBlock(2).block(BlockInit.GOO).canMultiply().bucket(ItemInit.GOO_BUCKET);
      GOO_DAMAGE = new DamageSource("goo");
   }
}
