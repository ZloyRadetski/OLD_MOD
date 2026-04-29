package net.portalmod.common.sorted.portal;

import net.minecraft.util.Direction;
import net.portalmod.core.math.Vec3;

public class VolatilePortalHelper implements AbstractPortalHelper {
   private static final float TOLERANCE = 0.01F;
   public final Vec3 position;
   public final Direction normal;
   public final float radius;

   public VolatilePortalHelper(Vec3 position, Direction normal, float radius) {
      this.position = position;
      this.normal = normal;
      this.radius = radius;
   }

   public boolean willHelpPortal(Vec3 hitPos, Direction face) {
      if (face != this.normal) {
         return false;
      } else if (hitPos.clone().sub(this.position).magnitudeSqr() > (double)(this.radius * this.radius * 4.0F)) {
         return false;
      } else {
         double zDistance = Math.abs(hitPos.choose(face.func_176740_k()) - this.position.choose(face.func_176740_k()));
         if (zDistance > (double)0.01F) {
            return false;
         } else {
            double dx = Math.abs(hitPos.x - this.position.x);
            double dy = Math.abs(hitPos.y - this.position.y);
            double dz = Math.abs(hitPos.z - this.position.z);
            return Math.max(dy, Math.max(dx, dz)) <= (double)this.radius;
         }
      }
   }

   public Vec3 helpPortal(Vec3 hitPos, Direction face) {
      if (!this.willHelpPortal(hitPos, face)) {
         return hitPos;
      } else {
         Vec3 newPos = this.position.clone();
         newPos.set(face.func_176740_k(), hitPos.choose(face.func_176740_k()));
         return newPos;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof VolatilePortalHelper)) {
         return false;
      } else {
         VolatilePortalHelper other = (VolatilePortalHelper)obj;
         return other.position.equals(this.position) && other.normal.equals(this.normal) && other.radius == this.radius;
      }
   }
}
