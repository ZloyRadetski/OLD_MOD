package net.portalmod.mixins.entity;

import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.TrackedEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.interfaces.ITeleportLerpable;
import net.portalmod.core.packet.SEntityPortalTeleportLerpPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TrackedEntity.class})
public class TrackedEntityMixin {
   @Shadow
   @Final
   private Entity field_219461_c;

   @Redirect(
      method = {"sendChanges"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
   ordinal = 3
)
   )
   private void pmHijackPositionPacket(Consumer<IPacket<?>> instance, Object packetObject) {
      IPacket<?> packet = (IPacket)packetObject;
      ITeleportLerpable victim = (ITeleportLerpable)this.field_219461_c;
      if (victim.hasUsedPortal() && this.field_219461_c instanceof LivingEntity) {
         byte xRot = (byte)MathHelper.func_76141_d(this.field_219461_c.field_70177_z * 256.0F / 360.0F);
         byte yRot = (byte)MathHelper.func_76141_d(this.field_219461_c.field_70125_A * 256.0F / 360.0F);
         PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SEntityPortalTeleportLerpPacket(this.field_219461_c.func_145782_y(), xRot, yRot, this.field_219461_c.func_233570_aj_(), victim.getLerpPositions()));
      } else {
         instance.accept(packet);
      }

      victim.setHasUsedPortal(false);
      victim.getLerpPositions().clear();
   }
}
