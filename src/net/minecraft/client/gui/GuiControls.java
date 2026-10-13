package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiControls extends GuiScreen {
   private static final GameSettings.Options[] optionsArr = new GameSettings.Options[]{
      GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN
   };
   private GuiScreen parentScreen;
   public String screenTitle = "Controls";
   private GameSettings options;
   public KeyBinding buttonId = null;
   public long time;
   private GuiKeyBindingList keyBindingList;
   private GuiButton buttonReset;
   private static final long b = 91663225943679L;

   public GuiControls(GuiScreen var1, GameSettings var2) {
      this.parentScreen = var1;
      this.options = var2;
   }

   @Override
   public void initGui() {
      this.keyBindingList = new GuiKeyBindingList(this, this.mc);
      this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
      this.screenTitle = I18n.format("controls.title");
      int var4 = 0;

      for (GameSettings.Options var8 : optionsArr) {
         if (var8.getEnumFloat()) {
            this.buttonList.add(new GuiOptionSlider(var8.returnEnumOrdinal(), this.width / 2 - 155 + var4 % 2 * 160, 18 + 24 * (var4 >> 1), var8));
         } else {
            this.buttonList
               .add(
                  new GuiOptionButton(
                     var8.returnEnumOrdinal(), this.width / 2 - 155 + var4 % 2 * 160, 18 + 24 * (var4 >> 1), var8, this.options.getKeyBinding(var8)
                  )
               );
         }

         var4++;
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.keyBindingList.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == 200) {
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (var1.id == 201) {
         for (KeyBinding var8 : this.mc.gameSettings.keyBindings) {
            var8.setKeyCode(var8.getKeyCodeDefault());
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (var1.id < 100 && var1 instanceof GuiOptionButton) {
         this.options.setOptionValue(((GuiOptionButton)var1).returnEnumOptions(), 1);
         var1.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(var1.id));
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      if (this.buttonId != null) {
         this.options.setOptionKeyBinding(this.buttonId, -100 + var3);
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (var3 != 0 || !this.keyBindingList.mouseClicked(var1, var2, var3)) {
         super.mouseClicked(var1, var2, var3);
      }
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      if (var3 != 0 || !this.keyBindingList.mouseReleased(var1, var2, var3)) {
         super.mouseReleased(var1, var2, var3);
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (this.buttonId != null) {
         if (var2 == 1) {
            this.options.setOptionKeyBinding(this.buttonId, 0);
         } else if (var2 != 0) {
            this.options.setOptionKeyBinding(this.buttonId, var2);
         } else if (var1 > 0) {
            this.options.setOptionKeyBinding(this.buttonId, var1 + 256);
         }

         this.buttonId = null;
         this.time = Minecraft.getSystemTime();
         KeyBinding.resetKeyBindingArrayAndHash();
      } else {
         super.keyTyped(var1, var2);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.keyBindingList.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
      boolean var7 = true;

      for (KeyBinding var11 : this.options.keyBindings) {
         if (var11.getKeyCode() != var11.getKeyCodeDefault()) {
            var7 = false;
            break;
         }
      }

      this.buttonReset.enabled = !var7;
      super.drawScreen(var1, var2, var3);
   }
}
