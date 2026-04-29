package net.portalmod.common.sorted.portal;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class CThroughPortalProofPacket implements AbstractPacket<CThroughPortalProofPacket> {
   private int[] portalChain;

   public CThroughPortalProofPacket() {
   }

   public CThroughPortalProofPacket(int[] portalChain) {
      this.portalChain = portalChain;
   }

   public void encode(PacketBuffer buffer) {
      int length = this.portalChain.length;
      buffer.writeInt(length);

      for(int i = 0; i < length; ++i) {
         buffer.writeInt(this.portalChain[i]);
      }

   }

   public CThroughPortalProofPacket decode(PacketBuffer buffer) {
      int length = buffer.readInt();
      int[] portalChain = new int[length];

      for(int i = 0; i < length; ++i) {
         portalChain[i] = buffer.readInt();
      }

      return new CThroughPortalProofPacket(portalChain);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity player = ((NetworkEvent.Context)context.get()).getSender();
         if (player != null) {
            MinecraftServer server = player.func_184102_h();
            if (server != null) {
               PortalServerProofManager.getInstance().setProof(player, server.func_71259_af(), this.portalChain);
            }
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
