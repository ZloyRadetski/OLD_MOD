package net.portalmod.common.sorted.button;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.portalmod.common.blocks.QuadBlock;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.antline.AntlineActivator;
import net.portalmod.core.init.EntityTagInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.math.BiHashMap;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class SuperButtonBlock extends QuadBlock implements AntlineActivator {
   public static final BooleanProperty PRESSED = BooleanProperty.func_177716_a("pressed");
   public static final BooleanProperty ACTIVE = BooleanProperty.func_177716_a("active");
   public static final EnumProperty<ButtonMode> MODE = EnumProperty.func_177709_a("mode", ButtonMode.class);
   private static final BiHashMap<Direction, QuadBlockCorner, VoxelShapeGroup> SHAPES = new BiHashMap<Direction, QuadBlockCorner, VoxelShapeGroup>();

   public SuperButtonBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.UP)).func_206870_a(CORNER, QuadBlockCorner.UP_LEFT)).func_206870_a(PRESSED, false)).func_206870_a(ACTIVE, false)).func_206870_a(MODE, ButtonMode.NORMAL));
      this.initAABBs();
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, CORNER, PRESSED, ACTIVE, MODE});
   }

   public void func_225534_a_(BlockState state, ServerWorld level, BlockPos pos, Random random) {
      this.checkPressed(state, level, pos);
   }

   private void initAABBs() {
      VoxelShapeGroup shape = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)0.0F, (double)0.0F, (double)12.0F, (double)3.0F, (double)12.0F).add((double)0.0F, (double)0.0F, (double)12.0F, (double)2.5F, (double)2.0F, (double)16.0F).add((double)12.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)2.0F, (double)2.5F).addPart("normal", (double)0.0F, (double)3.0F, (double)0.0F, (double)8.5F, (double)5.0F, (double)8.5F).addPart("pressed", (double)0.0F, (double)3.0F, (double)0.0F, (double)8.5F, (double)4.0F, (double)8.5F).addPart("trigger", (double)0.0F, (double)4.0F, (double)0.0F, (double)8.5F, (double)6.0F, (double)8.5F).build();

      for(Direction facing : Direction.values()) {
         for(QuadBlockCorner corner : QuadBlockCorner.values()) {
            Mat4 matrix = Mat4.identity();
            matrix.translate(new Vec3((double)0.5F));
            if (facing.func_176743_c() == AxisDirection.NEGATIVE) {
               matrix.scale((new Vec3(facing.func_176730_m())).mul((double)2.0F).add((double)1.0F));
               matrix.rotateDeg((new Vec3(facing.func_176730_m())).to3f(), (float)(corner.getRot() - 90));
            } else {
               matrix.rotateDeg((new Vec3(facing.func_176730_m())).to3f(), (float)corner.getRot());
            }

            if (facing.func_176740_k() == Axis.X) {
               matrix.rotateDeg(Vector3f.field_229183_f_, -90.0F).rotateDeg(Vector3f.field_229181_d_, 90.0F);
            }

            if (facing.func_176740_k() == Axis.Z) {
               matrix.rotateDeg(Vector3f.field_229179_b_, 90.0F);
            }

            matrix.rotateDeg(Vector3f.field_229181_d_, 90.0F);
            matrix.translate(new Vec3((double)-0.5F));
            SHAPES.put(facing, corner, shape.clone().transform(matrix));
         }
      }

   }

   public ActionResultType func_225533_a_(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
      if (!(player.func_184586_b(hand).func_77973_b() instanceof WrenchItem)) {
         return ActionResultType.PASS;
      } else {
         boolean shouldCycle = blockState.func_177229_b(MODE) == ButtonMode.NORMAL || !(Boolean)blockState.func_177229_b(ACTIVE);
         if (shouldCycle) {
            ButtonMode newMode = this.cycleMode(blockState, world, pos);
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.button_mode." + newMode.func_176610_l()), true);
         }

         this.setBlockStateValue(ACTIVE, false, blockState, world, pos);
         this.checkPressed(blockState, world, pos);
         WrenchItem.playUseSound(world, result.func_216347_e());
         this.updateAllNeighbors(world, pos, blockState);
         return ActionResultType.func_233537_a_(world.field_72995_K);
      }
   }

   public ButtonMode cycleMode(BlockState blockState, World world, BlockPos pos) {
      ButtonMode currentMode = (ButtonMode)blockState.func_177229_b(MODE);
      ButtonMode newMode = currentMode.cycle();
      this.setBlockStateValue(MODE, newMode, blockState, world, pos);
      return newMode;
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext selectionContext) {
      VoxelShape shape = this.getShapeGroup(state).getVariant((Boolean)state.func_177229_b(PRESSED) ? "pressed" : "normal");
      return shape != null ? shape : VoxelShapes.func_197880_a();
   }

   private VoxelShapeGroup getShapeGroup(BlockState state) {
      return SHAPES.get(state.func_177229_b(FACING), state.func_177229_b(CORNER));
   }

   public void func_220069_a(BlockState state, World level, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      if (!level.field_72995_K) {
         if (!state.func_196955_c(level, pos)) {
            level.func_241212_a_(pos, true, (Entity)null, 0);
         }

         Direction facing = (Direction)state.func_177229_b(FACING);
         boolean isPowered = this.getAllPositions(state, pos).stream().filter((blockPos) -> level.func_180495_p(blockPos).func_177230_c() instanceof SuperButtonBlock).anyMatch((checkingPos) -> level.func_175709_b(checkingPos.func_177972_a(facing.func_176734_d()), facing));
         if (isPowered && (Boolean)state.func_177229_b(ACTIVE)) {
            this.setBlockStateValue(ACTIVE, false, state, level, pos);
            this.updateAllNeighbors(level, pos, state);
            this.playPressSound(level, pos, false);
         }

      }
   }

   public void func_196262_a(BlockState state, World level, BlockPos pos, Entity entity) {
      if (!level.field_72995_K) {
         boolean hasScheduledTick = this.getAllPositions(state, pos).stream().anyMatch((pos1) -> level.func_205220_G_().func_205359_a(pos1, this));
         if (!hasScheduledTick) {
            this.checkPressed(state, level, pos);
         }

      }
   }

   private void checkPressed(BlockState state, World level, BlockPos pos) {
      List<BlockPos> blocks = this.getAllPositions(state, pos);
      boolean wasPressed = (Boolean)state.func_177229_b(PRESSED);
      boolean wasActive = (Boolean)state.func_177229_b(ACTIVE);
      ButtonMode mode = (ButtonMode)state.func_177229_b(MODE);
      boolean pressed = blocks.stream().anyMatch((cornerPos) -> this.isBeingPressed(level, cornerPos));
      if (pressed) {
         level.func_205220_G_().func_205360_a(pos, this, 10);
      }

      if (wasPressed != pressed) {
         this.setBlockStateValue(PRESSED, pressed, state, level, pos);
         this.playPressSound(level, pos, pressed);
         if (mode == ButtonMode.NORMAL) {
            this.setBlockStateValue(ACTIVE, pressed, state, level, pos);
            this.playActivationSound(level, pos, pressed);
         } else if (mode == ButtonMode.PERSISTENT && !wasActive) {
            this.setBlockStateValue(ACTIVE, true, state, level, pos);
            this.playActivationSound(level, pos, true);
         } else if (mode == ButtonMode.TOGGLE && pressed) {
            this.setBlockStateValue(ACTIVE, !wasActive, state, level, pos);
            this.playActivationSound(level, pos, !wasActive);
         }

         this.updateAllNeighbors(level, pos, state);
      }

   }

   public void playPressSound(World level, BlockPos pos, boolean pressed) {
      level.func_184133_a((PlayerEntity)null, pos, pressed ? (SoundEvent)SoundInit.SUPER_BUTTON_PRESS.get() : (SoundEvent)SoundInit.SUPER_BUTTON_RELEASE.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSlightSoundPitch());
   }

   public void playActivationSound(World level, BlockPos pos, boolean activated) {
      level.func_184133_a((PlayerEntity)null, pos, activated ? (SoundEvent)SoundInit.BUTTON_ACTIVATE.get() : (SoundEvent)SoundInit.BUTTON_DEACTIVATE.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSlightSoundPitch());
   }

   public AxisAlignedBB getTrigger(BlockState state, BlockPos pos) {
      return this.getShapeGroup(state).getPart("trigger").func_197752_a().func_186670_a(pos);
   }

   private boolean isBeingPressed(World level, BlockPos pos) {
      AxisAlignedBB trigger = this.getTrigger(level.func_180495_p(pos), pos);
      List<? extends Entity> entities = level.func_175647_a(LivingEntity.class, trigger, EntityPredicates.field_180132_d.and((entity) -> !entity.func_200600_R().func_220341_a(EntityTagInit.BUTTON_NO_PRESS)).and((entity) -> !(entity instanceof TestElementEntity) || !((TestElementEntity)entity).isFizzling()));
      return !entities.isEmpty();
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("super_button", list);
   }

   public boolean isAntlineActive(BlockState state) {
      return (Boolean)state.func_177229_b(ACTIVE);
   }

   public Direction getHorsedOn(BlockState state) {
      return ((Direction)state.func_177229_b(FACING)).func_176734_d();
   }

   public boolean antlineConnectsInDirection(Direction direction, BlockState state) {
      return direction.func_176740_k() != ((Direction)state.func_177229_b(FACING)).func_176740_k();
   }
}
