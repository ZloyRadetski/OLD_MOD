package net.portalmod.common.sorted.button;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.portalmod.common.blocks.DoubleBlock;
import net.portalmod.common.blocks.InteractKeyInteractable;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.antline.AntlineActivator;
import net.portalmod.common.sorted.portalgun.CPortalGunInteractionPacket;
import net.portalmod.common.sorted.portalgun.PortalGunInteraction;
import net.portalmod.core.init.AttributeInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.math.BiHashMap;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class StandingButtonBlock extends DoubleBlock implements AntlineActivator, InteractKeyInteractable {
   public static final DirectionProperty FACING;
   public static final BooleanProperty PRESSED;
   public static final BooleanProperty ACTIVE;
   public static final EnumProperty<ButtonMode> MODE;
   public static final int BUTTON_DELAY = 20;
   private static final BiHashMap<Direction, DoubleBlockHalf, VoxelShapeGroup> SHAPE;
   private static final VoxelShapeGroup LOWER;
   private static final VoxelShapeGroup UPPER;

   public StandingButtonBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.UP)).func_206870_a(HALF, DoubleBlockHalf.LOWER)).func_206870_a(PRESSED, false)).func_206870_a(ACTIVE, false)).func_206870_a(MODE, ButtonMode.NORMAL));
      this.initAABBs();
   }

   private void initAABBs() {
      for(Direction facing : Direction.values()) {
         Vec3 normal = new Vec3(facing.func_176730_m());
         Mat4 matrix = Mat4.identity();
         matrix.translate(new Vec3((double)0.5F));
         if (facing == Direction.DOWN) {
            matrix.scale(normal.mul((double)2.0F).add((double)1.0F));
         }

         if (facing.func_176740_k() != Axis.Y) {
            matrix.rotateDeg(normal.cross(Direction.UP.func_176730_m()).to3f(), -90.0F);
         }

         matrix.translate(new Vec3((double)-0.5F));

         for(DoubleBlockHalf half : DoubleBlockHalf.values()) {
            VoxelShapeGroup shape = half == DoubleBlockHalf.UPPER ? UPPER : LOWER;
            SHAPE.put(facing, half, shape.clone().transform(matrix));
         }
      }

   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      return ((VoxelShapeGroup)SHAPE.get(state.func_177229_b(FACING), state.func_177229_b(HALF))).getVariant((Boolean)state.func_177229_b(PRESSED) ? "on" : "off");
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, HALF, PRESSED, ACTIVE, MODE});
   }

   public Direction getUpperDirection(BlockState state) {
      return (Direction)state.func_177229_b(FACING);
   }

   public boolean canPress(BlockState blockState) {
      return !(Boolean)blockState.func_177229_b(PRESSED);
   }

   public void press(BlockState blockState, World world, BlockPos pos) {
      ButtonMode mode = (ButtonMode)blockState.func_177229_b(MODE);
      Boolean wasActive = (Boolean)blockState.func_177229_b(ACTIVE);
      this.setBlockStateValue(PRESSED, true, blockState, world, pos);
      world.func_205220_G_().func_205360_a(pos, this, 20);
      if (mode != ButtonMode.NORMAL && (mode != ButtonMode.PERSISTENT || wasActive)) {
         if (mode == ButtonMode.TOGGLE) {
            this.setActivated(!wasActive, blockState, world, pos);
         }
      } else {
         this.setActivated(true, blockState, world, pos);
      }

      this.updateAllNeighbors(world, pos, blockState);
   }

   private void setActivated(boolean activated, BlockState blockState, World world, BlockPos pos) {
      this.setBlockStateValue(ACTIVE, activated, blockState, world, pos);
      world.func_184133_a((PlayerEntity)null, pos, activated ? (SoundEvent)SoundInit.BUTTON_ACTIVATE.get() : (SoundEvent)SoundInit.BUTTON_DEACTIVATE.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSlightSoundPitch());
   }

   public void func_225534_a_(BlockState blockState, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)blockState.func_177229_b(PRESSED)) {
         this.setBlockStateValue(PRESSED, false, blockState, world, pos);
         world.func_195593_d(pos, this);
         if (blockState.func_177229_b(MODE) == ButtonMode.NORMAL) {
            this.setActivated(false, blockState, world, pos);
         }

         this.updateAllNeighbors(world, pos, world.func_180495_p(pos));
      }

   }

   public ActionResultType func_225533_a_(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
      if (!WrenchItem.usedWrench(player, hand)) {
         double rayLength = rayTraceResult.func_216347_e().func_178788_d(player.func_174824_e(1.0F)).func_72433_c();
         double reach = player.func_233637_b_((Attribute)AttributeInit.BUTTON_REACH.get());
         if (blockState.func_177229_b(HALF) == DoubleBlockHalf.UPPER && this.canPress(blockState)) {
            if (rayLength > reach) {
               player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.standing_button.too_far_away"), true);
               return ActionResultType.CONSUME;
            } else {
               this.press(blockState, world, pos);
               return ActionResultType.func_233537_a_(world.field_72995_K);
            }
         } else {
            return ActionResultType.PASS;
         }
      } else {
         boolean shouldCycle = blockState.func_177229_b(MODE) == ButtonMode.NORMAL || !(Boolean)blockState.func_177229_b(ACTIVE);
         if (shouldCycle) {
            ButtonMode newMode = this.cycleMode(blockState, world, pos);
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.button_mode." + newMode.func_176610_l()), true);
         }

         this.setBlockStateValue(ACTIVE, false, blockState, world, pos);
         WrenchItem.playUseSound(world, rayTraceResult.func_216347_e());
         this.updateAllNeighbors(world, pos, world.func_180495_p(pos));
         return ActionResultType.func_233537_a_(world.field_72995_K);
      }
   }

   public boolean interactKeyInteract(PlayerEntity player, BlockRayTraceResult rayHit) {
      if (this.withinInteractRange(player, rayHit)) {
         PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.PRESS_BUTTON)).blockHit(rayHit).build());
         return true;
      } else {
         return false;
      }
   }

   public ButtonMode cycleMode(BlockState blockState, World world, BlockPos pos) {
      ButtonMode currentMode = (ButtonMode)blockState.func_177229_b(MODE);
      ButtonMode newMode = currentMode.cycle();
      this.setBlockStateValue(MODE, newMode, blockState, world, pos);
      return newMode;
   }

   public void func_220069_a(BlockState state, World level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
      if (!level.field_72995_K) {
         Direction facing = (Direction)state.func_177229_b(FACING);
         if ((Boolean)state.func_177229_b(ACTIVE) && state.func_177229_b(HALF) == DoubleBlockHalf.LOWER && level.func_175709_b(pos.func_177972_a(facing.func_176734_d()), facing)) {
            this.setActivated(false, state, level, pos);
            this.updateAllNeighbors(level, pos, level.func_180495_p(pos));
         }

      }
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      Direction facing = context.func_196000_l();
      BlockPos upperPos = context.func_195995_a().func_177972_a(facing);
      return ModUtil.canPlaceAt(context, upperPos) ? (BlockState)this.func_176223_P().func_206870_a(FACING, facing) : null;
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("standing_button", list);
   }

   public boolean isAntlineActive(BlockState state) {
      return (Boolean)state.func_177229_b(ACTIVE);
   }

   public Direction getHorsedOn(BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.UPPER ? null : ((Direction)state.func_177229_b(FACING)).func_176734_d();
   }

   public boolean antlineConnectsInDirection(Direction direction, BlockState state) {
      return state.func_177229_b(HALF) == DoubleBlockHalf.LOWER && direction.func_176740_k() != ((Direction)state.func_177229_b(FACING)).func_176740_k();
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      return (BlockState)state.func_206870_a(FACING, rotation.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      return this.func_185499_a(state, mirror.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   static {
      FACING = BlockStateProperties.field_208155_H;
      PRESSED = BooleanProperty.func_177716_a("pressed");
      ACTIVE = BooleanProperty.func_177716_a("active");
      MODE = EnumProperty.func_177709_a("mode", ButtonMode.class);
      SHAPE = new BiHashMap<Direction, DoubleBlockHalf, VoxelShapeGroup>();
      LOWER = (new VoxelShapeGroup.Builder()).add((double)4.0F, (double)0.0F, (double)4.0F, (double)12.0F, (double)2.0F, (double)12.0F).add((double)5.5F, 2.95, (double)5.5F, (double)10.5F, (double)16.0F, (double)10.5F).add((double)6.0F, (double)2.0F, (double)6.0F, (double)10.0F, (double)16.0F, (double)10.0F).build();
      UPPER = (new VoxelShapeGroup.Builder()).add((double)6.0F, (double)0.0F, (double)6.0F, (double)10.0F, (double)3.0F, (double)10.0F).add((double)5.5F, (double)0.0F, (double)5.5F, (double)10.5F, (double)3.0F, (double)10.5F).addPart("off", (double)6.0F, (double)3.0F, (double)6.0F, (double)10.0F, (double)5.0F, (double)10.0F).addPart("on", (double)6.0F, (double)3.0F, (double)6.0F, (double)10.0F, (double)3.5F, (double)10.0F).build();
   }
}
