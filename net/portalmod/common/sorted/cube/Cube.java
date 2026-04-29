package net.portalmod.common.sorted.cube;

import java.util.ArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.sorted.button.SuperButtonBlock;
import net.portalmod.common.triggers.CodeBoundTrigger;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.init.EntityInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class Cube extends TestElementEntity {
   public double oldDeltaY = (double)0.0F;
   public boolean oldActive = true;
   public boolean wasOnGround = true;
   public float wasVerticalSpeed = 0.0F;

   public Cube(EntityType<? extends LivingEntity> entityType, World level) {
      super(entityType, level);
   }

   public static DamageSource cube(LivingEntity entity) {
      return new EntityDamageSource("cube", entity);
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.func_233666_p_();
   }

   public void func_70106_y() {
      super.func_70106_y();
   }

   public boolean func_70104_M() {
      return true;
   }

   public boolean func_241845_aY() {
      return true;
   }

   public boolean func_241849_j(Entity entity) {
      return entity instanceof PlayerEntity && !entity.func_184196_w(this) && super.func_241849_j(entity);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_200600_R() == EntityInit.GABE.get() && this.field_70146_Z.nextDouble() < 0.001) {
         this.func_184185_a((SoundEvent)SoundInit.CUBE_GABE.get(), 3.0F, ModUtil.randomSlightSoundPitch());
      }

      if (!this.isFizzling()) {
         if (!this.field_70170_p.field_72995_K && this.func_184218_aH() && this.isActive() && !this.oldActive) {
            ((CodeBoundTrigger)CriteriaTriggerInit.CUBE_ON_BUTTON.get()).trigger((ServerPlayerEntity)this.func_184187_bx());
         }

         this.oldActive = this.isActive();
         AxisAlignedBB aabb = this.func_174813_aQ().func_186662_g(1.0E-7);

         for(Entity entity : this.field_70170_p.func_72839_b(this, aabb.func_186662_g(0.1))) {
            AxisAlignedBB entityAABB = entity.func_174813_aQ();
            double x0 = aabb.field_72336_d - entityAABB.field_72340_a;
            double x1 = -(aabb.field_72340_a - entityAABB.field_72336_d);
            double y1 = -(aabb.field_72338_b - entityAABB.field_72337_e);
            double z0 = aabb.field_72334_f - entityAABB.field_72339_c;
            double z1 = -(aabb.field_72339_c - entityAABB.field_72334_f);
            double x = Math.min(x0, x1);
            double z = Math.min(z0, z1);
            if (y1 < x && y1 < z && this.func_213322_ci().field_72448_b > -0.1 && this.oldDeltaY < (double)-0.5F && entity instanceof LivingEntity) {
               DamageSource damageSource = cube(this);
               float damage = (float)this.oldDeltaY * -3.0F;
               ((LivingEntity)entity).func_110142_aN().func_94547_a(damageSource, ((LivingEntity)entity).func_110143_aJ(), damage);
               entity.func_70097_a(damageSource, damage);
            }
         }

         if (this.func_233570_aj_() && !this.wasOnGround && this.oldDeltaY < -0.3) {
            float volume = MathHelper.func_76131_a((float)(-this.oldDeltaY), 0.0F, 1.0F);
            this.field_70170_p.func_184148_a((PlayerEntity)null, this.func_213303_ch().field_72450_a, this.func_213303_ch().field_72448_b, this.func_213303_ch().field_72449_c, (SoundEvent)SoundInit.CUBE_HIT.get(), SoundCategory.NEUTRAL, volume, ModUtil.randomSoundPitch());
         }

         this.wasOnGround = this.func_233570_aj_();
         this.func_145775_I();

         for(Entity entity : this.field_70170_p.func_175674_a(this, this.func_174813_aQ().func_72314_b(0.05, -0.01, 0.05), EntityPredicates.func_200823_a(this))) {
            this.func_70108_f(entity);
         }
      }

      this.field_70761_aq = this.field_70177_z;
      this.oldDeltaY = this.func_213322_ci().field_72448_b;
      this.field_70759_as = this.field_70761_aq;
   }

   public void onSpawnedByPlayer() {
      this.field_70170_p.func_184148_a((PlayerEntity)null, this.func_213303_ch().field_72450_a, this.func_213303_ch().field_72448_b, this.func_213303_ch().field_72449_c, (SoundEvent)SoundInit.CUBE_HIT.get(), SoundCategory.NEUTRAL, 0.5F, ModUtil.randomSoundPitch());
   }

   public boolean func_184218_aH() {
      return super.func_184218_aH();
   }

   public boolean isActive() {
      for(int z = -1; z <= 1; ++z) {
         for(int y = -1; y <= 1; ++y) {
            for(int x = -1; x <= 1; ++x) {
               BlockPos pos = (new BlockPos(x, y, z)).func_177971_a(this.func_233580_cy_());
               BlockState state = this.field_70170_p.func_180495_p(pos);
               if (state.func_177230_c() == BlockInit.SUPER_BUTTON.get() && (Boolean)state.func_177229_b(SuperButtonBlock.ACTIVE) && ((SuperButtonBlock)BlockInit.SUPER_BUTTON.get()).getTrigger(state, pos).func_72326_a(this.func_174813_aQ())) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   protected int func_225508_e_(float f1, float f2) {
      return 0;
   }

   public Iterable<ItemStack> func_184193_aE() {
      return new ArrayList();
   }

   public ItemStack func_184582_a(EquipmentSlotType slotType) {
      return ItemStack.field_190927_a;
   }

   public void func_184201_a(EquipmentSlotType slotType, ItemStack itemStack) {
   }

   public HandSide func_184591_cq() {
      return HandSide.RIGHT;
   }
}
