package net.portalmod.client.screens.widgets;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ToggleButton extends Button {
   private boolean value;

   public ToggleButton(int x, int y, int width, int height, ITextComponent text, Button.IPressable pressable, boolean value) {
      super(x, y, width, height, text, ($1) -> {
      });
      this.value = value;
   }

   public ToggleButton(int x, int y, int width, int height, ITextComponent text, Button.IPressable pressable, boolean value, Button.ITooltip tooltip) {
      super(x, y, width, height, text, ($1) -> {
      }, tooltip);
      this.value = value;
   }

   public void func_230982_a_(double p_230982_1_, double p_230982_3_) {
      super.func_230982_a_(p_230982_1_, p_230982_3_);
      this.value = !this.value;
   }

   public ITextComponent func_230458_i_() {
      return new StringTextComponent(super.func_230458_i_().getString() + ": " + (this.value ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"));
   }

   public boolean getValue() {
      return this.value;
   }
}
