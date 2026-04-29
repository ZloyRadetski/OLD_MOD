package net.portalmod.common.sorted.antline;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.portalmod.core.util.ModUtil;

public class AntlineBlockItem extends BlockItem {
   public AntlineBlockItem(Block block, Item.Properties properties) {
      super(block, properties);
   }

   protected boolean func_195941_b(BlockItemUseContext context, BlockState state) {
      World level = context.func_195991_k();
      BlockPos pos = context.func_195995_a();
      Direction clickedFace = context.func_196000_l();
      if (level.func_175625_s(pos) != null && level.func_175625_s(pos) instanceof AntlineTileEntity) {
         AntlineTileEntity tileEntity = (AntlineTileEntity)level.func_175625_s(pos);
         AntlineTileEntity.SideMap sideMap = tileEntity.getSideMap();
         Vector3d clickedVector = context.func_221532_j();
         int count = 0;
         if (clickedVector.field_72450_a == (double)Math.round(clickedVector.field_72450_a)) {
            ++count;
         }

         if (clickedVector.field_72448_b == (double)Math.round(clickedVector.field_72448_b)) {
            ++count;
         }

         if (clickedVector.field_72449_c == (double)Math.round(clickedVector.field_72449_c)) {
            ++count;
         }

         if (count != 1 || sideMap.hasSide(clickedFace.func_176734_d())) {
            return false;
         }
      } else {
         super.func_195941_b(context, state);
      }

      AntlineTileEntity tileEntity = (AntlineTileEntity)level.func_175625_s(pos);
      AntlineTileEntity.SideMap sideMap = tileEntity.getSideMap();
      sideMap.put(clickedFace.func_176734_d(), AntlineTileEntity.Side.dot(clickedFace.func_176734_d()));
      if (level instanceof ServerWorld && this.func_179223_d() instanceof AntlineBlock) {
         AntlineBlock block = (AntlineBlock)this.func_179223_d();
         boolean shift = context.func_195999_j() != null && context.func_195999_j().func_225608_bj_();
         AntlineTileEntity.Side side = (AntlineTileEntity.Side)sideMap.get(clickedFace.func_176734_d());
         block.sideUpdate(level, side, pos, true, shift, (Direction)null);
         block.recursiveSignalChain(level, side, pos, (Direction)null, false, 0);
         block.sendUpdatePacket(level, pos, clickedFace.func_176734_d(), (AntlineTileEntity)level.func_175625_s(pos));
      }

      return true;
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("antline", list);
   }
}
