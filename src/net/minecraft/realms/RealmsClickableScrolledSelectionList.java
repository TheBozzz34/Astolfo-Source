package net.minecraft.realms;

import net.minecraft.client.gui.GuiClickableScrolledSelectionListProxy;

public class RealmsClickableScrolledSelectionList {
   private final GuiClickableScrolledSelectionListProxy proxy;
   private static final long a = 109058521897449L;
   private static final long b = -1687200670131683204L;

   public RealmsClickableScrolledSelectionList(int i, int j, int k, int l, int m) {
      this.proxy = new GuiClickableScrolledSelectionListProxy(this, i, j, k, l, m);
   }

   public void render(int i, int j, float f) {
      this.proxy.drawScreen(i, j, f);
   }

   public int b() {
      return this.proxy.X();
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

   public int n() {
      return 0;
   }

   public int g() {
      return this.proxy.X() / 2 + (int)b;
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
      return this.proxy.Y();
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
