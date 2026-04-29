package net.portalmod.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.portalmod.PortalMod;

public class ClientModUtil {
   public static void sendClientChat(Object... text) {
      ClientWorld clientWorld = Minecraft.func_71410_x().field_71441_e;
      if (clientWorld == null) {
         PortalMod.LOGGER.error("Tried to send a client chat message while not in a client environment");
      } else {
         boolean isClientSide = Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER;
         ModUtil.sendChat(clientWorld, isClientSide, text);
      }
   }

   public static PlayerEntity getLocalPlayer() {
      return Minecraft.func_71410_x().field_71439_g;
   }
}
