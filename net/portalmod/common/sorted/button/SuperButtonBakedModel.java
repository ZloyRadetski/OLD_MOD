package net.portalmod.common.sorted.button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.portalmod.core.math.BiHashMap;

public class SuperButtonBakedModel implements IDynamicBakedModel {
   private final BiHashMap<Direction, QuadBlockCorner, IBakedModel> variants = new BiHashMap<Direction, QuadBlockCorner, IBakedModel>();
   private final IBakedModel base;

   public SuperButtonBakedModel(IBakedModel base) {
      this.base = base;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      IBakedModel variant = this.variants.get(state.func_177229_b(SuperButtonBlock.FACING), state.func_177229_b(SuperButtonBlock.CORNER));
      return (List<BakedQuad>)(variant == null ? new ArrayList() : variant.func_200117_a(state, side, rand));
   }

   public void addVariant(Direction facing, QuadBlockCorner corner, IBakedModel variant) {
      this.variants.put(facing, corner, variant);
   }

   public boolean func_177555_b() {
      return this.base.func_177555_b();
   }

   public boolean func_177556_c() {
      return this.base.func_177556_c();
   }

   public boolean func_188618_c() {
      return this.base.func_188618_c();
   }

   public boolean func_230044_c_() {
      return this.base.func_230044_c_();
   }

   public TextureAtlasSprite func_177554_e() {
      return this.base.func_177554_e();
   }

   public ItemOverrideList func_188617_f() {
      return ItemOverrideList.field_188022_a;
   }
}
