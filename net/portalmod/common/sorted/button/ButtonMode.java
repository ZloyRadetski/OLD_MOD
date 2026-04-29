package net.portalmod.common.sorted.button;

import net.minecraft.util.IStringSerializable;

public enum ButtonMode implements IStringSerializable {
   NORMAL,
   PERSISTENT,
   TOGGLE;

   public String func_176610_l() {
      return this.name().toLowerCase();
   }

   public ButtonMode cycle() {
      return values()[(this.ordinal() + 1) % 3];
   }
}
