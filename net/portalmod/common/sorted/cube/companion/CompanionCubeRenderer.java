package net.portalmod.common.sorted.cube.companion;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.cube.CubeRenderer;

public class CompanionCubeRenderer extends CubeRenderer {
   protected static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/entity/cube/companion_cube.png");

   public CompanionCubeRenderer(EntityRendererManager erm) {
      super(erm);
      this.func_177094_a(new CompanionCubeGlowLayer(this));
   }

   public ResourceLocation getTextureLocation(Cube cube) {
      return TEXTURE;
   }
}
