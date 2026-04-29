package net.portalmod.common.sorted.cubedropper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.blocks.MultiBlock;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.button.QuadBlockCorner;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.BiHashMap;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class CubeDropperBlock extends MultiBlock {
   public static final EnumProperty<QuadBlockCorner> CORNER = EnumProperty.func_177709_a("corner", QuadBlockCorner.class);
   public static final EnumProperty<DoubleBlockHalf> HALF;
   public static final BooleanProperty OPEN;
   private static final BiHashMap<DoubleBlockHalf, QuadBlockCorner, VoxelShapeGroup> SHAPE;

   public CubeDropperBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(CORNER, QuadBlockCorner.UP_LEFT)).func_206870_a(HALF, DoubleBlockHalf.UPPER)).func_206870_a(OPEN, false));
      this.initAABBs();
   }

   private void initAABBs() {
      VoxelShapeGroup upper = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)6.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)8.0F).add((double)0.0F, (double)6.0F, (double)0.0F, (double)8.0F, (double)16.0F, (double)16.0F).add((double)2.0F, (double)0.0F, (double)2.0F, (double)16.0F, (double)6.0F, (double)8.0F).add((double)2.0F, (double)0.0F, (double)2.0F, (double)8.0F, (double)6.0F, (double)16.0F).add((double)0.0F, (double)15.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F).build();
      VoxelShapeGroup lower = (new VoxelShapeGroup.Builder()).add((double)2.0F, (double)0.0F, (double)2.0F, (double)16.0F, (double)16.0F, (double)8.0F).add((double)2.0F, (double)0.0F, (double)2.0F, (double)8.0F, (double)16.0F, (double)16.0F).addPart("closed", (double)8.0F, (double)2.0F, (double)8.0F, (double)16.0F, (double)3.0F, (double)16.0F).build();

      for(QuadBlockCorner corner : QuadBlockCorner.values()) {
         for(DoubleBlockHalf half : DoubleBlockHalf.values()) {
            Mat4 matrix = Mat4.identity();
            matrix.translate(new Vec3((double)0.5F));
            matrix.rotateDeg(new Vector3f(0.0F, 1.0F, 0.0F), (float)(corner.getRot() - 90));
            matrix.translate(new Vec3((double)-0.5F));
            VoxelShapeGroup group = half == DoubleBlockHalf.LOWER ? lower : upper;
            SHAPE.put(half, corner, group.clone().transform(matrix));
         }
      }

   }

   public VoxelShape func_220053_a(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return ((VoxelShapeGroup)SHAPE.get(blockState.func_177229_b(HALF), blockState.func_177229_b(CORNER))).getVariant((Boolean)blockState.func_177229_b(OPEN) ? "" : "closed");
   }

   public BlockPos getMainPosition(BlockState blockState, BlockPos pos) {
      if (!((QuadBlockCorner)blockState.func_177229_b(CORNER)).isLeft()) {
         pos = pos.func_177976_e();
      }

      if (!((QuadBlockCorner)blockState.func_177229_b(CORNER)).isUp()) {
         pos = pos.func_177978_c();
      }

      if (blockState.func_177229_b(HALF) == DoubleBlockHalf.LOWER) {
         pos = pos.func_177984_a();
      }

      return pos;
   }

   public List<BlockPos> getConnectedPositions(BlockState mainState, BlockPos mainPos) {
      return new ArrayList(Arrays.asList(mainPos.func_177972_a(Direction.EAST), mainPos.func_177972_a(Direction.SOUTH), mainPos.func_177972_a(Direction.SOUTH).func_177972_a(Direction.EAST), mainPos.func_177977_b(), mainPos.func_177977_b().func_177972_a(Direction.EAST), mainPos.func_177977_b().func_177972_a(Direction.SOUTH), mainPos.func_177977_b().func_177972_a(Direction.SOUTH).func_177972_a(Direction.EAST)));
   }

   public Map<BlockPos, BlockState> getOtherParts(BlockState blockState, BlockPos pos) {
      QuadBlockCorner corner = (QuadBlockCorner)blockState.func_177229_b(CORNER);
      boolean isLower = blockState.func_177229_b(HALF) == DoubleBlockHalf.LOWER;
      boolean isLeft = corner.isLeft();
      boolean isUp = corner.isUp();
      Direction vertical = isLower ? Direction.UP : Direction.DOWN;
      Direction leftRight = isLeft ? Direction.EAST : Direction.WEST;
      Direction upDown = isUp ? Direction.SOUTH : Direction.NORTH;
      DoubleBlockHalf oppositeHalf = isLower ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER;
      QuadBlockCorner oppositeLeftRight = corner.mirrorLeftRight();
      QuadBlockCorner oppositeUpDown = corner.mirrorUpDown();
      QuadBlockCorner diagonal = corner.mirrorUpDown().mirrorLeftRight();
      HashMap<BlockPos, BlockState> map = new HashMap();
      map.put(pos.func_177972_a(leftRight), blockState.func_206870_a(CORNER, oppositeLeftRight));
      map.put(pos.func_177972_a(upDown), blockState.func_206870_a(CORNER, oppositeUpDown));
      map.put(pos.func_177972_a(upDown).func_177972_a(leftRight), blockState.func_206870_a(CORNER, diagonal));
      map.put(pos.func_177972_a(vertical), blockState.func_206870_a(HALF, oppositeHalf));
      map.put(pos.func_177972_a(vertical).func_177972_a(leftRight), ((BlockState)blockState.func_206870_a(HALF, oppositeHalf)).func_206870_a(CORNER, oppositeLeftRight));
      map.put(pos.func_177972_a(vertical).func_177972_a(upDown), ((BlockState)blockState.func_206870_a(HALF, oppositeHalf)).func_206870_a(CORNER, oppositeUpDown));
      map.put(pos.func_177972_a(vertical).func_177972_a(upDown).func_177972_a(leftRight), ((BlockState)blockState.func_206870_a(HALF, oppositeHalf)).func_206870_a(CORNER, diagonal));
      return map;
   }

   public boolean isSamePart(BlockState one, BlockState two) {
      return one.func_177229_b(HALF) == two.func_177229_b(HALF) && one.func_177229_b(CORNER) == two.func_177229_b(CORNER);
   }

   public void addMainBlockProperties(Map<Property<?>, Comparable<?>> map) {
      map.put(HALF, DoubleBlockHalf.UPPER);
      map.put(CORNER, QuadBlockCorner.UP_LEFT);
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      if (context.func_196000_l() != Direction.DOWN) {
         return null;
      } else {
         boolean prefersLeft = clickedOnPositiveHalf(context, Axis.X);
         boolean prefersUp = clickedOnPositiveHalf(context, Axis.Z);
         boolean[] flipUp = new boolean[]{false, false, true, true};
         boolean[] flipLeft = new boolean[]{false, true, false, true};

         for(int i = 0; i < 4; ++i) {
            QuadBlockCorner corner = QuadBlockCorner.getCorner(prefersUp ^ flipUp[i], prefersLeft ^ flipLeft[i]);
            if (this.isCornerPlaceable(context, corner)) {
               return (BlockState)this.func_176223_P().func_206870_a(CORNER, corner);
            }
         }

         return null;
      }
   }

   public boolean isCornerPlaceable(BlockItemUseContext context, QuadBlockCorner corner) {
      BlockPos topLeftPos = context.func_195995_a();
      if (!corner.isLeft()) {
         topLeftPos = topLeftPos.func_177976_e();
      }

      if (!corner.isUp()) {
         topLeftPos = topLeftPos.func_177978_c();
      }

      BlockPos[] topLeftOffsets = new BlockPos[]{topLeftPos, topLeftPos.func_177974_f(), topLeftPos.func_177968_d(), topLeftPos.func_177968_d().func_177974_f(), topLeftPos.func_177977_b(), topLeftPos.func_177977_b().func_177974_f(), topLeftPos.func_177977_b().func_177968_d(), topLeftPos.func_177977_b().func_177968_d().func_177974_f()};
      return Arrays.stream(topLeftOffsets).allMatch((pos) -> ModUtil.canPlaceAt(context, pos));
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      super.func_220069_a(state, world, pos, block, neighborPos, b);
      if (!world.field_72995_K) {
         boolean isPowered = this.getAllPositions(state, pos).stream().filter((checkingPos) -> {
            BlockState blockState = world.func_180495_p(checkingPos);
            return blockState.func_177230_c() instanceof CubeDropperBlock && blockState.func_177229_b(HALF) == DoubleBlockHalf.UPPER;
         }).anyMatch((checkingPos) -> world.func_175709_b(checkingPos.func_177984_a(), Direction.DOWN));
         if (isPowered) {
            TileEntity blockEntity = world.func_175625_s(this.getMainPosition(state, pos));
            if (blockEntity instanceof CubeDropperTileEntity) {
               ((CubeDropperTileEntity)blockEntity).resetDropper();
            }
         }

      }
   }

   public ActionResultType func_225533_a_(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
      if (world.field_72995_K) {
         return ActionResultType.PASS;
      } else {
         ItemStack itemStack = player.func_184586_b(hand);
         Item item = itemStack.func_77973_b();
         TileEntity tileEntity = world.func_175625_s(this.getMainPosition(blockState, pos));
         if (!(tileEntity instanceof CubeDropperTileEntity)) {
            return ActionResultType.FAIL;
         } else {
            CubeDropperTileEntity dropperEntity = (CubeDropperTileEntity)tileEntity;
            if (item instanceof SpawnEggItem) {
               dropperEntity.onEggClick(itemStack, player);
               player.func_184185_a(SoundEvents.field_187620_cL, 1.0F, 1.0F);
               return ActionResultType.SUCCESS;
            } else if (item instanceof WrenchItem) {
               dropperEntity.onWrenchClick(player);
               WrenchItem.playUseSound(world, result.func_216347_e());
               return ActionResultType.SUCCESS;
            } else {
               return ActionResultType.PASS;
            }
         }
      }
   }

   public void setOpen(boolean open, BlockState blockState, World world, BlockPos pos) {
      this.setBlockStateValue(OPEN, open, blockState, world, pos);
      world.func_184133_a((PlayerEntity)null, pos, open ? (SoundEvent)SoundInit.CUBE_DROPPER_OPEN.get() : (SoundEvent)SoundInit.CUBE_DROPPER_CLOSE.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSlightSoundPitch());
   }

   public void func_196243_a(BlockState blockState, World world, BlockPos pos, BlockState newState, boolean b) {
      if (this.isMainBlock(blockState) && !blockState.func_203425_a(newState.func_177230_c())) {
         TileEntity blockEntity = world.func_175625_s(pos);
         if (blockEntity instanceof CubeDropperTileEntity) {
            ((CubeDropperTileEntity)blockEntity).onRemove();
         }
      }

      super.func_196243_a(blockState, world, pos, newState, b);
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{CORNER, HALF, OPEN});
   }

   public boolean hasTileEntity(BlockState state) {
      return this.isMainBlock(state);
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return this.isMainBlock(state) ? ((TileEntityType)TileEntityTypeInit.CUBE_DROPPER.get()).func_200968_a() : null;
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      int times = ModUtil.getRotationAmount(rotation);
      return (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).rotate(times));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      switch (mirror) {
         case FRONT_BACK:
            return (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).mirrorLeftRight());
         case LEFT_RIGHT:
            return (BlockState)state.func_206870_a(CORNER, ((QuadBlockCorner)state.func_177229_b(CORNER)).mirrorUpDown());
         default:
            return state;
      }
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("cube_dropper", list);
   }

   static {
      HALF = BlockStateProperties.field_208163_P;
      OPEN = BlockStateProperties.field_208193_t;
      SHAPE = new BiHashMap<DoubleBlockHalf, QuadBlockCorner, VoxelShapeGroup>();
   }
}
