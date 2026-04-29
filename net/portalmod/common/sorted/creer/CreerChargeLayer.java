package net.portalmod.common.sorted.creer;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.EnergyLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;

public class CreerChargeLayer extends EnergyLayer<CreeperEntity, CreerModel<CreeperEntity>> {
   private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final CreerModel<CreeperEntity> model = new CreerModel<CreeperEntity>(2.0F);

   public CreerChargeLayer(IEntityRenderer<CreeperEntity, CreerModel<CreeperEntity>> p_i50947_1_) {
      super(p_i50947_1_);
   }

   protected float func_225634_a_(float p_225634_1_) {
      return p_225634_1_ * 0.01F;
   }

   protected ResourceLocation func_225633_a_() {
      return POWER_LOCATION;
   }

   protected EntityModel<CreeperEntity> func_225635_b_() {
      return this.model;
   }
}
