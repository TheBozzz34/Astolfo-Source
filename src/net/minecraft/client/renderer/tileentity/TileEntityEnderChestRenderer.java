package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest> {
   private static final ResourceLocation ENDER_CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
   private ModelChest field_147521_c = new ModelChest();
   private static final long a = 94016640789393L;

   public void renderTileEntityAt(TileEntityEnderChest var1, double var2, double var4, double var6, float var8, int var9) {
      long var10 = a ^ 20856399181768L;
      int var13 = 0;
      if (var1.hasWorldObj()) {
         var13 = var1.getBlockMetadata();
      }

      if (var9 >= 0) {
         this.bindTexture(DESTROY_STAGES[var9]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         long var10000 = 544139840591025713L ^ var10;
         GlStateManager.scale(4.0F, 4.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(ENDER_CHEST_TEXTURE);
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate((float)var2, (float)var4 + 1.0F, (float)var6 + 1.0F);
      GlStateManager.scale(1.0F, -1.0F, -1.0F);
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      short var14 = 0;
      if (var13 == 2) {
         var14 = 180;
      }

      if (var13 == 3) {
         var14 = 0;
      }

      if (var13 == 4) {
         var14 = 90;
      }

      if (var13 == 5) {
         var14 = -90;
      }

      GlStateManager.rotate(var14, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      float var15 = var1.prevLidAngle + (var1.lidAngle - var1.prevLidAngle) * var8;
      var15 = 1.0F - var15;
      var15 = 1.0F - var15 * var15 * var15;
      this.field_147521_c.chestLid.rotateAngleX = -(var15 * (float) Math.PI / 2.0F);
      this.field_147521_c.renderAll();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (var9 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         long var18 = 8359856071166250729L ^ var10;
         GlStateManager.matrixMode(5888);
      }
   }
}
