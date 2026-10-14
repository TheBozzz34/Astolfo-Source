package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest> {
   private static final ResourceLocation ENDER_CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
   private ModelChest field_147521_c = new ModelChest();
   private static final long a = 94016640789393L;

   public void renderTileEntityAt(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage) {
      long l = a ^ 20856399181768L;
      int i = 0;
      if (te.hasWorldObj()) {
         i = te.getBlockMetadata();
      }

      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         long m = 544139840591025713L ^ l;
         GlStateManager.scale(4.0F, 4.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(ENDER_CHEST_TEXTURE);
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
      GlStateManager.scale(1.0F, -1.0F, -1.0F);
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      short s = 0;
      if (i == 2) {
         s = 180;
      }

      if (i == 3) {
         s = 0;
      }

      if (i == 4) {
         s = 90;
      }

      if (i == 5) {
         s = -90;
      }

      GlStateManager.rotate(s, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
      f = 1.0F - f;
      f = 1.0F - f * f * f;
      this.field_147521_c.chestLid.rotateAngleX = -(f * (float) Math.PI / 2.0F);
      this.field_147521_c.renderAll();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         long n = 8359856071166250729L ^ l;
         GlStateManager.matrixMode(5888);
      }
   }
}
