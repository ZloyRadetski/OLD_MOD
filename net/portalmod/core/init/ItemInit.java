package net.portalmod.core.init;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.portalmod.common.blocks.ISTERWrapper;
import net.portalmod.common.items.BulletsItem;
import net.portalmod.common.items.ModSpawnEggItem;
import net.portalmod.common.items.PanelBlockItem;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.antline.AntlineBlockItem;
import net.portalmod.common.sorted.gel.container.EmptyGelContainer;
import net.portalmod.common.sorted.gel.container.GelContainer;
import net.portalmod.common.sorted.goo.GooBucketItem;
import net.portalmod.common.sorted.longfallboots.LongFallBoots;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.common.sorted.portalgun.PortalGunISTER;
import net.portalmod.common.sorted.sign.ChamberSignItem;
import net.portalmod.core.PortalModTab;

public class ItemInit {
   public static final DeferredRegister<Item> ITEMS;
   private static final Item.Properties GEL_BASE;
   public static final RegistryObject<Item> PORTALGUN;
   public static final RegistryObject<Item> LONGFALL_BOOTS;
   public static final RegistryObject<Item> WRENCH;
   public static final RegistryObject<Item> COMPANION_CUBE;
   public static final RegistryObject<Item> STORAGE_CUBE;
   public static final RegistryObject<Item> VINTAGE_CUBE;
   public static final RegistryObject<Item> GABE;
   public static final RegistryObject<Item> CUBE_DROPPER;
   public static final RegistryObject<Item> SUPER_BUTTON;
   public static final RegistryObject<Item> STANDING_BUTTON;
   public static final RegistryObject<Item> ANTLINE;
   public static final RegistryObject<Item> ANTLINE_INDICATOR;
   public static final RegistryObject<Item> ANTLINE_TIMER;
   public static final RegistryObject<Item> ANTLINE_DECODER;
   public static final RegistryObject<Item> ANTLINE_ENCODER;
   public static final RegistryObject<Item> TRIGGER;
   public static final RegistryObject<Item> AUTOPORTAL;
   public static final RegistryObject<Item> CHAMBER_DOOR;
   public static final RegistryObject<Item> PUSH_DOOR;
   public static final RegistryObject<Item> FIZZLER_EMITTER;
   public static final RegistryObject<Item> FAITHPLATE;
   public static final RegistryObject<Item> CHAMBER_SIGN;
   public static final RegistryObject<Item> CHAMBER_LIGHTS;
   public static final RegistryObject<Item> GOO_BUCKET;
   public static final RegistryObject<Item> CONTAINER;
   public static final RegistryObject<Item> REPULSION_GEL;
   public static final RegistryObject<Item> PROPULSION_GEL;
   public static final RegistryObject<Item> TURRET;
   public static final RegistryObject<Item> BULLETS;
   public static final RegistryObject<Item> RADIO;
   public static final RegistryObject<Item> DISC_RAIN;
   public static final RegistryObject<Item> FOREST_CAKE;
   public static final RegistryObject<Item> PLATFORM_BEAM;
   public static final RegistryObject<Item> RUSTY_PLATFORM_BEAM;
   public static final RegistryObject<Item> LUNECAST;
   public static final RegistryObject<Item> LUNECAST_STAIRS;
   public static final RegistryObject<Item> LUNECAST_SLAB;
   public static final RegistryObject<Item> LUNECAST_PLATFORM;
   public static final RegistryObject<Item> BLACKPLATE;
   public static final RegistryObject<Item> BLACKPLATE_STAIRS;
   public static final RegistryObject<Item> BLACKPLATE_SLAB;
   public static final RegistryObject<Item> BLACKPLATE_PLATFORM;
   public static final RegistryObject<Item> ARBORED_LUNECAST;
   public static final RegistryObject<Item> ARBORED_LUNECAST_STAIRS;
   public static final RegistryObject<Item> ARBORED_LUNECAST_SLAB;
   public static final RegistryObject<Item> ARBORED_LUNECAST_PLATFORM;
   public static final RegistryObject<Item> ARBORED_BLACKPLATE;
   public static final RegistryObject<Item> ARBORED_BLACKPLATE_STAIRS;
   public static final RegistryObject<Item> ARBORED_BLACKPLATE_SLAB;
   public static final RegistryObject<Item> ARBORED_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Item> ERODED_LUNECAST;
   public static final RegistryObject<Item> ERODED_LUNECAST_STAIRS;
   public static final RegistryObject<Item> ERODED_LUNECAST_SLAB;
   public static final RegistryObject<Item> ERODED_LUNECAST_PLATFORM;
   public static final RegistryObject<Item> ERODED_BLACKPLATE;
   public static final RegistryObject<Item> ERODED_BLACKPLATE_STAIRS;
   public static final RegistryObject<Item> ERODED_BLACKPLATE_SLAB;
   public static final RegistryObject<Item> ERODED_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Item> FRACTURED_LUNECAST;
   public static final RegistryObject<Item> FRACTURED_LUNECAST_STAIRS;
   public static final RegistryObject<Item> FRACTURED_LUNECAST_SLAB;
   public static final RegistryObject<Item> FRACTURED_LUNECAST_PLATFORM;
   public static final RegistryObject<Item> FRACTURED_BLACKPLATE;
   public static final RegistryObject<Item> FRACTURED_BLACKPLATE_STAIRS;
   public static final RegistryObject<Item> FRACTURED_BLACKPLATE_SLAB;
   public static final RegistryObject<Item> FRACTURED_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Item> VINTAGE_LUNECAST;
   public static final RegistryObject<Item> VINTAGE_LUNECAST_STAIRS;
   public static final RegistryObject<Item> VINTAGE_LUNECAST_SLAB;
   public static final RegistryObject<Item> VINTAGE_LUNECAST_PLATFORM;
   public static final RegistryObject<Item> VINTAGE_BLACKPLATE;
   public static final RegistryObject<Item> VINTAGE_BLACKPLATE_STAIRS;
   public static final RegistryObject<Item> VINTAGE_BLACKPLATE_SLAB;
   public static final RegistryObject<Item> VINTAGE_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Item> WIRE_MESH_BLOCK;
   public static final RegistryObject<Item> WIRE_MESH;
   public static final RegistryObject<Item> IRON_FRAME;
   public static final RegistryObject<Item> BARRED_IRON_FRAME;
   public static final RegistryObject<Item> MESHED_IRON_FRAME;
   public static final RegistryObject<Item> RUSTY_IRON_FRAME;
   public static final RegistryObject<Item> RUSTY_BARRED_IRON_FRAME;
   public static final RegistryObject<Item> RUSTY_MESHED_IRON_FRAME;

