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

   public void renderTileEntityAt(TileEntityEnchantmentTable var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 0.75F, (float)var6 + 0.5F);
      float var14 = var1.tickCount + var8;
      GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(var14 * 0.1F) * 0.01F, 0.0F);
      float var15 = var1.bookRotation - var1.bookRotationPrev;

      while (var15 >= (float) Math.PI) {
         var15 -= (float) (Math.PI * 2);
      }

      while (var15 < (float) -Math.PI) {
         var15 += (float) (Math.PI * 2);
      }

      float var16 = var1.bookRotationPrev + var15 * var8;
      GlStateManager.rotate(-var16 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float var17 = var1.pageFlipPrev + (var1.pageFlip - var1.pageFlipPrev) * var8 + 0.25F;
      float var18 = var1.pageFlipPrev + (var1.pageFlip - var1.pageFlipPrev) * var8 + 0.75F;
      var17 = (var17 - MathHelper.truncateDoubleToInt(var17)) * 1.6F - 0.3F;
      var18 = (var18 - MathHelper.truncateDoubleToInt(var18)) * 1.6F - 0.3F;
      if (var17 < 0.0F) {
         var17 = 0.0F;
      }

      if (var18 < 0.0F) {
         var18 = 0.0F;
      }

      if (var17 > 1.0F) {
         var17 = 1.0F;
      }

      if (var18 > 1.0F) {
         var18 = 1.0F;
      }

      float var19 = var1.bookSpreadPrev + (var1.bookSpread - var1.bookSpreadPrev) * var8;
      GlStateManager.enableCull();
      this.field_147541_c.render((Entity)null, var14, var17, var18, var19, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
