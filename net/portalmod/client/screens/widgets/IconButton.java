package net.portalmod.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class IconButton extends Button {
   private static final int SIZE = 20;
   private final ResourceLocation texture;
   private final int u;
   private final int v;

   public IconButton(int x, int y, ResourceLocation texture, int u, int v, Button.IPressable onClick) {
      super(x, y, 20, 20, StringTextComponent.field_240750_d_, onClick);
      this.texture = texture;
      this.u = u;
      this.v = v;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      if (this.field_230694_p_) {
         Minecraft minecraft = Minecraft.func_71410_x();
         minecraft.func_110434_K().func_110577_a(this.texture);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         func_238463_a_(matrixStack, this.field_230690_l_ + 2, this.field_230691_m_ + 2, (float)this.u, (float)(this.v + (this.field_230693_o_ ? 0 : 16)), 16, 16, 512, 512);
      }

   }
}
