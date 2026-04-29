package net.portalmod.common.sorted.portal;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class SForgetPortalPacket implements AbstractPacket<SForgetPortalPacket> {
   private UUID uuid;
   private PortalEnd end;

   public SForgetPortalPacket() {
   }

   public SForgetPortalPacket(UUID uuid, PortalEnd end) {
      this.uuid = uuid;
      this.end = end;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179252_a(this.uuid).writeBoolean(this.end == PortalEnd.SECONDARY);
   }

   public SForgetPortalPacket decode(PacketBuffer buffer) {
      UUID uuid = buffer.func_179253_g();
      PortalEnd end = buffer.readBoolean() ? PortalEnd.SECONDARY : PortalEnd.PRIMARY;
      return new SForgetPortalPacket(uuid, end);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
               PortalEntity oldPortal = ClientPortalManager.getInstance().get(this.uuid, this.end);
               ClientPortalManager.getInstance().forgetPortal(this.uuid, this.end);
               if (oldPortal != null) {
                  PortalPhotonParticle.createClosingParticles(oldPortal);
               }

               PortalEntity otherPortal = ClientPortalManager.getInstance().get(this.uuid, this.end.other());
               if (otherPortal != null) {
                  otherPortal.pushEntities();
               }

            }));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
