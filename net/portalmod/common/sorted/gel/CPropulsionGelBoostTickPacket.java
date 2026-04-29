package net.portalmod.common.sorted.gel;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class CPropulsionGelBoostTickPacket implements AbstractPacket<CPropulsionGelBoostTickPacket> {
   private int ticks;

   public CPropulsionGelBoostTickPacket() {
   }

   public CPropulsionGelBoostTickPacket(int ticks) {
      this.ticks = ticks;
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.ticks);
   }

   public CPropulsionGelBoostTickPacket decode(PacketBuffer buffer) {
      return new CPropulsionGelBoostTickPacket(buffer.readInt());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity player = ((NetworkEvent.Context)context.get()).getSender();
         if (player != null) {
            ((IGelAffected)player).setPropulsionTicks(this.ticks);
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
