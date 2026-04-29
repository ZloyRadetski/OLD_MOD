package net.portalmod.common.sorted.radio;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.portalmod.core.init.TileEntityTypeInit;

public class RadioBlock extends Block {
   public static final DirectionProperty FACING;
   public static final EnumProperty<RadioState> STATE;
   public static final BooleanProperty POWERED;
   VoxelShape AABB = Block.func_208617_a((double)3.5F, (double)0.0F, (double)6.0F, (double)12.5F, (double)6.0F, (double)10.0F);
   VoxelShape AABB_SIDE = Block.func_208617_a((double)6.0F, (double)0.0F, (double)3.5F, (double)10.0F, (double)6.0F, (double)12.5F);

   public RadioBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.NORTH)).func_206870_a(STATE, RadioState.OFF)).func_206870_a(POWERED, false));
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader level) {
      return ((TileEntityType)TileEntityTypeInit.RADIO.get()).func_200968_a();
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, STATE, POWERED});
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext selectionContext) {
      return ((Direction)state.func_177229_b(FACING)).func_176740_k() == Axis.X ? this.AABB_SIDE : this.AABB;
   }

   public boolean func_196260_a(BlockState state, IWorldReader level, BlockPos pos) {
      return func_220055_a(level, pos.func_177977_b(), Direction.UP);
   }

   public ActionResultType func_225533_a_(BlockState state, World level, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rayTraceResult) {
      if (!level.func_201670_d()) {
         ((RadioBlockTileEntity)level.func_175625_s(pos)).switchManual();
      }

      return ActionResultType.SUCCESS;
   }

   public void func_220069_a(BlockState state, World level, BlockPos pos, Block block, BlockPos targetPos, boolean b) {
      if (!level.func_201670_d()) {
         if (!state.func_196955_c(level, pos)) {
            level.func_241212_a_(pos, true, (Entity)null, 0);
         } else {
            RadioBlockTileEntity tile = (RadioBlockTileEntity)level.func_175625_s(pos);
            if (level.func_175640_z(pos)) {
               level.func_180501_a(pos, (BlockState)state.func_206870_a(POWERED, true), 2);
               tile.play();
            } else if ((Boolean)state.func_177229_b(POWERED)) {
               level.func_180501_a(pos, (BlockState)state.func_206870_a(POWERED, false), 2);
               if (level.func_234923_W_() != World.field_234920_i_) {
                  tile.stop();
               }
            }

         }
      }
   }

   public BlockState func_196258_a(BlockItemUseContext context) {
      boolean isPowered = context.func_195991_k().func_175640_z(context.func_195995_a());
      RegistryKey<World> dimension = context.func_195991_k().func_234923_W_();
      BlockState state = (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, context.func_195992_f())).func_206870_a(POWERED, isPowered);
      if (dimension == World.field_234918_g_) {
         state = (BlockState)state.func_206870_a(STATE, isPowered ? RadioState.ON : RadioState.OFF);
      } else if (dimension == World.field_234920_i_) {
         state = (BlockState)state.func_206870_a(STATE, isPowered ? RadioState.ACTIVE : RadioState.INACTIVE);
      }

      return state;
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
      FACING = BlockStateProperties.field_208157_J;
      STATE = EnumProperty.func_177709_a("state", RadioState.class);
      POWERED = BlockStateProperties.field_208194_u;
   }
}
