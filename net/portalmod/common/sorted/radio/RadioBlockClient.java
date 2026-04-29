package net.portalmod.common.sorted.radio;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.SoundEvent;
import net.portalmod.core.init.SoundInit;

public class RadioBlockClient {
   protected static final Map<RadioBlockTileEntity, RadioSound> RADIOS = new HashMap();

   protected static void handlePacket(RadioBlockTileEntity be, RadioState state) {
      if (!state.isPlaying()) {
         Minecraft.func_71410_x().func_147118_V().func_147683_b((ISound)RADIOS.get(be));
      } else {
         if (state == RadioState.ACTIVE) {
            RADIOS.put(be, new RadioSound(be.func_174877_v(), (SoundEvent)SoundInit.RADIO_DINOSAUR1.get(), false));
         } else if (state == RadioState.ON) {
            RADIOS.put(be, new RadioSound(be.func_174877_v(), (SoundEvent)SoundInit.RADIO_LOOP.get(), true));
         }

         Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)RADIOS.get(be));
      }

   }
}
