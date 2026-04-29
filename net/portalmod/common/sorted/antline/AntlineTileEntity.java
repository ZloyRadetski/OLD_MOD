package net.portalmod.common.sorted.antline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.util.ModUtil;

public class AntlineTileEntity extends TileEntity {
   private final SideMap sideMap;

   public SideMap getSideMap() {
      return this.sideMap;
   }

   public AntlineTileEntity(TileEntityType<?> type) {
      super(type);
      this.sideMap = new SideMap();
   }

   public AntlineTileEntity() {
      this((TileEntityType)TileEntityTypeInit.ANTLINE.get());
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      if (this.sideMap.isBlank()) {
         this.sideMap.makeDefault();
      }

      return super.func_189515_b(this.sideMap.get(nbt));
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      this.sideMap.set(nbt);
   }

   public void load(CompoundNBT nbt, boolean complete) {
      if (complete) {
         this.sideMap.set(nbt);
      } else {
         this.sideMap.merge(nbt);
      }

   }

   protected void invalidateCaps() {
      if (!this.field_145850_b.field_72995_K) {
         SideMap sidemap = this.getSideMap();
         int count = sidemap.getSideCount();

         for(int i = 0; i < count - 1; ++i) {
            AntlineBlock.func_220075_c(getBlock().func_176223_P(), this.field_145850_b, this.func_174877_v());
         }

         super.invalidateCaps();
      }
   }

   private static Block getBlock() {
      return (Block)BlockInit.ANTLINE.get();
   }

   public IModelData getModelData() {
      return this.sideMap.toModelData();
   }

   public void func_189667_a(Rotation rotation) {
      SideMap initial = (SideMap)this.sideMap.clone();

      for(Direction direction : Direction.values()) {
         Side side = (Side)initial.get(direction);
         side.setSideDirection(rotation.func_185831_a(side.toDirection()));
         this.sideMap.put(rotation.func_185831_a(direction), side);
      }

      ((Side)this.sideMap.get(Direction.UP)).rotate(ModUtil.getRotationAmount(rotation));
      ((Side)this.sideMap.get(Direction.DOWN)).rotate(ModUtil.getRotationAmount(rotation));
   }

   public void func_189668_a(Mirror mirror) {
      if (mirror != Mirror.NONE) {
         SideMap initial = (SideMap)this.sideMap.clone();

         for(Direction direction : Direction.values()) {
            Side side = (Side)initial.get(direction);
            side.setSideDirection(mirror.func_185803_b(side.toDirection()));
            boolean onWall = direction.func_176740_k() != Axis.Y;
            side.mirror(onWall || mirror == Mirror.FRONT_BACK);
            this.sideMap.put(mirror.func_185803_b(direction), side);
         }

      }
   }

