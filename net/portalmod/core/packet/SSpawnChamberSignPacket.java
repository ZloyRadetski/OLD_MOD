package net.portalmod.core.packet;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSpawnChamberSignPacket implements AbstractPacket<SSpawnChamberSignPacket> {
   public int id;
   public UUID uuid;
   public BlockPos pos;
   public Direction direction;
   public boolean verticallyAligned;

   public SSpawnChamberSignPacket() {
   }

   public SSpawnChamberSignPacket(int id, UUID uuid, BlockPos pos, Direction direction, boolean verticallyAligned) {
      this.id = id;
      this.uuid = uuid;
      this.pos = pos;
      this.direction = direction;
      this.verticallyAligned = verticallyAligned;
   }

   public SSpawnChamberSignPacket decode(PacketBuffer buffer) {
      int id = buffer.readInt();
      UUID uuid = buffer.func_179253_g();
      BlockPos blockPos = buffer.func_179259_c();
      Direction direction = Direction.func_176731_b(buffer.readInt());
      boolean verticallyAligned = buffer.readBoolean();
      return new SSpawnChamberSignPacket(id, uuid, blockPos, direction, verticallyAligned);
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.id);
      buffer.func_179252_a(this.uuid);
      buffer.func_179255_a(this.pos);
      buffer.writeInt(this.direction.func_176736_b());
      buffer.writeBoolean(this.verticallyAligned);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSSpawnChamberSignPacket(this)));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
