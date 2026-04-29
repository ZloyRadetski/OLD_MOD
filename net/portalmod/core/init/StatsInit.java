package net.portalmod.core.init;

import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class StatsInit {
   public static final ResourceLocation PORTALS_SHOT;

   private StatsInit() {
   }

   public static void init() {
   }

   private static ResourceLocation makeCustomStat(String name, IStatFormatter formatter) {
      ResourceLocation resourcelocation = new ResourceLocation("portalmod", name);
      Registry.func_218322_a(Registry.field_212623_l, resourcelocation, resourcelocation);
      Stats.field_199092_j.func_199077_a(resourcelocation, formatter);
      return resourcelocation;
   }

   static {
      PORTALS_SHOT = makeCustomStat("portals_shot", IStatFormatter.field_223218_b_);
   }
}
