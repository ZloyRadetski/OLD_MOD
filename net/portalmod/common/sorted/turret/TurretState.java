package net.portalmod.common.sorted.turret;

import net.minecraft.util.IStringSerializable;

public enum TurretState implements IStringSerializable {
   RESTING,
   OPENING,
   SHOOTING,
   LOST_TARGET,
   CLOSING,
   FALLING,
   DEAD;

   public String func_176610_l() {
      return this.name().toLowerCase();
   }

   public boolean isStanding() {
      return this != FALLING && this != DEAD;
   }

   public boolean wingsOpen() {
      return this == OPENING || this == SHOOTING || this == LOST_TARGET || this == FALLING;
   }
}
