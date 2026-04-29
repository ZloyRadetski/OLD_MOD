package net.portalmod.client.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Function;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.portalmod.common.sorted.portalgun.skins.ClientSkinManager;

public class PortalGunAnimatedTexture extends Texture {
   private static final ResourceLocation DEFAULT_SKIN = new ResourceLocation("portalmod", "textures/portalgun/default.png");
   private final String skinId;
   private final RenderMaterial material;
   private final int framerate;
   private NativeImage ni;

   public PortalGunAnimatedTexture(String id, int framerate) {
      this.skinId = id;
      this.material = new RenderMaterial(AtlasTexture.field_110575_b, ClientSkinManager.getInstance().getSkinLocation(id));
      this.framerate = framerate;
   }

   public ResourceLocation getTextureLocation() {
      return this.material.func_229313_b_();
   }

   public IVertexBuilder buffer(IRenderTypeBuffer renderTypeBuffer, Function<ResourceLocation, RenderType> renderType) {
      return this.material.func_229311_a_(renderTypeBuffer, renderType);
   }

   public void setupAnimation() {
      float height = this.material.func_229314_c_().func_94210_h();
      float index = this.framerate != 0 && this.getFrameCount() != 0 ? (float)System.currentTimeMillis() / (1000.0F / (float)this.framerate) % (float)this.getFrameCount() : 0.0F;
      RenderSystem.matrixMode(5890);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.translatef(0.0F, height / (float)this.getFrameCount() * index, 0.0F);
      RenderSystem.scalef(1.0F, 1.0F / (float)this.getFrameCount(), 1.0F);
      RenderSystem.matrixMode(5888);
   }

   public void endAnimation() {
      RenderSystem.matrixMode(5890);
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
   }

   public void func_195413_a(IResourceManager rm) throws IOException {
      if (this.ni == null) {
         File skinFile = new File(ClientSkinManager.getInstance().getSkinsFolder(), "textures/" + this.skinId + ".png");
         if (!this.skinId.equals("default") && skinFile.exists()) {
            if (!skinFile.exists()) {
               return;
            }

            this.ni = NativeImage.func_195713_a(Files.newInputStream(skinFile.toPath()));
         } else {
            IResource iresource = rm.func_199002_a(DEFAULT_SKIN);
            Throwable var4 = null;

            try {
               this.ni = NativeImage.func_195713_a(iresource.func_199027_b());
            } catch (Throwable var13) {
               var4 = var13;
               throw var13;
            } finally {
               if (iresource != null) {
                  if (var4 != null) {
                     try {
                        iresource.close();
                     } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                     }
                  } else {
                     iresource.close();
                  }
               }

            }
         }

         if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.upload(this.ni));
         } else {
            this.upload(this.ni);
         }

      }
   }

   private void upload(NativeImage ni) {
      TextureUtil.func_225681_a_(this.func_110552_b(), 0, ni.func_195702_a(), ni.func_195714_b());
      ni.func_227789_a_(0, 0, 0, 0, 0, ni.func_195702_a(), ni.func_195714_b(), false, false, false, true);
   }

   public int getFrameCount() {
      return this.ni == null ? 0 : this.ni.func_195714_b() / this.ni.func_195702_a();
   }
}
