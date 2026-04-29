package net.portalmod.mixins.entity;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.portalmod.common.entities.Fizzleable;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.common.sorted.portal.IClientTeleportable;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalHandler;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.common.triggers.CodeBoundTrigger;
import net.portalmod.core.init.AttributeInit;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.init.FluidInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.interfaces.IGetPose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntityMixin extends LivingEntity implements IClientTeleportable, IGetPose, PortalHandler, Fizzleable {
   @Shadow
   @Final
   public PlayerAbilities field_71075_bZ;
   @Shadow
   @Nullable
   private Pose forcedPose;
   @Unique
   private boolean clientJustPortaled = false;
   @Unique
   private boolean pmWasFlying = false;

   public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World level) {
      super(entityType, level);
   }

   public void setJustPortaled(boolean justPortaled) {
      this.clientJustPortaled = justPortaled;
   }

   public boolean getJustPortaled() {
      return this.clientJustPortaled;
   }

   public void removeJustPortaled() {
      this.clientJustPortaled = false;
   }

   public void onTeleport(PortalEntity from, PortalEntity to) {
      for(Entity passenger : this.func_184188_bt()) {
         if (passenger instanceof TestElementEntity) {
            ((TestElementEntity)passenger).onHolderTeleport(from, to);
         }
      }

   }

   public void onTeleportPacket() {
      for(Entity passenger : this.func_184188_bt()) {
         if (passenger instanceof TestElementEntity) {
            ((TestElementEntity)passenger).onHolderTeleportPacket();
         }
      }

   }

   public boolean shouldCheckForFizzlers() {
      return true;
   }

   public void onTouchingFizzler() {
      PlayerEntity player = (PlayerEntity)this;
      if (!player.func_175149_v()) {
         if (!player.field_70170_p.field_72995_K || player.func_175144_cb()) {
            PortalGun.fizzleGunsInInventory(player);
         }
      }
   }

   @Inject(
      method = {"isSecondaryUseActive"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmUseWrenchSneaking(CallbackInfoReturnable<Boolean> info) {
      PlayerEntity player = (PlayerEntity)this;
      if (player.func_184614_ca().func_77973_b() instanceof WrenchItem) {
         info.setReturnValue(false);
      }

   }

   @Redirect(
      method = {"maybeBackOffFromEdge"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;noCollision(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Z"
)
   )
   private boolean pmMaybeBackOffFromEdgeCube(World level, Entity entity, AxisAlignedBB bb) {
      boolean noCollision = level.func_226665_a__(entity, bb);
      boolean noCubeCollision = level.func_175674_a(entity, bb.func_186662_g(1.0E-7), (cube) -> cube instanceof Cube && cube.func_174813_aQ().func_72326_a(bb) && !entity.func_184223_x(cube)).stream().map(Entity::func_174813_aQ).map(VoxelShapes::func_197881_a).allMatch(VoxelShape::func_197766_b);
      boolean noPortalCollision = !VoxelShapes.func_197879_c(PortalEntity.getCollisionShape(entity), VoxelShapes.func_197881_a(bb), IBooleanFunction.field_223238_i_);
      return noCollision && noCubeCollision && noPortalCollision;
   }

   @Inject(
      method = {"travel"},
      at = {@At("HEAD")}
   )
   private void pmFixStopFlyingVelocity(CallbackInfo info) {
      PlayerEntity thiss = (PlayerEntity)this;
      if (thiss.field_71075_bZ.field_75100_b && !thiss.func_184218_aH()) {
         this.pmWasFlying = true;
      } else {
         if (this.pmWasFlying) {
            double gravity = ((ModifiableAttributeInstance)Objects.requireNonNull(thiss.func_110148_a((Attribute)ForgeMod.ENTITY_GRAVITY.get()))).func_111126_e();
            Vector3d d = thiss.func_213322_ci();
            thiss.func_213293_j(d.field_72450_a, -gravity, d.field_72449_c);
         }

         this.pmWasFlying = false;
      }

   }

   public Pose pmGetNextPose() {
      if (this.forcedPose != null) {
         return this.forcedPose;
      } else if (!this.func_213298_c(Pose.SWIMMING)) {
         return this.func_213283_Z();
      } else {
         Pose pose;
         if (this.func_184613_cA()) {
            pose = Pose.FALL_FLYING;
         } else if (this.func_70608_bn()) {
            pose = Pose.SLEEPING;
         } else if (this.func_203007_ba()) {
            pose = Pose.SWIMMING;
         } else if (this.func_204805_cN()) {
            pose = Pose.SPIN_ATTACK;
         } else if (this.func_225608_bj_() && !this.field_71075_bZ.field_75100_b) {
            pose = Pose.CROUCHING;
         } else {
            pose = Pose.STANDING;
         }

         Pose pose1;
         if (!this.func_175149_v() && !this.func_184218_aH() && !this.func_213298_c(pose)) {
            if (this.func_213298_c(Pose.CROUCHING)) {
               pose1 = Pose.CROUCHING;
            } else {
               pose1 = Pose.SWIMMING;
            }
         } else {
            pose1 = pose;
         }

         return pose1;
      }
   }

   @Inject(
      method = {"startFallFlying"},
      at = {@At("HEAD")}
   )
   private void pmGrantFaithPlateElytraAdvancement(CallbackInfo info) {
      if (!this.field_70170_p.field_72995_K && ((Flingable)this).isFlinging() && ((PlayerEntity)this).func_213322_ci().func_72433_c() > 0.7) {
         ((CodeBoundTrigger)CriteriaTriggerInit.FAITH_PLATE_ELYTRA.get()).trigger((ServerPlayerEntity)this);
      }

   }

   @Inject(
      method = {"getHurtSound"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pmPlayGooHurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> cir) {
      if (damageSource == FluidInit.GOO_DAMAGE) {
         cir.setReturnValue(SoundInit.GOO_DAMAGE.get());
      }

   }

   @Inject(
      method = {"createAttributes"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void pmAddAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
      cir.setReturnValue(((AttributeModifierMap.MutableAttribute)cir.getReturnValue()).func_233814_a_((Attribute)AttributeInit.GRAB_REACH.get()).func_233814_a_((Attribute)AttributeInit.BUTTON_REACH.get()));
   }
}
