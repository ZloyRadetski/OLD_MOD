package net.portalmod.common.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class ClickMoonCriteriaTrigger implements ICriterionTrigger<Instance> {
   private static final ResourceLocation ID = new ResourceLocation("portalmod", "click_moon");

   public ResourceLocation func_192163_a() {
      return ID;
   }

   public void func_192165_a(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<Instance> p_192165_2_) {
   }

   public void func_192164_b(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<Instance> p_192164_2_) {
   }

   public void func_192167_a(PlayerAdvancements p_192167_1_) {
   }

   public Instance createInstance(JsonObject p_230307_1_, ConditionArrayParser p_230307_2_) {
      return null;
   }

   public static class Instance implements ICriterionInstance {
      public ResourceLocation func_192244_a() {
         return ClickMoonCriteriaTrigger.ID;
      }

      public JsonObject func_230240_a_(ConditionArraySerializer p_230240_1_) {
         return new JsonObject();
      }
   }
}
