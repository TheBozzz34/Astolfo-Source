package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton {
   private RealmsButton realmsButton;
   private static final long c = 7416776915197L;

   public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text) {
      super(buttonId, x, y, text);
      this.realmsButton = realmsButtonIn;
   }

   public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text, int widthIn, int heightIn) {
      super(buttonId, x, y, widthIn, heightIn, text);
      this.realmsButton = realmsButtonIn;
   }

   public int getId() {
      return super.id;
   }

   public boolean getEnabled() {
      return super.enabled;
   }

   public void setEnabled(boolean isEnabled) {
      super.enabled = isEnabled;
   }

   public void setText(String text) {
      super.displayString = text;
   }

   @Override
   public int getButtonWidth() {
      return super.getButtonWidth();
   }

   public int V() {
      return super.yPosition;
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.realmsButton.clicked(mouseX, mouseY);
      }

      return super.mousePressed(mc, mouseX, mouseY);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.realmsButton.released(mouseX, mouseY);
   }

   @Override
   public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      this.realmsButton.renderBg(mouseX, mouseY);
   }

   public RealmsButton getRealmsButton() {
      return this.realmsButton;
   }

   @Override
   public int getHoverState(boolean mouseOver) {
      return this.realmsButton.getYImage(mouseOver);
   }

   public int func_154312_c(boolean bl) {
      return super.getHoverState(bl);
   }

   public int g() {
      return this.height;
   }
}
