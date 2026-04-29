package net.portalmod.core.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(
   modid = "portalmod",
   bus = Bus.MOD
)
public class DataGenInit {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      ExistingFileHelper helper = event.getExistingFileHelper();
      generator.func_200390_a(new BlockStateGen(generator, helper));
      generator.func_200390_a(new RecipeGen(generator));
      generator.func_200390_a(new LootTableGen(generator));
   }
}
