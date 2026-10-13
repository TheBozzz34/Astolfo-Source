package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedTextures;
import net.minecraft.src.CustomItems;
import net.minecraft.src.Reflector;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private static final boolean ENABLE_SKIP = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
   private static final Logger logger = LogManager.getLogger();
   public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
   public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
   private final List<TextureAtlasSprite> listAnimatedSprites;
   private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
   private final Map<String, TextureAtlasSprite> mapUploadedSprites;
   private final String basePath;
   private final IIconCreator iconCreator;
   private int mipmapLevels;
   private final TextureAtlasSprite missingImage;
   private boolean skipFirst = false;
   private TextureAtlasSprite[] iconGrid = null;
   private int T = -1;
   private int iconGridCountX = -1;
   private int y = -1;
   private double iconGridSizeU = -1.0;
   private double iconGridSizeV = -1.0;
   private int u = 0;
   public int atlasWidth = 0;
   public int atlasHeight = 0;
   private static final long a = 27945421490205L;

   public TextureMap(String var1) {
      this(var1, (IIconCreator)null);
   }

   public TextureMap(String var1, boolean var2) {
      this(var1, (IIconCreator)null, var2);
   }

   public TextureMap(String var1, IIconCreator var2) {
      this(var1, var2, false);
   }

   public TextureMap(String var1, IIconCreator var2, boolean var3) {
      this.listAnimatedSprites = Lists.newArrayList();
      this.mapRegisteredSprites = Maps.newHashMap();
      this.mapUploadedSprites = Maps.newHashMap();
      this.missingImage = new TextureAtlasSprite("missingno");
      this.basePath = var1;
      this.iconCreator = var2;
      this.skipFirst = var3 && ENABLE_SKIP;
   }

   private void initMissingImage() {
      int var1 = this.getMinSpriteSize();
      int[] var2 = this.getMissingImageData(var1);
      this.missingImage.N(var1);
      this.missingImage.W(var1);
      int[][] var3 = new int[this.mipmapLevels + 1][];
      var3[0] = var2;
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{var3}));
      this.missingImage.P(this.u++);
   }

   @Override
   public void loadTexture(IResourceManager var1) throws IOException {
      ShadersTex.resManager = var1;
      if (this.iconCreator != null) {
         this.loadSprites(var1, this.iconCreator);
      }
   }

   public void loadSprites(IResourceManager var1, IIconCreator var2) {
      this.mapRegisteredSprites.clear();
      this.u = 0;
      var2.registerSprites(this);
      if (this.mipmapLevels >= 4) {
         this.mipmapLevels = this.detectMaxMipmapLevel(this.mapRegisteredSprites, var1);
         Config.log("Mipmap levels: " + this.mipmapLevels);
      }

      this.initMissingImage();
      this.deleteGlTexture();
      this.loadTextureAtlas(var1);
   }

   public void loadTextureAtlas(IResourceManager var1) {
      long var2 = a ^ 85209644597267L;
      Config.dbg("Multitexture: " + Config.isMultiTexture());
      if (Config.isMultiTexture()) {
         for (Object var7 : this.mapUploadedSprites.values()) {
            ((TextureAtlasSprite)var7).e();
         }
      }

      ConnectedTextures.updateIcons(this);
      CustomItems.updateIcons(this);
      int var29 = Minecraft.getGLMaximumTextureSize();
      Stitcher var30 = new Stitcher(var29, var29, true, 0, this.mipmapLevels);
      long var10000 = 6462237240004000619L ^ var2;
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      int var8 = Integer.MAX_VALUE;
      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this);
      int var9 = this.getMinSpriteSize();
      int var10 = 1 << this.mipmapLevels;

      for (Object var12 : this.mapRegisteredSprites.entrySet()) {
         TextureAtlasSprite var13 = (TextureAtlasSprite)((Entry)var12).getValue();
         ResourceLocation var14 = new ResourceLocation(var13.getIconName());
         ResourceLocation var15 = this.completeResourceLocation(var14, 0);
         if (!var13.hasCustomLoader(var1, var14)) {
            try {
               IResource var16 = var1.getResource(var15);
               BufferedImage[] var17 = new BufferedImage[1 + this.mipmapLevels];
               var17[0] = TextureUtil.readBufferedImage(var16.getInputStream());
               if (this.mipmapLevels > 0 && var17 != null) {
                  int var18 = var17[0].getWidth();
                  var17[0] = TextureUtils.scaleToPowerOfTwo(var17[0], var9);
                  int var19 = var17[0].getWidth();
                  if (!TextureUtils.isPowerOfTwo(var18)) {
                     Config.log("Scaled non power of 2: " + var13.getIconName() + ", " + var18 + " -> " + var19);
                  }
               }

               TextureMetadataSection var49 = var16.getMetadata("texture");
               if (var49 != null) {
                  List var52 = var49.getListMipmaps();
                  if (!var52.isEmpty()) {
                     int var20 = var17[0].getWidth();
                     int var21 = var17[0].getHeight();
                     if (MathHelper.roundUpToPowerOfTwo(var20) != var20 || MathHelper.roundUpToPowerOfTwo(var21) != var21) {
                        throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                     }
                  }

                  for (Object var59$raw : var52) {
                     int var59 = (int)var59$raw;
                     if (var59 > 0 && var59 < var17.length - 1 && var17[var59] == null) {
                        ResourceLocation var22 = this.completeResourceLocation(var14, var59);

                        try {
                           var17[var59] = TextureUtil.readBufferedImage(var1.getResource(var22).getInputStream());
                        } catch (IOException var26) {
                           logger.error("Unable to load miplevel {} from: {}", var59, var22, var26);
                        }
                     }
                  }
               }

               AnimationMetadataSection var53 = var16.getMetadata("animation");
               var13.loadSprite(var17, var53);
            } catch (RuntimeException var27) {
               logger.error("Unable to parse metadata from " + var15, var27);
               continue;
            } catch (IOException var28) {
               logger.error("Using missing texture, unable to load " + var15 + ", " + var28.getClass().getName());
               continue;
            }

            var8 = Math.min(var8, Math.min(var13.getIconWidth(), var13.getIconHeight()));
            int var43 = Math.min(Integer.lowestOneBit(var13.getIconWidth()), Integer.lowestOneBit(var13.getIconHeight()));
            if (var43 < var10) {
               logger.warn(
                  "Texture {} with size {}x{} limits mip level from {} to {}",
                  var15,
                  var13.getIconWidth(),
                  var13.getIconHeight(),
                  MathHelper.calculateLogBaseTwo(var10),
                  MathHelper.calculateLogBaseTwo(var43)
               );
               var10 = var43;
            }

            var30.addSprite(var13);
         } else if (!var13.L(var1, var14)) {
            var8 = Math.min(var8, Math.min(var13.getIconWidth(), var13.getIconHeight()));
            var30.addSprite(var13);
         }
      }

      int var31 = Math.min(var8, var10);
      int var32 = MathHelper.calculateLogBaseTwo(var31);
      if (var32 < 0) {
         var32 = 0;
      }

      if (var32 < this.mipmapLevels) {
         logger.info("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, var32, var31);
         this.mipmapLevels = var32;
      }

      for (Object var35 : this.mapRegisteredSprites.values()) {
         final TextureAtlasSprite var39 = (TextureAtlasSprite)var35;

         try {
            var39.generateMipmaps(this.mipmapLevels);
         } catch (Throwable var25) {
            CrashReport var46 = CrashReport.makeCrashReport(var25, "Applying mipmap");
            CrashReportCategory var50 = var46.makeCategory("Sprite being mipmapped");
            var50.addCrashSectionCallable("Sprite name", new Callable<String>() {
               public String call() throws Exception {
                  return var39.getIconName();
               }
            });
            var50.addCrashSectionCallable("Sprite size", new Callable<String>() {
               private static final long a = 88460750642069L;

               public String call() throws Exception {
                  return var39.getIconWidth() + " x " + var39.getIconHeight();
               }
            });
            var50.addCrashSectionCallable("Sprite frames", new Callable<String>() {
               private static final long a = 49279306030653L;

               public String call() throws Exception {
                  return var39.getFrameCount() + " frames";
               }
            });
            var50.addCrashSection("Mipmap levels", this.mipmapLevels);
            throw new ReportedException(var46);
         }
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      var30.addSprite(this.missingImage);
      var30.doStitch();
      logger.info("Created: {}x{} {}-atlas", var30.f(), var30.Y(), this.basePath);
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, var30.f(), var30.Y());
      HashMap var34 = Maps.newHashMap(this.mapRegisteredSprites);

      for (Object var40 : var30.getStichSlots()) {
         TextureAtlasSprite var44 = (TextureAtlasSprite)var40;
         String var47 = var44.getIconName();
         var34.remove(var47);
         this.mapUploadedSprites.put(var47, var44);

         try {
            TextureUtil.uploadTextureMipmap(var44.getFrameTextureData(0), var44.getIconWidth(), var44.getIconHeight(), var44.d(), var44.D(), false, false);
         } catch (Throwable var24) {
            CrashReport var54 = CrashReport.makeCrashReport(var24, "Stitching texture atlas");
            CrashReportCategory var57 = var54.makeCategory("Texture being stitched together");
            var57.addCrashSection("Atlas path", this.basePath);
            var57.addCrashSection("Sprite", var44);
            throw new ReportedException(var54);
         }

         if (var44.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(var44);
         }
      }

      for (Object var41 : var34.values()) {
         ((TextureAtlasSprite)var41).copyFrom(this.missingImage);
      }

      if (Config.isMultiTexture()) {
         int var38 = var30.f();
         int var42 = var30.Y();

         for (Object var48 : var30.getStichSlots()) {
            TextureAtlasSprite var51 = (TextureAtlasSprite)var48;
            var51.sheetWidth = var38;
            var51.sheetHeight = var42;
            var51.mipmapLevels = this.mipmapLevels;
            TextureAtlasSprite var55 = var51.spriteSingle;
            if (var55 != null) {
               var55.sheetWidth = var38;
               var55.sheetHeight = var42;
               var55.mipmapLevels = this.mipmapLevels;
               var51.bindSpriteTexture();
               boolean var58 = false;
               boolean var60 = true;
               TextureUtil.uploadTextureMipmap(var55.getFrameTextureData(0), var55.getIconWidth(), var55.getIconHeight(), var55.d(), var55.D(), var58, var60);
            }
         }

         Config.getMinecraft().getTextureManager().bindTexture(locationBlocksTexture);
      }

      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
      if (Config.equals(System.getProperty("saveTextureMap"), "true")) {
         TextureUtil.saveGlTexture(this.basePath.replaceAll("/", "_"), this.getGlTextureId(), this.mipmapLevels, var30.f(), var30.Y());
      }
   }

   public ResourceLocation completeResourceLocation(ResourceLocation var1, int var2) {
      return this.isAbsoluteLocation(var1)
         ? (
            var2 == 0
               ? new ResourceLocation(var1.getResourceDomain(), var1.getResourcePath() + ".png")
               : new ResourceLocation(var1.getResourceDomain(), var1.getResourcePath() + "mipmap" + var2 + ".png")
         )
         : (
            var2 == 0
               ? new ResourceLocation(var1.getResourceDomain(), String.format("%s/%s%s", this.basePath, var1.getResourcePath(), ".png"))
               : new ResourceLocation(var1.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, var1.getResourcePath(), var2, ".png"))
         );
   }

   public TextureAtlasSprite getAtlasSprite(String var1) {
      TextureAtlasSprite var5 = this.mapUploadedSprites.get(var1);
      if (var5 == null) {
         var5 = this.missingImage;
      }

      return var5;
   }

   public void updateAnimations() {
      if (Config.isShaders()) {
         ShadersTex.updatingTex = this.getMultiTexID();
      }

      boolean var4 = false;
      boolean var5 = false;
      TextureUtil.bindTexture(this.getGlTextureId());

      for (Object var7 : this.listAnimatedSprites) {
         TextureAtlasSprite var8 = (TextureAtlasSprite)var7;
         if (this.isTerrainAnimationActive(var8)) {
            var8.updateAnimation();
            if (var8.spriteNormal != null) {
               var4 = true;
            }

            if (var8.spriteSpecular != null) {
               var5 = true;
            }
         }
      }

      if (Config.isMultiTexture()) {
         for (Object var13 : this.listAnimatedSprites) {
            TextureAtlasSprite var16 = (TextureAtlasSprite)var13;
            if (this.isTerrainAnimationActive(var16)) {
               TextureAtlasSprite var9 = var16.spriteSingle;
               if (var9 != null) {
                  if (var16 == TextureUtils.iconClock || var16 == TextureUtils.iconCompass) {
                     var9.frameCounter = var16.frameCounter;
                  }

                  var16.bindSpriteTexture();
                  var9.updateAnimation();
               }
            }
         }

         TextureUtil.bindTexture(this.getGlTextureId());
      }

      if (Config.isShaders()) {
         if (var4) {
            TextureUtil.bindTexture(this.getMultiTexID().norm);

            for (Object var14 : this.listAnimatedSprites) {
               TextureAtlasSprite var17 = (TextureAtlasSprite)var14;
               if (var17.spriteNormal != null && this.isTerrainAnimationActive(var17)) {
                  if (var17 == TextureUtils.iconClock || var17 == TextureUtils.iconCompass) {
                     var17.spriteNormal.frameCounter = var17.frameCounter;
                  }

                  var17.spriteNormal.updateAnimation();
               }
            }
         }

         if (var5) {
            TextureUtil.bindTexture(this.getMultiTexID().spec);

            for (Object var15 : this.listAnimatedSprites) {
               TextureAtlasSprite var18 = (TextureAtlasSprite)var15;
               if (var18.spriteSpecular != null && this.isTerrainAnimationActive(var18)) {
                  if (var18 == TextureUtils.iconClock || var18 == TextureUtils.iconCompass) {
                     var18.spriteNormal.frameCounter = var18.frameCounter;
                  }

                  var18.spriteSpecular.updateAnimation();
               }
            }
         }

         if (var4 || var5) {
            TextureUtil.bindTexture(this.getGlTextureId());
         }
      }

      if (Config.isShaders()) {
         ShadersTex.updatingTex = null;
      }
   }

   public TextureAtlasSprite registerSprite(ResourceLocation var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      }

      TextureAtlasSprite var5 = this.mapRegisteredSprites.get(var1.toString());
      if (var5 == null) {
         var5 = TextureAtlasSprite.makeAtlasSprite(var1);
         this.mapRegisteredSprites.put(var1.toString(), var5);
         if (var5.getIndexInMap() < 0) {
            var5.P(this.u++);
         }
      }

      return var5;
   }

   @Override
   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int var1) {
      this.mipmapLevels = var1;
   }

   public TextureAtlasSprite getMissingSprite() {
      return this.missingImage;
   }

   public TextureAtlasSprite c(String var1) {
      ResourceLocation var2 = new ResourceLocation(var1);
      return this.mapRegisteredSprites.get(var2.toString());
   }

   public boolean setTextureEntry(String var1, TextureAtlasSprite var2) {
      if (!this.mapRegisteredSprites.containsKey(var1)) {
         this.mapRegisteredSprites.put(var1, var2);
         if (var2.getIndexInMap() < 0) {
            var2.P(this.u++);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean t(TextureAtlasSprite var1) {
      return this.setTextureEntry(var1.getIconName(), var1);
   }

   public String getBasePath() {
      return this.basePath;
   }

   public int getMipmapLevels() {
      return this.mipmapLevels;
   }

   private boolean isAbsoluteLocation(ResourceLocation var1) {
      String var2 = var1.getResourcePath();
      return this.isAbsoluteLocationPath(var2);
   }

   private boolean isAbsoluteLocationPath(String var1) {
      String var5 = var1.toLowerCase();
      return var5.startsWith("mcpatcher/") || var5.startsWith("optifine/");
   }

   public TextureAtlasSprite getSpriteSafe(String var1) {
      ResourceLocation var2 = new ResourceLocation(var1);
      return this.mapRegisteredSprites.get(var2.toString());
   }

   private boolean isTerrainAnimationActive(TextureAtlasSprite var1) {
      return var1 == TextureUtils.iconWaterStill || var1 == TextureUtils.iconWaterFlow
         ? Config.W_boolean()
         : (
            var1 == TextureUtils.iconLavaStill || var1 == TextureUtils.iconLavaFlow
               ? Config.pl()
               : (
                  var1 == TextureUtils.iconFireLayer0 || var1 == TextureUtils.iconFireLayer1
                     ? Config.w()
                     : (
                        var1 == TextureUtils.iconPortal
                           ? Config.isAnimatedPortal()
                           : (var1 != TextureUtils.iconClock && var1 != TextureUtils.iconCompass ? Config.F() : true)
                     )
               )
         );
   }

   public int getCountRegisteredSprites() {
      return this.u;
   }

   private int detectMaxMipmapLevel(Map var1, IResourceManager var2) {
      int var6 = this.detectMinimumSpriteSize(var1, var2, 20);
      if (var6 < 16) {
         var6 = 16;
      }

      var6 = MathHelper.roundUpToPowerOfTwo(var6);
      if (var6 > 16) {
         Config.log("Sprite size: " + var6);
      }

      int var7 = MathHelper.calculateLogBaseTwo(var6);
      if (var7 < 4) {
         var7 = 4;
      }

      return var7;
   }

   private int detectMinimumSpriteSize(Map var1, IResourceManager var2, int var3) {
      HashMap var7 = new HashMap();

      for (Object var9 : var1.entrySet()) {
         TextureAtlasSprite var10 = (TextureAtlasSprite)((Entry)var9).getValue();
         ResourceLocation var11 = new ResourceLocation(var10.getIconName());
         ResourceLocation var12 = this.completeResourceLocation(var11, 0);
         if (!var10.hasCustomLoader(var2, var11)) {
            try {
               IResource var13 = var2.getResource(var12);
               if (var13 != null) {
                  InputStream var14 = var13.getInputStream();
                  if (var14 != null) {
                     Dimension var15 = TextureUtils.getImageSize(var14, "png");
                     if (var15 != null) {
                        int var16 = var15.width;
                        int var17 = MathHelper.roundUpToPowerOfTwo(var16);
                        if (!var7.containsKey(var17)) {
                           var7.put(var17, 1);
                        } else {
                           int var18 = (Integer)var7.get(var17);
                           var7.put(var17, var18 + 1);
                        }
                     }
                  }
               }
            } catch (Exception var19) {
            }
         }
      }

      int var20 = 0;
      Set var21 = var7.keySet();
      TreeSet var22 = new TreeSet(var21);

      for (Object var27$raw : var22) {
         int var27 = (int)var27$raw;
         int var23 = (Integer)var7.get(var27);
         var20 += var23;
      }

      int var26 = 16;
      int var28 = 0;
      int var24 = var20 * var3 / 100;

      for (Object var30$raw : var22) {
         int var30 = (int)var30$raw;
         int var31 = (Integer)var7.get(var30);
         var28 += var31;
         if (var30 > var26) {
            var26 = var30;
         }

         if (var28 > var24) {
            return var26;
         }
      }

      return var26;
   }

   private int getMinSpriteSize() {
      int var4 = 1 << this.mipmapLevels;
      if (var4 < 8) {
         var4 = 8;
      }

      return var4;
   }

   private int[] getMissingImageData(int var1) {
      BufferedImage var4 = new BufferedImage(16, 16, 2);
      var4.setRGB(0, 0, 16, 16, TextureUtil.missingTextureData, 0, 16);
      BufferedImage var5 = TextureUtils.scaleToPowerOfTwo(var4, var1);
      int[] var6 = new int[var1 * var1];
      var5.getRGB(0, 0, var1, var1, var6, 0, var1);
      return var6;
   }

   public boolean isTextureBound() {
      int var4 = GlStateManager.getBoundTexture();
      int var5 = this.getGlTextureId();
      return var4 == var5;
   }

   public TextureAtlasSprite getIconByUV(double var1, double var3) {
      if (this.iconGrid == null) {
         return null;
      }

      int var8 = (int)(var1 / this.iconGridSizeU);
      int var9 = (int)(var3 / this.iconGridSizeV);
      int var10 = var9 * this.iconGridCountX + var8;
      return var10 >= 0 && var10 <= this.iconGrid.length ? this.iconGrid[var10] : null;
   }
}
