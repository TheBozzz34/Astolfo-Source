package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
   private static final long e = 20320261327238L;

   public GuiListExtended(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void elementClicked(int var1, boolean var2, int var3, int var4) {
   }

   @Override
   public boolean isSelected(int var1) {
      return false;
   }

   @Override
   public void drawBackground() {
   }

   @Override
   public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.getListEntry(var1).drawEntry(var1, var2, var3, this.getListWidth(), var4, var5, var6, this.getSlotIndexFromScreenCoords(var5, var6) == var1);
   }

   @Override
   public void func_178040_a(int var1, int var2, int var3) {
      this.getListEntry(var1).setSelected(var1, var2, var3);
   }

   public boolean mouseClicked(int var1, int var2, int var3) {
      if (this.isMouseYWithinSlotBounds(var2)) {
         int var7 = this.getSlotIndexFromScreenCoords(var1, var2);
         if (var7 >= 0) {
            int var8 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int var9 = this.top + 4 - this.Y() + var7 * this.slotHeight + this.headerPadding;
            int var10 = var1 - var8;
            int var11 = var2 - var9;
            if (this.getListEntry(var7).mousePressed(var7, var1, var2, var3, var10, var11)) {
               this.a(false);
               return true;
            }
         }
      }

      return false;
   }

   public boolean mouseReleased(int var1, int var2, int var3) {
      for (int var7 = 0; var7 < this.getSize(); var7++) {
         int var8 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int var9 = this.top + 4 - this.Y() + var7 * this.slotHeight + this.headerPadding;
         int var10 = var1 - var8;
         int var11 = var2 - var9;
         this.getListEntry(var7).mouseReleased(var7, var1, var2, var3, var10, var11);
      }

      this.a(true);
      return false;
   }

   public abstract GuiListExtended.IGuiListEntry getListEntry(int var1);

   public interface IGuiListEntry {
      void setSelected(int var1, int var2, int var3);

      void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8);

      boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6);

      void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6);
   }
}
