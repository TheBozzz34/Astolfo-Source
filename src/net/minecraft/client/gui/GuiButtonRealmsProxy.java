package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton {
   private RealmsButton realmsButton;
   private static final long c = 7416776915197L;

   public GuiButtonRealmsProxy(RealmsButton var1, int var2, int var3, int var4, String var5) {
      super(var2, var3, var4, var5);
      this.realmsButton = var1;
   }

   public GuiButtonRealmsProxy(RealmsButton var1, int var2, int var3, int var4, String var5, int var6, int var7) {
      super(var2, var3, var4, var6, var7, var5);
      this.realmsButton = var1;
   }

   public int getId() {
      return super.id;
   }

   public boolean getEnabled() {
      return super.enabled;
   }

   public void setEnabled(boolean var1) {
      super.enabled = var1;
   }

   public void setText(String var1) {
      super.displayString = var1;
   }

   @Override
   public int getButtonWidth() {
      return super.getButtonWidth();
   }

   public int V() {
      return super.yPosition;
   }

   @Override
   public boolean mousePressed(Minecraft var1, int var2, int var3) {
      if (super.mousePressed(var1, var2, var3)) {
         this.realmsButton.clicked(var2, var3);
      }

      return super.mousePressed(var1, var2, var3);
   }

   @Override
   public void mouseReleased(int var1, int var2) {
      this.realmsButton.released(var1, var2);
   }

   @Override
   public void mouseDragged(Minecraft var1, int var2, int var3) {
      this.realmsButton.renderBg(var2, var3);
   }

   public RealmsButton getRealmsButton() {
      return this.realmsButton;
   }

   @Override
   public int getHoverState(boolean var1) {
      return this.realmsButton.getYImage(var1);
   }

   public int func_154312_c(boolean var1) {
      return super.getHoverState(var1);
   }

   public int g() {
      return this.height;
   }
}
