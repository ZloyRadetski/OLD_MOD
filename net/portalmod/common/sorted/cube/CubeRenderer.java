package net.portalmod.common.sorted.cube;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.portalmod.common.entities.TestElementEntityRenderer;

public abstract class CubeRenderer extends TestElementEntityRenderer<Cube, CubeModel<Cube>> {
   public CubeRenderer(EntityRendererManager erm) {
      super(erm, new CubeModel(), 0.5F);
   }

   public void render(Cube cube, float f, float f2, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int i) {
      super.render(cube, f, f2, matrixStack, renderTypeBuffer, i);
   }
}
