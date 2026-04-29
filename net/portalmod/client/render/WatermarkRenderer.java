package net.portalmod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class WatermarkRenderer {
   public static final ResourceLocation WM_LEFT = new ResourceLocation("portalmod", "textures/gui/watermark/playtester_left.png");
   public static final ResourceLocation WM_RIGHT = new ResourceLocation("portalmod", "textures/gui/watermark/playtester_right.png");

   public static void render(MatrixStack matrixStack) {
      MainWindow window = Minecraft.func_71410_x().func_228018_at_();
      int width = 256;
      int height = 32;
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE);
      RenderSystem.disableAlphaTest();
      Minecraft.func_71410_x().func_110434_K().func_110577_a(WM_LEFT);
      blit(matrixStack, 0, 0, 0, 0.0F, 0.0F, width, height, width, height);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(WM_RIGHT);
      blit(matrixStack, window.func_198107_o() - width, 0, 0, 0.0F, 0.0F, width, height, width, height);
   }

   private static void blit(MatrixStack matrixStack, int x, int y, int z, float u0, float v0, int uw, int uh, int width, int height) {
      innerBlit(matrixStack, x, x + uw, y, y + uh, z, uw, uh, u0, v0, width, height);
   }

   private static void innerBlit(MatrixStack matrixStack, int x0, int x1, int y0, int y1, int z, int uw, int uh, float u0, float v0, int width, int height) {
      innerBlit(matrixStack.func_227866_c_().func_227870_a_(), x0, x1, y0, y1, z, (u0 + 0.0F) / (float)width, (u0 + (float)uw) / (float)width, (v0 + 0.0F) / (float)height, (v0 + (float)uh) / (float)height);
   }

   private static void innerBlit(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
      BufferBuilder bufferbuilder = Tessellator.func_178181_a().func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_227851_o_);
      bufferbuilder.func_227888_a_(matrix, (float)x0, (float)y1, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u0, v1).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x1, (float)y1, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u1, v1).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x1, (float)y0, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u1, v0).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x0, (float)y0, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u0, v0).func_181675_d();
      bufferbuilder.func_178977_d();
      WorldVertexBufferUploader.func_181679_a(bufferbuilder);
   }
}
