package net.portalmod.core.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

public class TextureInit {
   private static final Registry BLOCKS;
   public static final ResourceLocation FAITHPLATE_TARGET;

   private TextureInit() {
   }

   public static void register(TextureStitchEvent.Pre event) {
      TextureInit.Registry.register(event);
   }

   static {
      BLOCKS = new Registry(AtlasTexture.field_110575_b);
      FAITHPLATE_TARGET = BLOCKS.register("block/faithplate_target");
   }

   private static class Registry {
      private static final Map<ResourceLocation, Registry> REGISTRIES = new HashMap();
      private final List<ResourceLocation> ENTRIES = new ArrayList();

      public Registry(ResourceLocation phase) {
         REGISTRIES.put(phase, this);
      }

      public ResourceLocation register(String name) {
         ResourceLocation location = new ResourceLocation("portalmod", name);
         this.ENTRIES.add(location);
         return location;
      }

      public static void register(TextureStitchEvent.Pre event) {
         ResourceLocation phase = event.getMap().func_229223_g_();
         if (REGISTRIES.containsKey(phase)) {
            for(ResourceLocation entry : ((Registry)REGISTRIES.get(phase)).ENTRIES) {
               event.addSprite(entry);
            }
         }

      }
   }
}
