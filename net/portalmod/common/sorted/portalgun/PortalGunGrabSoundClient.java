package net.portalmod.common.sorted.portalgun;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.portalmod.core.init.SoundInit;

public class PortalGunGrabSoundClient {
   protected static final Map<PlayerEntity, LocatableSound> SOUNDS = new HashMap();

   public static void handlePacket(PlayerEntity player, boolean start) {
      SoundHandler soundManager = Minecraft.func_71410_x().func_147118_V();
      boolean hasSound = SOUNDS.containsKey(player);
      if (start && !hasSound) {
         SOUNDS.put(player, new EntityLoopableSound(player, (SoundEvent)SoundInit.PORTALGUN_HOLD.get(), SoundCategory.PLAYERS));
         soundManager.func_147682_a((ISound)SOUNDS.get(player));
      }

      if (!start && hasSound) {
         soundManager.func_147683_b((ISound)SOUNDS.get(player));
         SOUNDS.remove(player);
      }

   }
}
