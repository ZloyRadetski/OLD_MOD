package net.portalmod.mixins.accessors;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ServerWorld.class})
public interface ServerLevelAccessor {
   @Invoker("add")
   void pmForceAddPortal(Entity var1);
}
