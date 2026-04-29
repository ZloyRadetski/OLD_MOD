package net.portalmod.common.sorted.faithplate;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.portalmod.core.math.Vec3;

public class FaithPlateParabola {
   public static final double GRAVITY = 0.08;
   private final Vec3 target;
   private final Vec3 projectedTarget;
   private final double minHeight;
   private double height;

   public FaithPlateParabola(Vec3 target, double height) {
      this.target = target;
      this.projectedTarget = this.getProjectedTarget();
      this.minHeight = this.getMinHeight(this.getProjectedTarget());
      this.setHeight(height);
   }

   public FaithPlateParabola(Vector3d target, double height) {
      this(new Vec3(target), height);
   }

   public FaithPlateParabola(Vec3 target) {
      this(target, Double.NEGATIVE_INFINITY);
   }

   public FaithPlateParabola(Vector3d target) {
      this(target, Double.NEGATIVE_INFINITY);
   }

   public void setHeight(double height) {
      this.height = Math.min((double)100.0F, Math.max(height, this.minHeight));
   }

   public double getHeight() {
      return this.height;
   }

   private double getMinHeight(Vec3 target) {
      if (target.y != (double)0.0F && !this.isVertical()) {
         Vec3 vertex = target.y > (double)0.0F ? target : new Vec3((double)0.0F);
         Vec3 point = target.y < (double)0.0F ? target : new Vec3((double)0.0F);
         double xb = target.x / (double)2.0F;
         double a = (vertex.y - point.y) / ((double)2.0F * vertex.x * point.x - point.x * point.x - vertex.x * vertex.x);
         double b = (double)-2.0F * a * vertex.x;
         return a * xb * xb + b * xb;
      } else {
         return (double)0.5F;
      }
   }

   public Vec3 getProjectedTarget() {
      return new Vec3(Math.sqrt(this.target.x * this.target.x + this.target.z * this.target.z), this.target.y, (double)0.0F);
   }

   public double getA() {
      return (this.projectedTarget.y - this.getB() * this.projectedTarget.x) / (this.projectedTarget.x * this.projectedTarget.x);
   }

   public double getB() {
      return ((double)4.0F * this.height - this.projectedTarget.y) / this.projectedTarget.x;
   }

   public boolean isVertical() {
      return Double.isInfinite(this.getA()) || Double.isNaN(this.getA());
   }

   public double getAngle() {
      return this.isVertical() ? (Math.PI / 2D) : Math.atan(this.getB());
   }

   public double getVelocity() {
      return this.isVertical() ? Math.sqrt(0.16 * this.height) : Math.sqrt(-(0.08 / ((double)2.0F * this.getA()))) / Math.cos(this.getAngle()) * 0.996;
   }

   public double getRotation() {
      return this.isVertical() ? (double)0.0F : Math.atan2(this.target.z, this.target.x);
   }

   public double getComponentX() {
      return Math.cos(this.getRotation());
   }

   public double getComponentZ() {
      return Math.sin(this.getRotation());
   }

   public double getMiddlePoint() {
      return this.projectedTarget.x / (double)2.0F;
   }

   public BlockRayTraceResult findFirstBlockHit(World world, FaithPlateTileEntity be) {
      double step = 0.01;
      Vec3 startOffset = new Vec3((double)0.5F, (double)1.0F, (double)0.5F);
      Vec3 prev = startOffset.clone();
      double targetStep = Double.POSITIVE_INFINITY;

      for(double t = (double)0.0F; t < targetStep; t += step) {
         double x = startOffset.x;
         double z = startOffset.z;
         double y;
         if (this.isVertical()) {
            y = t + startOffset.y;
            targetStep = Math.abs(this.projectedTarget.y) * (double)2.0F;
         } else {
            y = this.getA() * t * t + this.getB() * t + startOffset.y;
            x = this.getComponentX() * t + startOffset.x;
            z = this.getComponentZ() * t + startOffset.z;
            targetStep = Math.abs(this.projectedTarget.x) * (double)2.0F;
         }

         Vec3 current = new Vec3(x, y, z);
         BlockPos pos = new BlockPos(x, y, z);
         BlockState state = world.func_180495_p(pos.func_177971_a(be.func_174877_v()));
         if (!state.func_196952_d(world, pos).func_197766_b() && !state.func_203425_a(be.func_195044_w().func_177230_c())) {
            BlockPos prevBlock = new BlockPos(prev.x, prev.y, prev.z);
            Vector3i diff = pos.func_177973_b(prevBlock);
            Direction face = Direction.func_218383_a(diff.func_177958_n(), diff.func_177956_o(), diff.func_177952_p());
            if (face == null) {
               face = this.getNearestCubeFace(new Vector3d(current.x, current.y, current.z)).func_176734_d();
            }

            return new BlockRayTraceResult(new Vector3d(x, y, z), face, pos, false);
         }

         prev = current;
      }

      return null;
   }

   public Direction getNearestCubeFace(Vector3d p) {
      Vector3d point = p.func_178788_d(new Vector3d(Math.floor(p.field_72450_a), Math.floor(p.field_72448_b), Math.floor(p.field_72449_c)));
      double[] d = new double[]{point.field_72450_a, (double)1.0F - point.field_72450_a, point.field_72448_b, (double)1.0F - point.field_72448_b, point.field_72449_c, (double)1.0F - point.field_72449_c};
      Direction[] faces = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.SOUTH, Direction.NORTH};
      int minIndex = 0;

      for(int i = 1; i < 6; ++i) {
         if (d[i] < d[minIndex]) {
            minIndex = i;
         }
      }

      return faces[minIndex];
   }
}
