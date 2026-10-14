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
   private static final long c = 17305418659257L;

   public GuiSimpleScrolledSelectionListProxy(
      RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn
   ) {
      super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.field_178050_u = realmsSimpleScrolledSelectionList;
   }

   @Override
   public int getSize() {
      return this.field_178050_u.Q();
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

   public int i() {
      return super.width;
   }

   public int h() {
      return super.mouseY;
   }

   public int Q() {
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
      long l = c ^ 50800915904283L;
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
         int m = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(k, m, tessellator);
         }

         this.drawSelectionBox(k, m, mouseXIn, mouseYIn);
         GlStateManager.disableDepth();
         this.W(0, this.top, 255, 255);
         this.W(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         long n = 76897885860284166L ^ l;
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         n = 835214825381328682L ^ l;
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         int i2 = this.func_148135_f();
         if (i2 > 0) {
            int i3 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            i3 = MathHelper.clamp_int(i3, 32, this.bottom - this.top - 8);
            int i4 = (int)this.amountScrolled * (this.bottom - this.top - i3) / i2 + this.top;
            if (i4 < this.top) {
               i4 = this.top;
            }

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(j, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(j, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i, i4 + i3, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(j, i4 + i3, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(j, i4, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i, i4, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i, i4 + i3 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(j - 1, i4 + i3 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(j - 1, i4, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            worldRenderer.pos(i, i4, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
         }

         this.func_148142_b(mouseXIn, mouseYIn);
         GlStateManager.enableTexture2D();
         n = 7603328013323868741L ^ l;
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }
}
