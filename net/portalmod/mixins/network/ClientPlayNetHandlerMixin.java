package net.portalmod.mixins.network;

import java.util.Deque;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.portalmod.common.sorted.sign.ChamberSignEntity;
import net.portalmod.core.interfaces.ITeleportLerpable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ClientPlayNetHandler.class})
public class ClientPlayNetHandlerMixin {
   @Shadow
   private ClientWorld field_147300_g;

   @Inject(
      method = {"handleMoveEntity"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;lerpTo(DDDFFIZ)V"
)}
   )
   private void pmMoveEntityLerpTo(SEntityPacket entityPacket, CallbackInfo info) {
      this.pmEndTeleportLerpBeforeLerpTo(entityPacket.func_149065_a(this.field_147300_g));
   }

   @Inject(
      method = {"handleMoveEntity"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;isControlledByLocalInstance()Z"
)},
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   private void pmFixChamberSignTeleporting(SEntityPacket p_147259_1_, CallbackInfo ci, Entity entity) {
      if (entity instanceof ChamberSignEntity) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"handleTeleportEntity"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;lerpTo(DDDFFIZ)V"
)}
   )
   private void pmTeleportEntityLerpTo(SEntityTeleportPacket entityPacket, CallbackInfo ci) {
      this.pmEndTeleportLerpBeforeLerpTo(this.field_147300_g.func_73045_a(entityPacket.func_149451_c()));
   }

   private void pmEndTeleportLerpBeforeLerpTo(Entity entity) {
      Deque<Tuple<Vector3d, Vector3d>> lerpPositions = ((ITeleportLerpable)entity).getLerpPositions();
      if (!lerpPositions.isEmpty()) {
         Vector3d pos = (Vector3d)((Tuple)lerpPositions.peekLast()).func_76340_b();
         entity.func_226286_f_(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c);
         lerpPositions.clear();
      }
   }
}
