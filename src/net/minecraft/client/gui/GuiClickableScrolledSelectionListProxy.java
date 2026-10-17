package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.input.Mouse;

public class GuiClickableScrolledSelectionListProxy extends GuiSlot {
   private final RealmsClickableScrolledSelectionList field_178046_u;

   public GuiClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList selectionList, int i, int j, int k, int l, int m) {
      super(Minecraft.getMinecraft(), i, j, k, l, m);
      this.field_178046_u = selectionList;
   }

   @Override
   public int getSize() {
      return this.field_178046_u.getItemCount();
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      this.field_178046_u.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return this.field_178046_u.isSelectedItem(slotIndex);
   }

   @Override
   public void drawBackground() {
      this.field_178046_u.renderBackground();
   }

   @Override
   public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
      this.field_178046_u.renderItem(entryID, i, j, k, mouseXIn, mouseYIn);
   }

   public int func_178044_e() {
      return super.width;
   }

   public int getMouseY() {
      return super.mouseY;
   }

   public int getMouseX() {
      return super.mouseX;
   }

   @Override
   public int getContentHeight() {
      return this.field_178046_u.getMaxPosition();
   }

   @Override
   public int getScrollBarX() {
      return this.field_178046_u.getScrollbarPosition();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
      if (this.scrollMultiplier > 0.0F && Mouse.getEventButtonState()) {
         this.field_178046_u.customMouseEvent(this.top, this.bottom, this.headerPadding, this.amountScrolled, this.slotHeight);
      }
   }

   public void func_178043_a(int i, int j, int k, Tezzelator tezzelator) {
      this.field_178046_u.renderSelected(i, j, k, tezzelator);
   }

   @Override
   public void drawSelectionBox(int i, int j, int mouseXIn, int mouseYIn) {
      int k = this.getSize();

      for (int l = 0; l < k; l++) {
         int m = j + l * this.slotHeight + this.headerPadding;
         int n = this.slotHeight - 4;
         if (m > this.bottom || m + n < this.top) {
            this.func_178040_a(l, i, m);
         }

         if (this.showSelectionBox && this.isSelected(l)) {
            this.func_178043_a(this.width, m, n, Tezzelator.instance);
         }

         this.drawSlot(l, i, m, n, mouseXIn, mouseYIn);
      }
   }
}
