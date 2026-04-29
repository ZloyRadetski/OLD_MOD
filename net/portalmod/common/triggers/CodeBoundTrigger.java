package net.portalmod.common.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class CodeBoundTrigger extends AbstractCriterionTrigger<Instance> {
   private final ResourceLocation id;

   public CodeBoundTrigger(String name) {
      this.id = new ResourceLocation("portalmod", name);
   }

   public ResourceLocation func_192163_a() {
      return this.id;
   }

   protected Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entity, ConditionArrayParser condition) {
      return new Instance(this.id, entity);
   }

   public void trigger(ServerPlayerEntity player) {
      this.func_235959_a_(player, (x) -> true);
   }

   public static class Instance extends CriterionInstance {
      public Instance(ResourceLocation id, EntityPredicate.AndPredicate player) {
         super(id, player);
      }
   }
}
