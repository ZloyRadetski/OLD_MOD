package net.portalmod.common.sorted.portal;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class SPortalPairPacket implements AbstractPacket<SPortalPairPacket> {
   private UUID uuid;
   private PartialPortalPair ppp;

   public SPortalPairPacket() {
   }

   public SPortalPairPacket(UUID uuid, PartialPortalPair ppp) {
      this.uuid = uuid;
      this.ppp = ppp;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179252_a(this.uuid).writeBoolean(this.ppp.has(PortalEnd.PRIMARY)).writeBoolean(this.ppp.has(PortalEnd.SECONDARY));
      if (this.ppp.has(PortalEnd.PRIMARY)) {
         this.ppp.get(PortalEnd.PRIMARY).write(buffer);
      }

      if (this.ppp.has(PortalEnd.SECONDARY)) {
         this.ppp.get(PortalEnd.SECONDARY).write(buffer);
      }

   }

   public SPortalPairPacket decode(PacketBuffer buffer) {
      UUID uuid = buffer.func_179253_g();
      boolean hasPrimary = buffer.readBoolean();
      boolean hasSecondary = buffer.readBoolean();
      PartialPortalPair ppp = new PartialPortalPair();
      if (hasPrimary) {
         ppp.set(PortalEnd.PRIMARY, PartialPortal.read(buffer));
      }

      if (hasSecondary) {
         ppp.set(PortalEnd.SECONDARY, PartialPortal.read(buffer));
      }

      return new SPortalPairPacket(uuid, ppp);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPortalManager.getInstance().getPartialMap().put(this.uuid, this.ppp)));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
