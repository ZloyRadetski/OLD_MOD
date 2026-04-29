package net.portalmod.common.sorted.portal;

import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public final class PortalPairCache extends HashMap<UUID, PortalPair> {
   private static final long serialVersionUID = 4606521387923682753L;
   public static final PortalPairCache SERVER = new PortalPairCache();
   public static final PortalPairCache CLIENT = new PortalPairCache();

   private PortalPairCache() {
   }

   public static PortalPairCache select(boolean isClientSide) {
      return isClientSide ? CLIENT : SERVER;
   }

   public static PortalPairCache select(World level) {
      return select(level.field_72995_K);
   }

   public void put(UUID gunUUID, PortalEnd end, PortalEntity portal) {
      PortalPair pair = new PortalPair();
      if (this.containsKey(gunUUID)) {
         pair = (PortalPair)this.get(gunUUID);
      }

      if (pair.has(end)) {
         pair.get(end).onReplaced();
      }

      pair.set(end, portal);
      this.put(gunUUID, pair);
   }

   public void remove(UUID gunUUID, PortalEntity portal) {
      if (this.containsKey(gunUUID)) {
         PortalPair pair = (PortalPair)this.get(gunUUID);
         pair.remove(portal);
         if (pair.isEmpty()) {
            this.remove(gunUUID);
         }

      }
   }

   public boolean has(UUID gunUUID, PortalEnd end) {
      return !this.containsKey(gunUUID) ? false : ((PortalPair)this.get(gunUUID)).has(end);
   }

   @Nullable
   public PortalEntity get(UUID gunUUID, PortalEnd end) {
      return !this.containsKey(gunUUID) ? null : ((PortalPair)this.get(gunUUID)).get(end);
   }
}
