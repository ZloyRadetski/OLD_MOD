package net.portalmod.core.init;

import java.util.function.Supplier;
import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class SoundTypeInit {
   public static final Entry GEL = new Entry(1.0F, 1.0F, () -> (SoundEvent)SoundInit.GEL_BREAK.get(), () -> (SoundEvent)SoundInit.GEL_STEP.get(), () -> (SoundEvent)SoundInit.GEL_PLACE.get(), () -> SoundEvents.field_187878_fo, () -> SoundEvents.field_187876_fn);

   private SoundTypeInit() {
   }

   public static class Entry extends SoundType {
      private final Supplier<SoundEvent> breakSound;
      private final Supplier<SoundEvent> stepSound;
      private final Supplier<SoundEvent> placeSound;
      private final Supplier<SoundEvent> hitSound;
      private final Supplier<SoundEvent> fallSound;

      public Entry(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
         super(volume, pitch, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null);
         this.breakSound = breakSound;
         this.stepSound = stepSound;
         this.placeSound = placeSound;
         this.hitSound = hitSound;
         this.fallSound = fallSound;
      }

      public SoundEvent func_185845_c() {
         return (SoundEvent)this.breakSound.get();
      }

      public SoundEvent func_185844_d() {
         return (SoundEvent)this.stepSound.get();
      }

      public SoundEvent func_185841_e() {
         return (SoundEvent)this.placeSound.get();
      }

      public SoundEvent func_185846_f() {
         return (SoundEvent)this.hitSound.get();
      }

      public SoundEvent func_185842_g() {
         return (SoundEvent)this.fallSound.get();
      }
   }
}
