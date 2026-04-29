package net.portalmod.common.sorted.faithplate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.Colour;
import net.portalmod.core.util.ModUtil;

public class FaithPlateTER extends TileEntityRenderer<FaithPlateTileEntity> {
   public static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("portalmod", "entity/faithplate/faithplate");
   public static final ResourceLocation TEXTURE_ORANGE = new ResourceLocation("portalmod", "entity/faithplate/faithplate_active");
   public static final ResourceLocation TEXTURE_BLUE_E = new ResourceLocation("portalmod", "entity/faithplate/faithplate_emission");
   public static final ResourceLocation TEXTURE_ORANGE_E = new ResourceLocation("portalmod", "entity/faithplate/faithplate_active_emission");
   public static RenderMaterial MATERIAL_BLUE;
   public static RenderMaterial MATERIAL_ORANGE;
   public static RenderMaterial MATERIAL_BLUE_E;
   public static RenderMaterial MATERIAL_ORANGE_E;
   private final FaithPlatePlateModel plateModel = new FaithPlatePlateModel();
   public static BlockPos selected;

   public FaithPlateTER(TileEntityRendererDispatcher terd) {
      super(terd);
      MATERIAL_BLUE = new RenderMaterial(AtlasTexture.field_110575_b, TEXTURE_BLUE);
      MATERIAL_ORANGE = new RenderMaterial(AtlasTexture.field_110575_b, TEXTURE_ORANGE);
      MATERIAL_BLUE_E = new RenderMaterial(AtlasTexture.field_110575_b, TEXTURE_BLUE_E);
      MATERIAL_ORANGE_E = new RenderMaterial(AtlasTexture.field_110575_b, TEXTURE_ORANGE_E);
   }

