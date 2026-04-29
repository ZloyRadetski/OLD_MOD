package net.portalmod.common.particles;

import javax.annotation.Nullable;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.portalmod.mixins.accessors.FlameParticleAccessor;

public class SmallFlameFactory implements IParticleFactory<BasicParticleType> {
   private final IAnimatedSprite sprite;

   public SmallFlameFactory(IAnimatedSprite sprite) {
      this.sprite = sprite;
   }

   @Nullable
   public Particle createParticle(BasicParticleType basicParticleType, ClientWorld world, double a, double b, double c, double d, double e, double f) {
      FlameParticle flameParticle = FlameParticleAccessor.pmInit(world, a, b, c, d, e, f);
      flameParticle.func_217568_a(this.sprite);
      flameParticle.func_70541_f(0.5F);
      return flameParticle;
   }
}
