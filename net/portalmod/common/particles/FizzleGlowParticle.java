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

public class FizzleGlowParticle extends SpriteTexturedParticle {
   protected FizzleGlowParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
      super(world, x, y, z, dx, dy, dz);
      this.field_70547_e = (int)(Math.random() * (double)6.0F) + 3;
      this.field_70545_g = 0.0F;
      this.field_187130_j -= 0.1;
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217603_c;
   }

   protected int func_189214_a(float idk) {
      return 15728880;
   }

   public void func_189213_a() {
      super.func_189213_a();
   }

   public static void createGlowParticles(World world, LivingEntity entity) {
      double heightMid = (double)entity.func_213302_cg() * (double)0.5F;
      double widthMid = (double)entity.func_213311_cf() * (double)0.5F;
      Vector3d middle = entity.func_213303_ch().func_72441_c((double)0.0F, heightMid, (double)0.0F);

      for(int i = 0; i < 20; ++i) {
         Vector3d offset = new Vector3d(random(widthMid), random(heightMid), random(widthMid));
         Vector3d particlePos = middle.func_178787_e(offset);
         world.func_195594_a((IParticleData)ParticleInit.FIZZLE_GLOW.get(), particlePos.field_72450_a, particlePos.field_72448_b, particlePos.field_72449_c, offset.field_72450_a * (double)0.5F, offset.field_72448_b * (double)0.5F, offset.field_72449_c * (double)0.5F);
      }

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
         FizzleGlowParticle flameparticle = new FizzleGlowParticle(world, x, y, z, dx, dy, dz);
         flameparticle.func_217568_a(this.sprite);
         return flameparticle;
      }
   }
}
