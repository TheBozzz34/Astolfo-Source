package net.minecraft.realms;

import net.minecraft.client.gui.GuiSlotRealmsProxy;

public class RealmsScrolledSelectionList {
   private final GuiSlotRealmsProxy proxy;
   private static final long a = 109397933927662L;
   private static final long b = -4242488550899056516L;

   public RealmsScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      this.proxy = new GuiSlotRealmsProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.drawScreen(var1, var2, var3);
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

   public void renderItem(int var1, int var2, int var3, int var4, Tezzelator var5, int var6, int var7) {
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, Tezzelator.instance, var5, var6);
   }

   public int h() {
      return 0;
   }

   public void selectItem(int var1, boolean var2, int var3, int var4) {
   }

   public boolean isSelectedItem(int var1) {
      return false;
   }

   public void i() {
   }

   public int n() {
      return 0;
   }

   public int r() {
      return this.proxy.J() / 2 + (int)b;
   }

   public void h_void() {
      this.proxy.handleMouseInput();
   }

   public void scroll(int var1) {
      this.proxy.scrollBy(var1);
   }

   public int F() {
      return this.proxy.Y();
   }

   public void renderList(int var1, int var2, int var3, int var4) {
   }
}
