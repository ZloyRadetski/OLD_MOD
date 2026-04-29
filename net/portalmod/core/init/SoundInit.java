package net.portalmod.core.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundInit {
   public static final DeferredRegister<SoundEvent> SOUNDS;
   public static final RegistryObject<SoundEvent> RADIO_LOOP;
   public static final RegistryObject<SoundEvent> RADIO_DINOSAUR1;
   public static final RegistryObject<SoundEvent> GEL_BREAK;
   public static final RegistryObject<SoundEvent> GEL_COLLECT;
   public static final RegistryObject<SoundEvent> GEL_PLACE;
   public static final RegistryObject<SoundEvent> GEL_STEP;
   public static final RegistryObject<SoundEvent> REPULSION_GEL_BOUNCE;
   public static final RegistryObject<SoundEvent> CHAMBER_DOOR_OPEN;
   public static final RegistryObject<SoundEvent> CHAMBER_DOOR_CLOSE;
   public static final RegistryObject<SoundEvent> CHAMBER_LIGHTS_AMBIENT;
   public static final RegistryObject<SoundEvent> CHAMBER_LIGHTS_FLICKER;
   public static final RegistryObject<SoundEvent> PUSH_DOOR_OPEN;
   public static final RegistryObject<SoundEvent> PUSH_DOOR_CLOSE;
   public static final RegistryObject<SoundEvent> CUBE_DROPPER_OPEN;
   public static final RegistryObject<SoundEvent> CUBE_DROPPER_CLOSE;
   public static final RegistryObject<SoundEvent> BUTTON_ACTIVATE;
   public static final RegistryObject<SoundEvent> BUTTON_DEACTIVATE;
   public static final RegistryObject<SoundEvent> SUPER_BUTTON_PRESS;
   public static final RegistryObject<SoundEvent> SUPER_BUTTON_RELEASE;
   public static final RegistryObject<SoundEvent> FAITHPLATE_LAUNCH;
   public static final RegistryObject<SoundEvent> FIZZLER_ACTIVATE;
   public static final RegistryObject<SoundEvent> FIZZLER_DEACTIVATE;
   public static final RegistryObject<SoundEvent> ANTLINE_INDICATOR_ACTIVATE;
   public static final RegistryObject<SoundEvent> ANTLINE_INDICATOR_DEACTIVATE;
   public static final RegistryObject<SoundEvent> ANTLINE_TIMER_TICK;
   public static final RegistryObject<SoundEvent> CAKE_EAT_CANDLE;
   public static final RegistryObject<SoundEvent> PORTALGUN_FIRE_PRIMARY;
   public static final RegistryObject<SoundEvent> PORTALGUN_FIRE_SECONDARY;
   public static final RegistryObject<SoundEvent> PORTALGUN_MISS;
   public static final RegistryObject<SoundEvent> PORTALGUN_FIZZLE;
   public static final RegistryObject<SoundEvent> PORTALGUN_LIFT;
   public static final RegistryObject<SoundEvent> PORTALGUN_HOLD;
   public static final RegistryObject<SoundEvent> PORTALGUN_DROP;
   public static final RegistryObject<SoundEvent> WRENCH_USE;
   public static final RegistryObject<SoundEvent> WRENCH_FAIL;
   public static final RegistryObject<SoundEvent> DISC_RAIN;
   public static final RegistryObject<SoundEvent> PORTAL_OPEN;
   public static final RegistryObject<SoundEvent> PORTAL_CLOSE;
   public static final RegistryObject<SoundEvent> PORTAL_TELEPORT;
   public static final RegistryObject<SoundEvent> GOO_DAMAGE;
   public static final RegistryObject<SoundEvent> ENTITY_FIZZLE;
   public static final RegistryObject<SoundEvent> CUBE_HIT;
   public static final RegistryObject<SoundEvent> CUBE_GABE;
   public static final RegistryObject<SoundEvent> TURRET_OPEN;
   public static final RegistryObject<SoundEvent> TURRET_CLOSE;
   public static final RegistryObject<SoundEvent> TURRET_FIRE;
   public static final RegistryObject<SoundEvent> TURRET_FIRE_FAIL;
   public static final RegistryObject<SoundEvent> TURRET_STOCK;
   public static final RegistryObject<SoundEvent> CHAMBER_SIGN_PLACE;

   private SoundInit() {
   }

   private static RegistryObject<SoundEvent> register(String id) {
      return SOUNDS.register(id, () -> new SoundEvent(new ResourceLocation("portalmod", id)));
   }

   static {
      SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "portalmod");
      RADIO_LOOP = register("block.radio.loop");
      RADIO_DINOSAUR1 = register("block.radio.transmission");
      GEL_BREAK = register("block.gel.break");
      GEL_COLLECT = register("block.gel.collect");
      GEL_PLACE = register("block.gel.place");
      GEL_STEP = register("block.gel.step");
      REPULSION_GEL_BOUNCE = register("block.gel.bounce");
      CHAMBER_DOOR_OPEN = register("block.chamber_door.open");
      CHAMBER_DOOR_CLOSE = register("block.chamber_door.close");
      CHAMBER_LIGHTS_AMBIENT = register("block.chamber_lights.hum");
      CHAMBER_LIGHTS_FLICKER = register("block.chamber_lights.flicker");
      PUSH_DOOR_OPEN = register("block.push_door.open");
      PUSH_DOOR_CLOSE = register("block.push_door.close");
      CUBE_DROPPER_OPEN = register("block.cube_dropper.open");
      CUBE_DROPPER_CLOSE = register("block.cube_dropper.close");
      BUTTON_ACTIVATE = register("block.button.activate");
      BUTTON_DEACTIVATE = register("block.button.deactivate");
      SUPER_BUTTON_PRESS = register("block.super_button.press");
      SUPER_BUTTON_RELEASE = register("block.super_button.release");
      FAITHPLATE_LAUNCH = register("block.faithplate.launch");
      FIZZLER_ACTIVATE = register("block.fizzler_emitter.activate");
      FIZZLER_DEACTIVATE = register("block.fizzler_emitter.deactivate");
      ANTLINE_INDICATOR_ACTIVATE = register("block.antline_indicator.activate");
      ANTLINE_INDICATOR_DEACTIVATE = register("block.antline_indicator.deactivate");
      ANTLINE_TIMER_TICK = register("block.antline_timer.tick");
      CAKE_EAT_CANDLE = register("block.forest_cake.eat_candle");
      PORTALGUN_FIRE_PRIMARY = register("item.portalgun.fire_primary");
      PORTALGUN_FIRE_SECONDARY = register("item.portalgun.fire_secondary");
      PORTALGUN_MISS = register("item.portalgun.miss");
      PORTALGUN_FIZZLE = register("item.portalgun.fizzle");
      PORTALGUN_LIFT = register("item.portalgun.lift");
      PORTALGUN_HOLD = register("item.portalgun.hold");
      PORTALGUN_DROP = register("item.portalgun.drop");
      WRENCH_USE = register("item.wrench.use");
      WRENCH_FAIL = register("item.wrench.fail");
      DISC_RAIN = register("disc.rain");
      PORTAL_OPEN = register("entity.portal.open");
      PORTAL_CLOSE = register("entity.portal.close");
      PORTAL_TELEPORT = register("entity.portal.teleport");
      GOO_DAMAGE = register("entity.player.hurt_goo");
      ENTITY_FIZZLE = register("entity.fizzle");
      CUBE_HIT = register("entity.cube.hit");
      CUBE_GABE = register("entity.cube.gabe");
      TURRET_OPEN = register("entity.turret.open");
      TURRET_CLOSE = register("entity.turret.close");
      TURRET_FIRE = register("entity.turret.fire");
      TURRET_FIRE_FAIL = register("entity.turret.fire_fail");
      TURRET_STOCK = register("entity.turret.stock");
      CHAMBER_SIGN_PLACE = register("entity.chamber_sign.place");
   }
}
