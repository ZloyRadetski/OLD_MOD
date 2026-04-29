package net.portalmod.common.sorted.portal;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.portalmod.core.init.ParticleInit;

public class PortalParticle extends SpriteTexturedParticle {
   private final int u;
   private final int v;

   protected PortalParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd, PortalParticleData data, IAnimatedSprite sprite) {
      super(level, x, y, z, xd, yd, zd);
      this.field_70547_e = (int)(Math.random() * (double)10.0F) + 10;
      this.field_70545_g = 1.0F;
      this.func_217566_b(sprite);
      this.field_70544_f /= 2.0F;
      this.u = this.field_187136_p.nextInt(4);
      this.v = this.field_187136_p.nextInt(4);
      this.field_70552_h = data.getRed();
      this.field_70553_i = data.getGreen();
      this.field_70551_j = data.getBlue();
      this.field_82339_as = data.getAlpha();
   }

   public void func_189213_a() {
      super.func_189213_a();
      this.field_82339_as = MathHelper.func_76131_a((float)Math.pow((double)((float)(this.field_70547_e - this.field_70546_d) / 7.0F), (double)2.0F), 0.0F, 1.0F);
      this.field_190015_G = this.field_190014_F;
      Vector3d delta = new Vector3d(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      ActiveRenderInfo camera = Minecraft.func_71410_x().field_71460_t.func_215316_n();
      delta.func_178789_a(camera.func_216777_e()).func_178785_b(camera.func_216778_f() + 180.0F);
      this.field_190014_F = -((float)Math.atan2(delta.field_72448_b, delta.field_72450_a) - ((float)Math.PI / 2F));
      if (this.field_70546_d == 1) {
         this.field_190015_G = this.field_190014_F;
      }

   }

   public static void spawnBurst(ClientWorld level, Vector3d pos, Direction direction) {
      double x = pos.field_72450_a;
      double y = pos.field_72448_b;
      double z = pos.field_72449_c;
      new Random();
      int count = 40;

      for(int i = 0; i < count; ++i) {
         Vector3i normal = direction.func_176730_m();
         level.func_195594_a(new PortalParticleData(0.0F, 0.0F, 1.0F, 1.0F, direction), x, y, z, (double)0.0F, (double)0.0F, (double)0.0F);
      }

   }

   protected float func_217563_c() {
      return this.field_217569_E.func_94214_a((double)(this.u * 4));
   }

   protected float func_217562_e() {
      return this.field_217569_E.func_94207_b((double)(this.v * 4));
   }

   protected float func_217564_d() {
      return this.field_217569_E.func_94214_a((double)((this.u + 1) * 4));
   }

   protected float func_217560_f() {
      return this.field_217569_E.func_94207_b((double)((this.v + 1) * 4));
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217603_c;
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(BasicParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd) {
         PortalParticle particle = new PortalParticle(level, x, y, z, xd, yd, zd, (PortalParticleData)type, this.sprite);
         particle.func_217568_a(this.sprite);
         return particle;
      }
   }

   public static class PortalParticleData extends BasicParticleType {
      public static final IParticleData.IDeserializer<PortalParticleData> DESERIALIZER = new IParticleData.IDeserializer<PortalParticleData>() {
         public PortalParticleData fromCommand(ParticleType<PortalParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float a = reader.readFloat();
            reader.expect(' ');
            Direction direction = Direction.func_82600_a(reader.readInt());
            return new PortalParticleData(r, g, b, a, direction);
         }

         public PortalParticleData fromNetwork(ParticleType<PortalParticleData> type, PacketBuffer packetBuffer) {
            return new PortalParticleData(packetBuffer.readFloat(), packetBuffer.readFloat(), packetBuffer.readFloat(), packetBuffer.readFloat(), Direction.func_82600_a(packetBuffer.readInt()));
         }
      };
      private final float r;
      private final float g;
      private final float b;
      private final float a;
      private final Direction direction;

      public PortalParticleData(float r, float g, float b, float a, Direction direction) {
         super(false);
         this.r = r;
         this.g = g;
         this.b = b;
         this.a = MathHelper.func_76131_a(a, 0.01F, 4.0F);
         this.direction = direction;
      }

      public void func_197553_a(PacketBuffer packetBuffer) {
         packetBuffer.writeFloat(this.r);
         packetBuffer.writeFloat(this.g);
         packetBuffer.writeFloat(this.b);
         packetBuffer.writeFloat(this.a);
         packetBuffer.writeInt(this.direction.func_176745_a());
      }

      public String func_197555_a() {
         return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d", Registry.field_212632_u.func_177774_c(this.func_197554_b()), this.r, this.g, this.b, this.a, this.direction.func_176745_a());
      }

      public BasicParticleType func_197554_b() {
         return (BasicParticleType)ParticleInit.PORTAL_PARTICLE.get();
      }

      public float getRed() {
         return this.r;
      }

      public float getGreen() {
         return this.g;
      }

      public float getBlue() {
         return this.b;
      }

      public float getAlpha() {
         return this.a;
      }

      public Direction getDirection() {
         return this.direction;
      }
   }
}
