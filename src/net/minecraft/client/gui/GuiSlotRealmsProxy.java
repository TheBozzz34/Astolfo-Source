package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class GuiSlotRealmsProxy extends GuiSlot {
   private final RealmsScrolledSelectionList selectionList;

   public GuiSlotRealmsProxy(RealmsScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getMinecraft(), var2, var3, var4, var5, var6);
      this.selectionList = var1;
   }

   @Override
   public int getSize() {
      return this.selectionList.h();
   }

   @Override
   public void elementClicked(int var1, boolean var2, int var3, int var4) {
      this.selectionList.selectItem(var1, var2, var3, var4);
   }

   @Override
   public boolean isSelected(int var1) {
      return this.selectionList.isSelectedItem(var1);
   }

   @Override
   public void drawBackground() {
      this.selectionList.i();
   }

   @Override
   public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.selectionList.renderItem(var1, var2, var3, var4, var5, var6);
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
