package net.portalmod.common.sorted.portal;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.SimpleTexture.TextureData;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PortalAnimatedTextureHelper {
   private static final HashMap<ResourceLocation, Optional<Dimension>> TEXTURE_SIZES = new HashMap();

   public static Optional<Dimension> getTextureSize(ResourceLocation location) {
      IResourceManager rm = Minecraft.func_71410_x().func_195551_G();
      if (!TEXTURE_SIZES.containsKey(location)) {
         try {
            SimpleTexture.TextureData data = TextureData.func_217799_a(rm, location);
            Throwable var3 = null;

            try {
               TEXTURE_SIZES.put(location, Optional.of(new Dimension(data.func_217800_b().func_195702_a(), data.func_217800_b().func_195714_b())));
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (data != null) {
                  if (var3 != null) {
                     try {
                        data.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     data.close();
                  }
               }

            }
         } catch (Exception var15) {
            TEXTURE_SIZES.put(location, Optional.empty());
         }
      }

      return (Optional)TEXTURE_SIZES.get(location);
   }
}
