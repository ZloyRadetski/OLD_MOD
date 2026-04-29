package net.portalmod.core.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalPhotonParticle;
import net.portalmod.common.sorted.portal.SPortalShotPacket;
import net.portalmod.common.sorted.portalgun.SPortalGunFailShotPacket;
import net.portalmod.common.sorted.sign.ChamberSignEntity;
import net.portalmod.common.sorted.trigger.STriggerStartConfigPacket;
import net.portalmod.common.sorted.trigger.TriggerSelectionClient;
import net.portalmod.common.sorted.trigger.TriggerTileEntity;

public class ClientPacketHandler {
   public static void handleSPortalShotPacket(SPortalShotPacket packet) {
      World level = Minecraft.func_71410_x().field_71441_e;
      if (level != null) {
         PortalEntity portal = (PortalEntity)level.func_73045_a(packet.id);
         if (portal != null) {
            PortalPhotonParticle.createOpeningParticles(portal);
         }

      }
   }

   public static void handleSPortalGunFailShotPacket(SPortalGunFailShotPacket packet) {
      PortalPhotonParticle.createFailParticles(Minecraft.func_71410_x().field_71441_e, packet.position, packet.normal, packet.upVector, packet.dyeColor);
   }

   public static void handleSSpawnChamberSignPacket(SSpawnChamberSignPacket packet) {
      ClientWorld level = Minecraft.func_71410_x().field_71441_e;
      if (level != null) {
         ChamberSignEntity entity = new ChamberSignEntity(level, packet.pos, packet.direction, packet.verticallyAligned);
         entity.func_145769_d(packet.id);
         entity.func_184221_a(packet.uuid);
         level.func_217411_a(entity.func_145782_y(), entity);
      }
   }

   public static void handleSTriggerStartConfigPacket(STriggerStartConfigPacket packet) {
      World level = Minecraft.func_71410_x().field_71441_e;
      if (level != null) {
         TileEntity be = level.func_175625_s(packet.pos);
         if (be instanceof TriggerTileEntity) {
            TriggerSelectionClient.startSelecting((TriggerTileEntity)be);
         }

      }
   }
}
