package net.portalmod.common.sorted.portal;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;
import net.portalmod.core.packet.ClientPacketHandler;

public class SPortalShotPacket implements AbstractPacket<SPortalShotPacket> {
   public int id;

   public SPortalShotPacket() {
   }

   public SPortalShotPacket(int id) {
      this.id = id;
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.id);
   }

   public SPortalShotPacket decode(PacketBuffer buffer) {
      return new SPortalShotPacket(buffer.readInt());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSPortalShotPacket(this)));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
