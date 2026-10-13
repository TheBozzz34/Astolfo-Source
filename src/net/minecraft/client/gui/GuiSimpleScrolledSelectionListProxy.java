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

   public GuiSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getMinecraft(), var2, var3, var4, var5, var6);
      this.field_178050_u = var1;
   }

   @Override
   public int getSize() {
      return this.field_178050_u.Q();
   }

   @Override
   public void elementClicked(int var1, boolean var2, int var3, int var4) {
      this.field_178050_u.selectItem(var1, var2, var3, var4);
   }

   @Override
   public boolean isSelected(int var1) {
      return this.field_178050_u.isSelectedItem(var1);
   }

   @Override
   public void drawBackground() {
      this.field_178050_u.renderBackground();
   }

   @Override
   public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_178050_u.renderItem(var1, var2, var3, var4, var5, var6);
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
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = c ^ 50800915904283L;
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
         this.W(0, this.top, 255, 255);
         this.W(this.bottom, this.height, 255, 255);
         GlStateManager.enableBlend();
         long var10000 = 76897885860284166L ^ var4;
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         var10000 = 835214825381328682L ^ var4;
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
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
         var10000 = 7603328013323868741L ^ var4;
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }
}
