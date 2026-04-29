package net.portalmod.common.blocks;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class ChamberLightsBlock extends DoubleBlock {
   public static final EnumProperty<Direction.Axis> AXIS;
   public static final BooleanProperty ROTATED;
   public static final BooleanProperty ACTIVE;
   public static final BooleanProperty POWERED;

   public ChamberLightsBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(AXIS, Axis.Y)).func_206870_a(ACTIVE, true)).func_206870_a(POWERED, false)).func_206870_a(ROTATED, false)).func_206870_a(HALF, DoubleBlockHalf.LOWER));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{AXIS, ACTIVE, POWERED, HALF, ROTATED});
   }

   public Direction getUpperDirection(BlockState state) {
      return Direction.func_211699_a((Direction.Axis)state.func_177229_b(AXIS), AxisDirection.POSITIVE);
   }

   public boolean isSamePart(BlockState one, BlockState two) {
      return super.isSamePart(one, two) && one.func_177229_b(AXIS) == two.func_177229_b(AXIS) && one.func_177229_b(ROTATED) == two.func_177229_b(ROTATED);
   }

   public boolean lookDirectionInfluencesLocation() {
      return true;
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block block, BlockPos nPos, boolean moved) {
      Boolean wasPowered = (Boolean)state.func_177229_b(POWERED);
      Stream var10000 = this.getAllPositions(state, pos).stream();
      world.getClass();
      boolean isPowered = var10000.anyMatch(world::func_175640_z);
      if (wasPowered != isPowered) {
         this.setBlockStateValue(POWERED, isPowered, state, world, pos);
         if (isPowered) {
            this.setBlockStateValue(ACTIVE, false, state, world, pos);
         } else {
            this.setBlockStateValue(ACTIVE, (new Random()).nextBoolean(), state, world, pos);
            this.blink(world.func_180495_p(pos), world, pos);
         }

      }
   }

   public void blink(BlockState state, World world, BlockPos pos) {
      Random random = new Random();
      boolean oldActive = (Boolean)state.func_177229_b(ACTIVE);
      this.setBlockStateValue(ACTIVE, !oldActive, state, world, pos);
      if (!oldActive) {
         this.playBlinkSound(world, pos);
      }

      if (!(random.nextDouble() < (double)0.5F) || (Boolean)state.func_177229_b(POWERED) != oldActive) {
         double ticks = Math.abs(random.nextGaussian()) * (double)5.0F + (double)1.0F;
         world.func_205220_G_().func_205360_a(pos, this, (int)ticks);
      }
   }

   public void func_225534_a_(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      this.blink(state, world, pos);
   }

   @Nullable
   public BlockState func_196258_a(@Nonnull BlockItemUseContext context) {
      if (context.func_195999_j() == null) {
         return null;
      } else {
         Direction.Axis axis = context.func_195999_j().func_174811_aO().func_176740_k() == Axis.X ? Axis.Z : Axis.X;
         boolean prefersHorizontal = context.func_195999_j() != null && context.func_195999_j().func_225608_bj_();
         Optional<DoubleBlockHalf> verticalTopHalf = getPlacementHalf(context, Direction.UP);
         Optional<DoubleBlockHalf> horizontalTopHalf = getPlacementHalf(context, Direction.func_211699_a(axis, AxisDirection.POSITIVE));
         if (!verticalTopHalf.isPresent() && !horizontalTopHalf.isPresent()) {
            return null;
         } else {
            boolean willBeHorizontal = prefersHorizontal && horizontalTopHalf.isPresent() || !verticalTopHalf.isPresent();
            BlockState blockstate = (BlockState)((BlockState)this.func_176223_P().func_206870_a(HALF, willBeHorizontal ? (DoubleBlockHalf)horizontalTopHalf.get() : (DoubleBlockHalf)verticalTopHalf.get())).func_206870_a(ACTIVE, (new Random()).nextBoolean());
            return willBeHorizontal ? (BlockState)((BlockState)blockstate.func_206870_a(AXIS, axis)).func_206870_a(ROTATED, context.func_196010_d().func_176740_k() == Axis.Y) : (BlockState)blockstate.func_206870_a(ROTATED, context.func_195992_f().func_176740_k() == Axis.X);
         }
      }
   }

   public void func_180633_a(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
      super.func_180633_a(world, pos, blockState, entity, itemStack);
      this.blink(blockState, world, pos);
   }

   public void playBlinkSound(World world, BlockPos pos) {
      world.func_184133_a((PlayerEntity)null, pos, (SoundEvent)SoundInit.CHAMBER_LIGHTS_FLICKER.get(), SoundCategory.BLOCKS, (new Random()).nextFloat() * 0.3F + 0.1F, ModUtil.randomSlightSoundPitch());
   }

   @OnlyIn(Dist.CLIENT)
   public void func_180655_c(BlockState state, World level, BlockPos pos, Random random) {
      if (!(Boolean)state.func_177229_b(POWERED) && random.nextInt(24) == 0) {
         level.func_184134_a((double)pos.func_177958_n() + (double)0.5F, (double)pos.func_177956_o() + (double)0.5F, (double)pos.func_177952_p() + (double)0.5F, (SoundEvent)SoundInit.CHAMBER_LIGHTS_AMBIENT.get(), SoundCategory.AMBIENT, 0.3F, 1.0F, false);
      }

   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
      if (axis == Axis.Y) {
         return rotation != Rotation.NONE && rotation != Rotation.CLOCKWISE_180 ? (BlockState)state.func_235896_a_(ROTATED) : state;
      } else if (rotation == Rotation.NONE) {
         return state;
      } else if (rotation == Rotation.CLOCKWISE_180) {
         return (BlockState)state.func_235896_a_(HALF);
      } else {
         BlockState rotated = (BlockState)state.func_206870_a(AXIS, axis == Axis.X ? Axis.Z : Axis.X);
         return (rotation != Rotation.CLOCKWISE_90 || axis != Axis.Z) && (rotation != Rotation.COUNTERCLOCKWISE_90 || axis != Axis.X) ? rotated : (BlockState)rotated.func_235896_a_(HALF);
      }
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
      return (axis != Axis.X || mirror != Mirror.FRONT_BACK) && (axis != Axis.Z || mirror != Mirror.LEFT_RIGHT) ? state : (BlockState)state.func_235896_a_(HALF);
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("chamber_lights", list);
   }

   static {
      AXIS = BlockStateProperties.field_208148_A;
      ROTATED = BooleanProperty.func_177716_a("rotated");
      ACTIVE = BooleanProperty.func_177716_a("active");
      POWERED = BlockStateProperties.field_208194_u;
   }
}
