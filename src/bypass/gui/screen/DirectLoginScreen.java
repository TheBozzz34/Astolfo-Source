package bypass.gui.screen;

import bypass.account.Alt;
import bypass.account.AltLogin;
import bypass.gui.Checkbox;
import bypass.gui.PasswordField;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

public class DirectLoginScreen extends GuiScreen {
   private GuiScreen parentScreen;
   private GuiTextField usernameField;
   private PasswordField passwordField;
   private Checkbox hidePasswordCheckbox;

   public DirectLoginScreen(GuiScreen guiScreen) {
      this.parentScreen = guiScreen;
   }

   @Override
   public void updateScreen() {
      this.usernameField.updateCursorCounter();
      this.passwordField.updateCursorCounter();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 125, this.height / 6 + 150, 120, 20, "Login"));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height / 6 + 150, 120, 20, "Cancel"));
      this.usernameField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 6 + 35, 200, 20);
      this.usernameField.setFocused(true);
      this.usernameField.setMaxStringLength(128);
      this.passwordField = new PasswordField(4, this.width / 2 - 100, this.height / 6 + 80, 200, 20);
      this.passwordField.setMaxStringLength(128);
      String string = "Hide password";
      this.hidePasswordCheckbox = new Checkbox(string, this.width / 2 - (15 + this.fontRendererObj.getStringWidth(string)) / 2, this.height / 6 + 115, true);
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         this.login();
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (typedChar == '\t') {
         this.usernameField.setFocused(this.passwordField.isFocused());
         this.passwordField.setFocused(!this.usernameField.isFocused());
      } else if (typedChar == '\r') {
         this.login();
         this.mc.displayGuiScreen(this.parentScreen);
      }

      this.usernameField.textboxKeyTyped(typedChar, keyCode);
      this.passwordField.textboxKeyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
      this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
      this.hidePasswordCheckbox.handleMouseClick(mouseX, mouseY, mouseButton);
   }

   private void login() {
      if (this.passwordField.getText().contains("key:")) {
         String[] strings = this.passwordField.getText().replaceAll("key:", "").split(":");
         this.mc.session = new Session(this.usernameField.getText(), strings[1], strings[0], "mojang");
      } else if (this.usernameField.getText().contains(":")) {
         String[] strings2 = this.usernameField.getText().split(":");
         if (strings2.length == 2) {
            AltLogin.AUTH.loginAlt(new Alt(strings2[0], strings2[1]));
         }
      } else {
         AltLogin.AUTH.loginAlt(new Alt(this.usernameField.getText(), this.passwordField.getText()));
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, "Direct Login", this.width / 2, 30, 65531);
      String string = "Username or email:";
      this.passwordField.setVisible(true);
      if (this.usernameField.getText().contains("@")) {
         string = "Email:";
         if (this.usernameField.getText().contains(":")) {
            string = "Email and password:";
            this.passwordField.setVisible(false);
         }
      } else if (this.usernameField.getText().contains(":")) {
         string = "Username and password:";
         this.passwordField.setVisible(false);
      }

      if (this.passwordField.getVisible()) {
         this.I(
            this.fontRendererObj,
            this.passwordField.getText().contains("key:") ? "Session key:" : "Password or session key:",
            this.width / 2 - 100,
            this.height / 6 + 68,
            16777215
         );
      }

      this.I(this.fontRendererObj, string, this.width / 2 - 100, this.height / 6 + 23, 16777215);
      this.passwordField.setPasswordMode(this.hidePasswordCheckbox.isChecked());
      this.usernameField.drawTextBox();
      this.passwordField.drawTextBox();
      this.hidePasswordCheckbox.drawCheckbox(mouseX, mouseY);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
