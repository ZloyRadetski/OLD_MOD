package net.portalmod.common.sorted.radio;

import net.minecraft.client.audio.LocatableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class RadioSound extends LocatableSound {
   public RadioSound(BlockPos pos, SoundEvent sound, boolean looping) {
      super(sound, SoundCategory.RECORDS);
      this.field_147660_d = (double)pos.func_177958_n() + (double)0.5F;
      this.field_147661_e = (double)pos.func_177956_o() + (double)0.5F;
      this.field_147658_f = (double)pos.func_177952_p() + (double)0.5F;
      this.field_147659_g = looping;
      this.field_147662_b = 0.5F;
   }
}
