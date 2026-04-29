package net.portalmod.client.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;

public class DynamicTextureVertexBuilder implements IVertexBuilder {
   private final IVertexBuilder real;
   private float offsetU;
   private float offsetV;

   public DynamicTextureVertexBuilder(IVertexBuilder real) {
      this.real = real;
   }

   public void setOffset(float u, float v) {
      this.offsetU = u;
      this.offsetV = v;
   }

   public IVertexBuilder func_225582_a_(double x, double y, double z) {
      return this.real.func_225582_a_(x, y, z);
   }

   public IVertexBuilder func_225586_a_(int r, int g, int b, int a) {
      return this.real.func_225586_a_(r, g, b, a);
   }

   public IVertexBuilder func_225583_a_(float u, float v) {
      return this.real.func_225583_a_(u + this.offsetU, v + this.offsetV);
   }

   public IVertexBuilder func_225585_a_(int u, int v) {
      return this.real.func_225585_a_(u, v);
   }

   public IVertexBuilder func_225587_b_(int u, int v) {
      return this.real.func_225587_b_(u, v);
   }

   public IVertexBuilder func_225584_a_(float x, float y, float z) {
      return this.real.func_225584_a_(x, y, z);
   }

   public void func_181675_d() {
      this.real.func_181675_d();
   }
}
