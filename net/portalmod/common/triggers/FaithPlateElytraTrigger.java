package net.portalmod.common.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class FaithPlateElytraTrigger extends AbstractCriterionTrigger<Instance> {
   private static final ResourceLocation ID = new ResourceLocation("portalmod", "faith_plate_elytra");

   public ResourceLocation func_192163_a() {
      return ID;
   }

   protected Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entity, ConditionArrayParser condition) {
      return new Instance(entity);
   }

   public void trigger(ServerPlayerEntity player) {
      this.func_235959_a_(player, (x) -> true);
   }

   public static class Instance extends CriterionInstance {
      public Instance(EntityPredicate.AndPredicate player) {
         super(FaithPlateElytraTrigger.ID, player);
      }
   }
}
