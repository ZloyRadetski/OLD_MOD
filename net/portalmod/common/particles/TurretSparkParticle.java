package net.portalmod.common.particles;

import java.util.Random;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.core.init.ParticleInit;

public class TurretSparkParticle extends SpriteTexturedParticle {
   public int u;
   public int v;

   protected TurretSparkParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
      super(world, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F);
      this.field_70547_e = 3;
      this.field_70545_g = 0.0F;
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217603_c;
   }

   protected int func_189214_a(float idk) {
      return 15728880;
   }

   public void func_189213_a() {
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.u = this.field_70546_d % 2;
      this.v = (int)Math.floor((double)this.field_70546_d / (double)2.0F);
   }

   protected float func_217563_c() {
      return this.field_217569_E.func_94214_a((double)(this.u * 8));
   }

   protected float func_217564_d() {
      return this.field_217569_E.func_94214_a((double)((this.u + 1) * 8));
   }

   protected float func_217562_e() {
      return this.field_217569_E.func_94207_b((double)(this.v * 8));
   }

   protected float func_217560_f() {
      return this.field_217569_E.func_94207_b((double)((this.v + 1) * 8));
   }

   public static void createGlowParticles(World world, LivingEntity entity, Vector3d direction) {
      double heightMid = (double)entity.func_213302_cg() * (double)0.5F;
      Vector3d middle = entity.func_213303_ch().func_72441_c((double)0.0F, heightMid, (double)0.0F);
      Vector3d front = middle.func_178787_e(direction.func_72432_b().func_216372_d((double)0.25F, (double)0.0F, (double)0.25F));
      Vector3d side = new Vector3d(-direction.func_72432_b().field_72449_c, (double)0.0F, direction.func_72432_b().field_72450_a);
      long tickTime = world.func_82737_E() + (long)entity.func_145782_y();
      boolean leftSide = tickTime % 8L < 4L;
      boolean upperSide = tickTime % 4L < 2L;
      float verticalOffset = upperSide ? 0.12F : 0.0F;
      side = side.func_216372_d(leftSide ? (double)-0.25F : (double)0.25F, (double)0.0F, leftSide ? (double)-0.25F : (double)0.25F);
      Vector3d particlePos = front.func_72441_c((double)0.0F, (double)0.08F, (double)0.0F);
      world.func_195594_a((IParticleData)ParticleInit.TURRET_SPARK.get(), particlePos.field_72450_a + side.field_72450_a, particlePos.field_72448_b + (double)verticalOffset, particlePos.field_72449_c + side.field_72449_c, (double)0.0F, (double)0.0F, (double)0.0F);
   }

   public static double random(double i) {
      return ((double)(new Random()).nextFloat() - (double)0.5F) * (double)2.0F * i;
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50823_1_) {
         this.sprite = p_i50823_1_;
      }

      public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
         TurretSparkParticle particle = new TurretSparkParticle(world, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F);
         particle.func_217566_b(this.sprite);
         return particle;
      }
   }
}
