package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.MathHelper;

public class GuiSimpleScrolledSelectionListProxy extends GuiSlot {
   private final RealmsSimpleScrolledSelectionList field_178050_u;

   public GuiSimpleScrolledSelectionListProxy(
      RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn
   ) {
      super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.field_178050_u = realmsSimpleScrolledSelectionList;
   }

   @Override
   public int getSize() {
      return this.field_178050_u.getItemCount();
   }

   @Override
   public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      this.field_178050_u.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return this.field_178050_u.isSelectedItem(slotIndex);
   }

   @Override
   public void drawBackground() {
      this.field_178050_u.renderBackground();
   }

   @Override
   public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
      this.field_178050_u.renderItem(entryID, i, j, k, mouseXIn, mouseYIn);
   }

   public int getWidth() {
      return super.width;
   }

   public int getMouseY() {
      return super.mouseY;
   }

   public int getMouseX() {
      return super.mouseX;
   }

   @Override
   public int getContentHeight() {
      return this.field_178050_u.getMaxPosition();
   }

   @Override
   public int getScrollBarX() {
      return this.field_178050_u.getScrollbarPosition();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
   }

   @Override
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
         this.overlayBackground(0, this.top, 255, 255);
         this.overlayBackground(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
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
}
