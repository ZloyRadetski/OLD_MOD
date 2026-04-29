package net.portalmod.mixins.accessors;

import java.util.Set;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ChunkRenderDispatcher.CompiledChunk.class})
public interface CompiledChunkAccessor {
   @Accessor("transparencyState")
   BufferBuilder.State pmGetTransparencyState();

   @Accessor("hasBlocks")
   Set<RenderType> pmGetHasBlocks();
}
