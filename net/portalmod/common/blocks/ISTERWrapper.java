package net.portalmod.common.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class ISTERWrapper extends ItemStackTileEntityRenderer {
   public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
      ItemRenderer itemRenderer = Minecraft.func_71410_x().func_175599_af();
      IBakedModel model = ((BakedModelWrapper)itemRenderer.func_184393_a(itemStack, Minecraft.func_71410_x().field_71441_e, Minecraft.func_71410_x().field_71439_g)).getBase();
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      model = ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, transformType == TransformType.FIRST_PERSON_LEFT_HAND);
      matrixStack.func_227861_a_((double)-0.5F, (double)-0.5F, (double)-0.5F);
      RenderType renderType = RenderTypeLookup.func_239219_a_(itemStack, true);
      IVertexBuilder vertexBuilder = ItemRenderer.func_239391_c_(renderTypeBuffer, renderType, true, itemStack.func_77962_s());
      itemRenderer.func_229114_a_(model, itemStack, light, overlay, matrixStack, vertexBuilder);
   }
}
