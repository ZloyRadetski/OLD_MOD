package net.portalmod.core.init;

import net.portalmod.client.render.Shader;
import net.portalmod.core.util.Registry;

public class ShaderInit {
   public static final Registry<Shader> REGISTRY = new Registry<Shader>();
   public static final Registry.Entry<Shader> PORTAL_FRAME;
   public static final Registry.Entry<Shader> PORTAL_HIGHLIGHT;
   public static final Registry.Entry<Shader> PORTAL_MASK;
   public static final Registry.Entry<Shader> FAITHPLATE_GUI;
   public static final Registry.Entry<Shader> FAITHPLATE_GRID;
   public static final Registry.Entry<Shader> BLIT;
   public static final Registry.Entry<Shader> LOADER;
   public static final Registry.Entry<Shader> COLOR;
   public static final Registry.Entry<Shader> ACTUAL_BLIT;
   public static final Registry.Entry<Shader> COLOR_PICKER_SV;
   public static final Registry.Entry<Shader> COLOR_PICKER_HUE;
   public static final Registry.Entry<Shader> COLOR_PICKER_SOLID;

   private ShaderInit() {
   }

   static {
      PORTAL_FRAME = REGISTRY.<Shader>register("portal_frame", () -> (new Shader.Builder()).add(35633, "portal/vertex.vsh").add(35632, "portal/frame.fsh").build());
      PORTAL_HIGHLIGHT = REGISTRY.<Shader>register("portal_highlight", () -> (new Shader.Builder()).add(35633, "portal/vertex.vsh").add(35632, "portal/highlight.fsh").build());
      PORTAL_MASK = REGISTRY.<Shader>register("portal_mask", () -> (new Shader.Builder()).add(35633, "portal/vertex.vsh").add(35632, "portal/mask.fsh").build());
      FAITHPLATE_GUI = REGISTRY.<Shader>register("faithplate_gui", () -> (new Shader.Builder()).add(35633, "gui/vertex.vsh").add(35632, "gui/fragment.fsh").build());
      FAITHPLATE_GRID = REGISTRY.<Shader>register("faithplate_grid", () -> (new Shader.Builder()).add(35633, "gui/vertex.vsh").add(35632, "gui/grid.fsh").build());
      BLIT = REGISTRY.<Shader>register("gui_blit", () -> (new Shader.Builder()).add(35633, "gui/blit.vsh").add(35632, "gui/blit.fsh").build());
      LOADER = REGISTRY.<Shader>register("loader", () -> (new Shader.Builder()).add(35633, "gui/loader/loader.vsh").add(35632, "gui/loader/loader.fsh").build());
      COLOR = REGISTRY.<Shader>register("color", () -> (new Shader.Builder()).add(35633, "color/color.vsh").add(35632, "color/color.fsh").build());
      ACTUAL_BLIT = REGISTRY.<Shader>register("actual_blit", () -> (new Shader.Builder()).add(35633, "blit/blit.vsh").add(35632, "blit/blit.fsh").build());
      COLOR_PICKER_SV = REGISTRY.<Shader>register("color_picker_sv", () -> (new Shader.Builder()).add(35633, "gui/color_picker/vertex.vsh").add(35632, "gui/color_picker/sv.fsh").build());
      COLOR_PICKER_HUE = REGISTRY.<Shader>register("color_picker_hue", () -> (new Shader.Builder()).add(35633, "gui/color_picker/vertex.vsh").add(35632, "gui/color_picker/hue.fsh").build());
      COLOR_PICKER_SOLID = REGISTRY.<Shader>register("color_picker_hue", () -> (new Shader.Builder()).add(35633, "gui/color_picker/vertex.vsh").add(35632, "gui/color_picker/solid.fsh").build());
   }
}
