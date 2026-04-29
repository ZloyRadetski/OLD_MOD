package net.portalmod.common.sorted.portalgun;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.common.blocks.PushDoorBlock;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.sorted.button.StandingButtonBlock;
import net.portalmod.common.sorted.portal.ITeleportable;
import net.portalmod.common.sorted.portal.PortalEnd;
import net.portalmod.common.triggers.CodeBoundTrigger;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.packet.AbstractPacket;

public class CPortalGunInteractionPacket implements AbstractPacket<CPortalGunInteractionPacket> {
   private PortalGunInteraction type;
   private PortalEnd end;
   private int data;
   private BlockRayTraceResult blockHit;

   public CPortalGunInteractionPacket() {
   }

   public CPortalGunInteractionPacket(PortalGunInteraction type, PortalEnd end, int data, BlockRayTraceResult blockHit) {
      this.type = type;
      this.end = end;
      this.data = data;
      this.blockHit = blockHit;
   }

   public void encode(PacketBuffer buffer) {
      buffer.func_179249_a(this.type);
      buffer.func_179249_a(this.end);
      buffer.writeInt(this.data);
      this.writeBlockHitResult(buffer, this.blockHit);
   }

   public CPortalGunInteractionPacket decode(PacketBuffer buffer) {
      return new CPortalGunInteractionPacket((PortalGunInteraction)buffer.func_179257_a(PortalGunInteraction.class), (PortalEnd)buffer.func_179257_a(PortalEnd.class), buffer.readInt(), this.readBlockHitResult(buffer));
   }

   private void writeBlockHitResult(PacketBuffer buffer, BlockRayTraceResult result) {
      BlockPos pos = result.func_216350_a();
      buffer.func_179255_a(pos);
      buffer.func_179249_a(result.func_216354_b());
      Vector3d location = result.func_216347_e();
      buffer.writeFloat((float)(location.field_72450_a - (double)pos.func_177958_n()));
      buffer.writeFloat((float)(location.field_72448_b - (double)pos.func_177956_o()));
      buffer.writeFloat((float)(location.field_72449_c - (double)pos.func_177952_p()));
      buffer.writeBoolean(result.func_216346_c() == Type.MISS);
      buffer.writeBoolean(result.func_216353_d());
   }

   private BlockRayTraceResult readBlockHitResult(PacketBuffer buffer) {
      BlockPos blockpos = buffer.func_179259_c();
      Direction direction = (Direction)buffer.func_179257_a(Direction.class);
      float x = buffer.readFloat();
      float y = buffer.readFloat();
      float z = buffer.readFloat();
      Vector3d position = new Vector3d((double)((float)blockpos.func_177958_n() + x), (double)((float)blockpos.func_177956_o() + y), (double)((float)blockpos.func_177952_p() + z));
      boolean miss = buffer.readBoolean();
      boolean inside = buffer.readBoolean();
      return miss ? BlockRayTraceResult.func_216352_a(position, direction, blockpos) : new BlockRayTraceResult(position, direction, blockpos, inside);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
         ServerPlayerEntity player = ((NetworkEvent.Context)context.get()).getSender();
         if (player != null) {
            switch (this.type) {
               case PICK_ENTITY:
                  if (player.func_184614_ca().func_77973_b() instanceof PortalGun) {
                     ((CodeBoundTrigger)CriteriaTriggerInit.GRAB_ENTITY.get()).trigger(player);
                  }

                  Entity entity = player.field_70170_p.func_73045_a(this.data);
                  if (entity instanceof TestElementEntity) {
                     ((TestElementEntity)entity).pickUp(player);
                  }

                  ((ITeleportable)entity).removeLastUsedPortal();
                  break;
               case DROP_ENTITY:
                  TestElementEntity.dropHeldEntities(player, false, false, player.func_184614_ca());
                  break;
               case THROW_ENTITY:
                  TestElementEntity.dropHeldEntities(player, true, false, player.func_184614_ca());
                  break;
               case RELEASE_ENTITY:
                  TestElementEntity.dropHeldEntities(player, false, true, player.func_184614_ca());
                  break;
               case SHOOT_PORTAL:
                  PortalGun.placePortal(player, player.field_70170_p, this.end, player.func_184614_ca(), this.blockHit);
                  break;
               case PRESS_BUTTON:
               case OPEN_DOOR:
                  BlockState blockState = player.field_70170_p.func_180495_p(this.blockHit.func_216350_a());
                  Block block = blockState.func_177230_c();
                  if (block instanceof StandingButtonBlock && ((StandingButtonBlock)block).canPress(blockState)) {
                     ((StandingButtonBlock)block).press(blockState, player.field_70170_p, this.blockHit.func_216350_a());
                  }

                  if (block instanceof PushDoorBlock) {
                     ((PushDoorBlock)block).interact(blockState, player.field_70170_p, this.blockHit.func_216350_a(), this.blockHit);
                  }
                  break;
               case FIZZLE:
                  PortalGun.fizzleGunsInInventory(player);
            }

         }
      });
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }

   public static class Builder {
      private final PortalGunInteraction type;
      private PortalEnd end;
      private int data;
      private BlockRayTraceResult blockHit;

      public Builder(PortalGunInteraction type) {
         this.end = PortalEnd.NONE;
         this.data = -1;
         this.blockHit = BlockRayTraceResult.func_216352_a(Vector3d.field_186680_a, Direction.NORTH, BlockPos.field_177992_a);
         this.type = type;
      }

      public Builder end(PortalEnd end) {
         this.end = end;
         return this;
      }

      public Builder data(int data) {
         this.data = data;
         return this;
      }

      public Builder blockHit(BlockRayTraceResult blockHit) {
         this.blockHit = blockHit;
         return this;
      }

      public CPortalGunInteractionPacket build() {
         return new CPortalGunInteractionPacket(this.type, this.end, this.data, this.blockHit);
      }
   }
}
