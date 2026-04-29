package net.portalmod.core.init;

import net.portalmod.client.animation.Animation;

public class AnimationInit {
   public static final Animation COMPRESSION;
   public static final Animation COMPRESSION_START;
   public static final Animation COMPRESSION_STOP;
   private static final double CLAW_REST = -1.5708;
   private static final double CLAW_RECOIL = -2.2689;
   public static final Animation CLAWS;
   public static final Animation CLAWS_OPEN;
   public static final Animation CLAWS_CLOSE;
   public static final Animation LIFT;
   public static final Animation RECOIL_X;
   public static final Animation RECOIL_Y;
   public static final Animation FIZZLE_BODY;
   public static final Animation FAITHPLATE_BONE;
   public static final Animation FAITHPLATE_ARM;
   public static final Animation FAITHPLATE_PLATE;
   public static final Animation FAITHPLATE_LOCK;

   private AnimationInit() {
   }

   static {
      COMPRESSION = (new Animation.Builder()).keyframe(Animation.Curve.CUBIC, (double)3.0F, (double)100.0F).keyframe(Animation.Curve.QUADRATIC, (double)0.0F, (double)400.0F).build();
      COMPRESSION_START = (new Animation.Builder()).keyframe(Animation.Curve.CUBIC, (double)1.0F, (double)100.0F).build();
      COMPRESSION_STOP = (new Animation.Builder((double)1.0F)).keyframe(Animation.Curve.CUBIC, (double)0.0F, (double)300.0F).build();
      CLAWS = (new Animation.Builder(-1.5708)).keyframe(Animation.Curve.CUBIC, -2.2689, (double)100.0F).keyframe(Animation.Curve.QUADRATIC, -1.5708, (double)400.0F).build();
      CLAWS_OPEN = (new Animation.Builder(-1.5708)).keyframe(Animation.Curve.EASE_OUT_BACK, -2.2689, (double)200.0F).build();
      CLAWS_CLOSE = (new Animation.Builder(-2.2689)).keyframe(Animation.Curve.QUADRATIC, -1.5708, (double)400.0F).build();
      LIFT = (new Animation.Builder((double)0.0F)).keyframe(Animation.Curve.CUBIC, (double)5.0F, (double)100.0F).keyframe(Animation.Curve.LINEAR, (double)5.0F, 1.0E12).keyframe(Animation.Curve.QUADRATIC, (double)0.0F, (double)200.0F).build();
      RECOIL_X = (new Animation.Builder()).keyframe(Animation.Curve.CUBIC, (double)30.0F, (double)100.0F).keyframe(Animation.Curve.QUADRATIC, (double)0.0F, (double)400.0F).build();
      RECOIL_Y = (new Animation.Builder()).keyframe(Animation.Curve.QUADRATIC, (double)15.0F, (double)200.0F).keyframe(Animation.Curve.QUADRATIC, (double)0.0F, (double)300.0F).build();
      FIZZLE_BODY = (new Animation.Builder()).keyframe(Animation.Curve.LINEAR, (double)10.0F, (double)100.0F).keyframe(Animation.Curve.LINEAR, (double)-10.0F, (double)100.0F).keyframe(Animation.Curve.LINEAR, (double)5.0F, (double)100.0F).keyframe(Animation.Curve.LINEAR, (double)-5.0F, (double)100.0F).keyframe(Animation.Curve.LINEAR, (double)0.0F, (double)100.0F).build();
      FAITHPLATE_BONE = (new Animation.Builder((double)-0.2182F)).keyframe(Animation.Curve.QUADRATIC, (double)0.1F, (double)150.0F).keyframe(Animation.Curve.QUADRATIC, (double)-0.2182F, (double)600.0F).build();
      FAITHPLATE_ARM = (new Animation.Builder((double)0.3491F)).keyframe(Animation.Curve.QUADRATIC, (double)-0.3F, (double)150.0F).keyframe(Animation.Curve.QUADRATIC, (double)0.3491F, (double)600.0F).build();
      FAITHPLATE_PLATE = (new Animation.Builder((double)0.2618F)).keyframe(Animation.Curve.QUADRATIC, (double)-0.7F, (double)150.0F).keyframe(Animation.Curve.QUADRATIC, (double)0.2618F, (double)600.0F).build();
      FAITHPLATE_LOCK = (new Animation.Builder((double)0.0F)).keyframe(Animation.Curve.QUADRATIC, (double)-1.2F, (double)150.0F).keyframe(Animation.Curve.QUADRATIC, (double)-0.1309F, (double)600.0F).build();
   }
}
