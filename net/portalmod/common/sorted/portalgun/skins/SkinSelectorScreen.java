package net.portalmod.common.sorted.portalgun.skins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Rectangle;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.portalmod.client.animation.PortalGunAnimatedTexture;
import net.portalmod.client.render.Shader;
import net.portalmod.client.screens.widgets.IconButton;
import net.portalmod.core.init.ShaderInit;
import net.portalmod.core.util.Colour;

public class SkinSelectorScreen extends Screen {
   private static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/gui/skinselector.png");
   private static final int WIDTH = 328;
   private static final int HEIGHT = 172;
   private static SkinSelectorScreen instance;
   private Screen lastScreen;
   private long loadingStart = -1L;
   private static final int SKIN_PREVIEW_X = 211;
   private static final int SKIN_PREVIEW_Y = 11;
   private static final int SKIN_PREVIEW_WIDTH = 100;
   private static final int SKIN_PREVIEW_HEIGHT = 100;
   private SkinPreviewWidget skinPreviewWidget;
   private static final int COLOR_PICKER_X = 218;
   private static final int COLOR_PICKER_Y = 113;
   private static final int COLOR_PICKER_WIDTH = 75;
   private static final int COLOR_PICKER_HEIGHT = 57;
   private ColorPickerWidget colorPickerWidget;
   private Rectangle colorPickerMaskRegion;
   private static final int SKIN_LIST_X = 8;
   private static final int SKIN_LIST_Y = 18;
   private static final int SKIN_LIST_WIDTH = 189;
   private static final int SKIN_LIST_HEIGHT = 142;
   private static final int SKIN_ENTRY_HEIGHT = 30;
   private final List<SkinEntryWidget> skinEntryList = new ArrayList();
   private SkinEntryWidget selectedSkin;
   private Rectangle listRegion;
   private static final int SCROLLBAR_THUMB_U = 336;
   private static final int SCROLLBAR_THUMB_V = 0;
   private static final int SCROLLBAR_THUMB_EDGE_HEIGHT = 4;
   private static final int SCROLLBAR_THUMB_CENTER_HEIGHT = 6;
   private static final int SCROLLBAR_THUMB_WIDTH = 7;
   private static final int SCROLL_AMOUNT = 15;
   private float scrollOffset;
   private boolean draggingScrollbar;
   private float draggingScrollbarRelativeY;
   private Rectangle scrollbarRegion;
   private static final int REFRESH_TIMEOUT = 300000;
   private static final int APPLY_BUTTON_WIDTH = 70;
   private Button infoButton;
   private Button refreshButton;
   private Button applyButton;
   private volatile long lastRefresh = 0L;

   private SkinSelectorScreen() {
      super(new TranslationTextComponent("options.portalmod.skins.title"));
   }

   public static SkinSelectorScreen getInstance(Screen lastScreen) {
      if (instance == null) {
         instance = new SkinSelectorScreen();
      }

      instance.lastScreen = lastScreen;
      instance.scrollOffset = 0.0F;
      return instance;
   }

   protected void func_231160_c_() {
      this.initRefreshButton();
      this.initInfoButton();
      this.initApplyButton();
      this.skinPreviewWidget = new SkinPreviewWidget(this.getX() + 211, this.getY() + 11, 100, 100, this);
      this.func_230481_d_(this.skinPreviewWidget);
      this.colorPickerWidget = new ColorPickerWidget(this.getX() + 218, this.getY() + 113, 75, 57);
      this.func_230481_d_(this.colorPickerWidget);
      this.colorPickerMaskRegion = new Rectangle(this.getX() + 214, this.getY() + 111, 97, 61);
      this.listRegion = new Rectangle(this.getX() + 8, this.getY() + 18, 189, 142);
      this.scrollbarRegion = new Rectangle(this.listRegion.x + this.listRegion.width + 1, this.listRegion.y + 1, 7, this.listRegion.height - 2);
      if (!this.isLoading()) {
         this.initSkinList();
      }

   }

