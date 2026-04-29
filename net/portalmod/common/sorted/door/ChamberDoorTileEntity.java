package net.portalmod.common.sorted.door;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.common.sorted.antline.indicator.IndicatorActivated;
import net.portalmod.common.sorted.antline.indicator.IndicatorInfo;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.Vec3;

public class ChamberDoorTileEntity extends TileEntity implements ITickableTileEntity, IndicatorActivated {
   public ChamberDoorTileEntity() {
      super((TileEntityType)TileEntityTypeInit.CHAMBER_DOOR.get());
   }

   public void func_73660_a() {
      BlockState blockState = this.func_195044_w();
      if (blockState.func_177230_c() instanceof ChamberDoorBlock) {
         ChamberDoorBlock doorBlock = (ChamberDoorBlock)blockState.func_177230_c();
         BlockPos pos = this.func_174877_v();
         World world = this.field_145850_b;
         Direction facing = (Direction)blockState.func_177229_b(ChamberDoorBlock.FACING);
         IndicatorInfo indicatorInfo = this.checkIndicators(blockState, world, pos);
         boolean isOpen = (Boolean)blockState.func_177229_b(ChamberDoorBlock.OPEN);
         if (indicatorInfo.hasIndicators) {
            if (isOpen != indicatorInfo.allIndicatorsActivated) {
               doorBlock.setOpen(indicatorInfo.allIndicatorsActivated, blockState, world, pos);
            }

         } else {
            Vector3d middlePos = ChamberDoorBlock.getExactMiddlePos(blockState, pos);
            int changeProximity = isOpen ? 4 : 3;
            boolean hasNearbyPlayer = false;

            for(PlayerEntity player : world.func_217369_A()) {
               if (!player.func_175149_v()) {
                  boolean inFront = player.func_213303_ch().func_178788_d(middlePos).func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72430_b((new Vec3(facing.func_176730_m())).to3d()) > (double)0.0F;
                  double playerDistance = player.func_213303_ch().func_72438_d(middlePos);
                  if (playerDistance < (double)changeProximity && inFront || isOpen && playerDistance < (double)1.5F) {
                     hasNearbyPlayer = true;
                     break;
                  }
               }
            }

            if (isOpen != hasNearbyPlayer) {
               doorBlock.setOpen(hasNearbyPlayer, blockState, world, pos);
            }

         }
      }
   }

   public boolean isAutomatic() {
      IndicatorInfo indicatorInfo = this.checkIndicators(this.func_195044_w(), this.field_145850_b, this.func_174877_v());
      return !indicatorInfo.hasIndicators;
   }

   public List<BlockPos> getIndicatorPositions(BlockState blockState, World world, BlockPos pos) {
      Direction facing = (Direction)blockState.func_177229_b(ChamberDoorBlock.FACING);
      boolean isLower = blockState.func_177229_b(ChamberDoorBlock.HALF) == DoubleBlockHalf.LOWER;
      boolean isLeft = blockState.func_177229_b(ChamberDoorBlock.SIDE) == ChamberDoorBlock.Side.LEFT;
      Direction verticalDirection = isLower ? Direction.UP : Direction.DOWN;
      Direction horizontalDirection = isLeft ? facing.func_176735_f() : facing.func_176746_e();
      List<BlockPos> possibleIndicatorPositions = new ArrayList();
      possibleIndicatorPositions.addAll(this.getSurroundingPositions(pos.func_177972_a(facing), verticalDirection, horizontalDirection));
      possibleIndicatorPositions.addAll(this.getDoorPositions(pos.func_177972_a(facing), verticalDirection, horizontalDirection));
      possibleIndicatorPositions.addAll(this.getOuterCornerPositions(pos, verticalDirection, horizontalDirection));
      possibleIndicatorPositions.addAll(this.getOuterCornerPositions(pos.func_177972_a(facing.func_176734_d()), verticalDirection, horizontalDirection));
      return possibleIndicatorPositions;
   }

   public List<BlockPos> getSurroundingPositions(BlockPos pos, Direction vertical, Direction horizontal) {
      return new ArrayList(Arrays.asList(pos.func_177972_a(vertical.func_176734_d()), pos.func_177972_a(horizontal.func_176734_d()).func_177972_a(vertical.func_176734_d()), pos.func_177972_a(horizontal.func_176734_d()), pos.func_177972_a(horizontal.func_176734_d()).func_177972_a(vertical), pos.func_177972_a(horizontal.func_176734_d()).func_177967_a(vertical, 2), pos.func_177967_a(vertical, 2), pos.func_177972_a(horizontal).func_177967_a(vertical, 2), pos.func_177967_a(horizontal, 2).func_177967_a(vertical, 2), pos.func_177967_a(horizontal, 2).func_177972_a(vertical), pos.func_177967_a(horizontal, 2), pos.func_177967_a(horizontal, 2).func_177972_a(vertical.func_176734_d()), pos.func_177972_a(horizontal).func_177972_a(vertical.func_176734_d())));
   }

   public List<BlockPos> getDoorPositions(BlockPos pos, Direction vertical, Direction horizontal) {
      return new ArrayList(Arrays.asList(pos.func_177972_a(horizontal), pos.func_177972_a(horizontal).func_177972_a(vertical), pos.func_177972_a(vertical), pos));
   }

   public List<BlockPos> getOuterCornerPositions(BlockPos pos, Direction vertical, Direction horizontal) {
      return new ArrayList(Arrays.asList(pos.func_177972_a(horizontal.func_176734_d()).func_177972_a(vertical.func_176734_d()), pos.func_177972_a(horizontal.func_176734_d()).func_177967_a(vertical, 2), pos.func_177967_a(horizontal, 2).func_177967_a(vertical, 2), pos.func_177967_a(horizontal, 2).func_177972_a(vertical.func_176734_d())));
   }
}
