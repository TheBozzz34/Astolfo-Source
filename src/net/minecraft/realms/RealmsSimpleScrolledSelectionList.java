package net.minecraft.realms;

import net.minecraft.client.gui.GuiSimpleScrolledSelectionListProxy;

public class RealmsSimpleScrolledSelectionList {
   private final GuiSimpleScrolledSelectionListProxy proxy;

   public RealmsSimpleScrolledSelectionList(int i, int j, int k, int l, int m) {
      this.proxy = new GuiSimpleScrolledSelectionListProxy(this, i, j, k, l, m);
   }

   public void render(int i, int j, float f) {
      this.proxy.drawScreen(i, j, f);
   }

   public int width() {
      return this.proxy.getWidth();
   }

   public int ym() {
      return this.proxy.getMouseY();
   }

   public int xm() {
      return this.proxy.getMouseX();
   }

   public void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
   }

   public void renderItem(int i, int j, int k, int l, int m, int n) {
      this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
   }

   public int getItemCount() {
      return 0;
   }

   public void selectItem(int i, boolean bl, int j, int k) {
   }

   public boolean isSelectedItem(int i) {
      return false;
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getWidth() / 2 + 124;
   }

   public void mouseEvent() {
      this.proxy.handleMouseInput();
   }

   public void scroll(int i) {
      this.proxy.scrollBy(i);
   }

   public int getScroll() {
      return this.proxy.getAmountScrolled();
   }

   public void renderList(int i, int j, int k, int l) {
   }
}
