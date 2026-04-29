package net.portalmod.common.entities;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.particles.FizzleFlakeParticle;
import net.portalmod.common.particles.FizzleGlowParticle;
import net.portalmod.common.particles.PortalGunSparkParticle;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portalgun.CPortalGunInteractionPacket;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.common.sorted.portalgun.PortalGunInteraction;
import net.portalmod.core.init.EntityTagInit;
import net.portalmod.core.init.FluidInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.math.AABBUtil;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;

public abstract class TestElementEntity extends LivingEntity implements Fizzleable {
   public static final DataParameter<Integer> FIZZLE_TICKS_ID;
   public static final DataParameter<Boolean> FROM_DROPPER_ID;
   private static final DataParameter<Integer> WIGGLE_ID;
   private static final DataParameter<Integer> DATA_ID_HURT;
   private static final DataParameter<Integer> DATA_ID_HURTDIR;
   private static final DataParameter<Float> DATA_ID_DAMAGE;
   public static final float HOLDING_DISTANCE = 1.5F;
   public int maxFizzleTime = 35;
   public boolean canFizzle = true;
   public Vec3 serverOldPos;
   private Vec3 oldRidingVec;
   private boolean holderJustTeleported;
   private Mat4 holderJustTeleportedMatrix;
   private Integer oldPortalChainLength;
   private Float eyeHeightOld;

   public TestElementEntity(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
      super(p_i48577_1_, p_i48577_2_);
   }

   public void func_70071_h_() {
      if (this.getWiggle() > 0) {
         this.setWiggle(this.getWiggle() - 1);
      }

      if (this.getHurtTime() > 0) {
         this.setHurtTime(this.getHurtTime() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      super.func_70071_h_();
      this.checkForFizzlers(this);
      if (this.isFizzling()) {
         this.fizzleTick();
      }

   }

   public boolean shouldCheckForFizzlers() {
      return !this.isFizzling();
   }

   public void onTouchingFizzler() {
      if (!this.isFizzling()) {
         this.startFizzling();
         this.fizzleTick();
      }

   }

   public boolean isFizzling() {
      return this.getFizzleTicks() > 0;
   }

   public void startFizzling() {
      if (this.canFizzle && !this.isFizzling()) {
         this.setFizzleTicks(this.getFizzleTicks() + 1);
      }

   }

   public int getFizzleLight(int packedLight) {
      int fizzleAmount = (int)((double)this.getFizzleTicks() * 0.6);
      return LightTexture.func_228451_a_(Math.max(0, LightTexture.func_228450_a_(packedLight) - fizzleAmount), Math.max(0, LightTexture.func_228454_b_(packedLight) - fizzleAmount));
   }

   public void fizzleTick() {
      int fizzleTicks = this.getFizzleTicks();
      if (fizzleTicks == 1) {
         this.fizzleInit();
      }

      double minSpeed = 0.08;
      Vector3d xzMovement = this.func_213322_ci().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F);
      if (xzMovement.func_72433_c() == (double)0.0F) {
         xzMovement = new Vector3d((double)(new Random()).nextFloat() * 0.1 - 0.05, (double)0.0F, (double)(new Random()).nextFloat() * 0.1 - 0.05);
      }

      Vector3d newMovement = xzMovement.func_72433_c() < minSpeed ? xzMovement.func_72432_b().func_186678_a(minSpeed) : xzMovement.func_186678_a(0.95);
      this.func_213317_d(newMovement);
      if (this.field_70170_p.field_72995_K) {
         FizzleGlowParticle.createGlowParticles(this.field_70170_p, this);
         FizzleFlakeParticle.createFlakeParticles(this.field_70170_p, this);
      }

      this.field_70177_z += 35.0F * (float)newMovement.func_72433_c();
      this.field_70759_as = this.field_70177_z;
      if (fizzleTicks > this.maxFizzleTime && this.func_70089_S()) {
         this.fizzleKill();
      }

      this.setFizzleTicks(fizzleTicks + 1);
   }

   public void fizzleInit() {
      this.func_189654_d(true);
      this.field_70170_p.func_184148_a((PlayerEntity)null, this.func_213303_ch().field_72450_a, this.func_213303_ch().field_72448_b, this.func_213303_ch().field_72449_c, (SoundEvent)SoundInit.ENTITY_FIZZLE.get(), SoundCategory.NEUTRAL, 1.0F, ModUtil.randomSlightSoundPitch());
   }

   public void fizzleKill() {
      if (!this.isFromDropper() && !this.func_200600_R().func_220341_a(EntityTagInit.FIZZLER_NO_ITEM_DROPS) && !this.field_70170_p.field_72995_K) {
         this.func_213345_d(new DamageSource("fizzle"));
      }

      this.func_70106_y();
   }

   public void awardKill() {
      Entity holder = this.func_184187_bx();
      if (this.func_184218_aH() && holder instanceof PlayerEntity) {
         ((PlayerEntity)holder).func_71029_a(Stats.field_199090_h.func_199076_b(this.func_200600_R()));
         if (holder instanceof ServerPlayerEntity) {
            CriteriaTriggers.field_192122_b.func_192211_a((ServerPlayerEntity)holder, this, DamageSource.func_76365_a((PlayerEntity)holder));
         }
      }

   }

   public boolean pickUp(PlayerEntity player) {
      boolean riding = this.func_184220_m(player);
      if (!riding) {
         return false;
      } else {
         if (player.field_70170_p.func_201670_d()) {
            PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.PICK_ENTITY)).data(this.func_145782_y()).build());
         }

