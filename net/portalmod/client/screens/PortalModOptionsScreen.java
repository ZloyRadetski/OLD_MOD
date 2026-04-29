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

   private Button createSkinPageButton(int x, int y) {
      return new Button(x, y, 200, 20, this.getText("skins"), (b) -> {
         this.save();
         Minecraft.func_71410_x().func_147108_a(SkinSelectorScreen.getInstance(this));
      });
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int action) {
      this.RECURSION_SLIDER.func_231048_c_(mouseX, mouseY, action);
      return super.func_231048_c_(mouseX, mouseY, action);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_231165_f_(0);
      func_238472_a_(matrixStack, this.field_230712_o_, this.field_230704_d_, this.field_230708_k_ / 2, 20, 16777215);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
   }

   public void func_231175_as__() {
      this.close(false);
   }

   private void close(boolean goBackElseClose) {
      boolean prevMenu = (Boolean)PortalModConfigManager.MENU.get();
      this.save();
      if (prevMenu != (Boolean)PortalModConfigManager.MENU.get()) {
         MainMenuInjector.needsUpdate = true;
      }

      this.field_230706_i_.func_147108_a(!goBackElseClose && Minecraft.func_71410_x().field_71441_e != null ? null : this.lastScreen);
   }

   private void save() {
      PortalModConfigManager.CROSSHAIR.set(this.CROSSHAIR_BUTTON.getValue());
      PortalModConfigManager.RECURSION.set((int)Math.round(this.RECURSION_SLIDER.getValue()));
      PortalModConfigManager.RENDER_SELF.set(this.RENDER_SELF_BUTTON.getValue());
      PortalModConfigManager.HIGHLIGHTS.set(this.HIGHLIGHTS_BUTTON.getValue());
      PortalModConfigManager.TOOLTIPS.set(this.TOOLTIPS_BUTTON.getValue());
      PortalModConfigManager.MENU.set(this.MENU_BUTTON.getValue());
      PortalModConfigManager.PORTAL_FUNNELING.set(this.FUNNELING_BUTTON.getValue());
      PortalModConfigManager.SEPARATE_GUN.set(this.SEPARATE_GUN_BUTTON.getValue());
   }

   private static class RecursionSlider extends Slider {
      public RecursionSlider(int x, int y, int width, int height, ITextComponent prefix, ITextComponent suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, Button.IPressable handler) {
         super(x, y, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler);
      }

      protected void func_230441_a_(MatrixStack matrixStack, Minecraft minecraft, int x, int y) {
         if (this.field_230694_p_) {
            if (this.dragging) {
               this.sliderValue = (double)((float)(x - (this.field_230690_l_ + 4)) / (float)(this.field_230688_j_ - 8));
               this.updateSlider();
            }

            GuiUtils.drawContinuousTexturedBox(matrixStack, field_230687_i_, this.field_230690_l_ + (int)(this.sliderValue * (double)((float)(this.field_230688_j_ - 8))), this.field_230691_m_, 0, 66 + (this.func_230449_g_() ? 20 : 0), 8, this.field_230689_k_, 200, 20, 2, 3, 2, 2, (float)this.func_230927_p_());
         }

      }

      public void updateSlider() {
         super.updateSlider();
         int range = (int)this.maxValue - (int)this.minValue;
         this.sliderValue = (double)Math.round(this.sliderValue * (double)range) / (double)range;
      }
   }
}
