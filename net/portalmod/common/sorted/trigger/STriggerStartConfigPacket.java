package net.portalmod.common.sorted.trigger;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;
import net.portalmod.core.packet.ClientPacketHandler;

public class STriggerStartConfigPacket implements AbstractPacket<STriggerStartConfigPacket> {
   public BlockPos pos;

   public STriggerStartConfigPacket() {
   }

   public STriggerStartConfigPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
   }

   public STriggerStartConfigPacket decode(PacketBuffer buffer) {
      return new STriggerStartConfigPacket(buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSTriggerStartConfigPacket(this)));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
