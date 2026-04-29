package net.portalmod.core.util;

import net.minecraft.util.math.MathHelper;
import net.portalmod.core.math.Vec3;

public class Colour {
   public static final Colour WHITE = new Colour(1.0F, 1.0F, 1.0F, 1.0F);
   private int r;
   private int g;
   private int b;
   private int a;

   public Colour(int r, int g, int b, int a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }

   public Colour(float r, float g, float b, float a) {
      this(Math.round(r * 255.0F), Math.round(g * 255.0F), Math.round(b * 255.0F), Math.round(a * 255.0F));
   }

   public Colour(float[] rgb) {
      this(rgb[0], rgb[1], rgb[2], 1.0F);
   }

   public Colour(int argb) {
      this.a = argb >> 24 & 255;
      this.r = argb >> 16 & 255;
      this.g = argb >> 8 & 255;
      this.b = argb & 255;
   }

   public void lighten(float amount) {
      this.r = (int)MathHelper.func_76131_a((float)this.r + amount * 255.0F, 0.0F, 255.0F);
      this.g = (int)MathHelper.func_76131_a((float)this.g + amount * 255.0F, 0.0F, 255.0F);
      this.b = (int)MathHelper.func_76131_a((float)this.b + amount * 255.0F, 0.0F, 255.0F);
   }

   public void darken(float amount) {
      this.lighten(-amount);
   }

   public Colour opaque() {
      this.a = 255;
      return this;
   }

   public static Colour fromHSV(float h, float s, float v) {
      float c = v * s;
      float x = c * (1.0F - Math.abs(h / 60.0F % 2.0F - 1.0F));
      float m = v - c;
      float rr = 0.0F;
      float gg = 0.0F;
      float bb = 0.0F;
      if (h < 60.0F) {
         rr = c;
         gg = x;
      } else if (h < 120.0F) {
         rr = x;
         gg = c;
      } else if (h < 180.0F) {
         gg = c;
         bb = x;
      } else if (h < 240.0F) {
         gg = x;
         bb = c;
      } else if (h < 300.0F) {
         bb = c;
         rr = x;
      } else {
         bb = x;
         rr = c;
      }

      return new Colour(rr + m, gg + m, bb + m, 1.0F);
   }

   public Vec3 getHSV() {
      float rr = (float)this.r / 255.0F;
      float gg = (float)this.g / 255.0F;
      float bb = (float)this.b / 255.0F;
      float max = Math.max(rr, Math.max(gg, bb));
      float min = Math.min(rr, Math.min(gg, bb));
      float delta = max - min;
      float h = 0.0F;
      if (delta == 0.0F) {
         h = 0.0F;
      } else if (max == rr) {
         h = 60.0F * ((gg - bb) / delta % 6.0F);
      } else if (max == gg) {
         h = 60.0F * ((bb - rr) / delta + 2.0F);
      } else if (max == bb) {
         h = 60.0F * ((rr - gg) / delta + 4.0F);
      }

      return new Vec3((double)h, max == 0.0F ? (double)0.0F : (double)(delta / max), (double)max);
   }

   public int getValue() {
      return this.a & -16777216 | this.r & 16711680 | this.g & '\uff00' | this.b & 255;
   }

   public int getRGBValue() {
      return (this.r & 255) << 16 | (this.g & 255) << 8 | this.b & 255;
   }

   public int getIntR() {
      return this.r;
   }

   public int getIntG() {
      return this.g;
   }

   public int getIntB() {
      return this.b;
   }

   public int getIntA() {
      return this.a;
   }

   public float getFloatR() {
      return (float)this.r / 255.0F;
   }

   public float getFloatG() {
      return (float)this.g / 255.0F;
   }

   public float getFloatB() {
      return (float)this.b / 255.0F;
   }

   public float getFloatA() {
      return (float)this.a / 255.0F;
   }
}
