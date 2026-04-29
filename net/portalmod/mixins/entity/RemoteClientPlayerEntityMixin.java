package net.portalmod.mixins.entity;

import java.util.Deque;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.core.interfaces.ITeleportLerpable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RemoteClientPlayerEntity.class})
public abstract class RemoteClientPlayerEntityMixin extends LivingEntity {
   protected RemoteClientPlayerEntityMixin(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
      super(p_i48577_1_, p_i48577_2_);
   }

   @Inject(
      method = {"aiStep"},
      at = {@At("HEAD")}
   )
   private void pmLerpPosWithPortal(CallbackInfo info) {
      Deque<Tuple<Vector3d, Vector3d>> lerpPositions = ((ITeleportLerpable)this).getLerpPositions();
      if (!lerpPositions.isEmpty() && this.field_70170_p.field_72995_K) {
         Tuple<Vector3d, Vector3d> currentLerpPos = (Tuple)lerpPositions.pop();
         this.func_70107_b(((Vector3d)currentLerpPos.func_76340_b()).field_72450_a, ((Vector3d)currentLerpPos.func_76340_b()).field_72448_b, ((Vector3d)currentLerpPos.func_76340_b()).field_72449_c);
         this.field_70169_q = ((Vector3d)currentLerpPos.func_76341_a()).field_72450_a;
         this.field_70167_r = ((Vector3d)currentLerpPos.func_76341_a()).field_72448_b;
         this.field_70166_s = ((Vector3d)currentLerpPos.func_76341_a()).field_72449_c;
         this.field_70142_S = ((Vector3d)currentLerpPos.func_76341_a()).field_72450_a;
         this.field_70137_T = ((Vector3d)currentLerpPos.func_76341_a()).field_72448_b;
         this.field_70136_U = ((Vector3d)currentLerpPos.func_76341_a()).field_72449_c;
         this.field_70716_bi = 0;
      }
   }
}
