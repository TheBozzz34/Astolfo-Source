package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

public class Stitcher {
   private final int mipmapLevelStitcher;
   private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
   private int currentWidth;
   private int currentHeight;
   private final int maxWidth;
   private final int maxHeight;
   private final boolean forcePowerOf2;
   private final int maxTileDimension;
   private static final long b = 816403471593L;

   public Stitcher(int var1, int var2, boolean var3, int var4, int var5) {
      this.mipmapLevelStitcher = var5;
      this.maxWidth = var1;
      this.maxHeight = var2;
      this.forcePowerOf2 = var3;
      this.maxTileDimension = var4;
   }

   public int f() {
      return this.currentWidth;
   }

   public int Y() {
      return this.currentHeight;
   }

   public void addSprite(TextureAtlasSprite var1) {
      Stitcher.Holder var5 = new Stitcher.Holder(var1, this.mipmapLevelStitcher);
      if (this.maxTileDimension > 0) {
         var5.setNewDimension(this.maxTileDimension);
      }

      this.setStitchHolders.add(var5);
   }

   public void doStitch() {
      Stitcher.Holder[] var4 = this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
      Arrays.sort(var4);

      for (Stitcher.Holder var8 : var4) {
         if (!this.allocateSlot(var8)) {
            String var9 = String.format(
               "Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?",
               var8.getAtlasSprite().getIconName(),
               var8.getAtlasSprite().getIconWidth(),
               var8.getAtlasSprite().getIconHeight(),
               this.currentWidth,
               this.currentHeight,
               this.maxWidth,
               this.maxHeight
            );
            throw new StitcherException(var8, var9);
         }
      }

      if (this.forcePowerOf2) {
         this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
         this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
      }
   }

   public List<TextureAtlasSprite> getStichSlots() {
      ArrayList var4 = Lists.newArrayList();

      for (Object var6 : this.stitchSlots) {
         ((Stitcher.Slot)var6).getAllStitchSlots(var4);
      }

      ArrayList var11 = Lists.newArrayList();

      for (Object var7 : var4) {
         Stitcher.Slot var8 = (Stitcher.Slot)var7;
         Stitcher.Holder var9 = var8.getStitchHolder();
         TextureAtlasSprite var10 = var9.getAtlasSprite();
         var10.initSprite(this.currentWidth, this.currentHeight, var8.getOriginX(), var8.getOriginY(), var9.isRotated());
         var11.add(var10);
      }

      return var11;
   }

   private static int getMipmapDimension(int var0, int var1) {
      return (var0 >> var1) + ((var0 & (1 << var1) - 1) == 0 ? 0 : 1) << var1;
   }

   private boolean allocateSlot(Stitcher.Holder var1) {
      for (int var5 = 0; var5 < this.stitchSlots.size(); var5++) {
         if (this.stitchSlots.get(var5).addSlot(var1)) {
            return true;
         }

         var1.rotate();
         if (this.stitchSlots.get(var5).addSlot(var1)) {
            return true;
         }

         var1.rotate();
      }

      return this.expandAndAllocateSlot(var1);
   }

   private boolean expandAndAllocateSlot(Stitcher.Holder var1) {
      int var5 = Math.min(var1.getWidth(), var1.getHeight());
      boolean var6 = this.currentWidth == 0 && this.currentHeight == 0;
      boolean var7;
      if (this.forcePowerOf2) {
         int var8 = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
         int var9 = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
         int var10 = MathHelper.roundUpToPowerOfTwo(this.currentWidth + var5);
         int var11 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + var5);
         boolean var12 = var10 <= this.maxWidth;
         boolean var13 = var11 <= this.maxHeight;
         if (!var12 && !var13) {
            return false;
         }

         boolean var14 = var8 != var10;
         boolean var15 = var9 != var11;
         if (var14 ^ var15) {
            var7 = !var14;
         } else {
            var7 = var12 && var8 <= var9;
         }
      } else {
         boolean var16 = this.currentWidth + var5 <= this.maxWidth;
         boolean var18 = this.currentHeight + var5 <= this.maxHeight;
         if (!var16 && !var18) {
            return false;
         }

         var7 = var16 && (var6 || this.currentWidth <= this.currentHeight);
      }

      int var17 = Math.max(var1.getWidth(), var1.getHeight());
      if (MathHelper.roundUpToPowerOfTwo((!var7 ? this.currentHeight : this.currentWidth) + var17) > (!var7 ? this.maxHeight : this.maxWidth)) {
         return false;
      }

