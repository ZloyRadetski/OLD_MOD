package net.portalmod.common.sorted.gel;

import java.util.UUID;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.injectors.LivingEntityInjector;

public class PropulsionGelBlock extends AbstractGelBlock {
   private static final UUID SPEED_MODIFIER_GEL = UUID.fromString("46ea8b5a-6e03-44d0-b9b3-ed94341b0c51");
   private static final UUID SPEED_MODIFIER_GEL_BOUNCE = UUID.fromString("69ea0d28-0fc5-4cfd-8640-1525a61295cc");

   public PropulsionGelBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public static boolean actuallyOnSpeedGel(BlockPos pos, BlockState state, Entity entity) {
      VoxelShape shapeEntity = state.func_215700_a(entity.field_70170_p, pos, ISelectionContext.func_216374_a(entity));
      VoxelShape alignedShapeEntity = shapeEntity.func_197751_a((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
      return VoxelShapes.func_197879_c(alignedShapeEntity, VoxelShapes.func_197881_a(entity.func_174813_aQ().func_186662_g((double)0.001F)), IBooleanFunction.field_223238_i_);
   }

   public static void applyGelSpeedBoost(LivingEntity entity, int propulsionTicks) {
      ModifiableAttributeInstance speedAttribute = entity.func_110148_a(Attributes.field_233821_d_);
      if (speedAttribute != null) {
         double speedBoost = (double)0.12F * ((double)1.0F + Math.cos(Math.PI + Math.PI * (double)propulsionTicks / (double)30.0F)) / (double)2.0F;
         AttributeModifier propulsionGelBoost = new AttributeModifier(SPEED_MODIFIER_GEL, "Propulsion Gel Boost", speedBoost, Operation.ADDITION);
         if (speedAttribute.func_111127_a(SPEED_MODIFIER_GEL) == null) {
            speedAttribute.func_233767_b_(propulsionGelBoost);
         }

      }
   }

   public static void applyBounceSpeedBoost(LivingEntity entity) {
      ModifiableAttributeInstance speedAttribute = entity.func_110148_a(Attributes.field_233821_d_);
      if (speedAttribute != null) {
         AttributeModifier gelSpeedBounceBoost = new AttributeModifier(SPEED_MODIFIER_GEL_BOUNCE, "Propulsion Gel Bounce Boost", (double)0.7F, Operation.ADDITION);
         if (speedAttribute.func_111127_a(SPEED_MODIFIER_GEL_BOUNCE) == null) {
            speedAttribute.func_233767_b_(gelSpeedBounceBoost);
         }

      }
   }

   public static void removeGelSpeedBoost(LivingEntity entity) {
      ModifiableAttributeInstance speedAttribute = entity.func_110148_a(Attributes.field_233821_d_);
      if (speedAttribute != null) {
         speedAttribute.func_188479_b(SPEED_MODIFIER_GEL);
      }
   }

   public static void removeBounceSpeedBoost(LivingEntity entity) {
      ModifiableAttributeInstance speedAttribute = entity.func_110148_a(Attributes.field_233821_d_);
      if (speedAttribute != null) {
         speedAttribute.func_188479_b(SPEED_MODIFIER_GEL_BOUNCE);
      }
   }

   public static void onPreTick(LivingEntity entity) {
      BlockPos pos = entity.func_233580_cy_();
      BlockState state = entity.field_70170_p.func_180495_p(pos);
      boolean isInSpeedGel = state.func_177230_c() instanceof PropulsionGelBlock;
      IGelAffected gelAffected = (IGelAffected)entity;
      if (entity.field_70170_p.field_72995_K == (entity instanceof PlayerEntity)) {
         if (isInSpeedGel) {
            if (actuallyOnSpeedGel(pos, state, entity)) {
               double speed = entity.func_213322_ci().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72433_c();
               if (speed > 0.01) {
                  gelAffected.incrementPropulsionTicks();
               } else {
                  gelAffected.decrementPropulsionTicks();
               }

               Vector3d velocity = entity.func_213322_ci().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72432_b();
               Vector3d oldVelocity = ((IGelAffected)entity).getLastLastDeltaMovement().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72432_b();
               gelAffected.setPropulsionTicks((int)Math.round((double)gelAffected.getPropulsionTicks() * (velocity.func_72430_b(oldVelocity) / (double)2.0F + (double)0.5F)));
            }
         } else {
            if (entity.func_233570_aj_() && gelAffected.getLeftGround() && !(state.func_177230_c() instanceof RepulsionGelBlock)) {
               gelAffected.setPropulsionTicks(0);
            }

            if (LivingEntityInjector.effectsShouldBeReset(entity, true)) {
               gelAffected.decrementPropulsionTicks();
            }
         }

         if (entity.field_70170_p.field_72995_K) {
            PacketInit.INSTANCE.sendToServer(new CPropulsionGelBoostTickPacket(gelAffected.getPropulsionTicks()));
         }
      }

      removeGelSpeedBoost(entity);
      applyGelSpeedBoost(entity, gelAffected.getPropulsionTicks());
      if (gelAffected.getPropulsionTicks() > 0) {
         if (gelAffected.getBounced()) {
            applyBounceSpeedBoost(entity);
         } else {
            removeBounceSpeedBoost(entity);
         }
      } else {
         removeGelSpeedBoost(entity);
      }

      gelAffected.setLeftGround(!entity.func_233570_aj_());
   }
}
