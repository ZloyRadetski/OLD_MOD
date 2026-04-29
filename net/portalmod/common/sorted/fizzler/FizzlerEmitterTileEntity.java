package net.portalmod.common.sorted.fizzler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.portalmod.common.sorted.antline.indicator.IndicatorActivated;
import net.portalmod.common.sorted.antline.indicator.IndicatorInfo;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.util.ModUtil;

public class FizzlerEmitterTileEntity extends TileEntity implements ITickableTileEntity, IndicatorActivated {
   public static final int MAX_DISTANCE = 16;

   public FizzlerEmitterTileEntity() {
      super((TileEntityType)TileEntityTypeInit.FIZZLER_EMITTER.get());
   }

   public void func_73660_a() {
      this.handleAntlineActivation();
   }

   private void handleAntlineActivation() {
      World world = this.field_145850_b;
      BlockState state = this.func_195044_w();
      BlockPos pos = this.func_174877_v();
      boolean activated = (Boolean)state.func_177229_b(FizzlerEmitterBlock.ACTIVE);
      Direction facing = (Direction)state.func_177229_b(FizzlerEmitterBlock.FACING);
      Direction upDirection = ((FizzlerEmitterBlock)state.func_177230_c()).getUpperDirection(state);
      int distance = this.distanceToOtherSide(facing, upDirection);
      if (distance != 0) {
         BlockPos otherFizzlerPos = pos.func_177967_a(facing, distance);
         List<BlockPos> indicatorPositions = this.getIndicatorPositions(state, world, pos);
         indicatorPositions.addAll(this.getIndicatorPositions(world.func_180495_p(otherFizzlerPos), world, otherFizzlerPos));
         IndicatorInfo indicatorInfo = IndicatorActivated.checkPositions(world, indicatorPositions);
         if (indicatorInfo.hasIndicators) {
            if (indicatorInfo.allIndicatorsActivated != activated) {
               this.setActive(indicatorInfo.allIndicatorsActivated, distance, facing, upDirection);
            }

         } else {
            if (!activated) {
               this.setActive(true, distance, facing, upDirection);
            }

         }
      }
   }

   public void setActive(boolean active, int distance, Direction facing, Direction upDirection) {
      if (distance > 0) {
         this.setField(active, distance, facing, upDirection);
         BlockPos oppositeEmitterPos = this.func_174877_v().func_177967_a(facing, distance);
         BlockState oppositeEmitterState = this.field_145850_b.func_180495_p(oppositeEmitterPos);
         SoundEvent soundEvent = active ? (SoundEvent)SoundInit.FIZZLER_ACTIVATE.get() : (SoundEvent)SoundInit.FIZZLER_DEACTIVATE.get();
         float pitch = (ModUtil.randomSoundPitch() + 2.0F) / 3.0F;
         this.field_145850_b.func_184133_a((PlayerEntity)null, this.func_174877_v().func_177967_a(facing, distance / 2), soundEvent, SoundCategory.BLOCKS, 1.0F, pitch);
         ((FizzlerEmitterBlock)oppositeEmitterState.func_177230_c()).setBlockStateValue(FizzlerEmitterBlock.ACTIVE, active, oppositeEmitterState, this.field_145850_b, oppositeEmitterPos);
         ((FizzlerEmitterBlock)this.func_195044_w().func_177230_c()).setBlockStateValue(FizzlerEmitterBlock.ACTIVE, active, this.func_195044_w(), this.field_145850_b, this.func_174877_v());
         ((FizzlerEmitterBlock)oppositeEmitterState.func_177230_c()).updateAllNeighbors(this.field_145850_b, oppositeEmitterPos, oppositeEmitterState);
         ((FizzlerEmitterBlock)this.func_195044_w().func_177230_c()).updateAllNeighbors(this.field_145850_b, this.func_174877_v(), this.func_195044_w());
      }

   }

