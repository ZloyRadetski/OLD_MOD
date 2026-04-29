package net.portalmod.common.sorted.portal;

public interface ITeleportable {
   void setLastUsedPortal(int var1);

   int getLastUsedPortal();

   boolean hasLastUsedPortal();

   void removeLastUsedPortal();
}
