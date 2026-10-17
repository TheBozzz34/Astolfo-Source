package net.minecraft.src;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import shadersmod.client.MultiTexID;
import shadersmod.client.Shaders;

public class TextureUtils {
   public static final String texGrassTop = "grass_top";
   public static final String texStone = "stone";
   public static final String texDirt = "dirt";
   public static final String texCoarseDirt = "coarse_dirt";
   public static final String texGrassSide = "grass_side";
   public static final String texStoneslabSide = "stone_slab_side";
   public static final String texStoneslabTop = "stone_slab_top";
   public static final String texBedrock = "bedrock";
   public static final String texSand = "sand";
   public static final String texGravel = "gravel";
   public static final String texLogOak = "log_oak";
   public static final String texLogBigOak = "log_big_oak";
   public static final String texLogAcacia = "log_acacia";
   public static final String texLogSpruce = "log_spruce";
   public static final String texLogBirch = "log_birch";
   public static final String texLogJungle = "log_jungle";
   public static final String texLogOakTop = "log_oak_top";
   public static final String texLogBigOakTop = "log_big_oak_top";
   public static final String texLogAcaciaTop = "log_acacia_top";
   public static final String texLogSpruceTop = "log_spruce_top";
   public static final String texLogBirchTop = "log_birch_top";
   public static final String texLogJungleTop = "log_jungle_top";
   public static final String texLeavesOak = "leaves_oak";
   public static final String texLeavesBigOak = "leaves_big_oak";
   public static final String texLeavesAcacia = "leaves_acacia";
   public static final String texLeavesBirch = "leaves_birch";
   public static final String texLeavesSpuce = "leaves_spruce";
   public static final String texLeavesJungle = "leaves_jungle";
   public static final String texGoldOre = "gold_ore";
   public static final String texIronOre = "iron_ore";
   public static final String texCoalOre = "coal_ore";
   public static final String texObsidian = "obsidian";
   public static final String texGrassSideOverlay = "grass_side_overlay";
   public static final String texSnow = "snow";
   public static final String texGrassSideSnowed = "grass_side_snowed";
   public static final String texMyceliumSide = "mycelium_side";
   public static final String texMyceliumTop = "mycelium_top";
   public static final String texDiamondOre = "diamond_ore";
   public static final String texRedstoneOre = "redstone_ore";
   public static final String texLapisOre = "lapis_ore";
   public static final String texCactusSide = "cactus_side";
   public static final String texClay = "clay";
   public static final String texFarmlandWet = "farmland_wet";
   public static final String texFarmlandDry = "farmland_dry";
   public static final String texNetherrack = "netherrack";
   public static final String texSoulSand = "soul_sand";
   public static final String texGlowstone = "glowstone";
   public static final String texLeavesSpruce = "leaves_spruce";
   public static final String texLeavesSpruceOpaque = "leaves_spruce_opaque";
   public static final String texEndStone = "end_stone";
   public static final String texSandstoneTop = "sandstone_top";
   public static final String texSandstoneBottom = "sandstone_bottom";
   public static final String texRedstoneLampOff = "redstone_lamp_off";
   public static final String texRedstoneLampOn = "redstone_lamp_on";
   public static final String texWaterStill = "water_still";
   public static final String texWaterFlow = "water_flow";
   public static final String texLavaStill = "lava_still";
   public static final String texLavaFlow = "lava_flow";
   public static final String texFireLayer0 = "fire_layer_0";
   public static final String texFireLayer1 = "fire_layer_1";
   public static final String texPortal = "portal";
   public static final String texGlass = "glass";
   public static final String texGlassPaneTop = "glass_pane_top";
   public static final String texCompass = "compass";
   public static final String texClock = "clock";
   public static TextureAtlasSprite iconGrassTop;
   public static TextureAtlasSprite iconGrassSide;
   public static TextureAtlasSprite iconGrassSideOverlay;
   public static TextureAtlasSprite iconSnow;
   public static TextureAtlasSprite iconGrassSideSnowed;
   public static TextureAtlasSprite iconMyceliumSide;
   public static TextureAtlasSprite iconMyceliumTop;
   public static TextureAtlasSprite iconWaterStill;
   public static TextureAtlasSprite iconWaterFlow;
   public static TextureAtlasSprite iconLavaStill;
   public static TextureAtlasSprite iconLavaFlow;
   public static TextureAtlasSprite iconPortal;
   public static TextureAtlasSprite iconFireLayer0;
   public static TextureAtlasSprite iconFireLayer1;
   public static TextureAtlasSprite iconGlass;
   public static TextureAtlasSprite iconGlassPaneTop;
   public static TextureAtlasSprite iconCompass;
   public static TextureAtlasSprite iconClock;
   public static final String SPRITE_PREFIX_BLOCKS = "minecraft:blocks/";
   public static final String SPRITE_PREFIX_ITEMS = "minecraft:items/";
   private static IntBuffer staticBuffer = GLAllocation.createDirectIntBuffer(256);