      Stitcher.Slot var19;
      if (var7) {
         if (var1.getWidth() > var1.getHeight()) {
            var1.rotate();
         }

         if (this.currentHeight == 0) {
            this.currentHeight = var1.getHeight();
         }

         var19 = new Stitcher.Slot(this.currentWidth, 0, var1.getWidth(), this.currentHeight);
         this.currentWidth = this.currentWidth + var1.getWidth();
      } else {
         var19 = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, var1.getHeight());
         this.currentHeight = this.currentHeight + var1.getHeight();
      }

      var19.addSlot(var1);
      this.stitchSlots.add(var19);
      return true;
   }

   public static class Holder implements Comparable<Stitcher.Holder> {
      private final TextureAtlasSprite theTexture;
      private final int width;
      private final int height;
      private final int mipmapLevelHolder;
      private boolean rotated;
      private float scaleFactor = 1.0F;
      private static final long a = 40177243970771L;
      private static final long g = -5790750834346164099L;

      public Holder(TextureAtlasSprite var1, int var2) {
         this.theTexture = var1;
         this.width = var1.getIconWidth();
         this.height = var1.getIconHeight();
         this.mipmapLevelHolder = var2;
         this.rotated = Stitcher.getMipmapDimension(this.height, var2) > Stitcher.getMipmapDimension(this.width, var2);
      }

      public TextureAtlasSprite getAtlasSprite() {
         return this.theTexture;
      }

      public int getWidth() {
         return this.rotated
            ? Stitcher.getMipmapDimension((int)(this.height * this.scaleFactor), this.mipmapLevelHolder)
            : Stitcher.getMipmapDimension((int)(this.width * this.scaleFactor), this.mipmapLevelHolder);
      }

      public int getHeight() {
         return this.rotated
            ? Stitcher.getMipmapDimension((int)(this.width * this.scaleFactor), this.mipmapLevelHolder)
            : Stitcher.getMipmapDimension((int)(this.height * this.scaleFactor), this.mipmapLevelHolder);
      }

      public void rotate() {
         this.rotated = !this.rotated;
      }

      public boolean isRotated() {
         return this.rotated;
      }

      public void setNewDimension(int var1) {
         if (this.width > var1 && this.height > var1) {
            this.scaleFactor = (float)var1 / Math.min(this.width, this.height);
         }
      }

      @Override
      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.theTexture.getIconName() + (int)g;
      }

      public int compareTo(Stitcher.Holder var1) {
         int var5;
         if (this.getHeight() == var1.getHeight()) {
            if (this.getWidth() == var1.getWidth()) {
               if (this.theTexture.getIconName() == null) {
                  return var1.theTexture.getIconName() == null ? 0 : -1;
               }

               return this.theTexture.getIconName().compareTo(var1.theTexture.getIconName());
            }

            var5 = this.getWidth() < var1.getWidth() ? 1 : -1;
         } else {
            var5 = this.getHeight() < var1.getHeight() ? 1 : -1;
         }

         return var5;
      }
   }

   public static class Slot {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Slot> subSlots;
      private Stitcher.Holder holder;
      private static final long a = 38365014766716L;
      private static final long f = 3613827521994293373L;

      public Slot(int var1, int var2, int var3, int var4) {
         this.originX = var1;
         this.originY = var2;
         this.width = var3;
         this.height = var4;
      }

      public Stitcher.Holder getStitchHolder() {
         return this.holder;
      }

      public int getOriginX() {
         return this.originX;
      }

      public int getOriginY() {
         return this.originY;
      }

      public boolean addSlot(Stitcher.Holder var1) {
         if (this.holder != null) {
            return false;
         }

         int var5 = var1.getWidth();
         int var6 = var1.getHeight();
         if (var5 <= this.width && var6 <= this.height) {
            if (var5 == this.width && var6 == this.height) {
               this.holder = var1;
               return true;
            }

            if (this.subSlots == null) {
               this.subSlots = Lists.newArrayListWithCapacity(1);
               this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, var5, var6));
               int var7 = this.width - var5;
               int var8 = this.height - var6;
               if (var8 > 0 && var7 > 0) {
                  int var9 = Math.max(this.height, var7);
                  int var10 = Math.max(this.width, var8);
                  if (var9 >= var10) {
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + var6, var5, var8));
                     this.subSlots.add(new Stitcher.Slot(this.originX + var5, this.originY, var7, this.height));
                  } else {
                     this.subSlots.add(new Stitcher.Slot(this.originX + var5, this.originY, var7, var6));
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + var6, this.width, var8));
                  }
               } else if (var7 == 0) {
                  this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + var6, var5, var8));
               } else if (var8 == 0) {
                  this.subSlots.add(new Stitcher.Slot(this.originX + var5, this.originY, var7, var6));
               }
            }

            for (Object var12 : this.subSlots) {
               if (((Stitcher.Slot)var12).addSlot(var1)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void getAllStitchSlots(List<Stitcher.Slot> var1) {
         if (this.holder != null) {
            var1.add(this);
         } else if (this.subSlots != null) {
            for (Object var6 : this.subSlots) {
               ((Stitcher.Slot)var6).getAllStitchSlots(var1);
            }
         }
      }

      @Override
      public String toString() {
         return "Slot{originX="
            + this.originX
            + ", originY="
            + this.originY
            + ", width="
            + this.width
            + ", height="
            + this.height
            + ", texture="
            + this.holder
            + ", subSlots="
            + this.subSlots
            + (int)f;
      }
   }
}
