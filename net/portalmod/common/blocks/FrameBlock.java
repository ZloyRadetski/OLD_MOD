package net.portalmod.common.blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;

public class FrameBlock extends Block implements IWaterLoggable {
   public static Set<Block> FRAME_BLOCKS = new HashSet();
   public static final DirectionProperty FACING;
   public static final BooleanProperty WATERLOGGED;
   public boolean isFilled;
   private static final Map<Direction, VoxelShapeGroup> FILLED_SHAPE;
   private static final Map<Direction, VoxelShapeGroup> HOLLOW_SHAPE;

   public FrameBlock(AbstractBlock.Properties properties, boolean isFilled) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.UP)).func_206870_a(WATERLOGGED, false));
      this.initAABBs();
      this.isFilled = isFilled;
   }

   public boolean func_200123_i(BlockState state, IBlockReader level, BlockPos pos) {
      return true;
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, WATERLOGGED});
   }

   private void initAABBs() {
      VoxelShapeGroup filledShape = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)0.0F, (double)2.0F, (double)16.0F, (double)16.0F).build();
      VoxelShapeGroup hollowShape = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)0.0F, (double)2.0F, (double)1.0F, (double)16.0F).add((double)0.0F, (double)15.0F, (double)0.0F, (double)2.0F, (double)16.0F, (double)16.0F).add((double)0.0F, (double)1.0F, (double)0.0F, (double)2.0F, (double)15.0F, (double)1.0F).add((double)0.0F, (double)1.0F, (double)15.0F, (double)2.0F, (double)15.0F, (double)16.0F).build();

      for(Direction facing : Direction.values()) {
         Mat4 matrix = Mat4.identity();
         matrix.translate(new Vec3((double)0.5F));
         if (facing.func_176740_k() == Axis.Y) {
            int angle = facing.func_176743_c() == AxisDirection.NEGATIVE ? 90 : -90;
            matrix.rotateDeg(Vector3f.field_229183_f_, (float)angle);
         } else {
            int angle = facing.func_176736_b() * -90 + 90;
            matrix.rotateDeg(Vector3f.field_229181_d_, (float)angle);
         }

         matrix.translate(new Vec3((double)-0.5F));
         FILLED_SHAPE.put(facing, filledShape.clone().transform(matrix));
         HOLLOW_SHAPE.put(facing, hollowShape.clone().transform(matrix));
      }

   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      if (FRAME_BLOCKS.isEmpty()) {
         DefaultedRegistry.field_212618_g.func_201756_e().filter((block) -> block instanceof FrameBlock).forEach((block) -> FRAME_BLOCKS.add(block));
      }

      boolean holdingFrame = FRAME_BLOCKS.stream().anyMatch((block) -> context.func_216375_a(block.func_199767_j()));
      return ((VoxelShapeGroup)(!this.isFilled && !holdingFrame ? HOLLOW_SHAPE : FILLED_SHAPE).get(state.func_177229_b(FACING))).getShape();
   }

   public VoxelShape func_220071_b(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      return this.isFilled ? this.func_220053_a(state, level, pos, context) : ((VoxelShapeGroup)HOLLOW_SHAPE.get(state.func_177229_b(FACING))).getShape();
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      Direction nearestLookingDirection = context.func_196010_d();
      return (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, context.func_195999_j().func_225608_bj_() ? nearestLookingDirection.func_176734_d() : nearestLookingDirection)).func_206870_a(WATERLOGGED, context.func_195991_k().func_204610_c(context.func_195995_a()).func_206886_c() == Fluids.field_204546_a);
   }

   public BlockState func_196271_a(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.func_177229_b(WATERLOGGED)) {
         p_196271_4_.func_205219_F_().func_205360_a(p_196271_5_, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(p_196271_4_));
      }

      return p_196271_1_;
   }

   public FluidState func_204507_t(BlockState blockState) {
      return (Boolean)blockState.func_177229_b(WATERLOGGED) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(blockState);
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      return this.func_185499_a(state, mirror.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      WATERLOGGED = BlockStateProperties.field_208198_y;
      FILLED_SHAPE = new HashMap();
      HOLLOW_SHAPE = new HashMap();
   }
}
