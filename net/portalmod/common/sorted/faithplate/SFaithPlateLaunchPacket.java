package net.portalmod.common.sorted.faithplate;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class SFaithPlateLaunchPacket implements AbstractPacket<SFaithPlateLaunchPacket> {
   protected BlockPos pos;

   public SFaithPlateLaunchPacket() {
   }

   public SFaithPlateLaunchPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
   }

   public SFaithPlateLaunchPacket decode(PacketBuffer buffer) {
      return new SFaithPlateLaunchPacket(buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FaithPlateClient.handleLaunch(this, context)));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