   public static void update() {
      TextureMap textureMap = getTextureMapBlocks();
      if (textureMap != null) {
         String string = "minecraft:blocks/";
         iconGrassTop = textureMap.getSpriteSafe(string + "grass_top");
         iconGrassSide = textureMap.getSpriteSafe(string + "grass_side");
         iconGrassSideOverlay = textureMap.getSpriteSafe(string + "grass_side_overlay");
         iconSnow = textureMap.getSpriteSafe(string + "snow");
         iconGrassSideSnowed = textureMap.getSpriteSafe(string + "grass_side_snowed");
         iconMyceliumSide = textureMap.getSpriteSafe(string + "mycelium_side");
         iconMyceliumTop = textureMap.getSpriteSafe(string + "mycelium_top");
         iconWaterStill = textureMap.getSpriteSafe(string + "water_still");
         iconWaterFlow = textureMap.getSpriteSafe(string + "water_flow");
         iconLavaStill = textureMap.getSpriteSafe(string + "lava_still");
         iconLavaFlow = textureMap.getSpriteSafe(string + "lava_flow");
         iconFireLayer0 = textureMap.getSpriteSafe(string + "fire_layer_0");
         iconFireLayer1 = textureMap.getSpriteSafe(string + "fire_layer_1");
         iconPortal = textureMap.getSpriteSafe(string + "portal");
         iconGlass = textureMap.getSpriteSafe(string + "glass");
         iconGlassPaneTop = textureMap.getSpriteSafe(string + "glass_pane_top");
         String string2 = "minecraft:items/";
         iconCompass = textureMap.getSpriteSafe(string2 + "compass");
         iconClock = textureMap.getSpriteSafe(string2 + "clock");
      }
   }

   public static BufferedImage fixTextureDimensions(String name, BufferedImage bi) {
      if (name.startsWith("/mob/zombie") || name.startsWith("/mob/pigzombie")) {
         int i = bi.getWidth();
         int j = bi.getHeight();
         if (i == j * 2) {
            BufferedImage bufferedImage = new BufferedImage(i, j * 2, 2);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(bi, 0, 0, i, j, (ImageObserver)null);
            return bufferedImage;
         }
      }

      return bi;
   }

   public static int ceilPowerOfTwo(int val) {
      byte b = 1;

      while (b < val) {
         b *= 2;
      }

      return b;
   }

   public static int getPowerOfTwo(int val) {
      byte b = 1;

      int i;
      for (i = 0; b < val; i++) {
         b *= 2;
      }

      return i;
   }

   public static int twoToPower(int power) {
      byte b = 1;

      for (int i = 0; i < power; i++) {
         b *= 2;
      }

      return b;
   }

   public static ITextureObject getTexture(ResourceLocation loc) {
      ITextureObject iTextureObject = Config.getTextureManager().getTexture(loc);
      if (iTextureObject != null) {
         return iTextureObject;
      }

      if (!Config.hasResource(loc)) {
         return null;
      }

      SimpleTexture simpleTexture = new SimpleTexture(loc);
      Config.getTextureManager().loadTexture(loc, simpleTexture);
      return simpleTexture;
   }

   public static void resourcesReloaded(IResourceManager rm) {
      if (getTextureMapBlocks() != null) {
         Config.dbg("*** Reloading custom textures ***");
         CustomSky.reset();
         TextureAnimations.reset();
         update();
         NaturalTextures.update();
         BetterGrass.update();
         BetterSnow.update();
         TextureAnimations.update();
         CustomColors.update();
         CustomSky.update();
         RandomMobs.resetTextures();
         CustomItems.updateModels();
         Shaders.resourcesReloaded();
         Lang.resourcesReloaded();
         Config.updateTexturePackClouds();
         SmartLeaves.updateLeavesModels();
         Config.getTextureManager().tick();
      }
   }

   public static TextureMap getTextureMapBlocks() {
      return Minecraft.getMinecraft().getTextureMapBlocks();
   }

   public static void registerResourceListener() {
      IResourceManager iResourceManager = Config.getResourceManager();
      if (iResourceManager instanceof IReloadableResourceManager) {
         IReloadableResourceManager iReloadableResourceManager = (IReloadableResourceManager)iResourceManager;
         IResourceManagerReloadListener iResourceManagerReloadListener = new IResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {
               TextureUtils.resourcesReloaded(resourceManager);
            }
         };
         iReloadableResourceManager.registerReloadListener(iResourceManagerReloadListener);
      }

