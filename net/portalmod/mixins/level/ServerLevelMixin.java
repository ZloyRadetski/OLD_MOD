package net.portalmod.mixins.level;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalManager;
import net.portalmod.core.util.EntityTickWrapper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerWorld.class})
public abstract class ServerLevelMixin {
   @Shadow
   @Final
   private static Logger field_147491_a;

   @Shadow
   protected abstract boolean func_217478_l(Entity var1);

   @Redirect(
      method = {"tickNonPassenger"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;tick()V"
)
   )
   private void pmWrapServerTickNonPassenger(Entity entity) {
      EntityTickWrapper.wrapTick(entity, Entity::func_70071_h_);
   }

   @Redirect(
      method = {"tickPassenger"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;rideTick()V"
)
   )
   private void pmWrapServerTickPassenger(Entity entity) {
      EntityTickWrapper.wrapTick(entity, Entity::func_70098_U);
   }

   @Inject(
      method = {"updateChunkPos"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmDoNotUpdateChunkPosForPortal(Entity entity, CallbackInfo info) {
      if (entity instanceof PortalEntity) {
         info.cancel();
      }

   }

   @Redirect(
      method = {"unload"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/server/ServerWorld;onEntityRemoved(Lnet/minecraft/entity/Entity;)V"
)
   )
   private void pmWrapUnloadEntity(ServerWorld level, Entity entity) {
      PortalManager.getInstance().unloadingChunk = true;
      level.func_217484_g(entity);
      PortalManager.getInstance().unloadingChunk = false;
   }
}
