package net.portalmod.common.sorted.portal;

import net.minecraft.network.PacketBuffer;
import net.portalmod.core.math.Vec3;

public class PartialPortal {
   private final Vec3 position;
   private final Vec3 normal;
   private final Vec3 up;

   public PartialPortal(Vec3 position, Vec3 normal, Vec3 up) {
      this.position = position;
      this.normal = normal;
      this.up = up;
   }

   public PartialPortal(PortalEntity portal) {
      this.position = new Vec3(portal.func_213303_ch());
      this.normal = new Vec3(portal.getNormal());
      this.up = new Vec3(portal.getUpVector());
   }

   public Vec3 getPosition() {
      return this.position;
   }

   public Vec3 getNormal() {
      return this.normal;
   }

   public Vec3 getUpVector() {
      return this.up;
   }

   public OrthonormalBasis getDestinationBasis() {
      return new OrthonormalBasis(this.normal.clone().negate(), this.up);
   }

   public void write(PacketBuffer buffer) {
      buffer.writeDouble(this.position.x).writeDouble(this.position.y).writeDouble(this.position.z).writeDouble(this.normal.x).writeDouble(this.normal.y).writeDouble(this.normal.z).writeDouble(this.up.x).writeDouble(this.up.y).writeDouble(this.up.z);
   }

   public static PartialPortal read(PacketBuffer buffer) {
      Vec3 position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      Vec3 normal = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      Vec3 up = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      return new PartialPortal(position, normal, up);
   }
}
