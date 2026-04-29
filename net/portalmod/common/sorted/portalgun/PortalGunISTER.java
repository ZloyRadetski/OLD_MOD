package net.portalmod.common.sorted.portalgun;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.portalmod.client.animation.PortalGunAnimatedTexture;
import net.portalmod.common.sorted.portalgun.skins.ClientSkinManager;
import net.portalmod.core.init.AnimationInit;
import net.portalmod.core.util.Colour;

public class PortalGunISTER extends ItemStackTileEntityRenderer {
   public static UUID renderingPortalGunOwner;

   public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight, int packedOverlay) {
      matrixStack.func_227860_a_();
      switch (transformType) {
         case FIRST_PERSON_LEFT_HAND:
            matrixStack.func_227861_a_(0.2, 0.1, (double)0.0F);
            matrixStack.func_227862_a_(1.5F, 1.5F, 1.5F);
            break;
         case FIRST_PERSON_RIGHT_HAND:
            matrixStack.func_227861_a_(0.8, 0.1, (double)0.0F);
            matrixStack.func_227862_a_(1.5F, 1.5F, 1.5F);
            break;
         case GROUND:
            matrixStack.func_227861_a_((double)0.5F, 0.35, (double)0.5F);
            matrixStack.func_227862_a_(0.75F, 0.75F, 0.75F);
            break;
         case GUI:
            matrixStack.func_227861_a_(0.55, 0.3, (double)0.0F);
            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(30.0F));
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-135.0F));
            break;
         case HEAD:
            matrixStack.func_227861_a_((double)0.5F, (double)0.5F, (double)0.0F);
            matrixStack.func_227862_a_(1.5F, 1.5F, 1.5F);
            break;
         case THIRD_PERSON_LEFT_HAND:
            matrixStack.func_227861_a_((double)0.5F, 0.2, 0.1);
            break;
         case THIRD_PERSON_RIGHT_HAND:
            matrixStack.func_227861_a_((double)0.5F, 0.2, 0.1);
            break;
         case FIXED:
            matrixStack.func_227861_a_((double)0.5F, 0.3, 0.45);
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F));
            matrixStack.func_227862_a_(1.3F, 1.3F, 1.3F);
      }

      boolean animate = true;
      if (transformType != TransformType.FIRST_PERSON_RIGHT_HAND && transformType != TransformType.FIRST_PERSON_LEFT_HAND) {
         animate = false;
      } else {
         float xRot = (float)((double)((float)AnimationInit.RECOIL_X.compute(System.currentTimeMillis())) + (double)1.5F * AnimationInit.LIFT.compute(System.currentTimeMillis()));
         float yRot = (float)((double)((float)AnimationInit.RECOIL_Y.compute(System.currentTimeMillis())) + AnimationInit.LIFT.compute(System.currentTimeMillis()));
         float zRot = (float)((double)((float)AnimationInit.FIZZLE_BODY.compute(System.currentTimeMillis())) + (double)0.5F * AnimationInit.LIFT.compute(System.currentTimeMillis()));
         matrixStack.func_227861_a_((double)0.0F, (double)0.0F, (double)0.5F);
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(yRot));
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(xRot));
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(zRot));
         matrixStack.func_227861_a_((double)0.0F, (double)0.0F, (double)-0.5F);
      }

      new Colour(255, 0, 0, 255);
      new Colour(255, 0, 0, 255);
      Colour lastPortalColor = new Colour(64, 59, 75, 255);
      Colour stripeColour = new Colour(255, 255, 255, 0);
      boolean gunLightOn = false;
      CompoundNBT nbt = itemStack.func_196082_o();
      if (nbt.func_74764_b("AccentColor")) {
         if (nbt.func_74779_i("AccentColor").equals("none")) {
            stripeColour = new Colour(255, 255, 255, 0);
         } else {
            stripeColour = new Colour(DyeColor.func_204271_a(nbt.func_74779_i("AccentColor"), DyeColor.RED).func_193349_f());
         }
      }

      if (nbt.func_74764_b("LastPortal")) {
         int lastPortal = nbt.func_74762_e("LastPortal");
         gunLightOn = true;
         if (lastPortal == -1) {
            lastPortalColor = PortalGun.getLeftColour(nbt);
         } else if (lastPortal == 1) {
            lastPortalColor = PortalGun.getRightColour(nbt);
         } else {
            gunLightOn = false;
         }

         if (gunLightOn) {
            lastPortalColor.darken(0.15F + 0.05F * (float)Math.sin((double)System.currentTimeMillis() / (double)10.0F % (double)360.0F * Math.PI / (double)180.0F));
         }
      }

      UUID gunUUID = (UUID)PortalGun.getUUID(itemStack).orElse((Object)null);
      PortalGunModel model = PortalGunModelManager.getInstance().getModel(gunUUID);
      UUID actualRenderingPortalGunOwner = renderingPortalGunOwner;
      String skin = "default";
      switch (transformType) {
         case FIRST_PERSON_LEFT_HAND:
         case FIRST_PERSON_RIGHT_HAND:
         case HEAD:
         case THIRD_PERSON_LEFT_HAND:
         case THIRD_PERSON_RIGHT_HAND:
            skin = ClientSkinManager.getInstance().getSelectedSkinForPlayer(actualRenderingPortalGunOwner);
         case GROUND:
         default:
            break;
         case GUI:
            if (ClientSkinManager.getInstance().hasUUID()) {
               skin = ClientSkinManager.getInstance().getSelectedSkinForPlayer((UUID)null);
            }

            actualRenderingPortalGunOwner = null;
      }

      int intTint = ClientSkinManager.getInstance().getTintForPlayerOnSkin(actualRenderingPortalGunOwner, skin);
      Colour tint = intTint == 0 ? Colour.WHITE : (new Colour(intTint)).opaque();
      renderGun(matrixStack, gunUUID, model, renderTypeBuffer, ClientSkinManager.getInstance().getSkinTexture(skin), stripeColour, lastPortalColor, tint, gunLightOn, animate, packedLight, packedOverlay);
      matrixStack.func_227865_b_();
   }

   public static void renderGun(MatrixStack matrixStack, UUID gunUUID, PortalGunModel model, IRenderTypeBuffer renderTypeBuffer, PortalGunAnimatedTexture texture, Colour stripeColour, Colour lastPortalColor, Colour tint, boolean gunLightOn, boolean animate, int packedLight, int packedOverlay) {
      matrixStack.func_227860_a_();
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      matrixStack.func_227861_a_((double)0.0F, (double)-1.5F, (double)0.0F);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.func_71410_x().field_71438_f.field_228415_m_.func_228487_b_();
      irendertypebuffer$impl.func_228461_a_();
      IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.func_228640_c_(texture.getTextureLocation()));
      texture.setupAnimation();
      model.render(gunUUID, model.gun, matrixStack, ivertexbuilder, packedLight, packedOverlay, tint, !animate);
      model.render(gunUUID, model.stripes, matrixStack, ivertexbuilder, packedLight, packedOverlay, stripeColour, !animate);
      irendertypebuffer$impl.func_228461_a_();
      texture.endAnimation();
      ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.func_228640_c_(texture.getTextureLocation()));
      model.render(gunUUID, model.colour, matrixStack, ivertexbuilder, gunLightOn ? LightTexture.func_228451_a_(15, 15) : packedLight, packedOverlay, lastPortalColor, !animate);
      irendertypebuffer$impl.func_228461_a_();
      matrixStack.func_227865_b_();
   }

   public static void startShootAnimation(UUID gunUUID) {
      PortalGun.getModel(gunUUID).startAnimation(gunUUID, "shoot");
      AnimationInit.RECOIL_X.start();
      AnimationInit.RECOIL_Y.start();
   }

   public static void startFizzleAnimation() {
      AnimationInit.FIZZLE_BODY.start();
   }

   public static void startLiftAnimation(UUID gunUUID) {
      PortalGun.getModel(gunUUID).startAnimation(gunUUID, "lift");
      AnimationInit.LIFT.start();
   }

   public static void stopLiftAnimation(UUID gunUUID) {
      PortalGun.getModel(gunUUID).startAnimation(gunUUID, "drop");
      AnimationInit.LIFT.stop();
   }
}
