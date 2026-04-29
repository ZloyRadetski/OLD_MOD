package net.portalmod.common.sorted.gel;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.common.triggers.CodeBoundTrigger;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class RepulsionGelBlock extends AbstractGelBlock {
   public static float minBounceHeight = 4.0F;

   public void func_180658_a(World world, BlockPos blockPos, Entity entity, float fallDistance) {
      if (!entity.func_225608_bj_()) {
         super.func_180658_a(world, blockPos, entity, fallDistance);
      }

   }

   public RepulsionGelBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public static void verticalBounce(Entity entity, float velocity) {
      IGelAffected gelAffected = (IGelAffected)entity;
      if (!gelAffected.getBounced()) {
         gelAffected.setBounced(true);
         playBounceSound(entity);
         entity.func_213293_j(entity.func_213322_ci().field_72450_a, (double)velocity, entity.func_213322_ci().field_72449_c);
         entity.field_70143_R = 0.0F;
         gelAffected.setLastNeurtalHeight(0.0F);
      }
   }

   public static void playBounceSound(Entity entity) {
      entity.func_184185_a((SoundEvent)SoundInit.REPULSION_GEL_BOUNCE.get(), 1.0F, ModUtil.randomSoundPitch());
   }

   public static double checkSpeedInDirection(Entity entity, Direction direction) {
      Vector3d deltaMovement = ((IGelAffected)entity).getLastDeltaMovement();
      switch (direction) {
         case NORTH:
            return -deltaMovement.field_72449_c;
         case EAST:
            return deltaMovement.field_72450_a;
         case SOUTH:
            return deltaMovement.field_72449_c;
         case WEST:
            return -deltaMovement.field_72450_a;
         default:
            return (double)0.0F;
      }
   }

   public static void horizontalBounce(BlockPos pos, Entity entity, Direction direction) {
      Vector3d deltaMovement = ((IGelAffected)entity).getLastDeltaMovement();
      if (!entity.func_175149_v() && !((IGelAffected)entity).getHorizontalBounced()) {
         float horizontalBounceAmount = 0.7F;
         float verticalBounceAmount = 0.25F;
         float speedBounceBonusAmount = 1.4F;
         float maxSpeedBoostAmount = 2.0F;
         Vector3d bounceDir = new Vector3d((double)direction.func_176734_d().func_82601_c(), (double)0.0F, (double)direction.func_176734_d().func_82599_e());
         Vector3d parallel = new Vector3d(Math.abs(bounceDir.field_72449_c), (double)0.0F, Math.abs(bounceDir.field_72450_a));
         Vector3d bounceSurface = Vector3d.func_237489_a_(pos).func_178787_e(bounceDir.func_186678_a((double)-0.5F));
         Vector3d distance = entity.func_213303_ch().func_178788_d(bounceSurface);
         if (direction.func_176740_k() == Axis.X) {
            if (Math.abs(distance.field_72450_a) > (double)(entity.func_213311_cf() / 1.5F)) {
               return;
            }
         } else if (Math.abs(distance.field_72449_c) > (double)(entity.func_213311_cf() / 1.5F)) {
            return;
         }

         float speed = (float)checkSpeedInDirection(entity, direction);
         if ((double)speed > 0.1) {
            speedBounceBonusAmount = deltaMovement.func_216369_h(parallel).func_72433_c() < (double)maxSpeedBoostAmount ? speedBounceBonusAmount : 1.0F;
            entity.func_213317_d(bounceDir.func_186678_a((double)horizontalBounceAmount).func_72441_c((double)0.0F, Math.max((double)verticalBounceAmount, entity.func_213322_ci().field_72448_b), (double)0.0F).func_178787_e(deltaMovement.func_216369_h(parallel).func_186678_a((double)speedBounceBonusAmount)));
            entity.func_184185_a((SoundEvent)SoundInit.REPULSION_GEL_BOUNCE.get(), 1.0F, ModUtil.randomSoundPitch());
            ((IGelAffected)entity).setHorizontalBounced(true);
         }

      }
   }

   public static void calculateBounce(World level, BlockState state, BlockPos pos, Entity entity) {
      IGelAffected gelAffected = (IGelAffected)entity;
      float playerFallHeight = (float)Math.floor((double)(gelAffected.getLastNeutralHeight() - (float)pos.func_177956_o()));
      if (playerFallHeight >= 193.0F) {
         playerFallHeight = (float)(Math.pow((double)(playerFallHeight - 193.0F), (double)0.6666667F) + (double)192.0F);
      }

      boolean bounceVertical = (Boolean)state.func_177229_b(DOWN);
      if (!entity.func_225608_bj_()) {
         if ((Boolean)state.func_177229_b(NORTH)) {
            horizontalBounce(pos, entity, Direction.NORTH);
         }

         if ((Boolean)state.func_177229_b(EAST)) {
            horizontalBounce(pos, entity, Direction.EAST);
         }

         if ((Boolean)state.func_177229_b(SOUTH)) {
            horizontalBounce(pos, entity, Direction.SOUTH);
         }

         if ((Boolean)state.func_177229_b(WEST)) {
            horizontalBounce(pos, entity, Direction.WEST);
         }
      }

      boolean bounceFromAbove = (double)playerFallHeight > 0.1 && entity.func_233570_aj_();
      boolean bounceFromSpeed = entity.func_213322_ci().func_72433_c() > 0.2 && entity.func_233570_aj_();
      boolean bounceFromJump = gelAffected.getWasOnGround() && entity.func_213322_ci().field_72448_b > 0.2;
      double heightInBlock = entity.func_213303_ch().field_72448_b % (double)1.0F;
      if (!(heightInBlock > (double)0.5F)) {
         if (state.func_177230_c() == BlockInit.REPULSION_GEL.get() && bounceVertical && !entity.func_225608_bj_() && (bounceFromAbove || bounceFromSpeed || bounceFromJump)) {
            entity.field_70143_R = 0.0F;
            if (entity instanceof PlayerEntity && !entity.field_70170_p.field_72995_K && playerFallHeight >= 100.0F) {
               ((CodeBoundTrigger)CriteriaTriggerInit.BOUNCE_ON_GEL.get()).trigger((ServerPlayerEntity)entity);
            }

            float x = Math.max(playerFallHeight, minBounceHeight);
            float velocity = (float)((double)(0.0178F * x) + (double)0.294F * Math.pow((double)x, (double)0.5F) + (double)0.134F * Math.pow((double)x, (double)0.33333334F));
            if (entity instanceof Flingable) {
               boolean launched = ((Flingable)entity).isFlinging();
               if (launched) {
                  velocity = (float)Math.sqrt(0.16 * (double)x);
               }
            }

            verticalBounce(entity, velocity);
            if (level.field_72995_K && (bounceFromSpeed || bounceFromJump)) {
               PacketInit.INSTANCE.sendToServer(new CRepulsionGelBouncePacket(true));
            }
         }

      }
   }

   public static void onPreTick(LivingEntity entity) {
      IGelAffected gelAffected = (IGelAffected)entity;
      if (entity.func_213322_ci().field_72448_b < -0.1) {
         if (entity.field_70170_p.field_72995_K) {
            if (entity instanceof PlayerEntity) {
               gelAffected.setBounced(false);
               PacketInit.INSTANCE.sendToServer(new CRepulsionGelBouncePacket(false));
            }
         } else {
            gelAffected.setBounced(false);
         }
      } else {
         gelAffected.setLastNeurtalHeight((float)entity.func_226278_cu_());
      }

   }

   public static void onPostTick(LivingEntity entity) {
      World level = entity.field_70170_p;
      BlockPos pos = new BlockPos(entity.func_213303_ch());
      BlockState state = level.func_180495_p(pos);
      BlockState stateAbove = level.func_180495_p(pos.func_177984_a());
      if (state.func_177230_c() == BlockInit.REPULSION_GEL.get()) {
         calculateBounce(level, state, pos, entity);
      }

      if (stateAbove.func_177230_c() == BlockInit.REPULSION_GEL.get()) {
         calculateBounce(level, stateAbove, pos.func_177984_a(), entity);
      }

   }
}
