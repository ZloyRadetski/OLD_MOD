package net.portalmod.common.sorted.portal;

import java.util.Optional;
import net.minecraft.util.math.MathHelper;
import net.portalmod.core.math.Vec3;

public class CameraAnimator {
   private static CameraAnimator instance;
   private float pitchStart = 0.0F;
   private float pitchEnd = 0.0F;
   private long pitchStartTime = -1L;
   private int pitchAnimationDuration;
   private float yawStart = 0.0F;
   private float yawEnd = 0.0F;
   private long yawStartTime = -1L;
   private int yawAnimationDuration;
   private float rollStart = 0.0F;
   private float rollEnd = 0.0F;
   private long rollStartTime = -1L;
   private int rollAnimationDuration;
   private Vec3 posStart = Vec3.origin();
   private Vec3 posEnd = Vec3.origin();
   private long posStartTime = -1L;
   private int posAnimationDuration;

   private CameraAnimator() {
   }

   public static CameraAnimator getInstance() {
      if (instance == null) {
         instance = new CameraAnimator();
      }

      return instance;
   }

   public void startPitchAnimation(float pitchStart, float pitchEnd, int animationDuration, boolean shallowestAngle) {
      if (shallowestAngle) {
         pitchStart = normalizeAngle(pitchStart);
         pitchEnd = normalizeAngle(pitchEnd);
         float diff = pitchStart - pitchEnd;
         if (diff > 360.0F - diff) {
            pitchStart -= 360.0F;
         }
      }

      this.pitchStart = pitchStart;
      this.pitchEnd = pitchEnd;
      this.pitchStartTime = System.currentTimeMillis();
      this.pitchAnimationDuration = animationDuration;
   }

   public void startYawAnimation(float yawStart, float yawEnd, int animationDuration, boolean shallowestAngle) {
      if (shallowestAngle) {
         yawStart = normalizeAngle(yawStart);
         yawEnd = normalizeAngle(yawEnd);
         float diff = yawStart - yawEnd;
         if (diff > 360.0F - diff) {
            yawStart -= 360.0F;
         }
      }

      this.yawStart = yawStart;
      this.yawEnd = yawEnd;
      this.yawStartTime = System.currentTimeMillis();
      this.yawAnimationDuration = animationDuration;
   }

   public void startRollAnimation(float rollStart, float rollEnd, int animationDuration, boolean shallowestAngle) {
      if (shallowestAngle) {
         rollStart = normalizeAngle(rollStart);
         rollEnd = normalizeAngle(rollEnd);
         float diff = rollStart - rollEnd;
         if (diff > 360.0F - diff) {
            rollStart -= 360.0F;
         }
      }

      this.rollStart = rollStart;
      this.rollEnd = rollEnd;
      this.rollStartTime = System.currentTimeMillis();
      this.rollAnimationDuration = animationDuration;
   }

   public void startPosAnimation(Vec3 posStart, Vec3 posEnd, int animationDuration) {
      this.posStart = posStart;
      this.posEnd = posEnd;
      this.posStartTime = System.currentTimeMillis();
      this.posAnimationDuration = animationDuration;
   }

   public static float normalizeAngle(float angle) {
      while(angle >= 360.0F) {
         angle -= 360.0F;
      }

      while(angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   private Optional<Float> getFactor(long startTime, int duration) {
      long delta = System.currentTimeMillis() - startTime;
      if (startTime > -1L && delta <= (long)duration * 2L) {
         float factor = (float)delta / (float)duration;
         float easedFactor = 1.0F - (float)Math.exp((double)(-factor * 3.0F));
         return Optional.of(easedFactor);
      } else {
         return Optional.empty();
      }
   }

   public Optional<Float> getRelativePitch() {
      return this.getFactor(this.pitchStartTime, this.pitchAnimationDuration).map((x) -> MathHelper.func_219799_g(x, this.pitchStart - this.pitchEnd, 0.0F));
   }

   public Optional<Float> getRelativeYaw() {
      return this.getFactor(this.yawStartTime, this.yawAnimationDuration).map((x) -> MathHelper.func_219799_g(x, this.yawStart - this.yawEnd, 0.0F));
   }

   public Optional<Float> getRelativeRoll() {
      return this.getFactor(this.rollStartTime, this.rollAnimationDuration).map((x) -> MathHelper.func_219799_g(x, this.rollStart - this.rollEnd, 0.0F));
   }

   public Optional<Vec3> getRelativePos() {
      return this.getFactor(this.posStartTime, this.posAnimationDuration).map((x) -> this.posStart.clone().sub(this.posEnd).lerp(Vec3.origin(), (double)x));
   }
}
