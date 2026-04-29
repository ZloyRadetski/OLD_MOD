package net.portalmod.common.sorted.portal;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.OptionalDouble;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.RenderType.State;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Vector3d;
import net.portalmod.core.math.Vec3;
import net.portalmod.mixins.accessors.ChunkRenderDispatcherAccessor;
import net.portalmod.mixins.accessors.CompiledChunkAccessor;

public class PortalTransparencyHandler {
   public static final RenderType.State PORTAL_TRANSLUCENT_STATE;
   public static final RenderType PORTAL_TRANSLUCENT;
   private static final BufferBuilder bufferBuilder;

   public static void resortTransparency(ActiveRenderInfo portalCamera) {
      WorldRenderer lr = Minecraft.func_71410_x().field_71438_f;
      Queue<RegionRenderCacheBuilder> freeBuffers = ((ChunkRenderDispatcherAccessor)lr.field_174995_M).pmGetFreeBuffers();
      if (!freeBuffers.isEmpty()) {
         ObjectListIterator var3 = lr.field_72755_R.iterator();

         while(var3.hasNext()) {
            WorldRenderer.LocalRenderInformationContainer lric = (WorldRenderer.LocalRenderInformationContainer)var3.next();
            Vector3d vector3d = portalCamera.func_216785_c();
            float x = (float)vector3d.field_72450_a;
            float y = (float)vector3d.field_72448_b;
            float z = (float)vector3d.field_72449_c;
            CompiledChunkAccessor cca = (CompiledChunkAccessor)lric.field_178036_a.field_178590_b.get();
            BufferBuilder.State bufferbuilder$state = cca.pmGetTransparencyState();
            if (bufferbuilder$state != null && cca.pmGetHasBlocks().contains(RenderType.func_228645_f_())) {
               bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_176600_a);
               bufferBuilder.func_178993_a(bufferbuilder$state);
               bufferBuilder.func_181674_a(x - (float)lric.field_178036_a.func_178568_j().func_177958_n(), y - (float)lric.field_178036_a.func_178568_j().func_177956_o(), z - (float)lric.field_178036_a.func_178568_j().func_177952_p());
               bufferBuilder.func_178977_d();
               lric.field_178036_a.func_228924_a_(PORTAL_TRANSLUCENT).func_227875_a_(bufferBuilder);
            }
         }
      }

   }

   public static void resortMainTransparency(Vec3 position) {
      WorldRenderer lr = Minecraft.func_71410_x().field_71438_f;
      Queue<RegionRenderCacheBuilder> freeBuffers = ((ChunkRenderDispatcherAccessor)lr.field_174995_M).pmGetFreeBuffers();
      if (!freeBuffers.isEmpty()) {
         ObjectListIterator var3 = lr.field_72755_R.iterator();

         while(var3.hasNext()) {
            WorldRenderer.LocalRenderInformationContainer lric = (WorldRenderer.LocalRenderInformationContainer)var3.next();
            float x = (float)position.x;
            float y = (float)position.y;
            float z = (float)position.z;
            CompiledChunkAccessor cca = (CompiledChunkAccessor)lric.field_178036_a.field_178590_b.get();
            BufferBuilder.State bufferbuilder$state = cca.pmGetTransparencyState();
            if (bufferbuilder$state != null && cca.pmGetHasBlocks().contains(RenderType.func_228645_f_())) {
               bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_176600_a);
               bufferBuilder.func_178993_a(bufferbuilder$state);
               bufferBuilder.func_181674_a(x - (float)lric.field_178036_a.func_178568_j().func_177958_n(), y - (float)lric.field_178036_a.func_178568_j().func_177956_o(), z - (float)lric.field_178036_a.func_178568_j().func_177952_p());
               bufferBuilder.func_178977_d();
               lric.field_178036_a.func_228924_a_(RenderType.func_228645_f_()).func_227875_a_(bufferBuilder);
            }
         }
      }

   }

   static {
      PORTAL_TRANSLUCENT_STATE = State.func_228694_a_().func_228723_a_(new RenderState.ShadeModelState(true)).func_228719_a_(new RenderState.LightmapState(true)).func_228724_a_(new RenderState.TextureState(AtlasTexture.field_110575_b, false, true)).func_228720_a_(new RenderState.LineState(OptionalDouble.of(1.1))).func_228726_a_(new RenderState.TransparencyState("translucent_transparency", () -> {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      }, () -> {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      })).func_228721_a_(new RenderState.TargetState("translucent_target", () -> {
         if (Minecraft.func_238218_y_()) {
            Minecraft.func_71410_x().field_71438_f.func_239228_q_().func_147610_a(false);
         }

      }, () -> {
         if (Minecraft.func_238218_y_()) {
            Minecraft.func_71410_x().func_147110_a().func_147610_a(false);
         }

      })).func_228728_a_(true);
      PORTAL_TRANSLUCENT = RenderType.func_228633_a_("portal_translucent", DefaultVertexFormats.field_176600_a, 7, 262144, true, true, PORTAL_TRANSLUCENT_STATE);
      bufferBuilder = new BufferBuilder(262144);
   }
}
