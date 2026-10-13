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
   private static final long a = 46869750114722L;

   public DirectLoginScreen(GuiScreen var1) {
      this.N = var1;
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
      String var3 = "Hide password";
      this.n = new Checkbox(var3, this.width / 2 - (15 + this.fontRendererObj.getStringWidth(var3)) / 2, this.height / 6 + 115, true);
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      if (var1.id == 0) {
         this.a();
         this.mc.displayGuiScreen(this.N);
      } else if (var1.id == 1) {
         this.mc.displayGuiScreen(this.N);
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var1 == '\t') {
         this.u.setFocused(this.c.isFocused());
         this.c.setFocused(!this.u.isFocused());
      } else if (var1 == '\r') {
         this.a();
         this.mc.displayGuiScreen(this.N);
      }

      this.u.textboxKeyTyped(var1, var2);
      this.c.textboxKeyTyped(var1, var2);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.u.mouseClicked(var1, var2, var3);
      this.c.mouseClicked(var1, var2, var3);
      this.n.b(var1, var2, var3);
   }

   private void a() {
      if (this.c.getText().contains("key:")) {
         String[] var4 = this.c.getText().replaceAll("key:", "").split(":");
         this.mc.session = new Session(this.u.getText(), var4[1], var4[0], "mojang");
      } else if (this.u.getText().contains(":")) {
         String[] var5 = this.u.getText().split(":");
         if (var5.length == 2) {
            AltLogin.AUTH.A(new Alt(var5[0], var5[1]));
         }
      } else {
         AltLogin.AUTH.A(new Alt(this.u.getText(), this.c.getText()));
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, "Direct Login", this.width / 2, 30, 65531);
      String var7 = "Username or email:";
      this.c.setVisible(true);
      if (this.u.getText().contains("@")) {
         var7 = "Email:";
         if (this.u.getText().contains(":")) {
            var7 = "Email and password:";
            this.c.setVisible(false);
         }
      } else if (this.u.getText().contains(":")) {
         var7 = "Username and password:";
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

      this.I(this.fontRendererObj, var7, this.width / 2 - 100, this.height / 6 + 23, 16777215);
      this.c.Z(this.n.a());
      this.u.drawTextBox();
      this.c.drawTextBox();
      this.n.E(var1, var2);
      super.drawScreen(var1, var2, var3);
   }
}
