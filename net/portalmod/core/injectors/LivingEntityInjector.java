package net.portalmod.core.injectors;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.common.sorted.gel.IGelAffected;
import net.portalmod.common.sorted.gel.PropulsionGelBlock;
import net.portalmod.common.sorted.gel.RepulsionGelBlock;
import net.portalmod.core.init.FluidTagInit;
import net.portalmod.core.math.AABBUtil;

public class LivingEntityInjector {
   public static void onPreTick(LivingEntity entity) {
      RepulsionGelBlock.onPreTick(entity);
      PropulsionGelBlock.onPreTick(entity);
      if (effectsShouldBeReset(entity, true) && entity.func_213322_ci().field_72448_b < (double)0.0F) {
         ((Flingable)entity).setFlinging(false);
      }

   }

   public static void onPostTick(LivingEntity entity) {
      RepulsionGelBlock.onPostTick(entity);
      IGelAffected gelAffected = (IGelAffected)entity;
      gelAffected.setWasOnGround(entity.func_233570_aj_());
      gelAffected.setLastLastDeltaMovement(gelAffected.getLastDeltaMovement());
      gelAffected.setLastDeltaMovement(entity.func_213322_ci());
      gelAffected.setHorizontalBounced(false);
   }

   public static boolean effectsShouldBeReset(LivingEntity entity, boolean includeOnGround) {
      AxisAlignedBB aabb = entity.func_174813_aQ().func_186664_h(0.001);
      List<BlockPos> blockPoses = AABBUtil.getBlocksWithin(aabb);
      boolean isInGoo = false;

      for(BlockPos pos : blockPoses) {
         FluidState fluidstate = entity.field_70170_p.func_204610_c(pos);
         if (fluidstate.func_206884_a(FluidTagInit.GOO)) {
            float height = (float)pos.func_177956_o() + fluidstate.func_215679_a(entity.field_70170_p, pos);
            if ((double)height >= aabb.field_72338_b) {
               isInGoo = true;
               break;
            }
         }
      }

      return includeOnGround && entity.func_233570_aj_() || entity.func_70094_T() || entity.func_70090_H() || entity.func_180799_ab() || isInGoo || entity.func_189652_ae() || entity.func_70644_a(Effects.field_188424_y) || entity instanceof PlayerEntity && ((PlayerEntity)entity).field_71075_bZ.field_75100_b;
   }
}
