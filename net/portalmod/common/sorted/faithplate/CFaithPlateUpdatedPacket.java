package net.portalmod.common.sorted.faithplate;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.packet.AbstractPacket;

public class CFaithPlateUpdatedPacket implements AbstractPacket<CFaithPlateUpdatedPacket> {
   private BlockPos pos;
   private CompoundNBT nbt;

   public CFaithPlateUpdatedPacket() {
   }

   public CFaithPlateUpdatedPacket(BlockPos pos, CompoundNBT nbt) {
      this.pos = pos;
      this.nbt = nbt;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos).func_150786_a(this.nbt);
   }

   public CFaithPlateUpdatedPacket decode(PacketBuffer buffer) {
      return new CFaithPlateUpdatedPacket(buffer.func_179259_c(), buffer.func_150793_b());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         FaithPlateTileEntity blockEntity = (FaithPlateTileEntity)((NetworkEvent.Context)context.get()).getSender().field_70170_p.func_175625_s(this.pos);
         if (blockEntity != null) {
            blockEntity.load(this.nbt);
            ((NetworkEvent.Context)context.get()).getSender().field_70170_p.func_184138_a(this.pos, ((Block)BlockInit.FAITHPLATE.get()).func_176223_P(), ((Block)BlockInit.FAITHPLATE.get()).func_176223_P(), 3);
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
