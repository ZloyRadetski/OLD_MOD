package net.portalmod.common.sorted.platform;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.portalmod.common.blocks.PortalableBlock;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class PlatformBlock extends BreakableBlock implements IWaterLoggable, PortalableBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<Half> HALF;
   public static final EnumProperty<Half> ORIGINAL_HALF;
   public static final BooleanProperty BEAM;
   public static final BooleanProperty WATERLOGGED;

   public PlatformBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.UP)).func_206870_a(HALF, Half.BOTTOM)).func_206870_a(ORIGINAL_HALF, Half.BOTTOM)).func_206870_a(BEAM, false)).func_206870_a(WATERLOGGED, false));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, HALF, BEAM, ORIGINAL_HALF, WATERLOGGED});
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
      boolean usedWrench = WrenchItem.usedWrench(player, hand);
      if (!usedWrench && !isBeamItem(player.func_184586_b(hand).func_77973_b())) {
         return ActionResultType.PASS;
      } else if (hasBeamBelow(state, world, pos)) {
         return ActionResultType.FAIL;
      } else {
         BlockState cycled = (BlockState)state.func_235896_a_(BEAM);
         world.func_175656_a(pos, cycled);
         player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.platform." + ((Boolean)cycled.func_177229_b(BEAM) ? "beam" : "normal")), true);
         if (usedWrench) {
            WrenchItem.playUseSound(world, Vector3d.func_237489_a_(pos));
         } else {
            player.func_184185_a(SoundEvents.field_187845_fY, 1.0F, 0.8F * ModUtil.randomSlightSoundPitch());
         }

         return ActionResultType.SUCCESS;
      }
   }

   public VoxelShape func_220053_a(BlockState blockState, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
      double raise = blockState.func_177229_b(HALF) == Half.BOTTOM ? (double)0.0F : (double)8.0F;
      VoxelShapeGroup SHAPE_PLATFORM = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)3.0F + raise, (double)0.0F, (double)16.0F, (double)8.0F + raise, (double)16.0F).build();
      VoxelShapeGroup SHAPE_BEAM = (new VoxelShapeGroup.Builder()).add((double)5.0F, (double)0.0F, (double)5.0F, (double)11.0F, (double)3.0F + raise, (double)11.0F).add((double)0.0F, (double)3.0F + raise, (double)0.0F, (double)16.0F, (double)8.0F + raise, (double)16.0F).build();
      VoxelShapeGroup combined = (Boolean)blockState.func_177229_b(BEAM) ? SHAPE_BEAM : SHAPE_PLATFORM;
      int angleX = blockState.func_177229_b(FACING) == Direction.UP ? 0 : (blockState.func_177229_b(FACING) == Direction.DOWN ? 180 : 90);
      int angleY = ((Direction)blockState.func_177229_b(FACING)).func_176736_b() * 90;
      Mat4 matrix = Mat4.identity();
      matrix.translate(new Vec3((double)0.5F));
      matrix.rotateDeg(Vector3f.field_229180_c_, (float)angleY);
      matrix.rotateDeg(Vector3f.field_229179_b_, (float)angleX);
      matrix.translate(new Vec3((double)-0.5F));
      return combined.transform(matrix).getShape();
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      boolean waterlogged = context.func_195991_k().func_204610_c(context.func_195995_a()).func_206886_c() == Fluids.field_204546_a;
      BlockState state = (BlockState)((BlockState)((BlockState)((BlockState)this.func_176223_P().func_206870_a(WATERLOGGED, waterlogged)).func_206870_a(FACING, this.getPlacementDirection(context))).func_206870_a(HALF, context.func_195999_j().func_225608_bj_() ? Half.BOTTOM : Half.TOP)).func_206870_a(ORIGINAL_HALF, context.func_195999_j().func_225608_bj_() ? Half.BOTTOM : Half.TOP);
      return (BlockState)state.func_206870_a(BEAM, hasBeamBelow(state, context.func_195991_k(), context.func_195995_a()));
   }

   public Direction getPlacementDirection(BlockItemUseContext context) {
      BlockState clickedState = context.func_195991_k().func_180495_p(context.func_195995_a().func_177972_a(context.func_196000_l().func_176734_d()));
      if (clickedState.func_177230_c() instanceof PlatformBlock) {
         Direction facing = (Direction)clickedState.func_177229_b(FACING);
         if (facing.func_176740_k() != context.func_196000_l().func_176740_k()) {
            return facing;
         }
      } else if (clickedState.func_177230_c() instanceof PlatformBeamBlock && ((Direction)clickedState.func_177229_b(FACING)).equals(context.func_196000_l())) {
         return context.func_196000_l();
      }

      return context.func_196010_d().func_176734_d();
   }

   public BlockState func_196271_a(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
      if ((Boolean)state.func_177229_b(WATERLOGGED)) {
         world.func_205219_F_().func_205360_a(pos, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(world));
      }

      return direction == ((Direction)state.func_177229_b(FACING)).func_176734_d() && hasBeamBelow(state, world, pos) ? (BlockState)state.func_206870_a(BEAM, true) : state;
   }

   public void func_220082_b(BlockState state, World world, BlockPos pos, BlockState oldState, boolean b) {
      world.func_190524_a(pos, this, pos);
   }

   public void func_220069_a(BlockState state, World level, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      boolean power = level.func_175640_z(pos);
      Half oldHalf = (Half)state.func_177229_b(HALF);
      Half newHalf = power ? Half.TOP : (Half)state.func_177229_b(ORIGINAL_HALF);
      if (newHalf != oldHalf) {
         level.func_175656_a(pos, (BlockState)state.func_206870_a(HALF, newHalf));
      }

   }

   public FluidState func_204507_t(BlockState blockState) {
      return (Boolean)blockState.func_177229_b(WATERLOGGED) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(blockState);
   }

   public static boolean hasBeamBelow(BlockState blockState, IWorld world, BlockPos pos) {
      BlockState belowState = world.func_180495_p(pos.func_177972_a(((Direction)blockState.func_177229_b(FACING)).func_176734_d()));
      return belowState.func_177230_c() instanceof PlatformBeamBlock && ((Direction)belowState.func_177229_b(FACING)).func_176740_k() == ((Direction)blockState.func_177229_b(FACING)).func_176740_k();
   }

   public static boolean isBeamItem(Item item) {
      return item instanceof BlockItem && ((BlockItem)item).func_179223_d() instanceof PlatformBeamBlock;
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      return this.func_185499_a(state, mirror.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   public void func_190948_a(ItemStack stack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("platform", list);
   }

   public boolean isPortalableOnFace(BlockState state, Direction face) {
      return state.func_177229_b(FACING) == face;
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      HALF = BlockStateProperties.field_208164_Q;
      ORIGINAL_HALF = EnumProperty.func_177709_a("original_half", Half.class);
      BEAM = BooleanProperty.func_177716_a("beam");
      WATERLOGGED = BlockStateProperties.field_208198_y;
   }
}
