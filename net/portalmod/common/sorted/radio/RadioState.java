package net.portalmod.common.sorted.radio;

import net.minecraft.util.IStringSerializable;

public enum RadioState implements IStringSerializable {
   OFF(false),
   ON(true),
   INACTIVE(false),
   ACTIVE(true);

   private boolean isPlaying;

   private RadioState(boolean isPlaying) {
      this.isPlaying = isPlaying;
   }

   public boolean isPlaying() {
      return this.isPlaying;
   }

   public String toString() {
      return this.func_176610_l();
   }

   public String func_176610_l() {
      return this.name().toLowerCase();
   }
}
