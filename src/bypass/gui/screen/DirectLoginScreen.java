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
   private GuiScreen N;
   private GuiTextField u;
   private PasswordField c;
   private Checkbox n;

   public DirectLoginScreen(GuiScreen guiScreen) {
      this.N = guiScreen;
   }

   @Override
   public void updateScreen() {
      this.u.updateCursorCounter();
      this.c.updateCursorCounter();
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
      this.u = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 6 + 35, 200, 20);
      this.u.setFocused(true);
      this.u.setMaxStringLength(128);
      this.c = new PasswordField(4, this.width / 2 - 100, this.height / 6 + 80, 200, 20);
      this.c.setMaxStringLength(128);
      String string = "Hide password";
      this.n = new Checkbox(string, this.width / 2 - (15 + this.fontRendererObj.getStringWidth(string)) / 2, this.height / 6 + 115, true);
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         this.a();
         this.mc.displayGuiScreen(this.N);
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.N);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (typedChar == '\t') {
         this.u.setFocused(this.c.isFocused());
         this.c.setFocused(!this.u.isFocused());
      } else if (typedChar == '\r') {
         this.a();
         this.mc.displayGuiScreen(this.N);
      }

      this.u.textboxKeyTyped(typedChar, keyCode);
      this.c.textboxKeyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.u.mouseClicked(mouseX, mouseY, mouseButton);
      this.c.mouseClicked(mouseX, mouseY, mouseButton);
      this.n.b(mouseX, mouseY, mouseButton);
   }

   private void a() {
      if (this.c.getText().contains("key:")) {
         String[] strings = this.c.getText().replaceAll("key:", "").split(":");
         this.mc.session = new Session(this.u.getText(), strings[1], strings[0], "mojang");
      } else if (this.u.getText().contains(":")) {
         String[] strings2 = this.u.getText().split(":");
         if (strings2.length == 2) {
            AltLogin.AUTH.A(new Alt(strings2[0], strings2[1]));
         }
      } else {
         AltLogin.AUTH.A(new Alt(this.u.getText(), this.c.getText()));
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, "Direct Login", this.width / 2, 30, 65531);
      String string = "Username or email:";
      this.c.setVisible(true);
      if (this.u.getText().contains("@")) {
         string = "Email:";
         if (this.u.getText().contains(":")) {
            string = "Email and password:";
            this.c.setVisible(false);
         }
      } else if (this.u.getText().contains(":")) {
         string = "Username and password:";
         this.c.setVisible(false);
      }

      if (this.c.getVisible()) {
         this.I(
            this.fontRendererObj,
            this.c.getText().contains("key:") ? "Session key:" : "Password or session key:",
            this.width / 2 - 100,
            this.height / 6 + 68,
            16777215
         );
      }

      this.I(this.fontRendererObj, string, this.width / 2 - 100, this.height / 6 + 23, 16777215);
      this.c.Z(this.n.a());
      this.u.drawTextBox();
      this.c.drawTextBox();
      this.n.E(mouseX, mouseY);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
