package net.portalmod.common.sorted.gel;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class CRepulsionGelBouncePacket implements AbstractPacket<CRepulsionGelBouncePacket> {
   private boolean bounced;

   public CRepulsionGelBouncePacket() {
   }

   public CRepulsionGelBouncePacket(boolean bounced) {
      this.bounced = bounced;
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeBoolean(this.bounced);
   }

   public CRepulsionGelBouncePacket decode(PacketBuffer buffer) {
      return new CRepulsionGelBouncePacket(buffer.readBoolean());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity player = ((NetworkEvent.Context)context.get()).getSender();
         if (player != null) {
            ((IGelAffected)player).setBounced(this.bounced);
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