   public void setField(boolean active, int distance, Direction facing, Direction upDirection) {
      boolean rotated = (Boolean)this.func_195044_w().func_177229_b(FizzlerEmitterBlock.ROTATED);

      for(int i = 1; i < distance; ++i) {
         if (active) {
            BlockState fizzlerField = (BlockState)((BlockState)((Block)BlockInit.FIZZLER_FIELD.get()).func_176223_P().func_206870_a(FizzlerFieldBlock.ROTATED, rotated)).func_206870_a(FizzlerFieldBlock.AXIS, facing.func_176740_k());
            this.field_145850_b.func_180501_a(this.func_174877_v().func_177967_a(facing, i), fizzlerField, 2);
            this.field_145850_b.func_180501_a(this.func_174877_v().func_177967_a(facing, i).func_177972_a(upDirection), (BlockState)fizzlerField.func_206870_a(FizzlerFieldBlock.HALF, DoubleBlockHalf.UPPER), 2);
         } else {
            this.field_145850_b.func_180501_a(this.func_174877_v().func_177967_a(facing, i), Blocks.field_150350_a.func_176223_P(), 2);
            this.field_145850_b.func_180501_a(this.func_174877_v().func_177967_a(facing, i).func_177972_a(upDirection), Blocks.field_150350_a.func_176223_P(), 2);
         }
      }

   }

   public int distanceToOtherSide(Direction direction, Direction upDirection) {
      for(int i = 1; i <= 16; ++i) {
         BlockState lowerState = this.field_145850_b.func_180495_p(this.func_174877_v().func_177967_a(direction, i));
         BlockState upperState = this.field_145850_b.func_180495_p(this.func_174877_v().func_177967_a(direction, i).func_177972_a(upDirection));
         if (this.isOtherSide(lowerState, direction, false) && this.isOtherSide(upperState, direction, true)) {
            return i;
         }

         if (this.isBlockingField(lowerState, direction, false) || this.isBlockingField(upperState, direction, true)) {
            return 0;
         }
      }

      return 0;
   }

   public boolean isBlockingField(BlockState state, Direction direction, boolean upper) {
      if (!(state.func_177230_c() instanceof FizzlerFieldBlock)) {
         return !state.func_185904_a().func_76222_j();
      } else {
         return state.func_177229_b(FizzlerFieldBlock.AXIS) != direction.func_176740_k() || upper != (state.func_177229_b(FizzlerFieldBlock.HALF) == DoubleBlockHalf.UPPER);
      }
   }

   public boolean isOtherSide(BlockState state, Direction direction, boolean upper) {
      if (!(state.func_177230_c() instanceof FizzlerEmitterBlock)) {
         return false;
      } else {
         return state.func_177229_b(FizzlerEmitterBlock.FACING) == direction.func_176734_d() && upper == (state.func_177229_b(FizzlerEmitterBlock.HALF) == DoubleBlockHalf.UPPER);
      }
   }

   public List<BlockPos> getIndicatorPositions(BlockState state, World world, BlockPos pos) {
      Direction up = ((FizzlerEmitterBlock)state.func_177230_c()).getUpperDirection(state);
      Direction backwards = ((Direction)state.func_177229_b(FizzlerEmitterBlock.FACING)).func_176734_d();
      Vector3i perpendicular = up.func_176730_m().func_177955_d(backwards.func_176730_m());
      Direction side = Direction.func_218383_a(perpendicular.func_177958_n(), perpendicular.func_177956_o(), perpendicular.func_177952_p());
      return new ArrayList(Arrays.asList(pos.func_177972_a(side), pos.func_177972_a(side).func_177972_a(up), pos.func_177972_a(side.func_176734_d()), pos.func_177972_a(side.func_176734_d()).func_177972_a(up), pos.func_177972_a(backwards).func_177972_a(side), pos.func_177972_a(backwards).func_177972_a(side).func_177972_a(up), pos.func_177972_a(backwards).func_177972_a(side.func_176734_d()), pos.func_177972_a(backwards).func_177972_a(side.func_176734_d()).func_177972_a(up)));
   }
}
