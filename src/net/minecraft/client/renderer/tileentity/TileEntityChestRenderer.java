package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest> {
   private static final ResourceLocation textureTrappedDouble = new ResourceLocation("textures/entity/chest/trapped_double.png");
   private static final ResourceLocation textureChristmasDouble = new ResourceLocation("textures/entity/chest/christmas_double.png");
   private static final ResourceLocation textureNormalDouble = new ResourceLocation("textures/entity/chest/normal_double.png");
   private static final ResourceLocation textureTrapped = new ResourceLocation("textures/entity/chest/trapped.png");
   private static final ResourceLocation textureChristmas = new ResourceLocation("textures/entity/chest/christmas.png");
   private static final ResourceLocation textureNormal = new ResourceLocation("textures/entity/chest/normal.png");
   private ModelChest simpleChest = new ModelChest();
   private ModelChest largeChest = new ModelLargeChest();
   private boolean isChristmas;
   private static final long a = 27772768693376L;

   public TileEntityChestRenderer() {
      Calendar var3 = Calendar.getInstance();
      if (var3.get(2) + 1 == 12 && var3.get(5) >= 24 && var3.get(5) <= 26) {
         this.isChristmas = true;
      }
   }

   public void renderTileEntityAt(TileEntityChest var1, double var2, double var4, double var6, float var8, int var9) {
      long var10 = a ^ 17263430838569L;
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      int var13;
      if (!var1.hasWorldObj()) {
         var13 = 0;
      } else {
         Block var14 = var1.getBlockType();
         var13 = var1.getBlockMetadata();
         if (var14 instanceof BlockChest && var13 == 0) {
            ((BlockChest)var14).checkForSurroundingChests(var1.getWorld(), var1.getPos(), var1.getWorld().getBlockState(var1.getPos()));
            var13 = var1.getBlockMetadata();
         }

         var1.checkForAdjacentChests();
      }

      if (var1.adjacentChestZNeg == null && var1.adjacentChestXNeg == null) {
         ModelChest var18;
         if (var1.adjacentChestXPos == null && var1.adjacentChestZPos == null) {
            var18 = this.simpleChest;
            if (var9 >= 0) {
               this.bindTexture(DESTROY_STAGES[var9]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               long var22 = 4062313224494743971L ^ var10;
               GlStateManager.scale(4.0F, 4.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (var1.getChestType() == 1) {
               this.bindTexture(textureTrapped);
            } else if (this.isChristmas) {
               this.bindTexture(textureChristmas);
            } else {
               this.bindTexture(textureNormal);
            }
         } else {
            var18 = this.largeChest;
            if (var9 >= 0) {
               this.bindTexture(DESTROY_STAGES[var9]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               long var10000 = 1680150609612396418L ^ var10;
               GlStateManager.scale(8.0F, 4.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (var1.getChestType() == 1) {
               this.bindTexture(textureTrappedDouble);
            } else if (this.isChristmas) {
               this.bindTexture(textureChristmasDouble);
            } else {
               this.bindTexture(textureNormalDouble);
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         if (var9 < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.translate((float)var2, (float)var4 + 1.0F, (float)var6 + 1.0F);
         GlStateManager.scale(1.0F, -1.0F, -1.0F);
         GlStateManager.translate(0.5F, 0.5F, 0.5F);
         short var15 = 0;
         if (var13 == 2) {
            var15 = 180;
         }

         if (var13 == 3) {
            var15 = 0;
         }

         if (var13 == 4) {
            var15 = 90;
         }

         if (var13 == 5) {
            var15 = -90;
         }

         if (var13 == 2 && var1.adjacentChestXPos != null) {
            GlStateManager.translate(1.0F, 0.0F, 0.0F);
         }

         if (var13 == 5 && var1.adjacentChestZPos != null) {
            GlStateManager.translate(0.0F, 0.0F, -1.0F);
         }

         GlStateManager.rotate(var15, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5F, -0.5F, -0.5F);
         float var16 = var1.prevLidAngle + (var1.lidAngle - var1.prevLidAngle) * var8;
         if (var1.adjacentChestZNeg != null) {
            float var17 = var1.adjacentChestZNeg.prevLidAngle + (var1.adjacentChestZNeg.lidAngle - var1.adjacentChestZNeg.prevLidAngle) * var8;
            if (var17 > var16) {
               var16 = var17;
            }
         }

         if (var1.adjacentChestXNeg != null) {
            float var21 = var1.adjacentChestXNeg.prevLidAngle + (var1.adjacentChestXNeg.lidAngle - var1.adjacentChestXNeg.prevLidAngle) * var8;
            if (var21 > var16) {
               var16 = var21;
            }
         }

         var16 = 1.0F - var16;
         var16 = 1.0F - var16 * var16 * var16;
         var18.chestLid.rotateAngleX = -(var16 * (float) Math.PI / 2.0F);
         var18.renderAll();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (var9 >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            long var23 = 1680150609612396418L ^ var10;
            GlStateManager.matrixMode(5888);
         }
      }
   }
}
