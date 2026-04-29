package net.portalmod.common.sorted.antline.indicator;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.portalmod.common.sorted.antline.AntlineConnector;
import net.portalmod.core.math.BiHashMap;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;

public class AntlineDevice extends HorizontalFaceBlock implements AntlineConnector {
   private static final BiHashMap<Direction, AttachFace, VoxelShapeGroup> SHAPE = new BiHashMap<Direction, AttachFace, VoxelShapeGroup>();

   public AntlineDevice(AbstractBlock.Properties properties) {
      super(properties);
      this.initAABBs();
   }

   protected void initAABBs() {
      VoxelShapeGroup shape = (new VoxelShapeGroup.Builder()).add((double)0.0F, (double)3.0F, (double)3.0F, (double)2.0F, (double)13.0F, (double)13.0F).build();

      for(Direction facing : Direction.values()) {
         for(AttachFace attachFace : AttachFace.values()) {
            Mat4 matrix = Mat4.identity();
            matrix.translate(new Vec3((double)0.5F));
            if (attachFace != AttachFace.WALL) {
               int angle = attachFace == AttachFace.FLOOR ? 90 : -90;
               matrix.rotateDeg(Vector3f.field_229183_f_, (float)angle);
            } else {
               int angle = facing.func_176736_b() * -90 - 90;
               matrix.rotateDeg(Vector3f.field_229181_d_, (float)angle);
            }

            matrix.translate(new Vec3((double)-0.5F));
            SHAPE.put(facing, attachFace, shape.clone().transform(matrix));
         }
      }

   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      return ((VoxelShapeGroup)SHAPE.get(state.func_177229_b(field_185512_D), state.func_177229_b(field_196366_M))).getShape();
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{field_185512_D, field_196366_M});
   }

   public PushReaction func_149656_h(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public Direction getHorsedOn(BlockState state) {
      return func_196365_i(state).func_176734_d();
   }

   public boolean antlineConnectsInDirection(Direction direction, BlockState state) {
      return direction.func_176740_k() != this.getHorsedOn(state).func_176740_k();
   }
}
