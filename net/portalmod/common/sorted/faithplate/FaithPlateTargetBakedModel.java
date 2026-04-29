package net.portalmod.common.sorted.faithplate;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.portalmod.core.init.TextureInit;

public class FaithPlateTargetBakedModel implements IDynamicBakedModel {
   private void putVertex(BakedQuadBuilder builder, Vector3d normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {
      ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().func_227894_c_().asList();

      for(int j = 0; j < elements.size(); ++j) {
         VertexFormatElement e = (VertexFormatElement)elements.get(j);
         switch (e.func_177375_c()) {
            case POSITION:
               builder.put(j, new float[]{(float)x, (float)y, (float)z, 1.0F});
               break;
            case COLOR:
               builder.put(j, new float[]{r, g, b, 1.0F});
               break;
            case UV:
               switch (e.func_177369_e()) {
                  case 0:
                     float iu = sprite.func_94214_a((double)u);
                     float iv = sprite.func_94207_b((double)v);
                     builder.put(j, new float[]{iu, iv});
                     continue;
                  case 2:
                     builder.put(j, new float[]{0.0F, 0.0F});
                     continue;
                  default:
                     builder.put(j, new float[0]);
                     continue;
               }
            case NORMAL:
               builder.put(j, new float[]{(float)normal.field_72450_a, (float)normal.field_72448_b, (float)normal.field_72449_c});
               break;
            default:
               builder.put(j, new float[0]);
         }
      }

   }

   private BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, TextureAtlasSprite sprite) {
      Vector3d normal = v3.func_178788_d(v2).func_72431_c(v1.func_178788_d(v2)).func_72432_b();
      int tw = sprite.func_94211_a();
      int th = sprite.func_94216_b();
      BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
      builder.setQuadOrientation(Direction.func_210769_a(normal.field_72450_a, normal.field_72448_b, normal.field_72449_c));
      this.putVertex(builder, normal, v1.field_72450_a, v1.field_72448_b, v1.field_72449_c, 0.0F, 0.0F, sprite, 1.0F, 1.0F, 1.0F);
      this.putVertex(builder, normal, v2.field_72450_a, v2.field_72448_b, v2.field_72449_c, 0.0F, (float)th, sprite, 1.0F, 1.0F, 1.0F);
      this.putVertex(builder, normal, v3.field_72450_a, v3.field_72448_b, v3.field_72449_c, (float)tw, (float)th, sprite, 1.0F, 1.0F, 1.0F);
      this.putVertex(builder, normal, v4.field_72450_a, v4.field_72448_b, v4.field_72449_c, (float)tw, 0.0F, sprite, 1.0F, 1.0F, 1.0F);
      return builder.build();
   }

   private Vector3d v(double x, double y, double z) {
      return new Vector3d(x, y, z);
   }

   private void addQuad(List<BakedQuad> quads, Direction side, TextureAtlasSprite texture, float offset) {
      double l = (double)(-offset);
      double r = (double)(1.0F + offset);
      switch (side) {
         case UP:
            quads.add(this.createQuad(this.v(l, r, l), this.v(l, r, r), this.v(r, r, r), this.v(r, r, l), texture));
            break;
         case DOWN:
            quads.add(this.createQuad(this.v(l, l, r), this.v(l, l, l), this.v(r, l, l), this.v(r, l, r), texture));
            break;
         case EAST:
            quads.add(this.createQuad(this.v(r, r, r), this.v(r, l, r), this.v(r, l, l), this.v(r, r, l), texture));
            break;
         case WEST:
            quads.add(this.createQuad(this.v(l, r, l), this.v(l, l, l), this.v(l, l, r), this.v(l, r, r), texture));
            break;
         case NORTH:
            quads.add(this.createQuad(this.v(r, r, l), this.v(r, l, l), this.v(l, l, l), this.v(l, r, l), texture));
            break;
         case SOUTH:
            quads.add(this.createQuad(this.v(l, r, r), this.v(l, l, r), this.v(r, l, r), this.v(r, r, r), texture));
      }

   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      return Collections.emptyList();
   }

   public BakedQuad getQuad(Direction side, float offset) {
      List<BakedQuad> quads = new ArrayList();
      this.addQuad(quads, side, (TextureAtlasSprite)Minecraft.func_71410_x().func_228015_a_(AtlasTexture.field_110575_b).apply(TextureInit.FAITHPLATE_TARGET), offset);
      return (BakedQuad)quads.get(0);
   }

   public boolean func_177555_b() {
      return true;
   }

   public boolean func_177556_c() {
      return true;
   }

   public boolean func_188618_c() {
      return false;
   }

   public boolean func_230044_c_() {
      return true;
   }

   public TextureAtlasSprite func_177554_e() {
      return (TextureAtlasSprite)Minecraft.func_71410_x().func_228015_a_(AtlasTexture.field_110575_b).apply(TextureInit.FAITHPLATE_TARGET);
   }

   public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
      return (TextureAtlasSprite)Minecraft.func_71410_x().func_228015_a_(AtlasTexture.field_110575_b).apply(TextureInit.FAITHPLATE_TARGET);
   }

   public ItemOverrideList func_188617_f() {
      return ItemOverrideList.field_188022_a;
   }
}
