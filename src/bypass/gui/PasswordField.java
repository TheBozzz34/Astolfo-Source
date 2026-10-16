package bypass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class PasswordField extends GuiTextField {
   private boolean passwordMode = false;
   private boolean revealed = false;

   public PasswordField(int i, int j, int k, int l, int m) {
      super(i, Minecraft.getMinecraft().fontRendererObj, j, k, l, m);
   }

   public void setPasswordMode(boolean bl) {
      this.passwordMode = bl;
   }

   public void setRevealed(boolean bl) {
      if (!this.passwordMode) {
         this.revealed = true;
      }

      this.revealed = bl;
   }

   public boolean isTextShown() {
      return !this.passwordMode ? true : this.revealed;
   }

   public boolean isPasswordMode() {
      return this.passwordMode;
   }

   @Override
   public void drawTextBox() {
      boolean bl = this.passwordMode && !this.revealed;
      String string = "";
      if (bl) {
         string = this.getText();
         String string2 = "";

         for (int i = 0; i < this.getText().length(); i++) {
            string2 = string2 + "*";
         }

         this.setText(string2);
      }

      super.drawTextBox();
      if (bl) {
         this.setText(string);
      }
   }
}
