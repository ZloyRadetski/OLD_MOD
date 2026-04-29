package net.portalmod.mixins.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.portalmod.client.render.PortalCamera;
import net.portalmod.common.sorted.fizzler.FizzlerFieldBlock;
import net.portalmod.common.sorted.portal.DuplicateEntityRenderer;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalRenderer;
import net.portalmod.common.sorted.portal.PortalTransparencyHandler;
import net.portalmod.core.config.PortalModConfigManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({WorldRenderer.class})
public class LevelRendererMixin {
   @Redirect(
      method = {"levelEvent"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;isAir(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)Z"
)
   )
   public boolean fizzlerIsAirToo(BlockState instance, IBlockReader blockReader, BlockPos pos) {
      return instance.isAir(blockReader, pos) || instance.func_177230_c() instanceof FizzlerFieldBlock;
   }

   @Redirect(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V"
)
   )
   private void pmClear(int buffers, boolean onOsx) {
      if (PortalRenderer.getInstance().recursion == 0) {
         GL11.glStencilMask(-1);
         RenderSystem.clear(17664, onOsx);
      }

   }

   @Redirect(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/FogRenderer;setupColor(Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/world/ClientWorld;IF)V"
)
   )
   private void pmSetupColor(ActiveRenderInfo camera, float partialTicks, ClientWorld level, int renderDistance, float darken) {
      if (PortalRenderer.getInstance().currentlyRenderingPortals) {
         PortalEntity currentPortal = (PortalEntity)PortalRenderer.getInstance().portalChain.peekLast();
         if (currentPortal != null && currentPortal.getOtherPortal().isPresent()) {
            camera = new PortalCamera(camera, partialTicks);
            camera.func_216774_a(((PortalEntity)currentPortal.getOtherPortal().get()).func_213303_ch());
         }
      }

      FogRenderer.func_228371_a_(camera, partialTicks, level, renderDistance, darken);
   }

   @Redirect(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/FogRenderer$FogType;FZF)V"
)
   )
   private void pmSetupFog(ActiveRenderInfo camera, FogRenderer.FogType type, float renderDistance, boolean b, float partialTicks) {
      if (PortalRenderer.getInstance().currentlyRenderingPortals) {
         PortalEntity currentPortal = (PortalEntity)PortalRenderer.getInstance().portalChain.peekLast();
         if (currentPortal != null && currentPortal.getOtherPortal().isPresent()) {
            camera = new PortalCamera(camera, partialTicks);
            camera.func_216774_a(((PortalEntity)currentPortal.getOtherPortal().get()).func_213303_ch());
         }
      }

      FogRenderer.setupFog(camera, type, renderDistance, b, partialTicks);
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/world/DimensionRenderInfo;constantAmbientLight()Z"
)}
   )
   private void pmRenderPortals(MatrixStack matrixStack, float partialTicks, long l, boolean b, ActiveRenderInfo camera, GameRenderer gr, LightTexture lt, Matrix4f matrix, CallbackInfo info) {
      ClientWorld level = Minecraft.func_71410_x().field_71441_e;
      Vector3d vector3d = camera.func_216785_c();
      double x = vector3d.func_82615_a();
      double y = vector3d.func_82617_b();
      double z = vector3d.func_82616_c();
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      ClippingHelper clippinghelper = new ClippingHelper(matrix4f, matrix);
      clippinghelper.func_228952_a_(x, y, z);
      PortalRenderer.getInstance().renderPortals(level, camera, clippinghelper, matrix, partialTicks);
   }

   @ModifyArgs(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/WorldRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/matrix/MatrixStack;DDD)V"
)
   )
   private void pmSwitchToPortalTransparency(Args args) {
      RenderType renderType = (RenderType)args.get(0);
      if (renderType == RenderType.func_228645_f_() && PortalRenderer.getInstance().currentlyRenderingPortals) {
         args.set(0, PortalTransparencyHandler.PORTAL_TRANSLUCENT);
      }

   }

   @Redirect(
      method = {"renderChunkLayer"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;",
   ordinal = 1
)
   )
   private RenderType pmRenderPortalTransparencyBackwards() {
      return PortalRenderer.getInstance().currentlyRenderingPortals ? PortalTransparencyHandler.PORTAL_TRANSLUCENT : RenderType.func_228645_f_();
   }

   @Redirect(
      method = {"renderChunkLayer"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;isEmpty(Lnet/minecraft/client/renderer/RenderType;)Z"
)
   )
   private boolean pmIsPortalTransparencyEmpty(ChunkRenderDispatcher.CompiledChunk compiledChunk, RenderType renderType) {
      return compiledChunk.func_228912_a_(renderType == PortalTransparencyHandler.PORTAL_TRANSLUCENT ? RenderType.func_228645_f_() : renderType);
   }

   @Inject(
      method = {"renderChunkLayer"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/RenderType;setupRenderState()V",
   shift = Shift.AFTER
)}
   )
   private void pmResortPortalTransparency(RenderType renderType, MatrixStack matrixStack, double x, double y, double z, CallbackInfo ci) {
      if (renderType == PortalTransparencyHandler.PORTAL_TRANSLUCENT) {
         Minecraft.func_71410_x().func_213239_aq().func_76320_a("pm_translucent_sort");
         PortalTransparencyHandler.resortTransparency(PortalRenderer.getInstance().currentCamera);
         Minecraft.func_71410_x().func_213239_aq().func_76319_b();
      }

   }

   @Redirect(
      method = {"setupRender"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher;setCamera(Lnet/minecraft/util/math/vector/Vector3d;)V"
)
   )
   private void pmUseMainCameraForRebuilding(ChunkRenderDispatcher instance, Vector3d pos) {
      instance.func_217669_a(Minecraft.func_71410_x().field_71460_t.func_215316_n().func_216785_c());
   }

   @Redirect(
      method = {"setupRender"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/GameSettings;renderDistance:I",
   opcode = 180,
   ordinal = 0
)
   )
   private int pmMaskRenderDistanceForAllChangedGuard(GameSettings instance) {
      int override = PortalRenderer.getInstance().nestedBfsDistanceOverride;
      return override > 0 ? override : instance.field_151451_c;
   }

   @Redirect(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/entity/Entity;)Z"
)
   )
   private boolean pmAvoidRenderingOutlineInFakeEntities(Minecraft instance, Entity entity) {
      return PortalRenderer.getInstance().recursion > 0 ? false : instance.func_238206_b_(entity);
   }

   @Redirect(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;isDetached()Z"
)
   )
   private boolean pmRenderSelf(ActiveRenderInfo instance) {
      return !(Boolean)PortalModConfigManager.RENDER_SELF.get() ? instance.func_216770_i() : true;
   }

   @Redirect(
      method = {"renderLevel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ClippingHelper;DDD)Z"
)
   )
   private boolean pmShouldRenderSelf(EntityRendererManager erm, Entity entity, ClippingHelper clippingHelper, double camX, double camY, double camZ) {
      return (Boolean)PortalModConfigManager.RENDER_SELF.get() && entity == Minecraft.func_71410_x().field_175622_Z ? DuplicateEntityRenderer.shouldRenderSelf(entity, clippingHelper) : erm.func_229086_a_(entity, clippingHelper, camX, camY, camZ);
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/profiler/IProfiler;popPush(Ljava/lang/String;)V",
   ordinal = 10
)}
   )
   private void pmRenderDuplicateEntities(MatrixStack matrixStack, float partialTicks, long l, boolean b, ActiveRenderInfo camera, GameRenderer gr, LightTexture lt, Matrix4f projectionMatrix, CallbackInfo info) {
      Vector3d vector3d = camera.func_216785_c();
      double camX = vector3d.func_82615_a();
      double camY = vector3d.func_82617_b();
      double camZ = vector3d.func_82616_c();
      ClippingHelper clippinghelper = new ClippingHelper(matrixStack.func_227866_c_().func_227870_a_(), projectionMatrix);
      clippinghelper.func_228952_a_(camX, camY, camZ);
      DuplicateEntityRenderer.renderDuplicateEntities(clippinghelper, camX, camY, camZ, partialTicks, matrixStack, camera);
   }
}
