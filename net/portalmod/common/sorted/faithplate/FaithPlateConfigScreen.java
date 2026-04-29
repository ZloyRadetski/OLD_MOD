package net.portalmod.common.sorted.faithplate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.portalmod.client.render.Shader;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.ShaderInit;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class FaithPlateConfigScreen extends Screen {
   private static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/gui/faithplate.png");
   private static final String WHOLE_FLOAT_REGEX = "[\\d.,+\\-]*";
   private static final Pattern FLOAT_REGEX = Pattern.compile("^0*([+\\-])?(?:0*(\\d+\\.?\\d*).*)?$");
   private static final Pattern DIGIT_REGEX = Pattern.compile("^([+\\-])?((?:\\d\\.|\\d){1,5})?.*$");
   private static final long VRESIZE_CURSOR = GLFW.glfwCreateStandardCursor(221190);
   private static final int imageWidth = 230;
   private static final int imageHeight = 239;
   private int pitch = 18;
   public static final int MAX_HEIGHT = 100;
   private RenderWidget panel;
   private ExtendedButton selector;
   private ExtendedButton done;
   private NumberInputField heightField;
   private FaithplateCheckboxButton enable;
   private BlockPos selected;
   private FaithPlateParabola parabola;
   int verticalOffset = 75;

   public FaithPlateConfigScreen(BlockPos selected) {
      super(new TranslationTextComponent("screen.portalmod.faithplate"));
      this.selected = selected;
   }

   private int getX() {
      return (this.field_230708_k_ - 230 - 100) / 2;
   }

   private int getY() {
      return (this.field_230709_l_ - 239 + 55) / 2;
   }

   protected void func_231160_c_() {
      FaithPlateTileEntity be = (FaithPlateTileEntity)Minecraft.func_71410_x().field_71441_e.func_175625_s(this.selected);
      int buttonWidth = 107;
      this.panel = (RenderWidget)this.func_230481_d_(new RenderWidget(this, this.getX() + 10, this.getY() + 22, 210, 121, new StringTextComponent("Render Panel")));
      this.selector = (ExtendedButton)this.func_230480_a_(new ExtendedButton(this.getX() + 7, this.getY() + 150, buttonWidth, 20, new TranslationTextComponent("container.faithplate.select"), (button) -> {
         FaithPlateTER.selected = this.selected;
         this.func_231175_as__();
      }));
      this.done = (ExtendedButton)this.func_230480_a_(new ExtendedButton(this.getX() + 9 + buttonWidth, this.getY() + 150, buttonWidth, 20, new TranslationTextComponent("gui.done"), (button) -> this.func_231175_as__()));
      this.enable = (FaithplateCheckboxButton)this.func_230480_a_(new FaithplateCheckboxButton(this.getX() + 230, this.getY() + 25 + this.verticalOffset, 20, 20, new TranslationTextComponent("container.faithplate.enabled"), be.isEnabled()));
      this.enable.setUnavailable(be.isIndicatorControlled());
      this.heightField = (NumberInputField)this.func_230481_d_(new NumberInputField(this, this.field_230712_o_, this.getX() + 230, this.getY() + 70 + this.verticalOffset, 85, 20, new StringTextComponent("Height")));
      float middle = 0.0F;
      float target = 0.0F;
      if (be.getTargetPos() != null && be.getTargetFace() != null) {
         Vec3 normal = (new Vec3(be.getTargetFace().func_176730_m())).mul((double)0.5F);
         Vec3 pos = (new Vec3(be.getTargetPos())).add((double)0.0F, (double)-0.5F, (double)0.0F).add(normal);
         this.parabola = new FaithPlateParabola(pos);
         this.parabola.setHeight((double)be.getHeight());
         middle = (float)this.parabola.getMiddlePoint();
         target = (float)this.parabola.getProjectedTarget().x;
      } else {
         this.panel.setEnabled(false);
         this.heightField.func_146184_c(false);
      }

      this.updateField();
      ((Shader)ShaderInit.FAITHPLATE_GUI.get()).bind().setMatrix("modelViewProjection", Mat4.createScale((double)((float)this.pitch * 2.0F / (float)this.panel.func_230998_h_()), (double)((float)this.pitch * 2.0F / (float)this.panel.func_238483_d_()), (double)1.0F).toBuffer()).unbind();
      ((Shader)ShaderInit.FAITHPLATE_GRID.get()).bind().setMatrix("modelViewProjection", Mat4.identity().toBuffer()).setInt("res", this.panel.func_230998_h_(), this.panel.func_238483_d_()).setInt("pitch", this.pitch).setFloat("middle", middle).setFloat("target", target).setInt("offset", -90, -45).unbind();
   }

   public void func_231175_as__() {
      World level = Minecraft.func_71410_x().field_71441_e;
      if (level != null) {
         FaithPlateTileEntity be = (FaithPlateTileEntity)level.func_175625_s(this.selected);
         if (be != null) {
            Vector3d pos = be.getTargetPos();
            Direction face = be.getTargetFace();
            CompoundNBT nbt = new CompoundNBT();
            if (pos != null && face != null) {
               CompoundNBT target = new CompoundNBT();
               target.func_74780_a("x", pos.func_82615_a());
               target.func_74780_a("y", pos.func_82617_b());
               target.func_74780_a("z", pos.func_82616_c());
               target.func_74774_a("side", (byte)face.func_176745_a());
               target.func_74776_a("height", (float)this.parabola.getHeight());
               nbt.func_218657_a("target", target);
            }

            nbt.func_74757_a("enabled", this.enable.selected());
            PacketInit.INSTANCE.sendToServer(new CFaithPlateUpdatedPacket(be.func_174877_v(), nbt));
            if (FaithPlateTER.selected == null) {
               PacketInit.INSTANCE.sendToServer(new CFaithPlateEndConfigPacket(be.func_174877_v()));
            }
         }

         Minecraft.func_71410_x().func_147108_a((Screen)null);
         this.setCursor(0L);
      }
   }

   private void updateField() {
      if (this.panel.enabled) {
         String text = "";
         Matcher matcher = DIGIT_REGEX.matcher(this.parabola.getHeight() + "");
         if (this.parabola.getHeight() == (double)((int)this.parabola.getHeight())) {
            matcher = DIGIT_REGEX.matcher((int)this.parabola.getHeight() + "");
         }

         if (matcher.matches()) {
            text = text + (matcher.group(1) != null ? matcher.group(1) : "");
            text = text + (matcher.group(2) != null ? matcher.group(2) : "");
         } else {
            text = "0";
         }

         this.heightField.func_146180_a(text);
      }
   }

   private void updateParabola() {
      if (this.panel.enabled) {
         double height;
         try {
            height = Double.parseDouble(this.heightField.func_146179_b());
         } catch (NumberFormatException var4) {
            height = (double)0.0F;
         }

         this.parabola.setHeight(height);
      }
   }

   private void setCursor(long cursor) {
      GLFW.glfwSetCursor(Minecraft.func_71410_x().func_228018_at_().func_198092_i(), cursor);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int action) {
      this.setCursor(0L);
      return super.func_231048_c_(mouseX, mouseY, action);
   }

   public void func_238651_a_(MatrixStack matrixStack, int i) {
      super.func_238651_a_(matrixStack, i);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      GL11.glEnable(3042);
      func_238463_a_(matrixStack, this.getX(), this.getY(), 0.0F, 0.0F, 330, 179, 512, 512);
      GL11.glDisable(3042);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      this.panel.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.selector.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.done.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.enable.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.heightField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      fontRenderer.func_243248_b(matrixStack, new TranslationTextComponent("container.faithplate"), (float)(this.getX() + 11), (float)(this.getY() + 8), 16777215);
      func_238475_b_(matrixStack, fontRenderer, new TranslationTextComponent("container.faithplate.height"), this.getX() + 230, this.getY() + 53 + this.verticalOffset, -1);
      if (!this.panel.enabled) {
         func_238472_a_(matrixStack, fontRenderer, new TranslationTextComponent("container.faithplate.noTarget"), (this.field_230708_k_ - 100) / 2, (this.getY() + this.panel.field_230691_m_ + this.panel.func_238483_d_()) / 2, -43691);
      }

   }

   private static class RenderWidget extends Widget {
      private final FaithPlateConfigScreen parent;
      private boolean handleClicked = false;
      private boolean enabled = true;
      private Vector2f offset = new Vector2f(0.0F, 0.0F);
      private Vector2f baseOffset = new Vector2f(-90.0F, 45.0F);
      private static VertexBuffer vbo;

      public RenderWidget(FaithPlateConfigScreen parent, int x, int y, int width, int height, ITextComponent text) {
         super(x, y, width, height, text);
         this.parent = parent;
         MainWindow window = Minecraft.func_71410_x().func_228018_at_();
         double guiScale = window.func_198100_s();
         if (vbo != null) {
            vbo.close();
         }

         vbo = new VertexBuffer(DefaultVertexFormats.field_181707_g);
         float fbx = (float)((double)((float)(parent.field_230708_k_ - this.func_230998_h_() - 100) / 2.0F) * guiScale);
         float fby = (float)((double)(parent.field_230709_l_ - (y + this.func_238483_d_())) * guiScale);
         float fbw = fbx + (float)((int)((double)this.func_230998_h_() * guiScale));
         float fbh = fby + (float)((int)((double)this.func_238483_d_() * guiScale));
         BufferBuilder bufferbuilder = Tessellator.func_178181_a().func_178180_c();
         bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         bufferbuilder.func_225582_a_((double)fbx, (double)fby, (double)0.0F).func_225583_a_(0.0F, 1.0F).func_181675_d();
         bufferbuilder.func_225582_a_((double)fbw, (double)fby, (double)0.0F).func_225583_a_(1.0F, 1.0F).func_181675_d();
         bufferbuilder.func_225582_a_((double)fbw, (double)fbh, (double)0.0F).func_225583_a_(1.0F, 0.0F).func_181675_d();
         bufferbuilder.func_225582_a_((double)fbx, (double)fbh, (double)0.0F).func_225583_a_(0.0F, 0.0F).func_181675_d();
         bufferbuilder.func_178977_d();
         vbo.func_227875_a_(bufferbuilder);
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }

      public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
         if (this.enabled) {
            double a = (double)0.0F;
            double b = (double)0.0F;
            if (this.parent.parabola != null) {
               a = this.parent.parabola.getA();
               b = this.parent.parabola.getB();
            }

            Minecraft mc = Minecraft.func_71410_x();
            MainWindow window = mc.func_228018_at_();
            double guiScale = window.func_198100_s();
            int fbX = (int)((double)((float)(this.parent.field_230708_k_ - this.func_230998_h_() - 100) / 2.0F) * guiScale);
            int fbY = (int)((double)(this.parent.field_230709_l_ - (this.field_230691_m_ + this.func_238483_d_())) * guiScale);
            int fbW = (int)((double)this.func_230998_h_() * guiScale);
            int fbH = (int)((double)this.func_238483_d_() * guiScale);
            GL11.glViewport(fbX, fbY, fbW, fbH);
            RenderSystem.activeTexture(33984);
            mc.func_110434_K().func_110577_a(FaithPlateConfigScreen.TEXTURE);
            RenderSystem.enableBlend();
            ((Shader)ShaderInit.FAITHPLATE_GRID.get()).bind().setInt("sprite", 0).setInt("offset", (int)this.offset.field_189982_i - 90, (int)this.offset.field_189983_j - 45).setInt("guisize", (int)guiScale).setFloat("a", (float)a).setFloat("b", (float)b).setFloat("atlasSize", 512.0F, 512.0F).setFloat("height", (float)this.parent.parabola.getHeight());
            GL11.glBegin(7);
            GL11.glVertex2f(-1.0F, -1.0F);
            GL11.glVertex2f(1.0F, -1.0F);
            GL11.glVertex2f(1.0F, 1.0F);
            GL11.glVertex2f(-1.0F, 1.0F);
            GL11.glEnd();
            ((Shader)ShaderInit.FAITHPLATE_GRID.get()).unbind();
            RenderSystem.disableBlend();
            GL11.glViewport(0, 0, window.func_198109_k(), window.func_198091_l());
         }
      }

      protected boolean func_230992_c_(double mouseX, double mouseY) {
         return this.enabled ? super.func_230992_c_(mouseX, mouseY) : false;
      }

      public void func_230982_a_(double mouseX, double mouseY) {
         if (this.enabled) {
            int offsetX = (int)(this.baseOffset.field_189982_i + this.offset.field_189982_i) + this.field_230688_j_ / 2;
            int offsetY = (int)(this.baseOffset.field_189983_j - this.offset.field_189983_j) + this.field_230689_k_ / 2;
            int x = (int)(mouseX - (double)this.field_230690_l_ - (double)offsetX - (double)this.parent.pitch * this.parent.parabola.getMiddlePoint());
            int y = (int)(mouseY - (double)this.field_230691_m_ - (double)offsetY + (double)this.parent.pitch * this.parent.parabola.getHeight());
            this.handleClicked = x * x + y * y < 25;
            if (this.handleClicked) {
               this.parent.setCursor(FaithPlateConfigScreen.VRESIZE_CURSOR);
            }

         }
      }

      public boolean func_231043_a_(double mouseX, double mouseY, double amount) {
         if (!this.enabled) {
            return false;
         } else {
            int oldPitch = this.parent.pitch;
            FaithPlateConfigScreen var8 = this.parent;
            var8.pitch = (int)((double)var8.pitch + amount);
            this.parent.pitch = MathHelper.func_76125_a(this.parent.pitch, 5, 50);
            ((Shader)ShaderInit.FAITHPLATE_GRID.get()).bind().setInt("pitch", this.parent.pitch).unbind();
            int offsetX = (int)(this.baseOffset.field_189982_i + this.offset.field_189982_i) + this.field_230688_j_ / 2;
            int offsetY = (int)(this.baseOffset.field_189983_j - this.offset.field_189983_j) + this.field_230689_k_ / 2;
            float xMouseRelative = (float)(mouseX - (double)this.field_230690_l_ - (double)offsetX);
            float yMouseRelative = (float)(mouseY - (double)this.field_230691_m_ - (double)offsetY);
            float xOld = xMouseRelative / (float)oldPitch;
            float yOld = yMouseRelative / (float)oldPitch;
            float xNew = xMouseRelative / (float)this.parent.pitch;
            float yNew = yMouseRelative / (float)this.parent.pitch;
            this.offset = new Vector2f(this.offset.field_189982_i + (xNew - xOld) * (float)this.parent.pitch, this.offset.field_189983_j - (yNew - yOld) * (float)this.parent.pitch);
            return true;
         }
      }

      protected void func_230983_a_(double mouseX, double mouseY, double deltaX, double deltaY) {
         if (this.enabled) {
            if (this.handleClicked) {
               if (!Screen.func_231174_t_() && !Screen.func_231172_r_() && !Screen.func_231173_s_()) {
                  this.parent.parabola.setHeight(-(mouseY - (double)this.field_230691_m_ - (double)this.baseOffset.field_189983_j - (double)(this.field_230689_k_ / 2) + (double)this.offset.field_189983_j) / (double)this.parent.pitch);
               } else {
                  this.parent.parabola.setHeight((double)(-Math.round((mouseY - (double)this.field_230691_m_ - (double)this.baseOffset.field_189983_j - (double)(this.field_230689_k_ / 2) + (double)this.offset.field_189983_j) / (double)this.parent.pitch)));
               }
            } else {
               this.offset = new Vector2f(this.offset.field_189982_i + (float)deltaX, this.offset.field_189983_j - (float)deltaY);
            }

            this.parent.updateField();
         }
      }
   }

   private static class NumberInputField extends TextFieldWidget {
      private final FaithPlateConfigScreen parent;

      public NumberInputField(FaithPlateConfigScreen parent, FontRenderer font, int x, int y, int width, int height, ITextComponent text) {
         super(font, x, y, width, height, text);
         this.parent = parent;
      }

      public void func_146175_b(int len) {
         if (this.parent.panel.enabled) {
            super.func_146175_b(len);
            if (this.func_146179_b().isEmpty()) {
               this.func_146180_a("0");
            }

            this.parent.updateParabola();
         }
      }

      public void func_146191_b(String text) {
         if (this.parent.panel.enabled && text.matches("[\\d.,+\\-]*") && this.func_146179_b().replace(".", "").replace("+", "").replace("-", "").length() < 5) {
            String recovery = this.func_146179_b();
            text = text.replaceAll(",", ".");
            if (text.contains(".") && this.func_146179_b().contains(".")) {
               text = text.replaceAll(".", "");
            }

            super.func_146191_b(text);
            Matcher matcher = FaithPlateConfigScreen.FLOAT_REGEX.matcher(this.func_146179_b());
            if (matcher.matches()) {
               text = "";
               text = text + (matcher.group(1) != null ? matcher.group(1) : "");
               text = text + (matcher.group(2) != null ? matcher.group(2) : "");
            } else {
               text = recovery;
            }

            matcher = FaithPlateConfigScreen.DIGIT_REGEX.matcher(text);
            if (matcher.matches()) {
               text = "";
               text = text + (matcher.group(1) != null ? matcher.group(1) : "");
               text = text + (matcher.group(2) != null ? matcher.group(2) : "");
            } else {
               text = recovery;
            }

            if (text.isEmpty()) {
               text = "0";
            }

            this.func_146180_a(text);
            this.parent.updateParabola();
         }
      }

      public boolean func_230999_j_() {
         return !this.parent.panel.enabled ? false : super.func_230999_j_();
      }
   }
}
