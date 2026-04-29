package net.portalmod.common.sorted.antline.indicator;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.sorted.antline.AntlineActivator;
import net.portalmod.core.util.ModUtil;

public class AntlineEncoderBlock extends AntlineIcon implements AntlineActivator {
   public static final BooleanProperty POWERED = BooleanProperty.func_177716_a("powered");

   public AntlineEncoderBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196366_M, AttachFace.FLOOR)).func_206870_a(field_185512_D, Direction.NORTH)).func_206870_a(ICON, 0)).func_206870_a(POWERED, false));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{POWERED});
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block block, BlockPos pos1, boolean b) {
      boolean powered = world.func_175640_z(pos);
      if (powered != (Boolean)state.func_177229_b(POWERED)) {
         world.func_175656_a(pos, (BlockState)state.func_206870_a(POWERED, powered));
      }

   }

   public void func_220082_b(BlockState state, World world, BlockPos pos, BlockState oldState, boolean b) {
      world.func_190524_a(pos, this, pos);
   }

   public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
      return true;
   }

   public boolean isAntlineActive(BlockState state) {
      return (Boolean)state.func_177229_b(POWERED);
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("antline_encoder", list);
   }
}
