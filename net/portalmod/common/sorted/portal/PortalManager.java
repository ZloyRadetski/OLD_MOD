package net.portalmod.common.sorted.portal;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.math.Vec3;

public class PortalManager extends WorldSavedData {
   private static PortalManager instance;
   public static final String PATH = "portalmod_portals";
   private final Map<UUID, PortalPair> portalMap = new HashMap();
   private final Map<Pair<UUID, PortalEnd>, PortalPlacementInfo> scheduledPlacements = new HashMap();
   private final HashMap<RegistryKey<World>, HashMap<ChunkPos, List<PortalEntity>>> portalsPerChunk = new HashMap();
   private final Deque<PortalEntity> pendingRemovals = new ArrayDeque();
   public boolean unloadingChunk = false;

   private PortalManager() {
      super("portalmod_portals");
   }

   public static PortalManager getInstance() {
      if (instance == null) {
         instance = new PortalManager();
      }

      return instance;
   }

   public void tick() {
      for(PortalEntity portal : this.pendingRemovals) {
         ((ServerWorld)portal.field_70170_p).removeEntity(portal, false);
      }

      this.pendingRemovals.clear();
      List<Pair<UUID, PortalEnd>> completedPlacements = new ArrayList();
      this.scheduledPlacements.forEach((key, info) -> {
         if (--info.tickCountdown <= 0L) {
            PortalEntity portal = PortalPlacer.placePortal(info.level, info.end, info.hue, info.gunUUID, info.position, info.face, info.upDirection, info.override, info.overwriteForeignPortals, info.lookingDirections, info.player);
            info.onPlace.accept(portal);
            completedPlacements.add(key);
         }

      });
      Map var10001 = this.scheduledPlacements;
      completedPlacements.forEach(var10001::remove);
   }

   public void scheduleRemoval(PortalEntity portal) {
      this.pendingRemovals.add(portal);
   }

   public void schedulePlacement(World level, PortalEnd end, String hue, UUID gunUUID, Vec3 position, Direction face, Direction upDirection, boolean override, @Nullable Direction[] lookingDirections, @Nullable ServerPlayerEntity player, long tickCountdown, Consumer<PortalEntity> onPlace) {
      this.schedulePlacement(level, end, hue, gunUUID, position, face, upDirection, override, false, lookingDirections, player, tickCountdown, onPlace);
   }

   public void schedulePlacement(World level, PortalEnd end, String hue, UUID gunUUID, Vec3 position, Direction face, Direction upDirection, boolean override, boolean overwriteForeignPortals, @Nullable Direction[] lookingDirections, @Nullable ServerPlayerEntity player, long tickCountdown, Consumer<PortalEntity> onPlace) {
      Pair<UUID, PortalEnd> key = new Pair(gunUUID, end);
      this.scheduledPlacements.put(key, new PortalPlacementInfo(level, end, hue, gunUUID, position, face, upDirection, override, overwriteForeignPortals, lookingDirections, player, tickCountdown, onPlace));
   }

   public void clearScheduledPlacements(UUID gunUUID) {
      List<Pair<UUID, PortalEnd>> removedScheduledPlacements = (List)this.scheduledPlacements.keySet().stream().filter((key) -> ((UUID)key.getFirst()).equals(gunUUID)).collect(Collectors.toList());
      Map var10001 = this.scheduledPlacements;
      removedScheduledPlacements.forEach(var10001::remove);
   }

