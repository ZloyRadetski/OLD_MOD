package net.portalmod.common.sorted.creer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class CreerRenderer extends MobRenderer<CreeperEntity, CreerModel<CreeperEntity>> {
   public static final LazyValue<CreerRenderer> INSTANCE = new LazyValue(() -> new CreerRenderer(Minecraft.func_71410_x().func_175598_ae()));
   private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

   private CreerRenderer(EntityRendererManager p_i46186_1_) {
      super(p_i46186_1_, new CreerModel(), 0.5F);
      this.func_177094_a(new CreerChargeLayer(this));
   }

   protected void scale(CreeperEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float f = p_225620_1_.func_70831_j(p_225620_3_);
      float f1 = 1.0F + MathHelper.func_76126_a(f * 100.0F) * f * 0.01F;
      f = MathHelper.func_76131_a(f, 0.0F, 1.0F);
      f *= f;
      f *= f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      p_225620_2_.func_227862_a_(f2, f3, f2);
   }

   protected float getWhiteOverlayProgress(CreeperEntity p_225625_1_, float p_225625_2_) {
      float f = p_225625_1_.func_70831_j(p_225625_2_);
      return (int)(f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.func_76131_a(f, 0.5F, 1.0F);
   }

   public ResourceLocation getTextureLocation(CreeperEntity p_110775_1_) {
      return CREEPER_LOCATION;
   }

   public static boolean isCreer(Entity creeper) {
      return creeper instanceof CreeperEntity && creeper.func_145818_k_() && TextFormatting.func_110646_a(creeper.func_200200_C_().getString()).equalsIgnoreCase("creer");
   }
}
