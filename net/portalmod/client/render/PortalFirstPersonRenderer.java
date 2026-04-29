package net.portalmod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PortalFirstPersonRenderer {
   private static int swingTime = 0;
   private static float oAttackAnim = 0.0F;
   private static float attackAnim = 0.0F;
   public static boolean swinging = false;

   public static void renderItem(LivingEntity p_228397_1_, ItemStack p_228397_2_, ItemCameraTransforms.TransformType p_228397_3_, boolean p_228397_4_, MatrixStack p_228397_5_, IRenderTypeBuffer p_228397_6_, int p_228397_7_) {
      if (!p_228397_2_.func_190926_b()) {
         Minecraft.func_71410_x().func_175599_af().func_229109_a_(p_228397_1_, p_228397_2_, p_228397_3_, p_228397_4_, p_228397_5_, p_228397_6_, p_228397_1_.field_70170_p, p_228397_7_, OverlayTexture.field_229196_a_);
      }

   }

   public static void applyItemArmTransform(MatrixStack p_228406_1_, HandSide p_228406_2_, float p_228406_3_) {
      int i = p_228406_2_ == HandSide.RIGHT ? 1 : -1;
      p_228406_1_.func_227861_a_((double)((float)i * 0.56F), (double)-0.52F, (double)-0.72F);
   }

   public static void updateSwingTime() {
      oAttackAnim = attackAnim;
      int duration = 1;
      if (Minecraft.func_71410_x().field_71439_g != null) {
         duration = (int)((float)Minecraft.func_71410_x().field_71439_g.func_82166_i() * 1.5F);
         if (Minecraft.func_71410_x().field_71439_g.field_82175_bq && !swinging) {
            swinging = true;
         }
      }

      if (swinging) {
         if (++swingTime >= duration) {
            swingTime = 0;
            swinging = false;
         }
      } else {
         swingTime = 0;
      }

      attackAnim = (float)swingTime / (float)duration;
   }

   public static float getAttackAnim(float partialTicks) {
      float f = attackAnim - oAttackAnim;
      if (f < 0.0F) {
         ++f;
      }

      return oAttackAnim + f * partialTicks;
   }

   public static void renderArmWithItem(AbstractClientPlayerEntity player, float partialTicks, float interpolatedPitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack transform, IRenderTypeBuffer buffers, int light) {
      float actualSwingProgress = getAttackAnim(partialTicks);
      HandSide handside = hand == Hand.MAIN_HAND ? player.func_184591_cq() : player.func_184591_cq().func_188468_a();
      transform.func_227860_a_();
      boolean flag3 = handside == HandSide.RIGHT;
      if (player.func_184587_cr() && player.func_184605_cv() > 0 && player.func_184600_cs() == hand) {
         int k = flag3 ? 1 : -1;
         applyItemArmTransform(transform, handside, equipProgress);
      } else if (player.func_204805_cN()) {
         applyItemArmTransform(transform, handside, equipProgress);
         int j = flag3 ? 1 : -1;
         transform.func_227861_a_((double)((float)j * -0.4F), (double)0.8F, (double)0.3F);
         transform.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float)j * 65.0F));
         transform.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_((float)j * -85.0F));
      } else {
         float animation = MathHelper.func_76126_a((actualSwingProgress * 2.0F - 0.5F) * (float)Math.PI) / 2.0F + 0.5F;
         transform.func_227861_a_((double)0.0F, (double)0.0F, (double)animation * 0.3);
         float rotationAnimation = MathHelper.func_76126_a((actualSwingProgress * 4.0F - 0.5F) * (float)Math.PI) / 2.0F + 0.5F;
         transform.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(animation * 7.0F));
         if ((double)actualSwingProgress >= (double)0.5F) {
            transform.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(rotationAnimation * 4.0F));
         }

         applyItemArmTransform(transform, handside, equipProgress);
      }

      renderItem(player, stack, flag3 ? TransformType.FIRST_PERSON_RIGHT_HAND : TransformType.FIRST_PERSON_LEFT_HAND, !flag3, transform, buffers, light);
      transform.func_227865_b_();
   }
}
