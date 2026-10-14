package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class GuiSlotRealmsProxy extends GuiSlot {
   private final RealmsScrolledSelectionList selectionList;

   public GuiSlotRealmsProxy(RealmsScrolledSelectionList selectionListIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.selectionList = selectionListIn;
   }

   @Override
   public int getSize() {
      return this.selectionList.h();
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      this.selectionList.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return this.selectionList.isSelectedItem(slotIndex);
   }

   @Override
   public void drawBackground() {
      this.selectionList.i();
   }

   @Override
   public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
      this.selectionList.renderItem(entryID, i, j, k, mouseXIn, mouseYIn);
   }

   public int J() {
      return super.width;
   }

   public int a() {
      return super.mouseY;
   }

   public int N() {
      return super.mouseX;
   }

   @Override
   public int getContentHeight() {
      return this.selectionList.n();
   }

   @Override
   public int getScrollBarX() {
      return this.selectionList.r();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
   }
}
