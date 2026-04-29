package net.portalmod.mixins.accessors;

import java.util.List;
import net.minecraft.client.util.Splashes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Splashes.class})
public interface SplashesAccessor {
   @Accessor("SPLASHES_LOCATION")
   static void pmSetLocation(ResourceLocation value) {
      throw new AssertionError();
   }

   @Accessor("SPLASHES_LOCATION")
   static ResourceLocation pmGetLocation() {
      throw new AssertionError();
   }

   @Invoker("prepare")
   List<String> pmPrepare(IResourceManager var1, IProfiler var2);

   @Invoker("apply")
   void pmApply(List<String> var1, IResourceManager var2, IProfiler var3);
}
