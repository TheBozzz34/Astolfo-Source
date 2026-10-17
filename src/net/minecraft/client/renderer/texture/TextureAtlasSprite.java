package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;

public class TextureAtlasSprite {
   private final String iconName;
   public List<int[][]> framesTextureData = Lists.newArrayList();
   public int[][] interpolatedFrameData;
   private AnimationMetadataSection animationMetadata;
   public boolean rotated;
   public int originX;
   public int originY;
   public int width;
   public int height;
   private float minU;
   private float maxU;
   private float minV;
   private float maxV;
   public int frameCounter;
   public int tickCounter;
   private static String locationNameClock = "builtin/clock";
   private static String locationNameCompass = "builtin/compass";
   private int indexInMap = -1;
   public float baseU;
   public float baseV;
   public int sheetWidth;
   public int sheetHeight;
   public int glSpriteTextureId = -1;
   public TextureAtlasSprite spriteSingle = null;
   public boolean isSpriteSingle = false;
   public int mipmapLevels = 0;
   public TextureAtlasSprite spriteNormal = null;
   public TextureAtlasSprite spriteSpecular = null;
   public boolean isShadersSprite = false;

   private TextureAtlasSprite(TextureAtlasSprite textureAtlasSprite) {
      this.iconName = textureAtlasSprite.iconName;
      this.isSpriteSingle = true;
   }

   public TextureAtlasSprite(String spriteName) {
      this.iconName = spriteName;
      if (Config.isMultiTexture()) {
         this.spriteSingle = new TextureAtlasSprite(this);
      }
   }

