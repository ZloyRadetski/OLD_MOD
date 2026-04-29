package net.portalmod.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.portalmod.core.util.Colour;

public class AnimatedModel extends Model {
   private final Map<String, AnimationSet> animations = new HashMap();
   private final Map<ModelRenderer, InitialValuesHolder> initialValues = new HashMap();
   private final Map<Object, AnimationState> animationStates = new HashMap();
   public static final Target X = (model, x) -> model.field_78800_c = x;
   public static final Target Y = (model, y) -> model.field_78797_d = y;
   public static final Target Z = (model, z) -> model.field_78798_e = z;
   public static final Target XROT = (model, xRot) -> model.field_78795_f = xRot;
   public static final Target YROT = (model, yRot) -> model.field_78796_g = yRot;
   public static final Target ZROT = (model, zRot) -> model.field_78808_h = zRot;

   public AnimatedModel(int texWidth, int texHeight) {
      super(RenderType::func_228640_c_);
      this.field_78090_t = texWidth;
      this.field_78089_u = texHeight;
   }

   protected void attachAnimation(String name, ModelRenderer model, Target target, Animation animation) {
      this.initialValues.put(model, new InitialValuesHolder(model));
      this.animations.putIfAbsent(name, new AnimationSet());
      ((AnimationSet)this.animations.get(name)).putIfAbsent(model, new HashMap());
      ((HashMap)((AnimationSet)this.animations.get(name)).get(model)).put(target, animation);
   }

   private long getMillis() {
      return System.currentTimeMillis();
   }

   public void startAnimation(Object entity, String name) {
      this.animationStates.put(entity, new AnimationState((AnimationSet)this.animations.get(name), this.getMillis()));
   }

   public void startAnimation(String name) {
      this.startAnimation((Object)null, name);
   }

   private void computeAnimation(Object entity) {
      AnimationState state = (AnimationState)this.animationStates.get(entity);
      if (state == null) {
         this.skipAnimation();
      } else {
         long delta = this.getMillis() - state.time;
         state.animationSet.forEach((model, map) -> map.forEach((target, animation) -> target.accept(model, (float)animation.computeAbs(delta))));
      }
   }

   private void computeAnimation() {
      this.computeAnimation((Object)null);
   }

   private void skipAnimation() {
      this.initialValues.forEach((model, currentInitialValues) -> {
         model.field_78800_c = currentInitialValues.x;
         model.field_78797_d = currentInitialValues.y;
         model.field_78798_e = currentInitialValues.z;
         model.field_78795_f = currentInitialValues.xRot;
         model.field_78796_g = currentInitialValues.yRot;
         model.field_78808_h = currentInitialValues.zRot;
      });
   }

   public void render(Object entity, ModelRenderer model, MatrixStack matrixStack, IVertexBuilder vertexBuilder, int light, int overlay, Colour colour, boolean skipAnimation) {
      if (skipAnimation) {
         this.skipAnimation();
      } else {
         this.computeAnimation(entity);
      }

      model.func_228309_a_(matrixStack, vertexBuilder, light, overlay, colour.getFloatR(), colour.getFloatG(), colour.getFloatB(), colour.getFloatA());
   }

   public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int light, int overlay, float r, float g, float b, float a) {
   }

   private static class InitialValuesHolder {
      float x;
      float y;
      float z;
      float xRot;
      float yRot;
      float zRot;

      private InitialValuesHolder(ModelRenderer model) {
         this.x = model.field_78800_c;
         this.y = model.field_78797_d;
         this.z = model.field_78798_e;
         this.xRot = model.field_78795_f;
         this.yRot = model.field_78796_g;
         this.zRot = model.field_78808_h;
      }
   }

   private static final class AnimationSet extends HashMap<ModelRenderer, HashMap<Target, Animation>> {
      private AnimationSet() {
      }
   }

   private static final class AnimationState {
      public AnimationSet animationSet;
      public long time;

      public AnimationState(AnimationSet animationSet, long time) {
         this.animationSet = animationSet;
         this.time = time;
      }
   }

   private interface Target extends BiConsumer<ModelRenderer, Float> {
   }
}