   private void initSkinList() {
      this.skinEntryList.clear();
      this.field_230705_e_.removeIf((child) -> child instanceof SkinEntryWidget);
      List<PortalGunSkin> skins = (List)ClientSkinManager.getInstance().getSkinCatalog().values().stream().filter((skinx) -> ClientSkinManager.getInstance().playerHasSkin((UUID)null, skinx.skin_id)).collect(Collectors.toList());
      int i = 0;

      for(PortalGunSkin skin : skins) {
         if (ClientSkinManager.getInstance().getSkinTexture(skin.skin_id) instanceof PortalGunAnimatedTexture) {
            SkinEntryWidget widget = new SkinEntryWidget(this.listRegion.x, this.listRegion.y + 30 * i++, this.listRegion.width, 30, this, this.skinPreviewWidget, skin);
            this.skinEntryList.add(widget);
            this.func_230481_d_(widget);
         }
      }

      if (!this.skinEntryList.isEmpty()) {
         Optional<SkinEntryWidget> optionalEntry = this.skinEntryList.stream().filter((entry) -> entry.getSkin().skin_id.equals(ClientSkinManager.getInstance().getSelectedSkinForPlayer((UUID)null))).findAny();
         if (optionalEntry.isPresent()) {
            this.selectEntry((SkinEntryWidget)optionalEntry.get(), false);
         } else {
            ClientSkinManager.getInstance().setConfigSelectedSkin("default");
            Optional<SkinEntryWidget> optionalDefault = this.skinEntryList.stream().filter((entry) -> entry.getSkin().skin_id.equals("default")).findAny();
            if (optionalDefault.isPresent()) {
               this.selectEntry((SkinEntryWidget)optionalDefault.get(), false);
            } else {
               this.selectEntry((SkinEntryWidget)this.skinEntryList.get(0), false);
            }
         }
      }

   }

   private void initInfoButton() {
      int height = 20;
      int x = this.getX() + 211 - 70 + 1 - (height + 1) * 2;
      int y = this.getY() + 172 + 1;
      this.infoButton = new IconButton(x, y, TEXTURE, 0, 208, (button) -> {
         try {
            Util.func_110647_a().func_195642_a(new URI("https://portalmod.net/info-skins"));
         } catch (URISyntaxException var2) {
         }

      });
      this.func_230480_a_(this.infoButton);
   }

   private void initRefreshButton() {
      int height = 20;
      int x = this.getX() + 211 - 70 + 1 - height - 1;
      int y = this.getY() + 172 + 1;
      this.refreshButton = new IconButton(x, y, TEXTURE, 16, 208, (button) -> {
         long millis = System.currentTimeMillis();
         this.loadingStart = millis;
         this.lastRefresh = millis;
         ClientSkinManager.getInstance().onSkinCatalogRefresh();
         ClientSkinManager.getInstance().enqueueCallback(() -> {
            this.colorPickerWidget.init();
            this.initSkinList();
            this.loadingStart = -1L;
         });
      });
      this.refreshButton.field_230693_o_ = System.currentTimeMillis() - this.lastRefresh >= 300000L;
      this.func_230480_a_(this.refreshButton);
   }

   private void initApplyButton() {
      int height = 20;
      int x = this.getX() + 211 - 70 + 1;
      int y = this.getY() + 172 + 1;
      TranslationTextComponent text = new TranslationTextComponent("options.portalmod.skins.apply");
      this.applyButton = new Button(x, y, 70, height, text, (button) -> {
         ClientSkinManager.getInstance().onSkinSelected(this.selectedSkin.getSkin().skin_id, this.colorPickerWidget.getTint().getRGBValue());
         this.close(false);
      });
      this.func_230480_a_(this.applyButton);
   }

   private int getX() {
      return (this.field_230708_k_ - 328) / 2;
   }

   private int getY() {
      return (this.field_230709_l_ - 172) / 2;
   }

   public void selectEntry(SkinEntryWidget entry, boolean animate) {
      this.skinEntryList.forEach((item) -> item.setSelected(false, false));
      entry.setSelected(true, animate);
      if (animate) {
         if (this.selectedSkin != null && !this.selectedSkin.getSkin().tintable && entry.getSkin().tintable) {
            this.colorPickerWidget.startShowAnimation();
         }

         if (this.selectedSkin != null && this.selectedSkin.getSkin().tintable && !entry.getSkin().tintable) {
            this.colorPickerWidget.startHideAnimation();
         }
      }

      this.selectedSkin = entry;
   }

   public Colour getSkinTint() {
      return this.selectedSkin.getSkin().tintable ? this.colorPickerWidget.getTint() : Colour.WHITE;
   }

