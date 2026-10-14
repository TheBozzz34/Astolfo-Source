package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantmentTableRenderer extends TileEntitySpecialRenderer<TileEntityEnchantmentTable> {
   private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private ModelBook field_147541_c = new ModelBook();
   private static final long a = 26907031411969L;

   public void renderTileEntityAt(TileEntityEnchantmentTable te, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
      float f = te.tickCount + partialTicks;
      GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);
      float g = te.bookRotation - te.bookRotationPrev;

      while (g >= (float) Math.PI) {
         g -= (float) (Math.PI * 2);
      }

      while (g < (float) -Math.PI) {
         g += (float) (Math.PI * 2);
      }

      float h = te.bookRotationPrev + g * partialTicks;
      GlStateManager.rotate(-h * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f2 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
      float f3 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
      f2 = (f2 - MathHelper.truncateDoubleToInt(f2)) * 1.6F - 0.3F;
      f3 = (f3 - MathHelper.truncateDoubleToInt(f3)) * 1.6F - 0.3F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      float f4 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
      GlStateManager.enableCull();
      this.field_147541_c.render((Entity)null, f, f2, f3, f4, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
