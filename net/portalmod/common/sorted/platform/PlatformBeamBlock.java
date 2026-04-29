package net.portalmod.common.sorted.platform;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.portalmod.common.blocks.CustomPushBehavior;
import net.portalmod.core.util.ModUtil;

public class PlatformBeamBlock extends Block implements IWaterLoggable, CustomPushBehavior {
   public static final VoxelShape SHAPE_X = Block.func_208617_a((double)0.0F, (double)5.0F, (double)5.0F, (double)16.0F, (double)11.0F, (double)11.0F);
   public static final VoxelShape SHAPE_Y = Block.func_208617_a((double)5.0F, (double)0.0F, (double)5.0F, (double)11.0F, (double)16.0F, (double)11.0F);
   public static final VoxelShape SHAPE_Z = Block.func_208617_a((double)5.0F, (double)5.0F, (double)0.0F, (double)11.0F, (double)11.0F, (double)16.0F);
   public static final VoxelShape COLLISION_SHAPE_X = Block.func_208617_a((double)0.0F, (double)4.5F, (double)4.5F, (double)16.0F, (double)11.5F, (double)11.5F);
   public static final VoxelShape COLLISION_SHAPE_Y = Block.func_208617_a((double)4.5F, (double)0.0F, (double)4.5F, (double)11.5F, (double)16.0F, (double)11.5F);
   public static final VoxelShape COLLISION_SHAPE_Z = Block.func_208617_a((double)4.5F, (double)4.5F, (double)0.0F, (double)11.5F, (double)11.5F, (double)16.0F);
   public static final DirectionProperty FACING;
   public static final BooleanProperty WATERLOGGED;

   public PlatformBeamBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(WATERLOGGED, false)).func_206870_a(FACING, Direction.UP));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{WATERLOGGED, FACING});
   }

   public VoxelShape func_220071_b(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
      switch (((Direction)state.func_177229_b(FACING)).func_176740_k()) {
         case X:
            return COLLISION_SHAPE_X;
         case Y:
            return COLLISION_SHAPE_Y;
         default:
            return COLLISION_SHAPE_Z;
      }
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
      switch (((Direction)state.func_177229_b(FACING)).func_176740_k()) {
         case X:
            return SHAPE_X;
         case Y:
            return SHAPE_Y;
         default:
            return SHAPE_Z;
      }
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)((BlockState)this.func_176223_P().func_206870_a(WATERLOGGED, context.func_195991_k().func_204610_c(context.func_195995_a()).func_206886_c() == Fluids.field_204546_a)).func_206870_a(FACING, this.getPlacementDirection(context));
   }

   public Direction getPlacementDirection(BlockItemUseContext context) {
      BlockState clickedState = context.func_195991_k().func_180495_p(context.func_195995_a().func_177972_a(context.func_196000_l().func_176734_d()));
      if (clickedState.func_177230_c() instanceof PlatformBeamBlock) {
         Direction facing = (Direction)clickedState.func_177229_b(FACING);
         if (facing.func_176740_k() == context.func_196000_l().func_176740_k()) {
            return facing;
         }
      }

      if (clickedState.func_177230_c() instanceof PlatformBlock) {
         Direction facing = (Direction)clickedState.func_177229_b(PlatformBlock.FACING);
         if (facing.func_176734_d() == context.func_196000_l()) {
            return facing;
         }
      }

      return context.func_196000_l();
   }

   public BlockState func_196271_a(BlockState blockState, Direction direction, BlockState state, IWorld world, BlockPos pos, BlockPos pos2) {
      if ((Boolean)blockState.func_177229_b(WATERLOGGED)) {
         world.func_205219_F_().func_205360_a(pos, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(world));
      }

      return blockState;
   }

   public FluidState func_204507_t(BlockState blockState) {
      return (Boolean)blockState.func_177229_b(WATERLOGGED) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(blockState);
   }

   public boolean isStickyBlock(BlockState state) {
      return true;
   }

   public boolean canStickTo(BlockState state, BlockState other) {
      return other.isStickyBlock();
   }

   public boolean isStickyToNeighbor(World level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
      return dir.func_176740_k() == ((Direction)state.func_177229_b(FACING)).func_176740_k();
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      return this.func_185499_a(state, mirror.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   public void func_190948_a(ItemStack stack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("platform_beam", list);
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      WATERLOGGED = BlockStateProperties.field_208198_y;
   }
}
