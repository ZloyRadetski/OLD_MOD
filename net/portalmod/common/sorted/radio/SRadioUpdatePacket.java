package net.portalmod.common.sorted.radio;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class SRadioUpdatePacket implements AbstractPacket<SRadioUpdatePacket> {
   private BlockPos pos;
   private RadioState state;

   public SRadioUpdatePacket() {
   }

   public SRadioUpdatePacket(BlockPos pos, RadioState state) {
      this.pos = pos;
      this.state = state;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos).func_179249_a(this.state);
   }

   public SRadioUpdatePacket decode(PacketBuffer buffer) {
      return new SRadioUpdatePacket(buffer.func_179259_c(), (RadioState)buffer.func_179257_a(RadioState.class));
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
               RadioBlockTileEntity blockEntity = (RadioBlockTileEntity)Minecraft.func_71410_x().field_71441_e.func_175625_s(this.pos);
               if (blockEntity != null) {
                  blockEntity.handlePacket(this.state);
               }

            }));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
