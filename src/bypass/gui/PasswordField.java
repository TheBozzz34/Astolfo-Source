package bypass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class PasswordField extends GuiTextField {
   private boolean P = false;
   private boolean V = false;
   private static final long c = 135172913195417L;

   public PasswordField(int i, int j, int k, int l, int m) {
      super(i, Minecraft.getMinecraft().fontRendererObj, j, k, l, m);
   }

   public void Z(boolean bl) {
      this.P = bl;
   }

   public void L(boolean bl) {
      if (!this.P) {
         this.V = true;
      }

      this.V = bl;
   }

   public boolean R() {
      return !this.P ? true : this.V;
   }

   public boolean g() {
      return this.P;
   }

   @Override
   public void drawTextBox() {
      boolean bl = this.P && !this.V;
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