   private ItemInit() {
   }

   public static RegistryObject<Item> registerPanelBlockItem(String name, RegistryObject<Block> block) {
      return ITEMS.register(name, () -> new PanelBlockItem((Block)block.get(), properties()));
   }

   public static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block) {
      return ITEMS.register(name, () -> new BlockItem((Block)block.get(), properties()));
   }

   public static RegistryObject<Item> registerSpawnEgg(String name, RegistryObject<? extends EntityType<?>> entity, String tooltip) {
      return ITEMS.register(name, () -> new ModSpawnEggItem(entity, properties().func_200917_a(1), tooltip));
   }

   public static RegistryObject<Item> registerSpawnEgg(String name, RegistryObject<? extends EntityType<?>> entity) {
      return registerSpawnEgg(name, entity, (String)null);
   }

   public static void registerFluidDispenserBehavior() {
      IDispenseItemBehavior bucketDispense = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack func_82487_b(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            BucketItem bucketitem = (BucketItem)p_82487_2_.func_77973_b();
            BlockPos blockpos = p_82487_1_.func_180699_d().func_177972_a((Direction)p_82487_1_.func_189992_e().func_177229_b(DispenserBlock.field_176441_a));
            World world = p_82487_1_.func_197524_h();
            if (bucketitem.func_180616_a((PlayerEntity)null, world, blockpos, (BlockRayTraceResult)null)) {
               bucketitem.func_203792_a(world, p_82487_2_, blockpos);
               return new ItemStack(Items.field_151133_ar);
            } else {
               return this.defaultDispenseItemBehavior.dispense(p_82487_1_, p_82487_2_);
            }
         }
      };
      DispenserBlock.func_199774_a((IItemProvider)GOO_BUCKET.get(), bucketDispense);
   }

   public static Item.Properties properties() {
      return (new Item.Properties()).func_200916_a(PortalModTab.INSTANCE);
   }

   static {
      ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "portalmod");
      GEL_BASE = properties().func_200917_a(1);
      PORTALGUN = ITEMS.register("portalgun", () -> new PortalGun(properties().func_200917_a(1).func_234689_a_().setISTER(() -> PortalGunISTER::new)));
      LONGFALL_BOOTS = ITEMS.register("longfall_boots", () -> new LongFallBoots(ArmorMaterialInit.LONGFALL_BOOTS, EquipmentSlotType.FEET, properties().func_234689_a_()));
      WRENCH = ITEMS.register("wrench", () -> new WrenchItem(properties().func_200917_a(1)));
      COMPANION_CUBE = registerSpawnEgg("companion_cube", EntityInit.COMPANION_CUBE, "cube");
      STORAGE_CUBE = registerSpawnEgg("storage_cube", EntityInit.STORAGE_CUBE, "cube");
      VINTAGE_CUBE = registerSpawnEgg("vintage_cube", EntityInit.VINTAGE_CUBE, "cube");
      GABE = ITEMS.register("gabe", () -> new ModSpawnEggItem(EntityInit.GABE, (new Item.Properties()).func_200917_a(1), "cube"));
      CUBE_DROPPER = registerBlockItem("cube_dropper", BlockInit.CUBE_DROPPER);
      SUPER_BUTTON = registerBlockItem("super_button", BlockInit.SUPER_BUTTON);
      STANDING_BUTTON = registerBlockItem("standing_button", BlockInit.STANDING_BUTTON);
      ANTLINE = ITEMS.register("antline", () -> new AntlineBlockItem((Block)BlockInit.ANTLINE.get(), properties()));
      ANTLINE_INDICATOR = registerBlockItem("antline_indicator", BlockInit.ANTLINE_INDICATOR);
      ANTLINE_TIMER = registerBlockItem("antline_timer", BlockInit.ANTLINE_TIMER);
      ANTLINE_DECODER = registerBlockItem("antline_decoder", BlockInit.ANTLINE_DECODER);
      ANTLINE_ENCODER = registerBlockItem("antline_encoder", BlockInit.ANTLINE_ENCODER);
      TRIGGER = ITEMS.register("trigger", () -> new BlockItem((Block)BlockInit.TRIGGER.get(), properties()));
      AUTOPORTAL = ITEMS.register("autoportal", () -> new BlockItem((Block)BlockInit.AUTOPORTAL.get(), properties()));
      CHAMBER_DOOR = registerBlockItem("chamber_door", BlockInit.CHAMBER_DOOR);
      PUSH_DOOR = registerBlockItem("push_door", BlockInit.PUSH_DOOR);
      FIZZLER_EMITTER = registerBlockItem("fizzler_emitter", BlockInit.FIZZLER_EMITTER);
      FAITHPLATE = registerBlockItem("faithplate", BlockInit.FAITHPLATE);
      CHAMBER_SIGN = ITEMS.register("chamber_sign", () -> new ChamberSignItem(properties()));
      CHAMBER_LIGHTS = ITEMS.register("chamber_lights", () -> new BlockItem((Block)BlockInit.CHAMBER_LIGHTS.get(), properties().setISTER(() -> ISTERWrapper::new)));
      GOO_BUCKET = ITEMS.register("goo_bucket", () -> new GooBucketItem(FluidInit.GOO_FLUID, properties().func_200917_a(1)));
      CONTAINER = ITEMS.register("container", () -> new EmptyGelContainer(properties().func_200917_a(16)));
      REPULSION_GEL = ITEMS.register("repulsion_gel", () -> new GelContainer((Block)BlockInit.REPULSION_GEL.get(), GEL_BASE, 3634929));
      PROPULSION_GEL = ITEMS.register("propulsion_gel", () -> new GelContainer((Block)BlockInit.PROPULSION_GEL.get(), GEL_BASE, 14910282));
      TURRET = registerSpawnEgg("turret", EntityInit.TURRET, "turret");
      BULLETS = ITEMS.register("bullets", () -> new BulletsItem(properties()));
      RADIO = registerBlockItem("radio", BlockInit.RADIO);
      DISC_RAIN = ITEMS.register("music_disc_rain", () -> new MusicDiscItem(1, SoundInit.DISC_RAIN, properties().func_200917_a(1).func_208103_a(Rarity.RARE)));
      FOREST_CAKE = ITEMS.register("forest_cake", () -> new BlockItem((Block)BlockInit.FOREST_CAKE.get(), properties().func_200917_a(1)));
      PLATFORM_BEAM = ITEMS.register("platform_beam", () -> new BlockItem((Block)BlockInit.PLATFORM_BEAM.get(), properties()));
      RUSTY_PLATFORM_BEAM = ITEMS.register("rusty_platform_beam", () -> new BlockItem((Block)BlockInit.RUSTY_PLATFORM_BEAM.get(), properties()));
      LUNECAST = registerPanelBlockItem("lunecast", BlockInit.LUNECAST);
      LUNECAST_STAIRS = registerBlockItem("lunecast_stairs", BlockInit.LUNECAST_STAIRS);
      LUNECAST_SLAB = registerBlockItem("lunecast_slab", BlockInit.LUNECAST_SLAB);
      LUNECAST_PLATFORM = registerBlockItem("lunecast_platform", BlockInit.LUNECAST_PLATFORM);
      BLACKPLATE = registerPanelBlockItem("blackplate", BlockInit.BLACKPLATE);
      BLACKPLATE_STAIRS = registerBlockItem("blackplate_stairs", BlockInit.BLACKPLATE_STAIRS);
      BLACKPLATE_SLAB = registerBlockItem("blackplate_slab", BlockInit.BLACKPLATE_SLAB);
      BLACKPLATE_PLATFORM = registerBlockItem("blackplate_platform", BlockInit.BLACKPLATE_PLATFORM);
      ARBORED_LUNECAST = registerPanelBlockItem("arbored_lunecast", BlockInit.ARBORED_LUNECAST);
      ARBORED_LUNECAST_STAIRS = registerBlockItem("arbored_lunecast_stairs", BlockInit.ARBORED_LUNECAST_STAIRS);
      ARBORED_LUNECAST_SLAB = registerBlockItem("arbored_lunecast_slab", BlockInit.ARBORED_LUNECAST_SLAB);
      ARBORED_LUNECAST_PLATFORM = registerBlockItem("arbored_lunecast_platform", BlockInit.ARBORED_LUNECAST_PLATFORM);
      ARBORED_BLACKPLATE = registerPanelBlockItem("arbored_blackplate", BlockInit.ARBORED_BLACKPLATE);
      ARBORED_BLACKPLATE_STAIRS = registerBlockItem("arbored_blackplate_stairs", BlockInit.ARBORED_BLACKPLATE_STAIRS);
      ARBORED_BLACKPLATE_SLAB = registerBlockItem("arbored_blackplate_slab", BlockInit.ARBORED_BLACKPLATE_SLAB);
      ARBORED_BLACKPLATE_PLATFORM = registerBlockItem("arbored_blackplate_platform", BlockInit.ARBORED_BLACKPLATE_PLATFORM);
      ERODED_LUNECAST = registerPanelBlockItem("eroded_lunecast", BlockInit.ERODED_LUNECAST);
      ERODED_LUNECAST_STAIRS = registerBlockItem("eroded_lunecast_stairs", BlockInit.ERODED_LUNECAST_STAIRS);
      ERODED_LUNECAST_SLAB = registerBlockItem("eroded_lunecast_slab", BlockInit.ERODED_LUNECAST_SLAB);
      ERODED_LUNECAST_PLATFORM = registerBlockItem("eroded_lunecast_platform", BlockInit.ERODED_LUNECAST_PLATFORM);
      ERODED_BLACKPLATE = registerPanelBlockItem("eroded_blackplate", BlockInit.ERODED_BLACKPLATE);
      ERODED_BLACKPLATE_STAIRS = registerBlockItem("eroded_blackplate_stairs", BlockInit.ERODED_BLACKPLATE_STAIRS);
      ERODED_BLACKPLATE_SLAB = registerBlockItem("eroded_blackplate_slab", BlockInit.ERODED_BLACKPLATE_SLAB);
      ERODED_BLACKPLATE_PLATFORM = registerBlockItem("eroded_blackplate_platform", BlockInit.ERODED_BLACKPLATE_PLATFORM);
      FRACTURED_LUNECAST = registerPanelBlockItem("fractured_lunecast", BlockInit.FRACTURED_LUNECAST);
      FRACTURED_LUNECAST_STAIRS = registerBlockItem("fractured_lunecast_stairs", BlockInit.FRACTURED_LUNECAST_STAIRS);
      FRACTURED_LUNECAST_SLAB = registerBlockItem("fractured_lunecast_slab", BlockInit.FRACTURED_LUNECAST_SLAB);
      FRACTURED_LUNECAST_PLATFORM = registerBlockItem("fractured_lunecast_platform", BlockInit.FRACTURED_LUNECAST_PLATFORM);
      FRACTURED_BLACKPLATE = registerPanelBlockItem("fractured_blackplate", BlockInit.FRACTURED_BLACKPLATE);
      FRACTURED_BLACKPLATE_STAIRS = registerBlockItem("fractured_blackplate_stairs", BlockInit.FRACTURED_BLACKPLATE_STAIRS);
      FRACTURED_BLACKPLATE_SLAB = registerBlockItem("fractured_blackplate_slab", BlockInit.FRACTURED_BLACKPLATE_SLAB);
      FRACTURED_BLACKPLATE_PLATFORM = registerBlockItem("fractured_blackplate_platform", BlockInit.FRACTURED_BLACKPLATE_PLATFORM);
      VINTAGE_LUNECAST = registerPanelBlockItem("vintage_lunecast", BlockInit.VINTAGE_LUNECAST);
      VINTAGE_LUNECAST_STAIRS = registerBlockItem("vintage_lunecast_stairs", BlockInit.VINTAGE_LUNECAST_STAIRS);
      VINTAGE_LUNECAST_SLAB = registerBlockItem("vintage_lunecast_slab", BlockInit.VINTAGE_LUNECAST_SLAB);
      VINTAGE_LUNECAST_PLATFORM = registerBlockItem("vintage_lunecast_platform", BlockInit.VINTAGE_LUNECAST_PLATFORM);
      VINTAGE_BLACKPLATE = registerPanelBlockItem("vintage_blackplate", BlockInit.VINTAGE_BLACKPLATE);
      VINTAGE_BLACKPLATE_STAIRS = registerBlockItem("vintage_blackplate_stairs", BlockInit.VINTAGE_BLACKPLATE_STAIRS);
      VINTAGE_BLACKPLATE_SLAB = registerBlockItem("vintage_blackplate_slab", BlockInit.VINTAGE_BLACKPLATE_SLAB);
      VINTAGE_BLACKPLATE_PLATFORM = registerBlockItem("vintage_blackplate_platform", BlockInit.VINTAGE_BLACKPLATE_PLATFORM);
      WIRE_MESH_BLOCK = registerBlockItem("wire_mesh_block", BlockInit.WIRE_MESH_BLOCK);
      WIRE_MESH = registerBlockItem("wire_mesh", BlockInit.WIRE_MESH);
      IRON_FRAME = registerBlockItem("iron_frame", BlockInit.IRON_FRAME);
      BARRED_IRON_FRAME = registerBlockItem("barred_iron_frame", BlockInit.BARRED_IRON_FRAME);
      MESHED_IRON_FRAME = registerBlockItem("meshed_iron_frame", BlockInit.MESHED_IRON_FRAME);
      RUSTY_IRON_FRAME = registerBlockItem("rusty_iron_frame", BlockInit.RUSTY_IRON_FRAME);
      RUSTY_BARRED_IRON_FRAME = registerBlockItem("rusty_barred_iron_frame", BlockInit.RUSTY_BARRED_IRON_FRAME);
      RUSTY_MESHED_IRON_FRAME = registerBlockItem("rusty_meshed_iron_frame", BlockInit.RUSTY_MESHED_IRON_FRAME);
   }
}
