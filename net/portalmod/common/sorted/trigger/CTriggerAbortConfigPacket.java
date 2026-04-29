package net.portalmod.common.sorted.trigger;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class CTriggerAbortConfigPacket implements AbstractPacket<CTriggerAbortConfigPacket> {
   private BlockPos pos;

   public CTriggerAbortConfigPacket() {
   }

   public CTriggerAbortConfigPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
   }

   public CTriggerAbortConfigPacket decode(PacketBuffer buffer) {
      return new CTriggerAbortConfigPacket(buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity sender = ((NetworkEvent.Context)context.get()).getSender();
         if (sender != null) {
            TriggerSelectionServer.endConfiguration(sender);
         }

      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
