package net.portalmod.mixins.accessors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.util.Splashes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface MinecraftAccessor {
   @Accessor("splashManager")
   void pmSetSplashManager(Splashes var1);

   @Accessor("mainRenderTarget")
   void pmSetMainRenderTarget(Framebuffer var1);

   @Accessor("pausePartialTick")
   float pmGetPausePartialTick();
}