      ITickableTextureObject iTickableTextureObject = new ITickableTextureObject() {
         @Override
         public void tick() {
            TextureAnimations.updateCustomAnimations();
         }

         @Override
         public void loadTexture(IResourceManager resourceManager) throws IOException {
         }

         @Override
         public int getGlTextureId() {
            return 0;
         }

         @Override
         public void setBlurMipmap(boolean bl, boolean bl2) {
         }

         @Override
         public void restoreLastBlurMipmap() {
         }

         @Override
         public MultiTexID getMultiTexID() {
            return null;
         }
      };
      ResourceLocation resourceLocation = new ResourceLocation("optifine/TickableTextures");
      Config.getTextureManager().loadTickableTexture(resourceLocation, iTickableTextureObject);
   }

   public static String fixResourcePath(String path, String basePath) {
      String string = "assets/minecraft/";
      if (path.startsWith(string)) {
         return path.substring(string.length());
      }

      if (path.startsWith("./")) {
         path = path.substring(2);
         if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
         }

         return basePath + path;
      } else {
         if (path.startsWith("/~")) {
            path = path.substring(1);
         }

         String string2 = "mcpatcher/";
         if (path.startsWith("~/")) {
            path = path.substring(2);
            return string2 + path;
         } else {
            return path.startsWith("/") ? string2 + path.substring(1) : path;
         }
      }
   }

   public static String getBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   public static void applyAnisotropicLevel() {
      if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
         float f = GL11.glGetFloat(34047);
         float g = Config.getAnisotropicFilterLevel();
         g = Math.min(g, f);
         GL11.glTexParameterf(3553, 34046, g);
      }
   }

   public static void bindTexture(int glTexId) {
      GlStateManager.bindTexture(glTexId);
   }

   public static boolean isPowerOfTwo(int x) {
      int i = MathHelper.roundUpToPowerOfTwo(x);
      return i == x;
   }

   public static BufferedImage scaleImage(BufferedImage bi, int w2) {
      int i = bi.getWidth();
      int j = bi.getHeight();
      int k = j * w2 / i;
      BufferedImage bufferedImage = new BufferedImage(w2, k, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (w2 < i || w2 % i != 0) {
         object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
      }

      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
      graphics2D.drawImage(bi, 0, 0, w2, k, (ImageObserver)null);
      return bufferedImage;
   }

   public static BufferedImage scaleToPowerOfTwo(BufferedImage bi, int minSize) {
      if (bi == null) {
         return bi;
      }

      int i = bi.getWidth();
      int j = bi.getHeight();
      int k = Math.max(i, minSize);
      k = MathHelper.roundUpToPowerOfTwo(k);
      if (k == i) {
         return bi;
      }

      int l = j * k / i;
      BufferedImage bufferedImage = new BufferedImage(k, l, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (k % i != 0) {
         object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
      }

      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
      graphics2D.drawImage(bi, 0, 0, k, l, (ImageObserver)null);
      return bufferedImage;
   }

   public static BufferedImage scaleMinTo(BufferedImage bi, int minSize) {
      if (bi == null) {
         return bi;
      }

      int i = bi.getWidth();
      int j = bi.getHeight();
      if (i >= minSize) {
         return bi;
      }

      int k = i;

      while (k < minSize) {
         k *= 2;
      }

      int l = j * k / i;
      BufferedImage bufferedImage = new BufferedImage(k, l, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
      graphics2D.drawImage(bi, 0, 0, k, l, (ImageObserver)null);
      return bufferedImage;
   }

   public static Dimension getImageSize(InputStream in, String suffix) {
      Iterator iterator = ImageIO.getImageReadersBySuffix(suffix);

      while (iterator.hasNext()) {
         ImageReader imageReader = (ImageReader)iterator.next();

         Dimension dimension;
         try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(in);
            imageReader.setInput(imageInputStream);
            int i = imageReader.getWidth(imageReader.getMinIndex());
            int j = imageReader.getHeight(imageReader.getMinIndex());
            dimension = new Dimension(i, j);
         } catch (IOException ioException) {
            continue;
         } finally {
            imageReader.dispose();
         }

         return dimension;
      }

      return null;
   }

   public static void dbgMipmaps(TextureAtlasSprite textureatlassprite) {
      int[][] intss = textureatlassprite.getFrameTextureData(0);

      for (int i = 0; i < intss.length; i++) {
         int[] ints = intss[i];
         if (ints == null) {
            Config.dbg("" + i + ": " + ints);
         } else {
            Config.dbg("" + i + ": " + ints.length);
         }
      }
   }

   public static void saveGlTexture(String name, int textureId, int mipmapLevels, int width, int height) {
      bindTexture(textureId);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      File file = new File(name);
      File file2 = file.getParentFile();
      if (file2 != null) {
         file2.mkdirs();
      }

      for (int i = 0; i < 16; i++) {
         File file3 = new File(name + "_" + i + ".png");
         file3.delete();
      }

      for (int j = 0; j <= mipmapLevels; j++) {
         File file4 = new File(name + "_" + j + ".png");
         int k = width >> j;
         int l = height >> j;
         int m = k * l;
         IntBuffer intBuffer = BufferUtils.createIntBuffer(m);
         int[] ints = new int[m];
         GL11.glGetTexImage(3553, j, 32993, 33639, intBuffer);
         intBuffer.get(ints);
         BufferedImage bufferedImage = new BufferedImage(k, l, 2);
         bufferedImage.setRGB(0, 0, k, l, ints, 0, k);

         try {
            ImageIO.write(bufferedImage, "png", file4);
            Config.dbg("Exported: " + file4);
         } catch (Exception exception) {
            Config.warn("Error writing: " + file4);
            Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
         }
      }
   }

   public static int getGLMaximumTextureSize() {
      for (int i = 65536; i > 0; i >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (IntBuffer)null);
         int j = GL11.glGetError();
         int k = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (k != 0) {
            return i;
         }
      }

      return -1;
   }
}
