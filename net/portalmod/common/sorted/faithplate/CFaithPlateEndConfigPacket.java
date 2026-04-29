package net.portalmod.common.sorted.faithplate;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class CFaithPlateEndConfigPacket implements AbstractPacket<CFaithPlateEndConfigPacket> {
   private BlockPos pos;

   public CFaithPlateEndConfigPacket() {
   }

   public CFaithPlateEndConfigPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
   }

   public CFaithPlateEndConfigPacket decode(PacketBuffer buffer) {
      return new CFaithPlateEndConfigPacket(buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity sender = ((NetworkEvent.Context)context.get()).getSender();
         if (sender != null) {
            FaithPlateTileEntity blockEntity = (FaithPlateTileEntity)sender.field_70170_p.func_175625_s(this.pos);
            if (blockEntity != null) {
               blockEntity.endConfiguration();
            }
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
