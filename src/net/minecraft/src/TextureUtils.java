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
   private static final long ab = 84052644820302L;

   public static void update() {
      TextureMap var2 = getTextureMapBlocks();
      if (var2 != null) {
         String var3 = "minecraft:blocks/";
         iconGrassTop = var2.getSpriteSafe(var3 + "grass_top");
         iconGrassSide = var2.getSpriteSafe(var3 + "grass_side");
         iconGrassSideOverlay = var2.getSpriteSafe(var3 + "grass_side_overlay");
         iconSnow = var2.getSpriteSafe(var3 + "snow");
         iconGrassSideSnowed = var2.getSpriteSafe(var3 + "grass_side_snowed");
         iconMyceliumSide = var2.getSpriteSafe(var3 + "mycelium_side");
         iconMyceliumTop = var2.getSpriteSafe(var3 + "mycelium_top");
         iconWaterStill = var2.getSpriteSafe(var3 + "water_still");
         iconWaterFlow = var2.getSpriteSafe(var3 + "water_flow");
         iconLavaStill = var2.getSpriteSafe(var3 + "lava_still");
         iconLavaFlow = var2.getSpriteSafe(var3 + "lava_flow");
         iconFireLayer0 = var2.getSpriteSafe(var3 + "fire_layer_0");
         iconFireLayer1 = var2.getSpriteSafe(var3 + "fire_layer_1");
         iconPortal = var2.getSpriteSafe(var3 + "portal");
         iconGlass = var2.getSpriteSafe(var3 + "glass");
         iconGlassPaneTop = var2.getSpriteSafe(var3 + "glass_pane_top");
         String var4 = "minecraft:items/";
         iconCompass = var2.getSpriteSafe(var4 + "compass");
         iconClock = var2.getSpriteSafe(var4 + "clock");
      }
   }

   public static BufferedImage fixTextureDimensions(String var0, BufferedImage var1) {
      if (var0.startsWith("/mob/zombie") || var0.startsWith("/mob/pigzombie")) {
         int var5 = var1.getWidth();
         int var6 = var1.getHeight();
         if (var5 == var6 * 2) {
            BufferedImage var7 = new BufferedImage(var5, var6 * 2, 2);
            Graphics2D var8 = var7.createGraphics();
            var8.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            var8.drawImage(var1, 0, 0, var5, var6, (ImageObserver)null);
            return var7;
         }
      }

      return var1;
   }

   public static int ceilPowerOfTwo(int var0) {
      byte var4 = 1;

      while (var4 < var0) {
         var4 *= 2;
      }

      return var4;
   }

   public static int g(int var0) {
      byte var4 = 1;

      int var5;
      for (var5 = 0; var4 < var0; var5++) {
         var4 *= 2;
      }

      return var5;
   }

   public static int P(int var0) {
      byte var4 = 1;

      for (int var5 = 0; var5 < var0; var5++) {
         var4 *= 2;
      }

      return var4;
   }

   public static ITextureObject getTexture(ResourceLocation var0) {
      ITextureObject var4 = Config.getTextureManager().getTexture(var0);
      if (var4 != null) {
         return var4;
      }

      if (!Config.hasResource(var0)) {
         return null;
      }

      SimpleTexture var5 = new SimpleTexture(var0);
      Config.getTextureManager().loadTexture(var0, var5);
      return var5;
   }

   public static void resourcesReloaded(IResourceManager var0) {
      if (getTextureMapBlocks() != null) {
         Config.dbg("*** Reloading custom textures ***");
         CustomSky.v();
         TextureAnimations.n();
         update();
         NaturalTextures.update();
         BetterGrass.G();
         BetterSnow.R();
         TextureAnimations.f();
         CustomColors.update();
         CustomSky.L();
         RandomMobs.resetTextures();
         CustomItems.G();
         Shaders.j();
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
      IResourceManager var3 = Config.getResourceManager();
      if (var3 instanceof IReloadableResourceManager) {
         IReloadableResourceManager var4 = (IReloadableResourceManager)var3;
         IResourceManagerReloadListener var5 = new IResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(IResourceManager var1) {
               TextureUtils.resourcesReloaded(var1);
            }
         };
         var4.registerReloadListener(var5);
      }

      ITickableTextureObject var6 = new ITickableTextureObject() {
         @Override
         public void tick() {
            TextureAnimations.s();
         }

         @Override
         public void loadTexture(IResourceManager var1) throws IOException {
         }

         @Override
         public int getGlTextureId() {
            return 0;
         }

         @Override
         public void setBlurMipmap(boolean var1, boolean var2) {
         }

         @Override
         public void restoreLastBlurMipmap() {
         }

         @Override
         public MultiTexID getMultiTexID() {
            return null;
         }
      };
      ResourceLocation var7 = new ResourceLocation("optifine/TickableTextures");
      Config.getTextureManager().loadTickableTexture(var7, var6);
   }

   public static String fixResourcePath(String var0, String var1) {
      String var5 = "assets/minecraft/";
      if (var0.startsWith(var5)) {
         return var0.substring(var5.length());
      }

      if (var0.startsWith("./")) {
         var0 = var0.substring(2);
         if (!var1.endsWith("/")) {
            var1 = var1 + "/";
         }

         return var1 + var0;
      } else {
         if (var0.startsWith("/~")) {
            var0 = var0.substring(1);
         }

         String var6 = "mcpatcher/";
         if (var0.startsWith("~/")) {
            var0 = var0.substring(2);
            return var6 + var0;
         } else {
            return var0.startsWith("/") ? var6 + var0.substring(1) : var0;
         }
      }
   }

   public static String getBasePath(String var0) {
      int var3 = var0.lastIndexOf(47);
      return var3 < 0 ? "" : var0.substring(0, var3);
   }

   public static void applyAnisotropicLevel() {
      long var0 = ab ^ 140355487236456L;
      if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
         float var3 = GL11.glGetFloat(34047);
         float var4 = Config.getAnisotropicFilterLevel();
         long var10000 = 5253308827969494935L ^ var0;
         var4 = Math.min(var4, var3);
         GL11.glTexParameterf(3553, 34046, var4);
      }
   }

   public static void bindTexture(int var0) {
      GlStateManager.bindTexture(var0);
   }

   public static boolean isPowerOfTwo(int var0) {
      int var4 = MathHelper.roundUpToPowerOfTwo(var0);
      return var4 == var0;
   }

   public static BufferedImage scaleImage(BufferedImage var0, int var1) {
      int var5 = var0.getWidth();
      int var6 = var0.getHeight();
      int var7 = var6 * var1 / var5;
      BufferedImage var8 = new BufferedImage(var1, var7, 2);
      Graphics2D var9 = var8.createGraphics();
      Object var10 = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (var1 < var5 || var1 % var5 != 0) {
         var10 = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
      }

      var9.setRenderingHint(RenderingHints.KEY_INTERPOLATION, var10);
      var9.drawImage(var0, 0, 0, var1, var7, (ImageObserver)null);
      return var8;
   }

   public static BufferedImage scaleToPowerOfTwo(BufferedImage var0, int var1) {
      if (var0 == null) {
         return var0;
      }

      int var5 = var0.getWidth();
      int var6 = var0.getHeight();
      int var7 = Math.max(var5, var1);
      var7 = MathHelper.roundUpToPowerOfTwo(var7);
      if (var7 == var5) {
         return var0;
      }

      int var8 = var6 * var7 / var5;
      BufferedImage var9 = new BufferedImage(var7, var8, 2);
      Graphics2D var10 = var9.createGraphics();
      Object var11 = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (var7 % var5 != 0) {
         var11 = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
      }

      var10.setRenderingHint(RenderingHints.KEY_INTERPOLATION, var11);
      var10.drawImage(var0, 0, 0, var7, var8, (ImageObserver)null);
      return var9;
   }

   public static BufferedImage m(BufferedImage var0, int var1) {
      if (var0 == null) {
         return var0;
      }

      int var5 = var0.getWidth();
      int var6 = var0.getHeight();
      if (var5 >= var1) {
         return var0;
      }

      int var7 = var5;

      while (var7 < var1) {
         var7 *= 2;
      }

      int var8 = var6 * var7 / var5;
      BufferedImage var9 = new BufferedImage(var7, var8, 2);
      Graphics2D var10 = var9.createGraphics();
      Object var11 = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      var10.setRenderingHint(RenderingHints.KEY_INTERPOLATION, var11);
      var10.drawImage(var0, 0, 0, var7, var8, (ImageObserver)null);
      return var9;
   }

   public static Dimension getImageSize(InputStream var0, String var1) {
      Iterator var5 = ImageIO.getImageReadersBySuffix(var1);

      while (var5.hasNext()) {
         ImageReader var6 = (ImageReader)var5.next();

         Dimension var7;
         try {
            ImageInputStream var8 = ImageIO.createImageInputStream(var0);
            var6.setInput(var8);
            int var9 = var6.getWidth(var6.getMinIndex());
            int var10 = var6.getHeight(var6.getMinIndex());
            var7 = new Dimension(var9, var10);
         } catch (IOException var14) {
            continue;
         } finally {
            var6.dispose();
         }

         return var7;
      }

      return null;
   }

   public static void dbgMipmaps(TextureAtlasSprite var0) {
      int[][] var4 = var0.getFrameTextureData(0);

      for (int var5 = 0; var5 < var4.length; var5++) {
         int[] var6 = var4[var5];
         if (var6 == null) {
            Config.dbg("" + var5 + ": " + var6);
         } else {
            Config.dbg("" + var5 + ": " + var6.length);
         }
      }
   }

   public static void saveGlTexture(String var0, int var1, int var2, int var3, int var4) {
      long var5 = ab ^ 49330444783295L;
      bindTexture(var1);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      File var8 = new File(var0);
      File var9 = var8.getParentFile();
      if (var9 != null) {
         var9.mkdirs();
      }

      for (int var10 = 0; var10 < 16; var10++) {
         File var11 = new File(var0 + "_" + var10 + ".png");
         var11.delete();
      }

      for (int var20 = 0; var20 <= var2; var20++) {
         File var21 = new File(var0 + "_" + var20 + ".png");
         int var12 = var3 >> var20;
         long var10000 = 2528722534420107649L ^ var5;
         int var13 = var4 >> var20;
         int var14 = var12 * var13;
         IntBuffer var15 = BufferUtils.createIntBuffer(var14);
         int[] var16 = new int[var14];
         GL11.glGetTexImage(3553, var20, 32993, 33639, var15);
         var15.get(var16);
         BufferedImage var17 = new BufferedImage(var12, var13, 2);
         var17.setRGB(0, 0, var12, var13, var16, 0, var12);

         try {
            ImageIO.write(var17, "png", var21);
            Config.dbg("Exported: " + var21);
         } catch (Exception var19) {
            Config.warn("Error writing: " + var21);
            Config.warn("" + var19.getClass().getName() + ": " + var19.getMessage());
         }
      }
   }

   public static int getGLMaximumTextureSize() {
      long var0 = ab ^ 129269130181127L;

      for (int var3 = 65536; var3 > 0; var3 >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, var3, var3, 0, 6408, 5121, (IntBuffer)null);
         int var4 = GL11.glGetError();
         long var10000 = 1998288007323378328L ^ var0;
         int var5 = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (var5 != 0) {
            return var3;
         }
      }

      return -1;
   }
}
