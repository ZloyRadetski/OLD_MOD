package net.portalmod.core.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyInit {
   public static KeyBinding PORTALGUN_INTERACT;

   private KeyInit() {
   }

   public static void init() {
      PORTALGUN_INTERACT = new KeyBinding("key.portalmod.portalgun_interact", 69, "key.category.portalmod");
      ClientRegistry.registerKeyBinding(PORTALGUN_INTERACT);
   }
}