   public void func_76184_a(CompoundNBT nbt) {
      for(String key : nbt.func_150296_c()) {
         CompoundNBT pair = nbt.func_74775_l(key);
         PortalPair portalPair = new PortalPair();
         if (pair.func_74764_b("primary")) {
            CompoundNBT primary = pair.func_74775_l("primary");
            ResourceLocation rl = new ResourceLocation(primary.func_74779_i("level"));
            RegistryKey<World> rk = RegistryKey.func_240903_a_(Registry.field_239699_ae_, rl);
            World level = ServerLifecycleHooks.getCurrentServer().func_71218_a(rk);
            PortalEntity blue = new PortalEntity(level);
            blue.func_70020_e(primary);
            portalPair.set(PortalEnd.PRIMARY, blue);
            ChunkPos chunkPos = new ChunkPos(MathHelper.func_76128_c(blue.func_226277_ct_()) >> 4, MathHelper.func_76128_c(blue.func_226281_cx_()) >> 4);
            HashMap<ChunkPos, List<PortalEntity>> chunks = (HashMap)this.portalsPerChunk.getOrDefault(rk, new HashMap());
            List<PortalEntity> portals = (List)chunks.getOrDefault(chunkPos, new ArrayList());
            portals.add(blue);
            chunks.put(chunkPos, portals);
            this.portalsPerChunk.put(rk, chunks);
         }

         if (pair.func_74764_b("secondary")) {
            CompoundNBT secondary = pair.func_74775_l("secondary");
            ResourceLocation rl = new ResourceLocation(secondary.func_74779_i("level"));
            RegistryKey<World> rk = RegistryKey.func_240903_a_(Registry.field_239699_ae_, rl);
            World level = ServerLifecycleHooks.getCurrentServer().func_71218_a(rk);
            PortalEntity orange = new PortalEntity(level);
            orange.func_70020_e(secondary);
            portalPair.set(PortalEnd.SECONDARY, orange);
            ChunkPos chunkPos = new ChunkPos(MathHelper.func_76128_c(orange.func_226277_ct_()) >> 4, MathHelper.func_76128_c(orange.func_226281_cx_()) >> 4);
            HashMap<ChunkPos, List<PortalEntity>> chunks = (HashMap)this.portalsPerChunk.getOrDefault(rk, new HashMap());
            List<PortalEntity> portals = (List)chunks.getOrDefault(chunkPos, new ArrayList());
            portals.add(orange);
            chunks.put(chunkPos, portals);
            this.portalsPerChunk.put(rk, chunks);
         }

         this.portalMap.put(UUID.fromString(key), portalPair);
      }

   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      this.portalMap.forEach((uuid, pair) -> {
         CompoundNBT pairNbt = new CompoundNBT();
         if (pair.has(PortalEnd.PRIMARY)) {
            CompoundNBT blueNbt = new CompoundNBT();
            pair.get(PortalEnd.PRIMARY).saveGlobal(blueNbt);
            blueNbt.func_74778_a("level", pair.get(PortalEnd.PRIMARY).field_70170_p.func_234923_W_().func_240901_a_().toString());
            pairNbt.func_218657_a("primary", blueNbt);
         }

         if (pair.has(PortalEnd.SECONDARY)) {
            CompoundNBT orangeNbt = new CompoundNBT();
            pair.get(PortalEnd.SECONDARY).saveGlobal(orangeNbt);
            orangeNbt.func_74778_a("level", pair.get(PortalEnd.SECONDARY).field_70170_p.func_234923_W_().func_240901_a_().toString());
            pairNbt.func_218657_a("secondary", orangeNbt);
         }

         nbt.func_218657_a(uuid.toString(), pairNbt);
      });
      return nbt;
   }

   public void clear() {
      this.portalMap.clear();
      this.portalsPerChunk.clear();
   }

   private static World getOverworld() {
      return ServerLifecycleHooks.getCurrentServer().func_71218_a(World.field_234918_g_);
   }

   private static void getStorageAndSetDirty() {
      ((PortalManager)((ServerWorld)getOverworld()).func_217481_x().func_215752_a(PortalManager::getInstance, "portalmod_portals")).func_76185_a();
   }

   public HashMap<RegistryKey<World>, HashMap<ChunkPos, List<PortalEntity>>> getPortalsPerChunk() {
      return this.portalsPerChunk;
   }

