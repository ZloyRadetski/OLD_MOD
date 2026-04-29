package net.portalmod.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.portalmod.client.screens.widgets.ToggleButton;
import net.portalmod.common.sorted.portalgun.skins.SkinSelectorScreen;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.injectors.MainMenuInjector;

public class PortalModOptionsScreen extends Screen {
   private static final int BUTTON_WIDTH = 200;
   private static final int BUTTON_HEIGHT = 20;
   private final Screen lastScreen;
   private ToggleButton CROSSHAIR_BUTTON;
   private Slider RECURSION_SLIDER;
   private ToggleButton RENDER_SELF_BUTTON;
   private ToggleButton HIGHLIGHTS_BUTTON;
   private ToggleButton TOOLTIPS_BUTTON;
   private ToggleButton MENU_BUTTON;
   private ToggleButton FUNNELING_BUTTON;
   private ToggleButton SEPARATE_GUN_BUTTON;
   private Button SKIN_PAGE_BUTTON;

   public PortalModOptionsScreen(Screen lastScreen) {
      super(new TranslationTextComponent("options.portalmod.title"));
      this.lastScreen = lastScreen;
   }

   public static Button getAccessButton(Screen screen) {
      return new Button(screen.field_230708_k_ / 2 - 75, screen.field_230709_l_ / 6 + 144 - 6, 150, 20, new TranslationTextComponent("options.portalmod.button"), ($1) -> Minecraft.func_71410_x().func_147108_a(new PortalModOptionsScreen(screen)));
   }

   protected void func_231160_c_() {
      int baseY = 50;
      int stepY = 25;
      int x = this.field_230708_k_ / 2 - 200 - 5;
      this.CROSSHAIR_BUTTON = this.createToggleButton(x, baseY, "crosshair", (Boolean)PortalModConfigManager.CROSSHAIR.get());
      int y;
      this.RECURSION_SLIDER = this.createRecursionSlider(x, y = baseY + stepY);
      int var6;
      this.RENDER_SELF_BUTTON = this.createToggleButton(x, var6 = y + stepY, "render_self", (Boolean)PortalModConfigManager.RENDER_SELF.get());
      this.HIGHLIGHTS_BUTTON = this.createToggleButton(x, var6 + stepY, "highlights", (Boolean)PortalModConfigManager.HIGHLIGHTS.get());
      x = this.field_230708_k_ / 2 + 5;
      this.TOOLTIPS_BUTTON = this.createToggleButton(x, baseY, "tooltips", (Boolean)PortalModConfigManager.TOOLTIPS.get());
      this.MENU_BUTTON = this.createToggleButton(x, y = baseY + stepY, "menu", (Boolean)PortalModConfigManager.MENU.get());
      int var8;
      this.FUNNELING_BUTTON = this.createToggleButton(x, var8 = y + stepY, "portal_funneling", (Boolean)PortalModConfigManager.PORTAL_FUNNELING.get());
      this.SEPARATE_GUN_BUTTON = this.createToggleButton(x, y = var8 + stepY, "separate_gun", (Boolean)PortalModConfigManager.SEPARATE_GUN.get());
      this.SKIN_PAGE_BUTTON = this.createSkinPageButton(x, y + stepY);
      this.func_230480_a_(this.CROSSHAIR_BUTTON);
      this.func_230480_a_(this.RECURSION_SLIDER);
      this.func_230480_a_(this.RENDER_SELF_BUTTON);
      this.func_230480_a_(this.HIGHLIGHTS_BUTTON);
      this.func_230480_a_(this.TOOLTIPS_BUTTON);
      this.func_230480_a_(this.MENU_BUTTON);
      this.func_230480_a_(this.FUNNELING_BUTTON);
      this.func_230480_a_(this.SEPARATE_GUN_BUTTON);
      this.func_230480_a_(this.SKIN_PAGE_BUTTON);
      this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 100, this.field_230709_l_ / 6 + 168, 200, 20, DialogTexts.field_240632_c_, (p_213056_1_) -> this.close(true)));
   }

   private TranslationTextComponent getText(String id) {
      return new TranslationTextComponent("options.portalmod." + id);
   }

   private ToggleButton createToggleButton(int x, int y, String id, boolean value) {
      return new ToggleButton(x, y, 200, 20, this.getText(id), ($1) -> {
      }, value);
   }

   private RecursionSlider createRecursionSlider(int x, int y) {
      return new RecursionSlider(x, y, 200, 20, this.getText("recursion").func_240702_b_(": "), StringTextComponent.field_240750_d_, (double)0.0F, (double)9.0F, (double)(Integer)PortalModConfigManager.RECURSION.get(), false, true, ($1) -> {
      });
   }

   private Button cr