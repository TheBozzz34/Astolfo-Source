package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
   public GuiListExtended(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return false;
   }

   @Override
   public void drawBackground() {
   }

   @Override
   public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
      this.getListEntry(entryID)
         .drawEntry(entryID, i, j, this.getListWidth(), k, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
   }

   @Override
   public void func_178040_a(int i, int j, int k) {
      this.getListEntry(i).setSelected(i, j, k);
   }

   public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
      if (this.isMouseYWithinSlotBounds(mouseY)) {
         int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
         if (i >= 0) {
            int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int l = mouseX - j;
            int m = mouseY - k;
            if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, m)) {
               this.setEnabled(false);
               return true;
            }
         }
      }

      return false;
   }

   public boolean mouseReleased(int i, int j, int k) {
      for (int l = 0; l < this.getSize(); l++) {
         int m = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int n = this.top + 4 - this.getAmountScrolled() + l * this.slotHeight + this.headerPadding;
         int i2 = i - m;
         int i3 = j - n;
         this.getListEntry(l).mouseReleased(l, i, j, k, i2, i3);
      }

      this.setEnabled(true);
      return false;
   }

   public abstract GuiListExtended.IGuiListEntry getListEntry(int index);

   public interface IGuiListEntry {
      void setSelected(int i, int j, int k);

      void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeightIn, int mouseXIn, int mouseYIn, boolean isSelected);

      boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m);

      void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
   }
}
