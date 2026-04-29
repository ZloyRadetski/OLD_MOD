package net.portalmod.common.sorted.faithplate;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class SFaithPlateStartConfigPacket implements AbstractPacket<SFaithPlateStartConfigPacket> {
   protected BlockPos pos;

   public SFaithPlateStartConfigPacket() {
   }

   public SFaithPlateStartConfigPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
   }

   public SFaithPlateStartConfigPacket decode(PacketBuffer buffer) {
      return new SFaithPlateStartConfigPacket(buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FaithPlateClient.setScreen(this.pos)));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
