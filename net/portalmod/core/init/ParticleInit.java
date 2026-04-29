package net.portalmod.core.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.portalmod.common.particles.FizzleFlakeParticle;
import net.portalmod.common.particles.FizzleGlowParticle;
import net.portalmod.common.particles.PortalGunSparkParticle;
import net.portalmod.common.particles.SmallFlameFactory;
import net.portalmod.common.particles.TurretSparkParticle;
import net.portalmod.common.sorted.portal.PortalParticle;
import net.portalmod.common.sorted.portal.PortalPhotonParticle;

@EventBusSubscriber(
   modid = "portalmod",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ParticleInit {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;
   public static final RegistryObject<BasicParticleType> PORTAL_PARTICLE;
   public static final RegistryObject<BasicParticleType> PORTAL_PHOTON;
   public static final RegistryObject<BasicParticleType> FIZZLE_GLOW;
   public static final RegistryObject<BasicParticleType> FIZZLE_FLAKE_FALLING;
   public static final RegistryObject<BasicParticleType> FIZZLE_FLAKE_LANDING;
   public static final RegistryObject<BasicParticleType> PORTALGUN_SPARK;
   public static final RegistryObject<BasicParticleType> TURRET_SPARK;
   public static final RegistryObject<BasicParticleType> SMALL_FLAME;

   private ParticleInit() {
   }

   @SubscribeEvent
   public static void registerParticleFactory(ParticleFactoryRegisterEvent event) {
      ParticleManager particleEngine = Minecraft.func_71410_x().field_71452_i;
      particleEngine.func_215234_a((ParticleType)PORTAL_PARTICLE.get(), PortalParticle.Factory::new);
      particleEngine.func_215234_a((ParticleType)PORTAL_PHOTON.get(), PortalPhotonParticle.Factory::new);
      particleEngine.func_215234_a((ParticleType)FIZZLE_GLOW.get(), FizzleGlowParticle.Factory::new);
      particleEngine.func_215234_a((ParticleType)FIZZLE_FLAKE_FALLING.get(), FizzleFlakeParticle.FallingFactory::new);
      particleEngine.func_215234_a((ParticleType)FIZZLE_FLAKE_LANDING.get(), FizzleFlakeParticle.LandingFactory::new);
      particleEngine.func_215234_a((ParticleType)PORTALGUN_SPARK.get(), PortalGunSparkParticle.Factory::new);
      particleEngine.func_215234_a((ParticleType)TURRET_SPARK.get(), TurretSparkParticle.Factory::new);
      particleEngine.func_215234_a((ParticleType)SMALL_FLAME.get(), SmallFlameFactory::new);
   }

   static {
      PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "portalmod");
      PORTAL_PARTICLE = PARTICLE_TYPES.register("portal_particle", () -> new BasicParticleType(false));
      PORTAL_PHOTON = PARTICLE_TYPES.register("portal_photon", () -> new BasicParticleType(false));
      FIZZLE_GLOW = PARTICLE_TYPES.register("fizzle_glow", () -> new BasicParticleType(false));
      FIZZLE_FLAKE_FALLING = PARTICLE_TYPES.register("fizzle_flake_falling", () -> new BasicParticleType(false));
      FIZZLE_FLAKE_LANDING = PARTICLE_TYPES.register("fizzle_flake_landing", () -> new BasicParticleType(false));
      PORTALGUN_SPARK = PARTICLE_TYPES.register("portalgun_spark", () -> new BasicParticleType(false));
      TURRET_SPARK = PARTICLE_TYPES.register("turret_spark", () -> new BasicParticleType(false));
      SMALL_FLAME = PARTICLE_TYPES.register("small_flame", () -> new BasicParticleType(false));
   }
}
