package net.portalmod.core.event;

import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.turret.TurretEntity;
import net.portalmod.core.init.EntityInit;

@EventBusSubscriber(
   modid = "portalmod",
   bus = Bus.MOD,
   value = {Dist.DEDICATED_SERVER}
)
public class ServerModEvents {
   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)EntityInit.COMPANION_CUBE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.STORAGE_CUBE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.VINTAGE_CUBE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.GABE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.TURRET.get(), TurretEntity.createAttributes().func_233813_a_());
   }
}
