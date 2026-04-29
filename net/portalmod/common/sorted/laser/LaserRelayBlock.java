package net.portalmod.common.sorted.laser;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.portalmod.core.math.VoxelShapeBuilder;

public class LaserRelayBlock extends Block {
   public static final BooleanProperty ACTIVE = BooleanProperty.func_177716_a("active");
   private static final VoxelShape SHAPE = (new VoxelShapeBuilder()).add((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, 1.9, (double)16.0F).add((double)3.0F, (double)0.0F, (double)3.0F, (double)13.0F, (double)14.0F, (double)13.0F).build();

   public LaserRelayBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(ACTIVE, false));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{ACTIVE});
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public PushReaction func_149656_h(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }
}
