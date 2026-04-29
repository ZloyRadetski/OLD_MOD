package net.portalmod.common.sorted.button;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.portalmod.core.math.Mat4;

public class SuperButtonModelTransform implements IModelTransform {
   private final Mat4 matrix;

   public SuperButtonModelTransform(Direction facing, QuadBlockCorner corner) {
      this.matrix = this.getMatrix(facing, corner);
   }

   private Mat4 getMatrix(Direction facing, QuadBlockCorner corner) {
      Mat4 matrix = Mat4.identity();
      Direction.Axis axis = facing.func_176740_k();
      if (facing.func_176743_c() == AxisDirection.NEGATIVE) {
         if (facing.func_176740_k() == Axis.Y) {
            matrix.rotateDeg(Vector3f.field_229183_f_, 180.0F);
         } else {
            matrix.rotateDeg(Vector3f.field_229181_d_, 180.0F);
         }
      }

      int axisFactor = axis == Axis.X ? 0 : -90;
      if (axis == Axis.X) {
         matrix.rotateDeg(Vector3f.field_229182_e_, 90.0F);
      }

      if (axis == Axis.Z) {
         matrix.rotateDeg(Vector3f.field_229179_b_, 90.0F);
      }

      return matrix.rotateDeg(Vector3f.field_229181_d_, (float)(corner.getRot() + axisFactor));
   }

   public TransformationMatrix func_225615_b_() {
      return new TransformationMatrix(this.matrix.to4f());
   }
}
