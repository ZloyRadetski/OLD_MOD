package net.portalmod.core.util;

import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class MatrixUtil {
   public static Mat4 absToRel(Vec3 x, Vec3 y) {
      Vec3 z = x.clone().cross(y);
      return new Mat4(x.x, y.x, z.x, (double)0.0F, x.y, y.y, z.y, (double)0.0F, x.z, y.z, z.z, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
   }

   public static Mat4 relToAbs(Vec3 x, Vec3 y) {
      Vec3 z = x.clone().cross(y);
      return new Mat4(x.x, x.y, x.z, (double)0.0F, y.x, y.y, y.z, (double)0.0F, z.x, z.y, z.z, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
   }
}
