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

public class FizzleFlakeParticle extends SpriteTexturedParticle {
   public FizzleFlakeParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
      super(world, x, y, z, dx, dy, dz);
      this.func_187115_a(0.01F, 0.01F);
      this.func_70538_b(0.1F, 0.1F, 0.1F);
      this.field_70545_g = 0.06F;
      this.field_187130_j -= 0.1;
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217602_b;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.preMoveUpdate();
      if (!this.field_187133_m) {
         this.field_187130_j -= (double)this.field_70545_g;
         this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
         this.postMoveUpdate();
         if (!this.field_187133_m) {
            this.field_187129_i *= (double)0.98F;
            this.field_187130_j *= (double)0.98F;
            this.field_187131_k *= (double)0.98F;
         }
      }

   }

   public void preMoveUpdate() {
      if (this.field_70547_e-- <= 0) {
         this.func_187112_i();
      }

   }

   public void postMoveUpdate() {
   }

   public static void createFlakeParticles(World world, LivingEntity entity) {
      double heightMid = (double)entity.func_213302_cg() * (double)0.5F;
      double widthMid = (double)entity.func_213311_cf() * (double)0.5F;
      Vector3d offset = new Vector3d(random(widthMid), random(heightMid), random(widthMid));
      Vector3d particlePos = entity.func_213303_ch().func_178787_e(offset);
      world.func_195594_a((IParticleData)ParticleInit.FIZZLE_FLAKE_FALLING.get(), particlePos.field_72450_a, particlePos.field_72448_b, particlePos.field_72449_c, (double)0.0F, (double)0.0F, (double)0.0F);
   }

   public static double random(double i) {
      return ((double)(new Random()).nextFloat() - (double)0.5F) * (double)2.0F * i;
   }

   public static class FallingParticle extends FizzleFlakeParticle {
      public final IParticleData landParticle;

      public FallingParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz, IParticleData landParticle) {
         super(world, x, y, z, dx, dy, dz);
         this.landParticle = landParticle;
      }

      public void postMoveUpdate() {
         if (this.field_187132_l) {
            this.func_187112_i();
            this.field_187122_b.func_195594_a(this.landParticle, this.field_187126_f, this.field_187127_g, this.field_187128_h, (double)0.0F, (double)0.0F, (double)0.0F);
         }

      }
   }

   public static class FallingFactory implements IParticleFactory<BasicParticleType> {
      public final IAnimatedSprite sprite;

      public FallingFactory(IAnimatedSprite sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
         FizzleFlakeParticle particle = new FallingParticle(world, x, y, z, dx, dy, dz, (IParticleData)ParticleInit.FIZZLE_FLAKE_LANDING.get());
         particle.func_217568_a(this.sprite);
         return particle;
      }
   }

   public static class LandingParticle extends FizzleFlakeParticle {
      public LandingParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
         super(world, x, y, z, dx, dy, dz);
         this.field_70547_e = (int)((double)16.0F / (Math.random() * 0.8 + 0.2));
      }
   }

   public static class LandingFactory implements IParticleFactory<BasicParticleType> {
      public final IAnimatedSprite sprite;

      public LandingFactory(IAnimatedSprite sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
         FizzleFlakeParticle particle = new LandingParticle(world, x, y, z, dx, dy, dz);
         particle.func_217568_a(this.sprite);
         return particle;
      }
   }
}
