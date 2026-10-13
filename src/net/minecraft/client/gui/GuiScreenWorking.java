package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   private String field_146591_a = "";
   private String field_146589_f = "";
   private int progress;
   private boolean doneWorking;
   private static final long a = 26978159644744L;

   @Override
   public void displaySavingString(String var1) {
      this.resetProgressAndMessage(var1);
   }

   @Override
   public void resetProgressAndMessage(String var1) {
      this.field_146591_a = var1;
      this.displayLoadingString("Working...");
   }

   @Override
   public void displayLoadingString(String var1) {
      this.field_146589_f = var1;
      this.setLoadingProgress(0);
   }

   @Override
   public void setLoadingProgress(int var1) {
      this.progress = var1;
   }

   @Override
   public void setDoneWorking() {
      this.doneWorking = true;
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      if (this.doneWorking) {
         if (!this.mc.isConnectedToRealms()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }
      } else {
         this.drawDefaultBackground();
         this.d(this.fontRendererObj, this.field_146591_a, this.width / 2, 70, 16777215);
         this.d(this.fontRendererObj, this.field_146589_f + " " + this.progress + "%", this.width / 2, 90, 16777215);
         super.drawScreen(var1, var2, var3);
      }
   }
}
