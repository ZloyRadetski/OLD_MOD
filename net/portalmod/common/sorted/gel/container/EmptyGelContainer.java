package net.portalmod.common.sorted.gel.container;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.portalmod.common.sorted.gel.AbstractGelBlock;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class EmptyGelContainer extends Item {
   public EmptyGelContainer(Item.Properties properties) {
      super(properties);
   }

   public ActionResultType func_195939_a(ItemUseContext context) {
      PlayerEntity player = context.func_195999_j();
      BlockPos clickedPos = context.func_195995_a();
      BlockState clickedState = context.func_195991_k().func_180495_p(clickedPos);
      boolean creative = player.field_71075_bZ.field_75098_d;
      Direction gelSide = context.func_196000_l().func_176734_d();
      BooleanProperty sideProperty = (BooleanProperty)AbstractGelBlock.STATES.get(gelSide);
      if (clickedState.func_177230_c() instanceof AbstractGelBlock && (Boolean)clickedState.func_177229_b(sideProperty)) {
         context.func_195991_k().func_175656_a(clickedPos, AbstractGelBlock.removeSide(gelSide, clickedState));
         context.func_195991_k().func_184133_a(player, clickedPos, (SoundEvent)SoundInit.GEL_COLLECT.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSlightSoundPitch());
         if (!creative) {
            ItemStack newContainer = new ItemStack(clickedState.func_177230_c().func_199767_j());
            newContainer.func_77982_d(context.func_195996_i().func_77978_p());
            GelContainer.setAmount(newContainer, 1);
            player.func_184611_a(context.func_221531_n(), newContainer);
         }

         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }
}
