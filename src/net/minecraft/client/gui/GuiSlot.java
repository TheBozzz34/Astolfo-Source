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
   private static final int OVERLAY_GRADIENT_TOP = new Color(7, 7, 7).getRGB();
   private static final int OVERLAY_GRADIENT_BOTTOM = new Color(17, 17, 17).getRGB();

   public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
      this.mc = mcIn;
      this.width = width;
      this.height = height;
      this.top = topIn;
      this.bottom = bottomIn;
      this.slotHeight = slotHeightIn;
      this.left = 0;
      this.right = width;
   }

   public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn) {
      this.width = widthIn;
      this.height = heightIn;
      this.top = topIn;
      this.bottom = bottomIn;
      this.left = 0;
      this.right = widthIn;
   }

   public void setShowSelectionBox(boolean showSelectionBoxIn) {
      this.showSelectionBox = showSelectionBoxIn;
   }

   public void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn) {
      this.hasListHeader = hasListHeaderIn;
      this.headerPadding = headerPaddingIn;
      if (!hasListHeaderIn) {
         this.headerPadding = 0;
      }
   }

   public abstract int getSize();

   public abstract void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY);

   public abstract boolean isSelected(int slotIndex);

   public int getContentHeight() {
      return this.getSize() * this.slotHeight + this.headerPadding;
   }

   public abstract void drawBackground();

   public void func_178040_a(int i, int j, int k) {
   }

   public abstract void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn);

   public void drawListHeader(int i, int j, Tessellator tessellator) {
   }

   public void func_148132_a(int i, int j) {
   }

   public void func_148142_b(int i, int j) {
   }

   public int getSlotIndexFromScreenCoords(int i, int j) {
      int k = this.left + this.width / 2 - this.getListWidth() / 2;
      int l = this.left + this.width / 2 + this.getListWidth() / 2;
      int m = j - this.top - this.headerPadding + (int)this.amountScrolled - 4;
      int n = m / this.slotHeight;
      return i < this.getScrollBarX() && i >= k && i <= l && n >= 0 && m >= 0 && n < this.getSize() ? n : -1;
   }

   public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
      this.scrollUpButtonID = scrollUpButtonIDIn;
      this.scrollDownButtonID = scrollDownButtonIDIn;
   }

   public void bindAmountScrolled() {
      this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, this.func_148135_f());
   }

   public int func_148135_f() {
      return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
   }

   public int getAmountScrolled() {
      return (int)this.amountScrolled;
   }

   public boolean isMouseYWithinSlotBounds(int i) {
      return i >= this.top && i <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
   }

   public void scrollBy(int amount) {
      this.amountScrolled += amount;
      this.bindAmountScrolled();
      this.initialClickY = -2;
   }

   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id == this.scrollUpButtonID) {
            this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         } else if (button.id == this.scrollDownButtonID) {
            this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         }
      }
   }

   public void drawScreen(int mouseXIn, int mouseYIn, float f) {
      if (this.field_178041_q) {
         this.mouseX = mouseXIn;
         this.mouseY = mouseYIn;
         this.drawBackground();
         int i = this.getScrollBarX();
         int j = i + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int l = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(k, l, tessellator);
         }

         this.drawSelectionBox(k, l, mouseXIn, mouseYIn);
         GlStateManager.disableDepth();
         byte b = 4;
         this.overlayBackground(0, this.top, 255, 255);
         this.overlayBackground(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(this.left, this.top + b, 0.0).tex(0.0, 1.0).color(0, 0, 0, 0).endVertex();
         worldRenderer.pos(this.right, this.top + b, 0.0).tex(1.0, 1.0).color(0, 0, 0, 0).endVertex();
         worldRenderer.pos(this.right, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(this.left, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
         tessellator.draw();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(this.left, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(this.right, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(this.right, this.bottom - b, 0.0).tex(1.0, 0.0).color(0, 0, 0, 0).endVertex();
         worldRenderer.pos(this.left, this.bottom - b, 0.0).tex(0.0, 0.0).color(0, 0, 0, 0).endVertex();
         tessellator.draw();
         int m = this.func_148135_f();
         if (m > 0) {
            int n = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            n = MathHelper.clamp_int(n, 32, this.bottom - this.top - 8);
            int i2 = (int)this.amountScrolled * (this.bottom - this.top - n) / m + this.top;
            if (i2 < this.top) {
               i2 = this.top;
            }

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(j, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(j, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i, i2 + n, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(j, i2 + n, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(j, i2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i, i2, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i, i2 + n - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(j - 1, i2 + n - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(j - 1, i2, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(i, i2, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
         }

         this.func_148142_b(mouseXIn, mouseYIn);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }

   public void handleMouseInput() {
      if (this.isMouseYWithinSlotBounds(this.mouseY)) {
         if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
            int i = (this.width - this.getListWidth()) / 2;
            int j = (this.width + this.getListWidth()) / 2;
            int k = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int l = k / this.slotHeight;
            if (l < this.getSize() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0) {
               this.elementClicked(l, false, this.mouseX, this.mouseY);
               this.selectedElement = l;
            } else if (this.mouseX >= i && this.mouseX <= j && k < 0) {
               this.func_148132_a(this.mouseX - i, this.mouseY - this.top + (int)this.amountScrolled - 4);
            }
         }

         if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
            this.initialClickY = -1;
         } else if (this.initialClickY == -1) {
            boolean bl = true;
            if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
               int m = (this.width - this.getListWidth()) / 2;
               int n = (this.width + this.getListWidth()) / 2;
               int i2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
               int i3 = i2 / this.slotHeight;
               if (i3 < this.getSize() && this.mouseX >= m && this.mouseX <= n && i3 >= 0 && i2 >= 0) {
                  boolean bl2 = i3 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                  this.elementClicked(i3, bl2, this.mouseX, this.mouseY);
                  this.selectedElement = i3;
                  this.lastClicked = Minecraft.getSystemTime();
               } else if (this.mouseX >= m && this.mouseX <= n && i2 < 0) {
                  this.func_148132_a(this.mouseX - m, this.mouseY - this.top + (int)this.amountScrolled - 4);
                  bl = false;
               }

               int i4 = this.getScrollBarX();
               int i5 = i4 + 6;
               if (this.mouseX >= i4 && this.mouseX <= i5) {
                  this.scrollMultiplier = -1.0F;
                  int i6 = this.func_148135_f();
                  if (i6 < 1) {
                     i6 = 1;
                  }

                  int i7 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getContentHeight());
                  i7 = MathHelper.clamp_int(i7, 32, this.bottom - this.top - 8);
                  this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - i7) / i6);
               } else {
                  this.scrollMultiplier = 1.0F;
               }

               if (bl) {
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

         int i8 = Mouse.getEventDWheel();
         if (i8 != 0) {
            if (i8 > 0) {
               i8 = -1;
            } else if (i8 < 0) {
               i8 = 1;
            }

            this.amountScrolled = this.amountScrolled + i8 * this.slotHeight / 2;
         }
      }
   }

   public void setEnabled(boolean enabledIn) {
      this.enabled = enabledIn;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public int getListWidth() {
      return 220;
   }

   public void drawSelectionBox(int i, int j, int mouseXIn, int mouseYIn) {
      int k = this.getSize();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();

      for (int l = 0; l < k; l++) {
         int m = j + l * this.slotHeight + this.headerPadding;
         int n = this.slotHeight - 4;
         if (m > this.bottom || m + n < this.top) {
            this.func_178040_a(l, i, m);
         }

         if (this.showSelectionBox && this.isSelected(l)) {
            int i2 = this.left + (this.width / 2 - this.getListWidth() / 2);
            int i3 = this.left + this.width / 2 + this.getListWidth() / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i2, m + n + 2, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i3, m + n + 2, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i3, m - 2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i2, m - 2, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i2 + 1, m + n + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i3 - 1, m + n + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i3 - 1, m - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i2 + 1, m - 1, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
         }

         this.drawSlot(l, i, m, n, mouseXIn, mouseYIn);
      }
   }

   public int getScrollBarX() {
      return this.width / 2 + 124;
   }

   public void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
      this.drawVerticalGradient(0, startY, this.width, endY, OVERLAY_GRADIENT_TOP, OVERLAY_GRADIENT_BOTTOM);
   }

   public void drawVerticalGradient(int i, int j, int k, int l, int m, int n) {
      float f = (m >> 24 & 0xFF) / 255.0F;
      float g = (m >> 16 & 0xFF) / 255.0F;
      float h = (m >> 8 & 0xFF) / 255.0F;
      float f2 = (m & 0xFF) / 255.0F;
      float f3 = (n >> 24 & 0xFF) / 255.0F;
      float f4 = (n >> 16 & 0xFF) / 255.0F;
      float f5 = (n >> 8 & 0xFF) / 255.0F;
      float f6 = (n & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(k, j, 0.0).color(g, h, f2, f).endVertex();
      worldRenderer.pos(i, j, 0.0).color(g, h, f2, f).endVertex();
      worldRenderer.pos(i, l, 0.0).color(f4, f5, f6, f3).endVertex();
      worldRenderer.pos(k, l, 0.0).color(f4, f5, f6, f3).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void setSlotXBoundsFromLeft(int leftIn) {
      this.left = leftIn;
      this.right = leftIn + this.width;
   }

   public int getSlotHeight() {
      return this.slotHeight;
   }
}
