package net.minecraft.realms;

import net.minecraft.client.gui.GuiClickableScrolledSelectionListProxy;

public class RealmsClickableScrolledSelectionList {
   private final GuiClickableScrolledSelectionListProxy proxy;
   private static final long a = 109058521897449L;
   private static final long b = -1687200670131683204L;

   public RealmsClickableScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      this.proxy = new GuiClickableScrolledSelectionListProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.drawScreen(var1, var2, var3);
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

   public void renderItem(int var1, int var2, int var3, int var4, Tezzelator var5, int var6, int var7) {
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, Tezzelator.instance, var5, var6);
   }

   public int getItemCount() {
      return 0;
   }

   public void selectItem(int var1, boolean var2, int var3, int var4) {
   }

   public boolean isSelectedItem(int var1) {
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

   public void customMouseEvent(int var1, int var2, int var3, float var4, int var5) {
   }

   public void q(int var1) {
      this.proxy.scrollBy(var1);
   }

   public int j() {
      return this.proxy.Y();
   }

   public void renderList(int var1, int var2, int var3, int var4) {
   }

   public void itemClicked(int var1, int var2, int var3, int var4, int var5) {
   }

   public void renderSelected(int var1, int var2, int var3, Tezzelator var4) {
   }

   public void T(int var1) {
      this.proxy.setSlotXBoundsFromLeft(var1);
   }
}
