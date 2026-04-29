package net.portalmod.common.sorted.laser;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
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
import net.minecraft.world.IBlockReader;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;

public class LaserCatcherBlock extends Block {
   public static final DirectionProperty FACING;
   public static final BooleanProperty ACTIVE;
   private static final Map<Direction, VoxelShapeGroup> SHAPES;

   public LaserCatcherBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.UP)).func_206870_a(ACTIVE, false));
      this.initAABBs();
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, ACTIVE});
   }

   private void initAABBs() {
      VoxelShapeGroup shape = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)1.0F, (double)16.0F).add((double)3.0F, (double)0.0F, (double)3.0F, (double)13.0F, (double)3.0F, (double)13.0F).build();

      for(Direction facing : Direction.values()) {
         Mat4 matrix = Mat4.identity();
         matrix.translate(new Vec3((double)0.5F));
         if (facing.func_176743_c() == AxisDirection.POSITIVE) {
            matrix.scale((new Vec3(facing.func_176730_m())).mul((double)2.0F).add((double)1.0F));
         }

         if (facing.func_176740_k() == Axis.X) {
            matrix.rotateDeg(Vector3f.field_229183_f_, -90.0F);
         }

         if (facing.func_176740_k() == Axis.Z) {
            matrix.rotateDeg(Vector3f.field_229179_b_, 90.0F);
         }

         matrix.translate(new Vec3((double)-0.5F));
         SHAPES.put(facing, shape.clone().transform(matrix));
      }

   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
      return ((VoxelShapeGroup)SHAPES.get(state.func_177229_b(FACING))).getShape();
   }

   public PushReaction func_149656_h(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      return state.func_185907_a(mirror.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      ACTIVE = BooleanProperty.func_177716_a("active");
      SHAPES = new HashMap();
   }
}
