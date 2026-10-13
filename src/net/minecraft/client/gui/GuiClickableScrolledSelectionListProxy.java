package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.input.Mouse;

public class GuiClickableScrolledSelectionListProxy extends GuiSlot {
   private final RealmsClickableScrolledSelectionList field_178046_u;
   private static final long c = 3139962015141L;

   public GuiClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getMinecraft(), var2, var3, var4, var5, var6);
      this.field_178046_u = var1;
   }

   @Override
   public int getSize() {
      return this.field_178046_u.getItemCount();
   }

   @Override
   public void elementClicked(int var1, boolean var2, int var3, int var4) {
      this.field_178046_u.selectItem(var1, var2, var3, var4);
   }

   @Override
   public boolean isSelected(int var1) {
      return this.field_178046_u.isSelectedItem(var1);
   }

   @Override
   public void drawBackground() {
      this.field_178046_u.renderBackground();
   }

   @Override
   public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_178046_u.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int X() {
      return super.width;
   }

   public int U() {
      return super.mouseY;
   }

   public int v() {
      return super.mouseX;
   }

   @Override
   public int getContentHeight() {
      return this.field_178046_u.n();
   }

   @Override
   public int getScrollBarX() {
      return this.field_178046_u.g();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
      if (this.scrollMultiplier > 0.0F && Mouse.getEventButtonState()) {
         this.field_178046_u.customMouseEvent(this.top, this.bottom, this.headerPadding, this.amountScrolled, this.slotHeight);
      }
   }

   public void func_178043_a(int var1, int var2, int var3, Tezzelator var4) {
      this.field_178046_u.renderSelected(var1, var2, var3, var4);
   }

   @Override
   public void drawSelectionBox(int var1, int var2, int var3, int var4) {
      int var8 = this.getSize();

      for (int var9 = 0; var9 < var8; var9++) {
         int var10 = var2 + var9 * this.slotHeight + this.headerPadding;
         int var11 = this.slotHeight - 4;
         if (var10 > this.bottom || var10 + var11 < this.top) {
            this.func_178040_a(var9, var1, var10);
         }

         if (this.showSelectionBox && this.isSelected(var9)) {
            this.func_178043_a(this.width, var10, var11, Tezzelator.instance);
         }

         this.drawSlot(var9, var1, var10, var11, var3, var4);
      }
   }
}
