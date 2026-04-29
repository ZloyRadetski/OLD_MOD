package net.portalmod.core.util;

import java.util.function.Consumer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.portalmod.core.math.Mat4;

public class VertexRenderer {
   private final VertexFormat format;
   private final int mode;
   private VertexBuffer vb;

   public VertexRenderer(VertexFormat format, int mode) {
      this.format = format;
      this.mode = mode;
   }

   public void reset() {
      if (this.vb != null) {
         this.vb.close();
      }

      this.vb = new VertexBuffer(this.format);
   }

   public void data(Consumer<BufferBuilder> data) {
      BufferBuilder bufferbuilder = Tessellator.func_178181_a().func_178180_c();
      bufferbuilder.func_181668_a(this.mode, this.format);
      data.accept(bufferbuilder);
      bufferbuilder.func_178977_d();
      this.vb.func_227875_a_(bufferbuilder);
   }

   public void render(Mat4 mat) {
      this.vb.func_177359_a();
      this.format.func_227892_a_(0L);
      this.vb.func_227874_a_(mat.to4f(), this.mode);
      VertexBuffer.func_177361_b();
      this.format.func_227895_d_();
   }

   public void render() {
      this.render(Mat4.identity());
   }
}
