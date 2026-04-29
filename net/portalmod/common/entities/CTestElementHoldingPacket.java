package net.portalmod.common.entities;

import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.packet.AbstractPacket;

public class CTestElementHoldingPacket implements AbstractPacket<CTestElementHoldingPacket> {
   private int id;
   private Vec3 oldPosition;
   private Vec3 position;
   private float oldRotation;
   private float rotation;

   public CTestElementHoldingPacket() {
   }

   public CTestElementHoldingPacket(int id, Vec3 oldPosition, Vec3 position, float oldRotation, float rotation) {
      this.id = id;
      this.oldPosition = oldPosition;
      this.position = position;
      this.oldRotation = oldRotation;
      this.rotation = rotation;
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.id);
      buffer.writeDouble(this.oldPosition.x);
      buffer.writeDouble(this.oldPosition.y);
      buffer.writeDouble(this.oldPosition.z);
      buffer.writeDouble(this.position.x);
      buffer.writeDouble(this.position.y);
      buffer.writeDouble(this.position.z);
      buffer.writeFloat(this.oldRotation);
      buffer.writeFloat(this.rotation);
   }

   public CTestElementHoldingPacket decode(PacketBuffer buffer) {
      int id = buffer.readInt();
      Vec3 oldPosition = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      Vec3 position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      float oldRotation = buffer.readFloat();
      float rotation = buffer.readFloat();
      return new CTestElementHoldingPacket(id, oldPosition, position, oldRotation, rotation);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity sender = ((NetworkEvent.Context)context.get()).getSender();
         if (sender != null) {
            Entity entity = sender.field_70170_p.func_73045_a(this.id);
            if (entity instanceof TestElementEntity) {
               TestElementEntity tee = (TestElementEntity)entity;
               if (entity.func_184187_bx() == sender) {
                  tee.serverOldPos = this.oldPosition;
                  tee.func_70107_b(this.position.x, this.position.y, this.position.z);
                  tee.field_70177_z = this.rotation;
                  tee.field_70126_B = this.oldRotation;
                  tee.field_70761_aq = this.rotation;
                  tee.field_70760_ar = this.oldRotation;
               }
            }
         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
