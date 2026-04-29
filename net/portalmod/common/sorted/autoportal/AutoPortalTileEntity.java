package net.portalmod.common.sorted.autoportal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.portalmod.common.sorted.antline.indicator.IndicatorActivated;
import net.portalmod.common.sorted.antline.indicator.IndicatorInfo;
import net.portalmod.common.sorted.portal.PortalColors;
import net.portalmod.common.sorted.portal.PortalEnd;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalPlacer;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ChangeDetector;

public class AutoPortalTileEntity extends TileEntity implements ITickableTileEntity, IndicatorActivated {
   public UUID gunUUID;
   public PortalEnd end;
   public Integer primaryColor;
   public Integer secondaryColor;
   public UUID lastOpenedUUID;
   public PortalEnd lastOpenedEnd;
   private final ChangeDetector powerChangeDetector;

   public AutoPortalTileEntity(TileEntityType<?> type) {
      super(type);
      this.powerChangeDetector = new ChangeDetector();
   }

   public AutoPortalTileEntity() {
      this((TileEntityType)TileEntityTypeInit.AUTOPORTAL.get());
   }

   public void link(UUID gunUUID, PortalEnd end, int primaryColor, int secondaryColor) {
      this.gunUUID = gunUUID;
      this.end = end;
      this.primaryColor = primaryColor;
      this.secondaryColor = secondaryColor;
      this.sendUpdate();
   }

   public void swapEnd() {
      this.end = this.end.other();
      this.sendUpdate();
   }

   public void setPowered(boolean value) {
      this.powerChangeDetector.set(value);
   }

   public void closePortal() {
      if (this.field_145850_b != null) {
         PortalEntity portal = (PortalEntity)((ServerWorld)this.field_145850_b).func_217461_a(this.lastOpenedUUID);
         if (portal != null) {
            portal.func_70106_y();
         }
      }

      this.lastOpenedUUID = null;
      this.sendUpdate();
   }

