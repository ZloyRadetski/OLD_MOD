package net.portalmod.core.packet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.interfaces.ITeleportLerpable;

public class SEntityPortalTeleportLerpPacket implements AbstractPacket<SEntityPortalTeleportLerpPacket> {
   private int entityId;
   private byte xRot;
   private byte yRot;
   private boolean isOnGround;
   private Deque<Tuple<Vector3d, Vector3d>> lerpPositions;

   public SEntityPortalTeleportLerpPacket() {
   }

   public SEntityPortalTeleportLerpPacket(int entityId, byte xRot, byte yRot, boolean isOnGround, Deque<Tuple<Vector3d, Vector3d>> lerpPositions) {
      this.entityId = entityId;
      this.xRot = xRot;
      this.yRot = yRot;
      this.isOnGround = isOnGround;
      this.lerpPositions = lerpPositions;
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.entityId);
      buffer.writeByte(this.xRot);
      buffer.writeByte(this.yRot);
      buffer.writeBoolean(this.isOnGround);
      buffer.writeInt(this.lerpPositions.size());

      for(Tuple<Vector3d, Vector3d> lerpPos : this.lerpPositions) {
         Vector3d oldPos = (Vector3d)lerpPos.func_76341_a();
         Vector3d newPos = (Vector3d)lerpPos.func_76340_b();
         buffer.writeDouble(oldPos.field_72450_a);
         buffer.writeDouble(oldPos.field_72448_b);
         buffer.writeDouble(oldPos.field_72449_c);
         buffer.writeDouble(newPos.field_72450_a);
         buffer.writeDouble(newPos.field_72448_b);
         buffer.writeDouble(newPos.field_72449_c);
      }

   }

   public SEntityPortalTeleportLerpPacket decode(PacketBuffer buffer) {
      int entityId = buffer.readInt();
      byte xRot = buffer.readByte();
      byte yRot = buffer.readByte();
      boolean isOnGround = buffer.readBoolean();
      int size = buffer.readInt();
      Deque<Tuple<Vector3d, Vector3d>> lerpPositions = new ArrayDeque();

      for(int i = 0; i < size; ++i) {
         Vector3d oldPos = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
         Vector3d newPos = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
         lerpPositions.add(new Tuple(oldPos, newPos));
      }

      return new SEntityPortalTeleportLerpPacket(entityId, xRot, yRot, isOnGround, lerpPositions);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
               Entity entity = Minecraft.func_71410_x().field_71441_e.func_73045_a(this.entityId);
               if (entity != null && entity != Minecraft.func_71410_x().field_71439_g) {
                  Vector3d lastPos = (Vector3d)((Tuple)this.lerpPositions.peekLast()).func_76340_b();
                  entity.func_213312_b(lastPos.field_72450_a, lastPos.field_72448_b, lastPos.field_72449_c);
                  float yRot = (float)(this.yRot * 360) / 256.0F;
                  float xRot = (float)(this.xRot * 360) / 256.0F;
                  entity.field_70177_z = xRot % 360.0F;
                  entity.field_70125_A = yRot % 360.0F;
                  entity.func_230245_c_(this.isOnGround);
                  Deque<Tuple<Vector3d, Vector3d>> lerpPositions = ((ITeleportLerpable)entity).getLerpPositions();
                  if (!lerpPositions.isEmpty()) {
                     Vector3d pos = (Vector3d)((Tuple)lerpPositions.peekLast()).func_76340_b();
                     entity.func_226286_f_(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c);
                     lerpPositions.clear();
                  }

                  lerpPositions.addAll(this.lerpPositions);
               }

            }));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
