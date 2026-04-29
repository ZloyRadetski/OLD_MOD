package net.portalmod.common.sorted.portalgun;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class SPortalGunAnimationPacket implements AbstractPacket<SPortalGunAnimationPacket> {
   protected UUID gunUUID;
   protected PortalGunAnimation animation;

   public SPortalGunAnimationPacket() {
   }

   public SPortalGunAnimationPacket(UUID gunUUID, PortalGunAnimation animation) {
      this.gunUUID = gunUUID;
      this.animation = animation;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179252_a(this.gunUUID);
      buffer.func_179249_a(this.animation);
   }

   public SPortalGunAnimationPacket decode(PacketBuffer buffer) {
      return new SPortalGunAnimationPacket(buffer.func_179253_g(), (PortalGunAnimation)buffer.func_179257_a(PortalGunAnimation.class));
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
               switch (this.animation) {
                  case SHOOT:
                     PortalGunISTER.startShootAnimation(this.gunUUID);
                     break;
                  case FIZZLE:
                     PortalGunISTER.startFizzleAnimation();
                     break;
                  case DROP:
                     PortalGunISTER.stopLiftAnimation(this.gunUUID);
                     break;
                  case LIFT:
                     PortalGunISTER.startLiftAnimation(this.gunUUID);
               }

            }));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