         return true;
      }
   }

   public void func_70098_U() {
      this.func_213317_d(Vector3d.field_186680_a);
      this.func_70071_h_();
      if (!(this.func_184187_bx() instanceof PlayerEntity)) {
         if (!(this.func_184187_bx() instanceof AbstractMinecartEntity) && !(this.func_184187_bx() instanceof BoatEntity)) {
            this.func_184210_p();
         } else {
            super.func_70098_U();
         }

      } else {
         PlayerEntity player = (PlayerEntity)this.func_184187_bx();
         if (!this.field_70170_p.field_72995_K) {
            if (this.serverOldPos != null) {
               this.field_70169_q = this.serverOldPos.x;
               this.field_70167_r = this.serverOldPos.y;
               this.field_70166_s = this.serverOldPos.z;
               this.field_70142_S = this.serverOldPos.x;
               this.field_70137_T = this.serverOldPos.y;
               this.field_70136_U = this.serverOldPos.z;
            }

            this.checkForFizzlers(this);
         } else {
            Vec3 eyePos = new Vec3(player.func_174824_e(1.0F).func_72441_c((double)0.0F, -0.4, (double)0.0F));
            Vec3 eyeOldPos = new Vec3(player.func_174824_e(0.0F).func_72441_c((double)0.0F, -0.4, (double)0.0F));
            Vec3 originalEyePos = eyePos.clone();
            if (this.eyeHeightOld != null) {
               eyeOldPos = eyeOldPos.sub((double)0.0F, (double)player.func_70047_e(), (double)0.0F);
               eyeOldPos.add((double)0.0F, (double)this.eyeHeightOld, (double)0.0F);
            }

            this.eyeHeightOld = player.func_70047_e();
            Vec3 originalEyeOldPos = eyeOldPos.clone();
            Vec3 ridingVersor = (new Vec3((double)0.0F, (double)0.0F, (double)1.0F)).transform(new Mat4(Vector3f.field_229179_b_.func_229187_a_(player.func_195050_f(1.0F)))).transform(new Mat4(Vector3f.field_229180_c_.func_229187_a_(player.func_195046_g(1.0F))));
            Vec3 ridingVec = ridingVersor.clone().mul((double)1.5F);
            Vec3 to = eyePos.clone().add(ridingVec);
            List<PortalEntity> portalChain = ModUtil.getPortalsAlongRay(this.field_70170_p, eyePos, to, (portalx) -> true);
            boolean passedPortal = this.oldPortalChainLength != null && portalChain.size() != this.oldPortalChainLength;
            this.oldPortalChainLength = portalChain.size();
            Mat4 portalMatrix = Mat4.identity();
            Mat4 portalRotationMatrix = Mat4.identity();

            for(PortalEntity portal : portalChain) {
               if (!portal.getOtherPortal().isPresent()) {
                  break;
               }

               Mat4 matrix = portal.getSourceBasis().getChangeOfBasisMatrix(((PortalEntity)portal.getOtherPortal().get()).getDestinationBasis());
               portalMatrix = Mat4.identity().translate(new Vec3(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch())).mul(matrix).translate((new Vec3(portal.func_213303_ch())).negate()).mul(portalMatrix);
               portalRotationMatrix = Mat4.identity().mul(matrix).mul(portalRotationMatrix);
            }

            eyePos = eyePos.transform(portalMatrix);
            eyeOldPos = eyeOldPos.transform(portalMatrix);
            ridingVec = ridingVec.transform(portalRotationMatrix);
            ridingVersor = ridingVersor.transform(portalRotationMatrix);
            Vector3d ridingPos = eyePos.clone().add(ridingVec).to3d();
            if (passedPortal && !this.holderJustTeleported) {
               AxisAlignedBB nextSpace = this.func_174813_aQ().func_191194_a(this.func_213303_ch().func_216371_e()).func_191194_a(ridingPos);
               boolean nextSpaceEmpty = true;
               List<PortalEntity> portalsInCube = PortalEntity.getOpenPortals(this.field_70170_p, nextSpace, (portalx) -> true);
               if (portalsInCube.isEmpty()) {
                  dropHeldEntities(player, false, true, player.func_184614_ca());
                  PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.RELEASE_ENTITY)).build());
                  return;
               }

               AxisAlignedBB oldHitbox = this.func_174813_aQ();
               this.func_174826_a(nextSpace);

               for(BlockPos pos : AABBUtil.getBlocksWithin(nextSpace)) {
                  VoxelShape blockShape = this.field_70170_p.func_180495_p(pos).func_215685_b(this.field_70170_p, pos, ISelectionContext.func_216374_a(this)).func_197751_a((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
                  if (VoxelShapes.func_197879_c(blockShape, VoxelShapes.func_197881_a(nextSpace), IBooleanFunction.field_223238_i_)) {
                     nextSpaceEmpty = false;
                  }
               }

               this.func_174826_a(oldHitbox);
               if (!nextSpaceEmpty) {
                  dropHeldEntities(player, false, true, player.func_184614_ca());
                  PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.RELEASE_ENTITY)).build());
                  return;
               }
            }

            Vec3 lookVec = (new Vec3((double)0.0F, (double)0.0F, (double)1.0F)).transform(new Mat4(Vector3f.field_229180_c_.func_229187_a_(this.func_184187_bx().func_70079_am()))).transform(portalRotationMatrix);
            this.field_70177_z = -((float)(Math.atan2(lookVec.x, lookVec.z) * (double)180.0F / Math.PI));
            this.field_70761_aq = this.field_70177_z;
            Vec3 oldRidingVec = this.oldRidingVec;
            if (oldRidingVec == null) {
               Vec3 unteleportedOldRidingVec = new Vec3(this.func_213303_ch());

               for(int i = portalChain.size() - 1; i >= 0; --i) {
                  PortalEntity portal = (PortalEntity)portalChain.get(i);
                  if (!portal.getOtherPortal().isPresent()) {
                     break;
                  }

                  Mat4 matrix = ((PortalEntity)portal.getOtherPortal().get()).getSourceBasis().getChangeOfBasisMatrix(portal.getDestinationBasis());
                  unteleportedOldRidingVec = unteleportedOldRidingVec.sub(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch()).transform(matrix).add(portal.func_213303_ch());
               }

               oldRidingVec = unteleportedOldRidingVec.clone().sub(originalEyeOldPos);
            }

            oldRidingVec = oldRidingVec.transform(portalRotationMatrix);
            if (this.holderJustTeleported) {
               if (this.holderJustTeleportedMatrix != null) {
                  oldRidingVec = oldRidingVec.transform(this.holderJustTeleportedMatrix);
                  this.holderJustTeleportedMatrix = null;
               } else {
                  oldRidingVec = ridingVersor.clone().mul(oldRidingVec.magnitude());
               }

               this.holderJustTeleported = false;
            } else if (passedPortal) {
               this.field_70126_B = this.field_70177_z;
               this.field_70760_ar = this.field_70761_aq;
            }

            Vec3 oldPos = eyeOldPos.clone().add(oldRidingVec);
            Vec3 center = (new Vec3(this.func_174813_aQ().func_189972_c())).sub((double)0.0F, this.func_174813_aQ().func_216360_c() / (double)2.0F, (double)0.0F);
            this.func_174826_a(this.func_174813_aQ().func_191194_a(center.negate().to3d()).func_191194_a(oldPos.to3d()));
            this.func_226286_f_(oldPos.x, oldPos.y, oldPos.z);
            this.func_213315_a(MoverType.SELF, ridingPos.func_178788_d(ModUtil.getOldPos(this)));
            PacketInit.INSTANCE.sendToServer(new CTestElementHoldingPacket(this.func_145782_y(), new Vec3(ModUtil.getOldPos(this)), new Vec3(this.func_213303_ch()), this.field_70760_ar, this.field_70761_aq));
            if (this.func_213303_ch().func_72438_d(ridingPos) > (double)1.0F) {
               dropHeldEntities(player, false, true, player.func_184614_ca());
               PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.RELEASE_ENTITY)).build());
            }

            Vec3 unteleportedPos = new Vec3(this.func_213303_ch());

            for(int i = portalChain.size() - 1; i >= 0; --i) {
               PortalEntity portal = (PortalEntity)portalChain.get(i);
               if (!portal.getOtherPortal().isPresent()) {
                  break;
               }

               Mat4 matrix = ((PortalEntity)portal.getOtherPortal().get()).getSourceBasis().getChangeOfBasisMatrix(portal.getDestinationBasis());
               unteleportedPos = unteleportedPos.sub(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch()).transform(matrix).add(portal.func_213303_ch());
            }

            this.oldRidingVec = unteleportedPos.clone().sub(originalEyePos);
         }

         this.field_70143_R = 0.0F;
         if (player.field_70170_p.field_72995_K) {
            if (player.func_184614_ca().func_77973_b() instanceof PortalGun) {
               PortalGunSparkParticle.createParticles(this.field_70170_p, player, false);
            } else if (player.func_184592_cb().func_77973_b() instanceof PortalGun) {
               PortalGunSparkParticle.createParticles(this.field_70170_p, player, true);
            }
         }

         if (this.isFizzling()) {
            this.awardKill();
            dropHeldEntities(player, false, false, player.func_184614_ca());
         }

      }
   }

   public void onHolderTeleport(PortalEntity from, PortalEntity to) {
      if (this.holderJustTeleportedMatrix == null) {
         this.holderJustTeleportedMatrix = Mat4.identity();
      }

      Mat4 matrix = from.getSourceBasis().getChangeOfBasisMatrix(to.getDestinationBasis());
      this.holderJustTeleportedMatrix = matrix.mul(this.holderJustTeleportedMatrix);
      this.holderJustTeleported = true;
   }

   public void onHolderTeleportPacket() {
      this.holderJustTeleported = true;
   }

   public boolean func_184220_m(Entity entity) {
      this.oldRidingVec = null;
      this.oldPortalChainLength = null;
      return super.func_184220_m(entity);
   }

   public void func_184210_p() {
      Entity vehicle = this.func_184187_bx();
      this.func_233575_bb_();
      if (vehicle instanceof PlayerEntity) {
         this.field_184245_j = 0;
         Vector3d momentum;
         if (this.field_70170_p.field_72995_K) {
            momentum = this.func_213303_ch().func_178788_d(ModUtil.getOldPos(this));
         } else {
            momentum = this.func_213303_ch().func_178788_d(this.serverOldPos.to3d());
         }

         this.func_213317_d(momentum.func_72441_c((double)ModUtil.symmetricRandom(0.01F), (double)0.0F, (double)ModUtil.symmetricRandom(0.01F)));
      }
   }

   public static void dropHeldEntities(PlayerEntity player, boolean yeet, boolean nullifyMomentum, ItemStack itemStack) {
      List<Entity> passengers = player.func_184188_bt();

      for(int i = passengers.size() - 1; i >= 0; --i) {
         Entity entity = (Entity)passengers.get(i);
         if (entity instanceof TestElementEntity) {
            entity.func_184210_p();
            if (itemStack.func_77973_b() instanceof PortalGun) {
               PortalGun.dropCube(player, itemStack);
            }

            float maxSpeed = 0.5F;
            float strength = 0.3F;
            Vector3d velocity = entity.func_213322_ci();
            boolean exceedsLimit = velocity.func_178787_e(player.func_213322_ci().func_216371_e()).func_72433_c() > (double)0.5F;
            if (nullifyMomentum) {
               entity.func_213317_d(Vector3d.field_186680_a);
            } else {
               if (exceedsLimit) {
                  entity.func_213317_d(velocity.func_72432_b().func_216372_d((double)0.5F, (double)0.5F, (double)0.5F).func_178787_e(player.func_213322_ci()));
               }

               if (yeet) {
                  entity.func_213317_d(velocity.func_178787_e(player.func_70676_i(0.0F).func_216372_d((double)0.3F, (double)0.3F, (double)0.3F)));
               }
            }
         }
      }

   }

   public boolean func_184186_bw() {
      Entity entity = this.func_184187_bx();
      if (entity instanceof PlayerEntity) {
         return ((PlayerEntity)entity).func_175144_cb();
      } else {
         return !this.field_70170_p.field_72995_K;
      }
   }

   public boolean isHoldable() {
      return !this.isFizzling();
   }

   public static boolean isHoldable(Entity entity) {
      return entity instanceof TestElementEntity && ((TestElementEntity)entity).isHoldable();
   }

   public boolean isPickedUp() {
      return this.func_184187_bx() != null && this.func_184187_bx() instanceof PlayerEntity;
   }

   public void dropIfPickedUp() {
      if (this.isPickedUp()) {
         PlayerEntity player = (PlayerEntity)this.func_184187_bx();

         assert player != null;

         dropHeldEntities(player, false, false, player.func_184614_ca());
      }

   }

   public void remove(boolean keepData) {
      super.remove(keepData);
      this.dropIfPickedUp();
   }

   public boolean func_70097_a(DamageSource source, float damage) {
      if (!this.field_70170_p.field_72995_K && this.func_70089_S()) {
         boolean shouldSwing = true;
         boolean holdingWrench = source instanceof EntityDamageSource && source.func_76346_g() instanceof LivingEntity && WrenchItem.hitWithWrench((LivingEntity)source.func_76346_g());
         boolean outOfWorld = source == DamageSource.field_76380_i;
         boolean inGoo = source == FluidInit.GOO_DAMAGE && this.func_184187_bx() == null;
         boolean isCreative = source.func_180136_u();
         boolean shouldHurt = holdingWrench || inGoo || outOfWorld || isCreative;
         if (holdingWrench) {
            damage *= 1.5F;
         }

         if (inGoo) {
            damage *= 0.1F;
            shouldSwing = false;
         }

         if (shouldHurt) {
            this.applyDamage(source, damage, shouldSwing, isCreative);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void applyDamage(DamageSource source, float damage, boolean swing, boolean creative) {
      if (swing) {
         this.setHurtDir(-this.getHurtDir());
         this.setHurtTime(10);
      }

      this.setDamage(this.getDamage() + damage * 10.0F);
      this.func_70018_K();
      if (creative || this.getDamage() > 40.0F) {
         this.func_70106_y();
         if (!creative && !this.isFromDropper() && this.field_70170_p.func_82736_K().func_223586_b(GameRules.field_223604_g)) {
            this.func_213345_d(source);
         }
      }

   }

   public void func_70057_ab() {
      this.setHurtDir(-this.getHurtDir());
      this.setHurtTime(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(FIZZLE_TICKS_ID, 0);
      this.field_70180_af.func_187214_a(FROM_DROPPER_ID, false);
      this.field_70180_af.func_187214_a(WIGGLE_ID, 0);
      this.field_70180_af.func_187214_a(DATA_ID_HURT, 0);
      this.field_70180_af.func_187214_a(DATA_ID_HURTDIR, 1);
      this.field_70180_af.func_187214_a(DATA_ID_DAMAGE, 0.0F);
   }

   public void func_213281_b(CompoundNBT nbt) {
      super.func_213281_b(nbt);
      nbt.func_74768_a("FizzleTicks", this.getFizzleTicks());
      nbt.func_74757_a("FromDropper", this.isFromDropper());
   }

   public void func_70037_a(CompoundNBT nbt) {
      super.func_70037_a(nbt);
      if (nbt.func_74764_b("FizzleTicks")) {
         this.setFizzleTicks(nbt.func_74762_e("FizzleTicks"));
      }

      if (nbt.func_74764_b("FromDropper")) {
         this.setFromDropper(nbt.func_74767_n("FromDropper"));
      }

   }

   public int getFizzleTicks() {
      return (Integer)this.field_70180_af.func_187225_a(FIZZLE_TICKS_ID);
   }

   public void setFizzleTicks(int fizzleTicks) {
      this.field_70180_af.func_187227_b(FIZZLE_TICKS_ID, fizzleTicks);
   }

   public boolean isFromDropper() {
      return (Boolean)this.field_70180_af.func_187225_a(FROM_DROPPER_ID);
   }

   public void setFromDropper(boolean fromDropper) {
      this.field_70180_af.func_187227_b(FROM_DROPPER_ID, fromDropper);
   }

   public int getWiggle() {
      return (Integer)this.field_70180_af.func_187225_a(WIGGLE_ID);
   }

   public void setWiggle(int wiggle) {
      this.field_70180_af.func_187227_b(WIGGLE_ID, wiggle);
   }

   public void setDamage(float damage) {
      this.field_70180_af.func_187227_b(DATA_ID_DAMAGE, damage);
   }

   public float getDamage() {
      return (Float)this.field_70180_af.func_187225_a(DATA_ID_DAMAGE);
   }

   public void setHurtTime(int hurtTime) {
      this.field_70180_af.func_187227_b(DATA_ID_HURT, hurtTime);
   }

   public int getHurtTime() {
      return (Integer)this.field_70180_af.func_187225_a(DATA_ID_HURT);
   }

   public void setHurtDir(int hurtDir) {
      this.field_70180_af.func_187227_b(DATA_ID_HURTDIR, hurtDir);
   }

   public int getHurtDir() {
      return (Integer)this.field_70180_af.func_187225_a(DATA_ID_HURTDIR);
   }

   static {
      FIZZLE_TICKS_ID = EntityDataManager.func_187226_a(TestElementEntity.class, DataSerializers.field_187192_b);
      FROM_DROPPER_ID = EntityDataManager.func_187226_a(TestElementEntity.class, DataSerializers.field_187198_h);
      WIGGLE_ID = EntityDataManager.func_187226_a(TestElementEntity.class, DataSerializers.field_187192_b);
      DATA_ID_HURT = EntityDataManager.func_187226_a(TestElementEntity.class, DataSerializers.field_187192_b);
      DATA_ID_HURTDIR = EntityDataManager.func_187226_a(TestElementEntity.class, DataSerializers.field_187192_b);
      DATA_ID_DAMAGE = EntityDataManager.func_187226_a(TestElementEntity.class, DataSerializers.field_187193_c);
   }
}
