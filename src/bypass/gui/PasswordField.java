package bypass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class PasswordField extends GuiTextField {
   private boolean P = false;
   private boolean V = false;
   private static final long c = 135172913195417L;

   public PasswordField(int var1, int var2, int var3, int var4, int var5) {
      super(var1, Minecraft.getMinecraft().fontRendererObj, var2, var3, var4, var5);
   }

   public void Z(boolean var1) {
      this.P = var1;
   }

   public void L(boolean var1) {
      if (!this.P) {
         this.V = true;
      }

      this.V = var1;
   }

   public boolean R() {
      return !this.P ? true : this.V;
   }

   public boolean g() {
      return this.P;
   }

   @Override
   public void drawTextBox() {
      boolean var5 = this.P && !this.V;
      String var6 = "";
      if (var5) {
         var6 = this.getText();
         String var7 = "";

         for (int var8 = 0; var8 < this.getText().length(); var8++) {
            var7 = var7 + "*";
         }

         this.setText(var7);
      }

      super.drawTextBox();
      if (var5) {
         this.setText(var6);
      }
   }
}
