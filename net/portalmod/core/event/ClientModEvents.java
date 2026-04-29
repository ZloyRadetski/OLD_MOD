package net.portalmod.core.event;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.EntityType;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.portalmod.common.items.ModSpawnEggItem;
import net.portalmod.common.sorted.antline.AntlineBakedModel;
import net.portalmod.common.sorted.antline.AntlineLoader;
import net.portalmod.common.sorted.autoportal.AutoPortalTER;
import net.portalmod.common.sorted.button.SuperButtonGeometry;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.common.sorted.cube.GabeRenderer;
import net.portalmod.common.sorted.cube.companion.CompanionCubeRenderer;
import net.portalmod.common.sorted.cube.storage.StorageCubeRenderer;
import net.portalmod.common.sorted.cube.vintage.VintageCubeRenderer;
import net.portalmod.common.sorted.faithplate.FaithPlateTER;
import net.portalmod.common.sorted.portal.PortalEntityRenderer;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.common.sorted.portalgun.PortalGunGeometry;
import net.portalmod.common.sorted.portalgun.PortalGunItemColor;
import net.portalmod.common.sorted.portalgun.skins.ClientSkinManager;
import net.portalmod.common.sorted.sign.ChamberSignRenderer;
import net.portalmod.common.sorted.trigger.TriggerTER;
import net.portalmod.common.sorted.turret.TurretEntity;
import net.portalmod.common.sorted.turret.TurretRenderer;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.EntityInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.init.KeyInit;
import net.portalmod.core.init.ShaderInit;
import net.portalmod.core.init.TextureInit;
import net.portalmod.core.init.TileEntityTypeInit;

@EventBusSubscriber(
   modid = "portalmod",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientModEvents {
   @SubscribeEvent
   public static void clientSetup(FMLClientSetupEvent event) {
      ClientSkinManager.getInstance().onClientStartup();
      KeyInit.init();
      Minecraft.func_71410_x().func_147110_a().enableStencil();
      RenderTypeLookup.setRenderLayer((Block)BlockInit.ANTLINE.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.WIRE_MESH_BLOCK.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.WIRE_MESH.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.IRON_FRAME.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.BARRED_IRON_FRAME.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.MESHED_IRON_FRAME.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.RUSTY_IRON_FRAME.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.RUSTY_BARRED_IRON_FRAME.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.RUSTY_MESHED_IRON_FRAME.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.ARBORED_BLACKPLATE.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.ARBORED_BLACKPLATE_SLAB.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.ARBORED_BLACKPLATE_STAIRS.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.ARBORED_BLACKPLATE_PLATFORM.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.CHAMBER_LIGHTS.get(), RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.CUBE_DROPPER.get(), RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.FIZZLER_EMITTER.get(), RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.FIZZLER_FIELD.get(), RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.PROPULSION_GEL.get(), RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.REPULSION_GEL.get(), RenderType.func_228645_f_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.STANDING_BUTTON.get(), RenderType.func_228643_e_());
      RenderTypeLookup.setRenderLayer((Block)BlockInit.TRIGGER.get(), RenderType.func_228643_e_());
      ClientRegistry.bindTileEntityRenderer((TileEntityType)TileEntityTypeInit.FAITHPLATE.get(), FaithPlateTER::new);
      ClientRegistry.bindTileEntityRenderer((TileEntityType)TileEntityTypeInit.TRIGGER.get(), TriggerTER::new);
      ClientRegistry.bindTileEntityRenderer((TileEntityType)TileEntityTypeInit.AUTOPORTAL.get(), AutoPortalTER::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.COMPANION_CUBE.get(), CompanionCubeRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.STORAGE_CUBE.get(), StorageCubeRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.VINTAGE_CUBE.get(), VintageCubeRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.GABE.get(), GabeRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.PORTAL.get(), PortalEntityRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.TURRET.get(), TurretRenderer::new);
      RenderingRegistry.registerEntityRenderingHandler((EntityType)EntityInit.CHAMBER_SIGN.get(), ChamberSignRenderer::new);
      Minecraft.func_71410_x().getItemColors().func_199877_a(new PortalGunItemColor(), new IItemProvider[]{(IItemProvider)ItemInit.PORTALGUN.get()});
      event.enqueueWork(() -> {
         registerItemProperty((Item)ItemInit.PORTALGUN.get(), "color", PortalGun::getColorOverride);
         ShaderInit.REGISTRY.registerAll();
      });
   }

   private static void registerItemProperty(Item item, String name, IItemPropertyGetter getter) {
      ItemModelsProperties.func_239418_a_(item, new ResourceLocation("portalmod", name), getter);
   }

   @SubscribeEvent
   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)EntityInit.COMPANION_CUBE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.STORAGE_CUBE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.VINTAGE_CUBE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.GABE.get(), Cube.createAttributes().func_233813_a_());
      event.put((EntityType)EntityInit.TURRET.get(), TurretEntity.createAttributes().func_233813_a_());
   }

   @SubscribeEvent
   public static void onRegisterSpawnEggs(RegistryEvent.Register<EntityType<?>> event) {
      ModSpawnEggItem.register();
   }

   @SubscribeEvent
   public static void onLoaderRegister(ModelRegistryEvent event) {
      registerLoader("antline", new AntlineLoader());
      registerLoader("super_button", new SuperButtonGeometry.Loader());
      registerLoader("portalgun", new PortalGunGeometry.Loader());
   }

   private static void registerLoader(String name, IModelLoader<?> loader) {
      ModelLoaderRegistry.registerLoader(new ResourceLocation("portalmod", name), loader);
   }

   @SubscribeEvent
   public static void onTextureStitch(TextureStitchEvent.Pre event) {
      TextureInit.register(event);
      if (event.getMap().func_229223_g_() == AtlasTexture.field_110575_b) {
         for(ResourceLocation texture : AntlineBakedModel.getAllTextures()) {
            event.addSprite(texture);
         }

         for(ResourceLocation texture : TriggerTER.getAllFieldTextures()) {
            event.addSprite(texture);
         }

         event.addSprite(FaithPlateTER.TEXTURE_BLUE);
         event.addSprite(FaithPlateTER.TEXTURE_ORANGE);
         event.addSprite(FaithPlateTER.TEXTURE_BLUE_E);
         event.addSprite(FaithPlateTER.TEXTURE_ORANGE_E);
         event.addSprite(AutoPortalTER.TEXTURE);
      }

   }
}
