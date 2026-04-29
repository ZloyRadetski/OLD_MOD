package net.portalmod.core.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.portalmod.PMState;
import net.portalmod.PortalMod;
import net.portalmod.client.render.PortalFirstPersonRenderer;
import net.portalmod.common.blocks.InteractKeyInteractable;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.sorted.creer.CreerRenderer;
import net.portalmod.common.sorted.goo.GooBlock;
import net.portalmod.common.sorted.portal.CameraAnimator;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalEntityClient;
import net.portalmod.common.sorted.portal.PortalRenderer;
import net.portalmod.common.sorted.portalgun.CPortalGunInteractionPacket;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.common.sorted.portalgun.PortalGunClient;
import net.portalmod.common.sorted.portalgun.PortalGunCrosshairRenderer;
import net.portalmod.common.sorted.portalgun.PortalGunInteraction;
import net.portalmod.common.sorted.portalgun.skins.ClientSkinManager;
import net.portalmod.common.sorted.trigger.TriggerSelectionClient;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.init.AttributeInit;
import net.portalmod.core.init.FluidTagInit;
import net.portalmod.core.init.KeyInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.injectors.MainMenuInjector;
import net.portalmod.core.math.AABBUtil;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ChangeDetector;
import net.portalmod.core.util.DebugRenderer;
import net.portalmod.core.util.ModUtil;
import net.portalmod.mixins.accessors.ActiveRenderInfoAccessor;

