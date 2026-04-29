package net.portalmod.common.sorted.portal;

import net.minecraft.util.IStringSerializable;

public enum PortalEnd implements IStringSerializable {
   NONE("none"),
   PRIMARY("primary"),
   SECONDARY("secondary");

   private final String name;

   private PortalEnd(String name) {
      this.name = name;
   }

   public String toString() {
      return this.func_176610_l();
   }

   public String func_176610_l() {
      return this.name;
   }

   public PortalEnd other() {
      if (this == PRIMARY) {
         return SECONDARY;
      } else {
         return this == SECONDARY ? PRIMARY : NONE;
      }
   }

   public static enum Safe {
      PRIMARY(PortalEnd.PRIMARY),
      SECONDARY(PortalEnd.SECONDARY);

      private final PortalEnd original;

      private Safe(PortalEnd original) {
         this.original = original;
      }

      public PortalEnd getOriginal() {
         return this.original;
      }
   }
}
