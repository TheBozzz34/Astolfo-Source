package net.minecraft.client.gui;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class GuiSlot {
   public final Minecraft mc;
   public int width;
   public int height;
   public int top;
   public int bottom;
   public int right;
   public int left;
   public final int slotHeight;
   private int scrollUpButtonID;
   private int scrollDownButtonID;
   public int mouseX;
   public int mouseY;
   public boolean field_148163_i = true;
   public int initialClickY = -2;
   public float scrollMultiplier;
   public float amountScrolled;
   public int selectedElement = -1;
   public long lastClicked;
   public boolean field_178041_q = true;
   public boolean showSelectionBox = true;
   public boolean hasListHeader;
   public int headerPadding;
   private boolean enabled = true;
   private static final int v = new Color(7, 7, 7).getRGB();
   private static final int l = new Color(17, 17, 17).getRGB();
   private static final long t = 96971305979210L;

   public GuiSlot(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      this.mc = var1;
      this.width = var2;
      this.height = var3;
      this.top = var4;
      this.bottom = var5;
      this.slotHeight = var6;
      this.left = 0;
      this.right = var2;
   }

   public void m(int var1, int var2, int var3, int var4) {
      this.width = var1;
      this.height = var2;
      this.top = var3;
      this.bottom = var4;
      this.left = 0;
      this.right = var1;
   }

   public void C(boolean var1) {
      this.showSelectionBox = var1;
   }

   public void setHasListHeader(boolean var1, int var2) {
      this.hasListHeader = var1;
      this.headerPadding = var2;
      if (!var1) {
         this.headerPadding = 0;
      }
   }

   public abstract int getSize();

   public abstract void elementClicked(int var1, boolean var2, int var3, int var4);

   public abstract boolean isSelected(int var1);

   public int getContentHeight() {
      return this.getSize() * this.slotHeight + this.headerPadding;
   }

   public abstract void drawBackground();

   public void func_178040_a(int var1, int var2, int var3) {
   }

   public abstract void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6);

   public void drawListHeader(int var1, int var2, Tessellator var3) {
   }

   public void func_148132_a(int var1, int var2) {
   }

   public void func_148142_b(int var1, int var2) {
   }

   public int getSlotIndexFromScreenCoords(int var1, int var2) {
      int var6 = this.left + this.width / 2 - this.getListWidth() / 2;
      int var7 = this.left + this.width / 2 + this.getListWidth() / 2;
      int var8 = var2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
      int var9 = var8 / this.slotHeight;
      return var1 < this.getScrollBarX() && var1 >= var6 && var1 <= var7 && var9 >= 0 && var8 >= 0 && var9 < this.getSize() ? var9 : -1;
   }

   public void U(int var1, int var2) {
      this.scrollUpButtonID = var1;
      this.scrollDownButtonID = var2;
   }

   public void bindAmountScrolled() {
      this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, this.func_148135_f());
   }

   public int func_148135_f() {
      return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
   }

   public int Y() {
      return (int)this.amountScrolled;
   }

   public boolean isMouseYWithinSlotBounds(int var1) {
      return var1 >= this.top && var1 <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
   }

   public void scrollBy(int var1) {
      this.amountScrolled += var1;
      this.bindAmountScrolled();
      this.initialClickY = -2;
   }

   public void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == this.scrollUpButtonID) {
            this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         } else if (var1.id == this.scrollDownButtonID) {
            this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         }
      }
   }

   public void drawScreen(int var1, int var2, float var3) {
      long var4 = t ^ 133834903392232L;
      if (this.field_178041_q) {
         this.mouseX = var1;
         this.mouseY = var2;
         this.drawBackground();
         int var7 = this.getScrollBarX();
         int var8 = var7 + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator var9 = Tessellator.getInstance();
         WorldRenderer var10 = var9.getWorldRenderer();
         int var11 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int var12 = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(var11, var12, var9);
         }

         this.drawSelectionBox(var11, var12, var1, var2);
         GlStateManager.disableDepth();
         byte var13 = 4;
         this.W(0, this.top, 255, 255);
         this.W(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         long var10000 = 516448980915009537L ^ var4;
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         var10000 = 1330979450984952668L ^ var4;
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         var10.pos(this.left, this.top + var13, 0.0).tex(0.0, 1.0).color(0, 0, 0, 0).endVertex();
         var10.pos(this.right, this.top + var13, 0.0).tex(1.0, 1.0).color(0, 0, 0, 0).endVertex();
         var10.pos(this.right, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
         var10.pos(this.left, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
         var9.draw();
         var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         var10.pos(this.left, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
         var10.pos(this.right, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
         var10.pos(this.right, this.bottom - var13, 0.0).tex(1.0, 0.0).color(0, 0, 0, 0).endVertex();
         var10.pos(this.left, this.bottom - var13, 0.0).tex(0.0, 0.0).color(0, 0, 0, 0).endVertex();
         var9.draw();
         int var14 = this.func_148135_f();
         if (var14 > 0) {
            int var15 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            var15 = MathHelper.clamp_int(var15, 32, this.bottom - this.top - 8);
            int var16 = (int)this.amountScrolled * (this.bottom - this.top - var15) / var14 + this.top;
            if (var16 < this.top) {
               var16 = this.top;
            }

            var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var10.pos(var7, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            var10.pos(var8, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var10.pos(var8, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            var10.pos(var7, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            var9.draw();
            var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var10.pos(var7, var16 + var15, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var8, var16 + var15, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var8, var16, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var7, var16, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            var9.draw();
            var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var10.pos(var7, var16 + var15 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            var10.pos(var8 - 1, var16 + var15 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            var10.pos(var8 - 1, var16, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            var10.pos(var7, var16, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            var9.draw();
         }

         this.func_148142_b(var1, var2);
         GlStateManager.enableTexture2D();
         var10000 = 913416673253702768L ^ var4;
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }

   public void handleMouseInput() {
      if (this.isMouseYWithinSlotBounds(this.mouseY)) {
         if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
            int var4 = (this.width - this.getListWidth()) / 2;
            int var5 = (this.width + this.getListWidth()) / 2;
            int var6 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int var7 = var6 / this.slotHeight;
            if (var7 < this.getSize() && this.mouseX >= var4 && this.mouseX <= var5 && var7 >= 0 && var6 >= 0) {
               this.elementClicked(var7, false, this.mouseX, this.mouseY);
               this.selectedElement = var7;
            } else if (this.mouseX >= var4 && this.mouseX <= var5 && var6 < 0) {
               this.func_148132_a(this.mouseX - var4, this.mouseY - this.top + (int)this.amountScrolled - 4);
            }
         }

         if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
            this.initialClickY = -1;
         } else if (this.initialClickY == -1) {
            boolean var13 = true;
            if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
               int var15 = (this.width - this.getListWidth()) / 2;
               int var16 = (this.width + this.getListWidth()) / 2;
               int var17 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
               int var8 = var17 / this.slotHeight;
               if (var8 < this.getSize() && this.mouseX >= var15 && this.mouseX <= var16 && var8 >= 0 && var17 >= 0) {
                  boolean var9 = var8 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                  this.elementClicked(var8, var9, this.mouseX, this.mouseY);
                  this.selectedElement = var8;
                  this.lastClicked = Minecraft.getSystemTime();
               } else if (this.mouseX >= var15 && this.mouseX <= var16 && var17 < 0) {
                  this.func_148132_a(this.mouseX - var15, this.mouseY - this.top + (int)this.amountScrolled - 4);
                  var13 = false;
               }

               int var18 = this.getScrollBarX();
               int var10 = var18 + 6;
               if (this.mouseX >= var18 && this.mouseX <= var10) {
                  this.scrollMultiplier = -1.0F;
                  int var11 = this.func_148135_f();
                  if (var11 < 1) {
                     var11 = 1;
                  }

                  int var12 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getContentHeight());
                  var12 = MathHelper.clamp_int(var12, 32, this.bottom - this.top - 8);
                  this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - var12) / var11);
               } else {
                  this.scrollMultiplier = 1.0F;
               }

               if (var13) {
                  this.initialClickY = this.mouseY;
               } else {
                  this.initialClickY = -2;
               }
            } else {
               this.initialClickY = -2;
            }
         } else if (this.initialClickY >= 0) {
            this.amountScrolled = this.amountScrolled - (this.mouseY - this.initialClickY) * this.scrollMultiplier;
            this.initialClickY = this.mouseY;
         }

         int var14 = Mouse.getEventDWheel();
         if (var14 != 0) {
            if (var14 > 0) {
               var14 = -1;
            } else if (var14 < 0) {
               var14 = 1;
            }

            this.amountScrolled = this.amountScrolled + var14 * this.slotHeight / 2;
         }
      }
   }

   public void a(boolean var1) {
      this.enabled = var1;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public int getListWidth() {
      return 220;
   }

   public void drawSelectionBox(int var1, int var2, int var3, int var4) {
      int var8 = this.getSize();
      Tessellator var9 = Tessellator.getInstance();
      WorldRenderer var10 = var9.getWorldRenderer();

      for (int var11 = 0; var11 < var8; var11++) {
         int var12 = var2 + var11 * this.slotHeight + this.headerPadding;
         int var13 = this.slotHeight - 4;
         if (var12 > this.bottom || var12 + var13 < this.top) {
            this.func_178040_a(var11, var1, var12);
         }

         if (this.showSelectionBox && this.isSelected(var11)) {
            int var14 = this.left + (this.width / 2 - this.getListWidth() / 2);
            int var15 = this.left + this.width / 2 + this.getListWidth() / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var10.pos(var14, var12 + var13 + 2, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var15, var12 + var13 + 2, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var15, var12 - 2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var14, var12 - 2, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            var10.pos(var14 + 1, var12 + var13 + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            var10.pos(var15 - 1, var12 + var13 + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var10.pos(var15 - 1, var12 - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            var10.pos(var14 + 1, var12 - 1, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            var9.draw();
            GlStateManager.enableTexture2D();
         }

         this.drawSlot(var11, var1, var12, var13, var3, var4);
      }
   }

   public int getScrollBarX() {
      return this.width / 2 + 124;
   }

   public void W(int var1, int var2, int var3, int var4) {
      this.W(0, var1, this.width, var2, v, l);
   }

   public void W(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var9 = (var5 >> 24 & 0xFF) / 255.0F;
      float var10 = (var5 >> 16 & 0xFF) / 255.0F;
      float var11 = (var5 >> 8 & 0xFF) / 255.0F;
      float var12 = (var5 & 0xFF) / 255.0F;
      float var13 = (var6 >> 24 & 0xFF) / 255.0F;
      float var14 = (var6 >> 16 & 0xFF) / 255.0F;
      float var15 = (var6 >> 8 & 0xFF) / 255.0F;
      float var16 = (var6 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator var17 = Tessellator.getInstance();
      WorldRenderer var18 = var17.getWorldRenderer();
      var18.begin(7, DefaultVertexFormats.POSITION_COLOR);
      var18.pos(var3, var2, 0.0).color(var10, var11, var12, var9).endVertex();
      var18.pos(var1, var2, 0.0).color(var10, var11, var12, var9).endVertex();
      var18.pos(var1, var4, 0.0).color(var14, var15, var16, var13).endVertex();
      var18.pos(var3, var4, 0.0).color(var14, var15, var16, var13).endVertex();
      var17.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void setSlotXBoundsFromLeft(int var1) {
      this.left = var1;
      this.right = var1 + this.width;
   }

   public int l() {
      return this.slotHeight;
   }
}
