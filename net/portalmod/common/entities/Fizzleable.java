package net.portalmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.portalmod.client.render.BlockColorHandler;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.init.ArgumentTypeInit;
import net.portalmod.core.init.AttributeInit;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.BlockTagInit;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.init.EntityInit;
import net.portalmod.core.init.EntityTagInit;
import net.portalmod.core.init.FluidInit;
import net.portalmod.core.init.FluidTagInit;
import net.portalmod.core.init.GameRuleInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.init.ItemTagInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.ParticleInit;
import net.portalmod.core.init.RecipeInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.init.StatsInit;
import net.portalmod.core.init.TileEntityTypeInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("portalmod")
public class PortalMod {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final String MODID = "portalmod";
   public static final boolean DEBUG = false;
   public static final boolean WATERMARK = false;

   public PortalMod() {
      IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
      bus.addListener(this::setup);
      EntityInit.ENTITIES.register(bus);
      TileEntityTypeInit.TILE_ENTITY_TYPES.register(bus);
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      FluidI