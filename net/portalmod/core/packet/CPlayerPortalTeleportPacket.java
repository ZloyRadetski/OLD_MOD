package net.portalmod.core.packet;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.common.sorted.portal.IClientTeleportable;
import net.portalmod.common.sorted.portal.PortalHandler;
import net.portalmod.core.interfaces.ITeleportLerpable;

public class CPlayerPortalTeleportPacket implements AbstractPacket<CPlayerPortalTeleportPacket> {
   public void encode(PacketBuffer buffer) {
   }

   public CPlayerPortalTeleportPacket decode(PacketBuffer buffer) {
      return new CPlayerPortalTeleportPacket();
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ((PortalHandler)((NetworkEvent.Context)context.get()).getSender()).onTeleportPacket();
         ((IClientTeleportable)((NetworkEvent.Context)context.get()).getSender()).setJustPortaled(true);
         ((ITeleportLerpable)((NetworkEvent.Context)context.get()).getSender()).setHasUsedPortal(true);
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
