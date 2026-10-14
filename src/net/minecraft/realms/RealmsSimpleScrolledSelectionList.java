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

   public int R() {
      return this.proxy.i();
   }

   public int u() {
      return this.proxy.h();
   }

   public int i() {
      return this.proxy.Q();
   }

   public void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
   }

   public void renderItem(int i, int j, int k, int l, int m, int n) {
      this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
   }

   public int Q() {
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
      return this.proxy.i() / 2 + 124;
   }

   public void L() {
      this.proxy.handleMouseInput();
   }

   public void scroll(int i) {
      this.proxy.scrollBy(i);
   }

   public int e() {
      return this.proxy.Y();
   }

   public void renderList(int i, int j, int k, int l) {
   }
}
