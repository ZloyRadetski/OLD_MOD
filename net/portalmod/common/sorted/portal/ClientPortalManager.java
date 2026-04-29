package net.portalmod.common.sorted.portal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class ClientPortalManager {
   private static ClientPortalManager instance;
   private final Map<UUID, PortalPair> PORTAL_MAP = new HashMap();
   private final Map<UUID, PartialPortalPair> PARTIAL_MAP = new HashMap();

   private ClientPortalManager() {
   }

   public static ClientPortalManager getInstance() {
      if (instance == null) {
         instance = new ClientPortalManager();
      }

      return instance;
   }

   public void clear() {
      this.PORTAL_MAP.clear();
      this.PARTIAL_MAP.clear();
   }

   public void put(UUID gunUUID, PortalEnd end, PortalEntity portal) {
      PortalPair pair = (PortalPair)this.PORTAL_MAP.getOrDefault(gunUUID, new PortalPair());
      pair.set(end, portal);
      this.PORTAL_MAP.put(gunUUID, pair);
   }

   public void remove(UUID gunUUID, PortalEntity portal) {
      this.PORTAL_MAP.computeIfPresent(gunUUID, (uuid, pair) -> {
         pair.remove(portal);
         return !pair.isEmpty() ? pair : null;
      });
   }

   public boolean has(UUID gunUUID, PortalEnd end) {
      return this.PORTAL_MAP.containsKey(gunUUID) && ((PortalPair)this.PORTAL_MAP.get(gunUUID)).has(end);
   }

   public boolean hasPartial(UUID gunUUID, PortalEnd end) {
      return this.PARTIAL_MAP.containsKey(gunUUID) && ((PartialPortalPair)this.PARTIAL_MAP.get(gunUUID)).has(end);
   }

   public boolean hasFullOrPartial(UUID gunUUID, PortalEnd end) {
      return this.has(gunUUID, end) || this.hasPartial(gunUUID, end);
   }

   @Nullable
   public PortalEntity get(UUID gunUUID, PortalEnd end) {
      return this.PORTAL_MAP.containsKey(gunUUID) ? ((PortalPair)this.PORTAL_MAP.get(gunUUID)).get(end) : null;
   }

   @Nullable
   public PartialPortal getPartial(UUID gunUUID, PortalEnd end) {
      return this.PARTIAL_MAP.containsKey(gunUUID) ? ((PartialPortalPair)this.PARTIAL_MAP.get(gunUUID)).get(end) : null;
   }

   public void forgetPortal(UUID gunUUID, PortalEnd end) {
      if (this.PORTAL_MAP.containsKey(gunUUID)) {
         ((PortalPair)this.PORTAL_MAP.get(gunUUID)).set(end, (PortalEntity)null);
      }

      if (this.PARTIAL_MAP.containsKey(gunUUID)) {
         ((PartialPortalPair)this.PARTIAL_MAP.get(gunUUID)).set(end, (PartialPortal)null);
      }

   }

   public Map<UUID, PortalPair> getPortalMap() {
      return this.PORTAL_MAP;
   }

   public Map<UUID, PartialPortalPair> getPartialMap() {
      return this.PARTIAL_MAP;
   }
}
