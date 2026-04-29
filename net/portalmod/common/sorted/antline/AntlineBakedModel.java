package net.portalmod.common.sorted.antline;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class AntlineBakedModel implements IDynamicBakedModel {
   private static final IModelData DEFAULT_MODEL_DATA = (new AntlineTileEntity.SideMap()).toModelData();

   public static ResourceLocation active(String string) {
      return new ResourceLocation("portalmod", "antline/" + string);
   }

   public static ResourceLocation inactive(String string) {
      return new ResourceLocation("portalmod", "antline/" + string);
   }

   public static List<ResourceLocation> getAllTextures() {
      List<ResourceLocation> textures = new ArrayList();
      textures.add(new ResourceLocation("portalmod", "antline/active_dot"));
      textures.add(new ResourceLocation("portalmod", "antline/active_corner"));
      textures.add(new ResourceLocation("portalmod", "antline/inactive_dot"));
      textures.add(new ResourceLocation("portalmod", "antline/inactive_corner"));
      return textures;
   }

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

   private BakedQuad createQuad(Vector3d vec1, Vector3d vec2, Vector3d vec3, Vector3d vec4, TextureAtlasSprite sprite) {
      Vector3d normal = vec3.func_178788_d(vec2).func_72431_c(vec1.func_178788_d(vec2)).func_72432_b();
      int u0 = 0;
      int v0 = 0;
      int u1 = sprite.func_94211_a();
      int v1 = sprite.func_94216_b();
      BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
      builder.setQuadOrientation(Direction.func_210769_a(normal.field_72450_a, normal.field_72448_b, normal.field_72449_c));
      this.putVertex(builder, normal, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, (float)u0, (float)v0, sprite, 1.0F, 1.0F, 1.0F);
      this.putVertex(builder, normal, vec2.field_72450_a, vec2.field_72448_b, vec2.field_72449_c, (float)u0, (float)v1, sprite, 1.0F, 1.0F, 1.0F);
      this.putVertex(builder, normal, vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c, (float)u1, (float)v1, sprite, 1.0F, 1.0F, 1.0F);
      this.putVertex(builder, normal, vec4.field_72450_a, vec4.field_72448_b, vec4.field_72449_c, (float)u1, (float)v0, sprite, 1.0F, 1.0F, 1.0F);
      return builder.build();
   }

   private Vector3d v(double x, double y, double z) {
      return new Vector3d(x, y, z);
   }

   private void addQuad(List<BakedQuad> quads, Direction side, Direction offset, ResourceLocation texture) {
      double d = 0.001;
      double u = 0.999;
      Vec3 y = new Vec3((side == Direction.UP ? side : side.func_176734_d()).func_176730_m());
      Vec3 z = new Vec3((side.func_176740_k().func_176722_c() ? Direction.DOWN : Direction.SOUTH).func_176730_m());
      Vec3 x = y.clone().cross(z);
      Mat4 relToAbs = new Mat4(x.x, y.x, z.x, (double)0.0F, x.y, y.y, z.y, (double)0.0F, x.z, y.z, z.z, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
      Vector3d absOffset = (new Vec3(offset.func_176730_m())).transform(relToAbs).mul((double)0.3125F).to3d();
      if (offset.func_176740_k().func_200128_b()) {
         absOffset = Vector3d.field_186680_a;
      }

      TextureAtlasSprite tex = (TextureAtlasSprite)Minecraft.func_71410_x().func_228015_a_(AtlasTexture.field_110575_b).apply(texture);
      switch (side) {
         case UP:
            quads.add(this.createQuad(this.v(u, u, d).func_178787_e(absOffset), this.v(u, u, u).func_178787_e(absOffset), this.v(d, u, u).func_178787_e(absOffset), this.v(d, u, d).func_178787_e(absOffset), tex));
            break;
         case DOWN:
            quads.add(this.createQuad(this.v(d, d, d).func_178787_e(absOffset), this.v(d, d, u).func_178787_e(absOffset), this.v(u, d, u).func_178787_e(absOffset), this.v(u, d, d).func_178787_e(absOffset), tex));
            break;
         case EAST:
            quads.add(this.createQuad(this.v(u, u, d).func_178787_e(absOffset), this.v(u, d, d).func_178787_e(absOffset), this.v(u, d, u).func_178787_e(absOffset), this.v(u, u, u).func_178787_e(absOffset), tex));
            break;
         case WEST:
            quads.add(this.createQuad(this.v(d, u, u).func_178787_e(absOffset), this.v(d, d, u).func_178787_e(absOffset), this.v(d, d, d).func_178787_e(absOffset), this.v(d, u, d).func_178787_e(absOffset), tex));
            break;
         case NORTH:
            quads.add(this.createQuad(this.v(d, u, d).func_178787_e(absOffset), this.v(d, d, d).func_178787_e(absOffset), this.v(u, d, d).func_178787_e(absOffset), this.v(u, u, d).func_178787_e(absOffset), tex));
            break;
         case SOUTH:
            quads.add(this.createQuad(this.v(u, u, u).func_178787_e(absOffset), this.v(u, d, u).func_178787_e(absOffset), this.v(d, d, u).func_178787_e(absOffset), this.v(d, u, u).func_178787_e(absOffset), tex));
      }

      switch (side) {
         case UP:
            quads.add(this.createQuad(this.v(d, u, d).func_178787_e(absOffset), this.v(d, u, u).func_178787_e(absOffset), this.v(u, u, u).func_178787_e(absOffset), this.v(u, u, d).func_178787_e(absOffset), tex));
            break;
         case DOWN:
            quads.add(this.createQuad(this.v(u, d, d).func_178787_e(absOffset), this.v(u, d, u).func_178787_e(absOffset), this.v(d, d, u).func_178787_e(absOffset), this.v(d, d, d).func_178787_e(absOffset), tex));
            break;
         case EAST:
            quads.add(this.createQuad(this.v(u, u, u).func_178787_e(absOffset), this.v(u, d, u).func_178787_e(absOffset), this.v(u, d, d).func_178787_e(absOffset), this.v(u, u, d).func_178787_e(absOffset), tex));
            break;
         case WEST:
            quads.add(this.createQuad(this.v(d, u, d).func_178787_e(absOffset), this.v(d, d, d).func_178787_e(absOffset), this.v(d, d, u).func_178787_e(absOffset), this.v(d, u, u).func_178787_e(absOffset), tex));
            break;
         case NORTH:
            quads.add(this.createQuad(this.v(u, u, d).func_178787_e(absOffset), this.v(u, d, d).func_178787_e(absOffset), this.v(d, d, d).func_178787_e(absOffset), this.v(d, u, d).func_178787_e(absOffset), tex));
            break;
         case SOUTH:
            quads.add(this.createQuad(this.v(d, u, u).func_178787_e(absOffset), this.v(d, d, u).func_178787_e(absOffset), this.v(u, d, u).func_178787_e(absOffset), this.v(u, u, u).func_178787_e(absOffset), tex));
      }

   }

   @Nonnull
   public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
      TileEntity te = world.func_175625_s(pos);
      return te instanceof AntlineTileEntity ? te.getModelData() : DEFAULT_MODEL_DATA;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideDir, @Nonnull Random rand, @Nonnull IModelData extraData) {
      List<BakedQuad> quads = new ArrayList();
      if (sideDir != null) {
         return Collections.emptyList();
      } else {
         AntlineTileEntity.SideMap sideMap = (AntlineTileEntity.SideMap)extraData.getData(AntlineTileEntity.SideMap.MODEL_PROPERTY);
         if (sideMap == null) {
            return Collections.emptyList();
         } else {
            sideMap.forEach((direction, side) -> {
               if (!side.isEmpty()) {
                  String path = side.isActive() ? "antline/active_" : "antline/inactive_";
                  if (side.getSideType() == AntlineTileEntity.Side.SideType.NORMAL) {
                     this.addQuad(quads, direction, Direction.UP, new ResourceLocation("portalmod", path + "dot"));
                  } else if (side.getSideType() == AntlineTileEntity.Side.SideType.CORNER) {
                     this.addQuad(quads, direction, Direction.UP, new ResourceLocation("portalmod", path + "corner"));
                  }

                  side.getConnections().forEach((connectionDir, b) -> {
                     if (b) {
                        this.addQuad(quads, direction, connectionDir, new ResourceLocation("portalmod", path + "dot"));
                     }

                  });
               }

            });
            return quads;
         }
      }
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
      return null;
   }

   public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
      return (TextureAtlasSprite)Minecraft.func_71410_x().func_228015_a_(AtlasTexture.field_110575_b).apply(inactive("inactive_dot"));
   }

   public ItemOverrideList func_188617_f() {
      return ItemOverrideList.field_188022_a;
   }
}
