package net.portalmod.common.sorted.button;

import net.minecraft.util.IStringSerializable;

public enum QuadBlockCorner implements IStringSerializable {
   UP_LEFT("up_left", 0, 1, 90, true, true),
   UP_RIGHT("up_right", 1, 1, 0, true, false),
   DOWN_RIGHT("down_right", 1, 0, -90, false, false),
   DOWN_LEFT("down_left", 0, 0, 180, false, true);

   private final String name;
   private final int x;
   private final int y;
   private final int rot;
   private final boolean isUp;
   private final boolean isLeft;

   private QuadBlockCorner(String name, int x, int y, int rot, boolean isUp, boolean isLeft) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.rot = rot;
      this.isUp = isUp;
      this.isLeft = isLeft;
   }

   public String toString() {
      return this.func_176610_l();
   }

   public String func_176610_l() {
      return this.name;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getRot() {
      return this.rot;
   }

   public static QuadBlockCorner getCorner(boolean up, boolean left) {
      if (up) {
         return left ? UP_LEFT : UP_RIGHT;
      } else {
         return left ? DOWN_LEFT : DOWN_RIGHT;
      }
   }

   public boolean isLeft() {
      return this.isLeft;
   }

   public boolean isUp() {
      return this.isUp;
   }

   public QuadBlockCorner rotate(int times) {
      QuadBlockCorner result = this;

      for(int i = 0; i < (times % 4 + 4) % 4; ++i) {
         result = result.rotate();
      }

      return result;
   }

   private QuadBlockCorner rotate() {
      switch (this) {
         case UP_LEFT:
            return UP_RIGHT;
         case UP_RIGHT:
            return DOWN_RIGHT;
         case DOWN_RIGHT:
            return DOWN_LEFT;
         default:
            return UP_LEFT;
      }
   }

   public QuadBlockCorner mirrorLeftRight() {
      switch (this) {
         case UP_LEFT:
            return UP_RIGHT;
         case UP_RIGHT:
            return UP_LEFT;
         case DOWN_RIGHT:
         default:
            return DOWN_LEFT;
         case DOWN_LEFT:
            return DOWN_RIGHT;
      }
   }

   public QuadBlockCorner mirrorUpDown() {
      switch (this) {
         case UP_LEFT:
            return DOWN_LEFT;
         case UP_RIGHT:
            return DOWN_RIGHT;
         case DOWN_RIGHT:
         default:
            return UP_RIGHT;
         case DOWN_LEFT:
            return UP_LEFT;
      }
   }
}
