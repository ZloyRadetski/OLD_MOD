package net.portalmod.common.sorted.autoportal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.portalmod.client.render.DynamicTextureVertexBuilder;
import net.portalmod.common.sorted.button.QuadBlockCorner;
import net.portalmod.common.sorted.portal.OrthonormalBasis;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class AutoPortalTER extends TileEntityRenderer<AutoPortalTileEntity> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "entity/autoportal");
   public static RenderMaterial MATERIAL;
   private final AutoPortalModel model = new AutoPortalModel();

   public AutoPortalTER(TileEntityRendererDispatcher terd) {
      super(terd);
      MATERIAL = new RenderMaterial(AtlasTexture.field_110575_b, TEXTURE);
   }

   public void render(AutoPortalTileEntity be, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int light, int overlay) {
      if (be.func_195044_w().func_177230_c() instanceof AutoPortalBlock) {
         AutoPortalBlock block = (AutoPortalBlock)be.func_195044_w().func_177230_c();
         Direction facing = (Direction)be.func_195044_w().func_177229_b(AutoPortalBlock.FACING);
         Direction direction = (Direction)be.func_195044_w().func_177229_b(AutoPortalBlock.DIRECTION);
         BlockPos pos = be.func_174877_v();
         Vec3 up = new Vec3(block.getOtherBlock(pos, QuadBlockCorner.DOWN_LEFT, QuadBlockCorner.UP_LEFT, facing, direction).func_177973_b(pos));
         Vec3 right = new Vec3(block.getOtherBlock(pos, QuadBlockCorner.DOWN_LEFT, QuadBlockCorner.DOWN_RIGHT, facing, direction).func_177973_b(pos));
         Mat4 matrix = (new OrthonormalBasis(right, up)).getChangeOfBasisFromCanonicalMatrix();
         up = up.mul((double)0.5F);
         right = right.mul((double)0.5F);
         Vec3 normal = (new Vec3(facing)).mul(-0.499);
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(right.x, right.y, right.z);
         matrixStack.func_227861_a_(up.x, up.y, up.z);
         matrixStack.func_227861_a_(normal.x, normal.y, normal.z);
         matrixStack.func_227861_a_((double)0.5F, (double)0.5F, (double)0.5F);
         matrixStack.func_227866_c_().func_227870_a_().func_226595_a_(matrix.to4f());
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         matrixStack.func_227861_a_((double)0.0F, (double)-1.5F, (double)0.0F);
         IVertexBuilder vertexBuilder = MATERIAL.func_229311_a_(renderBuffer, RenderType::func_228644_e_);
         DynamicTextureVertexBuilder dtvb = new DynamicTextureVertexBuilder(vertexBuilder);
         int closedOffset = be.lastOpenedUUID != null ? 0 : 2;
         this.model.frame.func_228309_a_(matrixStack, dtvb, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         Optional<Integer> colorIndex = be.getCurrentColorIndex();
         if (colorIndex.isPresent()) {
            int color = (Integer)colorIndex.get();
            float u = (float)(color % 4 * 11 + closedOffset) / 64.0F;
            float v = (float)(color / 4) * 16.0F / 64.0F;
            dtvb.setOffset(u, v);
            this.model.wawas.func_228309_a_(matrixStack, dtvb, LightTexture.func_228451_a_(15, 15), overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         }

         matrixStack.func_227865_b_();
      }

   }
}
