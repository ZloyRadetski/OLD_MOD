package net.portalmod.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.portalmod.common.sorted.button.QuadBlockCorner;
import net.portalmod.core.util.ModUtil;

public class QuadBlock extends MultiBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<QuadBlockCorner> CORNER;

   public QuadBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public BlockPos getMainPosition(BlockState blockState, BlockPos pos) {
      QuadBlockCorner corner = (QuadBlockCorner)blockState.func_177229_b(CORNER);
      Direction facing = (Direction)blockState.func_177229_b(FACING);
      Direction horizontal = facing == Direction.UP ? Direction.WEST : (facing == Direction.DOWN ? Direction.EAST : facing.func_176746_e());
      Direction vertical = facing.func_176740_k() == Axis.Y ? Direction.NORTH : Direction.UP;
      if (!corner.isLeft()) {
         pos = pos.func_177972_a(horizontal);
      }

      if (!corner.isUp()) {
         pos = pos.func_177972_a(vertical);
      }

      return pos;
   }

   public List<BlockPos> getConnectedPositions(BlockState mainState, BlockPos mainPos) {
      Direction facing = (Direction)mainState.func_177229_b(FACING);
      Direction horizontal = facing == Direction.UP ? Direction.EAST : (facing == Direction.DOWN ? Direction.WEST : facing.func_176735_f());
      Direction vertical = facing.func_176740_k() == Axis.Y ? Direction.SOUTH : Direction.DOWN;
      return new ArrayList(Arrays.asList(mainPos.func_177972_a(horizontal), mainPos.func_177972_a(horizontal).func_177972_a(vertical), mainPos.func_177972_a(vertical)));
   }

   public Map<BlockPos, BlockState> getOtherParts(BlockState blockState, BlockPos pos) {
      QuadBlockCorner base = (QuadBlockCorner)blockState.func_177229_b(CORNER);
      Direction facing = (Direction)blockState.func_177229_b(FACING);
      Map<BlockPos, BlockState> map = new HashMap();

      for(QuadBlockCorner corner : QuadBlockCorner.values()) {
         if (corner != base) {
            map.put(this.getOtherBlock(pos, base, corner, facing), blockState.func_206870_a(CORNER, corner));
         }
      }

      return map;
   }

   public boolean isSamePart(BlockState one, BlockState two) {
      return one.func_177229_b(FACING) == two.func_177229_b(FACING) && one.func_177229_b(CORNER) == two.func_177229_b(CORNER);
   }

   public void addMainBlockProperties(Map<Property<?>, Comparable<?>> map) {
      map.put(CORNER, QuadBlockCorner.UP_LEFT);
   }

   public List<BlockPos> getAllBlocks(BlockPos pos, QuadBlockCorner base, Direction facing) {
      List<BlockPos> poses = new ArrayList();

      for(QuadBlockCorner corner : QuadBlockCorner.values()) {
         poses.add(this.getOtherBlock(pos, base, corner, facing));
      }

      return poses;
   }

   public BlockPos getOtherBlock(BlockPos pos, QuadBlockCorner base, QuadBlockCorner corner, Direction facing) {
      Tuple<Direction, Direction> directions = this.placementDirectionsFromFacing(facing.func_176740_k());
      Direction a = (Direction)directions.func_76341_a();
      Direction b = (Direction)directions.func_76340_b();
      int x = corner.getX() - base.getX();
      int y = corner.getY() - base.getY();
      if (facing.func_176743_c() == AxisDirection.NEGATIVE) {
         x *= -1;
      }

      BlockPos newPos = new BlockPos(pos);
      if (x != 0) {
         newPos = newPos.func_177972_a(x < 0 ? a.func_176734_d() : a);
      }

      if (y != 0) {
         newPos = newPos.func_177972_a(y < 0 ? b.func_176734_d() : b);
      }

      return newPos;
   }

   public Tuple<Direction, Direction> placementDirectionsFromFacing(Direction.Axis axis) {
      if (axis == Axis.X) {
         return new Tuple(Direction.NORTH, Direction.UP);
      } else {
         return axis == Axis.Z ? new Tuple(Direction.EAST, Direction.UP) : new Tuple(Direction.EAST, Direction.NORTH);
      }
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      Direction direction = context.func_196000_l();
      Direction.Axis axis = direction.func_176740_k();
      boolean isPositive = direction.func_176743_c() == AxisDirection.POSITIVE;
      Direction upDirection = axis == Axis.Y ? Direction.NORTH : Direction.UP;
      Direction leftDirection = axis == Axis.Y ? (isPositive ? Direction.WEST : Direction.EAST) : direction.func_176746_e();
      boolean prefersUp = clickedOnPositiveHalf(context, upDirection.func_176734_d());
      boolean prefersLeft = clickedOnPositiveHalf(context, leftDirection.func_176734_d());
      boolean[] flipUp = new boolean[]{false, false, true, true};
      boolean[] flipLeft = new boolean[]{false, true, false, true};

      for(int i = 0; i < 4; ++i) {
         QuadBlockCorner corner = QuadBlockCorner.getCorner(prefersUp ^ flipUp[i], prefersLeft ^ flipLeft[i]);
         if (this.isCornerPlaceable(context, corner)) {
            return (BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, direction)).func_206870_a(CORNER, corner);
         }
      }

      return null;
   }

   public boolean isCornerPlaceable(BlockItemUseContext context, QuadBlockCorner corner) {
      return this.getAllBlocks(context.func_195995_a(), corner, context.func_196000_l()).stream().allMatch((pos) -> ModUtil.canPlaceAt(context, pos));
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      Direction facing = (Direction)state.func_177229_b(FACING);
      if (facing.func_176740_k() == Axis.Y) {
         int times = ModUtil.getRotationAmount(rotation);
         if (facing == Direction.DOWN) {
            times = 4 - times;
         }

         return (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).rotate(times));
      } else {
         return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a(facing));
      }
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      Direction facing = (Direction)state.func_177229_b(FACING);
      if (facing.func_176740_k() == Axis.Y) {
         switch (mirror) {
            case FRONT_BACK:
               return (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).mirrorLeftRight());
            case LEFT_RIGHT:
               return (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).mirrorUpDown());
            default:
               return state;
         }
      } else if (mirror == Mirror.NONE) {
         return state;
      } else {
         BlockState sideFlipped = (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).mirrorLeftRight());
         return mirror.func_185803_b(facing) == facing ? sideFlipped : (BlockState)sideFlipped.func_206870_a(FACING, facing.func_176734_d());
      }
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      CORNER = EnumProperty.func_177709_a("corner", QuadBlockCorner.class);
   }
}
