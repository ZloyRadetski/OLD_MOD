package net.portalmod.common.sorted.portalgun;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.portalmod.common.blocks.PortalableBlock;
import net.portalmod.common.sorted.portal.ClientPortalManager;
import net.portalmod.common.sorted.portal.PortalEnd;
import net.portalmod.core.config.PortalModConfigManager;

public class PortalGunCrosshairRenderer {
   private static final String BASE = "textures/gui/crosshair/portalgun_crosshair_";

   public static void render(MatrixStack matrixStack) {
      Minecraft mc = Minecraft.func_71410_x();
      if (mc.field_71441_e != null && mc.field_71439_g != null && mc.field_71442_b != null) {
         ItemStack itemStack = mc.field_71439_g.func_184614_ca();
         Optional<UUID> uuid = PortalGun.getUUID(itemStack);
         boolean isClassicCrosshair = (Boolean)PortalModConfigManager.CROSSHAIR.get();
         boolean isFirstPerson = mc.field_71474_y.func_243230_g().func_243192_a();
         boolean isSpectator = mc.field_71442_b.func_178889_l() == GameType.SPECTATOR;
         if (itemStack.func_77973_b() instanceof PortalGun && isFirstPerson && !isSpectator && uuid.isPresent()) {
            boolean primaryFilled;
            boolean secondaryFilled;
            if (isClassicCrosshair) {
               PlayerEntity player = mc.field_71439_g;
               World level = mc.field_71441_e;
               Vector3d rayPath = player.func_70676_i(0.0F).func_186678_a((double)mc.field_71460_t.func_205001_m());
               Vector3d from = player.func_174824_e(0.0F);
               Vector3d to = from.func_178787_e(rayPath);
               RayTraceContext rayCtx = new RayTraceContext(from, to, BlockMode.COLLIDER, FluidMode.ANY, (Entity)null);
               BlockRayTraceResult rayTrace = PortalGun.customClip(level, rayCtx);
               boolean isPortalable = PortalableBlock.isPortalable(mc.field_71441_e.func_180495_p(rayTrace.func_216350_a()), rayTrace.func_216354_b(), level);
               primaryFilled = isPortalable;
               secondaryFilled = isPortalable;
            } else {
               primaryFilled = ClientPortalManager.getInstance().hasFullOrPartial((UUID)uuid.get(), PortalEnd.PRIMARY);
               secondaryFilled = ClientPortalManager.getInstance().hasFullOrPartial((UUID)uuid.get(), PortalEnd.SECONDARY);
               CompoundNBT tag = itemStack.func_77978_p();
               if (tag != null && tag.func_74764_b("Locked") && !tag.func_74779_i("Locked").equals("None")) {
                  if (tag.func_74779_i("Locked").equals("Right")) {
                     secondaryFilled = primaryFilled;
                  }

                  if (tag.func_74779_i("Locked").equals("Left")) {
                     primaryFilled = secondaryFilled;
                  }
               }
            }

            CompoundNBT nbt = itemStack.func_196082_o();
            DyeColor primaryColor = PortalGun.getLeftDyeColour(nbt);
            DyeColor secondaryColor = PortalGun.getRightDyeColour(nbt);
            renderCrosshairPart(matrixStack, PortalEnd.PRIMARY, primaryColor, primaryFilled);
            renderCrosshairPart(matrixStack, PortalEnd.SECONDARY, secondaryColor, secondaryFilled);
            if (isClassicCrosshair) {
               CompoundNBT tag = itemStack.func_77978_p();
               if (tag != null && tag.func_74764_b("LastPortal") && (!tag.func_74764_b("Locked") || tag.func_74779_i("Locked").equals("None"))) {
                  int lastPortal = tag.func_74762_e("LastPortal");
                  if (lastPortal == -1) {
                     renderCrosshairDot(matrixStack, PortalEnd.PRIMARY, primaryColor);
                  }

                  if (lastPortal == 1) {
                     renderCrosshairDot(matrixStack, PortalEnd.SECONDARY, secondaryColor);
                  }
               }
            }

         }
      }
   }

   private static void renderCrosshairPart(MatrixStack matrixStack, PortalEnd end, DyeColor color, boolean filled) {
      ResourceLocation texture = new ResourceLocation("portalmod", "textures/gui/crosshair/portalgun_crosshair_" + color.func_176762_d() + ".png");
      MainWindow window = Minecraft.func_71410_x().func_228018_at_();
      int x = -17;
      int y = -17;
      int size = 33;
      RenderSystem.disableBlend();
      Minecraft.func_71410_x().func_110434_K().func_110577_a(texture);
      blit(matrixStack, window.func_198107_o() / 2 + x, window.func_198087_p() / 2 + y, 0, filled ? (float)size : 0.0F, end == PortalEnd.PRIMARY ? 0.0F : (float)size, size, size, size * 2, size * 2);
      RenderSystem.enableBlend();
   }

   private static void renderCrosshairDot(MatrixStack matrixStack, PortalEnd end, DyeColor color) {
      ResourceLocation texture = new ResourceLocation("portalmod", "textures/gui/crosshair/portalgun_crosshair_dots_" + color.func_176762_d() + ".png");
      MainWindow window = Minecraft.func_71410_x().func_228018_at_();
      int x = -17;
      int y = -17;
      int size = 33;
      RenderSystem.disableBlend();
      Minecraft.func_71410_x().func_110434_K().func_110577_a(texture);
      blit(matrixStack, window.func_198107_o() / 2 + x, window.func_198087_p() / 2 + y, 0, 0.0F, end == PortalEnd.PRIMARY ? 0.0F : (float)size, size, size, size, size * 2);
      RenderSystem.enableBlend();
   }

   private static void blit(MatrixStack matrixStack, int x, int y, int z, float u0, float v0, int uw, int uh, int width, int height) {
      innerBlit(matrixStack, x, x + uw, y, y + uh, z, uw, uh, u0, v0, width, height);
   }

   private static void innerBlit(MatrixStack matrixStack, int x0, int x1, int y0, int y1, int z, int uw, int uh, float u0, float v0, int width, int height) {
      innerBlit(matrixStack.func_227866_c_().func_227870_a_(), x0, x1, y0, y1, z, (u0 + 0.0F) / (float)width, (u0 + (float)uw) / (float)width, (v0 + 0.0F) / (float)height, (v0 + (float)uh) / (float)height);
   }

   private static void innerBlit(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
      BufferBuilder bufferbuilder = Tessellator.func_178181_a().func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_227851_o_);
      bufferbuilder.func_227888_a_(matrix, (float)x0, (float)y1, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u0, v1).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x1, (float)y1, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u1, v1).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x1, (float)y0, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u1, v0).func_181675_d();
      bufferbuilder.func_227888_a_(matrix, (float)x0, (float)y0, (float)z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_225583_a_(u0, v0).func_181675_d();
      bufferbuilder.func_178977_d();
      RenderSystem.enableAlphaTest();
      WorldVertexBufferUploader.func_181679_a(bufferbuilder);
   }
}
