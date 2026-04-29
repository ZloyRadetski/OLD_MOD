package net.portalmod.common.sorted.goo;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.core.init.FluidInit;
import net.portalmod.core.init.FluidTagInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;
import net.portalmod.mixins.accessors.EntityAccessor;

public class GooBlock extends FlowingFluidBlock {
   public static final double MOVEMENT_FRICTION = 0.6;
   public static final double SWIM_SPEED = (double)0.25F;
   public static final double FLOW_PUSH_STRENGTH = 0.05;
   public static final float FOG_DENSITY = 0.95F;
   public static final float FOG_DENSITY_WRENCH = 0.2F;
   public static final int DAMAGE_AMOUNT = 4;

   public GooBlock(Supplier<FlowingFluid> flowingFluid, AbstractBlock.Properties properties) {
      super(flowingFluid, properties);
   }

   public static Vec3 getFogColor() {
      return new Vec3((double)0.2734375F, (double)0.20703125F, (double)0.11328125F);
   }

   public static float getFogDensity(Entity entity) {
      return WrenchItem.holdingWrench(entity) ? 0.2F : 0.95F;
   }

   public static void applyGooResistance(Entity entity) {
      entity.func_213317_d(entity.func_213322_ci().func_186678_a(0.6));
   }

   public static void applyVerticalSwimSpeed(LivingEntity entity) {
      if (WrenchItem.holdingWrench(entity)) {
         entity.func_213317_d(entity.func_213322_ci().func_72441_c((double)0.0F, (double)0.25F, (double)0.0F));
      }

   }

   public static boolean isInGoo(Entity entity) {
      return !((EntityAccessor)entity).pmGetFirstTick() && entity.func_233571_b_(FluidTagInit.GOO) > (double)0.0F;
   }

   public static void addGooDamage(Entity entity) {
      entity.field_70143_R = 0.0F;
      if (!WrenchItem.holdingWrench(entity)) {
         entity.func_70097_a(FluidInit.GOO_DAMAGE, 4.0F);
      }
   }

   public static void handleGooDamage(LivingEntity entity, DamageSource damageSource) {
      entity.field_70170_p.func_217384_a((PlayerEntity)null, entity, (SoundEvent)SoundInit.GOO_DAMAGE.get(), entity.func_184176_by(), 1.0F, ModUtil.randomSoundPitch());
   }

   @Nullable
   public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
      return PathNodeType.LAVA;
   }
}