   public void put(UUID gunUUID, PortalEnd end, PortalEntity portal) {
      PortalPair pair = (PortalPair)this.portalMap.getOrDefault(gunUUID, new PortalPair());
      if (!pair.areInSameDimension(PortalEnd.PRIMARY, portal)) {
         PortalEntity primary = pair.get(PortalEnd.PRIMARY);
         ((ServerWorld)primary.field_70170_p).removeEntity(primary, false);
      }

      if (!pair.areInSameDimension(PortalEnd.SECONDARY, portal)) {
         PortalEntity secondary = pair.get(PortalEnd.SECONDARY);
         ((ServerWorld)secondary.field_70170_p).removeEntity(secondary, false);
      }

      pair.computeIfPresent(end, PortalEntity::onReplaced);
      pair.set(end, portal);
      portal.field_70175_ag = true;
      this.portalMap.put(gunUUID, pair);
      ChunkPos chunkPos = new ChunkPos(MathHelper.func_76128_c(portal.func_226277_ct_()) >> 4, MathHelper.func_76128_c(portal.func_226281_cx_()) >> 4);
      HashMap<ChunkPos, List<PortalEntity>> chunks = (HashMap)this.portalsPerChunk.getOrDefault(portal.field_70170_p.func_234923_W_(), new HashMap());
      List<PortalEntity> portals = (List)chunks.getOrDefault(chunkPos, new ArrayList());
      portals.add(portal);
      chunks.put(chunkPos, portals);
      this.portalsPerChunk.put(portal.field_70170_p.func_234923_W_(), chunks);
      PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPortalPairPacket(gunUUID, new PartialPortalPair(pair)));
      getStorageAndSetDirty();
   }

   public void remove(UUID gunUUID, PortalEntity portal) {
      this.portalMap.computeIfPresent(gunUUID, (uuid, pair) -> {
         pair.remove(portal);
         portal.field_70175_ag = false;
         RegistryKey<World> dimension = portal.field_70170_p.func_234923_W_();
         ChunkPos chunkPos = new ChunkPos(MathHelper.func_76128_c(portal.func_226277_ct_()) >> 4, MathHelper.func_76128_c(portal.func_226281_cx_()) >> 4);
         if (this.portalsPerChunk.containsKey(dimension) && ((HashMap)this.portalsPerChunk.get(dimension)).containsKey(chunkPos)) {
            List<PortalEntity> portals = (List)((HashMap)this.portalsPerChunk.get(dimension)).get(chunkPos);
            portals.remove(portal);
            if (portals.isEmpty()) {
               ((HashMap)this.portalsPerChunk.get(dimension)).remove(chunkPos);
            }
         }

         getStorageAndSetDirty();
         return !pair.isEmpty() ? pair : null;
      });
      PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPortalPairPacket(gunUUID, new PartialPortalPair((PortalPair)this.portalMap.getOrDefault(gunUUID, new PortalPair()))));
   }

   public boolean has(UUID gunUUID, PortalEnd end) {
      return this.portalMap.containsKey(gunUUID) && ((PortalPair)this.portalMap.get(gunUUID)).has(end);
   }

   @Nullable
   public PortalEntity get(UUID gunUUID, PortalEnd end) {
      return this.portalMap.containsKey(gunUUID) ? ((PortalPair)this.portalMap.get(gunUUID)).get(end) : null;
   }

   @Nullable
   public PortalPair getPair(UUID gunUUID) {
      return (PortalPair)this.portalMap.getOrDefault(gunUUID, (Object)null);
   }

   public Map<UUID, PortalPair> getPortalMap() {
      return this.portalMap;
   }

   private static class PortalPlacementInfo {
      public final World level;
      public final PortalEnd end;
      public final String hue;
      public final UUID gunUUID;
      public final Vec3 position;
      public final Direction face;
      public final Direction upDirection;
      public final boolean override;
      public final boolean overwriteForeignPortals;
      @Nullable
      public final Direction[] lookingDirections;
      @Nullable
      public final ServerPlayerEntity player;
      public long tickCountdown;
      public Consumer<PortalEntity> onPlace;

      public PortalPlacementInfo(World level, PortalEnd end, String hue, UUID gunUUID, Vec3 position, Direction face, Direction upDirection, boolean override, boolean overwriteForeignPortals, @Nullable Direction[] lookingDirections, @Nullable ServerPlayerEntity player, long tickCountdown, Consumer<PortalEntity> onPlace) {
         this.level = level;
         this.end = end;
         this.hue = hue;
         this.gunUUID = gunUUID;
         this.position = position;
         this.face = face;
         this.upDirection = upDirection;
         this.override = override;
         this.overwriteForeignPortals = overwriteForeignPortals;
         this.lookingDirections = lookingDirections;
         this.player = player;
         this.tickCountdown = tickCountdown;
         this.onPlace = onPlace;
      }
   }
}
