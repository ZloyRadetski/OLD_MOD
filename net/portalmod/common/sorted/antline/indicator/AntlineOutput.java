package net.portalmod.common.sorted.antline.indicator;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.antline.AntlineActivated;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public abstract class AntlineOutput extends AntlineDevice implements AntlineActivated, TestElementActivator {
   public static final BooleanProperty ACTIVATED = BooleanProperty.func_177716_a("activated");
   public static final BooleanProperty REVERSED = BooleanProperty.func_177716_a("reversed");

   public AntlineOutput(AbstractBlock.Properties properties) {
      super(properties);
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{ACTIVATED, REVERSED});
   }

   public ActionResultType func_225533_a_(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
      boolean reversed = (Boolean)blockState.func_177229_b(REVERSED);
      if (player.func_184586_b(hand).func_77973_b() instanceof WrenchItem) {
         world.func_175656_a(pos, (BlockState)blockState.func_206870_a(REVERSED, !reversed));
         player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.indicator_mode." + (reversed ? "normal" : "reversed")), true);
         WrenchItem.playUseSound(world, result.func_216347_e());
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public void playActivationSound(boolean active, World world, BlockPos pos) {
      world.func_184133_a((PlayerEntity)null, pos, (SoundEvent)(active ? SoundInit.ANTLINE_INDICATOR_ACTIVATE : SoundInit.ANTLINE_INDICATOR_DEACTIVATE).get(), SoundCategory.BLOCKS, 3.0F, ModUtil.randomSlightSoundPitch());
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block nBlock, BlockPos nPos, boolean b) {
      this.updateAntlineActivation(state, world, pos);
      super.func_220069_a(state, world, pos, nBlock, nPos, b);
   }

   public void func_220082_b(BlockState state, World world, BlockPos pos, BlockState oldState, boolean b) {
      world.func_195593_d(pos, this);
      this.updateAntlineActivation(state, world, pos);
   }
}
