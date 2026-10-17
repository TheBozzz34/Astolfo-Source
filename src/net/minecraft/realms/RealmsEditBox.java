package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class RealmsEditBox {
   private final GuiTextField editBox;

   public RealmsEditBox(int i, int j, int k, int l, int m) {
      this.editBox = new GuiTextField(i, Minecraft.getMinecraft().fontRendererObj, j, k, l, m);
   }

   public String getValue() {
      return this.editBox.getText();
   }

   public void tick() {
      this.editBox.updateCursorCounter();
   }

   public void setFocus(boolean bl) {
      this.editBox.setFocused(bl);
   }

   public void setValue(String string) {
      this.editBox.setText(string);
   }

   public void keyPressed(char c, int i) {
      this.editBox.textboxKeyTyped(c, i);
   }

   public boolean isFocused() {
      return this.editBox.isFocused();
   }

   public void mouseClicked(int i, int j, int k) {
      this.editBox.mouseClicked(i, j, k);
   }

   public void render() {
      this.editBox.drawTextBox();
   }

   public void setMaxLength(int i) {
      this.editBox.setMaxStringLength(i);
   }

   public void setIsEditable(boolean bl) {
      this.editBox.setEnabled(bl);
   }
}
