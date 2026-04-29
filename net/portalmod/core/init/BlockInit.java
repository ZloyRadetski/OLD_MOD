package net.portalmod.core.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.portalmod.common.blocks.ChamberLightsBlock;
import net.portalmod.common.blocks.ForestCakeBlock;
import net.portalmod.common.blocks.FrameBlock;
import net.portalmod.common.blocks.PushDoorBlock;
import net.portalmod.common.blocks.WireMeshBlock;
import net.portalmod.common.sorted.antline.AntlineBlock;
import net.portalmod.common.sorted.antline.indicator.AntlineDecoderBlock;
import net.portalmod.common.sorted.antline.indicator.AntlineEncoderBlock;
import net.portalmod.common.sorted.antline.indicator.AntlineIndicatorBlock;
import net.portalmod.common.sorted.antline.indicator.AntlineTimerBlock;
import net.portalmod.common.sorted.autoportal.AutoPortalBlock;
import net.portalmod.common.sorted.button.StandingButtonBlock;
import net.portalmod.common.sorted.button.SuperButtonBlock;
import net.portalmod.common.sorted.cubedropper.CubeDropperBlock;
import net.portalmod.common.sorted.door.ChamberDoorBlock;
import net.portalmod.common.sorted.faithplate.FaithPlateBlock;
import net.portalmod.common.sorted.fizzler.FizzlerEmitterBlock;
import net.portalmod.common.sorted.fizzler.FizzlerFieldBlock;
import net.portalmod.common.sorted.gel.PropulsionGelBlock;
import net.portalmod.common.sorted.gel.RepulsionGelBlock;
import net.portalmod.common.sorted.goo.GooBlock;
import net.portalmod.common.sorted.panel.PanelBlock;
import net.portalmod.common.sorted.platform.PlatformBeamBlock;
import net.portalmod.common.sorted.platform.PlatformBlock;
import net.portalmod.common.sorted.radio.RadioBlock;
import net.portalmod.common.sorted.trigger.TriggerBlock;
import net.portalmod.common.sorted.trigger.TriggerState;

