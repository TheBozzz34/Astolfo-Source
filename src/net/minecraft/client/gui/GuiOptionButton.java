package net.minecraft.client.gui;

import net.minecraft.client.settings.GameSettings;

public class GuiOptionButton extends GuiButton {
   private final GameSettings.Options enumOptions;

   public GuiOptionButton(int i, int j, int k, String string) {
      this(i, j, k, (GameSettings.Options)null, string);
   }

   public GuiOptionButton(int i, int j, int k, int l, int m, String string) {
      super(i, j, k, l, m, string);
      this.enumOptions = null;
   }

   public GuiOptionButton(int i, int j, int k, GameSettings.Options options, String string) {
      super(i, j, k, 150, 20, string);
      this.enumOptions = options;
   }

   public GameSettings.Options returnEnumOptions() {
      return this.enumOptions;
   }
}
