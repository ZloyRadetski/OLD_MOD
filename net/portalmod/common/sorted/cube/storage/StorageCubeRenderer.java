package net.portalmod.common.sorted.cube.storage;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.cube.CubeRenderer;

public class StorageCubeRenderer extends CubeRenderer {
   protected static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/entity/cube/storage_cube.png");

   public StorageCubeRenderer(EntityRendererManager erm) {
      super(erm);
      this.func_177094_a(new StorageCubeGlowLayer(this));
   }

   public ResourceLocation getTextureLocation(Cube cube) {
      return TEXTURE;
   }
}
