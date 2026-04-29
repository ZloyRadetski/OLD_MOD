package net.portalmod.common.sorted.portal;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.portalmod.core.init.ParticleInit;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class PortalPhotonParticle extends SpriteTexturedParticle {
   private final PortalEntity portal;
   private final double x0;
   private final double y0;
   private final double z0;
   private final double x1;
   private final double y1;
   private final double z1;
   private final float speed;
   private final float decay;
   private final float end;
   private final boolean smooth;
   private final boolean useGravity;

   protected PortalPhotonParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd, PortalPhotonParticleData data, IAnimatedSprite sprite) {
      super(level, x, y, z, xd, yd, zd);
      this.portal = data.getPortal();
      this.x0 = x;
      this.y0 = y;
      this.z0 = z;
      this.useGravity = data.usesGravity();
      if (this.useGravity) {
         this.x1 = (double)0.0F;
         this.y1 = (double)0.0F;
         this.z1 = (double)0.0F;
         this.field_187129_i = xd;
         this.field_187130_j = yd;
         this.field_187131_k = zd;
         this.field_70545_g = 0.08F;
      } else {
         this.x1 = xd;
         this.y1 = yd;
         this.z1 = zd;
         this.field_187129_i = (double)0.0F;
         this.field_187130_j = (double)0.0F;
         this.field_187131_k = (double)0.0F;
         this.field_70545_g = 0.0F;
      }

      this.speed = data.getSpeed();
      this.decay = data.getDecay();
      this.end = this.field_187136_p.nextFloat() * 0.1F;
      this.smooth = data.isSmooth();
      this.calculateAlpha();
      this.field_70547_e = 20;
      this.func_217566_b(sprite);
      this.field_70544_f = 0.03125F - (float)this.field_187136_p.nextGaussian() * 0.005F;
      this.field_70552_h = data.getRed();
      this.field_70553_i = data.getGreen();
      this.field_70551_j = data.getBlue();
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e || this.portal != null && !this.portal.func_70089_S()) {
         this.func_187112_i();
      }

      this.calculateAlpha();
      this.calculatePos();
   }

   private void calculateAlpha() {
      this.field_82339_as = MathHelper.func_76131_a((float)Math.pow((double)((float)(this.field_70547_e - this.field_70546_d) / this.decay), (double)2.0F), 0.0F, 0.8F);
      if (this.smooth) {
         this.field_82339_as *= 1.0F - (float)Math.exp((double)((float)(-this.field_70546_d) / 7.0F));
      }

      if (this.portal != null) {
         this.field_82339_as *= MathHelper.func_76131_a((float)this.portal.getAge() / 30.0F * (float)this.portal.getAge() / 30.0F, 0.0F, 1.0F);
      }

   }

   private void calculatePos() {
      if (this.useGravity) {
         this.field_187130_j -= 1.2 * (double)this.field_70545_g;
         this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
         this.field_187129_i *= (double)0.85F;
         this.field_187130_j *= (double)0.9F;
         this.field_187131_k *= (double)0.85F;
         if (this.field_187132_l) {
            this.field_187130_j *= (double)-1.0F;
         }
      } else {
         double f = (double)1.0F - Math.exp((double)((float)(-this.field_70546_d) / this.speed)) - (double)this.end;
         this.func_187109_b(MathHelper.func_219803_d(f, this.x0, this.x1), MathHelper.func_219803_d(f, this.y0, this.y1), MathHelper.func_219803_d(f, this.z0, this.z1));
      }

   }

   public void func_225606_a_(IVertexBuilder vertexBuilder, ActiveRenderInfo camera, float f) {
      Minecraft.func_71410_x().field_71460_t.func_228384_l_().func_205108_b();
      super.func_225606_a_(vertexBuilder, camera, f);
   }

   public static void createLivingParticles(PortalEntity portal) {
      Random random = new Random();

      for(int i = 0; i < 360; i += 20) {
         boolean doesRender = random.nextInt(100) < 30;
         if (doesRender) {
            int deltaAngle = 40 * (portal.getEnd() == PortalEnd.PRIMARY ? 1 : -1);
            float randomAngle = (random.nextFloat() - 0.5F) * 4.0F;
            float startAngle = ((float)i + randomAngle) * (float)Math.PI / 180.0F;
            float endAngle = ((float)(i + deltaAngle) + randomAngle) * (float)Math.PI / 180.0F;
            float randomRadius = (random.nextFloat() - 0.5F) * 0.1F;
            float startRadius = 0.55F + randomRadius;
            float endRadius = 0.45F + randomRadius;
            createRadialParticle(portal, random, startRadius, endRadius, startAngle, endAngle, random.nextFloat() * 2.0F + 15.0F, random.nextFloat() * 18.0F + 15.0F, true, true);
         }
      }

   }

   public static void createOpeningParticles(PortalEntity portal) {
      Random random = new Random();

      for(int i = 0; i < 360; i += 10) {
         for(int j = 0; j < 3; ++j) {
            float randomAngle = (random.nextFloat() - 0.5F) * 4.0F;
            float angle = ((float)i + randomAngle) * (float)Math.PI / 180.0F;
            float innerRadius = 0.2F;
            float outerRadius = 0.45F;
            float randomRadius = (random.nextFloat() - 0.5F) * 0.1F;
            float startRadius = 0.0F;
            float endRadius = (outerRadius - innerRadius) * ((float)j / 2.0F) + innerRadius + randomRadius;
            int decayBase = 0;
            switch (j) {
               case 0:
                  decayBase = 22;
                  break;
               case 1:
                  decayBase = 10;
                  break;
               case 2:
                  decayBase = 5;
            }

            createRadialParticle(portal, random, startRadius, endRadius, angle, angle, random.nextFloat() + 2.0F, random.nextFloat() * 18.0F + (float)decayBase, false, false);
         }
      }

   }

   public static void createClosingParticles(PortalEntity portal) {
      Random random = new Random();

      for(int i = 0; i < 360; i += 20) {
         float randomAngle = (random.nextFloat() - 0.5F) * 4.0F;
         float angle = ((float)i + randomAngle) * (float)Math.PI / 180.0F;
         float randomRadius = (random.nextFloat() - 0.5F) * 0.1F;
         float startRadius = 0.45F + randomRadius;
         float endRadius = 0.1F + randomRadius;
         createRadialParticle(portal, random, startRadius, endRadius, angle, angle, random.nextFloat() * 2.0F + 4.0F, random.nextFloat() * 18.0F + 30.0F, false, false);
      }

   }

   public static void createFailParticles(World level, Vec3 position, Vec3 normal, Vec3 upVector, String hue) {
      Random random = new Random();

      for(int i = 0; i < 360; i += 10) {
         float randomAngle = (random.nextFloat() - 0.5F) * 4.0F;
         float angle = ((float)i + randomAngle) * (float)Math.PI / 180.0F;
         Color color = PortalColors.getColor(hue);
         Mat4 modelMatrix = Mat4.identity().mul((new OrthonormalBasis(upVector.clone().cross(normal), upVector)).getChangeOfBasisFromCanonicalMatrix());
         if (color == null) {
            return;
         }

         float x0 = 0.0F;
         float y0 = 0.0F;
         float x1 = (float)Math.cos((double)angle);
         float y1 = (float)Math.sin((double)angle);
         Vec3 pos0 = (new Vec3((double)x0, (double)y0, (double)0.0F)).transform(modelMatrix).add(position);
         Vec3 pos1 = (new Vec3((double)x1, (double)y1, 0.7)).transform(modelMatrix);
         float randomRadius = random.nextFloat() * 0.3F;
         pos1.x *= 0.1 + (double)randomRadius;
         pos1.y *= 0.4 + (double)randomRadius;
         pos1.z *= 0.1 + (double)randomRadius;
         createParticle(level, (PortalEntity)null, random, pos0, pos1, color, random.nextFloat() * 2.0F + 4.0F, random.nextFloat() * 18.0F + 15.0F, false, true);
      }

   }

   private static void createRadialParticle(PortalEntity portal, Random random, float startRadius, float endRadius, float startAngle, float endAngle, float speed, float decay, boolean smooth, boolean linked) {
      Mat4 modelMatrix = portal.getSourceBasis().getChangeOfBasisFromCanonicalMatrix();
      Color color = PortalColors.getColor(portal);
      if (portal.field_70170_p != null && color != null) {
         float x0 = (float)Math.cos((double)startAngle);
         float y0 = (float)Math.sin((double)startAngle);
         float x1;
         float y1;
         if (startAngle == endAngle) {
            x1 = x0;
            y1 = y0;
         } else {
            x1 = (float)Math.cos((double)endAngle);
            y1 = (float)Math.sin((double)endAngle);
         }

         x0 *= startRadius;
         y0 *= startRadius * 2.0F;
         x1 *= endRadius;
         y1 *= endRadius * 2.0F;
         boolean outwards = endRadius > startRadius;
         Vec3 pos0 = (new Vec3((double)x0, (double)y0, outwards ? (double)0.0F : 0.1)).transform(modelMatrix).add(portal.func_213303_ch());
         Vec3 pos1 = (new Vec3((double)x1, (double)y1, outwards ? 0.1 : (double)0.0F)).transform(modelMatrix).add(portal.func_213303_ch());
         createParticle(portal.field_70170_p, linked ? portal : null, random, pos0, pos1, color, speed, decay, smooth, false);
      }
   }

   private static void createParticle(World level, PortalEntity portal, Random random, Vec3 pos0, Vec3 pos1, Color color, float speed, float decay, boolean smooth, boolean useGravity) {
      float randomR = random.nextFloat() * 0.1F;
      float randomG = random.nextFloat() * 0.1F;
      float randomB = random.nextFloat() * 0.1F;
      level.func_195594_a(new PortalPhotonParticleData(portal, MathHelper.func_76131_a((float)color.getRed() / 255.0F + randomR, 0.0F, 1.0F), MathHelper.func_76131_a((float)color.getGreen() / 255.0F + randomG, 0.0F, 1.0F), MathHelper.func_76131_a((float)color.getBlue() / 255.0F + randomB, 0.0F, 1.0F), speed, decay, smooth, useGravity), pos0.x, pos0.y, pos0.z, pos1.x, pos1.y, pos1.z);
   }

   protected float func_217563_c() {
      return this.field_217569_E.func_94214_a((double)0.0F);
   }

   protected float func_217562_e() {
      return this.field_217569_E.func_94207_b((double)0.0F);
   }

   protected float func_217564_d() {
      return this.field_217569_E.func_94214_a((double)2.0F);
   }

   protected float func_217560_f() {
      return this.field_217569_E.func_94207_b((double)2.0F);
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217603_c;
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(BasicParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd) {
         PortalPhotonParticle particle = new PortalPhotonParticle(level, x, y, z, xd, yd, zd, (PortalPhotonParticleData)type, this.sprite);
         particle.func_217568_a(this.sprite);
         return particle;
      }
   }

   public static class PortalPhotonParticleData extends BasicParticleType {
      private final PortalEntity portal;
      private final float r;
      private final float g;
      private final float b;
      private final float speed;
      private final float decay;
      private final boolean smooth;
      private final boolean useGravity;

      public PortalPhotonParticleData(PortalEntity portal, float r, float g, float b, float speed, float decay, boolean smooth, boolean useGravity) {
         super(false);
         this.portal = portal;
         this.r = r;
         this.g = g;
         this.b = b;
         this.speed = speed;
         this.decay = decay;
         this.smooth = smooth;
         this.useGravity = useGravity;
      }

      public BasicParticleType func_197554_b() {
         return (BasicParticleType)ParticleInit.PORTAL_PHOTON.get();
      }

      public PortalEntity getPortal() {
         return this.portal;
      }

      public float getRed() {
         return this.r;
      }

      public float getGreen() {
         return this.g;
      }

      public float getBlue() {
         return this.b;
      }

      public float getSpeed() {
         return this.speed;
      }

      public float getDecay() {
         return this.decay;
      }

      public boolean isSmooth() {
         return this.smooth;
      }

      public boolean usesGravity() {
         return this.useGravity;
      }
   }
}
