package net.portalmod.mixins.level;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   targets = {"net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray"}
)
public class ChunkArrayMixin {
}
