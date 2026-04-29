package net.portalmod.core.injectors;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class PlayerEntityInjector {
   static boolean wasFlying = false;

   public static void travel(PlayerEntity player) {
      if (player.field_71075_bZ.field_75100_b && !player.func_184218_aH()) {
         wasFlying = true;
         double d5 = player.func_213322_ci().field_72448_b;
         float f = player.field_70747_aH;
         player.field_70747_aH = player.field_71075_bZ.func_75093_a() * (float)(player.func_70051_ag() ? 2 : 1);
         if (player.func_70613_aW() || player.func_184186_bw()) {
            BlockPos blockpos = new BlockPos(player.func_213303_ch().field_72450_a, player.func_174813_aQ().field_72338_b - 0.5000001, player.func_213303_ch().field_72449_c);
            float f3 = player.field_70170_p.func_180495_p(blockpos).getSlipperiness(player.field_70170_p, blockpos, player);
            float f4 = player.func_233570_aj_() ? f3 * 0.91F : 0.91F;
            Vector3d vector3d5 = player.func_233633_a_(new Vector3d((double)player.field_70702_br, (double)player.field_70701_bs, (double)player.field_191988_bg), f3);
            double d2 = vector3d5.field_72448_b;
            if (player.func_70644_a(Effects.field_188424_y)) {
               d2 += (0.05 * (double)(player.func_70660_b(Effects.field_188424_y).func_76458_c() + 1) - vector3d5.field_72448_b) * 0.2;
               player.field_70143_R = 0.0F;
            } else if (player.field_70170_p.field_72995_K && !player.field_70170_p.func_175667_e(blockpos)) {
               if (player.func_226278_cu_() > (double)0.0F) {
                  d2 = -0.1;
               } else {
                  d2 = (double)0.0F;
               }
            } else if (!player.func_189652_ae()) {
               d2 -= 0.08;
            }

            player.func_213293_j(vector3d5.field_72450_a * (double)f4, d2, vector3d5.field_72449_c * (double)f4);
         }

         Vector3d vector3d = player.func_213322_ci();
         player.func_213293_j(vector3d.field_72450_a, d5 * 0.6, vector3d.field_72449_c);
         player.field_70747_aH = f;
         player.field_70143_R = 0.0F;
      } else {
         if (wasFlying) {
            Vector3d d = player.func_213322_ci();
            player.func_213293_j(d.field_72450_a, -0.08, d.field_72449_c);
         }

         wasFlying = false;
         if (player.func_70613_aW() || player.func_184186_bw()) {
            BlockPos blockpos = new BlockPos(player.func_213303_ch().field_72450_a, player.func_174813_aQ().field_72338_b - 0.5000001, player.func_213303_ch().field_72449_c);
            float f3 = player.field_70170_p.func_180495_p(blockpos).getSlipperiness(player.field_70170_p, blockpos, player);
            float f4 = player.func_233570_aj_() ? f3 * 0.91F : 0.91F;
            Vector3d vector3d5 = player.func_233633_a_(new Vector3d((double)player.field_70702_br, (double)player.field_70701_bs, (double)player.field_191988_bg), f3);
            double d2 = vector3d5.field_72448_b;
            if (player.func_70644_a(Effects.field_188424_y)) {
               d2 += (0.05 * (double)(player.func_70660_b(Effects.field_188424_y).func_76458_c() + 1) - vector3d5.field_72448_b) * 0.2;
               player.field_70143_R = 0.0F;
            } else if (player.field_70170_p.field_72995_K && !player.field_70170_p.func_175667_e(blockpos)) {
               if (player.func_226278_cu_() > (double)0.0F) {
                  d2 = -0.1;
               } else {
                  d2 = (double)0.0F;
               }
            } else if (!player.func_189652_ae()) {
               d2 -= 0.08;
            }

            player.func_213293_j(vector3d5.field_72450_a * (double)f4, d2, vector3d5.field_72449_c * (double)f4);
         }
      }

   }
}
