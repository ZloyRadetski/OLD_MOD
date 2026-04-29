package net.portalmod.common.sorted.fizzler;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.blocks.DoubleBlock;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class FizzlerEmitterBlock extends DoubleBlock implements Fizzler {
   public static final DirectionProperty FACING;
   public static final BooleanProperty ROTATED;
   public static final BooleanProperty ACTIVE;

   public FizzlerEmitterBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.NORTH)).func_206870_a(ROTATED, false)).func_206870_a(ACTIVE, false)).func_206870_a(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShapeGroup getShapeGroup(BlockState state) {
      VoxelShapeGroup group = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)3.0F, (double)1.0F, (double)16.0F, (double)13.0F).addPart("active", VoxelShapes.func_197872_a(Block.func_208617_a((double)1.0F, (double)0.0F, (double)5.0F, (double)3.0F, (double)16.0F, (double)11.0F), Block.func_208617_a((double)1.0F, (double)0.0F, (double)6.5F, (double)4.5F, (double)15.0F, (double)9.5F))).addPart("field", (double)0.0F, (double)0.0F, (double)7.0F, (double)16.0F, (double)16.0F, (double)9.0F).build();
      Direction facing = (Direction)state.func_177229_b(FACING);
      boolean rotated = (Boolean)state.func_177229_b(ROTATED);
      boolean lower = state.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
      Mat4 matrix = Mat4.identity();
      matrix.translate(new Vec3((double)0.5F));
      if (facing.func_176740_k() == Axis.Y) {
         matrix.rotateDeg(Vector3f.field_229183_f_, facing == Direction.UP ? 90.0F : -90.0F);
         if (!rotated) {
            matrix.rotateDeg(Vector3f.field_229179_b_, facing == Direction.UP ? -90.0F : 90.0F);
         }

         if (!lower ^ facing.func_176743_c() == AxisDirection.NEGATIVE) {
            matrix.scale((double)1.0F, (double)-1.0F, (double)1.0F);
         }
      } else {
         int angle = facing.func_176736_b() * -90 - 90;
         matrix.rotateDeg(Vector3f.field_229181_d_, (float)angle);
         if (rotated) {
            matrix.rotateDeg(Vector3f.field_229179_b_, -90.0F);
            if (facing.func_176740_k() == Axis.X ^ facing.func_176743_c() == AxisDirection.NEGATIVE) {
               matrix.scale((double)1.0F, (double)-1.0F, (double)1.0F);
            }
         }

         if (lower) {
            matrix.scale((double)1.0F, (double)-1.0F, (double)1.0F);
         }
      }

      matrix.translate(new Vec3((double)-0.5F));
      return group.transform(matrix);
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      return this.getShapeGroup(state).getVariant((Boolean)state.func_177229_b(ACTIVE) ? "active" : "");
   }

   public VoxelShape getFieldShape(BlockState state) {
      return this.getShapeGroup(state).getPart("field");
   }

   public Direction getUpperDirection(BlockState state) {
      Direction.Axis axis = ((Direction)state.func_177229_b(FACING)).func_176740_k();
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
      return (Boolean)state.func_177229_b(ACTIVE) && this.getFieldShape(state).func_197752_a().func_186670_a(pos).func_72326_a(box);
   }

   public boolean isActive(BlockState state) {
      return (Boolean)state.func_177229_b(ACTIVE);
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      if ((Boolean)state.func_177229_b(ACTIVE)) {
         Direction direction = (Direction)state.func_177229_b(FACING);
         BlockState neighbor = world.func_180495_p(pos.func_177972_a(direction));
         if (!this.isValidConnection(state, neighbor)) {
            this.setBlockStateValue(ACTIVE, false, state, world, pos);
            this.updateAllNeighbors(world, pos, state);
         }
      }

   }

   public boolean isValidConnection(BlockState state, BlockState neighbor) {
      if (neighbor.func_177230_c() instanceof FizzlerFieldBlock) {
         return neighbor.func_177229_b(FizzlerFieldBlock.AXIS) == ((Direction)state.func_177229_b(FACING)).func_176740_k() && neighbor.func_177229_b(FizzlerFieldBlock.ROTATED) == state.func_177229_b(ROTATED) && neighbor.func_177229_b(FizzlerFieldBlock.HALF) == state.func_177229_b(HALF);
      } else if (!(neighbor.func_177230_c() instanceof FizzlerEmitterBlock)) {
         return false;
      } else {
         return neighbor.func_177229_b(FACING) == ((Direction)state.func_177229_b(FACING)).func_176734_d() && neighbor.func_177229_b(HALF) == state.func_177229_b(HALF) && neighbor.func_177229_b(ROTATED) == state.func_177229_b(ROTATED) && (Boolean)neighbor.func_177229_b(ACTIVE);
      }
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      Direction clickedFace = context.func_196000_l();
      BlockState state = (BlockState)this.func_176223_P().func_206870_a(FACING, clickedFace);
      if (clickedFace.func_176740_k() == Axis.Y) {
         boolean rotated = context.func_195992_f().func_176740_k() == Axis.X;
         Optional<DoubleBlockHalf> half = getPlacementHalf(context, Direction.func_211699_a(context.func_195992_f().func_176740_k(), AxisDirection.POSITIVE));
         return !half.isPresent() ? null : (BlockState)((BlockState)state.func_206870_a(HALF, (Comparable)half.get())).func_206870_a(ROTATED, rotated);
      } else {
         PlayerEntity player = context.func_195999_j();
         boolean prefersHorizontal = player != null && player.func_225608_bj_();
         Optional<DoubleBlockHalf> verticalTopHalf = getPlacementHalf(context, Direction.UP);
         Optional<DoubleBlockHalf> horizontalTopHalf = getPlacementHalf(context, Direction.func_211699_a(clickedFace.func_176746_e().func_176740_k(), AxisDirection.POSITIVE));
         if (!verticalTopHalf.isPresent() && !horizontalTopHalf.isPresent()) {
            return null;
         } else {
            boolean willBeHorizontal = prefersHorizontal && horizontalTopHalf.isPresent() || !verticalTopHalf.isPresent();
            return (BlockState)((BlockState)state.func_206870_a(HALF, willBeHorizontal ? (DoubleBlockHalf)horizontalTopHalf.get() : (DoubleBlockHalf)verticalTopHalf.get())).func_206870_a(ROTATED, willBeHorizontal);
         }
      }
   }

   public boolean lookDirectionInfluencesLocation() {
      return true;
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, ROTATED, ACTIVE, HALF});
   }

   public boolean hasTileEntity(BlockState state) {
      return this.isMainBlock(state) && ((Direction)state.func_177229_b(FACING)).func_176743_c() == AxisDirection.POSITIVE;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return this.hasTileEntity(state) ? ((TileEntityType)TileEntityTypeInit.FIZZLER_EMITTER.get()).func_200968_a() : null;
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      Direction upperDirection = this.getUpperDirection(state);
      Direction facing = (Direction)state.func_177229_b(FACING);
      if (facing.func_176740_k().func_200128_b() && ModUtil.getRotationAmount(rotation) % 2 == 1) {
         state = (BlockState)state.func_235896_a_(ROTATED);
      }

      if (upperDirection.func_176743_c() != rotation.func_185831_a(upperDirection).func_176743_c()) {
         state = (BlockState)state.func_235896_a_(HALF);
      }

      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a(facing));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      Direction upperDirection = this.getUpperDirection(state);
      Direction facing = (Direction)state.func_177229_b(FACING);
      if (mirror.func_185803_b(upperDirection) != upperDirection) {
         state = (BlockState)state.func_235896_a_(HALF);
      }

      if (mirror.func_185803_b(facing) != facing) {
         state = (BlockState)state.func_206870_a(FACING, facing.func_176734_d());
      }

      return state;
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("fizzler_emitter", list);
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      ROTATED = BooleanProperty.func_177716_a("rotated");
      ACTIVE = BooleanProperty.func_177716_a("active");
   }
}
