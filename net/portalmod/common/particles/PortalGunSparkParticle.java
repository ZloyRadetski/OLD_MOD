package net.portalmod.common.particles;

import java.util.Random;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.portalmod.core.init.ParticleInit;

public class PortalGunSparkParticle extends SpriteTexturedParticle {
   public static final Random RANDOM = new Random();
   public int u;
   public int v;

   protected PortalGunSparkParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
      super(world, x, y, z, dx, dy, dz);
      this.field_70547_e = 2;
      this.field_70545_g = 0.0F;
      this.field_187129_i = dx;
      this.field_187130_j = dy;
      this.field_187131_k = dz;
      this.field_70544_f *= 0.65F;
      this.u = RANDOM.nextInt(4);
      this.v = RANDOM.nextInt(4);
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217603_c;
   }

   protected int func_189214_a(float idk) {
      return 15728880;
   }

   public void func_189213_a() {
      if (this.field_70546_d == 1) {
         this.func_82338_g(0.5F);
      }

      super.func_189213_a();
   }

   protected float func_217563_c() {
      return this.field_217569_E.func_94214_a((double)(this.u * 4));
   }

   protected float func_217564_d() {
      return this.field_217569_E.func_94214_a((double)((this.u + 1) * 4));
   }

   protected float func_217562_e() {
      return this.field_217569_E.func_94207_b((double)(this.v * 4));
   }

   protected float func_217560_f() {
      return this.field_217569_E.func_94207_b((double)((this.v + 1) * 4));
   }

   public static void createParticles(World world, PlayerEntity player, boolean offhand) {
      boolean isLefty = player.func_184591_cq() == HandSide.LEFT;
      Vector3d viewVec = player.func_70676_i(1.0F);
      Vector3d rightVec = Vector3d.func_189986_a(0.0F, player.field_70759_as + 90.0F);
      Vector3d downVec = viewVec.func_72431_c(rightVec);
      Vector3d viewLocation = player.func_174824_e(1.0F).func_178787_e(viewVec.func_186678_a(0.8)).func_178787_e(rightVec.func_186678_a(0.45).func_186678_a(offhand == isLefty ? (double)1.0F : (double)-1.0F)).func_178787_e(downVec.func_186678_a(0.3));
      int amount = RANDOM.nextInt(3) + 1;
      double speed = 0.07;
      double spread = 0.2;

      for(int i = 0; i < amount; ++i) {
         Vector3d offset = new Vector3d(symmetricRandom(spread), symmetricRandom(spread), symmetricRandom(spread));
         Vector3d particlePos = viewLocation.func_178787_e(offset);
         world.func_195594_a((IParticleData)ParticleInit.PORTALGUN_SPARK.get(), particlePos.field_72450_a, particlePos.field_72448_b, particlePos.field_72449_c, symmetricRandom(speed), symmetricRandom(speed), symmetricRandom(speed));
      }

   }

   public static double symmetricRandom(double i) {
      return ((double)RANDOM.nextFloat() - (double)0.5F) * (double)2.0F * i;
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50823_1_) {
         this.sprite = p_i50823_1_;
      }

      public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
         PortalGunSparkParticle particle = new PortalGunSparkParticle(world, x, y, z, dx, dy, dz);
         particle.func_217568_a(this.sprite);
         return particle;
      }
   }
}
