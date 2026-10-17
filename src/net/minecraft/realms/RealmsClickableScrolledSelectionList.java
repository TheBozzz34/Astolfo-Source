package net.minecraft.realms;

import net.minecraft.client.gui.GuiClickableScrolledSelectionListProxy;

public class RealmsClickableScrolledSelectionList {
   private final GuiClickableScrolledSelectionListProxy proxy;

   public RealmsClickableScrolledSelectionList(int i, int j, int k, int l, int m) {
      this.proxy = new GuiClickableScrolledSelectionListProxy(this, i, j, k, l, m);
   }

   public void render(int i, int j, float f) {
      this.proxy.drawScreen(i, j, f);
   }

   public int b() {
      return this.proxy.func_178044_e();
   }

   public int G() {
      return this.proxy.U();
   }

   public int s() {
      return this.proxy.v();
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
      return this.proxy.func_178044_e() / 2 + 124;
   }

   public void mouseEvent() {
      this.proxy.handleMouseInput();
   }

   public void customMouseEvent(int i, int j, int k, float f, int l) {
   }

   public void q(int i) {
      this.proxy.scrollBy(i);
   }

   public int j() {
      return this.proxy.getAmountScrolled();
   }

   public void renderList(int i, int j, int k, int l) {
   }

   public void itemClicked(int i, int j, int k, int l, int m) {
   }

   public void renderSelected(int i, int j, int k, Tezzelator tezzelator) {
   }

   public void T(int i) {
      this.proxy.setSlotXBoundsFromLeft(i);
   }
}
