package net.portalmod.common.sorted.trigger;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.util.ModUtil;

public class TriggerBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS;
   public static final EnumProperty<TriggerType> TYPE;
   public static final EnumProperty<TriggerState> STATE;
   public static final VoxelShape SHAPE_BASE;
   public static final VoxelShape SHAPE_X;
   public static final VoxelShape SHAPE_Z;

   public TriggerBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(AXIS, Axis.X)).func_206870_a(TYPE, TriggerType.PLAYER)).func_206870_a(STATE, TriggerState.NULL));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{AXIS, TYPE, STATE});
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
      return state.func_177229_b(AXIS) == Axis.Z ? SHAPE_Z : SHAPE_X;
   }

   public ActionResultType func_225533_a_(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
      TileEntity tileEntity = level.func_175625_s(pos);
      if (WrenchItem.usedWrench(player, hand) && tileEntity instanceof TriggerTileEntity) {
         if (player.func_225608_bj_()) {
            BlockState cycled = (BlockState)state.func_235896_a_(TYPE);
            level.func_175656_a(pos, cycled);
            WrenchItem.playUseSound(level, rayTraceResult.func_216347_e());
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.trigger_type." + ((TriggerType)cycled.func_177229_b(TYPE)).func_176610_l()), true);
            if (tileEntity instanceof TriggerTileEntity) {
               ((TriggerTileEntity)tileEntity).updateTriggerType();
            }

            return ActionResultType.func_233537_a_(level.field_72995_K);
         } else if (level.field_72995_K) {
            return ActionResultType.PASS;
         } else {
            TriggerTileEntity trigger = (TriggerTileEntity)tileEntity;
            if (!trigger.isBeingConfigured()) {
               TriggerSelectionServer.startConfiguration((ServerPlayerEntity)player, trigger);
               WrenchItem.playUseSound(level, rayTraceResult.func_216347_e());
            } else if (trigger.getConfiguringPlayer() != player) {
               WrenchItem.playFailSound(level, rayTraceResult.func_216347_e());
            }

            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(AXIS, context.func_195992_f().func_176740_k());
   }

   public boolean func_149744_f(BlockState state) {
      return true;
   }

   public int func_180656_a(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
      TileEntity blockEntity = level.func_175625_s(pos);
      return state.func_177229_b(STATE) == TriggerState.ACTIVE && blockEntity instanceof TriggerTileEntity ? Math.min(((TriggerTileEntity)blockEntity).getEntityCount(), 15) : 0;
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ((TileEntityType)TileEntityTypeInit.TRIGGER.get()).func_200968_a();
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return ModUtil.getRotationAmount(rotation) % 2 == 1 ? (BlockState)state.func_235896_a_(AXIS) : state;
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("trigger", list);
   }

   static {
      AXIS = BlockStateProperties.field_208199_z;
      TYPE = EnumProperty.func_177709_a("type", TriggerType.class);
      STATE = EnumProperty.func_177709_a("state", TriggerState.class);
      SHAPE_BASE = Block.func_208617_a((double)1.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)5.0F, (double)15.0F);
      SHAPE_X = VoxelShapes.func_197872_a(SHAPE_BASE, Block.func_208617_a((double)6.0F, (double)5.0F, (double)2.0F, (double)10.0F, (double)14.0F, (double)14.0F));
      SHAPE_Z = VoxelShapes.func_197872_a(SHAPE_BASE, Block.func_208617_a((double)2.0F, (double)5.0F, (double)6.0F, (double)14.0F, (double)14.0F, (double)10.0F));
   }
}