   public CompoundNBT func_189517_E_() {
      return this.func_189515_b(new CompoundNBT());
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.func_174877_v(), -1, this.func_189515_b(new CompoundNBT()));
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
      this.load(packet.func_148857_g(), true);
   }

   public static class SideMap extends HashMap<Direction, Side> {
      public static final ModelProperty<SideMap> MODEL_PROPERTY = new ModelProperty();

      public void makeDefault() {
         for(Direction direction : Direction.values()) {
            this.put(direction, direction == Direction.DOWN ? AntlineTileEntity.Side.dot(direction) : AntlineTileEntity.Side.emptySide(direction));
         }

      }

      public SideMap() {
         for(Direction direction : Direction.values()) {
            this.put(direction, AntlineTileEntity.Side.emptySide(direction));
         }

      }

      public boolean isBlank() {
         for(Direction direction : Direction.values()) {
            if (!((Side)this.get(direction)).isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public boolean hasSide(Direction direction) {
         return !((Side)this.get(direction)).isEmpty();
      }

      public void set(CompoundNBT nbt) {
         CompoundNBT data = nbt.func_74775_l("AntlineSides");

         for(Direction direction : Direction.values()) {
            this.put(direction, new Side(direction, data.func_74771_c(direction.func_176742_j())));
         }

      }

      public CompoundNBT get(CompoundNBT nbt) {
         CompoundNBT data = new CompoundNBT();

         for(Direction direction : Direction.values()) {
            data.func_74774_a(direction.func_176742_j(), ((Side)this.get(direction)).value);
         }

         nbt.func_218657_a("AntlineSides", data);
         return nbt;
      }

      public void merge(CompoundNBT nbt) {
         for(String s : nbt.func_150296_c()) {
            this.put(Direction.valueOf(s.toUpperCase()), new Side(Direction.valueOf(s.toUpperCase()), nbt.func_74771_c(s)));
         }

      }

      public int getSideCount() {
         return (int)this.values().stream().filter((v) -> v.value != 15).count();
      }

      public void removeSide(Direction direction) {
         this.put(direction, AntlineTileEntity.Side.emptySide(direction));
      }

      public IModelData toModelData() {
         ModelDataMap.Builder builder = new ModelDataMap.Builder();
         builder.withInitial(MODEL_PROPERTY, this);
         return builder.build();
      }
   }

   public static class Side {
      private byte value;
      private Direction sideDir;

      public Side(Direction sideDir, byte value) {
         this.sideDir = sideDir;
         this.value = value;
      }

      public static Side emptySide(Direction direction) {
         return new Side(direction, (byte)15);
      }

      public static Side dot(Direction direction) {
         return new Side(direction, (byte)0);
      }

      public void setActualValue(byte value) {
         this.value = value;
      }

      public void setValue(byte value) {
         this.value = (byte)(this.value & 16 | value & 15);
      }

      public void setSideDirection(Direction direction) {
         this.sideDir = direction;
      }

      public boolean isEmpty() {
         return this.getValue() == 15;
      }

      public boolean isConnectable() {
         return this.countConnections() <= 2;
      }

      public boolean isConnectableWith(Direction direction) {
         return this.isConnectable() || this.hasConnection(direction);
      }

      public Set<Direction> absoluteConnections() {
         return (Set)Arrays.stream(Direction.values()).filter(this::hasConnection).collect(Collectors.toSet());
      }

      public static byte valueByDirection(Direction direction) {
         switch (direction) {
            case NORTH:
               return 8;
            case EAST:
               return 4;
            case SOUTH:
               return 2;
            case WEST:
               return 1;
            default:
               return 0;
         }
      }

      private Direction toRelative(Direction direction) {
         if (this.sideDir.func_176740_k() != Axis.Y) {
            switch (direction) {
               case UP:
                  return Direction.NORTH;
               case DOWN:
                  return Direction.SOUTH;
               default:
                  switch (this.sideDir) {
                     case NORTH:
                        return direction;
                     case EAST:
                        return direction.func_176735_f();
                     case SOUTH:
                        return direction.func_176734_d();
                     case WEST:
                        return direction.func_176746_e();
                  }
            }
         }

         return direction;
      }

      public boolean hasConnection(Direction direction) {
         return Integer.bitCount(this.getValue() & valueByDirection(this.toRelative(direction))) != 0 && this.getValue() != 15;
      }

      public HashMap<Direction, Boolean> getConnections() {
         HashMap<Direction, Boolean> connections = new HashMap();

         for(Direction direction : Direction.values()) {
            if (direction.func_176740_k() != Axis.Y) {
               connections.put(this.toRelative(direction), this.hasConnection(direction));
            }
         }

         return connections;
      }

      public void addConnection(Direction direction) {
         int newValue = this.value | valueByDirection(this.toRelative(direction));
         this.setValue((byte)newValue);
      }

      public void removeConnection(Direction direction) {
         int newValue = this.value & ~valueByDirection(this.toRelative(direction));
         this.setValue((byte)newValue);
      }

      public SideType getSideType() {
         if (this.getValue() == 15) {
            return AntlineTileEntity.Side.SideType.NONE;
         } else {
            return this.getValue() != 5 && this.getValue() != 10 ? AntlineTileEntity.Side.SideType.CORNER : AntlineTileEntity.Side.SideType.NORMAL;
         }
      }

      public byte getValue() {
         return (byte)(this.value & 15);
      }

      public byte getActualValue() {
         return this.value;
      }

      public boolean isActive() {
         return (this.value & 16) != 0;
      }

      public void setActive(boolean active) {
         this.setActualValue((byte)(active ? this.value | 16 : this.value & 15));
      }

      public int countConnections() {
         return this.getValue() == 15 ? 0 : Integer.bitCount(this.getValue());
      }

      public Direction toDirection() {
         return this.sideDir;
      }

      public void rotate(int times) {
         times = Math.floorMod(times, 4);
         byte value = this.getValue();
         this.setValue((byte)(value >> times | value << 4 - times));
      }

      public void mirror(boolean flipEastWest) {
         if (flipEastWest) {
            int east = this.getValue() & valueByDirection(Direction.EAST);
            int west = this.getValue() & valueByDirection(Direction.WEST);
            this.setValue((byte)(this.getValue() & ~east & ~west | east >> 2 | west << 2));
         } else {
            int north = this.getValue() & valueByDirection(Direction.NORTH);
            int south = this.getValue() & valueByDirection(Direction.SOUTH);
            this.setValue((byte)(this.getValue() & ~north & ~south | north >> 2 | south << 2));
         }

      }

      public static enum SideType {
         NORMAL,
         NONE,
         CORNER;
      }
   }
}
