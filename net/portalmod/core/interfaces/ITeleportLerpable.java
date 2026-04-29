package net.portalmod.core.interfaces;

import java.util.Deque;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;

public interface ITeleportLerpable {
   Deque<Tuple<Vector3d, Vector3d>> getLerpPositions();

   boolean hasUsedPortal();

   void setHasUsedPortal(boolean var1);
}
