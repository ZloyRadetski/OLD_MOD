package net.portalmod.common.sorted.cube.vintage;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.cube.CubeRenderer;

public class VintageCubeRenderer extends CubeRenderer {
   protected static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/entity/cube/vintage_cube.png");

   public VintageCubeRenderer(EntityRendererManager erm) {
      super(erm);
   }

   public ResourceLocation getTextureLocation(Cube cube) {
      return TEXTURE;
   }
}
