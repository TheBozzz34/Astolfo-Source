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

   public GuiControls(GuiScreen screen, GameSettings settings) {
      this.parentScreen = screen;
      this.options = settings;
   }

   @Override
   public void initGui() {
      this.keyBindingList = new GuiKeyBindingList(this, this.mc);
      this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
      this.screenTitle = I18n.format("controls.title");
      int i = 0;

      for (GameSettings.Options options2 : optionsArr) {
         if (options2.getEnumFloat()) {
            this.buttonList.add(new GuiOptionSlider(options2.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), options2));
         } else {
            this.buttonList
               .add(
                  new GuiOptionButton(
                     options2.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), options2, this.options.getKeyBinding(options2)
                  )
               );
         }

         i++;
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.keyBindingList.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 200) {
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 201) {
         for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
            keyBinding.setKeyCode(keyBinding.getKeyCodeDefault());
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (button.id < 100 && button instanceof GuiOptionButton) {
         this.options.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
         button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (this.buttonId != null) {
         this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
         super.mouseReleased(mouseX, mouseY, state);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (this.buttonId != null) {
         if (keyCode == 1) {
            this.options.setOptionKeyBinding(this.buttonId, 0);
         } else if (keyCode != 0) {
            this.options.setOptionKeyBinding(this.buttonId, keyCode);
         } else if (typedChar > 0) {
            this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
         }

         this.buttonId = null;
         this.time = Minecraft.getSystemTime();
         KeyBinding.resetKeyBindingArrayAndHash();
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
      boolean bl = true;

      for (KeyBinding keyBinding : this.options.keyBindings) {
         if (keyBinding.getKeyCode() != keyBinding.getKeyCodeDefault()) {
            bl = false;
            break;
         }
      }

      this.buttonReset.enabled = !bl;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
