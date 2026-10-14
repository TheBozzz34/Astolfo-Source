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

   public ImageBufferDownload() {
   }

   public ImageBufferDownload(boolean bl) {
      this.p = bl;
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage image) {
      if (image == null) {
         return null;
      }

      this.imageWidth = 64;
      this.imageHeight = 64;
      int i = image.getWidth();
      int j = image.getHeight();

      byte b;
      for (b = 1; this.imageWidth < i || this.imageHeight < j; b *= 2) {
         this.imageWidth *= 2;
         this.imageHeight *= 2;
      }

      BufferedImage bufferedImage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
      Graphics graphics = bufferedImage.getGraphics();
      graphics.drawImage(image, 0, 0, (ImageObserver)null);
      if (image.getHeight() == 32 * b && !this.p) {
         graphics.drawImage(bufferedImage, 24 * b, 48 * b, 20 * b, 52 * b, 4 * b, 16 * b, 8 * b, 20 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 28 * b, 48 * b, 24 * b, 52 * b, 8 * b, 16 * b, 12 * b, 20 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 20 * b, 52 * b, 16 * b, 64 * b, 8 * b, 20 * b, 12 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 24 * b, 52 * b, 20 * b, 64 * b, 4 * b, 20 * b, 8 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 28 * b, 52 * b, 24 * b, 64 * b, 0 * b, 20 * b, 4 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 32 * b, 52 * b, 28 * b, 64 * b, 12 * b, 20 * b, 16 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 40 * b, 48 * b, 36 * b, 52 * b, 44 * b, 16 * b, 48 * b, 20 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 44 * b, 48 * b, 40 * b, 52 * b, 48 * b, 16 * b, 52 * b, 20 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 36 * b, 52 * b, 32 * b, 64 * b, 48 * b, 20 * b, 52 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 40 * b, 52 * b, 36 * b, 64 * b, 44 * b, 20 * b, 48 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 44 * b, 52 * b, 40 * b, 64 * b, 40 * b, 20 * b, 44 * b, 32 * b, (ImageObserver)null);
         graphics.drawImage(bufferedImage, 48 * b, 52 * b, 44 * b, 64 * b, 52 * b, 20 * b, 56 * b, 32 * b, (ImageObserver)null);
      }

      graphics.dispose();
      this.imageData = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
      if (!this.p) {
         this.setAreaOpaque(0, 0, 32 * b, 16 * b);
         this.setAreaTransparent(32 * b, 0, 64 * b, 32 * b);
         this.setAreaOpaque(0, 16 * b, 64 * b, 32 * b);
         this.setAreaTransparent(0, 32 * b, 16 * b, 48 * b);
         this.setAreaTransparent(16 * b, 32 * b, 40 * b, 48 * b);
         this.setAreaTransparent(40 * b, 32 * b, 56 * b, 48 * b);
         this.setAreaTransparent(0, 48 * b, 16 * b, 64 * b);
         this.setAreaOpaque(16 * b, 48 * b, 48 * b, 64 * b);
         this.setAreaTransparent(48 * b, 48 * b, 64 * b, 64 * b);
      }

      return bufferedImage;
   }

   @Override
   public void skinAvailable() {
   }

   private void setAreaTransparent(int i, int j, int k, int l) {
      if (!this.hasTransparency(i, j, k, l)) {
         for (int m = i; m < k; m++) {
            for (int n = j; n < l; n++) {
               this.imageData[m + n * this.imageWidth] = this.imageData[m + n * this.imageWidth] & 16777215;
            }
         }
      }
   }

   private void setAreaOpaque(int i, int j, int k, int l) {
      for (int m = i; m < k; m++) {
         for (int n = j; n < l; n++) {
            this.imageData[m + n * this.imageWidth] = this.imageData[m + n * this.imageWidth] | 0xFF000000;
         }
      }
   }

   private boolean hasTransparency(int i, int j, int k, int l) {
      for (int m = i; m < k; m++) {
         for (int n = j; n < l; n++) {
            int i2 = this.imageData[m + n * this.imageWidth];
            if ((i2 >> 24 & 0xFF) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
