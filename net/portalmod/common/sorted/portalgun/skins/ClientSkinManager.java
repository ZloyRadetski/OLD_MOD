package net.portalmod.common.sorted.portalgun.skins;

import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.portalmod.PortalMod;
import net.portalmod.client.animation.PortalGunAnimatedTexture;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.init.PacketInit;

public class ClientSkinManager extends SkinManager {
   private static ClientSkinManager instance;

   private ClientSkinManager() {
      super(true);
   }

   public static ClientSkinManager getInstance() {
      if (instance == null) {
         instance = new ClientSkinManager();
      }

      return instance;
   }

   public File getModFolder() {
      return new File(Minecraft.func_71410_x().field_71412_D, "portalmod");
   }

   public ResourceLocation getSkinLocation(String id) {
      return new ResourceLocation("portalmod", "portalgun/" + id);
   }

   public PortalGunAnimatedTexture getSkinTexture(String id) {
      return (PortalGunAnimatedTexture)Minecraft.func_71410_x().field_71446_o.func_229267_b_(this.getSkinLocation(id));
   }

   protected void uploadAllSkins(boolean overwrite) {
      this.getSkinCatalog().forEach((k, v) -> this.uploadSkin(v.skin_id, overwrite));
   }

   protected void uploadSkin(String id, boolean overwrite) {
      Texture currentTexture = Minecraft.func_71410_x().field_71446_o.func_229267_b_(this.getSkinLocation(id));
      if (!(currentTexture instanceof PortalGunAnimatedTexture) || overwrite) {
         PortalGunAnimatedTexture skinTexture = new PortalGunAnimatedTexture(id, ((PortalGunSkin)this.getSkinCatalog().get(id)).framerate);
         Minecraft.func_71410_x().field_71446_o.func_229263_a_(this.getSkinLocation(id), skinTexture);
      }

   }

   public boolean hasUUID() {
      return !Minecraft.func_71410_x().func_110432_I().func_148255_b().isEmpty();
   }

   public Optional<UUID> getOwnUUID() {
      String playerUUIDString = Minecraft.func_71410_x().func_110432_I().func_148255_b();

      UUID playerUUID;
      try {
         playerUUID = UUIDTypeAdapter.fromString(playerUUIDString);
      } catch (IllegalArgumentException var4) {
         PortalMod.LOGGER.error("Failed to get own UUID");
         return Optional.empty();
      }

      return Optional.of(playerUUID);
   }

   public void onClientStartup() {
      if (this.clientSide) {
         try {
            this.loadSkinCatalog();
            this.uploadAllSkins(true);
         } catch (IOException e) {
            PortalMod.LOGGER.error(e.getMessage());
         }

         String selectedSkin = (String)PortalModConfigManager.PORTALGUN_SKIN.get();
         int tint = (Integer)PortalModConfigManager.SKIN_TINT.get();
         if (!selectedSkin.equals("default") || (Boolean)PortalModConfigManager.HAS_SKINS.get()) {
            Optional<UUID> optionalUUID = this.getOwnUUID();
            if (optionalUUID.isPresent()) {
               UUID uuid = (UUID)optionalUUID.get();

               try {
                  this.fetchNewSkins();
                  this.fetchPlayer(uuid);
                  this.updateConfigHasSkinsFlag();
                  this.uploadAllSkins(true);
                  selectedSkin = this.adjustSelectedSkin(selectedSkin);
                  this.setConfigSelectedSkin(selectedSkin);
                  this.setSelectedSkinForPlayer((UUID)null, selectedSkin);
                  if (this.getSkinDefinition(selectedSkin).tintable) {
                     tint = this.adjustTint(tint);
                     this.setTintForPlayer(uuid, tint);
                  }
               } catch (IOException e) {
                  PortalMod.LOGGER.error(e.getMessage());
               }
            }

         }
      }
   }

   public void onClientLogin() {
      if (this.clientSide) {
         this.clearSelectedSkinMapExceptSelf();
         this.clearTintMapExceptSelf();
         String selectedSkin = this.getSelectedSkinForPlayer((UUID)null);
         int tint = this.getTintForPlayerOnSkin((UUID)null, selectedSkin);
         PacketInit.INSTANCE.sendToServer(new CSetPlayerSkinPacket(selectedSkin, tint));
      }
   }

   public void onClientReceivedPacket(SSetPlayerSkinPacket packet) {
      if (this.clientSide) {
         this.enqueueTask(() -> CompletableFuture.runAsync(() -> {
               UUID uuid = packet.getPlayer();
               String skin = packet.getSkin();
               int tint = this.isSkinTintable(skin) ? packet.getTint() : 0;
               this.setSkinAndTintForPlayer(uuid, skin, tint);
            }));
      }
   }

   public void onSkinCatalogRefresh() {
      if (this.clientSide) {
         this.enqueueTask(() -> {
            Optional<UUID> ownUUID = this.getOwnUUID();
            if (ownUUID.isPresent()) {
               try {
                  this.fetchPlayer((UUID)ownUUID.get());
                  if (!(Boolean)PortalModConfigManager.HAS_SKINS.get()) {
                     this.applyConfigDefaultTint();
                  }

                  this.updateConfigHasSkinsFlag();
               } catch (IOException e) {
                  PortalMod.LOGGER.error(e.getMessage());
               }
            }

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var3) {
            }
         });
      }
   }

   public void onSkinSelected(String skin, int tint) {
      if (this.clientSide) {
         tint = this.isSkinTintable(skin) ? tint : 0;
         this.setSkinAndTintForPlayer((UUID)null, skin, tint);
         this.setConfigSelectedSkin(skin);
         if (this.isSkinTintable(skin)) {
            this.setConfigTint(tint);
         }

         if (Minecraft.func_71410_x().func_147114_u() != null) {
            PacketInit.INSTANCE.sendToServer(new CSetPlayerSkinPacket(skin, tint));
         }

      }
   }
}
