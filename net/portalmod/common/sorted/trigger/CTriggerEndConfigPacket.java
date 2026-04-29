package net.portalmod.common.sorted.trigger;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.packet.AbstractPacket;

public class CTriggerEndConfigPacket implements AbstractPacket<CTriggerEndConfigPacket> {
   private BlockPos pos;
   private BlockPos start;
   private BlockPos end;

   public CTriggerEndConfigPacket() {
   }

   public CTriggerEndConfigPacket(BlockPos pos, BlockPos start, BlockPos end) {
      this.pos = pos;
      this.start = start;
      this.end = end;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
      buffer.func_179255_a(this.start);
      buffer.func_179255_a(this.end);
   }

   public CTriggerEndConfigPacket decode(PacketBuffer buffer) {
      return new CTriggerEndConfigPacket(buffer.func_179259_c(), buffer.func_179259_c(), buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity sender = ((NetworkEvent.Context)context.get()).getSender();
         if (sender != null) {
            TriggerTileEntity blockEntity = (TriggerTileEntity)sender.field_70170_p.func_175625_s(this.pos);
            if (blockEntity != null) {
               blockEntity.setField(this.start, this.end);
               sender.field_70170_p.func_184138_a(this.pos, ((Block)BlockInit.TRIGGER.get()).func_176223_P(), ((Block)BlockInit.TRIGGER.get()).func_176223_P(), 3);
               TriggerSelectionServer.endConfiguration(sender);
            }
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
