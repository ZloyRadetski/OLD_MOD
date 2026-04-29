package net.portalmod.common.sorted.portalgun.skins;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.portalmod.PortalMod;
import net.portalmod.core.init.PacketInit;

public class ServerSkinManager extends SkinManager {
   private static ServerSkinManager instance;

   private ServerSkinManager() {
      super(false);
   }

   public static ServerSkinManager getInstance() {
      if (instance == null) {
         instance = new ServerSkinManager();
      }

      return instance;
   }

   public File getModFolder() {
      return new File(ServerLifecycleHooks.getCurrentServer().func_71238_n(), "portalmod");
   }

   public void onServerLogin(ServerPlayerEntity player) {
      if (!this.clientSide) {
         this.enqueueTask(() -> {
            try {
               this.loadSkinCatalog();
            } catch (IOException e) {
               PortalMod.LOGGER.error(e.getMessage());
            }

            Map<UUID, String> temp = new HashMap(this.getSelectedSkinMap());
            temp.forEach((uuid, skin) -> {
               if (!uuid.equals(player.func_110124_au())) {
                  int tint = this.getTintForPlayerOnSkin(uuid, skin);
                  PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SSetPlayerSkinPacket(uuid, skin, tint));
               }

            });
         });
      }
   }

   public void onServerReceivedPacket(ServerPlayerEntity player, CSetPlayerSkinPacket packet) {
      if (!this.clientSide) {
         this.enqueueTask(() -> {
            UUID uuid = player.func_110124_au();
            String skin = packet.getSkin();
            int tint = packet.getTint();

            try {
               this.loadPlayer(uuid);
            } catch (IOException e) {
               PortalMod.LOGGER.error(e.getMessage());
            }

            if (!this.playerHasSkin(uuid, skin)) {
               try {
                  this.fetchPlayer(uuid);
                  this.cachePlayer(uuid);
                  if (!this.playerHasSkin(uuid, skin)) {
                     return;
                  }
               } catch (IOException e) {
                  PortalMod.LOGGER.error(e.getMessage());
                  return;
               }
            }

            this.setSkinAndTintForPlayer(uuid, skin, tint);
            PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SSetPlayerSkinPacket(uuid, skin, tint));
         });
      }
   }
}
