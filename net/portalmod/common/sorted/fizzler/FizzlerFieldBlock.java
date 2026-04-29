package net.portalmod.common.sorted.fizzler;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.blocks.DoubleBlock;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;

public class FizzlerFieldBlock extends DoubleBlock implements Fizzler {
   public static final EnumProperty<Direction.Axis> AXIS;
   public static final BooleanProperty ROTATED;

   public FizzlerFieldBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(AXIS, Axis.X)).func_206870_a(ROTATED, false)).func_206870_a(HALF, DoubleBlockHalf.LOWER));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{AXIS, ROTATED, HALF});
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.func_197880_a();
   }

   public VoxelShape getFieldShape(BlockState state) {
      VoxelShapeGroup group = (new VoxelShapeGroup.Builder()).add((double)7.0F, (double)0.0F, (double)0.0F, (double)9.0F, (double)16.0F, (double)16.0F).build();
      Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
      boolean rotated = (Boolean)state.func_177229_b(ROTATED);
      Mat4 matrix = Mat4.identity();
      matrix.translate(new Vec3((double)0.5F));
      if (axis == Axis.X) {
         matrix.rotateDeg(Vector3f.field_229181_d_, 90.0F);
      }

      if (axis == Axis.Y) {
         matrix.rotateDeg(Vector3f.field_229179_b_, 90.0F);
      }

      if (rotated) {
         matrix.rotateDeg(Vector3f.field_229183_f_, 90.0F);
      }

      matrix.translate(new Vec3((double)-0.5F));
      return group.transform(matrix).getShape();
   }

   public Direction getUpperDirection(BlockState state) {
      Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
      boolean rotated = (Boolean)state.func_177229_b(ROTATED);
      if (axis == Axis.Y) {
         return rotated ? Direction.EAST : Direction.SOUTH;
      } else if (axis == Axis.X) {
         return rotated ? Direction.SOUTH : Direction.UP;
      } else {
         return rotated ? Direction.EAST : Direction.UP;
      }
   }

   public boolean isInsideField(AxisAlignedBB box, BlockPos pos, BlockState state) {
      return this.getFieldShape(state).func_197752_a().func_186670_a(pos).func_72326_a(box);
   }

   public boolean isActive(BlockState state) {
      return true;
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      Direction facing = Direction.func_211699_a((Direction.Axis)state.func_177229_b(AXIS), AxisDirection.POSITIVE);
      BlockState leftBlock = world.func_180495_p(pos.func_177972_a(facing));
      BlockState rightBlock = world.func_180495_p(pos.func_177972_a(facing.func_176734_d()));
      if (!this.isValidConnection(state, leftBlock, facing) || !this.isValidConnection(state, rightBlock, facing.func_176734_d())) {
         world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
      }

   }

   public boolean isValidConnection(BlockState state, BlockState neighbor, Direction direction) {
      if (neighbor.func_177230_c() instanceof FizzlerFieldBlock) {
         return neighbor.func_177229_b(AXIS) == state.func_177229_b(AXIS) && neighbor.func_177229_b(ROTATED) == state.func_177229_b(ROTATED) && neighbor.func_177229_b(HALF) == state.func_177229_b(HALF);
      } else if (!(neighbor.func_177230_c() instanceof FizzlerEmitterBlock)) {
         return false;
      } else {
         return neighbor.func_177229_b(FizzlerEmitterBlock.FACING) == direction.func_176734_d() && neighbor.func_177229_b(FizzlerEmitterBlock.ROTATED) == state.func_177229_b(ROTATED) && neighbor.func_177229_b(FizzlerEmitterBlock.HALF) == state.func_177229_b(HALF) && (Boolean)neighbor.func_177229_b(FizzlerEmitterBlock.ACTIVE);
      }
   }

   static {
      AXIS = BlockStateProperties.field_208148_A;
      ROTATED = BooleanProperty.func_177716_a("rotated");
   }
}
