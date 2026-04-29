package net.portalmod.common.sorted.portal;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class DiscontinuousLerpPos {
   private final Vector3d from;
   private Vector3d to;
   private int ticks = 1;
   private int tick = 1;

   public DiscontinuousLerpPos(Vector3d from, Vector3d to, int ticks) {
      this.from = from;
      this.to = to;
      this.ticks = ticks;
   }

   public DiscontinuousLerpPos(Vector3d from) {
      this.from = from;
   }

   public void extendLerp(Vector3d to) {
      if (!this.isIncomplete()) {
         ++this.ticks;
      }

      this.to = to;
   }

   public boolean isExtended() {
      return this.ticks > 1;
   }

   public boolean isDone() {
      return this.tick == this.ticks;
   }

   public void consume() {
      ++this.tick;
   }

   public void apply(Entity entity) {
      double fragmentX = (this.to.field_72450_a - this.from.field_72450_a) / (double)this.ticks;
      double fragmentY = (this.to.field_72448_b - this.from.field_72448_b) / (double)this.ticks;
      double fragmentZ = (this.to.field_72449_c - this.from.field_72450_a) / (double)this.ticks;
      double x = this.from.field_72450_a + fragmentX * (double)this.tick;
      double y = this.from.field_72448_b + fragmentY * (double)this.tick;
      double z = this.from.field_72449_c + fragmentZ * (double)this.tick;
      double xo = this.from.field_72450_a + fragmentX * (double)(this.tick - 1);
      double yo = this.from.field_72448_b + fragmentY * (double)(this.tick - 1);
      double zo = this.from.field_72449_c + fragmentZ * (double)(this.tick - 1);
      entity.func_70107_b(x, y, z);
      entity.field_70169_q = xo;
      entity.field_70167_r = yo;
      entity.field_70166_s = zo;
      entity.field_70142_S = xo;
      entity.field_70137_T = yo;
      entity.field_70136_U = zo;
   }

   public Vector3d getFrom() {
      return this.from;
   }

   public Vector3d getTo() {
      return this.to;
   }

   public boolean isIncomplete() {
      return this.to == null;
   }

   public int getTicks() {
      return this.ticks;
   }
}
