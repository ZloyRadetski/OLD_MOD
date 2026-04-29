package net.portalmod.common.blocks;

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

public class BakedModelWrapper implements IDynamicBakedModel {
   private final IBakedModel base;

   public BakedModelWrapper(IBakedModel base) {
      this.base = base;
   }

   public IBakedModel getBase() {
      return this.base;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      return this.base.func_200117_a(state, side, rand);
   }

   public boolean func_177555_b() {
      return this.base.func_177555_b();
   }

   public boolean func_177556_c() {
      return this.base.func_177556_c();
   }

   public boolean func_188618_c() {
      return true;
   }

   public boolean func_230044_c_() {
      return this.base.func_230044_c_();
   }

   public TextureAtlasSprite func_177554_e() {
      return this.base.func_177554_e();
   }

   public ItemOverrideList func_188617_f() {
      return this.base.func_188617_f();
   }
}
