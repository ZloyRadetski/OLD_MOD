package net.portalmod.common.sorted.portal;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.packet.AbstractPacket;

public class PortalPacket {
   public static enum Type {
      REPLACE;
   }

   public static class Client implements AbstractPacket<Client> {
      private final Type type;
      private final int entityId;

      public Client(Type type, int entityId) {
         this.type = type;
         this.entityId = entityId;
      }

      public void encode(PacketBuffer buffer) {
         buffer.func_179249_a(this.type).writeInt(this.entityId);
      }

      public Client decode(PacketBuffer buffer) {
         return new Client((Type)buffer.func_179257_a(Type.class), buffer.readInt());
      }

      public boolean handle(Supplier<NetworkEvent.Context> context) {
         ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                  if (this.type == PortalPacket.Type.REPLACE) {
                     Minecraft.func_71410_x().field_71441_e.func_217413_d(this.entityId);
                  }

               }));
         ((NetworkEvent.Context)context.get()).setPacketHandled(true);
         return true;
      }
   }
}
