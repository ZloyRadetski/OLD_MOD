package net.portalmod.core.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.Color;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;

public class DebugRenderer {
   private static final HashMap<String, Tuple<VoxelShape, Color>> SHAPES = new HashMap();
   private static final VertexBuffer debugVG;
   private static final BufferBuilder builder;

   public static void putShape(String key, VoxelShape shape, Color color) {
      SHAPES.put(key, new Tuple(shape, color));
   }

   public static void removeShape(String key) {
      SHAPES.remove(key);
   }

   public static void renderAllShapes(MatrixStack matrixStack) {
      HashMap<String, Tuple<VoxelShape, Color>> temp = new HashMap(SHAPES);

      for(Tuple<VoxelShape, Color> shape : temp.values()) {
         for(AxisAlignedBB aabb : ((VoxelShape)shape.func_76341_a()).func_197756_d()) {
            renderBox(matrixStack.func_227866_c_().func_227870_a_(), aabb, (Color)shape.func_76340_b());
         }
      }

   }

   private static void renderBox(Matrix4f matrix, AxisAlignedBB aabb, Color color) {
      aabb = aabb.func_191194_a(Minecraft.func_71410_x().field_71460_t.func_215316_n().func_216785_c().func_216371_e());
      builder.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72337_e, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72337_e, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72337_e, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72337_e, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72337_e, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72337_e, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72337_e, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72337_e, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72337_e, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72337_e, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72337_e, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72337_e, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      float factor = 0.0625F;

      for(int i = 1; (double)((float)i * factor) < aabb.func_216364_b() + aabb.func_216360_c(); ++i) {
         float x0 = (float)i * factor;
         float y0 = 0.0F;
         if ((double)((float)i * factor) > aabb.func_216364_b()) {
            y0 = x0 - (float)aabb.func_216364_b();
            x0 = (float)aabb.func_216364_b();
         }

         float x1 = 0.0F;
         float y1 = (float)i * factor;
         if ((double)((float)i * factor) > aabb.func_216360_c()) {
            x1 = y1 - (float)aabb.func_216360_c();
            y1 = (float)aabb.func_216360_c();
         }

         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x0, (float)aabb.field_72338_b + y0, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x1, (float)aabb.field_72338_b + y1, (float)aabb.field_72339_c).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x0, (float)aabb.field_72338_b + y0, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x1, (float)aabb.field_72338_b + y1, (float)aabb.field_72334_f).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      }

      for(int i = 1; (double)((float)i * factor) < aabb.func_216362_d() + aabb.func_216360_c(); ++i) {
         float z0 = (float)i * factor;
         float y0 = 0.0F;
         if ((double)((float)i * factor) > aabb.func_216362_d()) {
            y0 = z0 - (float)aabb.func_216362_d();
            z0 = (float)aabb.func_216362_d();
         }

         float z1 = 0.0F;
         float y1 = (float)i * factor;
         if ((double)((float)i * factor) > aabb.func_216360_c()) {
            z1 = y1 - (float)aabb.func_216360_c();
            y1 = (float)aabb.func_216360_c();
         }

         builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b + y0, (float)aabb.field_72339_c + z0).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a, (float)aabb.field_72338_b + y1, (float)aabb.field_72339_c + z1).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b + y0, (float)aabb.field_72339_c + z0).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72336_d, (float)aabb.field_72338_b + y1, (float)aabb.field_72339_c + z1).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      }

      for(int i = 1; (double)((float)i * factor) < aabb.func_216364_b() + aabb.func_216362_d(); ++i) {
         float x0 = (float)i * factor;
         float z0 = 0.0F;
         if ((double)((float)i * factor) > aabb.func_216364_b()) {
            z0 = x0 - (float)aabb.func_216364_b();
            x0 = (float)aabb.func_216364_b();
         }

         float x1 = 0.0F;
         float z1 = (float)i * factor;
         if ((double)((float)i * factor) > aabb.func_216362_d()) {
            x1 = z1 - (float)aabb.func_216362_d();
            z1 = (float)aabb.func_216362_d();
         }

         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x0, (float)aabb.field_72338_b, (float)aabb.field_72339_c + z0).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x1, (float)aabb.field_72338_b, (float)aabb.field_72339_c + z1).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x0, (float)aabb.field_72337_e, (float)aabb.field_72339_c + z0).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
         builder.func_227888_a_(matrix, (float)aabb.field_72340_a + x1, (float)aabb.field_72337_e, (float)aabb.field_72339_c + z1).func_225586_a_(color.getRed(), color.getGreen(), color.getBlue(), 255).func_181675_d();
      }

      builder.func_178977_d();
      debugVG.func_227875_a_(builder);
      debugVG.func_177359_a();
      DefaultVertexFormats.field_181706_f.func_227892_a_(0L);
      debugVG.func_227874_a_(Matrix4f.func_226593_a_(1.0F, 1.0F, 1.0F), 1);
      DefaultVertexFormats.field_181706_f.func_227895_d_();
      VertexBuffer.func_177361_b();
   }

   static {
      debugVG = new VertexBuffer(DefaultVertexFormats.field_181706_f);
      builder = new BufferBuilder(1680000);
   }
}
