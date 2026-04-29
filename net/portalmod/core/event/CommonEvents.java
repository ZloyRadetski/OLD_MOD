package net.portalmod.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.portalmod.common.commands.PortalCommand;
import net.portalmod.common.entities.Fizzleable;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.items.ModSpawnEggItem;
import net.portalmod.common.sorted.faithplate.CFaithPlateEndConfigPacket;
import net.portalmod.common.sorted.faithplate.FaithPlateTER;
import net.portalmod.common.sorted.faithplate.FaithPlateTileEntity;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.common.sorted.fizzler.Fizzler;
import net.portalmod.common.sorted.portal.ClientPortalManager;
import net.portalmod.common.sorted.portal.PartialPortalPair;
import net.portalmod.common.sorted.portal.PortalManager;
import net.portalmod.common.sorted.portal.SPortalPairPacket;
import net.portalmod.common.sorted.portal.VolatilePortalHelperManager;
import net.portalmod.common.sorted.portalgun.PortalHelperServerManager;
import net.portalmod.common.sorted.portalgun.skins.ServerSkinManager;
import net.portalmod.common.sorted.trigger.TriggerSelectionClient;
import net.portalmod.common.sorted.trigger.TriggerSelectionServer;
import net.portalmod.core.init.GameRuleInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.injectors.LivingEntityInjector;
import net.portalmod.core.util.ModUtil;

@EventBusSubscriber(
   modid = "portalmod",
   bus = Bus.FORGE
)
public class CommonEvents {
   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event) {
      PortalCommand.register(event.getDispatcher());
   }

   @SubscribeEvent
   public static void onLevelLoad(WorldEvent.Load event) {
      if (event.getWorld().func_201670_d()) {
         ClientPortalManager.getInstance().clear();
      } else if (event.getWorld() == ServerLifecycleHooks.getCurrentServer().func_71218_a(World.field_234918_g_)) {
         PortalManager.getInstance().clear();
         ((ServerWorld)event.getWorld()).func_217481_x().func_215753_b(PortalManager::getInstance, "portalmod_portals");
      }
   }

   @SubscribeEvent
   public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
      LivingEntityInjector.onPreTick(event.getEntityLiving());
   }

   @SubscribeEvent
   public static void onLivingFall(LivingFallEvent event) {
      if (event.getEntityLiving().func_184582_a(EquipmentSlotType.FEET).func_77973_b() == ItemInit.LONGFALL_BOOTS.get()) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      PlayerEntity player = event.player;
      if (event.phase == Phase.START && player.field_71075_bZ.field_75100_b) {
         ((Flingable)player).setFlinging(false);
      }

      if (event.phase == Phase.END && player.func_175144_cb() && player.field_71071_by.func_70448_g().func_77973_b() != ItemInit.WRENCH.get()) {
         if (FaithPlateTER.selected != null) {
            PacketInit.INSTANCE.sendToServer(new CFaithPlateEndConfigPacket(FaithPlateTER.selected));
            FaithPlateTER.selected = null;
         }

         if (TriggerSelectionClient.isSelecting()) {
            TriggerSelectionClient.abort();
         }
      }

   }

   @SubscribeEvent
   public static void onPlayerPickUpItem(PlayerEvent.ItemPickupEvent event) {
      ItemEntity originalItemEntity = event.getOriginalEntity();
      PlayerEntity player = event.getPlayer();
      World level = player.field_70170_p;
      if (Fizzleable.isFizzleableItem(event.getStack())) {
         RayTraceContext context = new RayTraceContext(player.func_174824_e(1.0F), originalItemEntity.func_213303_ch(), BlockMode.COLLIDER, FluidMode.ANY, player);
         BlockRayTraceResult result = ModUtil.customClip(level, context, (posx) -> {
            BlockState state = level.func_180495_p(posx);
            Block block = state.func_177230_c();
            return Fizzler.isActiveFizzler(state) ? Optional.of(((Fizzler)block).getFieldShape(state)) : Optional.empty();
         });
         if (result.func_216346_c() == Type.BLOCK) {
            BlockPos pos = result.func_216350_a();
            BlockState state = level.func_180495_p(pos);
            if (Fizzler.isActiveFizzler(state)) {
               ((Fizzleable)player).onTouchingFizzler();
            }
         }
      }

   }

   @SubscribeEvent
   public static void onLivingDrops(LivingDropsEvent event) {
      LivingEntity entity = event.getEntityLiving();
      if (entity instanceof TestElementEntity) {
         for(ItemEntity itemEntity : event.getDrops()) {
            ItemStack itemStack = itemEntity.func_92059_d();
            if (itemStack.func_77973_b() instanceof ModSpawnEggItem && entity.func_145818_k_()) {
               itemStack.func_200302_a(entity.func_200201_e());
            }
         }
      }

   }

   @SubscribeEvent
   public static void onServerTick(TickEvent.ServerTickEvent event) {
      if (event.phase == Phase.START) {
         ServerSkinManager.getInstance().tick();
         PortalManager.getInstance().tick();
         VolatilePortalHelperManager.getInstance().clearVolatilePortalHelpers();
         PortalHelperServerManager.getInstance().tick();
      }

   }

   @SubscribeEvent
   public static void onServerLogin(PlayerEvent.PlayerLoggedInEvent event) {
      ServerSkinManager.getInstance().onServerLogin((ServerPlayerEntity)event.getPlayer());
   }

   @SubscribeEvent
   public static void onPlayerJoin(EntityJoinWorldEvent event) {
      if (!event.getWorld().func_201670_d() && event.getWorld().func_73046_m() != null) {
         if (event.getEntity() instanceof PlayerEntity) {
            PortalManager.getInstance().getPortalMap().forEach((k, v) -> PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getEntity()), new SPortalPairPacket(k, new PartialPortalPair(v))));
            GameRuleInit.sendBooleanRule((ServerPlayerEntity)event.getEntity(), GameRuleInit.PORTAL_SLOWSHOT);
            GameRuleInit.sendBooleanRule((ServerPlayerEntity)event.getEntity(), GameRuleInit.USE_PORTALABLE_BLACKLIST);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLeave(EntityLeaveWorldEvent event) {
      if (event.getEntity() instanceof ServerPlayerEntity) {
         FaithPlateTileEntity.endConfigurationForPlayer((PlayerEntity)event.getEntity());
         TriggerSelectionServer.endConfiguration((ServerPlayerEntity)event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onChunkLoad(ChunkEvent.Load event) {
      if (!event.getWorld().func_201670_d()) {
         ((List)((HashMap)PortalManager.getInstance().getPortalsPerChunk().getOrDefault(((ServerWorld)event.getWorld()).func_234923_W_(), new HashMap())).getOrDefault(event.getChunk().func_76632_l(), new ArrayList())).forEach((portal) -> {
            portal.field_70128_L = false;
            event.getWorld().func_217376_c(portal);
         });
      }
   }

   @SubscribeEvent
   public static void onChunkUnload(ChunkEvent.Unload event) {
      if (!event.getWorld().func_201670_d() && event.getWorld() instanceof ServerWorld) {
         ((List)((HashMap)PortalManager.getInstance().getPortalsPerChunk().getOrDefault(((ServerWorld)event.getWorld()).func_234923_W_(), new HashMap())).getOrDefault(event.getChunk().func_76632_l(), new ArrayList())).forEach(Entity::func_70106_y);
      }
   }
}
