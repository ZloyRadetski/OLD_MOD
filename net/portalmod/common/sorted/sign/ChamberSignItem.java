package net.portalmod.common.sorted.sign;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.portalmod.core.util.ModUtil;

public class ChamberSignItem extends Item {
   public ChamberSignItem(Item.Properties properties) {
      super(properties);
   }

   public ActionResultType func_195939_a(ItemUseContext context) {
      BlockPos blockpos = context.func_195995_a();
      Direction direction = context.func_196000_l();
      BlockPos blockpos1 = blockpos.func_177972_a(direction);
      PlayerEntity playerentity = context.func_195999_j();
      ItemStack itemstack = context.func_195996_i();
      if (playerentity != null && !this.mayPlace(playerentity, direction, itemstack, blockpos1)) {
         return ActionResultType.PASS;
      } else {
         World world = context.func_195991_k();
         boolean clickedBottomHalf = context.func_221532_j().field_72448_b % (double)1.0F < (double)0.25F;
         boolean clickedTopHalf = context.func_221532_j().field_72448_b % (double)1.0F > (double)0.75F;
         BlockPos placePos = clickedBottomHalf ? blockpos1.func_177977_b() : blockpos1;
         ChamberSignEntity chamberSign = new ChamberSignEntity(world, placePos, direction, !clickedBottomHalf && !clickedTopHalf);
         CompoundNBT compoundnbt = itemstack.func_77978_p();
         if (compoundnbt != null) {
            EntityType.func_208048_a(world, playerentity, chamberSign, compoundnbt);
         }

         if (chamberSign.func_70518_d()) {
            if (!world.field_72995_K) {
               chamberSign.func_184523_o();
               world.func_217376_c(chamberSign);
            }

            itemstack.func_190918_g(1);
            return ActionResultType.func_233537_a_(world.field_72995_K);
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   protected boolean mayPlace(PlayerEntity player, Direction direction, ItemStack itemStack, BlockPos pos) {
      return !direction.func_176740_k().func_200128_b() && player.func_175151_a(pos, direction, itemStack);
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("chamber_sign", list);
   }
}
