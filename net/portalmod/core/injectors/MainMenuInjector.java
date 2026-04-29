package net.portalmod.core.injectors;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.util.Splashes;
import net.minecraft.util.ResourceLocation;
import net.portalmod.mixins.accessors.MainMenuScreenAccessor;
import net.portalmod.mixins.accessors.MinecraftAccessor;
import net.portalmod.mixins.accessors.SplashesAccessor;

public class MainMenuInjector {
   private static final int NO_PANORAMAS = 7;
   private static final ResourceLocation EDITION = new ResourceLocation("portalmod", "textures/gui/title/edition.png");
   private static final ResourceLocation SPLASHES = new ResourceLocation("portalmod", "texts/splashes.txt");
   private static ResourceLocation prevEdition;
   private static RenderSkyboxCube prevCubeMap;
   private static ResourceLocation prevSplashes;
   public static boolean fading = true;
   public static boolean needsUpdate = true;

   public static MainMenuScreen getInjectedMenu(boolean custom, boolean fadeIn) {
      if (prevEdition == null) {
         prevEdition = MainMenuScreenAccessor.pmGetEdition();
      }

      if (prevCubeMap == null) {
         prevCubeMap = MainMenuScreenAccessor.pmGetCubeMap();
      }

      if (prevSplashes == null) {
         prevSplashes = SplashesAccessor.pmGetLocation();
      }

      RenderSkyboxCube CUBEMAP = new RenderSkyboxCube(new ResourceLocation("portalmod", "textures/gui/title/background/panorama" + (long)(Math.random() * (double)7.0F)));
      MainMenuScreenAccessor.pmSetCubeMap(custom ? CUBEMAP : prevCubeMap);
      if (!needsUpdate) {
         return new MainMenuScreen(false);
      } else {
         MainMenuScreenAccessor.pmSetEdition(custom ? EDITION : prevEdition);
         SplashesAccessor.pmSetLocation(custom ? SPLASHES : prevSplashes);

         try {
            Minecraft minecraft = Minecraft.func_71410_x();
            Splashes splashes = new Splashes(Minecraft.func_71410_x().func_110432_I());
            List<String> splashList = ((SplashesAccessor)splashes).pmPrepare(minecraft.func_195551_G(), minecraft.func_213239_aq());
            ((SplashesAccessor)splashes).pmApply(splashList, minecraft.func_195551_G(), minecraft.func_213239_aq());
            ((MinecraftAccessor)Minecraft.func_71410_x()).pmSetSplashManager(splashes);
         } catch (Exception e) {
            e.printStackTrace();
         }

         return new MainMenuScreen(false);
      }
   }
}
