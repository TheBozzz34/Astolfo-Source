package net.minecraft.realms;

import net.minecraft.client.gui.GuiSlotRealmsProxy;

public class RealmsScrolledSelectionList {
   private final GuiSlotRealmsProxy proxy;

   public RealmsScrolledSelectionList(int i, int j, int k, int l, int m) {
      this.proxy = new GuiSlotRealmsProxy(this, i, j, k, l, m);
   }

   public void render(int i, int j, float f) {
      this.proxy.drawScreen(i, j, f);
   }

   public int z() {
      return this.proxy.J();
   }

   public int M() {
      return this.proxy.a();
   }

   public int Q() {
      return this.proxy.N();
   }

   public void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
   }

   public void renderItem(int i, int j, int k, int l, int m, int n) {
      this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
   }

   public int h() {
      return 0;
   }

   public void selectItem(int i, boolean bl, int j, int k) {
   }

   public boolean isSelectedItem(int i) {
      return false;
   }

   public void i() {
   }

   public int n() {
      return 0;
   }

   public int r() {
      return this.proxy.J() / 2 + 124;
   }

   public void h_void() {
      this.proxy.handleMouseInput();
   }

   public void scroll(int i) {
      this.proxy.scrollBy(i);
   }

   public int F() {
      return this.proxy.Y();
   }

   public void renderList(int i, int j, int k, int l) {
   }
}
