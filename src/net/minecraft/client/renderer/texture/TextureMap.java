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

   public TextureMap(String string) {
      this(string, (IIconCreator)null);
   }

   public TextureMap(String string, boolean bl) {
      this(string, (IIconCreator)null, bl);
   }

   public TextureMap(String string, IIconCreator iconCreatorIn) {
      this(string, iconCreatorIn, false);
   }

   public TextureMap(String string, IIconCreator iIconCreator, boolean bl) {
      this.listAnimatedSprites = Lists.newArrayList();
      this.mapRegisteredSprites = Maps.newHashMap();
      this.mapUploadedSprites = Maps.newHashMap();
      this.missingImage = new TextureAtlasSprite("missingno");
      this.basePath = string;
      this.iconCreator = iIconCreator;
      this.skipFirst = bl && ENABLE_SKIP;
   }

   private void initMissingImage() {
      int i = this.getMinSpriteSize();
      int[] ints = this.getMissingImageData(i);
      this.missingImage.N(i);
      this.missingImage.W(i);
      int[][] intss = new int[this.mipmapLevels + 1][];
      intss[0] = ints;
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{intss}));
      this.missingImage.P(this.u++);
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      ShadersTex.resManager = resourceManager;
      if (this.iconCreator != null) {
         this.loadSprites(resourceManager, this.iconCreator);
      }
   }

   public void loadSprites(IResourceManager resourceManager, IIconCreator iIconCreator) {
      this.mapRegisteredSprites.clear();
      this.u = 0;
      iIconCreator.registerSprites(this);
      if (this.mipmapLevels >= 4) {
         this.mipmapLevels = this.detectMaxMipmapLevel(this.mapRegisteredSprites, resourceManager);
         Config.log("Mipmap levels: " + this.mipmapLevels);
      }

      this.initMissingImage();
      this.deleteGlTexture();
      this.loadTextureAtlas(resourceManager);
   }

   public void loadTextureAtlas(IResourceManager resourceManager) {
      long l = a ^ 85209644597267L;
      Config.dbg("Multitexture: " + Config.isMultiTexture());
      if (Config.isMultiTexture()) {
         for (Object object : this.mapUploadedSprites.values()) {
            ((TextureAtlasSprite)object).e();
         }
      }

      ConnectedTextures.updateIcons(this);
      CustomItems.updateIcons(this);
      int i = Minecraft.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(i, i, true, 0, this.mipmapLevels);
      long m = 6462237240004000619L ^ l;
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      int j = Integer.MAX_VALUE;
      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this);
      int k = this.getMinSpriteSize();
      int n = 1 << this.mipmapLevels;

      for (Object object2 : this.mapRegisteredSprites.entrySet()) {
         TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)((Entry)object2).getValue();
         ResourceLocation resourceLocation = new ResourceLocation(textureAtlasSprite.getIconName());
         ResourceLocation resourceLocation2 = this.completeResourceLocation(resourceLocation, 0);
         if (!textureAtlasSprite.hasCustomLoader(resourceManager, resourceLocation)) {
            try {
               IResource iResource = resourceManager.getResource(resourceLocation2);
               BufferedImage[] bufferedImages = new BufferedImage[1 + this.mipmapLevels];
               bufferedImages[0] = TextureUtil.readBufferedImage(iResource.getInputStream());
               if (this.mipmapLevels > 0 && bufferedImages != null) {
                  int i2 = bufferedImages[0].getWidth();
                  bufferedImages[0] = TextureUtils.scaleToPowerOfTwo(bufferedImages[0], k);
                  int i3 = bufferedImages[0].getWidth();
                  if (!TextureUtils.isPowerOfTwo(i2)) {
                     Config.log("Scaled non power of 2: " + textureAtlasSprite.getIconName() + ", " + i2 + " -> " + i3);
                  }
               }

               TextureMetadataSection textureMetadataSection = iResource.getMetadata("texture");
               if (textureMetadataSection != null) {
                  List list = textureMetadataSection.getListMipmaps();
                  if (!list.isEmpty()) {
                     int i4 = bufferedImages[0].getWidth();
                     int i5 = bufferedImages[0].getHeight();
                     if (MathHelper.roundUpToPowerOfTwo(i4) != i4 || MathHelper.roundUpToPowerOfTwo(i5) != i5) {
                        throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                     }
                  }

                  for (Object object3 : list) {
                     int i6 = (int)object3;
                     if (i6 > 0 && i6 < bufferedImages.length - 1 && bufferedImages[i6] == null) {
                        ResourceLocation resourceLocation3 = this.completeResourceLocation(resourceLocation, i6);

                        try {
                           bufferedImages[i6] = TextureUtil.readBufferedImage(resourceManager.getResource(resourceLocation3).getInputStream());
                        } catch (IOException ioException) {
                           logger.error("Unable to load miplevel {} from: {}", i6, resourceLocation3, ioException);
                        }
                     }
                  }
               }

               AnimationMetadataSection animationMetadataSection = iResource.getMetadata("animation");
               textureAtlasSprite.loadSprite(bufferedImages, animationMetadataSection);
            } catch (RuntimeException runtimeException) {
               logger.error("Unable to parse metadata from " + resourceLocation2, runtimeException);
               continue;
            } catch (IOException ioException2) {
               logger.error("Using missing texture, unable to load " + resourceLocation2 + ", " + ioException2.getClass().getName());
               continue;
            }

            j = Math.min(j, Math.min(textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight()));
            int i7 = Math.min(Integer.lowestOneBit(textureAtlasSprite.getIconWidth()), Integer.lowestOneBit(textureAtlasSprite.getIconHeight()));
            if (i7 < n) {
               logger.warn(
                  "Texture {} with size {}x{} limits mip level from {} to {}",
                  resourceLocation2,
                  textureAtlasSprite.getIconWidth(),
                  textureAtlasSprite.getIconHeight(),
                  MathHelper.calculateLogBaseTwo(n),
                  MathHelper.calculateLogBaseTwo(i7)
               );
               n = i7;
            }

            stitcher.addSprite(textureAtlasSprite);
         } else if (!textureAtlasSprite.L(resourceManager, resourceLocation)) {
            j = Math.min(j, Math.min(textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight()));
            stitcher.addSprite(textureAtlasSprite);
         }
      }

      int i8 = Math.min(j, n);
      int i9 = MathHelper.calculateLogBaseTwo(i8);
      if (i9 < 0) {
         i9 = 0;
      }

      if (i9 < this.mipmapLevels) {
         logger.info("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, i9, i8);
         this.mipmapLevels = i9;
      }

      for (Object object4 : this.mapRegisteredSprites.values()) {
         final TextureAtlasSprite textureAtlasSprite2 = (TextureAtlasSprite)object4;

         try {
            textureAtlasSprite2.generateMipmaps(this.mipmapLevels);
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Sprite being mipmapped");
            crashReportCategory.addCrashSectionCallable("Sprite name", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite2.getIconName();
               }
            });
            crashReportCategory.addCrashSectionCallable("Sprite size", new Callable<String>() {
               private static final long a = 88460750642069L;

               public String call() throws Exception {
                  return textureAtlasSprite2.getIconWidth() + " x " + textureAtlasSprite2.getIconHeight();
               }
            });
            crashReportCategory.addCrashSectionCallable("Sprite frames", new Callable<String>() {
               private static final long a = 49279306030653L;

               public String call() throws Exception {
                  return textureAtlasSprite2.getFrameCount() + " frames";
               }
            });
            crashReportCategory.addCrashSection("Mipmap levels", this.mipmapLevels);
            throw new ReportedException(crashReport);
         }
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);
      stitcher.doStitch();
      logger.info("Created: {}x{} {}-atlas", stitcher.f(), stitcher.Y(), this.basePath);
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.f(), stitcher.Y());
      HashMap hashMap = Maps.newHashMap(this.mapRegisteredSprites);

      for (Object object5 : stitcher.getStichSlots()) {
         TextureAtlasSprite textureAtlasSprite3 = (TextureAtlasSprite)object5;
         String string = textureAtlasSprite3.getIconName();
         hashMap.remove(string);
         this.mapUploadedSprites.put(string, textureAtlasSprite3);

         try {
            TextureUtil.uploadTextureMipmap(textureAtlasSprite3.getFrameTextureData(0), textureAtlasSprite3.getIconWidth(), textureAtlasSprite3.getIconHeight(), textureAtlasSprite3.d(), textureAtlasSprite3.D(), false, false);
         } catch (Throwable throwable2) {
            CrashReport crashReport2 = CrashReport.makeCrashReport(throwable2, "Stitching texture atlas");
            CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Texture being stitched together");
            crashReportCategory2.addCrashSection("Atlas path", this.basePath);
            crashReportCategory2.addCrashSection("Sprite", textureAtlasSprite3);
            throw new ReportedException(crashReport2);
         }

         if (textureAtlasSprite3.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(textureAtlasSprite3);
         }
      }

      for (Object object6 : hashMap.values()) {
         ((TextureAtlasSprite)object6).copyFrom(this.missingImage);
      }

      if (Config.isMultiTexture()) {
         int i10 = stitcher.f();
         int i11 = stitcher.Y();

         for (Object object7 : stitcher.getStichSlots()) {
            TextureAtlasSprite textureAtlasSprite4 = (TextureAtlasSprite)object7;
            textureAtlasSprite4.sheetWidth = i10;
            textureAtlasSprite4.sheetHeight = i11;
            textureAtlasSprite4.mipmapLevels = this.mipmapLevels;
            TextureAtlasSprite textureAtlasSprite5 = textureAtlasSprite4.spriteSingle;
            if (textureAtlasSprite5 != null) {
               textureAtlasSprite5.sheetWidth = i10;
               textureAtlasSprite5.sheetHeight = i11;
               textureAtlasSprite5.mipmapLevels = this.mipmapLevels;
               textureAtlasSprite4.bindSpriteTexture();
               boolean bl = false;
               boolean bl2 = true;
               TextureUtil.uploadTextureMipmap(textureAtlasSprite5.getFrameTextureData(0), textureAtlasSprite5.getIconWidth(), textureAtlasSprite5.getIconHeight(), textureAtlasSprite5.d(), textureAtlasSprite5.D(), bl, bl2);
            }
         }

         Config.getMinecraft().getTextureManager().bindTexture(locationBlocksTexture);
      }

      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
      if (Config.equals(System.getProperty("saveTextureMap"), "true")) {
         TextureUtil.saveGlTexture(this.basePath.replaceAll("/", "_"), this.getGlTextureId(), this.mipmapLevels, stitcher.f(), stitcher.Y());
      }
   }

   public ResourceLocation completeResourceLocation(ResourceLocation location, int i) {
      return this.isAbsoluteLocation(location)
         ? (
            i == 0
               ? new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".png")
               : new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + "mipmap" + i + ".png")
         )
         : (
            i == 0
               ? new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", this.basePath, location.getResourcePath(), ".png"))
               : new ResourceLocation(location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, location.getResourcePath(), i, ".png"))
         );
   }

   public TextureAtlasSprite getAtlasSprite(String iconName) {
      TextureAtlasSprite textureAtlasSprite = this.mapUploadedSprites.get(iconName);
      if (textureAtlasSprite == null) {
         textureAtlasSprite = this.missingImage;
      }

      return textureAtlasSprite;
   }

   public void updateAnimations() {
      if (Config.isShaders()) {
         ShadersTex.updatingTex = this.getMultiTexID();
      }

      boolean bl = false;
      boolean bl2 = false;
      TextureUtil.bindTexture(this.getGlTextureId());

      for (Object object : this.listAnimatedSprites) {
         TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)object;
         if (this.isTerrainAnimationActive(textureAtlasSprite)) {
            textureAtlasSprite.updateAnimation();
            if (textureAtlasSprite.spriteNormal != null) {
               bl = true;
            }

            if (textureAtlasSprite.spriteSpecular != null) {
               bl2 = true;
            }
         }
      }

      if (Config.isMultiTexture()) {
         for (Object object2 : this.listAnimatedSprites) {
            TextureAtlasSprite textureAtlasSprite2 = (TextureAtlasSprite)object2;
            if (this.isTerrainAnimationActive(textureAtlasSprite2)) {
               TextureAtlasSprite textureAtlasSprite3 = textureAtlasSprite2.spriteSingle;
               if (textureAtlasSprite3 != null) {
                  if (textureAtlasSprite2 == TextureUtils.iconClock || textureAtlasSprite2 == TextureUtils.iconCompass) {
                     textureAtlasSprite3.frameCounter = textureAtlasSprite2.frameCounter;
                  }

                  textureAtlasSprite2.bindSpriteTexture();
                  textureAtlasSprite3.updateAnimation();
               }
            }
         }

         TextureUtil.bindTexture(this.getGlTextureId());
      }

      if (Config.isShaders()) {
         if (bl) {
            TextureUtil.bindTexture(this.getMultiTexID().norm);

            for (Object object3 : this.listAnimatedSprites) {
               TextureAtlasSprite textureAtlasSprite4 = (TextureAtlasSprite)object3;
               if (textureAtlasSprite4.spriteNormal != null && this.isTerrainAnimationActive(textureAtlasSprite4)) {
                  if (textureAtlasSprite4 == TextureUtils.iconClock || textureAtlasSprite4 == TextureUtils.iconCompass) {
                     textureAtlasSprite4.spriteNormal.frameCounter = textureAtlasSprite4.frameCounter;
                  }

                  textureAtlasSprite4.spriteNormal.updateAnimation();
               }
            }
         }

         if (bl2) {
            TextureUtil.bindTexture(this.getMultiTexID().spec);

            for (Object object4 : this.listAnimatedSprites) {
               TextureAtlasSprite textureAtlasSprite5 = (TextureAtlasSprite)object4;
               if (textureAtlasSprite5.spriteSpecular != null && this.isTerrainAnimationActive(textureAtlasSprite5)) {
                  if (textureAtlasSprite5 == TextureUtils.iconClock || textureAtlasSprite5 == TextureUtils.iconCompass) {
                     textureAtlasSprite5.spriteNormal.frameCounter = textureAtlasSprite5.frameCounter;
                  }

                  textureAtlasSprite5.spriteSpecular.updateAnimation();
               }
            }
         }

         if (bl || bl2) {
            TextureUtil.bindTexture(this.getGlTextureId());
         }
      }

      if (Config.isShaders()) {
         ShadersTex.updatingTex = null;
      }
   }

   public TextureAtlasSprite registerSprite(ResourceLocation location) {
      if (location == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      }

      TextureAtlasSprite textureAtlasSprite = this.mapRegisteredSprites.get(location.toString());
      if (textureAtlasSprite == null) {
         textureAtlasSprite = TextureAtlasSprite.makeAtlasSprite(location);
         this.mapRegisteredSprites.put(location.toString(), textureAtlasSprite);
         if (textureAtlasSprite.getIndexInMap() < 0) {
            textureAtlasSprite.P(this.u++);
         }
      }

      return textureAtlasSprite;
   }

   @Override
   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int mipmapLevelsIn) {
      this.mipmapLevels = mipmapLevelsIn;
   }

   public TextureAtlasSprite getMissingSprite() {
      return this.missingImage;
   }

   public TextureAtlasSprite c(String string) {
      ResourceLocation resourceLocation = new ResourceLocation(string);
      return this.mapRegisteredSprites.get(resourceLocation.toString());
   }

   public boolean setTextureEntry(String string, TextureAtlasSprite textureAtlasSprite) {
      if (!this.mapRegisteredSprites.containsKey(string)) {
         this.mapRegisteredSprites.put(string, textureAtlasSprite);
         if (textureAtlasSprite.getIndexInMap() < 0) {
            textureAtlasSprite.P(this.u++);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean t(TextureAtlasSprite textureAtlasSprite) {
      return this.setTextureEntry(textureAtlasSprite.getIconName(), textureAtlasSprite);
   }

   public String getBasePath() {
      return this.basePath;
   }

   public int getMipmapLevels() {
      return this.mipmapLevels;
   }

   private boolean isAbsoluteLocation(ResourceLocation resourceLocation) {
      String string = resourceLocation.getResourcePath();
      return this.isAbsoluteLocationPath(string);
   }

   private boolean isAbsoluteLocationPath(String string) {
      String string2 = string.toLowerCase();
      return string2.startsWith("mcpatcher/") || string2.startsWith("optifine/");
   }

   public TextureAtlasSprite getSpriteSafe(String string) {
      ResourceLocation resourceLocation = new ResourceLocation(string);
      return this.mapRegisteredSprites.get(resourceLocation.toString());
   }

   private boolean isTerrainAnimationActive(TextureAtlasSprite textureAtlasSprite) {
      return textureAtlasSprite == TextureUtils.iconWaterStill || textureAtlasSprite == TextureUtils.iconWaterFlow
         ? Config.W_boolean()
         : (
            textureAtlasSprite == TextureUtils.iconLavaStill || textureAtlasSprite == TextureUtils.iconLavaFlow
               ? Config.pl()
               : (
                  textureAtlasSprite == TextureUtils.iconFireLayer0 || textureAtlasSprite == TextureUtils.iconFireLayer1
                     ? Config.w()
                     : (
                        textureAtlasSprite == TextureUtils.iconPortal
                           ? Config.isAnimatedPortal()
                           : (textureAtlasSprite != TextureUtils.iconClock && textureAtlasSprite != TextureUtils.iconCompass ? Config.F() : true)
                     )
               )
         );
   }

   public int getCountRegisteredSprites() {
      return this.u;
   }

   private int detectMaxMipmapLevel(Map map, IResourceManager iResourceManager) {
      int i = this.detectMinimumSpriteSize(map, iResourceManager, 20);
      if (i < 16) {
         i = 16;
      }

      i = MathHelper.roundUpToPowerOfTwo(i);
      if (i > 16) {
         Config.log("Sprite size: " + i);
      }

      int j = MathHelper.calculateLogBaseTwo(i);
      if (j < 4) {
         j = 4;
      }

      return j;
   }

   private int detectMinimumSpriteSize(Map map, IResourceManager iResourceManager, int i) {
      HashMap hashMap = new HashMap();

      for (Object object : map.entrySet()) {
         TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)((Entry)object).getValue();
         ResourceLocation resourceLocation = new ResourceLocation(textureAtlasSprite.getIconName());
         ResourceLocation resourceLocation2 = this.completeResourceLocation(resourceLocation, 0);
         if (!textureAtlasSprite.hasCustomLoader(iResourceManager, resourceLocation)) {
            try {
               IResource iResource = iResourceManager.getResource(resourceLocation2);
               if (iResource != null) {
                  InputStream inputStream = iResource.getInputStream();
                  if (inputStream != null) {
                     Dimension dimension = TextureUtils.getImageSize(inputStream, "png");
                     if (dimension != null) {
                        int j = dimension.width;
                        int k = MathHelper.roundUpToPowerOfTwo(j);
                        if (!hashMap.containsKey(k)) {
                           hashMap.put(k, 1);
                        } else {
                           int l = (Integer)hashMap.get(k);
                           hashMap.put(k, l + 1);
                        }
                     }
                  }
               }
            } catch (Exception exception) {
            }
         }
      }

      int m = 0;
      Set set = hashMap.keySet();
      TreeSet treeSet = new TreeSet(set);

      for (Object object2 : treeSet) {
         int n = (int)object2;
         int i2 = (Integer)hashMap.get(n);
         m += i2;
      }

      int i3 = 16;
      int i4 = 0;
      int i5 = m * i / 100;

      for (Object object3 : treeSet) {
         int i6 = (int)object3;
         int i7 = (Integer)hashMap.get(i6);
         i4 += i7;
         if (i6 > i3) {
            i3 = i6;
         }

         if (i4 > i5) {
            return i3;
         }
      }

      return i3;
   }

   private int getMinSpriteSize() {
      int i = 1 << this.mipmapLevels;
      if (i < 8) {
         i = 8;
      }

      return i;
   }

   private int[] getMissingImageData(int i) {
      BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
      bufferedImage.setRGB(0, 0, 16, 16, TextureUtil.missingTextureData, 0, 16);
      BufferedImage bufferedImage2 = TextureUtils.scaleToPowerOfTwo(bufferedImage, i);
      int[] ints = new int[i * i];
      bufferedImage2.getRGB(0, 0, i, i, ints, 0, i);
      return ints;
   }

   public boolean isTextureBound() {
      int i = GlStateManager.getBoundTexture();
      int j = this.getGlTextureId();
      return i == j;
   }

   public TextureAtlasSprite getIconByUV(double d, double d2) {
      if (this.iconGrid == null) {
         return null;
      }

      int i = (int)(d / this.iconGridSizeU);
      int j = (int)(d2 / this.iconGridSizeV);
      int k = j * this.iconGridCountX + i;
      return k >= 0 && k <= this.iconGrid.length ? this.iconGrid[k] : null;
   }
}
