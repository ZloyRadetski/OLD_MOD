package net.portalmod.core.init;

import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class EntityTagInit {
   public static final Tags.IOptionalNamedTag<EntityType<?>> FIZZLER_NO_ITEM_DROPS = tag("fizzler_no_item_drops");
   public static final Tags.IOptionalNamedTag<EntityType<?>> BUTTON_NO_PRESS = tag("super_button_lightweight");

   private EntityTagInit() {
   }

   public static void init() {
   }

   public static Tags.IOptionalNamedTag<EntityType<?>> tag(String name) {
      return EntityTypeTags.createOptional(new ResourceLocation("portalmod", name));
   }
}
