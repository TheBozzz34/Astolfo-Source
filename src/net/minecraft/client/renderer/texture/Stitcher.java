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

   public Stitcher(int maxTextureWidth, int maxTextureHeight, boolean bl, int i, int mipmapLevel) {
      this.mipmapLevelStitcher = mipmapLevel;
      this.maxWidth = maxTextureWidth;
      this.maxHeight = maxTextureHeight;
      this.forcePowerOf2 = bl;
      this.maxTileDimension = i;
   }

   public int getCurrentWidth() {
      return this.currentWidth;
   }

   public int getCurrentHeight() {
      return this.currentHeight;
   }

   public void addSprite(TextureAtlasSprite textureAtlasSprite) {
      Stitcher.Holder holder = new Stitcher.Holder(textureAtlasSprite, this.mipmapLevelStitcher);
      if (this.maxTileDimension > 0) {
         holder.setNewDimension(this.maxTileDimension);
      }

      this.setStitchHolders.add(holder);
   }

   public void doStitch() {
      Stitcher.Holder[] holders = this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
      Arrays.sort(holders);

      for (Stitcher.Holder holder : holders) {
         if (!this.allocateSlot(holder)) {
            String string = String.format(
               "Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?",
               holder.getAtlasSprite().getIconName(),
               holder.getAtlasSprite().getIconWidth(),
               holder.getAtlasSprite().getIconHeight(),
               this.currentWidth,
               this.currentHeight,
               this.maxWidth,
               this.maxHeight
            );
            throw new StitcherException(holder, string);
         }
      }

      if (this.forcePowerOf2) {
         this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
         this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
      }
   }

   public List<TextureAtlasSprite> getStichSlots() {
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : this.stitchSlots) {
         ((Stitcher.Slot)object).getAllStitchSlots(arrayList);
      }

      ArrayList arrayList2 = Lists.newArrayList();

      for (Object object2 : arrayList) {
         Stitcher.Slot slot = (Stitcher.Slot)object2;
         Stitcher.Holder holder = slot.getStitchHolder();
         TextureAtlasSprite textureAtlasSprite = holder.getAtlasSprite();
         textureAtlasSprite.initSprite(this.currentWidth, this.currentHeight, slot.getOriginX(), slot.getOriginY(), holder.isRotated());
         arrayList2.add(textureAtlasSprite);
      }

      return arrayList2;
   }

   private static int getMipmapDimension(int i, int j) {
      return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
   }

   private boolean allocateSlot(Stitcher.Holder holder) {
      for (int i = 0; i < this.stitchSlots.size(); i++) {
         if (this.stitchSlots.get(i).addSlot(holder)) {
            return true;
         }

         holder.rotate();
         if (this.stitchSlots.get(i).addSlot(holder)) {
            return true;
         }

         holder.rotate();
      }

      return this.expandAndAllocateSlot(holder);
   }

   private boolean expandAndAllocateSlot(Stitcher.Holder holder) {
      int i = Math.min(holder.getWidth(), holder.getHeight());
      boolean bl = this.currentWidth == 0 && this.currentHeight == 0;
      boolean bl2;
      if (this.forcePowerOf2) {
         int j = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
         int k = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
         int l = MathHelper.roundUpToPowerOfTwo(this.currentWidth + i);
         int m = MathHelper.roundUpToPowerOfTwo(this.currentHeight + i);
         boolean bl3 = l <= this.maxWidth;
         boolean bl4 = m <= this.maxHeight;
         if (!bl3 && !bl4) {
            return false;
         }

         boolean bl5 = j != l;
         boolean bl6 = k != m;
         if (bl5 ^ bl6) {
            bl2 = !bl5;
         } else {
            bl2 = bl3 && j <= k;
         }
      } else {
         boolean bl7 = this.currentWidth + i <= this.maxWidth;
         boolean bl8 = this.currentHeight + i <= this.maxHeight;
         if (!bl7 && !bl8) {
            return false;
         }

         bl2 = bl7 && (bl || this.currentWidth <= this.currentHeight);
      }

      int n = Math.max(holder.getWidth(), holder.getHeight());
      if (MathHelper.roundUpToPowerOfTwo((!bl2 ? this.currentHeight : this.currentWidth) + n) > (!bl2 ? this.maxHeight : this.maxWidth)) {
         return false;
      }

      Stitcher.Slot slot;
      if (bl2) {
         if (holder.getWidth() > holder.getHeight()) {
            holder.rotate();
         }

         if (this.currentHeight == 0) {
            this.currentHeight = holder.getHeight();
         }

         slot = new Stitcher.Slot(this.currentWidth, 0, holder.getWidth(), this.currentHeight);
         this.currentWidth = this.currentWidth + holder.getWidth();
      } else {
         slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, holder.getHeight());
         this.currentHeight = this.currentHeight + holder.getHeight();
      }

      slot.addSlot(holder);
      this.stitchSlots.add(slot);
      return true;
   }

   public static class Holder implements Comparable<Stitcher.Holder> {
      private final TextureAtlasSprite theTexture;
      private final int width;
      private final int height;
      private final int mipmapLevelHolder;
      private boolean rotated;
      private float scaleFactor = 1.0F;

      public Holder(TextureAtlasSprite textureAtlasSprite, int i) {
         this.theTexture = textureAtlasSprite;
         this.width = textureAtlasSprite.getIconWidth();
         this.height = textureAtlasSprite.getIconHeight();
         this.mipmapLevelHolder = i;
         this.rotated = Stitcher.getMipmapDimension(this.height, i) > Stitcher.getMipmapDimension(this.width, i);
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

      public void setNewDimension(int i) {
         if (this.width > i && this.height > i) {
            this.scaleFactor = (float)i / Math.min(this.width, this.height);
         }
      }

      @Override
      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.theTexture.getIconName() + '}';
      }

      public int compareTo(Stitcher.Holder holder) {
         int i;
         if (this.getHeight() == holder.getHeight()) {
            if (this.getWidth() == holder.getWidth()) {
               if (this.theTexture.getIconName() == null) {
                  return holder.theTexture.getIconName() == null ? 0 : -1;
               }

               return this.theTexture.getIconName().compareTo(holder.theTexture.getIconName());
            }

            i = this.getWidth() < holder.getWidth() ? 1 : -1;
         } else {
            i = this.getHeight() < holder.getHeight() ? 1 : -1;
         }

         return i;
      }
   }

   public static class Slot {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Slot> subSlots;
      private Stitcher.Holder holder;

      public Slot(int i, int j, int widthIn, int heightIn) {
         this.originX = i;
         this.originY = j;
         this.width = widthIn;
         this.height = heightIn;
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

      public boolean addSlot(Stitcher.Holder holderIn) {
         if (this.holder != null) {
            return false;
         }

         int i = holderIn.getWidth();
         int j = holderIn.getHeight();
         if (i <= this.width && j <= this.height) {
            if (i == this.width && j == this.height) {
               this.holder = holderIn;
               return true;
            }

            if (this.subSlots == null) {
               this.subSlots = Lists.newArrayListWithCapacity(1);
               this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
               int k = this.width - i;
               int l = this.height - j;
               if (l > 0 && k > 0) {
                  int m = Math.max(this.height, k);
                  int n = Math.max(this.width, l);
                  if (m >= n) {
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                     this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                  } else {
                     this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                  }
               } else if (k == 0) {
                  this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
               } else if (l == 0) {
                  this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
               }
            }

            for (Object object : this.subSlots) {
               if (((Stitcher.Slot)object).addSlot(holderIn)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void getAllStitchSlots(List<Stitcher.Slot> list) {
         if (this.holder != null) {
            list.add(this);
         } else if (this.subSlots != null) {
            for (Object object : this.subSlots) {
               ((Stitcher.Slot)object).getAllStitchSlots(list);
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
            + '}';
      }
   }
}
