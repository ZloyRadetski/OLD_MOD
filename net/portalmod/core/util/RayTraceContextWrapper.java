package net.portalmod.core.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class RayTraceContextWrapper extends RayTraceContext {
   private final RayTraceContext wrapped;
   private Vector3d from;
   private Vector3d to;

   public RayTraceContextWrapper(RayTraceContext context) {
      super(Vector3d.field_186680_a, Vector3d.field_186680_a, BlockMode.COLLIDER, FluidMode.ANY, (Entity)null);
      this.wrapped = context;
   }

   public void setTo(Vector3d to) {
      this.to = to;
   }

   public void setFrom(Vector3d from) {
      this.from = from;
   }

   public Vector3d func_222250_a() {
      return this.to != null ? this.to : this.wrapped.func_222250_a();
   }

   public Vector3d func_222253_b() {
      return this.from != null ? this.from : this.wrapped.func_222253_b();
   }

   public VoxelShape func_222251_a(BlockState state, IBlockReader level, BlockPos pos) {
      return this.wrapped.func_222251_a(state, level, pos);
   }

   public VoxelShape func_222252_a(FluidState state, IBlockReader level, BlockPos pos) {
      return this.wrapped.func_222252_a(state, level, pos);
   }
}
