package net.portalmod.common.sorted.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.portalmod.common.entities.TestElementEntityRenderer;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class TurretRenderer extends TestElementEntityRenderer<TurretEntity, TurretModel<TurretEntity>> {
   private static final ResourceLocation[] TEXTURE = new ResourceLocation[]{new ResourceLocation("portalmod", "textures/entity/turret/turret.png"), new ResourceLocation("portalmod", "textures/entity/turret/turret_off.png")};
   private static final VertexBuffer LASER_BUFFER;
   public static TurretState state;

   public TurretRenderer(EntityRendererManager erm) {
      super(erm, new TurretModel(), 0.5F);
      this.func_177094_a(new TurretEyeLayer(this));
   }

   public ResourceLocation getTextureLocation(TurretEntity turret) {
      return TEXTURE[turret.getState() == TurretState.DEAD ? 1 : 0];
   }

   public void render(TurretEntity turret, float a, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light) {
      TurretState turretState = turret.getState();
      float fallAnimTick = Math.min((float)turret.getAnimationTicks() + partialTicks, (float)turret.fallDuration);
      float fallAmount = 90.0F * fallAnimTick * fallAnimTick / (float)(turret.fallDuration * turret.fallDuration);
      int tipDir = turret.getTipDirectionRight() ? 1 : -1;
      fallAmount *= (float)tipDir;
      float tipOffset = 0.2F;
      float halfModelHeight = 0.8F;
      if (turretState == TurretState.DEAD) {
         fallAmount = 90.0F * (float)tipDir;
      }

      if (!turretState.isStanding()) {
         Vector3f lookAngle = new Vector3f(turret.func_70040_Z().func_216372_d((double)1.0F, (double)0.0F, (double)1.0F).func_72432_b());
         matrixStack.func_227860_a_();
         float progress = turretState == TurretState.FALLING ? fallAnimTick / (float)turret.fallDuration : 1.0F;
         matrixStack.func_227861_a_((double)(lookAngle.func_195902_c() * halfModelHeight * (float)tipDir * progress), (double)0.0F, (double)(-lookAngle.func_195899_a() * halfModelHeight * (float)tipDir * progress));
         matrixStack.func_227861_a_((double)(-lookAngle.func_195902_c() * tipOffset * (float)tipDir), (double)0.0F, (double)(lookAngle.func_195899_a() * tipOffset * (float)tipDir));
         matrixStack.func_227863_a_(lookAngle.func_229187_a_(fallAmount));
         matrixStack.func_227861_a_((double)(lookAngle.func_195902_c() * tipOffset * (float)tipDir), (double)0.0F, (double)(-lookAngle.func_195899_a() * tipOffset * (float)tipDir));
      }

      super.render(turret, a, partialTicks, matrixStack, renderTypeBuffer, light);
      if (!turretState.isStanding()) {
         matrixStack.func_227865_b_();
      }

      if (!turret.isFizzling()) {
         float rotation = -MathHelper.func_219799_g(partialTicks, turret.field_70760_ar, turret.field_70761_aq) * ((float)Math.PI / 180F);
         float z = MathHelper.func_76134_b(rotation);
         float x = MathHelper.func_76126_a(rotation);
         Vector3d localEyePos = new Vector3d((double)x * (double)2.5F / (double)16.0F, (double)turret.func_70047_e(), (double)z * (double)2.5F / (double)16.0F);
         Vec3 turretEyePos = new Vec3(turret.func_242282_l(partialTicks).func_178787_e(localEyePos));
         Vec3 turretEyeToCamera = (new Vec3(Minecraft.func_71410_x().field_71460_t.func_215316_n().func_216785_c())).sub(turretEyePos).normalize();
         Vector3d targetPos;
         if (turret.hasTarget() && turret.shouldLaserMove()) {
            targetPos = turret.targetEntity.func_242282_l(partialTicks).func_72441_c((double)0.0F, (double)turret.targetEntity.func_213302_cg() * (double)0.5F, (double)0.0F);
         } else {
            targetPos = turretEyePos.to3d().func_178787_e((new Vector3d((double)x, (double)0.0F, (double)z)).func_186678_a((double)5.0F));
         }

         if (turret.lastLaserPos == Vector3d.field_186680_a) {
            turret.lastLaserPos = targetPos;
         }

         if (turret.shouldLaserEase()) {
            double deltaTime = (double)Minecraft.func_71410_x().func_193989_ak();
            targetPos = turret.lastLaserPos.func_178787_e(targetPos.func_178788_d(turret.lastLaserPos).func_186678_a((double)1.0F - Math.exp((double)-0.5F * deltaTime)));
         }

         Vector3d turretToTarget = targetPos.func_178788_d(turretEyePos.to3d());
         turret.turretToTarget = turretToTarget;
         Vec3 laserForward = (new Vec3(turretToTarget)).normalize();
         Vec3 projectedTurretEyeToCamera = turretEyeToCamera.sub(laserForward.clone().mul(turretEyeToCamera.dot(laserForward)));
         Vec3 laserUp = projectedTurretEyeToCamera.clone().normalize();
         Vec3 laserRight = laserUp.clone().cross(laserForward).normalize();
         Mat4 laserMatrix = new Mat4(laserRight.x, laserUp.x, laserForward.x, (double)0.0F, laserRight.y, laserUp.y, laserForward.y, (double)0.0F, laserRight.z, laserUp.z, laserForward.z, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F);
         Vector3d rayPath = turretToTarget.func_186678_a((double)(Minecraft.func_71410_x().field_71460_t.func_205001_m() * 2.0F));
         Vector3d from = turretEyePos.to3d();
         Vector3d to = from.func_178787_e(rayPath);
         float laserLen = (float)((Vector3d)turret.traceAsFarAsPossible(from, to).getSecond()).func_178788_d(from).func_72433_c();
         float opacity = 1.0F - ((float)Math.sin((double)System.currentTimeMillis() / (double)1000.0F) + 1.0F) / 2.0F * 0.3F;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(localEyePos.field_72450_a, localEyePos.field_72448_b, localEyePos.field_72449_c);
         matrixStack.func_227866_c_().func_227870_a_().func_226595_a_(laserMatrix.to4f());
         BufferBuilder bufferBuilder = Tessellator.func_178181_a().func_178180_c();
         bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         bufferBuilder.func_225582_a_((double)0.03125F, (double)0.0F, (double)0.0F).func_225583_a_(0.0F, 0.0F).func_227885_a_(1.0F, 1.0F, 1.0F, opacity).func_181675_d();
         bufferBuilder.func_225582_a_((double)-0.03125F, (double)0.0F, (double)0.0F).func_225583_a_(0.0F, 1.0F).func_227885_a_(1.0F, 1.0F, 1.0F, opacity).func_181675_d();
         bufferBuilder.func_225582_a_((double)-0.03125F, (double)0.0F, (double)laserLen).func_225583_a_(laserLen / 4.0F, 1.0F).func_227885_a_(1.0F, 1.0F, 1.0F, opacity).func_181675_d();
         bufferBuilder.func_225582_a_((double)0.03125F, (double)0.0F, (double)laserLen).func_225583_a_(laserLen / 4.0F, 0.0F).func_227885_a_(1.0F, 1.0F, 1.0F, opacity).func_181675_d();
         bufferBuilder.func_178977_d();
         LASER_BUFFER.func_227875_a_(bufferBuilder);
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         Minecraft.func_71410_x().field_71446_o.func_110577_a(new ResourceLocation("portalmod", "textures/entity/turret/laser.png"));
         LASER_BUFFER.func_177359_a();
         DefaultVertexFormats.field_181709_i.func_227892_a_(0L);
         if (turretState.isStanding() && turret.getHurtTime() == 0) {
            LASER_BUFFER.func_227874_a_(matrixStack.func_227866_c_().func_227870_a_(), 7);
         }

         VertexBuffer.func_177361_b();
         DefaultVertexFormats.field_181709_i.func_227895_d_();
         matrixStack.func_227865_b_();
         turret.lastLaserPos = targetPos;
      }
   }

   public boolean shouldRender(TurretEntity p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return true;
   }

   static {
      LASER_BUFFER = new VertexBuffer(DefaultVertexFormats.field_181709_i);
      state = TurretState.RESTING;
   }
}
