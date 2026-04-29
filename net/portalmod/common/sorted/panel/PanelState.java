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
      CHAMBER_LIGHTS_FLICKER = register("block.chamber_lights.fli