public class BlockInit {
   public static final DeferredRegister<Block> BLOCKS;
   public static final Material TESTING_ELEMENT;
   public static final RegistryObject<Block> LUNECAST;
   public static final RegistryObject<Block> BLACKPLATE;
   public static final RegistryObject<Block> ARBORED_LUNECAST;
   public static final RegistryObject<Block> ARBORED_BLACKPLATE;
   public static final RegistryObject<Block> ERODED_LUNECAST;
   public static final RegistryObject<Block> ERODED_BLACKPLATE;
   public static final RegistryObject<Block> FRACTURED_LUNECAST;
   public static final RegistryObject<Block> FRACTURED_BLACKPLATE;
   public static final RegistryObject<Block> VINTAGE_LUNECAST;
   public static final RegistryObject<Block> VINTAGE_BLACKPLATE;
   public static final RegistryObject<Block> LUNECAST_SLAB;
   public static final RegistryObject<Block> BLACKPLATE_SLAB;
   public static final RegistryObject<Block> ARBORED_LUNECAST_SLAB;
   public static final RegistryObject<Block> ARBORED_BLACKPLATE_SLAB;
   public static final RegistryObject<Block> ERODED_LUNECAST_SLAB;
   public static final RegistryObject<Block> ERODED_BLACKPLATE_SLAB;
   public static final RegistryObject<Block> FRACTURED_LUNECAST_SLAB;
   public static final RegistryObject<Block> FRACTURED_BLACKPLATE_SLAB;
   public static final RegistryObject<Block> VINTAGE_LUNECAST_SLAB;
   public static final RegistryObject<Block> VINTAGE_BLACKPLATE_SLAB;
   public static final RegistryObject<Block> LUNECAST_STAIRS;
   public static final RegistryObject<Block> BLACKPLATE_STAIRS;
   public static final RegistryObject<Block> ARBORED_LUNECAST_STAIRS;
   public static final RegistryObject<Block> ARBORED_BLACKPLATE_STAIRS;
   public static final RegistryObject<Block> ERODED_LUNECAST_STAIRS;
   public static final RegistryObject<Block> ERODED_BLACKPLATE_STAIRS;
   public static final RegistryObject<Block> FRACTURED_LUNECAST_STAIRS;
   public static final RegistryObject<Block> FRACTURED_BLACKPLATE_STAIRS;
   public static final RegistryObject<Block> VINTAGE_LUNECAST_STAIRS;
   public static final RegistryObject<Block> VINTAGE_BLACKPLATE_STAIRS;
   public static final RegistryObject<Block> PLATFORM_BEAM;
   public static final RegistryObject<Block> RUSTY_PLATFORM_BEAM;
   public static final RegistryObject<Block> LUNECAST_PLATFORM;
   public static final RegistryObject<Block> BLACKPLATE_PLATFORM;
   public static final RegistryObject<Block> ARBORED_LUNECAST_PLATFORM;
   public static final RegistryObject<Block> ARBORED_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Block> ERODED_LUNECAST_PLATFORM;
   public static final RegistryObject<Block> ERODED_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Block> FRACTURED_LUNECAST_PLATFORM;
   public static final RegistryObject<Block> FRACTURED_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Block> VINTAGE_LUNECAST_PLATFORM;
   public static final RegistryObject<Block> VINTAGE_BLACKPLATE_PLATFORM;
   public static final RegistryObject<Block> IRON_FRAME;
   public static final RegistryObject<Block> BARRED_IRON_FRAME;
   public static final RegistryObject<Block> MESHED_IRON_FRAME;
   public static final RegistryObject<Block> RUSTY_IRON_FRAME;
   public static final RegistryObject<Block> RUSTY_BARRED_IRON_FRAME;
   public static final RegistryObject<Block> RUSTY_MESHED_IRON_FRAME;
   public static final RegistryObject<Block> RADIO;
   public static final RegistryObject<Block> FAITHPLATE;
   public static final RegistryObject<Block> WIRE_MESH_BLOCK;
   public static final RegistryObject<Block> WIRE_MESH;
   public static final RegistryObject<Block> FOREST_CAKE;
   public static final RegistryObject<Block> CHAMBER_DOOR;
   public static final RegistryObject<Block> CUBE_DROPPER;
   public static final RegistryObject<Block> PUSH_DOOR;
   public static final RegistryObject<Block> STANDING_BUTTON;
   public static final RegistryObject<Block> SUPER_BUTTON;
   public static final RegistryObject<Block> CHAMBER_LIGHTS;
   public static final RegistryObject<Block> TRIGGER;
   public static final RegistryObject<Block> AUTOPORTAL;
   public static final RegistryObject<Block> ANTLINE;
   public static final RegistryObject<Block> ANTLINE_INDICATOR;
   public static final RegistryObject<Block> ANTLINE_TIMER;
   public static final RegistryObject<Block> ANTLINE_DECODER;
   public static final RegistryObject<Block> ANTLINE_ENCODER;
   public static final RegistryObject<Block> FIZZLER_EMITTER;
   public static final RegistryObject<Block> FIZZLER_FIELD;
   public static final RegistryObject<FlowingFluidBlock> GOO;
   public static final RegistryObject<Block> REPULSION_GEL;
   public static final RegistryObject<Block> PROPULSION_GEL;
   public static final RegistryObject<Block> TEST_BLOCK;

   private BlockInit() {
   }

   public static RegistryObject<Block> registerLunecast(String name) {
      return BLOCKS.register(name, () -> new PanelBlock(Properties.func_200950_a(Blocks.field_196828_iC)));
   }

   public static RegistryObject<Block> registerBlackplate(String name) {
      return BLOCKS.register(name, () -> new PanelBlock(Properties.func_200950_a(Blocks.field_196858_iR)));
   }

   public static RegistryObject<Block> registerLunecastPlatform(String name) {
      return BLOCKS.register(name, () -> new PlatformBlock(Properties.func_200950_a(Blocks.field_196828_iC)));
   }

   public static RegistryObject<Block> registerBlackplatePlatform(String name) {
      return BLOCKS.register(name, () -> new PlatformBlock(Properties.func_200950_a(Blocks.field_196858_iR)));
   }

