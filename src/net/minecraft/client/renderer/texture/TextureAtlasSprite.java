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
   private static final long b = 140622711870331L;

   private TextureAtlasSprite(TextureAtlasSprite var1) {
      this.iconName = var1.iconName;
      this.isSpriteSingle = true;
   }

   public TextureAtlasSprite(String var1) {
      this.iconName = var1;
      if (Config.isMultiTexture()) {
         this.spriteSingle = new TextureAtlasSprite(this);
      }
   }

   public static TextureAtlasSprite makeAtlasSprite(ResourceLocation var0) {
      String var4 = var0.toString();
      return locationNameClock.equals(var4)
         ? new TextureClock(var4)
         : (locationNameCompass.equals(var4) ? new TextureCompass(var4) : new TextureAtlasSprite(var4));
   }

   public static void setLocationNameClock(String var0) {
      locationNameClock = var0;
   }

   public static void setLocationNameCompass(String var0) {
      locationNameCompass = var0;
   }

   public void initSprite(int var1, int var2, int var3, int var4, boolean var5) {
      this.originX = var3;
      this.originY = var4;
      this.rotated = var5;
      float var9 = (float)(0.01F / var1);
      float var10 = (float)(0.01F / var2);
      this.minU = var3 / (float)var1 + var9;
      this.maxU = (var3 + this.width) / (float)var1 - var9;
      this.minV = (float)var4 / var2 + var10;
      this.maxV = (float)(var4 + this.height) / var2 - var10;
      this.baseU = Math.min(this.minU, this.maxU);
      this.baseV = Math.min(this.minV, this.maxV);
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }

      if (this.spriteNormal != null) {
         this.spriteNormal.initSprite(var1, var2, var3, var4, var5);
      }

      if (this.spriteSpecular != null) {
         this.spriteSpecular.initSprite(var1, var2, var3, var4, var5);
      }
   }

   public void copyFrom(TextureAtlasSprite var1) {
      this.originX = var1.originX;
      this.originY = var1.originY;
      this.width = var1.width;
      this.height = var1.height;
      this.rotated = var1.rotated;
      this.minU = var1.minU;
      this.maxU = var1.maxU;
      this.minV = var1.minV;
      this.maxV = var1.maxV;
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }
   }

   public int d() {
      return this.originX;
   }

   public int D() {
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

   public float getInterpolatedU(double var1) {
      float var3 = this.maxU - this.minU;
      return this.minU + var3 * (float)var1 / 16.0F;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getInterpolatedV(double var1) {
      float var3 = this.maxV - this.minV;
      return this.minV + var3 * ((float)var1 / 16.0F);
   }

   public String getIconName() {
      return this.iconName;
   }

   public void updateAnimation() {
      if (this.animationMetadata != null) {
         this.tickCounter++;
         if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
            int var4 = this.animationMetadata.getFrameIndex(this.frameCounter);
            int var5 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % var5;
            this.tickCounter = 0;
            int var6 = this.animationMetadata.getFrameIndex(this.frameCounter);
            boolean var7 = false;
            boolean var8 = this.isSpriteSingle;
            if (var4 != var6 && var6 >= 0 && var6 < this.framesTextureData.size()) {
               TextureUtil.uploadTextureMipmap(this.framesTextureData.get(var6), this.width, this.height, this.originX, this.originY, var7, var8);
            }
         } else if (this.animationMetadata.isInterpolate()) {
            this.updateAnimationInterpolated();
         }
      }
   }

   private void updateAnimationInterpolated() {
      double var4 = 1.0 - (double)this.tickCounter / this.animationMetadata.getFrameTimeSingle(this.frameCounter);
      int var6 = this.animationMetadata.getFrameIndex(this.frameCounter);
      int var7 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
      int var8 = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % var7);
      if (var6 != var8 && var8 >= 0 && var8 < this.framesTextureData.size()) {
         int[][] var9 = this.framesTextureData.get(var6);
         int[][] var10 = this.framesTextureData.get(var8);
         if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != var9.length) {
            this.interpolatedFrameData = new int[var9.length][];
         }

         for (int var11 = 0; var11 < var9.length; var11++) {
            if (this.interpolatedFrameData[var11] == null) {
               this.interpolatedFrameData[var11] = new int[var9[var11].length];
            }

            if (var11 < var10.length && var10[var11].length == var9[var11].length) {
               for (int var12 = 0; var12 < var9[var11].length; var12++) {
                  int var13 = var9[var11][var12];
                  int var14 = var10[var11][var12];
                  int var15 = (int)(((var13 & 0xFF0000) >> 16) * var4 + ((var14 & 0xFF0000) >> 16) * (1.0 - var4));
                  int var16 = (int)(((var13 & 0xFF00) >> 8) * var4 + ((var14 & 0xFF00) >> 8) * (1.0 - var4));
                  int var17 = (int)((var13 & 0xFF) * var4 + (var14 & 0xFF) * (1.0 - var4));
                  this.interpolatedFrameData[var11][var12] = var13 & 0xFF000000 | var15 << 16 | var16 << 8 | var17;
               }
            }
         }

         TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
      }
   }

   public int[][] getFrameTextureData(int var1) {
      return this.framesTextureData.get(var1);
   }

   public int getFrameCount() {
      return this.framesTextureData.size();
   }

   public void N(int var1) {
      this.width = var1;
      if (this.spriteSingle != null) {
         this.spriteSingle.N(this.width);
      }
   }

   public void W(int var1) {
      this.height = var1;
      if (this.spriteSingle != null) {
         this.spriteSingle.W(this.height);
      }
   }

   public void loadSprite(BufferedImage[] var1, AnimationMetadataSection var2) throws IOException {
      this.resetSprite();
      int var6 = var1[0].getWidth();
      int var7 = var1[0].getHeight();
      this.width = var6;
      this.height = var7;
      int[][] var8 = new int[var1.length][];

      for (int var9 = 0; var9 < var1.length; var9++) {
         BufferedImage var10 = var1[var9];
         if (var10 != null) {
            if (var9 > 0 && (var10.getWidth() != var6 >> var9 || var10.getHeight() != var7 >> var9)) {
               throw new RuntimeException(
                  String.format(
                     "Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", var9, var10.getWidth(), var10.getHeight(), var6 >> var9, var7 >> var9
                  )
               );
            }

            var8[var9] = new int[var10.getWidth() * var10.getHeight()];
            var10.getRGB(0, 0, var10.getWidth(), var10.getHeight(), var8[var9], 0, var10.getWidth());
         }
      }

      if (var2 == null) {
         if (var7 != var6) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.framesTextureData.add(var8);
      } else {
         int var14 = var7 / var6;
         int var16 = var6;
         int var11 = var6;
         this.height = this.width;
         if (var2.getFrameCount() > 0) {
            for (int var13 : var2.getFrameIndexSet()) {
               if (var13 >= var14) {
                  throw new RuntimeException("invalid frameindex " + var13);
               }

               this.allocateFrameTextureData(var13);
               this.framesTextureData.set(var13, getFrameTextureData(var8, var16, var11, var13));
            }

            this.animationMetadata = var2;
         } else {
            ArrayList var19 = Lists.newArrayList();

            for (int var21 = 0; var21 < var14; var21++) {
               this.framesTextureData.add(getFrameTextureData(var8, var16, var11, var21));
               var19.add(new AnimationFrame(var21, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(var19, this.width, this.height, var2.getFrameTime(), var2.isInterpolate());
         }
      }

      if (!this.isShadersSprite) {
         if (Config.isShaders()) {
            this.loadShadersSprites();
         }

         for (int var15 = 0; var15 < this.framesTextureData.size(); var15++) {
            int[][] var17 = this.framesTextureData.get(var15);
            if (var17 != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
               for (int var18 = 0; var18 < var17.length; var18++) {
                  int[] var20 = var17[var18];
                  this.fixTransparentColor(var20);
               }
            }
         }

         if (this.spriteSingle != null) {
            this.spriteSingle.loadSprite(var1, var2);
         }
      }
   }

   public void generateMipmaps(int var1) {
      ArrayList var5 = Lists.newArrayList();

      for (int var6 = 0; var6 < this.framesTextureData.size(); var6++) {
         final int[][] var7 = this.framesTextureData.get(var6);
         if (var7 != null) {
            try {
               var5.add(TextureUtil.generateMipmapData(var1, this.width, var7));
            } catch (Throwable var11) {
               CrashReport var9 = CrashReport.makeCrashReport(var11, "Generating mipmaps for frame");
               CrashReportCategory var10 = var9.makeCategory("Frame being iterated");
               var10.addCrashSection("Frame index", var6);
               var10.addCrashSectionCallable("Frame sizes", new Callable<String>() {
                  private static final long a = 44797408702111L;

                  public String call() throws Exception {
                     StringBuilder var4 = new StringBuilder();

                     for (int[] var8 : var7) {
                        if (var4.length() > 0) {
                           var4.append(", ");
                        }

                        var4.append(var8 == null ? "null" : var8.length);
                     }

                     return var4.toString();
                  }
               });
               throw new ReportedException(var9);
            }
         }
      }

      this.setFramesTextureData(var5);
      if (this.spriteSingle != null) {
         this.spriteSingle.generateMipmaps(var1);
      }
   }

   private void allocateFrameTextureData(int var1) {
      if (this.framesTextureData.size() <= var1) {
         for (int var5 = this.framesTextureData.size(); var5 <= var1; var5++) {
            this.framesTextureData.add(null);
         }
      }

      if (this.spriteSingle != null) {
         this.spriteSingle.allocateFrameTextureData(var1);
      }
   }

   private static int[][] getFrameTextureData(int[][] var0, int var1, int var2, int var3) {
      int[][] var7 = new int[var0.length][];

      for (int var8 = 0; var8 < var0.length; var8++) {
         int[] var9 = var0[var8];
         if (var9 != null) {
            var7[var8] = new int[(var1 >> var8) * (var2 >> var8)];
            System.arraycopy(var9, var3 * var7[var8].length, var7[var8], 0, var7[var8].length);
         }
      }

      return var7;
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

   public void setFramesTextureData(List<int[][]> var1) {
      this.framesTextureData = var1;
      if (this.spriteSingle != null) {
         this.spriteSingle.setFramesTextureData(var1);
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

   public boolean hasCustomLoader(IResourceManager var1, ResourceLocation var2) {
      return false;
   }

   public boolean L(IResourceManager var1, ResourceLocation var2) {
      return true;
   }

   public int getIndexInMap() {
      return this.indexInMap;
   }

   public void P(int var1) {
      this.indexInMap = var1;
   }

   private void fixTransparentColor(int[] var1) {
      if (var1 != null) {
         long var5 = 0L;
         long var7 = 0L;
         long var9 = 0L;
         long var11 = 0L;

         for (int var13 = 0; var13 < var1.length; var13++) {
            int var14 = var1[var13];
            int var15 = var14 >> 24 & 0xFF;
            if (var15 >= 16) {
               int var16 = var14 >> 16 & 0xFF;
               int var17 = var14 >> 8 & 0xFF;
               int var18 = var14 & 0xFF;
               var5 += var16;
               var7 += var17;
               var9 += var18;
               var11++;
            }
         }

         if (var11 > 0L) {
            int var20 = (int)(var5 / var11);
            int var21 = (int)(var7 / var11);
            int var22 = (int)(var9 / var11);
            int var23 = var20 << 16 | var21 << 8 | var22;

            for (int var24 = 0; var24 < var1.length; var24++) {
               int var25 = var1[var24];
               int var19 = var25 >> 24 & 0xFF;
               if (var19 <= 16) {
                  var1[var24] = var23;
               }
            }
         }
      }
   }

   public double getSpriteU16(float var1) {
      float var2 = this.maxU - this.minU;
      return (var1 - this.minU) / var2 * 16.0F;
   }

   public double getSpriteV16(float var1) {
      float var2 = this.maxV - this.minV;
      return (var1 - this.minV) / var2 * 16.0F;
   }

   public void bindSpriteTexture() {
      if (this.glSpriteTextureId < 0) {
         this.glSpriteTextureId = TextureUtil.glGenTextures();
         TextureUtil.allocateTextureImpl(this.glSpriteTextureId, this.mipmapLevels, this.width, this.height);
         TextureUtils.applyAnisotropicLevel();
      }

      TextureUtils.bindTexture(this.glSpriteTextureId);
   }

   public void e() {
      if (this.glSpriteTextureId >= 0) {
         TextureUtil.deleteTexture(this.glSpriteTextureId);
         this.glSpriteTextureId = -1;
      }
   }

   public float toSingleU(float var1) {
      var1 -= this.baseU;
      float var2 = (float)this.sheetWidth / this.width;
      return var1 * var2;
   }

   public float toSingleV(float var1) {
      var1 -= this.baseV;
      float var2 = (float)this.sheetHeight / this.height;
      return var1 * var2;
   }

   public List<int[][]> getFramesTextureData() {
      ArrayList var1 = new ArrayList();
      var1.addAll(this.framesTextureData);
      return var1;
   }

   public AnimationMetadataSection getAnimationMetadata() {
      return this.animationMetadata;
   }

   public void setAnimationMetadata(AnimationMetadataSection var1) {
      this.animationMetadata = var1;
   }

   private void loadShadersSprites() {
      this.mipmapLevels = Config.getTextureMap().getMipmapLevels();
      if (Shaders.configNormalMap) {
         String var4 = this.iconName + "_n";
         ResourceLocation var5 = new ResourceLocation(var4);
         var5 = Config.getTextureMap().completeResourceLocation(var5, 0);
         if (Config.hasResource(var5)) {
            try {
               TextureAtlasSprite var6 = new TextureAtlasSprite(var4);
               var6.isShadersSprite = true;
               var6.copyFrom(this);
               var6.loadShaderSpriteFrames(var5, this.mipmapLevels + 1);
               var6.generateMipmaps(this.mipmapLevels);
               this.spriteNormal = var6;
            } catch (IOException var8) {
               Config.warn("Error loading normal texture: " + var4);
               Config.warn(var8.getClass().getName() + ": " + var8.getMessage());
            }
         }
      }

      if (Shaders.configSpecularMap) {
         String var9 = this.iconName + "_s";
         ResourceLocation var11 = new ResourceLocation(var9);
         var11 = Config.getTextureMap().completeResourceLocation(var11, 0);
         if (Config.hasResource(var11)) {
            try {
               TextureAtlasSprite var13 = new TextureAtlasSprite(var9);
               var13.isShadersSprite = true;
               var13.copyFrom(this);
               var13.loadShaderSpriteFrames(var11, this.mipmapLevels + 1);
               var13.generateMipmaps(this.mipmapLevels);
               this.spriteSpecular = var13;
            } catch (IOException var7) {
               Config.warn("Error loading specular texture: " + var9);
               Config.warn(var7.getClass().getName() + ": " + var7.getMessage());
            }
         }
      }
   }

   public void loadShaderSpriteFrames(ResourceLocation var1, int var2) throws IOException {
      IResource var6 = Config.getResource(var1);
      BufferedImage var7 = TextureUtil.readBufferedImage(var6.getInputStream());
      if (this.width != var7.getWidth()) {
         var7 = TextureUtils.scaleImage(var7, this.width);
      }

      AnimationMetadataSection var8 = var6.getMetadata("animation");
      int[][] var9 = new int[var2][];
      var9[0] = new int[var7.getWidth() * var7.getHeight()];
      var7.getRGB(0, 0, var7.getWidth(), var7.getHeight(), var9[0], 0, var7.getWidth());
      if (var8 == null) {
         this.framesTextureData.add(var9);
      } else {
         int var10 = var7.getHeight() / this.width;
         if (var8.getFrameCount() > 0) {
            for (int var12 : var8.getFrameIndexSet()) {
               if (var12 >= var10) {
                  throw new RuntimeException("invalid frameindex " + var12);
               }

               this.allocateFrameTextureData(var12);
               this.framesTextureData.set(var12, getFrameTextureData(var9, this.width, this.width, var12));
            }

            this.animationMetadata = var8;
         } else {
            ArrayList var13 = Lists.newArrayList();

            for (int var14 = 0; var14 < var10; var14++) {
               this.framesTextureData.add(getFrameTextureData(var9, this.width, this.width, var14));
               var13.add(new AnimationFrame(var14, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(var13, this.width, this.height, var8.getFrameTime(), var8.isInterpolate());
         }
      }
   }
}
