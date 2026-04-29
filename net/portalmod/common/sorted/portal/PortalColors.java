package net.portalmod.common.sorted.portal;

import java.awt.Color;
import net.portalmod.core.util.Colour;

public enum PortalColors {
   black(3618362),
   blue(365559),
   brown(9722408),
   cyan(2866603),
   gray(3618362),
   green(5271066),
   light_blue(3326703),
   light_gray(10329495),
   lime(8439583),
   magenta(12601526),
   orange(16553517),
   pink(16280477),
   purple(8795847),
   red(13782569),
   white(14737368),
   yellow(15845172);

   private final Color color;

   private PortalColors(int hex) {
      this.color = new Color(hex);
   }

   public static int getIndex(String color) {
      return valueOf(color.toLowerCase()).ordinal();
   }

   public static Color getColor(String color) {
      return valueOf(color.toLowerCase()).color;
   }

   public static Colour getColour(String color) {
      return new Colour(getColor(color).getRGB());
   }

   public static Color getColor(PortalEntity portal) {
      return getColor(portal.getColor());
   }
}