   public void sendUpdate() {
      if (this.field_145850_b != null) {
         this.field_145850_b.func_184138_a(this.func_174877_v(), this.func_195044_w(), this.func_195044_w(), 2);
      }

   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         BlockState blockState = this.func_195044_w();
         if (blockState.func_177230_c() instanceof AutoPortalBlock) {
            this.checkLastOpened();
            IndicatorInfo indicatorInfo = this.checkIndicators(blockState, this.field_145850_b, this.func_174877_v());
            boolean isPowered = (Boolean)blockState.func_177229_b(AutoPortalBlock.POWERED);
            if (indicatorInfo.hasIndicators && isPowered != indicatorInfo.allIndicatorsActivated) {
               ((AutoPortalBlock)blockState.func_177230_c()).setAntlinePowered(indicatorInfo.allIndicatorsActivated, blockState, this.field_145850_b, this.func_174877_v());
               if (indicatorInfo.allIndicatorsActivated) {
                  this.openPortal(blockState);
                  return;
               }
            }

            if (this.powerChangeDetector.isRising()) {
               this.openPortal(blockState);
            }

            this.powerChangeDetector.shift();
         }
      }
   }

   private void openPortal(BlockState blockState) {
      Direction facing = (Direction)this.func_195044_w().func_177229_b(AutoPortalBlock.FACING);
      Direction direction = (Direction)this.func_195044_w().func_177229_b(AutoPortalBlock.DIRECTION);
      Tuple<Direction, Direction> directions = ((AutoPortalBlock)blockState.func_177230_c()).placementDirectionsFromFacingAndDirection(facing, direction);
      Direction left = (Direction)directions.func_76341_a();
      Direction up = (Direction)directions.func_76340_b();
      if (facing.func_176743_c() == AxisDirection.POSITIVE) {
         left = left.func_176734_d();
      }

      Vec3 position = (new Vec3(this.func_174877_v())).add((double)0.5F).add((new Vec3(left.func_176734_d())).mul((double)0.5F)).add((new Vec3(up)).mul((double)0.5F)).add((new Vec3(facing.func_176734_d())).mul((double)0.5F));
      if (this.gunUUID != null && this.end != null && this.primaryColor != null && this.secondaryColor != null) {
         Optional<Integer> colorIndex = this.getCurrentColorIndex();
         if (colorIndex.isPresent()) {
            String color = PortalColors.values()[(Integer)colorIndex.get()].name();
            PortalEntity portal = PortalPlacer.placePortal(this.field_145850_b, this.end, color, this.gunUUID, position, facing, up, true, (Direction[])null, (ServerPlayerEntity)null);
            if (portal != null) {
               this.lastOpenedUUID = portal.func_110124_au();
            }

            this.sendUpdate();
         }
      }

   }

   private void checkLastOpened() {
      if (this.field_145850_b != null && this.lastOpenedUUID != null) {
         PortalEntity portal = (PortalEntity)((ServerWorld)this.field_145850_b).func_217461_a(this.lastOpenedUUID);
         if (portal == null || !portal.func_70089_S()) {
            this.lastOpenedUUID = null;
            this.sendUpdate();
         }

      }
   }

   public List<BlockPos> getIndicatorPositions(BlockState blockState, World world, BlockPos pos) {
      Direction facing = (Direction)blockState.func_177229_b(AutoPortalBlock.FACING);
      Direction direction = (Direction)blockState.func_177229_b(AutoPortalBlock.DIRECTION);
      Tuple<Direction, Direction> directions = ((AutoPortalBlock)blockState.func_177230_c()).placementDirectionsFromFacingAndDirection(facing, direction);
      Direction left = (Direction)directions.func_76341_a();
      Direction up = (Direction)directions.func_76340_b();
      if (facing.func_176743_c() == AxisDirection.POSITIVE) {
         left = left.func_176734_d();
      }

      Direction right = left.func_176734_d();
      Direction down = up.func_176734_d();
      List<BlockPos> positions = new ArrayList();
      positions.add(pos.func_177972_a(down));
      positions.add(pos.func_177972_a(left));
      positions.add(pos.func_177972_a(up).func_177972_a(left));
      positions.add(pos.func_177972_a(up).func_177972_a(up));
      positions.add(pos.func_177972_a(up).func_177972_a(right).func_177972_a(up));
      positions.add(pos.func_177972_a(up).func_177972_a(right).func_177972_a(right));
      positions.add(pos.func_177972_a(right).func_177972_a(right));
      positions.add(pos.func_177972_a(right).func_177972_a(down));
      return positions;
   }

   public Optional<Integer> getCurrentColorIndex() {
      return Optional.ofNullable(this.end == PortalEnd.PRIMARY ? this.primaryColor : this.secondaryColor);
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      if (this.gunUUID != null && this.end != null && this.primaryColor != null && this.secondaryColor != null) {
         nbt.func_186854_a("gunUUID", this.gunUUID);
         nbt.func_74778_a("end", this.end.toString().toLowerCase());
         nbt.func_74778_a("primaryColor", PortalColors.values()[this.primaryColor].toString().toLowerCase());
         nbt.func_74778_a("secondaryColor", PortalColors.values()[this.secondaryColor].toString().toLowerCase());
         if (this.lastOpenedUUID != null) {
            nbt.func_186854_a("lastOpenedPortal", this.lastOpenedUUID);
         }
      }

      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      this.load(nbt);
   }

   public void load(CompoundNBT nbt) {
      if (nbt.func_74764_b("gunUUID") && nbt.func_74764_b("end")) {
         if (nbt.func_74764_b("primaryColor") && nbt.func_74764_b("secondaryColor")) {
            this.gunUUID = nbt.func_186857_a("gunUUID");
            this.end = PortalEnd.valueOf(nbt.func_74779_i("end").toUpperCase());
            this.primaryColor = PortalColors.getIndex(nbt.func_74779_i("primaryColor"));
            this.secondaryColor = PortalColors.getIndex(nbt.func_74779_i("secondaryColor"));
            this.lastOpenedUUID = null;
            if (nbt.func_74764_b("lastOpenedPortal")) {
               this.lastOpenedUUID = nbt.func_186857_a("lastOpenedPortal");
            }

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
      this.load(packet.func_148857_g());
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return (new AxisAlignedBB(this.func_174877_v())).func_186662_g((double)1.0F);
   }

   public double func_145833_n() {
      return (double)256.0F;
   }
}
