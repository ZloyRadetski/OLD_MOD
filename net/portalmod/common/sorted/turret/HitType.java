package net.portalmod.common.sorted.turret;

import net.minecraft.util.IStringSerializable;

public enum HitType implements IStringSerializable {
   CLEAR,
   PERMEABLE,
   TRANSPARENT,
   SOLID,
   CUBE;

   public String func_176610_l() {
      return this.name().toLowerCase();
   }
}
