package net.portalmod.common.sorted.portalgun.skins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.portalmod.common.sorted.portalgun.PortalGunISTER;
import net.portalmod.common.sorted.portalgun.PortalGunModel;
import net.portalmod.common.sorted.portalgun.PortalGunModelManager;
import net.portalmod.core.util.Colour;

public class SkinPreviewWidget extends Widget {
   private final SkinSelectorScreen parent;
   private boolean dragging;
   private float mousePrevX;
   private float mousePrevY;
   private long mousePrevTime;
   private float xRot;
   private float yRot;
   private float zRot;
   private float xRotOld;
   private float yRotOld;
   private static final float MOMENTUM_LIMIT = 200.0F;
   private float xRotMomentum;
   private float yRotMomentum;
   private boolean clockwise;
   private long animationStart = -1L;
   private boolean animationHalfLifePassed;
   private String selectedSkin;
   private String nextSelectedSkin;

   public SkinPreviewWidget(int x, int y, int width, int height, SkinSelectorScreen parent) {
      super(x, y, width, height, StringTextComponent.field_240750_d_);
      this.parent = parent;
      this.xRot = 30.0F;
      this.yRot = -135.0F;
      this.xRotOld = this.xRot;
      this.yRotOld = this.yRot;
      this.xRotMomentum = 0.0F;
      this.yRotMomentum = 0.0F;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      partialTicks = Minecraft.func_71410_x().func_184121_ak();
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.func_71410_x().func_228019_au_().func_228487_b_();
      Colour lastPortalColor = new Colour(64, 59, 75, 255);
      Colour stripeColour = new Colour(255, 255, 255, 0);
      Colour tint = (Colour)Optional.ofNullable(this.parent.getSkinTint()).orElse(Colour.WHITE);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(AtlasTexture.field_110575_b);
      Minecraft.func_71410_x().field_71446_o.func_229267_b_(AtlasTexture.field_110575_b).func_174937_a(false, false);
      RenderSystem.pushMatrix();
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.translatef((float)this.field_230690_l_ + (float)this.field_230688_j_ / 2.0F, (float)this.field_230691_m_ + (float)this.field_230688_j_ / 2.0F, 100.0F);
      RenderSystem.scalef(5.0F, 5.0F, 5.0F);
      RenderSystem.scalef(16.0F, -16.0F, 16.0F);
      RenderHelper.func_227784_d_();
      matrixStack.func_227860_a_();
      this.computeAnimation();
      matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(MathHelper.func_219799_g(partialTicks, this.xRotOld, this.xRot)));
      matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(MathHelper.func_219799_g(partialTicks, this.yRotOld, this.yRot)));
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(this.zRot));
      matrixStack.func_227861_a_((double)0.0F, -0.2, (double)0.0F);
      PortalGunISTER.renderGun(matrixStack, (UUID)null, this.getModel(), irendertypebuffer$impl, ClientSkinManager.getInstance().getSkinTexture(this.selectedSkin), stripeColour, lastPortalColor, tint, false, false, 15728880, OverlayTexture.field_229196_a_);
      matrixStack.func_227865_b_();
      RenderSystem.enableDepthTest();
      RenderSystem.disableAlphaTest();
      RenderSystem.disableRescaleNormal();
      RenderSystem.popMatrix();
   }

   public void tick() {
      this.xRotOld = this.xRot;
      this.yRotOld = this.yRot;
      if (this.dragging) {
         this.stopRotation();
      } else {
         this.xRot += this.xRotMomentum;
         this.yRot += this.yRotMomentum;
         this.xRot = (this.xRot - 30.0F) * 0.99F + 30.0F;
         float idleSpeed = (float)((this.clockwise ? -1 : 1) * 4);
         this.yRotMomentum = (this.yRotMomentum - idleSpeed) * 0.8F + idleSpeed;
         this.xRotMomentum *= 0.5F;
      }

      this.wrapYRot();
      this.clampXRot();
   }

   private void setPrevInput(double x, double y) {
      this.mousePrevX = (float)x;
      this.mousePrevY = (float)y;
      this.mousePrevTime = System.currentTimeMillis();
   }

   private void stopRotation() {
      this.xRotMomentum = 0.0F;
      this.yRotMomentum = 0.0F;
   }

   private void wrapYRot() {
      while(this.yRot >= 360.0F) {
         this.yRot -= 360.0F;
         this.yRotOld -= 360.0F;
      }

      while(this.yRot < 0.0F) {
         this.yRot += 360.0F;
         this.yRotOld += 360.0F;
      }

   }

   private void clampXRot() {
      this.xRot = MathHelper.func_76131_a(this.xRot, -90.0F, 90.0F);
   }

   private float getDeltaTicks() {
      return Math.max((float)(System.currentTimeMillis() - this.mousePrevTime) / 50.0F, 0.001F);
   }

   private PortalGunModel getModel() {
      return PortalGunModelManager.getInstance().getModel((UUID)null);
   }

   private void computeAnimation() {
      if (this.animationStart != -1L) {
         float delta = (float)(System.currentTimeMillis() - this.animationStart) / 1000.0F;
         if (delta > 1.0F) {
            this.zRot = 0.0F;
            this.selectedSkin = this.nextSelectedSkin;
         } else {
            if ((double)delta > 0.15 && !this.animationHalfLifePassed) {
               this.selectedSkin = this.nextSelectedSkin;
               this.animationHalfLifePassed = true;
            }

            float factor = (float)((double)1.0F - Math.exp((double)(-10.0F * delta)));
            this.zRot = 360.0F * factor * factor;
         }
      }
   }

   public void setSelectedSkin(String skin, boolean animate) {
      this.nextSelectedSkin = skin;
      if (animate) {
         this.animationStart = System.currentTimeMillis();
         this.animationHalfLifePassed = false;
      } else {
         this.selectedSkin = this.nextSelectedSkin;
      }

   }

   public void func_230982_a_(double x, double y) {
      this.dragging = true;
      this.setPrevInput(x, y);
      this.stopRotation();
   }

   public void mouseReleasedAnywhere(int button) {
      if (this.func_230987_a_(button)) {
         this.dragging = false;
      }

   }

   public void func_212927_b(double x, double y) {
      super.func_212927_b(x, y);
      if (this.dragging) {
         float deltaXRot = (float)(y - (double)this.mousePrevY);
         float deltaYRot = (float)(x - (double)this.mousePrevX);
         this.xRotOld = this.xRot += deltaXRot;
         this.yRotOld = this.yRot += deltaYRot;
         this.wrapYRot();
         this.clampXRot();
         float delta = this.getDeltaTicks();
         this.xRotMomentum = MathHelper.func_76131_a(deltaXRot / delta, -200.0F, 200.0F);
         this.yRotMomentum = MathHelper.func_76131_a(deltaYRot / delta, -200.0F, 200.0F);
         if (this.yRotMomentum != 0.0F) {
            this.clockwise = this.yRotMomentum < 0.0F;
         }

         this.setPrevInput(x, y);
      }
   }
}
