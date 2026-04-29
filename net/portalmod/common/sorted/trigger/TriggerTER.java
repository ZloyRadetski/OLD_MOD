package net.portalmod.common.sorted.trigger;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.portalmod.common.items.WrenchItem;

public class TriggerTER extends TileEntityRenderer<TriggerTileEntity> {
   private final TriggerFieldBakedModel triggerFieldBakedModel = new TriggerFieldBakedModel();

   public TriggerTER(TileEntityRendererDispatcher terd) {
      super(terd);
   }

   public void render(TriggerTileEntity be, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int light, int overlay) {
      if (Minecraft.func_71410_x().field_71439_g != null && WrenchItem.holdingWrench(Minecraft.func_71410_x().field_71439_g)) {
         this.triggerFieldBakedModel.bakeQuadsOnce();
         AxisAlignedBB aabb = null;
         if (TriggerSelectionClient.isSelecting(be)) {
            aabb = TriggerSelectionClient.getBox();
         } else if (be.hasField()) {
            aabb = be.getField();
         }

         if (aabb != null) {
            this.renderTriggerField(be.getTriggerType(), aabb, matrixStack, renderBuffer, LightTexture.func_228451_a_(15, 0), overlay);
         }
      }
   }

   private static ResourceLocation getFieldTexture(TriggerType type, boolean inside) {
      String path = "block/trigger_field_";
      switch (type) {
         case PLAYER:
            path = path + "players";
            break;
         case MOB:
            path = path + "mobs";
      }

      path = path + "_" + (inside ? "inside" : "outside");
      return new ResourceLocation("portalmod", path);
   }

   public static List<ResourceLocation> getAllFieldTextures() {
      List<ResourceLocation> textures = new ArrayList();

      for(TriggerType type : TriggerType.values()) {
         textures.add(getFieldTexture(type, false));
         textures.add(getFieldTexture(type, true));
      }

      return textures;
   }

   private void renderTriggerField(TriggerType type, AxisAlignedBB aabb, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int light, int overlay) {
      float x0 = (float)aabb.field_72340_a;
      float y0 = (float)aabb.field_72338_b;
      float z0 = (float)aabb.field_72339_c;
      float x1 = (float)aabb.field_72336_d;
      float y1 = (float)aabb.field_72337_e;
      float z1 = (float)aabb.field_72334_f;
      ResourceLocation outsideTexture = getFieldTexture(type, false);
      ResourceLocation insideTexture = getFieldTexture(type, true);

      for(float y = y0; y < y1; ++y) {
         for(float z = z0; z < z1; ++z) {
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x0, y, z, Direction.WEST, outsideTexture, false);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x0 - 1.0F, y, z, Direction.EAST, insideTexture, true);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x1 - 1.0F, y, z, Direction.EAST, outsideTexture, false);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x1, y, z, Direction.WEST, insideTexture, true);
         }
      }

      for(float z = z0; z < z1; ++z) {
         for(float x = x0; x < x1; ++x) {
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y0, z, Direction.DOWN, outsideTexture, false);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y0 - 1.0F, z, Direction.UP, insideTexture, true);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y1 - 1.0F, z, Direction.UP, outsideTexture, false);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y1, z, Direction.DOWN, insideTexture, true);
         }
      }

      for(float y = y0; y < y1; ++y) {
         for(float x = x0; x < x1; ++x) {
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y, z0, Direction.NORTH, outsideTexture, false);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y, z0 - 1.0F, Direction.SOUTH, insideTexture, true);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y, z1 - 1.0F, Direction.SOUTH, outsideTexture, false);
            this.renderQuad(renderBuffer, matrixStack, light, overlay, x, y, z1, Direction.NORTH, insideTexture, true);
         }
      }

   }

   private void renderQuad(IRenderTypeBuffer renderBuffer, MatrixStack matrixStack, int light, int overlay, float x, float y, float z, Direction face, ResourceLocation texture, boolean inside) {
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_((double)x, (double)y, (double)z);
      renderBuffer.getBuffer(RenderType.func_239269_g_()).func_227889_a_(matrixStack.func_227866_c_(), this.triggerFieldBakedModel.getQuad(face, texture), 1.0F, 1.0F, 1.0F, light, overlay);
      matrixStack.func_227865_b_();
   }

   public boolean shouldRenderOffScreen(TriggerTileEntity be) {
      return true;
   }
}
