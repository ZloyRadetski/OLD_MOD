package net.portalmod.core.packet;

import java.util.Deque;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.interfaces.ITeleportLerpable;
import net.portalmod.core.util.ModUtil;

public class CPlayerPortalTeleportLerpPacket implements AbstractPacket<CPlayerPortalTeleportLerpPacket> {
   private Vector3d oldPosition;

   public CPlayerPortalTeleportLerpPacket() {
   }

   public CPlayerPortalTeleportLerpPacket(Vector3d oldPosition) {
      this.oldPosition = oldPosition;
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeDouble(this.oldPosition.field_72450_a);
      buffer.writeDouble(this.oldPosition.field_72448_b);
      buffer.writeDouble(this.oldPosition.field_72449_c);
   }

   public CPlayerPortalTeleportLerpPacket decode(PacketBuffer buffer) {
      Vector3d oldPosition = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      return new CPlayerPortalTeleportLerpPacket(oldPosition);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity sender = ((NetworkEvent.Context)context.get()).getSender();
         if (sender != null) {
            sender.field_70169_q = this.oldPosition.field_72450_a;
            sender.field_70167_r = this.oldPosition.field_72448_b;
            sender.field_70166_s = this.oldPosition.field_72449_c;
            Deque<Tuple<Vector3d, Vector3d>> lerpPositions = ((ITeleportLerpable)sender).getLerpPositions();
            lerpPositions.add(new Tuple(ModUtil.getOldPos(sender), sender.func_213303_ch()));

            while(lerpPositions.size() > 3) {
               lerpPositions.removeFirst();
            }

         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
