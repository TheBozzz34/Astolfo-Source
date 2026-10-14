package bypass.setting;

import bypass.gui.clickgui.defaultgui.ColorPicker;
import bypass.module.hidden.HypixelPacketHandler;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public final class ColorSetting extends Setting<Color> {
   public ColorMode mode = ColorMode.NONE;

   public ColorSetting(String string, String string2, Color color) {
      super(string, string2, color, SettingType.COLOR);
   }

   public Color getValue() {
      Color color = (Color)super.getValue();
      switch (this.mode) {
         case PULSATE:
            float f = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp_int((int)(color.getAlpha() * f), 0, 255));
         case RAINBOW:
            return ColorPicker.h(HypixelPacketHandler.r);
         default:
            return color;
      }
   }

   public void setValue(Color color) {
      super.setValue(color);
   }

   @Override
   public void parse(String string) {
      String[] strings = string.split(" ");
      if (strings.length == 3) {
         this.setValue(new Color(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2])));
      } else if (strings.length == 4) {
         this.setValue(new Color(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3])));
      } else if (strings.length == 5) {
         this.mode = ColorMode.values()[Integer.parseInt(strings[4])];
         this.setValue(new Color(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3])));
      }
   }

   @Override
   public String asString() {
      return this.value.getRed() + " " + this.value.getGreen() + " " + this.value.getBlue() + " " + this.value.getAlpha() + " " + this.mode.index;
   }
}
