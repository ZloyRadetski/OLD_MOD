package net.portalmod.common.sorted.antline.indicator;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.sorted.antline.AntlineActivated;
import net.portalmod.core.util.ModUtil;

public class AntlineIndicatorBlock extends AntlineOutput implements AntlineActivated, TestElementActivator {
   public AntlineIndicatorBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196366_M, AttachFace.FLOOR)).func_206870_a(field_185512_D, Direction.NORTH)).func_206870_a(ACTIVATED, false)).func_206870_a(REVERSED, false));
   }

   public boolean isActive(BlockState state) {
      boolean active = (Boolean)state.func_177229_b(ACTIVATED);
      boolean reversed = (Boolean)state.func_177229_b(REVERSED);
      return reversed != active;
   }

   public void onAntlineActivation(boolean active, BlockState state, World world, BlockPos pos) {
      BlockState current = world.func_180495_p(pos);
      if ((Boolean)current.func_177229_b(ACTIVATED) != active) {
         world.func_175656_a(pos, (BlockState)current.func_206870_a(ACTIVATED, active));
         this.playActivationSound(active != (Boolean)current.func_177229_b(REVERSED), world, pos);
      }
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("antline_indicator", list);
   }
}