   private void renderPlate(FaithPlateTileEntity be, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int combinedOverlay) {
      BlockPos pos = be.func_174877_v();
      BlockState state = be.func_195044_w();
      boolean onWall = state.func_177229_b(FaithPlateBlock.FACE) == FaithPlateBlock.Face.WALL;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_((double)0.5F, (double)0.5F, (double)0.5F);
      matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_((float)(onWall ? 0 : 180) - ((Direction)state.func_177229_b(FaithPlateBlock.FACING)).func_185119_l()));
      if (onWall) {
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(90.0F));
      }

      matrixStack.func_227861_a_((double)-0.5F, (double)-0.5F, (double)-0.5F);
      matrixStack.func_227861_a_((double)0.5F, (double)1.0F, (double)0.0F);
      matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F));
      matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
      matrixStack.func_227861_a_((double)0.0F, (double)-1.5F, (double)0.0F);
      boolean disabled = be.isEnabled() && be.getCooldown() < 2;
      IVertexBuilder ivbNormal = disabled ? MATERIAL_ORANGE.func_229311_a_(renderBuffer, RenderType::func_228644_e_) : MATERIAL_BLUE.func_229311_a_(renderBuffer, RenderType::func_228644_e_);
      IVertexBuilder ivbEmissive = disabled ? MATERIAL_ORANGE_E.func_229311_a_(renderBuffer, RenderType::func_228644_e_) : MATERIAL_BLUE_E.func_229311_a_(renderBuffer, RenderType::func_228644_e_);
      int light = WorldRenderer.func_228421_a_(be.func_145831_w(), onWall ? pos.func_177972_a((Direction)state.func_177229_b(FaithPlateBlock.FACING)) : pos.func_177984_a());
      this.plateModel.render(be, this.plateModel.bone, matrixStack, ivbNormal, light, combinedOverlay, new Colour(-1), false);
      this.plateModel.render(be, this.plateModel.bb_main, matrixStack, ivbNormal, light, combinedOverlay, new Colour(-1), false);
      this.plateModel.render(be, this.plateModel.bone, matrixStack, ivbEmissive, LightTexture.func_228451_a_(15, 15), combinedOverlay, new Colour(-1), false);
      this.plateModel.render(be, this.plateModel.bb_main, matrixStack, ivbEmissive, LightTexture.func_228451_a_(15, 15), combinedOverlay, new Colour(-1), false);
      matrixStack.func_227865_b_();
   }

   private void renderTrigger(FaithPlateTileEntity be, MatrixStack matrixStack) {
      BlockPos pos = be.func_174877_v();
      BlockState state = be.func_195044_w();
      Block block = state.func_177230_c();
      IRenderTypeBuffer renderTypeBuffer = Minecraft.func_71410_x().field_71438_f.field_228415_m_.func_228490_d_();
      IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderType.func_228659_m_());
      if (block instanceof FaithPlateBlock) {
         Vec3 normal = (new Vec3(((FaithPlateBlock)block).getNormal(state).func_176730_m())).mul(0.001);
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_((double)(-pos.func_177958_n()), (double)(-pos.func_177956_o()), (double)(-pos.func_177952_p()));
         matrixStack.func_227861_a_(normal.x, normal.y, normal.z);
         WorldRenderer.func_228430_a_(matrixStack, vertexBuilder, be.getTrigger(), 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227865_b_();
      }
   }

   private void renderPath(FaithPlateTileEntity be, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, Vector3d absoluteTargetBlockPos, Direction targetFace, int overlay) {
      BlockState state = be.func_195044_w();
      Vec3 normal = (new Vec3(targetFace.func_176730_m())).mul((double)0.5F);
      Vec3 absoluteTargetPos = (new Vec3(absoluteTargetBlockPos)).add((double)0.5F).add(normal);
      boolean onWall = state.func_177229_b(FaithPlateBlock.FACE) == FaithPlateBlock.Face.WALL;
      Direction plateDirection = onWall ? (Direction)state.func_177229_b(FaithPlateBlock.FACING) : Direction.UP;
      Vec3 relativeStartPoint = (new Vec3((double)0.5F)).add((new Vec3(plateDirection.func_176730_m())).mul((double)0.5F));
      BlockPos plateBlockPos = be.func_174877_v();
      Vec3 platePos = (new Vec3(plateBlockPos)).add(relativeStartPoint);
      Vec3 relativeTargetPos = absoluteTargetPos.clone().sub(platePos);
      FaithPlateParabola parabola = new FaithPlateParabola(relativeTargetPos.to3d(), (double)be.getPredictedHeight(absoluteTargetBlockPos));
      IVertexBuilder lineBuffer = renderBuffer.getBuffer(RenderType.func_228659_m_());
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(relativeStartPoint.x, relativeStartPoint.y, relativeStartPoint.z);
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      if (parabola.isVertical()) {
         this.vertex(lineBuffer, matrix4f, 0.0F, 0.0F, 0.0F);
         this.vertex(lineBuffer, matrix4f, 0.0F, be.getPredictedHeight(absoluteTargetBlockPos), 0.0F);
         matrixStack.func_227865_b_();
      } else {
         double a = parabola.getA();
         double b = parabola.getB();
         double horizontalDistance = relativeStartPoint.clone().mul((double)1.0F, (double)0.0F, (double)1.0F).sub(relativeTargetPos.clone().mul((double)1.0F, (double)0.0F, (double)1.0F)).magnitude();
         float increment = Math.max((float)horizontalDistance / 20.0F, 0.05F);
         float i = 0.0F;

         while(true) {
            float x = (float)((double)i * parabola.getComponentX());
            float z = (float)((double)i * parabola.getComponentZ());
            float y = (float)(a * (double)i * (double)i + b * (double)i);
            float i2 = i + increment;
            float x2 = (float)((double)i2 * parabola.getComponentX());
            float z2 = (float)((double)i2 * parabola.getComponentZ());
            float y2 = (float)(a * (double)i2 * (double)i2 + b * (double)i2);
            Vec3 next = (new Vec3((double)x2, (double)y2, (double)z2)).add(platePos);
            Vec3 targetToPlateNormal = platePos.clone().sub(absoluteTargetPos).normalize();
            Vec3 nextToPlateNormal = next.clone().sub(absoluteTargetPos).normalize();
            targetToPlateNormal.y = (double)0.0F;
            nextToPlateNormal.y = (double)0.0F;
            if (targetToPlateNormal.dot(nextToPlateNormal) < (double)0.0F || (float)be.func_174877_v().func_177956_o() + y < -1000.0F) {
               this.vertex(lineBuffer, matrix4f, x, y, z);
               this.vertex(lineBuffer, matrix4f, (float)relativeTargetPos.x, (float)relativeTargetPos.y, (float)relativeTargetPos.z);
               matrixStack.func_227865_b_();
               return;
            }

            this.vertex(lineBuffer, matrix4f, x, y, z);
            this.vertex(lineBuffer, matrix4f, x2, y2, z2);
            i += increment;
         }
      }
   }

   private void vertex(IVertexBuilder lineBuffer, Matrix4f matrix4f, float x, float y, float z) {
      lineBuffer.func_227888_a_(matrix4f, x, y, z).func_227885_a_(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
   }

   private void renderTarget(FaithPlateTileEntity be, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, Vector3d pos, Direction face, int light, int overlay) {
      float distance = (float)Minecraft.func_71410_x().field_71460_t.func_215316_n().func_216785_c().func_178788_d((new Vec3(pos)).add((Vector3i)be.func_174877_v()).to3d()).func_72430_b((new Vec3(face)).to3d());
      distance = (float)Math.min(1.0E-4 + Math.max(5.0E-4 * (double)(distance - 5.0F), (double)0.0F), 0.1);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(pos.func_82615_a(), pos.func_82617_b(), pos.func_82616_c());
      renderBuffer.getBuffer(RenderType.func_228643_e_()).func_227889_a_(matrixStack.func_227866_c_(), (new FaithPlateTargetBakedModel()).getQuad(face, distance), 1.0F, 1.0F, 1.0F, light, overlay);
      matrixStack.func_227865_b_();
      pos = pos.func_178787_e((new Vec3(face)).to3d());
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(pos.func_82615_a(), pos.func_82617_b(), pos.func_82616_c());
      renderBuffer.getBuffer(RenderType.func_228643_e_()).func_227889_a_(matrixStack.func_227866_c_(), (new FaithPlateTargetBakedModel()).getQuad(face.func_176734_d(), distance), 1.0F, 1.0F, 1.0F, light, overlay);
      matrixStack.func_227865_b_();
   }

   private void renderPointedPath(FaithPlateTileEntity be, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int overlay) {
      PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (be.func_174877_v().equals(selected) && player != null && !player.field_70145_X) {
         Item mainHandItem = player.func_184586_b(Hand.MAIN_HAND).func_77973_b();
         Item offHandItem = player.func_184586_b(Hand.OFF_HAND).func_77973_b();
         if (mainHandItem == ItemInit.WRENCH.get() || offHandItem == ItemInit.WRENCH.get()) {
            BlockRayTraceResult rayHit = ModUtil.rayTraceBlock(player, be.func_145831_w(), 64);
            if (rayHit.func_216346_c() != Type.MISS) {
               Direction targetFace = rayHit.func_216354_b();
               Vector3d absoluteTargetPos = rayHit.func_216347_e();
               absoluteTargetPos = WrenchItem.getTargetPos(targetFace, absoluteTargetPos);
               BlockPos plateBlockPos = be.func_174877_v();
               Vector3d renderTargetPos = absoluteTargetPos.func_178788_d(Vector3d.func_237491_b_(plateBlockPos));
               int targetBlockLight = this.getTargetLight(be.func_145831_w(), Vector3d.func_237489_a_(rayHit.func_216350_a()), targetFace);
               this.renderPath(be, matrixStack, renderBuffer, absoluteTargetPos, rayHit.func_216354_b(), overlay);
               this.renderTarget(be, matrixStack, renderBuffer, renderTargetPos, targetFace, targetBlockLight, overlay);
            }
         }
      }
   }

   private int getTargetLight(World level, Vector3d pos, Direction face) {
      return WorldRenderer.func_228421_a_(level, new BlockPos(pos.func_178787_e(Vector3d.func_237491_b_(face.func_176730_m()))));
   }

   public void render(FaithPlateTileEntity be, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int light, int overlay) {
      this.renderPlate(be, matrixStack, renderBuffer, overlay);
      this.renderPointedPath(be, matrixStack, renderBuffer, overlay);
      boolean holdingWrench = Minecraft.func_71410_x().field_71439_g != null && WrenchItem.holdingWrench(Minecraft.func_71410_x().field_71439_g);
      if (Minecraft.func_71410_x().field_71474_y.field_74330_P && holdingWrench) {
         this.renderTrigger(be, matrixStack);
      }

      if (be.getTargetFace() != null && be.getTargetPos() != null && !be.func_174877_v().equals(selected)) {
         if (Minecraft.func_71410_x().field_71474_y.field_74330_P && holdingWrench) {
            this.renderPath(be, matrixStack, renderBuffer, be.getTargetPos().func_178787_e(Vector3d.func_237491_b_(be.func_174877_v())), be.getTargetFace(), overlay);
         }

         Vector3d absoluteTargetPos = be.getTargetPos().func_178787_e(Vector3d.func_237489_a_(be.func_174877_v()));
         this.renderTarget(be, matrixStack, renderBuffer, be.getTargetPos(), be.getTargetFace(), this.getTargetLight(be.func_145831_w(), absoluteTargetPos, be.getTargetFace()), overlay);
      }
   }

   public Model getPlateModel() {
      return this.plateModel;
   }

   public boolean shouldRenderOffScreen(FaithPlateTileEntity be) {
      return true;
   }
}
