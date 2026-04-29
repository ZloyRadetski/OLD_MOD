package net.portalmod.common.blocks;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.portalmod.core.init.AttributeInit;

public interface InteractKeyInteractable {
   boolean interactKeyInteract(PlayerEntity var1, BlockRayTraceResult var2);

   default boolean withinInteractRange(PlayerEntity player, BlockRayTraceResult rayHit) {
      double buttonReach = player.func_233637_b_((Attribute)AttributeInit.BUTTON_REACH.get());
      return rayHit.func_216347_e().func_178788_d(player.func_174824_e(1.0F)).func_72433_c() < buttonReach;
   }
}
