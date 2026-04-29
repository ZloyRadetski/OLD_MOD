package net.portalmod.common.sorted.portal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.FogRenderer.FogType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.portalmod.PMState;
import net.portalmod.client.render.PortalCamera;
import net.portalmod.client.render.Shader;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.event.ClientEvents;
import net.portalmod.core.init.ShaderInit;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;
import net.portalmod.core.util.RenderUtil;
import net.portalmod.core.util.VertexRenderer;
import net.portalmod.mixins.accessors.LevelRendererAccessor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class PortalRenderer {
   private static PortalRenderer instance;
   private static final VertexRenderer portalMesh;
   private static final VertexRenderer screenQuad;
   private static final VertexRenderer blitQuad;
   private static final Framebuffer tempFBO;
   public int recursion = 0;
   public ActiveRenderInfo currentCamera;
   public int renderedPortals = 0;
   public boolean currentlyRenderingPortals = false;
   private boolean fabulousGraphics = false;
   private final Deque<PortalEntity> portalStack = new ArrayDeque();
   public MatrixStack clipMatrix = new MatrixStack();
   private final float[] projectionBuffer = new float[16];
   public Vec3 clearColor = new Vec3((double)0.0F);
   public List<PortalEntity> outlineRenderingPortalChain;
   public final Deque<PortalEntity> portalChain = new ArrayDeque();
   public int nestedBfsDistanceOverride = -1;
   private final float[] currentParentNdcRect = new float[]{-1.0F, -1.0F, 1.0F, 1.0F};
   private final Deque<float[]> parentNdcRectStack = new ArrayDeque();
   public int portalsCulledByNdcRect = 0;
   public int portalsStencilSkippedOccludedRays = 0;
   private static final double RAY_CORNER_EPSILON = 0.08;
   private static final Optional<VoxelShape> EMPTY_SHAPE_OVERRIDE;
   public static final Profile PROFILE;
   private static final Map<UUID, Long> PROFILER_PORTAL_OCCLUSION_LAST_PARTICLE_TICK;
   private static final int PORTAL_OPENING_GRID_W = 8;
   private static final int PORTAL_OPENING_GRID_H = 16;
   private static final int PORTAL_OPENING_CONTROL_POINT_COUNT = 128;

   public static boolean portalProfilerEnabled() {
      return Boolean.getBoolean("portalmod.portalProfiler");
   }

   private PortalRenderer() {
   }

   public static PortalRenderer getInstance() {
      if (instance == null) {
         instance = new PortalRenderer();
      }

      return instance;
   }

   private void renderMask(PortalEntity portal, Matrix4f model, Matrix4f view, Matrix4f projectionMatrix) {
      ((Shader)ShaderInit.PORTAL_MASK.get()).bind().setMatrix("model", model).setMatrix("view", view).setMatrix("projection", projectionMatrix);
      this.setupShaderClipPlane(ShaderInit.PORTAL_MASK.get(), (PortalEntity)this.portalStack.peekFirst());
      int age = portal.getAge();
      boolean spawning = age < 4;
      String path = "textures/portal/mask" + (spawning ? "_spawning" + age : "") + ".png";
      GL11.glEnable(3553);
      RenderSystem.activeTexture(33984);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(new ResourceLocation("portalmod", path));
      RenderSystem.colorMask(false, false, false, false);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      GL11.glEnable(3008);
      GL11.glEnable(34383);
      RenderSystem.depthMask(false);
      portalMesh.render();
      RenderSystem.depthMask(true);
      GL11.glDisable(34383);
      GL11.glDisable(3008);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.bindTexture(0);
      this.unbindBuffer();
      ((Shader)ShaderInit.PORTAL_MASK.get()).unbind();
   }

   private void renderBackground() {
      ((Shader)ShaderInit.COLOR.get()).bind().setFloat("color", (float)this.clearColor.x, (float)this.clearColor.y, (float)this.clearColor.z, 1.0F);
      RenderSystem.depthFunc(519);
      screenQuad.render();
      RenderSystem.depthFunc(513);
      RenderSystem.bindTexture(0);
      this.unbindBuffer();
      ((Shader)ShaderInit.COLOR.get()).unbind();
   }

   private void renderDepth(Matrix4f modelView) {
      GL20.glUseProgram(0);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      GL11.glEnable(3008);
      GL11.glEnable(34383);
      RenderSystem.depthFunc(519);
      RenderSystem.colorMask(false, false, false, false);
      portalMesh.render(new Mat4(modelView));
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.depthFunc(513);
      GL11.glDisable(34383);
      GL11.glDisable(3008);
      RenderSystem.bindTexture(0);
      this.unbindBuffer();
   }

   private void renderBorder(PortalEntity portal, Matrix4f model, Matrix4f view, Matrix4f projectionMatrix) {
      Minecraft mc = Minecraft.func_71410_x();
      if (mc.field_71439_g != null) {
         int ticks = mc.field_71439_g.field_70173_aa;
         int age = portal.getAge();
         boolean open = portal.isOpen() && this.recursion <= (Integer)PortalModConfigManager.RECURSION.get();
         boolean spawning = age < 4;
         String path = "textures/portal/" + (open ? "open_" : "closed_") + portal.getColor() + (spawning ? "_spawning" : "") + ".png";
         ResourceLocation location = new ResourceLocation("portalmod", path);
         Optional<Dimension> optionalTextureSize = PortalAnimatedTextureHelper.getTextureSize(location);
         if (optionalTextureSize.isPresent()) {
            Dimension textureSize = (Dimension)optionalTextureSize.get();
            int frameCount = (int)textureSize.getHeight() / (2 * (int)textureSize.getWidth());
            int frameIndex;
            if (spawning) {
               frameIndex = age % frameCount;
            } else {
               int frameTime = 1;
               frameIndex = ticks / 1 % frameCount;
            }

            ((Shader)ShaderInit.PORTAL_FRAME.get()).bind().setInt("frameCount", frameCount).setInt("frameIndex", frameIndex).setMatrix("model", model).setMatrix("view", view).setMatrix("projection", projectionMatrix);
            this.setupShaderClipPlane(ShaderInit.PORTAL_FRAME.get(), (PortalEntity)this.portalStack.peekFirst());
            RenderUtil.bindTexture(ShaderInit.PORTAL_FRAME.get(), "texture", path, 0);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.depthMask(false);
            portalMesh.render();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.bindTexture(0);
            this.unbindBuffer();
            ((Shader)ShaderInit.PORTAL_FRAME.get()).unbind();
         }
      }
   }

   public void renderPortals(ClientWorld level, ActiveRenderInfo camera, ClippingHelper clippingHelper, Matrix4f projectionMatrix, float partialTicks) {
      Minecraft mc = Minecraft.func_71410_x();
      Framebuffer mainFBO = mc.func_147110_a();
      boolean isOuter = this.recursion == 0;
      if (isOuter) {
         PROFILE.enabled = portalProfilerEnabled();
         if (PROFILE.enabled) {
            PROFILE.reset();
            PROFILE.push("frame");
         }

         this.portalsCulledByNdcRect = 0;
         this.portalsStencilSkippedOccludedRays = 0;
         this.parentNdcRectStack.clear();
         this.currentParentNdcRect[0] = -1.0F;
         this.currentParentNdcRect[1] = -1.0F;
         this.currentParentNdcRect[2] = 1.0F;
         this.currentParentNdcRect[3] = 1.0F;
      }

      PROFILE.push("portals@r" + this.recursion);
      mc.field_71438_f.field_228415_m_.func_228487_b_().func_228461_a_();
      if (this.recursion == 0) {
         this.currentlyRenderingPortals = true;
         this.fabulousGraphics = mc.field_71474_y.field_238330_f_ == GraphicsFanciness.FABULOUS;
         if (this.fabulousGraphics) {
            int w = mc.func_228018_at_().func_198109_k();
            int h = mc.func_228018_at_().func_198091_l();
            if (tempFBO.field_147622_a != w || tempFBO.field_147620_b != h) {
               tempFBO.func_216491_a(w, h, Minecraft.field_142025_a);
            }

            tempFBO.enableStencil();
            tempFBO.func_147610_a(false);
            GL11.glClear(16640);
         }

         mainFBO.func_147610_a(false);
         GL11.glEnable(2960);
         RenderSystem.stencilMask(128);
         RenderSystem.clear(1024, false);
      }

      this.renderedPortals = 0;

      for(Entity entity : level.func_217416_b()) {
         if (entity instanceof PortalEntity) {
            PROFILE.push("renderPortal");
            this.renderPortal((PortalEntity)entity, camera, clippingHelper, projectionMatrix, partialTicks, this.fabulousGraphics);
            PROFILE.pop();
            ++this.renderedPortals;
         }
      }

      if (this.recursion == 0) {
         this.currentlyRenderingPortals = false;
      }

      if (this.fabulousGraphics) {
         PROFILE.push("fabulousBlit@r" + this.recursion);
         this.blitFBOtoFBO(mainFBO, tempFBO);
         tempFBO.func_237506_a_(mainFBO);
         mainFBO.func_147610_a(false);
         PROFILE.pop();
      }

      if ((Boolean)PortalModConfigManager.HIGHLIGHTS.get()) {
         PROFILE.push("highlights");
         this.renderHighlights(camera, projectionMatrix);
         PROFILE.pop();
      }

      if (this.recursion == 0 && !this.fabulousGraphics) {
         mainFBO.func_147610_a(false);
         GL11.glEnable(2960);
         RenderSystem.stencilMask(255);
         RenderSystem.clear(1024, false);
      }

      GL11.glEnable(3008);
      PROFILE.pop();
      if (isOuter) {
         PROFILE.pop();
         PROFILE.snapshotInto(ClientEvents.debugStrings);
      }

   }

   private boolean discardPortal(PortalEntity portal, ActiveRenderInfo camera, ClippingHelper clippingHelper, Matrix4f projectionMatrix) {
      Vec3 cameraPos = new Vec3(camera.func_216785_c());
      Vec3 portalPos = new Vec3(portal.func_213303_ch());
      Vec3 portalToCamera = cameraPos.sub(portalPos);
      Vec3 portalNormal = new Vec3(portal.func_174811_aO());
      if (portalToCamera.magnitude() > (double)1.0F && portalToCamera.clone().normalize().dot(portalNormal) < (double)0.0F) {
         return true;
      } else {
         if (!this.portalStack.isEmpty()) {
            PortalEntity parentPortal = (PortalEntity)this.portalStack.peek();
            Vec3 parentPortalPos = new Vec3(parentPortal.func_213303_ch());
            Vec3 parentPortalNormal = new Vec3(parentPortal.func_174811_aO());
            Vec3 parentPortalPosWithMargin = parentPortalPos.clone().sub(parentPortalNormal.clone().mul((double)2.0F));
            Vec3 parentPortalToPortal = portalPos.clone().sub(parentPortalPosWithMargin);
            if (parentPortalToPortal.normalize().dot(parentPortalNormal) < (double)0.0F) {
               return true;
            }

            if (portal == this.portalStack.peek()) {
               return true;
            }
         }

         if (portalToCamera.magnitude() > (double)1.0F && !clippingHelper.func_228957_a_(portal.func_174813_aQ())) {
            return true;
         } else {
            if (this.recursion >= 1 && portalToCamera.magnitude() > (double)1.5F) {
               float[] rect = this.computePortalNdcRect(portal, camera, projectionMatrix);
               if (rect != null && !rectsIntersect(rect, this.currentParentNdcRect)) {
                  ++this.portalsCulledByNdcRect;
                  return true;
               }
            }

            return false;
         }
      }
   }

   private float[] computePortalNdcRect(PortalEntity portal, ActiveRenderInfo camera, Matrix4f projectionMatrix) {
      AxisAlignedBB bb = portal.func_174813_aQ();
      Matrix4f view = this.getViewMatrix(camera);
      Matrix4f mvp = projectionMatrix.func_226601_d_();
      mvp.func_226595_a_(view);
      float xMin = Float.POSITIVE_INFINITY;
      float yMin = Float.POSITIVE_INFINITY;
      float xMax = Float.NEGATIVE_INFINITY;
      float yMax = Float.NEGATIVE_INFINITY;
      double[] xs = new double[]{bb.field_72340_a, bb.field_72336_d};
      double[] ys = new double[]{bb.field_72338_b, bb.field_72337_e};
      double[] zs = new double[]{bb.field_72339_c, bb.field_72334_f};

      for(double x : xs) {
         for(double y : ys) {
            for(double z : zs) {
               Vector4f corner = new Vector4f((float)x, (float)y, (float)z, 1.0F);
               corner.func_229372_a_(mvp);
               float w = corner.func_195915_d();
               if (w <= 0.01F) {
                  return null;
               }

               float nx = corner.func_195910_a() / w;
               float ny = corner.func_195913_b() / w;
               if (nx < xMin) {
                  xMin = nx;
               }

               if (nx > xMax) {
                  xMax = nx;
               }

               if (ny < yMin) {
                  yMin = ny;
               }

               if (ny > yMax) {
                  yMax = ny;
               }
            }
         }
      }

      if (xMin < -1.0F) {
         xMin = -1.0F;
      }

      if (yMin < -1.0F) {
         yMin = -1.0F;
      }

      if (xMax > 1.0F) {
         xMax = 1.0F;
      }

      if (yMax > 1.0F) {
         yMax = 1.0F;
      }

      return new float[]{xMin, yMin, xMax, yMax};
   }

   private static void portalOpeningControlPoints(PortalEntity portal, Vector3d[] out) {
      OrthonormalBasis basis = portal.getSourceBasis();
      Vec3 rightUnit = basis.getX().normalize();
      Vec3 upUnit = basis.getY().normalize();
      double rx = rightUnit.x * (double)0.5F;
      double ry = rightUnit.y * (double)0.5F;
      double rz = rightUnit.z * (double)0.5F;
      double ux = upUnit.x;
      double uy = upUnit.y;
      double uz = upUnit.z;
      Vector3d center = portal.func_174813_aQ().func_189972_c();
      int idx = 0;

      for(int row = 0; row < 16; ++row) {
         double b = portalOpeningGridCoord(16, row);

         for(int col = 0; col < 8; ++col) {
            double a = portalOpeningGridCoord(8, col);
            out[idx++] = new Vector3d(center.field_72450_a + a * rx + b * ux, center.field_72448_b + a * ry + b * uy, center.field_72449_c + a * rz + b * uz);
         }
      }

   }

   private static double portalOpeningGridCoord(int dim, int index) {
      return dim <= 1 ? (double)0.0F : (double)-1.0F + (double)2.0F * (double)index / (double)(dim - 1);
   }

   private static boolean blockCountsAsOpaqueForPortalSight(BlockState state) {
      if (state.func_196958_f()) {
         return false;
      } else if (!state.func_200132_m()) {
         return false;
      } else {
         return state.func_185904_a().func_76230_c();
      }
   }

   private static boolean cornerVisibleAlongRay(ClientWorld world, Vector3d from, Vector3d corner, @Nullable Entity viewer, boolean emitDebugParticles) {
      RayTraceContext ctx = new RayTraceContext(from, corner, BlockMode.COLLIDER, FluidMode.NONE, viewer);
      BlockRayTraceResult hit = ModUtil.customClip(world, ctx, (pos) -> blockCountsAsOpaqueForPortalSight(world.func_180495_p(pos)) ? Optional.empty() : EMPTY_SHAPE_OVERRIDE);
      boolean visible;
      if (hit.func_216346_c() == Type.MISS) {
         visible = true;
      } else {
         double distCorner = from.func_72438_d(corner);
         double distHit = from.func_72438_d(hit.func_216347_e());
         visible = distHit >= distCorner - 0.08;
      }

      if (emitDebugParticles) {
         RedstoneParticleData cornerDust = visible ? new RedstoneParticleData(0.1F, 1.0F, 0.2F, 0.2F) : new RedstoneParticleData(1.0F, 0.15F, 0.1F, 0.2F);
         world.func_195594_a(cornerDust, corner.field_72450_a, corner.field_72448_b, corner.field_72449_c, (double)0.0F, (double)0.0F, (double)0.0F);
         if (!visible && hit.func_216346_c() != Type.MISS) {
            Vector3d h = hit.func_216347_e();
            RedstoneParticleData hitDust = new RedstoneParticleData(1.0F, 0.9F, 0.2F, 0.2F);
            world.func_195594_a(hitDust, h.field_72450_a, h.field_72448_b, h.field_72449_c, (double)0.0F, (double)0.0F, (double)0.0F);
         }
      }

      return visible;
   }

   public boolean portalOpeningFullyOccluded(PortalEntity portal, ActiveRenderInfo camera) {
      ClientWorld world = Minecraft.func_71410_x().field_71441_e;
      if (world == null) {
         return false;
      } else {
         Vector3d from = camera.func_216785_c();
         Entity viewer = camera.func_216773_g();
         if (viewer == null) {
            viewer = Minecraft.func_71410_x().field_71439_g;
         }

         Vector3d[] points = new Vector3d[128];
         portalOpeningControlPoints(portal, points);
         boolean emitDebugParticles = false;
         if (PROFILE.enabled) {
            long gt = world.func_82737_E();
            UUID id = portal.func_110124_au();
            Long lastTick = (Long)PROFILER_PORTAL_OCCLUSION_LAST_PARTICLE_TICK.get(id);
            if (lastTick == null || lastTick != gt) {
               PROFILER_PORTAL_OCCLUSION_LAST_PARTICLE_TICK.put(id, gt);
               emitDebugParticles = true;
            }
         }

         boolean anyVisible = false;

         for(Vector3d p : points) {
            boolean v = cornerVisibleAlongRay(world, from, p, viewer, emitDebugParticles);
            if (v) {
               anyVisible = true;
               if (!emitDebugParticles) {
                  return false;
               }
            }
         }

         return !anyVisible;
      }
   }

   private static boolean rectsIntersect(float[] a, float[] b) {
      return !(a[2] < b[0]) && !(a[0] > b[2]) && !(a[3] < b[1]) && !(a[1] > b[3]);
   }

   private static float[] intersectRect(float[] a, float[] b) {
      float xMin = Math.max(a[0], b[0]);
      float yMin = Math.max(a[1], b[1]);
      float xMax = Math.min(a[2], b[2]);
      float yMax = Math.min(a[3], b[3]);
      return !(xMin >= xMax) && !(yMin >= yMax) ? new float[]{xMin, yMin, xMax, yMax} : null;
   }

   private void finishPortalEntity(PortalEntity portal, ActiveRenderInfo camera, float partialTicks, boolean fabulousGraphics) {
      if (fabulousGraphics) {
         GL11.glDisable(2960);
      }

      if (!this.isShallowest()) {
         RenderUtil.setStandardClipPlane(this.clipMatrix.func_227866_c_().func_227870_a_());
      } else {
         GL11.glDisable(12288);
      }

      ActiveRenderInfo fogCamera = camera;
      if (portal.getOtherPortal().isPresent()) {
         fogCamera = new PortalCamera(camera, partialTicks);
         fogCamera.func_216774_a(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch());
      }

      this.setupSkyAndFog(fogCamera, partialTicks);
      this.portalChain.removeLast();
      --this.recursion;
   }

   private ActiveRenderInfo setupCamera(ActiveRenderInfo camera, PortalEntity portal, float partialTicks) {
      if (!portal.getOtherPortal().isPresent()) {
         return camera;
      } else {
         Mat4 portalToPortalRotationMatrix = PortalEntity.getPortalToPortalRotationMatrix(portal, (PortalEntity)portal.getOtherPortal().get());
         Mat4 portalToPortalMatrix = PortalEntity.getPortalToPortalMatrix(portal, (PortalEntity)portal.getOtherPortal().get());
         Vec3 newCameraPos = (new Vec3(camera.func_216785_c())).transform(portalToPortalMatrix);
         float xRot = camera.func_216777_e();
         float yRot = camera.func_216778_f();
         float zRot = camera instanceof PortalCamera ? ((PortalCamera)camera).getRoll() : PMState.cameraRoll;
         OrthonormalBasis basis = EulerConverter.toVectors(xRot, yRot, zRot);
         basis.transform(portalToPortalRotationMatrix);
         EulerConverter.EulerAngles angles = EulerConverter.toEulerAnglesLeastRoll(basis);
         return new PortalCamera(Minecraft.func_71410_x().field_71441_e, camera.func_216773_g(), newCameraPos, angles.getPitch(), angles.getYaw(), angles.getRoll(), partialTicks);
      }
   }

   private void setupMatrixStack(MatrixStack matrixStack, ActiveRenderInfo camera) {
      if (camera instanceof PortalCamera) {
         PortalCamera portalCamera = (PortalCamera)camera;
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(portalCamera.getRoll()));
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(portalCamera.func_216777_e()));
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(portalCamera.func_216778_f() + 180.0F));
      }

   }

   private void blitFBOtoFBO(Framebuffer src, Framebuffer dest) {
      ((Shader)ShaderInit.ACTUAL_BLIT.get()).bind().setMatrix("projection", Matrix4f.func_226593_a_(1.0F, 1.0F, 1.0F)).setInt("texture", 0);
      RenderSystem.disableBlend();
      RenderSystem.disableAlphaTest();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.activeTexture(33984);
      src.func_147612_c();
      dest.func_147610_a(false);
      blitQuad.render();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      ((Shader)ShaderInit.ACTUAL_BLIT.get()).unbind();
   }

   private void setupSkyAndFog(ActiveRenderInfo camera, float partialTicks) {
      Minecraft mc = Minecraft.func_71410_x();
      if (mc.field_71441_e != null) {
         FogRenderer.func_228371_a_(camera, partialTicks, mc.field_71441_e, mc.field_71474_y.field_151451_c, mc.field_71460_t.func_205002_d(partialTicks));
         float renderDistance = mc.field_71460_t.func_205001_m();
         boolean hasFog = mc.field_71441_e.func_239132_a_().func_230493_a_(MathHelper.func_76141_d((float)camera.func_216780_d().func_177958_n()), MathHelper.func_76141_d((float)camera.func_216780_d().func_177956_o())) || mc.field_71456_v.func_184046_j().func_184056_f();
         if (Minecraft.func_71410_x().field_71474_y.field_151451_c >= 4) {
            FogRenderer.setupFog(camera, FogType.FOG_SKY, renderDistance, hasFog, partialTicks);
         }

         FogRenderer.setupFog(camera, FogType.FOG_TERRAIN, Math.max(renderDistance - 16.0F, 32.0F), hasFog, partialTicks);
      }

   }

   private void renderPortal(PortalEntity portal, ActiveRenderInfo camera, ClippingHelper clippingHelper, Matrix4f projectionMatrix, float partialTicks, boolean fabulousGraphics) {
      ++this.recursion;
      this.portalChain.addLast(portal);
      if (PROFILE.enabled) {
         ++PROFILE.portalsVisited;
         if (this.recursion > PROFILE.maxRecursionReached) {
            PROFILE.maxRecursionReached = this.recursion;
         }
      }

      PROFILE.push("discardPortal");
      boolean culled = this.discardPortal(portal, camera, clippingHelper, projectionMatrix);
      PROFILE.pop();
      if (culled) {
         if (PROFILE.enabled) {
            ++PROFILE.portalsCulled;
         }

         this.finishPortalEntity(portal, camera, partialTicks, fabulousGraphics);
      } else {
         boolean openingOccluded = this.recursion == 1 && this.portalOpeningFullyOccluded(portal, camera);
         if (openingOccluded) {
            ++this.portalsStencilSkippedOccludedRays;
            this.finishPortalEntity(portal, camera, partialTicks, fabulousGraphics);
         } else {
            Minecraft mc = Minecraft.func_71410_x();
            Framebuffer mainFBO = mc.func_147110_a();
            Matrix4f modelMatrix = this.getModelMatrix(portal, camera, portal.getWallAttachmentDistance(camera));
            Matrix4f viewMatrix = this.getViewMatrix(camera);
            Matrix4f modelView = viewMatrix.func_226601_d_();
            modelView.func_226595_a_(modelMatrix);
            GL11.glEnable(2960);
            Optional<PortalEntity> otherPortalOptional = portal.getOtherPortal();
            MatrixStack matrixStack = new MatrixStack();
            ActiveRenderInfo portalCamera = this.setupCamera(camera, portal, partialTicks);
            this.setupMatrixStack(matrixStack, portalCamera);
            this.setupSkyAndFog(portalCamera, partialTicks);
            ActiveRenderInfo fogCamera = portalCamera;
            if (portal.getOtherPortal().isPresent()) {
               fogCamera = new PortalCamera(portalCamera, partialTicks);
               fogCamera.func_216774_a(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch());
            }

            FogRenderer.func_228371_a_(fogCamera, partialTicks, mc.field_71441_e, mc.field_71474_y.field_151451_c, mc.field_71460_t.func_205002_d(partialTicks));
            RenderSystem.stencilMask(127);
            RenderSystem.stencilFunc(514, this.recursion - 1, 127);
            RenderSystem.stencilOp(7680, 7680, 7682);
            PROFILE.push("mask(INCR)");
            this.renderMask(portal, modelMatrix, viewMatrix, projectionMatrix);
            PROFILE.pop();
            RenderSystem.stencilMask(0);
            RenderSystem.stencilFunc(514, this.recursion, 127);
            RenderSystem.stencilOp(7680, 7680, 7680);
            PROFILE.push("background");
            this.renderBackground();
            PROFILE.pop();
            boolean canNestRender = otherPortalOptional.isPresent() && !this.isDeepest();
            if (canNestRender) {
               PortalEntity otherPortal = (PortalEntity)otherPortalOptional.get();
               this.portalStack.push(otherPortal);
               float[] savedParentNdcRect = new float[]{this.currentParentNdcRect[0], this.currentParentNdcRect[1], this.currentParentNdcRect[2], this.currentParentNdcRect[3]};
               this.parentNdcRectStack.push(savedParentNdcRect);
               float[] portalRect = this.computePortalNdcRect(portal, camera, projectionMatrix);
               if (portalRect != null) {
                  float[] narrowed = intersectRect(savedParentNdcRect, portalRect);
                  if (narrowed != null) {
                     this.currentParentNdcRect[0] = narrowed[0];
                     this.currentParentNdcRect[1] = narrowed[1];
                     this.currentParentNdcRect[2] = narrowed[2];
                     this.currentParentNdcRect[3] = narrowed[3];
                  }
               }

               this.clipMatrix.func_227860_a_();
               RenderUtil.setupClipPlane(this.clipMatrix, portal, camera, 0.0F, false);
               this.currentCamera = portalCamera;
               Vec3 oldCameraPosOverrideForRenderingSelf = PMState.cameraPosOverrideForRenderingSelf;
               PMState.cameraPosOverrideForRenderingSelf = PMState.cameraPosOverrideForRenderingSelf == null ? null : PMState.cameraPosOverrideForRenderingSelf.clone().transform(PortalEntity.getPortalToPortalMatrix(portal, otherPortal));
               PROFILE.push("saveRenderChunks");
               ObjectList<WorldRenderer.LocalRenderInformationContainer> renderChunks = new ObjectArrayList();
               renderChunks.addAll(mc.field_71438_f.field_72755_R);
               PROFILE.pop();
               boolean renderOutline = this.shouldRenderOutline(this.portalChain);
               if (PROFILE.enabled) {
                  ++PROFILE.portalsNested;
               }

               LevelRendererAccessor lvlAcc = (LevelRendererAccessor)mc.field_71438_f;
               int origLastViewDistance = lvlAcc.pmGetLastViewDistance();
               int shift = Math.min(this.recursion, 5);
               int clampedDistance = Math.max(2, origLastViewDistance >> shift);
               boolean distanceClamped = clampedDistance < origLastViewDistance;
               int previousOverride = this.nestedBfsDistanceOverride;
               if (distanceClamped) {
                  lvlAcc.pmSetLastViewDistance(clampedDistance);
                  this.nestedBfsDistanceOverride = clampedDistance;
               }

               PROFILE.push("nestedRenderLevel@r" + this.recursion);

               try {
                  mc.field_71438_f.func_228426_a_(matrixStack, partialTicks, Util.func_211178_c(), renderOutline, portalCamera, mc.field_71460_t, mc.field_71460_t.field_78513_d, projectionMatrix);
               } finally {
                  if (distanceClamped) {
                     lvlAcc.pmSetLastViewDistance(origLastViewDistance);
                     this.nestedBfsDistanceOverride = previousOverride;
                  }

                  PROFILE.pop();
               }

               PROFILE.push("restoreRenderChunks");
               mc.field_71438_f.field_147595_R = true;
               mc.field_71438_f.field_72755_R.clear();
               mc.field_71438_f.field_72755_R.addAll(renderChunks);
               TileEntityRendererDispatcher.field_147556_a.func_217665_a(portal.field_70170_p, mc.func_110434_K(), mc.field_71466_p, camera, mc.field_71476_x);
               mc.field_71438_f.field_175010_j.func_229088_a_(portal.field_70170_p, camera, mc.field_147125_j);
               PROFILE.pop();
               this.currentCamera = camera;
               PMState.cameraPosOverrideForRenderingSelf = oldCameraPosOverrideForRenderingSelf;
               this.clipMatrix.func_227865_b_();
               this.portalStack.pop();
               float[] restored = (float[])this.parentNdcRectStack.pop();
               this.currentParentNdcRect[0] = restored[0];
               this.currentParentNdcRect[1] = restored[1];
               this.currentParentNdcRect[2] = restored[2];
               this.currentParentNdcRect[3] = restored[3];
               if (fabulousGraphics) {
                  GL11.glEnable(2960);
                  RenderSystem.stencilMask(0);
                  RenderSystem.stencilFunc(517, this.recursion, 127);
                  RenderSystem.stencilOp(7680, 7680, 7680);
                  PROFILE.push("fabulousBlit(nested)");
                  this.blitFBOtoFBO(tempFBO, mainFBO);
                  mainFBO.func_237506_a_(tempFBO);
                  mainFBO.func_147610_a(false);
                  PROFILE.pop();
               }
            }

            GL11.glDisable(12288);
            GL11.glEnable(2960);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.stencilMask(128);
            RenderSystem.stencilFunc(514, this.recursion, 255);
            RenderSystem.stencilOp(7680, 7680, 5386);
            PROFILE.push("mask(INVERT)");
            this.renderMask(portal, modelMatrix, viewMatrix, projectionMatrix);
            PROFILE.pop();
            RenderSystem.stencilMask(0);
            RenderSystem.stencilFunc(514, this.recursion, 127);
            RenderSystem.stencilOp(7680, 7680, 7680);
            PROFILE.push("border");
            this.renderBorder(portal, modelMatrix, viewMatrix, projectionMatrix);
            PROFILE.pop();
            RenderSystem.stencilMask(127);
            RenderSystem.stencilFunc(514, this.recursion, 127);
            RenderSystem.stencilOp(7680, 7680, 7683);
            PROFILE.push("depth(DECR)");
            this.renderDepth(modelView);
            PROFILE.pop();
            if (!fabulousGraphics) {
               RenderSystem.stencilMask(0);
               RenderSystem.stencilFunc(514, this.recursion - 1, 127);
               RenderSystem.stencilOp(7680, 7680, 7680);
            }

            this.finishPortalEntity(portal, camera, partialTicks, fabulousGraphics);
         }
      }
   }

   public boolean shouldRenderOutline(@Nullable Deque<PortalEntity> portalChain) {
      if (portalChain != null && this.outlineRenderingPortalChain != null) {
         if (portalChain.size() != this.outlineRenderingPortalChain.size()) {
            return false;
         } else {
            Iterator<PortalEntity> iterator = this.outlineRenderingPortalChain.iterator();

            for(PortalEntity portal : portalChain) {
               if (portal != iterator.next()) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return (portalChain == null || portalChain.isEmpty()) && (this.outlineRenderingPortalChain == null || this.outlineRenderingPortalChain.isEmpty());
      }
   }

   private boolean isDeepest() {
      return this.recursion > (Integer)PortalModConfigManager.RECURSION.get();
   }

   private boolean isShallowest() {
      return this.recursion <= 1;
   }

   public void renderHighlights(ActiveRenderInfo camera, Matrix4f projectionMatrix) {
      ClientWorld level = Minecraft.func_71410_x().field_71441_e;
      ClientPlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (level != null && player != null) {
         GL11.glEnable(2960);
         RenderSystem.stencilMask(0);
         RenderSystem.stencilFunc(514, this.recursion, 255);
         RenderSystem.stencilOp(7680, 7680, 7680);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 771);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(516);
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();

         for(Entity entity : level.func_217416_b()) {
            if (entity instanceof PortalEntity) {
               PortalEntity portal = (PortalEntity)entity;
               Vector3d cameraPos = Minecraft.func_71410_x().field_71460_t.func_215316_n().func_216785_c();
               if (this.currentCamera != null) {
                  cameraPos = this.currentCamera.func_216785_c();
               }

               ItemStack item = player.func_184614_ca();
               Optional<UUID> gunUUID = PortalGun.getUUID(item);
               if (item.func_77973_b() instanceof PortalGun && gunUUID.isPresent() && ((UUID)gunUUID.get()).equals(portal.getGunUUID())) {
                  Matrix4f model = this.getModelMatrix(portal, camera, portal.getWallAttachmentDistance(camera) * 3.0F);
                  Matrix4f view = this.getViewMatrix(camera);
                  ((Shader)ShaderInit.PORTAL_HIGHLIGHT.get()).bind().setMatrix("model", model).setMatrix("view", view).setMatrix("projection", projectionMatrix).setFloat("intensity", (float)portal.func_213303_ch().func_72438_d(cameraPos));
                  this.setupShaderClipPlane(ShaderInit.PORTAL_HIGHLIGHT.get(), (PortalEntity)this.portalStack.peekFirst());
                  RenderUtil.bindTexture(ShaderInit.PORTAL_HIGHLIGHT.get(), "texture", "textures/portal/highlight_" + portal.getColor() + ".png", 0);
                  portalMesh.render();
               }
            }
         }

         RenderSystem.enableCull();
         RenderSystem.depthFunc(513);
         RenderSystem.depthMask(true);
         RenderSystem.bindTexture(0);
         RenderSystem.disableBlend();
         this.unbindBuffer();
         ((Shader)ShaderInit.PORTAL_HIGHLIGHT.get()).unbind();
         if (this.fabulousGraphics) {
            GL11.glDisable(2960);
         } else {
            RenderSystem.stencilMask(0);
            RenderSystem.stencilFunc(514, this.recursion, 127);
            RenderSystem.stencilOp(7680, 7680, 7680);
         }

      }
   }

   private void unbindBuffer() {
      DefaultVertexFormats.field_181707_g.func_227895_d_();
      VertexBuffer.func_177361_b();
   }

   private void setupShaderClipPlane(Shader shader, @Nullable PortalEntity portal) {
      if (portal == null) {
         shader.bind().setInt("clipPlaneEnabled", 0);
      } else {
         Vec3 pos = new Vec3(portal.func_213303_ch());
         Vec3 vec = new Vec3(portal.func_174811_aO());
         shader.bind().setInt("clipPlaneEnabled", 1).setFloat("clipVec", (float)vec.x, (float)vec.y, (float)vec.z).setFloat("clipPos", (float)pos.x, (float)pos.y, (float)pos.z);
      }
   }

   private Matrix4f getModelMatrix(PortalEntity portal, ActiveRenderInfo camera, float offset) {
      Vector3i portalNormal = portal.func_174811_aO().func_176730_m();
      MatrixStack matrix = new MatrixStack();
      Vec3 offsetNormal = (new Vec3(camera.func_216785_c())).sub(portal.func_213303_ch()).normalize().mul((double)offset);
      matrix.func_227861_a_(offsetNormal.x, offsetNormal.y, offsetNormal.z);
      matrix.func_227861_a_((double)((float)portalNormal.func_177958_n() * 1.0E-4F), (double)((float)portalNormal.func_177956_o() * 1.0E-4F), (double)((float)portalNormal.func_177952_p() * 1.0E-4F));
      PortalEntity.setupMatrix(matrix, portal.func_174811_aO(), portal.getUpVector(), portal.getPivotPoint());
      return matrix.func_227866_c_().func_227870_a_();
   }

   private Matrix4f getViewMatrix(ActiveRenderInfo camera) {
      Vector3d cameraPos = camera.func_216785_c();
      MatrixStack matrix = new MatrixStack();
      float roll = camera instanceof PortalCamera ? ((PortalCamera)camera).getRoll() : PMState.cameraRoll;
      matrix.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(roll));
      matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(camera.func_216777_e()));
      matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(camera.func_216778_f() + 180.0F));
      matrix.func_227861_a_(-cameraPos.field_72450_a, -cameraPos.field_72448_b, -cameraPos.field_72449_c);
      return matrix.func_227866_c_().func_227870_a_();
   }

   public ActiveRenderInfo getCurrentCamera() {
      return this.currentCamera == null ? Minecraft.func_71410_x().field_71460_t.func_215316_n() : this.currentCamera;
   }

   static {
      portalMesh = new VertexRenderer(DefaultVertexFormats.field_181707_g, 7);
      screenQuad = new VertexRenderer(DefaultVertexFormats.field_181707_g, 7);
      blitQuad = new VertexRenderer(DefaultVertexFormats.field_181707_g, 7);
      tempFBO = new Framebuffer(Minecraft.func_71410_x().func_228018_at_().func_198109_k(), Minecraft.func_71410_x().func_228018_at_().func_198091_l(), true, Minecraft.field_142025_a);
      EMPTY_SHAPE_OVERRIDE = Optional.of(VoxelShapes.func_197880_a());
      PROFILE = new Profile();
      portalMesh.reset();
      portalMesh.data((bufferBuilder) -> {
         bufferBuilder.func_225582_a_((double)0.0F, (double)0.0F, (double)0.0F).func_225583_a_(0.0F, 0.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)1.0F, (double)0.0F, (double)0.0F).func_225583_a_(1.0F, 0.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)1.0F, (double)2.0F, (double)0.0F).func_225583_a_(1.0F, 1.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)0.0F, (double)2.0F, (double)0.0F).func_225583_a_(0.0F, 1.0F).func_181675_d();
      });
      screenQuad.reset();
      screenQuad.data((bufferBuilder) -> {
         bufferBuilder.func_225582_a_((double)-1.0F, (double)-1.0F, (double)1.0F).func_225583_a_(0.0F, 0.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)1.0F, (double)-1.0F, (double)1.0F).func_225583_a_(1.0F, 0.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)1.0F, (double)1.0F, (double)1.0F).func_225583_a_(1.0F, 1.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)-1.0F, (double)1.0F, (double)1.0F).func_225583_a_(0.0F, 1.0F).func_181675_d();
      });
      blitQuad.reset();
      blitQuad.data((bufferBuilder) -> {
         bufferBuilder.func_225582_a_((double)-1.0F, (double)-1.0F, (double)0.0F).func_225583_a_(0.0F, 0.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)1.0F, (double)-1.0F, (double)0.0F).func_225583_a_(1.0F, 0.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)1.0F, (double)1.0F, (double)0.0F).func_225583_a_(1.0F, 1.0F).func_181675_d();
         bufferBuilder.func_225582_a_((double)-1.0F, (double)1.0F, (double)0.0F).func_225583_a_(0.0F, 1.0F).func_181675_d();
      });
      PROFILER_PORTAL_OCCLUSION_LAST_PARTICLE_TICK = new HashMap();
   }

   public static final class Profile {
      public boolean enabled = false;
      public int topN = 12;
      private final LinkedHashMap<String, Entry> entries = new LinkedHashMap();
      private final Deque<String> labelStack = new ArrayDeque();
      private final Deque<Long> startStack = new ArrayDeque();
      public int portalsVisited;
      public int portalsCulled;
      public int portalsNested;
      public int maxRecursionReached;

      public void push(String label) {
         if (this.enabled) {
            this.labelStack.push(label);
            this.startStack.push(System.nanoTime());
         }
      }

      public void pop() {
         if (this.enabled) {
            if (!this.labelStack.isEmpty() && !this.startStack.isEmpty()) {
               long elapsed = System.nanoTime() - (Long)this.startStack.pop();
               String label = (String)this.labelStack.pop();
               Entry e = (Entry)this.entries.get(label);
               if (e == null) {
                  e = new Entry();
                  this.entries.put(label, e);
               }

               e.totalNs += elapsed;
               ++e.calls;
               if (elapsed > e.maxNs) {
                  e.maxNs = elapsed;
               }

            }
         }
      }

      public void reset() {
         this.entries.clear();
         this.labelStack.clear();
         this.startStack.clear();
         this.portalsVisited = 0;
         this.portalsCulled = 0;
         this.portalsNested = 0;
         this.maxRecursionReached = 0;
      }

      private static String fmt(long ns) {
         double ms = (double)ns / (double)1000000.0F;
         return String.format(Locale.ROOT, "%6.2fms", ms);
      }

      public void snapshotInto(List<String> out) {
         if (this.enabled) {
            Entry frame = (Entry)this.entries.get("frame");
            long frameNs = frame == null ? 1L : Math.max(1L, frame.totalNs);
            PortalRenderer pr = PortalRenderer.getInstance();
            out.add(String.format(Locale.ROOT, "PortalProfile %s | portals v=%d c=%d(ndc=%d) nested=%d occ=%d depth=%d/max%d", fmt(frameNs), this.portalsVisited, this.portalsCulled, pr.portalsCulledByNdcRect, this.portalsNested, pr.portalsStencilSkippedOccludedRays, this.maxRecursionReached, PortalModConfigManager.RECURSION.get()));
            List<Map.Entry<String, Entry>> sorted = new ArrayList(this.entries.entrySet());
            sorted.sort((a, b) -> Long.compare(((Entry)b.getValue()).totalNs, ((Entry)a.getValue()).totalNs));
            int shown = 0;

            for(Map.Entry<String, Entry> kv : sorted) {
               if (shown++ >= this.topN) {
                  break;
               }

               Entry e = (Entry)kv.getValue();
               double pct = (double)e.totalNs * (double)100.0F / (double)frameNs;
               out.add(String.format(Locale.ROOT, "%-28s %s x%-3d max %s %5.1f%%", kv.getKey(), fmt(e.totalNs), e.calls, fmt(e.maxNs), pct));
            }

         }
      }

      private static final class Entry {
         long totalNs;
         long maxNs;
         long calls;

         private Entry() {
         }
      }
   }
}
