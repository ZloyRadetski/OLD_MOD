package net.portalmod.common.sorted.gel;

import net.minecraft.util.math.vector.Vector3d;

public interface IGelAffected {
   int MAX_PROPULSION_TICKS = 30;

   void setLastNeurtalHeight(float var1);

   float getLastNeutralHeight();

   void setBounced(boolean var1);

   boolean getBounced();

   void setHorizontalBounced(boolean var1);

   boolean getHorizontalBounced();

   void setWasOnGround(boolean var1);

   boolean getWasOnGround();

   void setLeftGround(boolean var1);

   boolean getLeftGround();

   int getPropulsionTicks();

   void setPropulsionTicks(int var1);

   void incrementPropulsionTicks();

   void decrementPropulsionTicks();

   void setLastDeltaMovement(Vector3d var1);

   Vector3d getLastDeltaMovement();

   void setLastLastDeltaMovement(Vector3d var1);

   Vector3d getLastLastDeltaMovement();
}