   public static RegistryObject<Block> registerLunecastSlab(String name) {
      return BLOCKS.register(name + "_slab", () -> new SlabBlock(Properties.func_200950_a(Blocks.field_196828_iC)));
   }

   public static RegistryObject<Block> registerBlackplateSlab(String name) {
      return BLOCKS.register(name + "_slab", () -> new SlabBlock(Properties.func_200950_a(Blocks.field_196858_iR)));
   }

   public static RegistryObject<Block> registerLunecastStairs(String name) {
      return BLOCKS.register(name + "_stairs", () -> new StairsBlock(() -> ((Block)LUNECAST.get()).func_176223_P(), Properties.func_200950_a(Blocks.field_196828_iC)));
   }

   public static RegistryObject<Block> registerBlackplateStairs(String name) {
      return BLOCKS.register(name + "_stairs", () -> new StairsBlock(() -> ((Block)BLACKPLATE.get()).func_176223_P(), Properties.func_200950_a(Blocks.field_196858_iR)));
   }

   public static AbstractBlock.Properties stoneCopy(MaterialColor color) {
      return Properties.func_200949_a(Material.field_151576_e, color).func_235861_h_().func_200948_a(1.5F, 6.0F);
   }

   public static boolean always(BlockState p_235426_0_, IBlockReader p_235426_1_, BlockPos p_235426_2_) {
      return true;
   }

