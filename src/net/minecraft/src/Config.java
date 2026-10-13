package net.minecraft.src;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import shadersmod.client.Shaders;

public class Config {
   public static final String OF_NAME = "OptiFine";
   public static final String MC_VERSION = "1.8.8";
   public static final String OF_EDITION = "HD_U";
   public static final String OF_RELEASE = "H8";
   public static final String VERSION = "OptiFine_1.8.8_HD_U_H8";
   private static String newRelease = null;
   private static boolean notify64BitJava = false;
   public static String openGlVersion = null;
   public static String openGlRenderer = null;
   public static String openGlVendor = null;
   public static String[] openGlExtensions = null;
   public static GlVersion glVersion = null;
   public static GlVersion glslVersion = null;
   public static int minecraftVersionInt = -1;
   public static boolean fancyFogAvailable = false;
   public static boolean occlusionAvailable = false;
   private static GameSettings gameSettings = null;
   private static Minecraft minecraft = Minecraft.getMinecraft();
   private static boolean initialized = false;
   private static Thread minecraftThread = null;
   private static DisplayMode desktopDisplayMode = null;
   private static DisplayMode[] displayModes = null;
   private static int antialiasingLevel = 0;
   private static int availableProcessors = 0;
   public static boolean zoomMode = false;
   private static int texturePackClouds = 0;
   public static boolean waterOpacityChanged = false;
   private static boolean fullscreenModeChecked = false;
   private static boolean desktopModeChecked = false;
   private static DefaultResourcePack defaultResourcePackLazy = null;
   public static final Float DEF_ALPHA_FUNC_LEVEL = 0.1F;
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long b = 63959641325076L;

   public static String getVersion() {
      return "OptiFine_1.8.8_HD_U_H8";
   }

   public static String getVersionDebug() {
      StringBuffer var3 = new StringBuffer(32);
      if (isDynamicLights()) {
         var3.append("DL: ");
         var3.append(String.valueOf(DynamicLights.getCount()));
         var3.append(", ");
      }

      var3.append("OptiFine_1.8.8_HD_U_H8");
      String var4 = Shaders.getShaderPackName();
      if (var4 != null) {
         var3.append(", ");
         var3.append(var4);
      }

      return var3.toString();
   }

   public static void initGameSettings(GameSettings var0) {
      long var1 = b ^ 23453252208835L;
      if (gameSettings == null) {
         gameSettings = var0;
         desktopDisplayMode = Display.getDesktopDisplayMode();
         long var10000 = 8436825895959655514L ^ var1;
         updateAvailableProcessors();
         ReflectorForge.putLaunchBlackboard("optifine.ForgeSplashCompatible", Boolean.TRUE);
      }
   }

   public static void initDisplay() {
      checkInitialized();
      antialiasingLevel = gameSettings.ofAaLevel;
      checkDisplaySettings();
      checkDisplayMode();
      minecraftThread = Thread.currentThread();
      updateThreadPriorities();
      Shaders.startup(Minecraft.getMinecraft());
   }

   public static void checkInitialized() {
      if (!initialized && Display.isCreated()) {
         initialized = true;
         checkOpenGlCaps();
      }
   }

   private static void checkOpenGlCaps() {
      log("");
      log(getVersion());
      log("Build: " + getBuild());
      log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
      log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
      log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
      log("LWJGL: " + Sys.getVersion());
      openGlVersion = GL11.glGetString(7938);
      openGlRenderer = GL11.glGetString(7937);
      openGlVendor = GL11.glGetString(7936);
      log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
      log("OpenGL Version: " + getOpenGlVersionString());
      if (!GLContext.getCapabilities().OpenGL12) {
         log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
      }

      fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance;
      if (!fancyFogAvailable) {
         log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
      }

      occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query;
      if (!occlusionAvailable) {
         log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
      }

      int var3 = TextureUtils.getGLMaximumTextureSize();
      dbg("Maximum texture size: " + var3 + "x" + var3);
   }

   private static String getBuild() {
      try {
         InputStream var3 = Config.class.getResourceAsStream("/buildof.txt");
         return var3 == null ? null : readLines(var3)[0];
      } catch (Exception var5) {
         warn("" + var5.getClass().getName() + ": " + var5.getMessage());
         return null;
      }
   }

   public static boolean v() {
      return fancyFogAvailable;
   }

   public static boolean l() {
      return occlusionAvailable;
   }

   public static int getMinecraftVersionInt() {
      if (minecraftVersionInt < 0) {
         String[] var3 = tokenize("1.8.8", ".");
         int var4 = 0;
         if (var3.length > 0) {
            var4 += 10000 * parseInt(var3[0], 0);
         }

         if (var3.length > 1) {
            var4 += 100 * parseInt(var3[1], 0);
         }

         if (var3.length > 2) {
            var4 += 1 * parseInt(var3[2], 0);
         }

         minecraftVersionInt = var4;
      }

      return minecraftVersionInt;
   }

   public static String getOpenGlVersionString() {
      GlVersion var0 = getGlVersion();
      return "" + var0.getMajor() + "." + var0.getMinor() + "." + var0.getRelease();
   }

