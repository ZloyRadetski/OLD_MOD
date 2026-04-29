package net.portalmod.common.sorted.faithplate;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.packet.AbstractPacket;

public class CFaithPlateLaunchPacket implements AbstractPacket<CFaithPlateLaunchPacket> {
   private BlockPos pos;

   public CFaithPlateLaunchPacket() {
   }

   public CFaithPlateLaunchPacket(BlockPos pos) {
      this.pos = pos;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179255_a(this.pos);
   }

   public CFaithPlateLaunchPacket decode(PacketBuffer buffer) {
      return new CFaithPlateLaunchPacket(buffer.func_179259_c());
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         World level = ((NetworkEvent.Context)context.get()).getSender().field_70170_p;
         FaithPlateTileEntity be = (FaithPlateTileEntity)level.func_175625_s(this.pos);
         if (be != null) {
            PacketInit.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> ((NetworkEvent.Context)context.get()).getSender()), new SFaithPlateLaunchPacket(this.pos));
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
