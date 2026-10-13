package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownload implements IImageBuffer {
   private int[] imageData;
   private int imageWidth;
   private int imageHeight;
   public boolean p;
   private static final long a = 120015968607390L;

   public ImageBufferDownload() {
   }

   public ImageBufferDownload(boolean var1) {
      this.p = var1;
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage var1) {
      if (var1 == null) {
         return null;
      }

      this.imageWidth = 64;
      this.imageHeight = 64;
      int var5 = var1.getWidth();
      int var6 = var1.getHeight();

      byte var7;
      for (var7 = 1; this.imageWidth < var5 || this.imageHeight < var6; var7 *= 2) {
         this.imageWidth *= 2;
         this.imageHeight *= 2;
      }

      BufferedImage var8 = new BufferedImage(this.imageWidth, this.imageHeight, 2);
      Graphics var9 = var8.getGraphics();
      var9.drawImage(var1, 0, 0, (ImageObserver)null);
      if (var1.getHeight() == 32 * var7 && !this.p) {
         var9.drawImage(var8, 24 * var7, 48 * var7, 20 * var7, 52 * var7, 4 * var7, 16 * var7, 8 * var7, 20 * var7, (ImageObserver)null);
         var9.drawImage(var8, 28 * var7, 48 * var7, 24 * var7, 52 * var7, 8 * var7, 16 * var7, 12 * var7, 20 * var7, (ImageObserver)null);
         var9.drawImage(var8, 20 * var7, 52 * var7, 16 * var7, 64 * var7, 8 * var7, 20 * var7, 12 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 24 * var7, 52 * var7, 20 * var7, 64 * var7, 4 * var7, 20 * var7, 8 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 28 * var7, 52 * var7, 24 * var7, 64 * var7, 0 * var7, 20 * var7, 4 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 32 * var7, 52 * var7, 28 * var7, 64 * var7, 12 * var7, 20 * var7, 16 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 40 * var7, 48 * var7, 36 * var7, 52 * var7, 44 * var7, 16 * var7, 48 * var7, 20 * var7, (ImageObserver)null);
         var9.drawImage(var8, 44 * var7, 48 * var7, 40 * var7, 52 * var7, 48 * var7, 16 * var7, 52 * var7, 20 * var7, (ImageObserver)null);
         var9.drawImage(var8, 36 * var7, 52 * var7, 32 * var7, 64 * var7, 48 * var7, 20 * var7, 52 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 40 * var7, 52 * var7, 36 * var7, 64 * var7, 44 * var7, 20 * var7, 48 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 44 * var7, 52 * var7, 40 * var7, 64 * var7, 40 * var7, 20 * var7, 44 * var7, 32 * var7, (ImageObserver)null);
         var9.drawImage(var8, 48 * var7, 52 * var7, 44 * var7, 64 * var7, 52 * var7, 20 * var7, 56 * var7, 32 * var7, (ImageObserver)null);
      }

      var9.dispose();
      this.imageData = ((DataBufferInt)var8.getRaster().getDataBuffer()).getData();
      if (!this.p) {
         this.setAreaOpaque(0, 0, 32 * var7, 16 * var7);
         this.setAreaTransparent(32 * var7, 0, 64 * var7, 32 * var7);
         this.setAreaOpaque(0, 16 * var7, 64 * var7, 32 * var7);
         this.setAreaTransparent(0, 32 * var7, 16 * var7, 48 * var7);
         this.setAreaTransparent(16 * var7, 32 * var7, 40 * var7, 48 * var7);
         this.setAreaTransparent(40 * var7, 32 * var7, 56 * var7, 48 * var7);
         this.setAreaTransparent(0, 48 * var7, 16 * var7, 64 * var7);
         this.setAreaOpaque(16 * var7, 48 * var7, 48 * var7, 64 * var7);
         this.setAreaTransparent(48 * var7, 48 * var7, 64 * var7, 64 * var7);
      }

      return var8;
   }

   @Override
   public void skinAvailable() {
   }

   private void setAreaTransparent(int var1, int var2, int var3, int var4) {
      if (!this.hasTransparency(var1, var2, var3, var4)) {
         for (int var8 = var1; var8 < var3; var8++) {
            for (int var9 = var2; var9 < var4; var9++) {
               this.imageData[var8 + var9 * this.imageWidth] = this.imageData[var8 + var9 * this.imageWidth] & 16777215;
            }
         }
      }
   }

   private void setAreaOpaque(int var1, int var2, int var3, int var4) {
      for (int var8 = var1; var8 < var3; var8++) {
         for (int var9 = var2; var9 < var4; var9++) {
            this.imageData[var8 + var9 * this.imageWidth] = this.imageData[var8 + var9 * this.imageWidth] | 0xFF000000;
         }
      }
   }

   private boolean hasTransparency(int var1, int var2, int var3, int var4) {
      for (int var8 = var1; var8 < var3; var8++) {
         for (int var9 = var2; var9 < var4; var9++) {
            int var10 = this.imageData[var8 + var9 * this.imageWidth];
            if ((var10 >> 24 & 0xFF) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
