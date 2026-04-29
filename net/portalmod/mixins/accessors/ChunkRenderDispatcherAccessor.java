package net.portalmod.mixins.accessors;

import java.util.Queue;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ChunkRenderDispatcher.class})
public interface ChunkRenderDispatcherAccessor {
   @Accessor("freeBuffers")
   Queue<RegionRenderCacheBuilder> pmGetFreeBuffers();
}