@EventBusSubscriber(
   modid = "portalmod",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ClientEvents {
   private static final ChangeDetector creerNameDetector = new ChangeDetector();
   private static boolean wasPressed = false;
   public static final List<String> debugStrings = new ArrayList();

   @SubscribeEvent
   public static void onClientLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
      ClientSkinManager.getInstance().onClientLogin();
   }

   @SubscribeEvent
   public static void onPlayerRender(RenderPlayerEvent.Pre event) {
      BipedModel.ArmPose mainHand;
      BipedModel.ArmPose offHand;
      if (event.getEntityLiving().func_184591_cq() == HandSide.RIGHT) {
         mainHand = ((PlayerModel)event.getRenderer().func_217764_d()).field_187076_m;
         offHand = ((PlayerModel)event.getRenderer().func_217764_d()).field_187075_l;
      } else {
         offHand = ((PlayerModel)event.getRenderer().func_217764_d()).field_187076_m;
         mainHand = ((PlayerModel)event.getRenderer().func_217764_d()).field_187075_l;
      }

      if (event.getPlayer().func_184586_b(Hand.MAIN_HAND).func_77973_b() instanceof PortalGun) {
         mainHand = ArmPose.BOW_AND_ARROW;
      }

      if (event.getPlayer().func_184586_b(Hand.OFF_HAND).func_77973_b() instanceof PortalGun) {
         offHand = ArmPose.BOW_AND_ARROW;
      }

      if (mainHand.func_241657_a_()) {
         offHand = event.getEntityLiving().func_184592_cb().func_190926_b() ? ArmPose.EMPTY : ArmPose.ITEM;
      }

      if (event.getEntityLiving().func_184591_cq() == HandSide.RIGHT) {
         ((PlayerModel)event.getRenderer().func_217764_d()).field_187076_m = mainHand;
         ((PlayerModel)event.getRenderer().func_217764_d()).field_187075_l = offHand;
      } else {
         ((PlayerModel)event.getRenderer().func_217764_d()).field_187076_m = offHand;
         ((PlayerModel)event.getRenderer().func_217764_d()).field_187075_l = mainHand;
      }

   }

   @SubscribeEvent
   public static void screenOpen(GuiOpenEvent event) {
      if (event.getGui() instanceof MainMenuScreen) {
         event.setGui(MainMenuInjector.getInjectedMenu((Boolean)PortalModConfigManager.MENU.get(), MainMenuInjector.fading));
         MainMenuInjector.needsUpdate = false;
         MainMenuInjector.fading = false;
      }
   }

   @SubscribeEvent
   public static void renderLiving(RenderLivingEvent.Pre<?, ?> event) {
      if (!(event.getRenderer() instanceof CreerRenderer)) {
         if (!CreerRenderer.isCreer(event.getEntity())) {
            creerNameDetector.trigger(false);
         } else {
            creerNameDetector.trigger(true);
            CreeperEntity entity = (CreeperEntity)event.getEntity();
            float partialTicks = event.getPartialRenderTick();
            MatrixStack matrixStack = event.getMatrixStack();
            int light = event.getLight();
            IRenderTypeBuffer renderTypeBuffer = event.getBuffers();
            if (creerNameDetector.get()) {
               entity.func_213323_x_();
            }

            event.setCanceled(true);
            ((CreerRenderer)CreerRenderer.INSTANCE.func_179281_c()).func_225623_a_(entity, 0.0F, partialTicks, matrixStack, renderTypeBuffer, light);
         }
      }
   }

   @SubscribeEvent
   public static void fogDensity(EntityViewRenderEvent.FogDensity event) {
      ActiveRenderInfo info = event.getInfo();
      if (info.func_216771_k().func_206884_a(FluidTagInit.GOO)) {
         event.setDensity(GooBlock.getFogDensity(info.func_216773_g()));
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public static void fogColor(EntityViewRenderEvent.FogColors event) {
      ActiveRenderInfo info = event.getInfo();
      if (info.func_216771_k().func_206884_a(FluidTagInit.GOO)) {
         Vector3f color = GooBlock.getFogColor().to3f();
         event.setRed(color.func_195899_a());
         event.setGreen(color.func_195900_b());
         event.setBlue(color.func_195902_c());
      }

   }

   @SubscribeEvent
   public static void entitySize(EntityEvent.Size event) {
      if (CreerRenderer.isCreer(event.getEntity())) {
         EntitySize size = event.getOldSize();
         event.setNewSize(EntitySize.func_220314_b(size.field_220315_a, 1.0F), true);
      }
   }

   @SubscribeEvent
   public static void clientTick(TickEvent.ClientTickEvent event) {
      if (event.phase == Phase.END) {
         PortalFirstPersonRenderer.updateSwingTime();
      }

      if (event.phase == Phase.START) {
         ClientSkinManager.getInstance().tick();
         Minecraft mc = Minecraft.func_71410_x();
         PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
         World level = Minecraft.func_71410_x().field_71441_e;
         if (player != null && level != null && Minecraft.func_71410_x().field_71442_b != null) {
            if (TriggerSelectionClient.isSelecting()) {
               float rayLength = Minecraft.func_71410_x().field_71442_b.func_78757_d();
               Vector3d rayPath = player.func_70676_i(0.0F).func_186678_a((double)rayLength);
               Vector3d from = player.func_174824_e(0.0F);
               Vector3d to = from.func_178787_e(rayPath);
               RayTraceContext rayCtx = new RayTraceContext(from, to, BlockMode.OUTLINE, FluidMode.NONE, (Entity)null);
               BlockRayTraceResult rayHit = Minecraft.func_71410_x().field_71441_e.func_217299_a(rayCtx);
               BlockPos pos = rayHit.func_216350_a();
               BlockState state = level.func_180495_p(pos);
               if (state.func_224755_d(level, pos, rayHit.func_216354_b())) {
                  pos = pos.func_177972_a(rayHit.func_216354_b());
               }

               TriggerSelectionClient.updateSelectedPos(pos);
            }

            if ((mc.field_213279_p != null || mc.field_71462_r != null) && player.func_184614_ca().func_77973_b() instanceof PortalGun) {
               PortalGunClient.getInstance().resetPresses();
            }
         }

         PortalGunClient.getInstance().tick();
         handleInteractKey();
      }
   }

   public static void handleInteractKey() {
      if (!KeyInit.PORTALGUN_INTERACT.func_151470_d()) {
         wasPressed = false;
      } else if (!wasPressed) {
         wasPressed = true;
         PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
         if (player != null) {
            if (!player.func_175149_v()) {
               float rayLength = Minecraft.func_71410_x().field_71442_b.func_78757_d();
               Vector3d playerRotation = player.func_70676_i(0.0F);
               Vector3d rayPath = playerRotation.func_186678_a((double)rayLength);
               Vector3d from = player.func_174824_e(0.0F);
               Vector3d to = from.func_178787_e(rayPath);
               RayTraceContext rayCtx = new RayTraceContext(from, to, BlockMode.OUTLINE, FluidMode.NONE, (Entity)null);
               BlockRayTraceResult rayHit = Minecraft.func_71410_x().field_71441_e.func_217299_a(rayCtx);
               ItemStack itemStack = player.func_184614_ca();
               if (player.func_205708_a(TestElementEntity.class)) {
                  TestElementEntity.dropHeldEntities(player, false, false, itemStack);
                  PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.DROP_ENTITY)).build());
                  consumeAllKeyPresses(KeyInit.PORTALGUN_INTERACT.getKey());
               } else {
                  Block block = Minecraft.func_71410_x().field_71441_e.func_180495_p(rayHit.func_216350_a()).func_177230_c();
                  if (block instanceof InteractKeyInteractable && ((InteractKeyInteractable)block).interactKeyInteract(player, rayHit)) {
                     consumeAllKeyPresses(KeyInit.PORTALGUN_INTERACT.getKey());
                  } else {
                     try {
                        List<PortalEntity> portalChain = ModUtil.getPortalsAlongRay(player.field_70170_p, new Vec3(from), new Vec3(to), (portal) -> true);
                        Mat4 portalMatrix = ModUtil.getMatrixFromPortalChain(portalChain);
                        Pair<Vector3d, Vector3d> ray = ModUtil.teleportRay(portalChain, from, to);
                        AxisAlignedBB aabb = player.func_174813_aQ().func_216361_a(rayPath);
                        aabb = AABBUtil.transform(aabb, portalMatrix);
                        EntityRayTraceResult realWorldEntityRayTraceResult = ProjectileHelper.func_221273_a(player, from, to, player.func_174813_aQ().func_216361_a(rayPath), TestElementEntity::isHoldable, (double)(rayLength * rayLength));
                        EntityRayTraceResult teleportedEntityRayTraceResult = ProjectileHelper.func_221273_a(player, (Vector3d)ray.getFirst(), (Vector3d)ray.getSecond(), aabb, TestElementEntity::isHoldable, (double)(rayLength * rayLength));
                        if (realWorldEntityRayTraceResult == null && teleportedEntityRayTraceResult == null) {
                           return;
                        }

                        boolean realWorld = true;
                        EntityRayTraceResult entityRayTraceResult;
                        if (teleportedEntityRayTraceResult == null) {
                           entityRayTraceResult = realWorldEntityRayTraceResult;
                        } else if (realWorldEntityRayTraceResult == null) {
                           entityRayTraceResult = teleportedEntityRayTraceResult;
                           realWorld = false;
                        } else {
                           Vector3d teleportedFrom = (new Vec3(from)).transform(portalMatrix).to3d();
                           double realWorldDistance = realWorldEntityRayTraceResult.func_216347_e().func_178788_d(from).func_189985_c();
                           double teleportedDistance = teleportedEntityRayTraceResult.func_216347_e().func_178788_d(teleportedFrom).func_189985_c();
                           entityRayTraceResult = realWorldDistance <= teleportedDistance ? realWorldEntityRayTraceResult : teleportedEntityRayTraceResult;
                           realWorld = realWorldDistance <= teleportedDistance;
                        }

                        Entity entity = entityRayTraceResult.func_216348_a();
                        Vector3d reachPosition = player.func_213303_ch().func_72441_c((double)0.0F, 0.2, (double)0.0F);
                        double grabReach = player.func_233637_b_((Attribute)AttributeInit.GRAB_REACH.get());
                        Vec3 distance = (new Vec3(entity.func_213303_ch())).sub((new Vec3(reachPosition)).transform(realWorld ? Mat4.identity() : portalMatrix));
                        if (entity instanceof TestElementEntity && distance.magnitude() < grabReach + (double)(entity.func_213311_cf() / 2.0F)) {
                           ((TestElementEntity)entity).pickUp(player);
                           consumeAllKeyPresses(KeyInit.PORTALGUN_INTERACT.getKey());
                        }
                     } catch (Exception e) {
                        PortalMod.LOGGER.error("Error while picking up entity", e);
                     }

                  }
               }
            }
         }
      }
   }

   private static void consumeAllKeyPresses(InputMappings.Input k) {
      KeyBinding[] keys = Minecraft.func_71410_x().field_71474_y.field_74324_K;

      for(KeyBinding key : keys) {
         if (key.getKey() == k) {
            while(key.func_151468_f()) {
            }
         }
      }

   }

   @SubscribeEvent
   public static void onMouseClick(InputEvent.RawMouseEvent event) {
      Minecraft mc = Minecraft.func_71410_x();
      PlayerEntity player = mc.field_71439_g;
      if (player != null) {
         if (mc.field_213279_p == null && mc.field_71462_r == null) {
            if (player.func_184614_ca().func_77973_b() instanceof PortalGun) {
               boolean handled = PortalGunClient.getInstance().handleMouseButtons(event.getButton(), event.getAction());
               if (handled) {
                  event.setCanceled(true);
               }
            } else {
               PortalGunClient.getInstance().resetPresses();
            }
         }

      }
   }

   @SubscribeEvent
   public static void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
      if (!PortalEntity.shouldRenderBlockOverlay(event.getPlayer().field_70170_p, event.getBlockPos())) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public static void onRenderWorldLast(RenderWorldLastEvent event) {
      DebugRenderer.renderAllShapes(event.getMatrixStack());
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
      PMState.cameraPosOverrideForRenderingSelf = null;
      if (Minecraft.func_71410_x().field_71474_y.func_243230_g().func_243192_a()) {
         Optional<Float> optionalPitch = CameraAnimator.getInstance().getRelativePitch();
         optionalPitch.ifPresent((relativePitch) -> event.setPitch(event.getPitch() + relativePitch));
         Optional<Float> optionalYaw = CameraAnimator.getInstance().getRelativeYaw();
         optionalYaw.ifPresent((relativeYaw) -> event.setYaw(event.getYaw() + relativeYaw));
         Optional<Float> optionalRoll = CameraAnimator.getInstance().getRelativeRoll();
         optionalRoll.ifPresent((relativeRoll) -> event.setRoll(event.getRoll() + relativeRoll));
         Optional<Vec3> optionalPos = CameraAnimator.getInstance().getRelativePos();
         optionalPos.ifPresent((pos) -> {
            PMState.cameraPosOverrideForRenderingSelf = new Vec3(event.getInfo().func_216785_c());
            ((ActiveRenderInfoAccessor)event.getInfo()).pmSetPosition(event.getInfo().func_216785_c().func_178787_e(pos.to3d()));
         });
      }

      PortalEntityClient.teleportCameraAndApply(event);
      PMState.cameraRoll = event.getRoll();
   }

   @SubscribeEvent
   public static void onFogSetup(EntityViewRenderEvent.FogColors event) {
      PortalRenderer.getInstance().clearColor = new Vec3((double)event.getRed(), (double)event.getGreen(), (double)event.getBlue());
   }

   @SubscribeEvent
   public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
      Minecraft minecraft = Minecraft.func_71410_x();
      FontRenderer fontRenderer = minecraft.field_71466_p;
      if (event.getType() == ElementType.CROSSHAIRS) {
         PortalGunCrosshairRenderer.render(event.getMatrixStack());
      } else if (event.getType() == ElementType.SUBTITLES) {
         int x = 2;
         int y = 2;
         int index = 0;

         for(String text : debugStrings) {
            MatrixStack var10001 = event.getMatrixStack();
            float var10003 = (float)x;
            fontRenderer.getClass();
            fontRenderer.func_238405_a_(var10001, text, var10003, (float)(y + 9 * index++), -22016);
         }

         debugStrings.clear();
      }

   }
}
