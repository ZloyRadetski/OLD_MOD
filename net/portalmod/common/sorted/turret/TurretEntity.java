package net.portalmod.common.sorted.turret;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.particles.TurretSparkParticle;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.triggers.CodeBoundTrigger;
import net.portalmod.core.init.BlockTagInit;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.init.EntityInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class TurretEntity extends TestElementEntity {
   public static final DataParameter<Integer> AMMO_ID;
   public static final DataParameter<Boolean> INFINITE_AMMO_ID;
   public static final DataParameter<String> STATE_ID;
   public static final DataParameter<Integer> ANIMATION_TICKS_ID;
   public static final DataParameter<Boolean> TIP_DIRECTION_RIGHT_ID;
   public static final int AMMO_PER_BULLET = 20;
   public static final int MAX_BULLETS = 64;
   public static final float BULLET_DAMAGE = 0.5F;
   public static final float BULLET_KNOCKBACK = 0.1F;
   public static final EntitySize DIMENSIONS;
   public static final EntitySize DEAD_DIMENSIONS;
   public static Predicate<LivingEntity> TARGETS;
   public int fallDuration = 10;
   public int viewDistance = 32;
   public LivingEntity previousTargetEntity = null;
   private int thisTargetShootingTicks = 0;
   public LivingEntity targetEntity = null;
   public Vector3d lastLaserPos;
   public Vector3d turretToTarget;

   public static DamageSource damageSource(LivingEntity entity) {
      return new EntityDamageSource("turret", entity);
   }

   public TurretEntity(EntityType<? extends LivingEntity> entityType, World level) {
      super(entityType, level);
      this.lastLaserPos = Vector3d.field_186680_a;
      this.turretToTarget = Vector3d.field_186680_a;
   }

   public TurretEntity(World level) {
      super((EntityType)EntityInit.TURRET.get(), level);
      this.lastLaserPos = Vector3d.field_186680_a;
      this.turretToTarget = Vector3d.field_186680_a;
   }

   protected float func_213348_b(Pose pose, EntitySize size) {
      return 0.75F;
   }

   public float func_213307_e(Pose pose) {
      return this.getState() == TurretState.DEAD ? 0.3F : 0.75F;
   }

   public EntitySize func_213305_a(Pose pose) {
      return this.getState() == TurretState.DEAD ? DEAD_DIMENSIONS : DIMENSIONS;
   }

   public void func_70071_h_() {
      boolean serverSide = !this.field_70170_p.field_72995_K;
      super.func_70071_h_();
      this.field_70759_as = this.field_70177_z;
      this.field_70761_aq = this.field_70177_z;
      if (!this.isFizzling()) {
         if (this.func_184187_bx() instanceof PlayerEntity) {
            if (serverSide) {
               this.setState(TurretState.LOST_TARGET);
            }

         } else {
            this.animate();
            Vector3d deltaMovement = this.func_213322_ci();
            Vector3d motionXZ = new Vector3d(deltaMovement.field_72450_a, (double)0.0F, deltaMovement.field_72449_c);
            if (motionXZ.func_72433_c() > 0.01 && this.getState().isStanding() && this.field_70122_E) {
               if (serverSide) {
                  this.setState(TurretState.FALLING);
                  this.setTipDirectionRight(this.func_70040_Z().func_178785_b(90.0F).func_72430_b(deltaMovement) < (double)0.0F);
               }

            } else {
               this.updateTargetEntity();
               if (this.getState() != TurretState.SHOOTING && this.getState() != TurretState.FALLING) {
                  this.thisTargetShootingTicks = 0;
               } else {
                  this.shoot();
               }

               this.previousTargetEntity = this.targetEntity;
               if (this.field_70173_aa == 1) {
                  this.func_213323_x_();
               }

            }
         }
      }
   }

   public void fizzleTick() {
      super.fizzleTick();
   }

   public void animate() {
      this.setAnimationTicks(this.getAnimationTicks() + 1);
      if (this.getState() == TurretState.OPENING && this.getAnimationTicks() >= 10 || this.getState() == TurretState.LOST_TARGET && this.getAnimationTicks() >= 20 || this.getState() == TurretState.CLOSING && this.getAnimationTicks() >= 15) {
         this.animationFinished();
         this.setAnimationTicks(0);
      }

      if (this.getState() == TurretState.RESTING || this.getState() == TurretState.SHOOTING) {
         this.setAnimationTicks(0);
      }

      if (this.getState() == TurretState.FALLING && this.getAnimationTicks() >= this.fallDuration) {
         this.setState(TurretState.DEAD);
      }

   }

   public boolean shouldShoot() {
      return this.targetEntity != null && this.turretView(this.targetEntity) != HitType.TRANSPARENT;
   }

   public void shoot() {
      if (this.shouldShoot()) {
         if (this.targetEntity == this.previousTargetEntity && this.canShoot()) {
            ++this.thisTargetShootingTicks;
            if (this.thisTargetShootingTicks > 200 && this.targetEntity instanceof ServerPlayerEntity) {
               ((CodeBoundTrigger)CriteriaTriggerInit.SURVIVE_TURRET.get()).trigger((ServerPlayerEntity)this.targetEntity);
            }
         } else {
            this.thisTargetShootingTicks = 0;
         }

         long tickTime = (long)(this.field_70173_aa + this.func_145782_y());
         if (this.canShoot() && tickTime % 2L == 0L && this.field_70170_p.field_72995_K) {
            TurretSparkParticle.createGlowParticles(this.field_70170_p, this, this.turretToTarget);
         }

         if (tickTime % 8L == 0L) {
            this.func_184185_a(this.canShoot() ? (SoundEvent)SoundInit.TURRET_FIRE.get() : (SoundEvent)SoundInit.TURRET_FIRE_FAIL.get(), 4.5F, ModUtil.randomSlightSoundPitch());
         }

         if (tickTime % 2L == 0L && this.canShoot()) {
            if (!this.getInfiniteAmmo()) {
               this.setAmmo(this.getAmmo() - 1);
            }

            Vector3d you = this.func_174824_e(1.0F);
            Vector3d theGuySheTellsYouNotToWorryAbout = this.targetEntity.func_174824_e(1.0F).func_178786_a((double)0.0F, (double)0.5F, (double)0.0F);
            AxisAlignedBB eyeToEye = new AxisAlignedBB(you, theGuySheTellsYouNotToWorryAbout);

            for(Cube cube : this.field_70170_p.func_217374_a(Cube.class, EntityPredicate.field_221016_a, this, eyeToEye)) {
               AxisAlignedBB cubeAABB = cube.func_174813_aQ();
               if (cubeAABB.func_216365_b(you, theGuySheTellsYouNotToWorryAbout).isPresent()) {
                  cube.func_184185_a((SoundEvent)SoundInit.CUBE_HIT.get(), 0.75F, ModUtil.randomSoundPitch());
                  return;
               }
            }

            if ((new Random()).nextFloat() < 0.6F && !this.targetEntity.func_184585_cz()) {
               this.targetEntity.field_70172_ad = 0;
               boolean hurt = this.targetEntity.func_70097_a(damageSource(this), 0.5F);
               if (hurt) {
                  Vector3d knockbackDirection = this.func_213303_ch().func_178788_d(this.targetEntity.func_213303_ch()).func_186678_a((double)0.5F);
                  this.targetEntity.func_233627_a_(0.1F, knockbackDirection.field_72450_a, knockbackDirection.field_72449_c);
               }
            }

            if (this.targetEntity instanceof MonsterEntity) {
               PlayerEntity nearestPlayer = this.field_70170_p.func_217362_a(this, (double)10.0F);
               if (nearestPlayer instanceof ServerPlayerEntity) {
                  ((CodeBoundTrigger)CriteriaTriggerInit.TURRET_DEFENSE.get()).trigger((ServerPlayerEntity)nearestPlayer);
               }
            }

         }
      }
   }

   public boolean func_70097_a(DamageSource source, float damage) {
      if (source.func_76352_a()) {
         if (this.getState().isStanding()) {
            this.setState(TurretState.FALLING);
            this.setTipDirectionRight((new Random()).nextBoolean());
         }

         return false;
      } else {
         return super.func_70097_a(source, damage);
      }
   }

   public boolean canShoot() {
      return this.getAmmo() > 0 || this.getInfiniteAmmo();
   }

   public Pair<HitType, Vector3d> traceAsFarAsPossible(Vector3d startPos, Vector3d endPos) {
      Vector3d direction = endPos.func_178788_d(startPos).func_72432_b();
      Vector3d transparentBlockPos = null;
      HitType hitType = HitType.CLEAR;

      while(true) {
         RayTraceContext context = new RayTraceContext(startPos, endPos, BlockMode.COLLIDER, FluidMode.NONE, this);
         BlockRayTraceResult rayTraceResult = this.field_70170_p.func_217299_a(context);
         if (rayTraceResult.func_216346_c() == Type.MISS || startPos.func_72436_e(endPos) <= 0.1) {
            return new Pair(hitType, transparentBlockPos != null ? transparentBlockPos : endPos);
         }

         BlockState blockState = this.field_70170_p.func_180495_p(rayTraceResult.func_216350_a());
         Block block = blockState.func_177230_c();
         if (hitType != HitType.TRANSPARENT && block.func_203417_a(BlockTagInit.BLOCK_PERMEABLE)) {
            hitType = HitType.PERMEABLE;
         }

         if (block.func_203417_a(BlockTagInit.BLOCK_TRANSPARENT) && transparentBlockPos == null) {
            transparentBlockPos = rayTraceResult.func_216347_e();
            hitType = HitType.TRANSPARENT;
         }

         if (!block.func_203417_a(BlockTagInit.BLOCK_PERMEABLE) && !block.func_203417_a(BlockTagInit.BLOCK_TRANSPARENT)) {
            return new Pair(HitType.SOLID, transparentBlockPos != null ? transparentBlockPos : rayTraceResult.func_216347_e());
         }

         startPos = rayTraceResult.func_216347_e().func_178787_e(direction.func_186678_a(0.3));
      }
   }

   public HitType turretView(Entity targetEntity) {
      if (targetEntity == null) {
         return HitType.SOLID;
      } else {
         Vector3d turretPos = new Vector3d(this.func_226277_ct_(), this.func_226280_cw_(), this.func_226281_cx_());
         Vector3d targetPos = new Vector3d(targetEntity.func_226277_ct_(), targetEntity.func_226280_cw_(), targetEntity.func_226281_cx_());
         return targetEntity.field_70170_p == this.field_70170_p && !(targetPos.func_72436_e(turretPos) > (double)(this.viewDistance * this.viewDistance)) ? (HitType)this.traceAsFarAsPossible(turretPos, targetPos).getFirst() : HitType.SOLID;
      }
   }

   public void updateTargetEntity() {
      AxisAlignedBB searchBox = new AxisAlignedBB(this.func_213303_ch().field_72450_a - (double)this.viewDistance, this.func_213303_ch().field_72448_b - (double)this.viewDistance, this.func_213303_ch().field_72449_c - (double)this.viewDistance, this.func_213303_ch().field_72450_a + (double)this.viewDistance, this.func_213303_ch().field_72448_b + (double)this.viewDistance, this.func_213303_ch().field_72449_c + (double)this.viewDistance);
      Map<LivingEntity, Double> entityDistances = new HashMap();

      for(LivingEntity entity : this.field_70170_p.func_217374_a(LivingEntity.class, (new EntityPredicate()).func_221012_a(TARGETS), this, searchBox)) {
         Vector3d ray = entity.func_213303_ch().func_178788_d(this.func_213303_ch());
         double cosine = ray.func_72432_b().func_72430_b(this.func_70040_Z());
         double distanceSqr = ray.func_189985_c();
         if ((this.func_96124_cp() == null || entity.func_96124_cp() == null || !this.func_96124_cp().func_142054_a(entity.func_96124_cp())) && cosine > 0.6) {
            entityDistances.put(entity, distanceSqr);
         }
      }

      ArrayList<Map.Entry<LivingEntity, Double>> orderedEntities = new ArrayList(entityDistances.entrySet());
      orderedEntities.sort(Entry.comparingByValue());

      for(Map.Entry<LivingEntity, Double> entry : orderedEntities) {
         LivingEntity entity = (LivingEntity)entry.getKey();
         if (this.turretView(entity) != HitType.SOLID) {
            if (entry != this.targetEntity) {
               this.targetAcquired(entity);
            }

            return;
         }
      }

      if (this.targetEntity != null) {
         this.targetLost();
      }

   }

   public void targetAcquired(LivingEntity entity) {
      this.targetEntity = entity;
      if (this.getState() == TurretState.RESTING) {
         this.setState(TurretState.OPENING);
      }

   }

   public void targetLost() {
      this.targetEntity = null;
      if (this.getState() == TurretState.SHOOTING) {
         this.setState(TurretState.LOST_TARGET);
      }

   }

   public void animationFinished() {
      switch (this.getState()) {
         case OPENING:
         case LOST_TARGET:
            this.setState(this.targetEntity == null ? TurretState.CLOSING : TurretState.SHOOTING);
            break;
         case CLOSING:
            this.setState(this.targetEntity == null ? TurretState.RESTING : TurretState.OPENING);
      }

   }

   public boolean hasTarget() {
      return this.targetEntity != null;
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.func_233666_p_();
   }

   public void onSpawnedByPlayer(PlayerEntity player) {
      if (player.func_184812_l_()) {
         this.setInfiniteAmmo(true);
         player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.turret.infinite_ammo"), true);
      }

      this.field_70177_z = player.func_225608_bj_() ? player.field_70177_z : (float)(Math.round(player.field_70177_z / 45.0F) * 45);
   }

   public Iterable<ItemStack> func_184193_aE() {
      return Collections.emptyList();
   }

   public ItemStack func_184582_a(EquipmentSlotType type) {
      return ItemStack.field_190927_a;
   }

   public void func_184201_a(EquipmentSlotType type, ItemStack itemStack) {
   }

   public HandSide func_184591_cq() {
      return HandSide.RIGHT;
   }

   protected void func_213333_a(DamageSource source, int p_213333_2_, boolean p_213333_3_) {
      Entity entity = source.func_76346_g();
      if (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).func_184812_l_()) {
         this.func_199701_a_(new ItemStack((IItemProvider)ItemInit.BULLETS.get(), this.getAmmo() / 20));
      }
   }

   public ActionResultType func_184230_a(PlayerEntity player, Hand hand) {
      ItemStack holdingItem = player.func_184586_b(hand);
      if (holdingItem.func_77973_b() == ItemInit.BULLETS.get()) {
         if (this.getAmmo() >= 1280) {
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.turret.full"), true);
            return ActionResultType.PASS;
         } else if (this.getInfiniteAmmo()) {
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.turret.infinite_ammo"), true);
            return ActionResultType.PASS;
         } else {
            int bulletStoreAmount = player.func_225608_bj_() ? Math.min(holdingItem.func_190916_E(), 64 - this.getAmmo() / 20) : 1;
            this.setAmmo(this.getAmmo() + bulletStoreAmount * 20);
            if (!player.func_184812_l_()) {
               holdingItem.func_190918_g(bulletStoreAmount);
            }

            if (!this.field_70170_p.field_72995_K) {
               this.setWiggle(10);
               this.setHurtDir(-this.getHurtDir());
               this.func_184185_a((SoundEvent)SoundInit.TURRET_STOCK.get(), 1.0F, ModUtil.randomSlightSoundPitch());
            }

            return ActionResultType.SUCCESS;
         }
      } else if (holdingItem.func_77973_b() instanceof WrenchItem) {
         if (this.getInfiniteAmmo()) {
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.turret.infinite_ammo"), true);
            return ActionResultType.PASS;
         } else if (this.getAmmo() == 0) {
            player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.turret.empty"), true);
            return ActionResultType.CONSUME;
         } else {
            if (this.getAmmo() >= 20) {
               this.func_70099_a(new ItemStack((IItemProvider)ItemInit.BULLETS.get(), this.getAmmo() / 20), 0.8F);
            } else {
               this.func_70099_a(new ItemStack((IItemProvider)ItemInit.BULLETS.get()), 0.8F);
            }

            this.setAmmo(0);
            if (!this.field_70170_p.field_72995_K) {
               this.setWiggle(10);
               this.setHurtDir(-this.getHurtDir());
               this.func_184185_a((SoundEvent)SoundInit.TURRET_STOCK.get(), 1.0F, ModUtil.randomSlightSoundPitch());
            }

            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(AMMO_ID, 0);
      this.field_70180_af.func_187214_a(INFINITE_AMMO_ID, false);
      this.field_70180_af.func_187214_a(STATE_ID, String.valueOf(TurretState.RESTING));
      this.field_70180_af.func_187214_a(ANIMATION_TICKS_ID, 0);
      this.field_70180_af.func_187214_a(TIP_DIRECTION_RIGHT_ID, false);
   }

   public void func_213281_b(CompoundNBT nbt) {
      super.func_213281_b(nbt);
      nbt.func_74768_a("Ammo", this.getAmmo());
      nbt.func_74757_a("InfiniteAmmo", this.getInfiniteAmmo());
      nbt.func_74778_a("State", String.valueOf(this.getState()));
   }

   public void func_70037_a(CompoundNBT nbt) {
      super.func_70037_a(nbt);
      if (nbt.func_74764_b("Ammo")) {
         this.setAmmo(nbt.func_74762_e("Ammo"));
      }

      if (nbt.func_74764_b("InfiniteAmmo")) {
         this.setInfiniteAmmo(nbt.func_74767_n("InfiniteAmmo"));
      }

      if (nbt.func_74764_b("State")) {
         this.setState(TurretState.valueOf(nbt.func_74779_i("State")));
      }

   }

   public int getAmmo() {
      return (Integer)this.field_70180_af.func_187225_a(AMMO_ID);
   }

   public void setAmmo(int ammo) {
      this.field_70180_af.func_187227_b(AMMO_ID, ammo);
   }

   public boolean getInfiniteAmmo() {
      return (Boolean)this.field_70180_af.func_187225_a(INFINITE_AMMO_ID);
   }

   public void setInfiniteAmmo(boolean infiniteAmmo) {
      this.field_70180_af.func_187227_b(INFINITE_AMMO_ID, infiniteAmmo);
   }

   public void setState(TurretState state) {
      TurretState oldState = this.getState();
      this.field_70180_af.func_187227_b(STATE_ID, String.valueOf(state));
      this.setAnimationTicks(0);
      this.func_213323_x_();
      if (state != TurretState.OPENING && (state != TurretState.FALLING || oldState.wingsOpen())) {
         if (state == TurretState.CLOSING || state == TurretState.DEAD && oldState.wingsOpen()) {
            this.func_184185_a((SoundEvent)SoundInit.TURRET_CLOSE.get(), 3.5F, ModUtil.randomSlightSoundPitch());
         }
      } else {
         this.func_184185_a((SoundEvent)SoundInit.TURRET_OPEN.get(), 3.5F, ModUtil.randomSlightSoundPitch());
      }

   }

   public TurretState getState() {
      return TurretState.valueOf((String)this.field_70180_af.func_187225_a(STATE_ID));
   }

   public boolean shouldLaserMove() {
      return this.getState() == TurretState.SHOOTING || this.getState() == TurretState.OPENING;
   }

   public boolean shouldLaserEase() {
      return this.getState() == TurretState.SHOOTING || this.getState() == TurretState.LOST_TARGET || this.getState() == TurretState.OPENING;
   }

   public int getAnimationTicks() {
      return (Integer)this.field_70180_af.func_187225_a(ANIMATION_TICKS_ID);
   }

   public void setAnimationTicks(int animationTick) {
      this.field_70180_af.func_187227_b(ANIMATION_TICKS_ID, animationTick);
   }

   public boolean getTipDirectionRight() {
      return (Boolean)this.field_70180_af.func_187225_a(TIP_DIRECTION_RIGHT_ID);
   }

   public void setTipDirectionRight(boolean right) {
      this.field_70180_af.func_187227_b(TIP_DIRECTION_RIGHT_ID, right);
   }

   static {
      AMMO_ID = EntityDataManager.func_187226_a(TurretEntity.class, DataSerializers.field_187192_b);
      INFINITE_AMMO_ID = EntityDataManager.func_187226_a(TurretEntity.class, DataSerializers.field_187198_h);
      STATE_ID = EntityDataManager.func_187226_a(TurretEntity.class, DataSerializers.field_187194_d);
      ANIMATION_TICKS_ID = EntityDataManager.func_187226_a(TurretEntity.class, DataSerializers.field_187192_b);
      TIP_DIRECTION_RIGHT_ID = EntityDataManager.func_187226_a(TurretEntity.class, DataSerializers.field_187198_h);
      DIMENSIONS = EntitySize.func_220314_b(0.5F, 1.2F);
      DEAD_DIMENSIONS = EntitySize.func_220314_b(0.5F, 0.5F);
      TARGETS = (e) -> !(e instanceof TestElementEntity) && !e.func_175149_v() && (!(e instanceof PlayerEntity) || !((PlayerEntity)e).func_184812_l_()) && (!e.func_70631_g_() || e.getClassification(false) == EntityClassification.MONSTER) && !e.func_82150_aj() && !WrenchItem.holdingWrench(e);
   }
}
