package net.portalmod.core.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.sign.ChamberSignEntity;
import net.portalmod.common.sorted.turret.TurretEntity;

public class EntityInit {
   public static final DeferredRegister<EntityType<?>> ENTITIES;
   public static final RegistryObject<EntityType<Cube>> GABE;
   public static final RegistryObject<EntityType<Cube>> COMPANION_CUBE;
   public static final RegistryObject<EntityType<Cube>> STORAGE_CUBE;
   public static final RegistryObject<EntityType<Cube>> VINTAGE_CUBE;
   public static final RegistryObject<EntityType<TurretEntity>> TURRET;
   public static final RegistryObject<EntityType<ChamberSignEntity>> CHAMBER_SIGN;
   public static final RegistryObject<EntityType<PortalEntity>> PORTAL;

   private EntityInit() {
   }

   static {
      ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, "portalmod");
      GABE = ENTITIES.register("gabe", () -> Builder.func_220322_a(Cube::new, EntityClassification.AMBIENT).func_220321_a(0.8F, 0.8F).func_206830_a((new ResourceLocation("portalmod", "gabe")).toString()));
      COMPANION_CUBE = ENTITIES.register("companion_cube", () -> Builder.func_220322_a(Cube::new, EntityClassification.AMBIENT).func_220321_a(0.8F, 0.8F).func_206830_a((new ResourceLocation("portalmod", "companion_cube")).toString()));
      STORAGE_CUBE = ENTITIES.register("storage_cube", () -> Builder.func_220322_a(Cube::new, EntityClassification.AMBIENT).func_220321_a(0.8F, 0.8F).func_206830_a((new ResourceLocation("portalmod", "storage_cube")).toString()));
      VINTAGE_CUBE = ENTITIES.register("vintage_cube", () -> Builder.func_220322_a(Cube::new, EntityClassification.AMBIENT).func_220321_a(0.8F, 0.8F).func_206830_a((new ResourceLocation("portalmod", "vintage_cube")).toString()));
      TURRET = ENTITIES.register("turret", () -> Builder.func_220322_a(TurretEntity::new, EntityClassification.CREATURE).func_206830_a((new ResourceLocation("portalmod", "turret")).toString()));
      CHAMBER_SIGN = ENTITIES.register("chamber_sign", () -> Builder.func_220322_a(ChamberSignEntity::new, EntityClassification.MISC).func_220321_a(0.5F, 0.5F).func_233608_b_(Integer.MAX_VALUE).func_206830_a((new ResourceLocation("portalmod", "chamber_sign")).toString()));
      PORTAL = ENTITIES.register("portal", () -> Builder.func_220322_a(PortalEntity::new, EntityClassification.MISC).func_233606_a_(10).func_233608_b_(Integer.MAX_VALUE).setCustomClientFactory((spawnEntity, level) -> new PortalEntity(level)).func_206830_a((new ResourceLocation("portalmod", "portal")).toString()));
   }
}
