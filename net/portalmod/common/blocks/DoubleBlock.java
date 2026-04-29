package net.portalmod.common.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.portalmod.core.util.ModUtil;

public abstract class DoubleBlock extends MultiBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF;

   public DoubleBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public abstract Direction getUpperDirection(BlockState var1);

   public BlockPos getMainPosition(BlockState blockState, BlockPos pos) {
      return blockState.func_177229_b(HALF) == DoubleBlockHalf.LOWER ? pos : pos.func_177972_a(this.getUpperDirection(blockState).func_176734_d());
   }

   public List<BlockPos> getConnectedPositions(BlockState mainState, BlockPos mainPos) {
      return new ArrayList(Collections.singletonList(mainPos.func_177972_a(this.getUpperDirection(mainState))));
   }

   public Map<BlockPos, BlockState> getOtherParts(BlockState blockState, BlockPos pos) {
      boolean isLower = blockState.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
      Direction direction = this.getUpperDirection(blockState);
      HashMap<BlockPos, BlockState> map = new HashMap();
      map.put(pos.func_177972_a(isLower ? direction : direction.func_176734_d()), blockState.func_206870_a(HALF, isLower ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER));
      return map;
   }

   public boolean isSamePart(BlockState one, BlockState two) {
      return one.func_177229_b(HALF) == two.func_177229_b(HALF) && this.getUpperDirection(one) == this.getUpperDirection(two);
   }

   public void addMainBlockProperties(Map<Property<?>, Comparable<?>> map) {
      map.put(HALF, DoubleBlockHalf.LOWER);
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)getPlacementHalf(context, Direction.UP).map((doubleBlockHalf) -> (BlockState)this.func_176223_P().func_206870_a(HALF, doubleBlockHalf)).orElse((Object)null);
   }

   public static Optional<DoubleBlockHalf> getPlacementHalf(BlockItemUseContext context, Direction upDirection) {
      BlockPos pos = context.func_195995_a();
      boolean placedOnUpperSide = clickedOnPositiveHalf(context, upDirection);
      boolean canBeLower = ModUtil.canPlaceAt(context, pos.func_177972_a(upDirection));
      boolean canBeUpper = ModUtil.canPlaceAt(context, pos.func_177972_a(upDirection.func_176734_d()));
      if (!placedOnUpperSide && canBeUpper) {
         return Optional.of(DoubleBlockHalf.UPPER);
      } else if (placedOnUpperSide && canBeLower) {
         return Optional.of(DoubleBlockHalf.LOWER);
      } else if (canBeUpper) {
         return Optional.of(DoubleBlockHalf.UPPER);
      } else {
         return canBeLower ? Optional.of(DoubleBlockHalf.LOWER) : Optional.empty();
      }
   }

   static {
      HALF = BlockStateProperties.field_208163_P;
   }
}