   public static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
      String string = spriteResourceLocation.toString();
      return locationNameClock.equals(string)
         ? new TextureClock(string)
         : (locationNameCompass.equals(string) ? new TextureCompass(string) : new TextureAtlasSprite(string));
   }

   public static void setLocationNameClock(String clockName) {
      locationNameClock = clockName;
   }

   public static void setLocationNameCompass(String compassName) {
      locationNameCompass = compassName;
   }

   public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
      this.originX = originInX;
      this.originY = originInY;
      this.rotated = rotatedIn;
      float f = (float)(0.01F / inX);
      float g = (float)(0.01F / inY);
      this.minU = originInX / (float)inX + f;
      this.maxU = (originInX + this.width) / (float)inX - f;
      this.minV = (float)originInY / inY + g;
      this.maxV = (float)(originInY + this.height) / inY - g;
      this.baseU = Math.min(this.minU, this.maxU);
      this.baseV = Math.min(this.minV, this.maxV);
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }

      if (this.spriteNormal != null) {
         this.spriteNormal.initSprite(inX, inY, originInX, originInY, rotatedIn);
      }

      if (this.spriteSpecular != null) {
         this.spriteSpecular.initSprite(inX, inY, originInX, originInY, rotatedIn);
      }
   }

   public void copyFrom(TextureAtlasSprite atlasSpirit) {
      this.originX = atlasSpirit.originX;
      this.originY = atlasSpirit.originY;
      this.width = atlasSpirit.width;
      this.height = atlasSpirit.height;
      this.rotated = atlasSpirit.rotated;
      this.minU = atlasSpirit.minU;
      this.maxU = atlasSpirit.maxU;
      this.minV = atlasSpirit.minV;
      this.maxV = atlasSpirit.maxV;
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }
   }

   public int getOriginX() {
      return this.originX;
   }

   public int getOriginY() {
      return this.originY;
   }

   public int getIconWidth() {
      return this.width;
   }

   public int getIconHeight() {
      return this.height;
   }

   public float getMinU() {
      return this.minU;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public float getInterpolatedU(double u) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)u / 16.0F;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getInterpolatedV(double v) {
      float f = this.maxV - this.minV;
      return this.minV + f * ((float)v / 16.0F);
   }

   public String getIconName() {
      return this.iconName;
   }

   public void updateAnimation() {
      if (this.animationMetadata != null) {
         this.tickCounter++;
         if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
            int i = this.animationMetadata.getFrameIndex(this.frameCounter);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % j;
            this.tickCounter = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameCounter);
            boolean bl = false;
            boolean bl2 = this.isSpriteSingle;
            if (i != k && k >= 0 && k < this.framesTextureData.size()) {
               TextureUtil.uploadTextureMipmap(this.framesTextureData.get(k), this.width, this.height, this.originX, this.originY, bl, bl2);
            }
         } else if (this.animationMetadata.isInterpolate()) {
            this.updateAnimationInterpolated();
         }
      }
   }

   private void updateAnimationInterpolated() {
      double d = 1.0 - (double)this.tickCounter / this.animationMetadata.getFrameTimeSingle(this.frameCounter);
      int i = this.animationMetadata.getFrameIndex(this.frameCounter);
      int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
      int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);
      if (i != k && k >= 0 && k < this.framesTextureData.size()) {
         int[][] intss = this.framesTextureData.get(i);
         int[][] intss2 = this.framesTextureData.get(k);
         if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != intss.length) {
            this.interpolatedFrameData = new int[intss.length][];
         }

         for (int l = 0; l < intss.length; l++) {
            if (this.interpolatedFrameData[l] == null) {
               this.interpolatedFrameData[l] = new int[intss[l].length];
            }

            if (l < intss2.length && intss2[l].length == intss[l].length) {
               for (int m = 0; m < intss[l].length; m++) {
                  int n = intss[l][m];
                  int i2 = intss2[l][m];
                  int i3 = (int)(((n & 0xFF0000) >> 16) * d + ((i2 & 0xFF0000) >> 16) * (1.0 - d));
                  int i4 = (int)(((n & 0xFF00) >> 8) * d + ((i2 & 0xFF00) >> 8) * (1.0 - d));
                  int i5 = (int)((n & 0xFF) * d + (i2 & 0xFF) * (1.0 - d));
                  this.interpolatedFrameData[l][m] = n & 0xFF000000 | i3 << 16 | i4 << 8 | i5;
               }
            }
         }

         TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
      }
   }

   public int[][] getFrameTextureData(int index) {
      return this.framesTextureData.get(index);
   }

   public int getFrameCount() {
      return this.framesTextureData.size();
   }

   public void setIconWidth(int newWidth) {
      this.width = newWidth;
      if (this.spriteSingle != null) {
         this.spriteSingle.setIconWidth(this.width);
      }
   }

   public void setIconHeight(int newHeight) {
      this.height = newHeight;
      if (this.spriteSingle != null) {
         this.spriteSingle.setIconHeight(this.height);
      }
   }

   public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException {
      this.resetSprite();
      int i = images[0].getWidth();
      int j = images[0].getHeight();
      this.width = i;
      this.height = j;
      int[][] intss = new int[images.length][];

      for (int k = 0; k < images.length; k++) {
         BufferedImage bufferedImage = images[k];
         if (bufferedImage != null) {
            if (k > 0 && (bufferedImage.getWidth() != i >> k || bufferedImage.getHeight() != j >> k)) {
               throw new RuntimeException(
                  String.format(
                     "Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", k, bufferedImage.getWidth(), bufferedImage.getHeight(), i >> k, j >> k
                  )
               );
            }

            intss[k] = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), intss[k], 0, bufferedImage.getWidth());
         }
      }

      if (meta == null) {
         if (j != i) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.framesTextureData.add(intss);
      } else {
         int l = j / i;
         int m = i;
         int n = i;
         this.height = this.width;
         if (meta.getFrameCount() > 0) {
            for (int i2 : meta.getFrameIndexSet()) {
               if (i2 >= l) {
                  throw new RuntimeException("invalid frameindex " + i2);
               }

               this.allocateFrameTextureData(i2);
               this.framesTextureData.set(i2, getFrameTextureData(intss, m, n, i2));
            }

            this.animationMetadata = meta;
         } else {
            ArrayList arrayList = Lists.newArrayList();

            for (int i3 = 0; i3 < l; i3++) {
               this.framesTextureData.add(getFrameTextureData(intss, m, n, i3));
               arrayList.add(new AnimationFrame(i3, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(arrayList, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
         }
      }

      if (!this.isShadersSprite) {
         if (Config.isShaders()) {
            this.loadShadersSprites();
         }

         for (int i4 = 0; i4 < this.framesTextureData.size(); i4++) {
            int[][] intss2 = this.framesTextureData.get(i4);
            if (intss2 != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
               for (int i5 = 0; i5 < intss2.length; i5++) {
                  int[] ints = intss2[i5];
                  this.fixTransparentColor(ints);
               }
            }
         }

         if (this.spriteSingle != null) {
            this.spriteSingle.loadSprite(images, meta);
         }
      }
   }

   public void generateMipmaps(int level) {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.framesTextureData.size(); i++) {
         final int[][] intss = this.framesTextureData.get(i);
         if (intss != null) {
            try {
               arrayList.add(TextureUtil.generateMipmapData(level, this.width, intss));
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
               CrashReportCategory crashReportCategory = crashReport.makeCategory("Frame being iterated");
               crashReportCategory.addCrashSection("Frame index", i);
               crashReportCategory.addCrashSectionCallable("Frame sizes", new Callable<String>() {
                  public String call() throws Exception {
                     StringBuilder stringBuilder = new StringBuilder();

                     for (int[] ints : intss) {
                        if (stringBuilder.length() > 0) {
                           stringBuilder.append(", ");
                        }

                        stringBuilder.append(ints == null ? "null" : ints.length);
                     }

                     return stringBuilder.toString();
                  }
               });
               throw new ReportedException(crashReport);
            }
         }
      }

      this.setFramesTextureData(arrayList);
      if (this.spriteSingle != null) {
         this.spriteSingle.generateMipmaps(level);
      }
   }

   private void allocateFrameTextureData(int index) {
      if (this.framesTextureData.size() <= index) {
         for (int i = this.framesTextureData.size(); i <= index; i++) {
            this.framesTextureData.add(null);
         }
      }

      if (this.spriteSingle != null) {
         this.spriteSingle.allocateFrameTextureData(index);
      }
   }

   private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int i) {
      int[][] intss = new int[data.length][];

      for (int j = 0; j < data.length; j++) {
         int[] ints = data[j];
         if (ints != null) {
            intss[j] = new int[(rows >> j) * (columns >> j)];
            System.arraycopy(ints, i * intss[j].length, intss[j], 0, intss[j].length);
         }
      }

      return intss;
   }

   public void clearFramesTextureData() {
      this.framesTextureData.clear();
      if (this.spriteSingle != null) {
         this.spriteSingle.clearFramesTextureData();
      }
   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata != null;
   }

   public void setFramesTextureData(List<int[][]> newFramesTextureData) {
      this.framesTextureData = newFramesTextureData;
      if (this.spriteSingle != null) {
         this.spriteSingle.setFramesTextureData(newFramesTextureData);
      }
   }

   private void resetSprite() {
      this.animationMetadata = null;
      this.setFramesTextureData(Lists.newArrayList());
      this.frameCounter = 0;
      this.tickCounter = 0;
      if (this.spriteSingle != null) {
         this.spriteSingle.resetSprite();
      }
   }

   @Override
   public String toString() {
      return "TextureAtlasSprite{name='"
         + this.iconName
         + '\''
         + ", frameCount="
         + this.framesTextureData.size()
         + ", rotated="
         + this.rotated
         + ", x="
         + this.originX
         + ", y="
         + this.originY
         + ", height="
         + this.height
         + ", width="
         + this.width
         + ", u0="
         + this.minU
         + ", u1="
         + this.maxU
         + ", v0="
         + this.minV
         + ", v1="
         + this.maxV
         + '}';
   }

   public boolean hasCustomLoader(IResourceManager iResourceManager, ResourceLocation resourceLocation) {
      return false;
   }

   public boolean load(IResourceManager iResourceManager, ResourceLocation resourceLocation) {
      return true;
   }

   public int getIndexInMap() {
      return this.indexInMap;
   }

   public void setIndexInMap(int i) {
      this.indexInMap = i;
   }

   private void fixTransparentColor(int[] ints) {
      if (ints != null) {
         long l = 0L;
         long m = 0L;
         long n = 0L;
         long l2 = 0L;

         for (int i = 0; i < ints.length; i++) {
            int j = ints[i];
            int k = j >> 24 & 0xFF;
            if (k >= 16) {
               int i2 = j >> 16 & 0xFF;
               int i3 = j >> 8 & 0xFF;
               int i4 = j & 0xFF;
               l += i2;
               m += i3;
               n += i4;
               l2++;
            }
         }

         if (l2 > 0L) {
            int i5 = (int)(l / l2);
            int i6 = (int)(m / l2);
            int i7 = (int)(n / l2);
            int i8 = i5 << 16 | i6 << 8 | i7;

            for (int i9 = 0; i9 < ints.length; i9++) {
               int i10 = ints[i9];
               int i11 = i10 >> 24 & 0xFF;
               if (i11 <= 16) {
                  ints[i9] = i8;
               }
            }
         }
      }
   }

   public double getSpriteU16(float f) {
      float g = this.maxU - this.minU;
      return (f - this.minU) / g * 16.0F;
   }

   public double getSpriteV16(float f) {
      float g = this.maxV - this.minV;
      return (f - this.minV) / g * 16.0F;
   }

   public void bindSpriteTexture() {
      if (this.glSpriteTextureId < 0) {
         this.glSpriteTextureId = TextureUtil.glGenTextures();
         TextureUtil.allocateTextureImpl(this.glSpriteTextureId, this.mipmapLevels, this.width, this.height);
         TextureUtils.applyAnisotropicLevel();
      }

      TextureUtils.bindTexture(this.glSpriteTextureId);
   }

   public void deleteSpriteTexture() {
      if (this.glSpriteTextureId >= 0) {
         TextureUtil.deleteTexture(this.glSpriteTextureId);
         this.glSpriteTextureId = -1;
      }
   }

   public float toSingleU(float f) {
      f -= this.baseU;
      float g = (float)this.sheetWidth / this.width;
      return f * g;
   }

   public float toSingleV(float f) {
      f -= this.baseV;
      float g = (float)this.sheetHeight / this.height;
      return f * g;
   }

   public List<int[][]> getFramesTextureData() {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(this.framesTextureData);
      return arrayList;
   }

   public AnimationMetadataSection getAnimationMetadata() {
      return this.animationMetadata;
   }

   public void setAnimationMetadata(AnimationMetadataSection animationMetadataSection) {
      this.animationMetadata = animationMetadataSection;
   }

   private void loadShadersSprites() {
      this.mipmapLevels = Config.getTextureMap().getMipmapLevels();
      if (Shaders.configNormalMap) {
         String string = this.iconName + "_n";
         ResourceLocation resourceLocation = new ResourceLocation(string);
         resourceLocation = Config.getTextureMap().completeResourceLocation(resourceLocation, 0);
         if (Config.hasResource(resourceLocation)) {
            try {
               TextureAtlasSprite textureAtlasSprite = new TextureAtlasSprite(string);
               textureAtlasSprite.isShadersSprite = true;
               textureAtlasSprite.copyFrom(this);
               textureAtlasSprite.loadShaderSpriteFrames(resourceLocation, this.mipmapLevels + 1);
               textureAtlasSprite.generateMipmaps(this.mipmapLevels);
               this.spriteNormal = textureAtlasSprite;
            } catch (IOException ioException) {
               Config.warn("Error loading normal texture: " + string);
               Config.warn(ioException.getClass().getName() + ": " + ioException.getMessage());
            }
         }
      }

      if (Shaders.configSpecularMap) {
         String string2 = this.iconName + "_s";
         ResourceLocation resourceLocation2 = new ResourceLocation(string2);
         resourceLocation2 = Config.getTextureMap().completeResourceLocation(resourceLocation2, 0);
         if (Config.hasResource(resourceLocation2)) {
            try {
               TextureAtlasSprite textureAtlasSprite2 = new TextureAtlasSprite(string2);
               textureAtlasSprite2.isShadersSprite = true;
               textureAtlasSprite2.copyFrom(this);
               textureAtlasSprite2.loadShaderSpriteFrames(resourceLocation2, this.mipmapLevels + 1);
               textureAtlasSprite2.generateMipmaps(this.mipmapLevels);
               this.spriteSpecular = textureAtlasSprite2;
            } catch (IOException ioException2) {
               Config.warn("Error loading specular texture: " + string2);
               Config.warn(ioException2.getClass().getName() + ": " + ioException2.getMessage());
            }
         }
      }
   }

   public void loadShaderSpriteFrames(ResourceLocation resourceLocation, int i) throws IOException {
      IResource iResource = Config.getResource(resourceLocation);
      BufferedImage bufferedImage = TextureUtil.readBufferedImage(iResource.getInputStream());
      if (this.width != bufferedImage.getWidth()) {
         bufferedImage = TextureUtils.scaleImage(bufferedImage, this.width);
      }

      AnimationMetadataSection animationMetadataSection = iResource.getMetadata("animation");
      int[][] intss = new int[i][];
      intss[0] = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
      bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), intss[0], 0, bufferedImage.getWidth());
      if (animationMetadataSection == null) {
         this.framesTextureData.add(intss);
      } else {
         int j = bufferedImage.getHeight() / this.width;
         if (animationMetadataSection.getFrameCount() > 0) {
            for (int k : animationMetadataSection.getFrameIndexSet()) {
               if (k >= j) {
                  throw new RuntimeException("invalid frameindex " + k);
               }

               this.allocateFrameTextureData(k);
               this.framesTextureData.set(k, getFrameTextureData(intss, this.width, this.width, k));
            }

            this.animationMetadata = animationMetadataSection;
         } else {
            ArrayList arrayList = Lists.newArrayList();

            for (int l = 0; l < j; l++) {
               this.framesTextureData.add(getFrameTextureData(intss, this.width, this.width, l));
               arrayList.add(new AnimationFrame(l, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(arrayList, this.width, this.height, animationMetadataSection.getFrameTime(), animationMetadataSection.isInterpolate());
         }
      }
   }
}
