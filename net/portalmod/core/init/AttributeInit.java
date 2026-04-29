package net.portalmod.core.init;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeInit {
   public static final DeferredRegister<Attribute> ATTRIBUTES;
   public static final RegistryObject<Attribute> BUTTON_REACH;
   public static final RegistryObject<Attribute> GRAB_REACH;

   private AttributeInit() {
   }

   private static RegistryObject<Attribute> register(String name, double base, double min, double max) {
      return ATTRIBUTES.register("generic." + name, () -> (new RangedAttribute("attribute.name.generic.portalmod." + name, base, min, max)).func_233753_a_(true));
   }

   static {
      ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "portalmod");
      BUTTON_REACH = register("button_reach", (double)2.0F, (double)0.0F, (double)2048.0F);
      GRAB_REACH = register("grab_reach", (double)2.0F, (double)0.0F, (double)2048.0F);
   }
}
