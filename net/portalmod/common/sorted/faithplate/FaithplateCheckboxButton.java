package net.portalmod.common.sorted.faithplate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class FaithplateCheckboxButton extends AbstractButton {
   private static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/gui/checkbox.png");
   private boolean ticked;
   private boolean unavailable;

   public FaithplateCheckboxButton(int x, int y, int width, int height, ITextComponent textOn, boolean ticked) {
      super(x, y, width, height, textOn);
      this.ticked = ticked;
   }

   public void func_230930_b_() {
      if (!this.unavailable) {
         this.ticked = !this.ticked;
      }
   }

   public boolean selected() {
      return this.ticked;
   }

   public void setUnavailable(boolean unavailable) {
      this.unavailable = unavailable;
   }

   public void func_230431_b_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(TEXTURE);
      RenderSystem.enableDepthTest();
      FontRenderer fontRenderer = minecraft.field_71466_p;
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.field_230695_q_);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      func_238463_a_(matrixStack, this.field_230690_l_, this.field_230691_m_, this.unavailable ? 20.0F : 0.0F, this.ticked ? 20.0F : 0.0F, 20, this.field_230689_k_, 64, 64);
      func_238475_b_(matrixStack, fontRenderer, this.func_230458_i_(), this.field_230690_l_ + 24, this.field_230691_m_ + (this.field_230689_k_ - 8) / 2, this.unavailable ? 8289918 : 16777215);
   }
}
