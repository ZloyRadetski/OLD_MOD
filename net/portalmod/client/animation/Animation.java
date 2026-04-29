package net.portalmod.client.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Animation {
   private final List<Part> parts;
   private long start;

   private Animation(List<Part> parts) {
      this.parts = parts;
      this.start = 0L;
   }

   public void start(long millis) {
      this.start = millis;
   }

   public void start() {
      this.start(System.currentTimeMillis());
   }

   public void stop() {
      this.start = (long)((double)System.currentTimeMillis() - 1.0E12);
   }

   public double computeAbs(long time) {
      double accum = (double)0.0F;
      Part selected = null;

      for(Part part : this.parts) {
         if ((double)time < accum + part.duration) {
            selected = part;
            break;
         }

         accum += part.duration;
      }

      return selected == null ? ((Part)this.parts.get(this.parts.size() - 1)).end : selected.compute((double)time - accum);
   }

   public double compute(long time) {
      return this.computeAbs(time - this.start);
   }

   public static class Builder {
      private final List<Part> parts;
      private double lastValue;

      public Builder(double startValue) {
         this.parts = new ArrayList();
         this.lastValue = startValue;
      }

      public Builder() {
         this((double)0.0F);
      }

      public Builder segment(Curve type, double start, double end, double duration) {
         this.parts.add(new Part(type, start, end, duration));
         this.lastValue = end;
         return this;
      }

      public Builder keyframe(Curve type, double end, double duration) {
         return this.segment(type, this.lastValue, end, duration);
      }

      public Animation build() {
         return new Animation(this.parts);
      }
   }

   private static class Part {
      private final Curve type;
      private final double start;
      private final double end;
      private final double duration;

      private Part(Curve type, double start, double end, double duration) {
         this.type = type;
         this.start = start;
         this.end = end;
         this.duration = duration;
      }

      private double compute(double x) {
         return (this.end - this.start) * (Double)this.type.animationFunction.apply(x / this.duration) + this.start;
      }
   }

   public static enum Curve {
      INV_CUBIC((x) -> polynomial((double)0.33333334F, x)),
      INV_QUADRATIC((x) -> polynomial((double)0.5F, x)),
      LINEAR((x) -> polynomial((double)1.0F, x)),
      QUADRATIC((x) -> polynomial((double)2.0F, x)),
      CUBIC((x) -> polynomial((double)3.0F, x)),
      EASE_OUT(Curve::easeOut),
      EASE_OUT_BACK(Curve::easeOutBack);

      private final UnaryOperator<Double> animationFunction;

      private Curve(UnaryOperator<Double> animationFunction) {
         this.animationFunction = animationFunction;
      }

      public static double polynomial(double exp, double x) {
         return (double)1.0F - Math.pow((double)1.0F - x, exp);
      }

      private static double easeOut(double x) {
         int strength = 3;
         return (double)1.0F - Math.pow((double)1.0F - x, (double)strength);
      }

      private static double easeOutBack(double x) {
         double c1 = 1.70158;
         double c3 = c1 + (double)1.0F;
         return (double)1.0F + c3 * Math.pow(x - (double)1.0F, (double)3.0F) + c1 * Math.pow(x - (double)1.0F, (double)2.0F);
      }
   }
}
