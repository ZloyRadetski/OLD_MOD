package net.portalmod.common.sorted.antline;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class AntlineLoader implements IModelLoader<AntlineGeometry> {
   public void func_195410_a(IResourceManager resourceManager) {
   }

   public AntlineGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      return new AntlineGeometry();
   }
}