   public void func_238651_a_(MatrixStack matrixStack, int i) {
      super.func_238651_a_(matrixStack, i);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      func_238463_a_(matrixStack, this.getX(), this.getY(), 0.0F, 0.0F, 328, 172, 512, 512);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, (float)this.getX() + 8.0F, (float)this.getY() + 6.0F, 4210752);
      if (!this.isLoading()) {
         this.skinPreviewWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
         this.renderColorPicker(matrixStack, mouseX, mouseY, partialTicks);
         this.renderSkinList(matrixStack, mouseX, mouseY, partialTicks);
         this.renderScrollbar(matrixStack);
      } else {
         this.renderLoadingBlob();
      }

      this.infoButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.refreshButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.applyButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
   }

   private void setupScissorTest(Rectangle rect) {
      float scale = (float)Minecraft.func_71410_x().func_228018_at_().func_198100_s();
      RenderSystem.enableScissor((int)(scale * (float)rect.x), (int)(scale * (float)(this.field_230709_l_ - (rect.y + rect.height))), (int)(scale * (float)rect.width), (int)(scale * (float)rect.height));
   }

   private void drawTexturedRectangle(MatrixStack matrixStack, Rectangle rectangle) {
      Matrix4f matrix = matrixStack.func_227866_c_().func_227870_a_();
      int x0 = rectangle.x;
      int y0 = rectangle.y;
      int x1 = rectangle.x + rectangle.width;
      int y1 = rectangle.y + rectangle.height;
      BufferBuilder bufferbuilder = Tessellator.func_178181_a().func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      bufferbuilder.func_227888_a_(matrix, (float)x0, (float)y0, 0.0F).func_225583_a_(0.0F, 1.0F).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x0, (float)y1, 0.0F).func_225583_a_(0.0F, 0.0F).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x1, (float)y1, 0.0F).func_225583_a_(1.0F, 0.0F).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x1, (float)y0, 0.0F).func_225583_a_(1.0F, 1.0F).func_181675_d();
      bufferbuilder.func_178977_d();
      WorldVertexBufferUploader.func_181679_a(bufferbuilder);
   }

   private void renderColorPicker(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.selectedSkin.getSkin().tintable || this.colorPickerWidget.isAnimating()) {
         this.setupScissorTest(this.colorPickerMaskRegion);
         this.colorPickerWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
         RenderSystem.disableScissor();
      }
   }

   private void renderSkinList(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_((double)0.0F, (double)(-this.scrollOffset * (float)this.getScrollableRange()), (double)0.0F);
      this.setupScissorTest(this.listRegion);
      this.skinEntryList.forEach((skinEntryWidget) -> skinEntryWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks));
      RenderSystem.disableScissor();
      matrixStack.func_227865_b_();
   }

   private void renderScrollbar(MatrixStack matrixStack) {
      if (this.getListHeight() > this.listRegion.height) {
         Minecraft.func_71410_x().func_110434_K().func_110577_a(TEXTURE);
         int yOffset = 0;
         int vOffset = 0;
         this.blitScrollbarThumbPart(matrixStack, yOffset, vOffset, 4);
         yOffset += 4;

         for(vOffset += 4; yOffset < this.getScrollbarThumbHeight() - 4; yOffset += 6) {
            this.blitScrollbarThumbPart(matrixStack, yOffset, vOffset, 6);
         }

         vOffset += 6;
         this.blitScrollbarThumbPart(matrixStack, yOffset, vOffset, 4);
      }
   }

   private void blitScrollbarThumbPart(MatrixStack matrixStack, int yOffset, int vOffset, int height) {
      Rectangle thumbRegion = this.getScrollbarThumbRegion();
      func_238463_a_(matrixStack, thumbRegion.x, thumbRegion.y + yOffset, 336.0F, (float)(0 + vOffset), 7, height, 512, 512);
   }

   private void renderLoadingBlob() {
      MainWindow mainWindow = Minecraft.func_71410_x().func_228018_at_();
      float scale = (float)mainWindow.func_198100_s();
      Matrix4f projection = Matrix4f.func_195877_a((float)mainWindow.func_198109_k(), (float)mainWindow.func_198091_l(), 1000.0F, ForgeHooksClient.getGuiFarPlane());
      RenderSystem.enableBlend();
      ((Shader)ShaderInit.LOADER.get()).bind().setFloat("resolution", (float)this.listRegion.width, (float)this.listRegion.height).setFloat("millis", (float)(System.currentTimeMillis() - this.loadingStart) / 1000.0F);
      MatrixStack combined = new MatrixStack();
      combined.func_227862_a_(1.0F, -1.0F, 1.0F);
      combined.func_227866_c_().func_227870_a_().func_226595_a_(projection);
      combined.func_227861_a_((double)0.0F, (double)0.0F, (double)-2000.0F);
      combined.func_227862_a_(scale, scale, 1.0F);
      this.drawTexturedRectangle(combined, this.listRegion);
      ((Shader)ShaderInit.LOADER.get()).unbind();
   }

   private int getScrollbarThumbHeight() {
      int scrollbarHeight = (int)((float)this.listRegion.height / (float)this.getListHeight() * (float)this.scrollbarRegion.height);
      return (scrollbarHeight - 8) / 6 * 6 + 8;
   }

   private int getScrollbarThumbRange() {
      return this.scrollbarRegion.height - this.getScrollbarThumbHeight();
   }

   private int getScrollableRange() {
      return this.getListHeight() - this.listRegion.height;
   }

   private Rectangle getScrollbarThumbRegion() {
      return new Rectangle(this.scrollbarRegion.x, this.scrollbarRegion.y + (int)(this.scrollOffset * (float)this.getScrollbarThumbRange()), 7, this.getScrollbarThumbHeight());
   }

   private int getListHeight() {
      return this.skinEntryList.size() * 30;
   }

   private boolean isLoading() {
      return this.loadingStart != -1L;
   }

   public void func_231023_e_() {
      super.func_231023_e_();
      this.refreshButton.field_230693_o_ = System.currentTimeMillis() - this.lastRefresh >= 300000L;
      this.skinPreviewWidget.tick();
   }

   public boolean func_231044_a_(double x, double y, int button) {
      if (this.getScrollbarThumbRegion().contains(x, y)) {
         this.draggingScrollbar = true;
         this.draggingScrollbarRelativeY = (float)y - (float)this.getScrollbarThumbRegion().y;
      }

      boolean onList = this.listRegion.contains(x, y);
      if (onList) {
         y += (double)(this.scrollOffset * (float)this.getScrollableRange());
      }

      for(IGuiEventListener child : this.func_231039_at__()) {
         if (this.isLoading()) {
            return false;
         }

         if (onList == (child instanceof SkinEntryWidget)) {
            if (child instanceof ColorPickerWidget && !this.selectedSkin.getSkin().tintable) {
               return false;
            }

            if (child.func_231044_a_(x, y, button)) {
               this.func_231035_a_(child);
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_231048_c_(double x, double y, int button) {
      this.draggingScrollbar = false;
      this.skinPreviewWidget.mouseReleasedAnywhere(button);
      this.colorPickerWidget.mouseReleasedAnywhere(button);
      return super.func_231048_c_(x, y, button);
   }

   public void func_212927_b(double x, double y) {
      if (this.draggingScrollbar) {
         this.scrollOffset = (float)(y - (double)this.scrollbarRegion.y - (double)this.draggingScrollbarRelativeY) / (float)this.getScrollbarThumbRange();
         this.scrollOffset = MathHelper.func_76131_a(this.scrollOffset, 0.0F, 1.0F);
      }

      this.skinPreviewWidget.func_212927_b(x, y);
      this.colorPickerWidget.func_212927_b(x, y);
   }

   public boolean func_231043_a_(double x, double y, double amount) {
      if (this.listRegion.contains(x, y) && this.listRegion.height <= this.getListHeight()) {
         this.scrollOffset -= (float)amount * 15.0F / (float)this.getScrollableRange();
         this.scrollOffset = MathHelper.func_76131_a(this.scrollOffset, 0.0F, 1.0F);
      }

      return super.func_231043_a_(x, y, amount);
   }

   public void func_231175_as__() {
      this.close(true);
   }

   private void close(boolean goBackElseClose) {
      this.field_230706_i_.func_147108_a(!goBackElseClose && Minecraft.func_71410_x().field_71441_e != null ? null : this.lastScreen);
   }
}