   private static GlVersion getGlVersionLwjgl() {
      return GLContext.getCapabilities().OpenGL44
         ? new GlVersion(4, 4)
         : (
            GLContext.getCapabilities().OpenGL43
               ? new GlVersion(4, 3)
               : (
                  GLContext.getCapabilities().OpenGL42
                     ? new GlVersion(4, 2)
                     : (
                        GLContext.getCapabilities().OpenGL41
                           ? new GlVersion(4, 1)
                           : (
                              GLContext.getCapabilities().OpenGL40
                                 ? new GlVersion(4, 0)
                                 : (
                                    GLContext.getCapabilities().OpenGL33
                                       ? new GlVersion(3, 3)
                                       : (
                                          GLContext.getCapabilities().OpenGL32
                                             ? new GlVersion(3, 2)
                                             : (
                                                GLContext.getCapabilities().OpenGL31
                                                   ? new GlVersion(3, 1)
                                                   : (
                                                      GLContext.getCapabilities().OpenGL30
                                                         ? new GlVersion(3, 0)
                                                         : (
                                                            GLContext.getCapabilities().OpenGL21
                                                               ? new GlVersion(2, 1)
                                                               : (
                                                                  GLContext.getCapabilities().OpenGL20
                                                                     ? new GlVersion(2, 0)
                                                                     : (
                                                                        GLContext.getCapabilities().OpenGL15
                                                                           ? new GlVersion(1, 5)
                                                                           : (
                                                                              GLContext.getCapabilities().OpenGL14
                                                                                 ? new GlVersion(1, 4)
                                                                                 : (
                                                                                    GLContext.getCapabilities().OpenGL13
                                                                                       ? new GlVersion(1, 3)
                                                                                       : (
                                                                                          GLContext.getCapabilities().OpenGL12
                                                                                             ? new GlVersion(1, 2)
                                                                                             : (
                                                                                                GLContext.getCapabilities().OpenGL11
                                                                                                   ? new GlVersion(1, 1)
                                                                                                   : new GlVersion(1, 0)
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static GlVersion getGlVersion() {
      if (glVersion == null) {
         String var3 = GL11.glGetString(7938);
         glVersion = parseGlVersion(var3, (GlVersion)null);
         if (glVersion == null) {
            glVersion = getGlVersionLwjgl();
         }

         if (glVersion == null) {
            glVersion = new GlVersion(1, 0);
         }
      }

      return glVersion;
   }

   public static GlVersion getGlslVersion() {
      if (glslVersion == null) {
         String var3 = GL11.glGetString(35724);
         glslVersion = parseGlVersion(var3, (GlVersion)null);
         if (glslVersion == null) {
            glslVersion = new GlVersion(1, 10);
         }
      }

      return glslVersion;
   }

   public static GlVersion parseGlVersion(String var0, GlVersion var1) {
      try {
         if (var0 == null) {
            return var1;
         }

         Pattern var5 = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
         Matcher var6 = var5.matcher(var0);
         if (!var6.matches()) {
            return var1;
         }

         int var7 = Integer.parseInt(var6.group(1));
         int var8 = Integer.parseInt(var6.group(2));
         int var9 = var6.group(4) != null ? Integer.parseInt(var6.group(4)) : 0;
         String var10 = var6.group(5);
         return new GlVersion(var7, var8, var9, var10);
      } catch (Exception var11) {
         var11.printStackTrace();
         return var1;
      }
   }

   public static String[] getOpenGlExtensions() {
      if (openGlExtensions == null) {
         openGlExtensions = detectOpenGlExtensions();
      }

      return openGlExtensions;
   }

   private static String[] detectOpenGlExtensions() {
      try {
         GlVersion var3 = getGlVersion();
         if (var3.getMajor() >= 3) {
            int var4 = GL11.glGetInteger(33309);
            if (var4 > 0) {
               String[] var5 = new String[var4];

               for (int var6 = 0; var6 < var4; var6++) {
                  var5[var6] = GL30.glGetStringi(7939, var6);
               }

               return var5;
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      try {
         String var9 = GL11.glGetString(7939);
         return var9.split(" ");
      } catch (Exception var7) {
         var7.printStackTrace();
         return new String[0];
      }
   }

   public static void updateThreadPriorities() {
      updateAvailableProcessors();
      if (isSingleProcessor()) {
         if (isSmoothWorld()) {
            minecraftThread.setPriority(10);
            setThreadPriority("Server thread", 1);
         } else {
            minecraftThread.setPriority(5);
            setThreadPriority("Server thread", 5);
         }
      } else {
         minecraftThread.setPriority(10);
         setThreadPriority("Server thread", 5);
      }
   }

   private static void setThreadPriority(String var0, int var1) {
      try {
         ThreadGroup var5 = Thread.currentThread().getThreadGroup();
         if (var5 == null) {
            return;
         }

         int var6 = (var5.activeCount() + 10) * 2;
         Thread[] var7 = new Thread[var6];
         var5.enumerate(var7, false);

         for (int var8 = 0; var8 < var7.length; var8++) {
            Thread var9 = var7[var8];
            if (var9 != null && var9.getName().startsWith(var0)) {
               var9.setPriority(var1);
            }
         }
      } catch (Throwable var10) {
         warn(var10.getClass().getName() + ": " + var10.getMessage());
      }
   }

   public static boolean isMinecraftThread() {
      return Thread.currentThread() == minecraftThread;
   }

   public static boolean pm() {
      return gameSettings.g > 0;
   }

   public static int getMipmapLevels() {
      return gameSettings.g;
   }

   public static int getMipmapType() {
      switch (gameSettings.ofMipmapType) {
         case 0:
            return 9986;
         case 1:
            return 9986;
         case 2:
            if (isMultiTexture()) {
               return 9985;
            }

            return 9986;
         case 3:
            if (isMultiTexture()) {
               return 9987;
            }

            return 9986;
         default:
            return 9986;
      }
   }

   public static boolean isUseAlphaFunc() {
      float var3 = getAlphaFuncLevel();
      return var3 > DEF_ALPHA_FUNC_LEVEL + 1.0E-5F;
   }

   public static float getAlphaFuncLevel() {
      return DEF_ALPHA_FUNC_LEVEL;
   }

   public static boolean isFogFancy() {
      return !v() ? false : gameSettings.ofFogType == 2;
   }

   public static boolean isFogFast() {
      return gameSettings.ofFogType == 1;
   }

   public static boolean isFogOff() {
      return gameSettings.ofFogType == 3;
   }

   public static float S() {
      return gameSettings.ofFogStart;
   }

   public static void dbg(String var0) {
      LOGGER.info("[OptiFine] " + var0);
   }

   public static void warn(String var0) {
      LOGGER.warn("[OptiFine] " + var0);
   }

   public static void error(String var0) {
      LOGGER.error("[OptiFine] " + var0);
   }

   public static void log(String var0) {
      dbg(var0);
   }

   public static int B() {
      return gameSettings.ofChunkUpdates;
   }

   public static boolean a() {
      return gameSettings.X;
   }

   public static boolean isRainFancy() {
      return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
   }

   public static boolean isRainOff() {
      return gameSettings.ofRain == 3;
   }

   public static boolean c() {
      return gameSettings.ofClouds != 0
         ? gameSettings.ofClouds == 2
         : (
            isShaders() && !Shaders.shaderPackClouds.E()
               ? Shaders.shaderPackClouds.K()
               : (texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics)
         );
   }

   public static boolean isCloudsOff() {
      return gameSettings.ofClouds != 0
         ? gameSettings.ofClouds == 3
         : (isShaders() && !Shaders.shaderPackClouds.E() ? Shaders.shaderPackClouds.a() : (texturePackClouds != 0 ? texturePackClouds == 3 : false));
   }

   public static void updateTexturePackClouds() {
      texturePackClouds = 0;
      IResourceManager var3 = getResourceManager();
      if (var3 != null) {
         try {
            InputStream var4 = var3.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
            if (var4 == null) {
               return;
            }

            Properties var5 = new Properties();
            var5.load(var4);
            var4.close();
            String var6 = var5.getProperty("clouds");
            if (var6 == null) {
               return;
            }

            dbg("Texture pack clouds: " + var6);
            var6 = var6.toLowerCase();
            if (var6.equals("fast")) {
               texturePackClouds = 1;
            }

            if (var6.equals("fancy")) {
               texturePackClouds = 2;
            }

            if (var6.equals("off")) {
               texturePackClouds = 3;
            }
         } catch (Exception var7) {
         }
      }
   }

   public static ModelManager getModelManager() {
      return minecraft.getRenderItem().modelManager;
   }

   public static boolean isTreesFancy() {
      return gameSettings.ofTrees == 0 ? gameSettings.fancyGraphics : gameSettings.ofTrees != 1;
   }

   public static boolean isTreesSmart() {
      return gameSettings.ofTrees == 4;
   }

   public static boolean isCullFacesLeaves() {
      return gameSettings.ofTrees == 0 ? !gameSettings.fancyGraphics : gameSettings.ofTrees == 4;
   }

   public static boolean g() {
      return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
   }

   public static int limit(int var0, int var1, int var2) {
      return var0 < var1 ? var1 : (var0 > var2 ? var2 : var0);
   }

   public static float limit(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : (var0 > var2 ? var2 : var0);
   }

   public static double limit(double var0, double var2, double var4) {
      return var0 < var2 ? var2 : (var0 > var4 ? var4 : var0);
   }

   public static float limitTo1(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   public static boolean W_boolean() {
      return gameSettings.ofAnimatedWater != 2;
   }

   public static boolean T() {
      return gameSettings.ofAnimatedWater == 1;
   }

   public static boolean isAnimatedPortal() {
      return gameSettings.ofAnimatedPortal;
   }

   public static boolean pl() {
      return gameSettings.ofAnimatedLava != 2;
   }

   public static boolean pY() {
      return gameSettings.ofAnimatedLava == 1;
   }

   public static boolean w() {
      return gameSettings.ofAnimatedFire;
   }

   public static boolean isAnimatedRedstone() {
      return gameSettings.ofAnimatedRedstone;
   }

   public static boolean isAnimatedExplosion() {
      return gameSettings.ofAnimatedExplosion;
   }

   public static boolean isAnimatedFlame() {
      return gameSettings.ofAnimatedFlame;
   }

   public static boolean isAnimatedSmoke() {
      return gameSettings.ofAnimatedSmoke;
   }

   public static boolean isVoidParticles() {
      return gameSettings.ofVoidParticles;
   }

   public static boolean isWaterParticles() {
      return gameSettings.ofWaterParticles;
   }

   public static boolean isRainSplash() {
      return gameSettings.ofRainSplash;
   }

   public static boolean pz() {
      return gameSettings.ofPortalParticles;
   }

   public static boolean isPotionParticles() {
      return gameSettings.ofPotionParticles;
   }

   public static boolean isFireworkParticles() {
      return gameSettings.ofFireworkParticles;
   }

   public static float getAmbientOcclusionLevel() {
      return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : gameSettings.ofAoLevel;
   }

   public static String arrayToString(Object[] var0) {
      if (var0 == null) {
         return "";
      }

      StringBuffer var3 = new StringBuffer(var0.length * 5);

      for (int var4 = 0; var4 < var0.length; var4++) {
         Object var5 = var0[var4];
         if (var4 > 0) {
            var3.append(", ");
         }

         var3.append(String.valueOf(var5));
      }

      return var3.toString();
   }

   public static String arrayToString(int[] var0) {
      if (var0 == null) {
         return "";
      }

      StringBuffer var4 = new StringBuffer(var0.length * 5);

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6 = var0[var5];
         if (var5 > 0) {
            var4.append(", ");
         }

         var4.append(String.valueOf(var6));
      }

      return var4.toString();
   }

   public static Minecraft getMinecraft() {
      return minecraft;
   }

   public static TextureManager getTextureManager() {
      return minecraft.getTextureManager();
   }

   public static IResourceManager getResourceManager() {
      return minecraft.getResourceManager();
   }

   public static InputStream getResourceStream(ResourceLocation var0) throws IOException {
      return getResourceStream(minecraft.getResourceManager(), var0);
   }

   public static InputStream getResourceStream(IResourceManager var0, ResourceLocation var1) throws IOException {
      IResource var4 = var0.getResource(var1);
      return var4 == null ? null : var4.getInputStream();
   }

   public static IResource getResource(ResourceLocation var0) throws IOException {
      return minecraft.getResourceManager().getResource(var0);
   }

   public static boolean hasResource(ResourceLocation var0) {
      IResourcePack var3 = getDefiningResourcePack(var0);
      return var3 != null;
   }

   public static boolean hasResource(IResourceManager var0, ResourceLocation var1) {
      try {
         IResource var4 = var0.getResource(var1);
         return var4 != null;
      } catch (IOException var5) {
         return false;
      }
   }

   public static IResourcePack[] getResourcePacks() {
      ResourcePackRepository var3 = minecraft.getResourcePackRepository();
      List var4 = var3.getRepositoryEntries();
      ArrayList var5 = new ArrayList();

      for (Object var7 : var4) {
         var5.add(((ResourcePackRepository.Entry)var7).getResourcePack());
      }

      if (var3.getResourcePackInstance() != null) {
         var5.add(var3.getResourcePackInstance());
      }

      return (net.minecraft.client.resources.IResourcePack[])(var5.toArray(new IResourcePack[var5.size()]));
   }

   public static String getResourcePackNames() {
      if (minecraft.getResourcePackRepository() == null) {
         return "";
      }

      IResourcePack[] var3 = getResourcePacks();
      if (var3.length <= 0) {
         return getDefaultResourcePack().getPackName();
      }

      String[] var4 = new String[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         var4[var5] = var3[var5].getPackName();
      }

      return arrayToString(var4);
   }

   public static DefaultResourcePack getDefaultResourcePack() {
      if (defaultResourcePackLazy == null) {
         Minecraft var3 = Minecraft.getMinecraft();
         defaultResourcePackLazy = (DefaultResourcePack)Reflector.getFieldValue(var3, Reflector.Minecraft_defaultResourcePack);
         if (defaultResourcePackLazy == null) {
            ResourcePackRepository var4 = var3.getResourcePackRepository();
            if (var4 != null) {
               defaultResourcePackLazy = (DefaultResourcePack)var4.rprDefaultResourcePack;
            }
         }
      }

      return defaultResourcePackLazy;
   }

   public static boolean isFromDefaultResourcePack(ResourceLocation var0) {
      IResourcePack var3 = getDefiningResourcePack(var0);
      return var3 == getDefaultResourcePack();
   }

   public static IResourcePack getDefiningResourcePack(ResourceLocation var0) {
      ResourcePackRepository var4 = minecraft.getResourcePackRepository();
      IResourcePack var5 = var4.getResourcePackInstance();
      if (var5 != null && var5.resourceExists(var0)) {
         return var5;
      }

      List var6 = (List)Reflector.getFieldValue(var4, Reflector.ResourcePackRepository_repositoryEntries);
      if (var6 != null) {
         for (int var7 = var6.size() - 1; var7 >= 0; var7--) {
            ResourcePackRepository.Entry var8 = (ResourcePackRepository.Entry)var6.get(var7);
            IResourcePack var9 = var8.getResourcePack();
            if (var9.resourceExists(var0)) {
               return var9;
            }
         }
      }

      return getDefaultResourcePack().resourceExists(var0) ? getDefaultResourcePack() : null;
   }

   public static RenderGlobal getRenderGlobal() {
      return minecraft.renderGlobal;
   }

   public static boolean py() {
      return gameSettings.ofBetterGrass != 3;
   }

   public static boolean e() {
      return gameSettings.ofBetterGrass == 2;
   }

   public static boolean pd() {
      return gameSettings.lP;
   }

   public static boolean K() {
      return gameSettings.ZC;
   }

   public static boolean u() {
      return gameSettings.d;
   }

   public static boolean pR() {
      return !u() ? false : !isShaders() || Shaders.i_boolean();
   }

   public static boolean pM() {
      return !u() ? false : !isShaders() || Shaders.r();
   }

   public static boolean isVignetteEnabled() {
      return isShaders() && !Shaders.l() ? false : (gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2);
   }

   public static boolean s() {
      return gameSettings.lW;
   }

   public static void sleep(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }
   }

   public static boolean isTimeDayOnly() {
      return gameSettings.ofTime == 1;
   }

   public static boolean O() {
      return gameSettings.ofTime == 0;
   }

   public static boolean isTimeNightOnly() {
      return gameSettings.ofTime == 2;
   }

   public static boolean V() {
      return gameSettings.ofClearWater;
   }

   public static int getAnisotropicFilterLevel() {
      return gameSettings.ofAfLevel;
   }

   public static boolean pL() {
      return getAnisotropicFilterLevel() > 1;
   }

   public static int getAntialiasingLevel() {
      return antialiasingLevel;
   }

   public static boolean isAntialiasing() {
      return getAntialiasingLevel() > 0;
   }

   public static boolean x() {
      return getGameSettings().ofAaLevel > 0;
   }

   public static boolean isMultiTexture() {
      return getAnisotropicFilterLevel() > 1 ? true : getAntialiasingLevel() > 0;
   }

   public static boolean between(int var0, int var1, int var2) {
      return var0 >= var1 && var0 <= var2;
   }

   public static boolean isDrippingWaterLava() {
      return gameSettings.ofDrippingWaterLava;
   }

   public static boolean d() {
      return gameSettings.Zi;
   }

   public static Dimension getFullscreenDimension() {
      if (desktopDisplayMode == null) {
         return null;
      }

      if (gameSettings == null) {
         return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
      }

      String var3 = gameSettings.ofFullscreenMode;
      if (var3.equals("Default")) {
         return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
      }

      String[] var4 = tokenize(var3, " x");
      return var4.length < 2
         ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight())
         : new Dimension(parseInt(var4[0], -1), parseInt(var4[1], -1));
   }

   public static int parseInt(String var0, int var1) {
      try {
         if (var0 == null) {
            return var1;
         }

         var0 = var0.trim();
         return Integer.parseInt(var0);
      } catch (NumberFormatException var6) {
         return var1;
      }
   }

   public static float parseFloat(String var0, float var1) {
      try {
         if (var0 == null) {
            return var1;
         }

         var0 = var0.trim();
         return Float.parseFloat(var0);
      } catch (NumberFormatException var6) {
         return var1;
      }
   }

   public static boolean parseBoolean(String var0, boolean var1) {
      try {
         if (var0 == null) {
            return var1;
         }

         var0 = var0.trim();
         return Boolean.parseBoolean(var0);
      } catch (NumberFormatException var6) {
         return var1;
      }
   }

   public static String[] tokenize(String var0, String var1) {
      StringTokenizer var5 = new StringTokenizer(var0, var1);
      ArrayList var6 = new ArrayList();

      while (var5.hasMoreTokens()) {
         String var7 = var5.nextToken();
         var6.add(var7);
      }

      return (java.lang.String[])(var6.toArray(new String[var6.size()]));
   }

   public static DisplayMode getDesktopDisplayMode() {
      return desktopDisplayMode;
   }

   public static DisplayMode[] getDisplayModes() {
      if (displayModes == null) {
         try {
            DisplayMode[] var3 = Display.getAvailableDisplayModes();
            Set var4 = getDisplayModeDimensions(var3);
            ArrayList var5 = new ArrayList();

            for (Object var7$raw : var4) {
               Dimension var7 = (Dimension)var7$raw;
               DisplayMode[] var8 = getDisplayModes(var3, var7);
               DisplayMode var9 = getDisplayMode(var8, desktopDisplayMode);
               if (var9 != null) {
                  var5.add(var9);
               }
            }

            DisplayMode[] var11 = (org.lwjgl.opengl.DisplayMode[])(var5.toArray(new DisplayMode[var5.size()]));
            Arrays.sort(var11, new DisplayModeComparator());
            return var11;
         } catch (Exception var10) {
            var10.printStackTrace();
            displayModes = new DisplayMode[]{desktopDisplayMode};
         }
      }

      return displayModes;
   }

   public static DisplayMode getLargestDisplayMode() {
      DisplayMode[] var3 = getDisplayModes();
      if (var3 != null && var3.length >= 1) {
         DisplayMode var4 = var3[var3.length - 1];
         return desktopDisplayMode.getWidth() > var4.getWidth()
            ? desktopDisplayMode
            : (desktopDisplayMode.getWidth() == var4.getWidth() && desktopDisplayMode.getHeight() > var4.getHeight() ? desktopDisplayMode : var4);
      } else {
         return desktopDisplayMode;
      }
   }

   private static Set<Dimension> getDisplayModeDimensions(DisplayMode[] var0) {
      HashSet var4 = new HashSet();

      for (int var5 = 0; var5 < var0.length; var5++) {
         DisplayMode var6 = var0[var5];
         Dimension var7 = new Dimension(var6.getWidth(), var6.getHeight());
         var4.add(var7);
      }

      return var4;
   }

   private static DisplayMode[] getDisplayModes(DisplayMode[] var0, Dimension var1) {
      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var0.length; var6++) {
         DisplayMode var7 = var0[var6];
         if (var7.getWidth() == var1.getWidth() && var7.getHeight() == var1.getHeight()) {
            var5.add(var7);
         }
      }

      return (org.lwjgl.opengl.DisplayMode[])(var5.toArray(new DisplayMode[var5.size()]));
   }

   private static DisplayMode getDisplayMode(DisplayMode[] var0, DisplayMode var1) {
      if (var1 != null) {
         for (int var5 = 0; var5 < var0.length; var5++) {
            DisplayMode var6 = var0[var5];
            if (var6.getBitsPerPixel() == var1.getBitsPerPixel() && var6.getFrequency() == var1.getFrequency()) {
               return var6;
            }
         }
      }

      if (var0.length <= 0) {
         return null;
      }

      Arrays.sort(var0, new DisplayModeComparator());
      return var0[var0.length - 1];
   }

   public static String[] getDisplayModeNames() {
      DisplayMode[] var3 = getDisplayModes();
      String[] var4 = new String[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         DisplayMode var6 = var3[var5];
         String var7 = "" + var6.getWidth() + "x" + var6.getHeight();
         var4[var5] = var7;
      }

      return var4;
   }

   public static DisplayMode getDisplayMode(Dimension var0) throws LWJGLException {
      DisplayMode[] var4 = getDisplayModes();

      for (int var5 = 0; var5 < var4.length; var5++) {
         DisplayMode var6 = var4[var5];
         if (var6.getWidth() == var0.width && var6.getHeight() == var0.height) {
            return var6;
         }
      }

      return desktopDisplayMode;
   }

   public static boolean F() {
      return gameSettings.ofAnimatedTerrain;
   }

   public static boolean pN() {
      return gameSettings.ofAnimatedTextures;
   }

   public static boolean isSwampColors() {
      return gameSettings.lD;
   }

   public static boolean isRandomMobs() {
      return gameSettings.Zb;
   }

   public static void checkGlError(String var0) {
      int var4 = GL11.glGetError();
      if (var4 != 0) {
         String var5 = GLU.gluErrorString(var4);
         error("OpenGlError: " + var4 + " (" + var5 + "), at: " + var0);
      }
   }

   public static boolean isSmoothBiomes() {
      return gameSettings.lx;
   }

   public static boolean isCustomColors() {
      return gameSettings.s;
   }

   public static boolean L() {
      return gameSettings.l0;
   }

   public static boolean m() {
      return gameSettings.lo;
   }

   public static boolean b() {
      return gameSettings.AJ;
   }

   public static boolean isConnectedTextures() {
      return gameSettings.ofConnectedTextures != 3;
   }

   public static boolean isNaturalTextures() {
      return gameSettings.ZB;
   }

   public static boolean isConnectedTexturesFancy() {
      return gameSettings.ofConnectedTextures == 2;
   }

   public static boolean isFastRender() {
      return gameSettings.ZE;
   }

   public static boolean pQ() {
      return gameSettings.ofTranslucentBlocks == 0 ? gameSettings.fancyGraphics : gameSettings.ofTranslucentBlocks == 2;
   }

   public static boolean isShaders() {
      return Shaders.shaderPackLoaded;
   }

   public static String[] readLines(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      return readLines(var1);
   }

   public static String[] readLines(InputStream var0) throws IOException {
      ArrayList var4 = new ArrayList();
      InputStreamReader var5 = new InputStreamReader(var0, "ASCII");
      BufferedReader var6 = new BufferedReader(var5);

      while (true) {
         String var7 = var6.readLine();
         if (var7 == null) {
            return (java.lang.String[])(var4.toArray(new String[var4.size()]));
         }

         var4.add(var7);
      }
   }

   public static String readFile(File var0) throws IOException {
      FileInputStream var3 = new FileInputStream(var0);
      return readInputStream(var3, "ASCII");
   }

   public static String readInputStream(InputStream var0) throws IOException {
      return readInputStream(var0, "ASCII");
   }

   public static String readInputStream(InputStream var0, String var1) throws IOException {
      InputStreamReader var5 = new InputStreamReader(var0, var1);
      BufferedReader var6 = new BufferedReader(var5);
      StringBuffer var7 = new StringBuffer();

      while (true) {
         String var8 = var6.readLine();
         if (var8 == null) {
            return var7.toString();
         }

         var7.append(var8);
         var7.append("\n");
      }
   }

   public static byte[] readAll(InputStream var0) throws IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();
      byte[] var5 = new byte[1024];

      while (true) {
         int var6 = var0.read(var5);
         if (var6 < 0) {
            var0.close();
            return var4.toByteArray();
         }

         var4.write(var5, 0, var6);
      }
   }

   public static GameSettings getGameSettings() {
      return gameSettings;
   }

   public static String W() {
      return newRelease;
   }

   public static void G(String var0) {
      newRelease = var0;
   }

   public static int compareRelease(String var0, String var1) {
      String[] var5 = splitRelease(var0);
      String[] var6 = splitRelease(var1);
      String var7 = var5[0];
      String var8 = var6[0];
      if (!var7.equals(var8)) {
         return var7.compareTo(var8);
      }

      int var9 = parseInt(var5[1], -1);
      int var10 = parseInt(var6[1], -1);
      if (var9 != var10) {
         return var9 - var10;
      }

      String var11 = var5[2];
      String var12 = var6[2];
      if (!var11.equals(var12)) {
         if (var11.isEmpty()) {
            return 1;
         }

         if (var12.isEmpty()) {
            return -1;
         }
      }

      return var11.compareTo(var12);
   }

   private static String[] splitRelease(String var0) {
      if (var0 != null && var0.length() > 0) {
         Pattern var4 = Pattern.compile("([A-Z])([0-9]+)(.*)");
         Matcher var5 = var4.matcher(var0);
         if (!var5.matches()) {
            return new String[]{"", "", ""};
         }

         String var6 = normalize(var5.group(1));
         String var7 = normalize(var5.group(2));
         String var8 = normalize(var5.group(3));
         return new String[]{var6, var7, var8};
      } else {
         return new String[]{"", "", ""};
      }
   }

   public static int intHash(int var0) {
      var0 = var0 ^ 61 ^ var0 >> 16;
      var0 += var0 << 3;
      var0 ^= var0 >> 4;
      var0 *= 668265261;
      return var0 ^ var0 >> 15;
   }

   public static int getRandom(BlockPos var0, int var1) {
      int var4 = intHash(var1 + 37);
      var4 = intHash(var4 + var0.getX());
      var4 = intHash(var4 + var0.getZ());
      return intHash(var4 + var0.getY());
   }

   public static WorldServer getWorldServer() {
      WorldClient var3 = minecraft.theWorld;
      if (var3 == null) {
         return null;
      }

      if (!minecraft.isIntegratedServerRunning()) {
         return null;
      }

      IntegratedServer var4 = minecraft.getIntegratedServer();
      if (var4 == null) {
         return null;
      }

      WorldProvider var5 = var3.provider;
      if (var5 == null) {
         return null;
      }

      int var6 = var5.getDimensionId();

      try {
         return var4.worldServerForDimension(var6);
      } catch (NullPointerException var8) {
         return null;
      }
   }

   public static int getAvailableProcessors() {
      return availableProcessors;
   }

   public static void updateAvailableProcessors() {
      availableProcessors = Runtime.getRuntime().availableProcessors();
   }

   public static boolean isSingleProcessor() {
      return getAvailableProcessors() <= 1;
   }

   public static boolean isSmoothWorld() {
      return gameSettings.i;
   }

   public static boolean y() {
      return !isSingleProcessor() ? false : gameSettings.lH;
   }

   public static boolean isDynamicFov() {
      return gameSettings.li;
   }

   public static boolean p8() {
      return gameSettings.allowBlockAlternatives;
   }

   public static int getChunkViewDistance() {
      return gameSettings == null ? 10 : gameSettings.renderDistanceChunks;
   }

   public static boolean equals(Object var0, Object var1) {
      return var0 == var1 ? true : (var0 == null ? false : var0.equals(var1));
   }

   public static boolean X(Object var0, Object[] var1) {
      if (var1 == null) {
         return false;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         Object var6 = var1[var5];
         if (equals(var0, var6)) {
            return true;
         }
      }

      return false;
   }

   public static boolean K(Object var0, Object[] var1) {
      if (var1 == null) {
         return false;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         Object var6 = var1[var5];
         if (var0 == var6) {
            return true;
         }
      }

      return false;
   }

   public static String normalize(String var0) {
      return var0 == null ? "" : var0;
   }

   public static void checkDisplaySettings() {
      int var3 = getAntialiasingLevel();
      if (var3 > 0) {
         DisplayMode var4 = Display.getDisplayMode();
         dbg("FSAA Samples: " + var3);

         try {
            Display.destroy();
            Display.setDisplayMode(var4);
            Display.create(new PixelFormat().withDepthBits(24).withSamples(var3));
            Display.setResizable(false);
            Display.setResizable(true);
         } catch (LWJGLException var18) {
            warn("Error setting FSAA: " + var3 + "x");
            var18.printStackTrace();

            try {
               Display.setDisplayMode(var4);
               Display.create(new PixelFormat().withDepthBits(24));
               Display.setResizable(false);
               Display.setResizable(true);
            } catch (LWJGLException var17) {
               var17.printStackTrace();

               try {
                  Display.setDisplayMode(var4);
                  Display.create();
                  Display.setResizable(false);
                  Display.setResizable(true);
               } catch (LWJGLException var16) {
                  var16.printStackTrace();
               }
            }
         }

         if (!Minecraft.isRunningOnMac && getDefaultResourcePack() != null) {
            InputStream var5 = null;
            InputStream var6 = null;

            try {
               var5 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
               var6 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
               if (var5 != null && var6 != null) {
                  Display.setIcon(new ByteBuffer[]{readIconImage(var5), readIconImage(var6)});
               }
            } catch (IOException var14) {
               warn("Error setting window icon: " + var14.getClass().getName() + ": " + var14.getMessage());
            } finally {
               IOUtils.closeQuietly(var5);
               IOUtils.closeQuietly(var6);
            }
         }
      }
   }

   private static ByteBuffer readIconImage(InputStream var0) throws IOException {
      BufferedImage var4 = ImageIO.read(var0);
      int[] var5 = var4.getRGB(0, 0, var4.getWidth(), var4.getHeight(), (int[])null, 0, var4.getWidth());
      ByteBuffer var6 = ByteBuffer.allocate(4 * var5.length);

      for (int var10 : var5) {
         var6.putInt(var10 << 8 | var10 >> 24 & 0xFF);
      }

      ((Buffer)var6).flip();
      return var6;
   }

   public static void checkDisplayMode() {
      try {
         if (minecraft.isFullScreen()) {
            if (fullscreenModeChecked) {
               return;
            }

            fullscreenModeChecked = true;
            desktopModeChecked = false;
            DisplayMode var3 = Display.getDisplayMode();
            Dimension var4 = getFullscreenDimension();
            if (var4 == null) {
               return;
            }

            if (var3.getWidth() == var4.width && var3.getHeight() == var4.height) {
               return;
            }

            DisplayMode var5 = getDisplayMode(var4);
            if (var5 == null) {
               return;
            }

            Display.setDisplayMode(var5);
            minecraft.displayWidth = Display.getDisplayMode().getWidth();
            minecraft.displayHeight = Display.getDisplayMode().getHeight();
            if (minecraft.displayWidth <= 0) {
               minecraft.displayWidth = 1;
            }

            if (minecraft.displayHeight <= 0) {
               minecraft.displayHeight = 1;
            }

            if (minecraft.currentScreen != null) {
               ScaledResolution var6 = new ScaledResolution(minecraft);
               int var7 = var6.getScaledWidth();
               int var8 = var6.getScaledHeight();
               minecraft.currentScreen.setWorldAndResolution(minecraft, var7, var8);
            }

            minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
            updateFramebufferSize();
            Display.setFullscreen(true);
            minecraft.gameSettings.updateVSync();
            GlStateManager.enableTexture2D();
         } else {
            if (desktopModeChecked) {
               return;
            }

            desktopModeChecked = true;
            fullscreenModeChecked = false;
            minecraft.gameSettings.updateVSync();
            Display.update();
            GlStateManager.enableTexture2D();
            Display.setResizable(false);
            Display.setResizable(true);
         }
      } catch (Exception var9) {
         var9.printStackTrace();
         gameSettings.ofFullscreenMode = "Default";
         gameSettings.saveOfOptions();
      }
   }

   public static void updateFramebufferSize() {
      minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
      if (minecraft.entityRenderer != null) {
         minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
      }
   }

   public static Object[] addObjectToArray(Object[] var0, Object var1) {
      if (var0 == null) {
         throw new NullPointerException("The given array is NULL");
      }

      int var5 = var0.length;
      int var6 = var5 + 1;
      Object[] var7 = (Object[])Array.newInstance(var0.getClass().getComponentType(), var6);
      System.arraycopy(var0, 0, var7, 0, var5);
      var7[var5] = var1;
      return var7;
   }

   public static Object[] addObjectToArray(Object[] var0, Object var1, int var2) {
      ArrayList var3 = new ArrayList<>(Arrays.asList(var0));
      var3.add(var2, var1);
      Object[] var4 = (Object[])Array.newInstance(var0.getClass().getComponentType(), var3.size());
      return var3.toArray(var4);
   }

   public static Object[] addObjectsToArray(Object[] var0, Object[] var1) {
      if (var0 == null) {
         throw new NullPointerException("The given array is NULL");
      }

      if (var1.length == 0) {
         return var0;
      }

      int var5 = var0.length;
      int var6 = var5 + var1.length;
      Object[] var7 = (Object[])Array.newInstance(var0.getClass().getComponentType(), var6);
      System.arraycopy(var0, 0, var7, 0, var5);
      System.arraycopy(var1, 0, var7, var5, var1.length);
      return var7;
   }

   public static boolean isCustomItems() {
      return gameSettings.ZZ;
   }

   public static void drawFps() {
      int var2 = Minecraft.B();
      String var3 = getUpdates(minecraft.xQ);
      int var4 = minecraft.renderGlobal.getCountActiveRenderers();
      int var5 = minecraft.renderGlobal.P();
      int var6 = minecraft.renderGlobal.Z();
      String var7 = "" + var2 + " fps, C: " + var4 + ", E: " + var5 + "+" + var6 + ", U: " + var3;
      minecraft.fontRendererObj.D(var7, 2.0F, 2.0F, -2039584);
   }

   private static String getUpdates(String var0) {
      int var4 = var0.indexOf(40);
      if (var4 < 0) {
         return "";
      }

      int var5 = var0.indexOf(32, var4);
      return var5 < 0 ? "" : var0.substring(var4 + 1, var5);
   }

   public static int getBitsOs() {
      String var2 = System.getenv("ProgramFiles(X86)");
      return var2 != null ? 64 : 32;
   }

   public static int getBitsJre() {
      String[] var3 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for (int var4 = 0; var4 < var3.length; var4++) {
         String var5 = var3[var4];
         String var6 = System.getProperty(var5);
         if (var6 != null && var6.contains("64")) {
            return 64;
         }
      }

      return 32;
   }

   public static boolean k() {
      return notify64BitJava;
   }

   public static void setNotify64BitJava(boolean var0) {
      notify64BitJava = var0;
   }

   public static void showGuiMessage(String var0, String var1) {
      GuiMessage var2 = new GuiMessage(minecraft.currentScreen, var0, var1);
      minecraft.displayGuiScreen(var2);
   }

   public static int[] addIntToArray(int[] var0, int var1) {
      return addIntsToArray(var0, new int[]{var1});
   }

   public static int[] addIntsToArray(int[] var0, int[] var1) {
      if (var0 != null && var1 != null) {
         int var5 = var0.length;
         int var6 = var5 + var1.length;
         int[] var7 = new int[var6];
         System.arraycopy(var0, 0, var7, 0, var5);

         for (int var8 = 0; var8 < var1.length; var8++) {
            var7[var8 + var5] = var1[var8];
         }

         return var7;
      } else {
         throw new NullPointerException("The given array is NULL");
      }
   }

   public static DynamicTexture getMojangLogoTexture(DynamicTexture var0) {
      try {
         ResourceLocation var3 = new ResourceLocation("textures/gui/title/mojang.png");
         InputStream var4 = getResourceStream(var3);
         if (var4 == null) {
            return var0;
         }

         BufferedImage var5 = ImageIO.read(var4);
         return var5 == null ? var0 : new DynamicTexture(var5);
      } catch (Exception var7) {
         warn(var7.getClass().getName() + ": " + var7.getMessage());
         return var0;
      }
   }

   public static void writeFile(File var0, String var1) throws IOException {
      FileOutputStream var4 = new FileOutputStream(var0);
      byte[] var5 = var1.getBytes("ASCII");
      var4.write(var5);
      var4.close();
   }

   public static TextureMap getTextureMap() {
      return getMinecraft().getTextureMapBlocks();
   }

   public static boolean isDynamicLights() {
      return gameSettings.ofDynamicLights != 3;
   }

   public static boolean isDynamicLightsFast() {
      return gameSettings.ofDynamicLights == 1;
   }

   public static boolean isDynamicHandLight() {
      return !isDynamicLights() ? false : (isShaders() ? Shaders.x() : true);
   }
}
