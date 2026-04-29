package net.portalmod.common.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class MultiBlock extends Block {
   private final Map<Property<?>, Comparable<?>> mainProperties = new HashMap();

   public MultiBlock(AbstractBlock.Properties blockProperties) {
      super(blockProperties);
      this.addMainBlockProperties(this.mainProperties);
   }

   public abstract BlockPos getMainPosition(BlockState var1, BlockPos var2);

   public abstract List<BlockPos> getConnectedPositions(BlockState var1, BlockPos var2);

   public abstract Map<BlockPos, BlockState> getOtherParts(BlockState var1, BlockPos var2);

   public abstract boolean isSamePart(BlockState var1, BlockState var2);

   public abstract void addMainBlockProperties(Map<Property<?>, Comparable<?>> var1);

   public boolean lookDirectionInfluencesLocation() {
      return false;
   }

   public boolean isMainBlock(BlockState blockState) {
      return this.mainProperties.entrySet().stream().allMatch((entry) -> blockState.func_177229_b((Property)entry.getKey()).equals(entry.getValue()));
   }

   public List<BlockPos> getAllPositions(BlockState blockState, BlockPos pos) {
      BlockPos mainPos = this.getMainPosition(blockState, pos);
      List<BlockPos> connectedPositions = this.getConnectedPositions(blockState, mainPos);
      connectedPositions.add(mainPos);
      return connectedPositions;
   }

   public <T extends Comparable<T>> void setBlockStateValue(Property<T> property, T value, BlockState blockState, World world, BlockPos pos) {
      for(BlockPos multiBlockPos : this.getAllPositions(blockState, pos)) {
         BlockState multiBlockState = world.func_180495_p(multiBlockPos);
         if (multiBlockState.func_177230_c().func_235332_a_(this)) {
            world.func_180501_a(multiBlockPos, (BlockState)multiBlockState.func_206870_a(property, value), 2);
         }
      }

   }

   public void updateAllNeighbors(World world, BlockPos pos, BlockState blockState) {
      if (!world.field_72995_K) {
         List<BlockPos> positions = this.getAllPositions(blockState, pos);

         for(BlockPos connected : positions) {
            for(Direction direction : Direction.values()) {
               BlockPos neighbor = connected.func_177972_a(direction);
               if (!positions.contains(neighbor)) {
                  world.func_230547_a_(neighbor, this);
               }
            }
         }

      }
   }

   public static boolean clickedOnPositiveHalf(BlockItemUseContext context, Direction direction) {
      boolean isPositiveDirection = direction.func_176743_c() == AxisDirection.POSITIVE;
      return clickedOnPositiveHalf(context, direction.func_176740_k()) == isPositiveDirection;
   }

   public static boolean clickedOnPositiveHalf(BlockItemUseContext context, Direction.Axis axis) {
      BlockPos pos = context.func_195995_a();
      if (axis == Axis.X) {
         return context.func_221532_j().field_72450_a - (double)pos.func_177958_n() > (double)0.5F;
      } else if (axis == Axis.Y) {
         return context.func_221532_j().field_72448_b - (double)pos.func_177956_o() > (double)0.5F;
      } else {
         return context.func_221532_j().field_72449_c - (double)pos.func_177952_p() > (double)0.5F;
      }
   }

   public BlockState func_196271_a(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
      if (this.getAllPositions(state, pos).contains(neighborPos)) {
         return neighborState.func_203425_a(this) && this.isSamePart(neighborState, (BlockState)this.getOtherParts(state, pos).get(neighborPos)) ? state : Blocks.field_150350_a.func_176223_P();
      } else {
         return state;
      }
   }

   public void func_180633_a(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
      if (!world.field_72995_K || !this.lookDirectionInfluencesLocation()) {
         this.getOtherParts(blockState, pos).forEach(world::func_175656_a);
      }
   }

   public void func_176208_a(World world, BlockPos pos, BlockState blockState, PlayerEntity player) {
      if (!world.field_72995_K) {
         if (player.func_184812_l_()) {
            this.preventCreativeDropFromMainPart(world, pos, blockState, player);
         } else {
            func_220054_a(blockState, world, pos, (TileEntity)null, player, player.func_184614_ca());
         }
      }

      super.func_176208_a(world, pos, blockState, player);
   }

   public void func_180657_a(World world, PlayerEntity player, BlockPos pos, BlockState blockState, @Nullable TileEntity tileEntity, ItemStack itemStack) {
      super.func_180657_a(world, player, pos, Blocks.field_150350_a.func_176223_P(), tileEntity, itemStack);
   }

   public void preventCreativeDropFromMainPart(World world, BlockPos pos, BlockState blockState, PlayerEntity player) {
      BlockPos mainPos = this.getMainPosition(blockState, pos);
      if (!pos.equals(mainPos)) {
         BlockState mainBlockState = world.func_180495_p(mainPos);
         if (mainBlockState.func_177230_c().func_235332_a_(this)) {
            world.func_180501_a(mainPos, Blocks.field_150350_a.func_176223_P(), 35);
            world.func_217378_a(player, 2001, mainPos, Block.func_196246_j(mainBlockState));
         }
      }

   }

   public PushReaction func_149656_h(BlockState p_149656_1_) {
      return PushReaction.BLOCK;
   }
}