   static {
      BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "portalmod");
      TESTING_ELEMENT = new Material(MaterialColor.field_151670_w, false, false, true, false, false, false, PushReaction.DESTROY);
      LUNECAST = registerLunecast("lunecast");
      BLACKPLATE = registerBlackplate("blackplate");
      ARBORED_LUNECAST = registerLunecast("arbored_lunecast");
      ARBORED_BLACKPLATE = registerBlackplate("arbored_blackplate");
      ERODED_LUNECAST = registerLunecast("eroded_lunecast");
      ERODED_BLACKPLATE = registerBlackplate("eroded_blackplate");
      FRACTURED_LUNECAST = registerLunecast("fractured_lunecast");
      FRACTURED_BLACKPLATE = registerBlackplate("fractured_blackplate");
      VINTAGE_LUNECAST = registerLunecast("vintage_lunecast");
      VINTAGE_BLACKPLATE = registerBlackplate("vintage_blackplate");
      LUNECAST_SLAB = registerLunecastSlab("lunecast");
      BLACKPLATE_SLAB = registerBlackplateSlab("blackplate");
      ARBORED_LUNECAST_SLAB = registerLunecastSlab("arbored_lunecast");
      ARBORED_BLACKPLATE_SLAB = registerBlackplateSlab("arbored_blackplate");
      ERODED_LUNECAST_SLAB = registerLunecastSlab("eroded_lunecast");
      ERODED_BLACKPLATE_SLAB = registerBlackplateSlab("eroded_blackplate");
      FRACTURED_LUNECAST_SLAB = registerLunecastSlab("fractured_lunecast");
      FRACTURED_BLACKPLATE_SLAB = registerBlackplateSlab("fractured_blackplate");
      VINTAGE_LUNECAST_SLAB = registerLunecastSlab("vintage_lunecast");
      VINTAGE_BLACKPLATE_SLAB = registerBlackplateSlab("vintage_blackplate");
      LUNECAST_STAIRS = registerLunecastStairs("lunecast");
      BLACKPLATE_STAIRS = registerBlackplateStairs("blackplate");
      ARBORED_LUNECAST_STAIRS = registerLunecastStairs("arbored_lunecast");
      ARBORED_BLACKPLATE_STAIRS = registerBlackplateStairs("arbored_blackplate");
      ERODED_LUNECAST_STAIRS = registerLunecastStairs("eroded_lunecast");
      ERODED_BLACKPLATE_STAIRS = registerBlackplateStairs("eroded_blackplate");
      FRACTURED_LUNECAST_STAIRS = registerLunecastStairs("fractured_lunecast");
      FRACTURED_BLACKPLATE_STAIRS = registerBlackplateStairs("fractured_blackplate");
      VINTAGE_LUNECAST_STAIRS = registerLunecastStairs("vintage_lunecast");
      VINTAGE_BLACKPLATE_STAIRS = registerBlackplateStairs("vintage_blackplate");
      PLATFORM_BEAM = BLOCKS.register("platform_beam", () -> new PlatformBeamBlock(Properties.func_200950_a(Blocks.field_196844_iK)));
      RUSTY_PLATFORM_BEAM = BLOCKS.register("rusty_platform_beam", () -> new PlatformBeamBlock(Properties.func_200950_a(Blocks.field_196844_iK)));
      LUNECAST_PLATFORM = registerLunecastPlatform("lunecast_platform");
      BLACKPLATE_PLATFORM = registerBlackplatePlatform("blackplate_platform");
      ARBORED_LUNECAST_PLATFORM = registerLunecastPlatform("arbored_lunecast_platform");
      ARBORED_BLACKPLATE_PLATFORM = registerBlackplatePlatform("arbored_blackplate_platform");
      ERODED_LUNECAST_PLATFORM = registerLunecastPlatform("eroded_lunecast_platform");
      ERODED_BLACKPLATE_PLATFORM = registerBlackplatePlatform("eroded_blackplate_platform");
      FRACTURED_LUNECAST_PLATFORM = registerLunecastPlatform("fractured_lunecast_platform");
      FRACTURED_BLACKPLATE_PLATFORM = registerBlackplatePlatform("fractured_blackplate_platform");
      VINTAGE_LUNECAST_PLATFORM = registerLunecastPlatform("vintage_lunecast_platform");
      VINTAGE_BLACKPLATE_PLATFORM = registerBlackplatePlatform("vintage_blackplate_platform");
      IRON_FRAME = BLOCKS.register("iron_frame", () -> new FrameBlock(Properties.func_200950_a(Blocks.field_150339_S).func_226896_b_(), false));
      BARRED_IRON_FRAME = BLOCKS.register("barred_iron_frame", () -> new FrameBlock(Properties.func_200950_a(Blocks.field_150339_S).func_226896_b_(), true));
      MESHED_IRON_FRAME = BLOCKS.register("meshed_iron_frame", () -> new FrameBlock(Properties.func_200950_a(Blocks.field_150339_S).func_226896_b_(), true));
      RUSTY_IRON_FRAME = BLOCKS.register("rusty_iron_frame", () -> new FrameBlock(Properties.func_200950_a(Blocks.field_150339_S).func_226896_b_(), false));
      RUSTY_BARRED_IRON_FRAME = BLOCKS.register("rusty_barred_iron_frame", () -> new FrameBlock(Properties.func_200950_a(Blocks.field_150339_S).func_226896_b_(), true));
      RUSTY_MESHED_IRON_FRAME = BLOCKS.register("rusty_meshed_iron_frame", () -> new FrameBlock(Properties.func_200950_a(Blocks.field_150339_S).func_226896_b_(), true));
      RADIO = BLOCKS.register("radio", () -> new RadioBlock(Properties.func_200945_a(TESTING_ELEMENT).func_200943_b(1.0F)));
      FAITHPLATE = BLOCKS.register("faithplate", () -> new FaithPlateBlock(stoneCopy(MaterialColor.field_151646_E)));
      WIRE_MESH_BLOCK = BLOCKS.register("wire_mesh_block", () -> new WireMeshBlock(Properties.func_200950_a(Blocks.field_235341_dI_)));
      WIRE_MESH = BLOCKS.register("wire_mesh", () -> new PaneBlock(Properties.func_200950_a(Blocks.field_235341_dI_)));
      FOREST_CAKE = BLOCKS.register("forest_cake", () -> new ForestCakeBlock(Properties.func_200950_a(Blocks.field_150414_aQ)));
      CHAMBER_DOOR = BLOCKS.register("chamber_door", () -> new ChamberDoorBlock(stoneCopy(MaterialColor.field_151646_E).func_200947_a(SoundType.field_185851_d).func_226896_b_()));
      CUBE_DROPPER = BLOCKS.register("cube_dropper", () -> new CubeDropperBlock(Properties.func_200945_a(Material.field_151576_e).func_226896_b_()));
      PUSH_DOOR = BLOCKS.register("push_door", () -> new PushDoorBlock(Properties.func_200950_a(Blocks.field_150454_av)));
      STANDING_BUTTON = BLOCKS.register("standing_button", () -> new StandingButtonBlock(stoneCopy(MaterialColor.field_151645_D).func_226896_b_()));
      SUPER_BUTTON = BLOCKS.register("super_button", () -> new SuperButtonBlock(stoneCopy(MaterialColor.field_151645_D).func_226896_b_()));
      CHAMBER_LIGHTS = BLOCKS.register("chamber_lights", () -> new ChamberLightsBlock(Properties.func_200950_a(Blocks.field_150379_bu).func_235838_a_((i) -> (Boolean)i.func_177229_b(ChamberLightsBlock.ACTIVE) ? 15 : 0)));
      TRIGGER = BLOCKS.register("trigger", () -> new TriggerBlock(Properties.func_200950_a(Blocks.field_196646_bz).func_235838_a_((i) -> i.func_177229_b(TriggerBlock.STATE) == TriggerState.ACTIVE ? 15 : 0)));
      AUTOPORTAL = BLOCKS.register("autoportal", () -> new AutoPortalBlock(Properties.func_200950_a(Blocks.field_150339_S)));
      ANTLINE = BLOCKS.register("antline", () -> new AntlineBlock(Properties.func_200949_a(TESTING_ELEMENT, MaterialColor.field_151674_s).func_235838_a_((i) -> 2).func_200942_a().func_200946_b()));
      ANTLINE_INDICATOR = BLOCKS.register("antline_indicator", () -> new AntlineIndicatorBlock(Properties.func_200945_a(TESTING_ELEMENT).func_226896_b_().func_200943_b(1.0F).func_235838_a_((i) -> 7)));
      ANTLINE_TIMER = BLOCKS.register("antline_timer", () -> new AntlineTimerBlock(Properties.func_200945_a(TESTING_ELEMENT).func_226896_b_().func_200943_b(1.0F).func_235838_a_((i) -> 7)));
      ANTLINE_DECODER = BLOCKS.register("antline_decoder", () -> new AntlineDecoderBlock(Properties.func_200945_a(TESTING_ELEMENT).func_226896_b_().func_200943_b(1.0F).func_235838_a_((i) -> 7)));
      ANTLINE_ENCODER = BLOCKS.register("antline_encoder", () -> new AntlineEncoderBlock(Properties.func_200945_a(TESTING_ELEMENT).func_226896_b_().func_200943_b(1.0F).func_235838_a_((i) -> 7)));
      FIZZLER_EMITTER = BLOCKS.register("fizzler_emitter", () -> new FizzlerEmitterBlock(stoneCopy(MaterialColor.field_151646_E).func_226896_b_().func_235838_a_((blockState) -> (Boolean)blockState.func_177229_b(FizzlerEmitterBlock.ACTIVE) ? 10 : 0)));
      FIZZLER_FIELD = BLOCKS.register("fizzler_field", () -> new FizzlerFieldBlock(Properties.func_200950_a(Blocks.field_150350_a).func_226896_b_().func_200948_a(-1.0F, 3600000.0F).func_222380_e().func_235838_a_((blockState) -> 10)));
      GOO = BLOCKS.register("goo", () -> new GooBlock(FluidInit.GOO_FLUID, Properties.func_200949_a(Material.field_151586_h, MaterialColor.field_151654_J)));
      REPULSION_GEL = BLOCKS.register("repulsion_gel", () -> new RepulsionGelBlock(Properties.func_200949_a(TESTING_ELEMENT, MaterialColor.field_151649_A).func_200947_a(SoundTypeInit.GEL).func_226896_b_().func_200942_a().func_200943_b(-1.0F)));
      PROPULSION_GEL = BLOCKS.register("propulsion_gel", () -> new PropulsionGelBlock(Properties.func_200949_a(TESTING_ELEMENT, MaterialColor.field_151676_q).func_200947_a(SoundTypeInit.GEL).func_226896_b_().func_200942_a().func_200943_b(-1.0F)));
      TEST_BLOCK = BLOCKS.register("test_block", () -> new Block(Properties.func_200950_a(Blocks.field_235397_ng_)));
   }
}
