package net.portalmod.common.sorted.door;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.blocks.MultiBlock;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class ChamberDoorBlock extends MultiBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty OPEN;
   public static final EnumProperty<DoubleBlockHalf> HALF;
   public static final EnumProperty<Side> SIDE;
   private static final VoxelShapeGroup UPPER;
   private static final VoxelShapeGroup LOWER;

   public ChamberDoorBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.NORTH)).func_206870_a(OPEN, false)).func_206870_a(HALF, DoubleBlockHalf.LOWER)).func_206870_a(SIDE, ChamberDoorBlock.Side.LEFT));
   }

   public BlockPos getMainPosition(BlockState blockState, BlockPos pos) {
      if (blockState.func_177229_b(SIDE) == ChamberDoorBlock.Side.RIGHT) {
         pos = pos.func_177972_a(((Direction)blockState.func_177229_b(FACING)).func_176746_e());
      }

      if (blockState.func_177229_b(HALF) == DoubleBlockHalf.UPPER) {
         pos = pos.func_177972_a(Direction.DOWN);
      }

      return pos;
   }

   public List<BlockPos> getConnectedPositions(BlockState mainState, BlockPos mainPos) {
      Direction horizontal = ((Direction)mainState.func_177229_b(FACING)).func_176735_f();
      return new ArrayList(Arrays.asList(mainPos.func_177984_a(), mainPos.func_177984_a().func_177972_a(horizontal), mainPos.func_177972_a(horizontal)));
   }

   public Map<BlockPos, BlockState> getOtherParts(BlockState blockState, BlockPos pos) {
      Direction facing = (Direction)blockState.func_177229_b(FACING);
      boolean isLower = blockState.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
      boolean isLeft = blockState.func_177229_b(SIDE) == ChamberDoorBlock.Side.LEFT;
      Direction vertical = isLower ? Direction.UP : Direction.DOWN;
      Direction horizontal = isLeft ? facing.func_176735_f() : facing.func_176746_e();
      DoubleBlockHalf oppositeHalf = isLower ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER;
      Side oppositeSide = isLeft ? ChamberDoorBlock.Side.RIGHT : ChamberDoorBlock.Side.LEFT;
      HashMap<BlockPos, BlockState> map = new HashMap();
      map.put(pos.func_177972_a(vertical), blockState.func_206870_a(HALF, oppositeHalf));
      map.put(pos.func_177972_a(horizontal), blockState.func_206870_a(SIDE, oppositeSide));
      map.put(pos.func_177972_a(horizontal).func_177972_a(vertical), ((BlockState)blockState.func_206870_a(SIDE, oppositeSide)).func_206870_a(HALF, oppositeHalf));
      return map;
   }

   public boolean isSamePart(BlockState one, BlockState two) {
      return one.func_177229_b(FACING) == two.func_177229_b(FACING) && one.func_177229_b(HALF) == two.func_177229_b(HALF) && one.func_177229_b(SIDE) == two.func_177229_b(SIDE);
   }

   public void addMainBlockProperties(Map<Property<?>, Comparable<?>> map) {
      map.put(HALF, DoubleBlockHalf.LOWER);
      map.put(SIDE, ChamberDoorBlock.Side.LEFT);
   }

   public boolean lookDirectionInfluencesLocation() {
      return true;
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, OPEN, HALF, SIDE});
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      int facing = ((Direction)state.func_177229_b(FACING)).func_176736_b() * 90;
      int side = state.func_177229_b(SIDE) == ChamberDoorBlock.Side.LEFT ? 0 : 180;
      VoxelShapeGroup shapeGroup = state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? UPPER : LOWER;
      Mat4 matrix = Mat4.identity();
      matrix.translate(new Vec3((double)0.5F));
      matrix.rotateDeg(Vector3f.field_229180_c_, (float)(facing + side));
      matrix.translate(new Vec3((double)-0.5F));
      return shapeGroup.clone().transform(matrix).getVariant((Boolean)state.func_177229_b(OPEN) ? "open" : "closed");
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      World world = context.func_195991_k();
      BlockPos pos = context.func_195995_a();
      Direction facing = context.func_195992_f();
      Direction clickedFace = context.func_196000_l();
      BlockState rotated = (BlockState)this.func_176223_P().func_206870_a(FACING, facing.func_176734_d());
      List<Tuple<DoubleBlockHalf, Side>> possibleStates = new ArrayList();
      if (canPlace(new BlockPos[]{pos.func_177984_a(), pos.func_177972_a(facing.func_176746_e()), pos.func_177972_a(facing.func_176746_e()).func_177984_a()}, context, world)) {
         possibleStates.add(new Tuple(DoubleBlockHalf.LOWER, ChamberDoorBlock.Side.LEFT));
      }

      if (canPlace(new BlockPos[]{pos.func_177977_b(), pos.func_177972_a(facing.func_176746_e()), pos.func_177972_a(facing.func_176746_e()).func_177977_b()}, context, world)) {
         possibleStates.add(new Tuple(DoubleBlockHalf.UPPER, ChamberDoorBlock.Side.LEFT));
      }

      if (canPlace(new BlockPos[]{pos.func_177984_a(), pos.func_177972_a(facing.func_176735_f()), pos.func_177972_a(facing.func_176735_f()).func_177984_a()}, context, world)) {
         possibleStates.add(new Tuple(DoubleBlockHalf.LOWER, ChamberDoorBlock.Side.RIGHT));
      }

      if (canPlace(new BlockPos[]{pos.func_177977_b(), pos.func_177972_a(facing.func_176735_f()), pos.func_177972_a(facing.func_176735_f()).func_177977_b()}, context, world)) {
         possibleStates.add(new Tuple(DoubleBlockHalf.UPPER, ChamberDoorBlock.Side.RIGHT));
      }

      if (possibleStates.isEmpty()) {
         return null;
      } else {
         if (clickedFace == Direction.UP) {
            Vector3d clickOffset = context.func_221532_j().func_178788_d(Vector3d.func_237489_a_(pos));
            double perpendicular = facing.func_176740_k() == Axis.X ? (facing.func_176743_c() == AxisDirection.POSITIVE ? clickOffset.field_72449_c : -clickOffset.field_72449_c) : (facing.func_176743_c() == AxisDirection.POSITIVE ? -clickOffset.field_72450_a : clickOffset.field_72450_a);
            Tuple<DoubleBlockHalf, Side> preferredState = new Tuple(DoubleBlockHalf.LOWER, perpendicular > (double)0.0F ? ChamberDoorBlock.Side.LEFT : ChamberDoorBlock.Side.RIGHT);
            if (possibleStates.stream().anyMatch((tuple) -> tuple.func_76341_a() == preferredState.func_76341_a() && tuple.func_76340_b() == preferredState.func_76340_b())) {
               return (BlockState)((BlockState)rotated.func_206870_a(HALF, (Comparable)preferredState.func_76341_a())).func_206870_a(SIDE, (Comparable)preferredState.func_76340_b());
            }
         }

         return (BlockState)((BlockState)rotated.func_206870_a(HALF, (Comparable)((Tuple)possibleStates.get(0)).func_76341_a())).func_206870_a(SIDE, (Comparable)((Tuple)possibleStates.get(0)).func_76340_b());
      }
   }

   public static boolean canPlace(BlockPos[] posArray, BlockItemUseContext context, World world) {
      for(BlockPos pos : posArray) {
         if (!ModUtil.canPlaceAt(context, pos)) {
            return false;
         }
      }

      return true;
   }

   public void setOpen(boolean open, BlockState blockState, World world, BlockPos pos) {
      this.setBlockStateValue(OPEN, open, blockState, world, pos);
      this.updateAllNeighbors(world, pos, blockState);
      playSound(open, blockState, world, pos);
   }

   public static void playSound(boolean open, BlockState blockState, World world, BlockPos pos) {
      Vector3d middlePos = getExactMiddlePos(blockState, pos);
      world.func_184148_a((PlayerEntity)null, middlePos.field_72450_a, middlePos.field_72448_b, middlePos.field_72449_c, open ? (SoundEvent)SoundInit.CHAMBER_DOOR_OPEN.get() : (SoundEvent)SoundInit.CHAMBER_DOOR_CLOSE.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSoundPitch());
   }

   public static Vector3d getExactMiddlePos(BlockState state, BlockPos pos) {
      Direction facing = (Direction)state.func_177229_b(FACING);
      return Vector3d.func_237492_c_(pos).func_178787_e((new Vec3(facing.func_176735_f().func_176730_m())).mul((double)0.5F).add((double)0.0F, (double)1.0F, (double)0.0F).to3d());
   }

   public boolean func_149740_M(BlockState state) {
      return true;
   }

   public int func_180641_l(BlockState state, World world, BlockPos pos) {
      TileEntity blockEntity = world.func_175625_s(this.getMainPosition(state, pos));
      if ((Boolean)state.func_177229_b(OPEN) && blockEntity instanceof ChamberDoorTileEntity) {
         return ((ChamberDoorTileEntity)blockEntity).isAutomatic() ? 15 : 0;
      } else {
         return 0;
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return this.isMainBlock(state);
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return this.isMainBlock(state) ? ((TileEntityType)TileEntityTypeInit.CHAMBER_DOOR.get()).func_200968_a() : null;
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      Direction facing = (Direction)state.func_177229_b(FACING);
      if (mirror == Mirror.NONE) {
         return state;
      } else {
         BlockState sideFlipped = (BlockState)state.func_206870_a(SIDE, ((Side)state.func_177229_b(SIDE)).flip());
         return mirror.func_185803_b(facing) == facing ? sideFlipped : (BlockState)sideFlipped.func_206870_a(FACING, facing.func_176734_d());
      }
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("chamber_door", list);
   }

   static {
      FACING = HorizontalBlock.field_185512_D;
      OPEN = BlockStateProperties.field_208193_t;
      HALF = BlockStateProperties.field_208163_P;
      SIDE = EnumProperty.func_177709_a("side", Side.class);
      UPPER = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)3.0F, (double)2.0F, (double)16.0F, (double)13.0F).add((double)0.0F, (double)14.0F, (double)3.0F, (double)16.0F, (double)16.0F, (double)13.0F).addPart("closed", (double)2.0F, (double)0.0F, (double)5.0F, (double)16.0F, (double)14.0F, (double)11.0F).addPart("open", (double)2.0F, (double)0.0F, (double)5.0F, (double)5.0F, (double)14.0F, (double)11.0F).build();
      LOWER = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)3.0F, (double)2.0F, (double)16.0F, (double)13.0F).addPart("closed", (double)0.0F, (double)0.0F, (double)5.0F, (double)16.0F, (double)16.0F, (double)11.0F).addPart("open", (double)0.0F, (double)0.0F, (double)5.0F, (double)5.0F, (double)16.0F, (double)11.0F).build();
   }

   public static enum Side implements IStringSerializable {
      LEFT,
      RIGHT;

      public String toString() {
         return this.func_176610_l();
      }

      public String func_176610_l() {
         return this == LEFT ? "left" : "right";
      }

      public Side flip() {
         return this == LEFT ? RIGHT : LEFT;
      }
   }
}
