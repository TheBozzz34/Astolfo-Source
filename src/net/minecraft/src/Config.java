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

   public static String getVersion() {
      return "OptiFine_1.8.8_HD_U_H8";
   }

   public static String getVersionDebug() {
      StringBuffer stringBuffer = new StringBuffer(32);
      if (isDynamicLights()) {
         stringBuffer.append("DL: ");
         stringBuffer.append(String.valueOf(DynamicLights.getCount()));
         stringBuffer.append(", ");
      }

      stringBuffer.append("OptiFine_1.8.8_HD_U_H8");
      String string = Shaders.getShaderPackName();
      if (string != null) {
         stringBuffer.append(", ");
         stringBuffer.append(string);
      }

      return stringBuffer.toString();
   }

   public static void initGameSettings(GameSettings settings) {
      if (gameSettings == null) {
         gameSettings = settings;
         desktopDisplayMode = Display.getDesktopDisplayMode();
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

      int i = TextureUtils.getGLMaximumTextureSize();
      dbg("Maximum texture size: " + i + "x" + i);
   }

   private static String getBuild() {
      try {
         InputStream inputStream = Config.class.getResourceAsStream("/buildof.txt");
         return inputStream == null ? null : readLines(inputStream)[0];
      } catch (Exception exception) {
         warn("" + exception.getClass().getName() + ": " + exception.getMessage());
         return null;
      }
   }

   public static boolean isFancyFogAvailable() {
      return fancyFogAvailable;
   }

   public static boolean isOcclusionAvailable() {
      return occlusionAvailable;
   }

   public static int getMinecraftVersionInt() {
      if (minecraftVersionInt < 0) {
         String[] strings = tokenize("1.8.8", ".");
         int i = 0;
         if (strings.length > 0) {
            i += 10000 * parseInt(strings[0], 0);
         }

         if (strings.length > 1) {
            i += 100 * parseInt(strings[1], 0);
         }

         if (strings.length > 2) {
            i += 1 * parseInt(strings[2], 0);
         }

         minecraftVersionInt = i;
      }

      return minecraftVersionInt;
   }

   public static String getOpenGlVersionString() {
      GlVersion ver = getGlVersion();
      return "" + ver.getMajor() + "." + ver.getMinor() + "." + ver.getRelease();
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
         String string = GL11.glGetString(7938);
         glVersion = parseGlVersion(string, (GlVersion)null);
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
         String string = GL11.glGetString(35724);
         glslVersion = parseGlVersion(string, (GlVersion)null);
         if (glslVersion == null) {
            glslVersion = new GlVersion(1, 10);
         }
      }

      return glslVersion;
   }

   public static GlVersion parseGlVersion(String versionString, GlVersion def) {
      try {
         if (versionString == null) {
            return def;
         }

         Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
         Matcher matcher2 = pattern.matcher(versionString);
         if (!matcher2.matches()) {
            return def;
         }

         int i = Integer.parseInt(matcher2.group(1));
         int j = Integer.parseInt(matcher2.group(2));
         int k = matcher2.group(4) != null ? Integer.parseInt(matcher2.group(4)) : 0;
         String string = matcher2.group(5);
         return new GlVersion(i, j, k, string);
      } catch (Exception exception) {
         exception.printStackTrace();
         return def;
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
         GlVersion glVersion = getGlVersion();
         if (glVersion.getMajor() >= 3) {
            int i = GL11.glGetInteger(33309);
            if (i > 0) {
               String[] strings = new String[i];

               for (int j = 0; j < i; j++) {
                  strings[j] = GL30.glGetStringi(7939, j);
               }

               return strings;
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      try {
         String string = GL11.glGetString(7939);
         return string.split(" ");
      } catch (Exception exception2) {
         exception2.printStackTrace();
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

   private static void setThreadPriority(String prefix, int priority) {
      try {
         ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
         if (threadGroup == null) {
            return;
         }

         int i = (threadGroup.activeCount() + 10) * 2;
         Thread[] threads = new Thread[i];
         threadGroup.enumerate(threads, false);

         for (int j = 0; j < threads.length; j++) {
            Thread thread = threads[j];
            if (thread != null && thread.getName().startsWith(prefix)) {
               thread.setPriority(priority);
            }
         }
      } catch (Throwable throwable) {
         warn(throwable.getClass().getName() + ": " + throwable.getMessage());
      }
   }

   public static boolean isMinecraftThread() {
      return Thread.currentThread() == minecraftThread;
   }

   public static boolean isMipmaps() {
      return gameSettings.mipmapLevels > 0;
   }

   public static int getMipmapLevels() {
      return gameSettings.mipmapLevels;
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
      float f = getAlphaFuncLevel();
      return f > DEF_ALPHA_FUNC_LEVEL + 1.0E-5F;
   }

   public static float getAlphaFuncLevel() {
      return DEF_ALPHA_FUNC_LEVEL;
   }

   public static boolean isFogFancy() {
      return !isFancyFogAvailable() ? false : gameSettings.ofFogType == 2;
   }

   public static boolean isFogFast() {
      return gameSettings.ofFogType == 1;
   }

   public static boolean isFogOff() {
      return gameSettings.ofFogType == 3;
   }

   public static float getFogStart() {
      return gameSettings.ofFogStart;
   }

   public static void dbg(String s) {
      LOGGER.info("[OptiFine] " + s);
   }

   public static void warn(String s) {
      LOGGER.warn("[OptiFine] " + s);
   }

   public static void error(String s) {
      LOGGER.error("[OptiFine] " + s);
   }

   public static void log(String s) {
      dbg(s);
   }

   public static int getUpdatesPerFrame() {
      return gameSettings.ofChunkUpdates;
   }

   public static boolean isDynamicUpdates() {
      return gameSettings.ofChunkUpdatesDynamic;
   }

   public static boolean isRainFancy() {
      return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
   }

   public static boolean isRainOff() {
      return gameSettings.ofRain == 3;
   }

   public static boolean isCloudsFancy() {
      return gameSettings.ofClouds != 0
         ? gameSettings.ofClouds == 2
         : (
            isShaders() && !Shaders.shaderPackClouds.isDefault()
               ? Shaders.shaderPackClouds.isFancy()
               : (texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics)
         );
   }

   public static boolean isCloudsOff() {
      return gameSettings.ofClouds != 0
         ? gameSettings.ofClouds == 3
         : (isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isOff() : (texturePackClouds != 0 ? texturePackClouds == 3 : false));
   }

   public static void updateTexturePackClouds() {
      texturePackClouds = 0;
      IResourceManager iResourceManager = getResourceManager();
      if (iResourceManager != null) {
         try {
            InputStream inputStream = iResourceManager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
            if (inputStream == null) {
               return;
            }

            Properties properties2 = new Properties();
            properties2.load(inputStream);
            inputStream.close();
            String string = properties2.getProperty("clouds");
            if (string == null) {
               return;
            }

            dbg("Texture pack clouds: " + string);
            string = string.toLowerCase();
            if (string.equals("fast")) {
               texturePackClouds = 1;
            }

            if (string.equals("fancy")) {
               texturePackClouds = 2;
            }

            if (string.equals("off")) {
               texturePackClouds = 3;
            }
         } catch (Exception exception) {
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

   public static boolean isDroppedItemsFancy() {
      return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
   }

   public static int limit(int val, int minIn, int maxIn) {
      return val < minIn ? minIn : (val > maxIn ? maxIn : val);
   }

   public static float limit(float val, float minIn, float maxIn) {
      return val < minIn ? minIn : (val > maxIn ? maxIn : val);
   }

   public static double limit(double val, double minIn, double maxIn) {
      return val < minIn ? minIn : (val > maxIn ? maxIn : val);
   }

   public static float limitTo1(float val) {
      return val < 0.0F ? 0.0F : (val > 1.0F ? 1.0F : val);
   }

   public static boolean W_boolean() {
      return gameSettings.ofAnimatedWater != 2;
   }

   public static boolean isGeneratedWater() {
      return gameSettings.ofAnimatedWater == 1;
   }

   public static boolean isAnimatedPortal() {
      return gameSettings.ofAnimatedPortal;
   }

   public static boolean isAnimatedLava() {
      return gameSettings.ofAnimatedLava != 2;
   }

   public static boolean isGeneratedLava() {
      return gameSettings.ofAnimatedLava == 1;
   }

   public static boolean isAnimatedFire() {
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

   public static boolean isPortalParticles() {
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

   public static String arrayToString(Object[] arr) {
      if (arr == null) {
         return "";
      }

      StringBuffer stringBuffer = new StringBuffer(arr.length * 5);

      for (int i = 0; i < arr.length; i++) {
         Object object = arr[i];
         if (i > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append(String.valueOf(object));
      }

      return stringBuffer.toString();
   }

   public static String arrayToString(int[] arr) {
      if (arr == null) {
         return "";
      }

      StringBuffer stringBuffer = new StringBuffer(arr.length * 5);

      for (int i = 0; i < arr.length; i++) {
         int j = arr[i];
         if (i > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append(String.valueOf(j));
      }

      return stringBuffer.toString();
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

   public static InputStream getResourceStream(ResourceLocation location) throws IOException {
      return getResourceStream(minecraft.getResourceManager(), location);
   }

   public static InputStream getResourceStream(IResourceManager resourceManager, ResourceLocation location) throws IOException {
      IResource iResource = resourceManager.getResource(location);
      return iResource == null ? null : iResource.getInputStream();
   }

   public static IResource getResource(ResourceLocation location) throws IOException {
      return minecraft.getResourceManager().getResource(location);
   }

   public static boolean hasResource(ResourceLocation location) {
      IResourcePack iResourcePack = getDefiningResourcePack(location);
      return iResourcePack != null;
   }

   public static boolean hasResource(IResourceManager resourceManager, ResourceLocation location) {
      try {
         IResource iResource = resourceManager.getResource(location);
         return iResource != null;
      } catch (IOException ioException) {
         return false;
      }
   }

   public static IResourcePack[] getResourcePacks() {
      ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
      List list = resourcePackRepository.getRepositoryEntries();
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         arrayList.add(((ResourcePackRepository.Entry)object).getResourcePack());
      }

      if (resourcePackRepository.getResourcePackInstance() != null) {
         arrayList.add(resourcePackRepository.getResourcePackInstance());
      }

      return (net.minecraft.client.resources.IResourcePack[])(arrayList.toArray(new IResourcePack[arrayList.size()]));
   }

   public static String getResourcePackNames() {
      if (minecraft.getResourcePackRepository() == null) {
         return "";
      }

      IResourcePack[] iResourcePacks = getResourcePacks();
      if (iResourcePacks.length <= 0) {
         return getDefaultResourcePack().getPackName();
      }

      String[] strings = new String[iResourcePacks.length];

      for (int i = 0; i < iResourcePacks.length; i++) {
         strings[i] = iResourcePacks[i].getPackName();
      }

      return arrayToString(strings);
   }

   public static DefaultResourcePack getDefaultResourcePack() {
      if (defaultResourcePackLazy == null) {
         Minecraft minecraft = Minecraft.getMinecraft();
         defaultResourcePackLazy = (DefaultResourcePack)Reflector.getFieldValue(minecraft, Reflector.Minecraft_defaultResourcePack);
         if (defaultResourcePackLazy == null) {
            ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
            if (resourcePackRepository != null) {
               defaultResourcePackLazy = (DefaultResourcePack)resourcePackRepository.rprDefaultResourcePack;
            }
         }
      }

      return defaultResourcePackLazy;
   }

   public static boolean isFromDefaultResourcePack(ResourceLocation loc) {
      IResourcePack iResourcePack = getDefiningResourcePack(loc);
      return iResourcePack == getDefaultResourcePack();
   }

   public static IResourcePack getDefiningResourcePack(ResourceLocation location) {
      ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
      IResourcePack iResourcePack = resourcePackRepository.getResourcePackInstance();
      if (iResourcePack != null && iResourcePack.resourceExists(location)) {
         return iResourcePack;
      }

      List list = (List)Reflector.getFieldValue(resourcePackRepository, Reflector.ResourcePackRepository_repositoryEntries);
      if (list != null) {
         for (int i = list.size() - 1; i >= 0; i--) {
            ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)list.get(i);
            IResourcePack iResourcePack2 = entry.getResourcePack();
            if (iResourcePack2.resourceExists(location)) {
               return iResourcePack2;
            }
         }
      }

      return getDefaultResourcePack().resourceExists(location) ? getDefaultResourcePack() : null;
   }

   public static RenderGlobal getRenderGlobal() {
      return minecraft.renderGlobal;
   }

   public static boolean isBetterGrass() {
      return gameSettings.ofBetterGrass != 3;
   }

   public static boolean isBetterGrassFancy() {
      return gameSettings.ofBetterGrass == 2;
   }

   public static boolean isWeatherEnabled() {
      return gameSettings.ofWeather;
   }

   public static boolean isSkyEnabled() {
      return gameSettings.ofSky;
   }

   public static boolean isSunMoonEnabled() {
      return gameSettings.ofSunMoon;
   }

   public static boolean isSunTexture() {
      return !isSunMoonEnabled() ? false : !isShaders() || Shaders.i_boolean();
   }

   public static boolean isMoonTexture() {
      return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isMoon();
   }

   public static boolean isVignetteEnabled() {
      return isShaders() && !Shaders.isVignette() ? false : (gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2);
   }

   public static boolean isStarsEnabled() {
      return gameSettings.ofStars;
   }

   public static void sleep(long ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public static boolean isTimeDayOnly() {
      return gameSettings.ofTime == 1;
   }

   public static boolean isTimeDefault() {
      return gameSettings.ofTime == 0;
   }

   public static boolean isTimeNightOnly() {
      return gameSettings.ofTime == 2;
   }

   public static boolean isClearWater() {
      return gameSettings.ofClearWater;
   }

   public static int getAnisotropicFilterLevel() {
      return gameSettings.ofAfLevel;
   }

   public static boolean isAnisotropicFiltering() {
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

   public static boolean between(int val, int minIn, int maxIn) {
      return val >= minIn && val <= maxIn;
   }

   public static boolean isDrippingWaterLava() {
      return gameSettings.ofDrippingWaterLava;
   }

   public static boolean isBetterSnow() {
      return gameSettings.ofBetterSnow;
   }

   public static Dimension getFullscreenDimension() {
      if (desktopDisplayMode == null) {
         return null;
      }

      if (gameSettings == null) {
         return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
      }

      String string = gameSettings.ofFullscreenMode;
      if (string.equals("Default")) {
         return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
      }

      String[] strings = tokenize(string, " x");
      return strings.length < 2
         ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight())
         : new Dimension(parseInt(strings[0], -1), parseInt(strings[1], -1));
   }

   public static int parseInt(String str, int defVal) {
      try {
         if (str == null) {
            return defVal;
         }

         str = str.trim();
         return Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
         return defVal;
      }
   }

   public static float parseFloat(String str, float defVal) {
      try {
         if (str == null) {
            return defVal;
         }

         str = str.trim();
         return Float.parseFloat(str);
      } catch (NumberFormatException numberFormatException) {
         return defVal;
      }
   }

   public static boolean parseBoolean(String str, boolean defVal) {
      try {
         if (str == null) {
            return defVal;
         }

         str = str.trim();
         return Boolean.parseBoolean(str);
      } catch (NumberFormatException numberFormatException) {
         return defVal;
      }
   }

   public static String[] tokenize(String str, String delim) {
      StringTokenizer stringTokenizer = new StringTokenizer(str, delim);
      ArrayList arrayList = new ArrayList();

      while (stringTokenizer.hasMoreTokens()) {
         String string = stringTokenizer.nextToken();
         arrayList.add(string);
      }

      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }

   public static DisplayMode getDesktopDisplayMode() {
      return desktopDisplayMode;
   }

   public static DisplayMode[] getDisplayModes() {
      if (displayModes == null) {
         try {
            DisplayMode[] displayModes2 = Display.getAvailableDisplayModes();
            Set set = getDisplayModeDimensions(displayModes2);
            ArrayList arrayList = new ArrayList();

            for (Object object : set) {
               Dimension dimension = (Dimension)object;
               DisplayMode[] displayModes3 = getDisplayModes(displayModes2, dimension);
               DisplayMode displayMode = getDisplayMode(displayModes3, desktopDisplayMode);
               if (displayMode != null) {
                  arrayList.add(displayMode);
               }
            }

            DisplayMode[] displayModes4 = (org.lwjgl.opengl.DisplayMode[])(arrayList.toArray(new DisplayMode[arrayList.size()]));
            Arrays.sort(displayModes4, new DisplayModeComparator());
            return displayModes4;
         } catch (Exception exception) {
            exception.printStackTrace();
            displayModes = new DisplayMode[]{desktopDisplayMode};
         }
      }

      return displayModes;
   }

   public static DisplayMode getLargestDisplayMode() {
      DisplayMode[] displayModes = getDisplayModes();
      if (displayModes != null && displayModes.length >= 1) {
         DisplayMode displayMode = displayModes[displayModes.length - 1];
         return desktopDisplayMode.getWidth() > displayMode.getWidth()
            ? desktopDisplayMode
            : (desktopDisplayMode.getWidth() == displayMode.getWidth() && desktopDisplayMode.getHeight() > displayMode.getHeight() ? desktopDisplayMode : displayMode);
      } else {
         return desktopDisplayMode;
      }
   }

   private static Set<Dimension> getDisplayModeDimensions(DisplayMode[] modes) {
      HashSet hashSet = new HashSet();

      for (int i = 0; i < modes.length; i++) {
         DisplayMode displayMode = modes[i];
         Dimension dimension = new Dimension(displayMode.getWidth(), displayMode.getHeight());
         hashSet.add(dimension);
      }

      return hashSet;
   }

   private static DisplayMode[] getDisplayModes(DisplayMode[] modes, Dimension dim) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < modes.length; i++) {
         DisplayMode displayMode = modes[i];
         if (displayMode.getWidth() == dim.getWidth() && displayMode.getHeight() == dim.getHeight()) {
            arrayList.add(displayMode);
         }
      }

      return (org.lwjgl.opengl.DisplayMode[])(arrayList.toArray(new DisplayMode[arrayList.size()]));
   }

   private static DisplayMode getDisplayMode(DisplayMode[] modes, DisplayMode desktopMode) {
      if (desktopMode != null) {
         for (int i = 0; i < modes.length; i++) {
            DisplayMode displayMode = modes[i];
            if (displayMode.getBitsPerPixel() == desktopMode.getBitsPerPixel() && displayMode.getFrequency() == desktopMode.getFrequency()) {
               return displayMode;
            }
         }
      }

      if (modes.length <= 0) {
         return null;
      }

      Arrays.sort(modes, new DisplayModeComparator());
      return modes[modes.length - 1];
   }

   public static String[] getDisplayModeNames() {
      DisplayMode[] displayModes = getDisplayModes();
      String[] strings = new String[displayModes.length];

      for (int i = 0; i < displayModes.length; i++) {
         DisplayMode displayMode = displayModes[i];
         String string = "" + displayMode.getWidth() + "x" + displayMode.getHeight();
         strings[i] = string;
      }

      return strings;
   }

   public static DisplayMode getDisplayMode(Dimension dim) throws LWJGLException {
      DisplayMode[] displayModes = getDisplayModes();

      for (int i = 0; i < displayModes.length; i++) {
         DisplayMode displayMode = displayModes[i];
         if (displayMode.getWidth() == dim.width && displayMode.getHeight() == dim.height) {
            return displayMode;
         }
      }

      return desktopDisplayMode;
   }

   public static boolean isAnimatedTerrain() {
      return gameSettings.ofAnimatedTerrain;
   }

   public static boolean isAnimatedTextures() {
      return gameSettings.ofAnimatedTextures;
   }

   public static boolean isSwampColors() {
      return gameSettings.ofSwampColors;
   }

   public static boolean isRandomMobs() {
      return gameSettings.ofRandomMobs;
   }

   public static void checkGlError(String loc) {
      int i = GL11.glGetError();
      if (i != 0) {
         String string = GLU.gluErrorString(i);
         error("OpenGlError: " + i + " (" + string + "), at: " + loc);
      }
   }

   public static boolean isSmoothBiomes() {
      return gameSettings.ofSmoothBiomes;
   }

   public static boolean isCustomColors() {
      return gameSettings.ofCustomColors;
   }

   public static boolean isCustomSky() {
      return gameSettings.ofCustomSky;
   }

   public static boolean isCustomFonts() {
      return gameSettings.ofCustomFonts;
   }

   public static boolean isShowCapes() {
      return gameSettings.ofShowCapes;
   }

   public static boolean isConnectedTextures() {
      return gameSettings.ofConnectedTextures != 3;
   }

   public static boolean isNaturalTextures() {
      return gameSettings.ofNaturalTextures;
   }

   public static boolean isConnectedTexturesFancy() {
      return gameSettings.ofConnectedTextures == 2;
   }

   public static boolean isFastRender() {
      return gameSettings.ofFastRender;
   }

   public static boolean isTranslucentBlocksFancy() {
      return gameSettings.ofTranslucentBlocks == 0 ? gameSettings.fancyGraphics : gameSettings.ofTranslucentBlocks == 2;
   }

   public static boolean isShaders() {
      return Shaders.shaderPackLoaded;
   }

   public static String[] readLines(File file) throws IOException {
      FileInputStream fis = new FileInputStream(file);
      return readLines(fis);
   }

   public static String[] readLines(InputStream is) throws IOException {
      ArrayList arrayList = new ArrayList();
      InputStreamReader inputStreamReader = new InputStreamReader(is, "ASCII");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

      while (true) {
         String string = bufferedReader.readLine();
         if (string == null) {
            return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
         }

         arrayList.add(string);
      }
   }

   public static String readFile(File file) throws IOException {
      FileInputStream fileInputStream = new FileInputStream(file);
      return readInputStream(fileInputStream, "ASCII");
   }

   public static String readInputStream(InputStream in) throws IOException {
      return readInputStream(in, "ASCII");
   }

   public static String readInputStream(InputStream in, String encoding) throws IOException {
      InputStreamReader inputStreamReader = new InputStreamReader(in, encoding);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuffer stringBuffer = new StringBuffer();

      while (true) {
         String string = bufferedReader.readLine();
         if (string == null) {
            return stringBuffer.toString();
         }

         stringBuffer.append(string);
         stringBuffer.append("\n");
      }
   }

   public static byte[] readAll(InputStream is) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] bytes = new byte[1024];

      while (true) {
         int i = is.read(bytes);
         if (i < 0) {
            is.close();
            return byteArrayOutputStream.toByteArray();
         }

         byteArrayOutputStream.write(bytes, 0, i);
      }
   }

   public static GameSettings getGameSettings() {
      return gameSettings;
   }

   public static String getNewRelease() {
      return newRelease;
   }

   public static void setNewRelease(String newReleaseIn) {
      newRelease = newReleaseIn;
   }

   public static int compareRelease(String rel1, String rel2) {
      String[] strings = splitRelease(rel1);
      String[] strings2 = splitRelease(rel2);
      String string = strings[0];
      String string2 = strings2[0];
      if (!string.equals(string2)) {
         return string.compareTo(string2);
      }

      int i = parseInt(strings[1], -1);
      int j = parseInt(strings2[1], -1);
      if (i != j) {
         return i - j;
      }

      String string3 = strings[2];
      String string4 = strings2[2];
      if (!string3.equals(string4)) {
         if (string3.isEmpty()) {
            return 1;
         }

         if (string4.isEmpty()) {
            return -1;
         }
      }

      return string3.compareTo(string4);
   }

   private static String[] splitRelease(String relStr) {
      if (relStr != null && relStr.length() > 0) {
         Pattern pattern = Pattern.compile("([A-Z])([0-9]+)(.*)");
         Matcher matcher2 = pattern.matcher(relStr);
         if (!matcher2.matches()) {
            return new String[]{"", "", ""};
         }

         String string = normalize(matcher2.group(1));
         String string2 = normalize(matcher2.group(2));
         String string3 = normalize(matcher2.group(3));
         return new String[]{string, string2, string3};
      } else {
         return new String[]{"", "", ""};
      }
   }

   public static int intHash(int x) {
      x = x ^ 61 ^ x >> 16;
      x += x << 3;
      x ^= x >> 4;
      x *= 668265261;
      return x ^ x >> 15;
   }

   public static int getRandom(BlockPos blockPos, int face) {
      int i = intHash(face + 37);
      i = intHash(i + blockPos.getX());
      i = intHash(i + blockPos.getZ());
      return intHash(i + blockPos.getY());
   }

   public static WorldServer getWorldServer() {
      WorldClient worldClient = minecraft.theWorld;
      if (worldClient == null) {
         return null;
      }

      if (!minecraft.isIntegratedServerRunning()) {
         return null;
      }

      IntegratedServer integratedServer = minecraft.getIntegratedServer();
      if (integratedServer == null) {
         return null;
      }

      WorldProvider worldProvider = worldClient.provider;
      if (worldProvider == null) {
         return null;
      }

      int i = worldProvider.getDimensionId();

      try {
         return integratedServer.worldServerForDimension(i);
      } catch (NullPointerException nullPointerException) {
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
      return gameSettings.ofSmoothWorld;
   }

   public static boolean y() {
      return !isSingleProcessor() ? false : gameSettings.ofLazyChunkLoading;
   }

   public static boolean isDynamicFov() {
      return gameSettings.ofDynamicFov;
   }

   public static boolean isAlternateBlocks() {
      return gameSettings.allowBlockAlternatives;
   }

   public static int getChunkViewDistance() {
      return gameSettings == null ? 10 : gameSettings.renderDistanceChunks;
   }

   public static boolean equals(Object o1, Object o2) {
      return o1 == o2 ? true : (o1 == null ? false : o1.equals(o2));
   }

   public static boolean equalsOne(Object a, Object[] bs) {
      if (bs == null) {
         return false;
      }

      for (int i = 0; i < bs.length; i++) {
         Object object = bs[i];
         if (equals(a, object)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isSameOne(Object a, Object[] bs) {
      if (bs == null) {
         return false;
      }

      for (int i = 0; i < bs.length; i++) {
         Object object = bs[i];
         if (a == object) {
            return true;
         }
      }

      return false;
   }

   public static String normalize(String s) {
      return s == null ? "" : s;
   }

   public static void checkDisplaySettings() {
      int i = getAntialiasingLevel();
      if (i > 0) {
         DisplayMode displayMode = Display.getDisplayMode();
         dbg("FSAA Samples: " + i);

         try {
            Display.destroy();
            Display.setDisplayMode(displayMode);
            Display.create(new PixelFormat().withDepthBits(24).withSamples(i));
            Display.setResizable(false);
            Display.setResizable(true);
         } catch (LWJGLException lwjglException) {
            warn("Error setting FSAA: " + i + "x");
            lwjglException.printStackTrace();

            try {
               Display.setDisplayMode(displayMode);
               Display.create(new PixelFormat().withDepthBits(24));
               Display.setResizable(false);
               Display.setResizable(true);
            } catch (LWJGLException lwjglException2) {
               lwjglException2.printStackTrace();

               try {
                  Display.setDisplayMode(displayMode);
                  Display.create();
                  Display.setResizable(false);
                  Display.setResizable(true);
               } catch (LWJGLException lwjglException3) {
                  lwjglException3.printStackTrace();
               }
            }
         }

         if (!Minecraft.isRunningOnMac && getDefaultResourcePack() != null) {
            InputStream inputStream = null;
            InputStream inputStream2 = null;

            try {
               inputStream = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
               inputStream2 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
               if (inputStream != null && inputStream2 != null) {
                  Display.setIcon(new ByteBuffer[]{readIconImage(inputStream), readIconImage(inputStream2)});
               }
            } catch (IOException ioException) {
               warn("Error setting window icon: " + ioException.getClass().getName() + ": " + ioException.getMessage());
            } finally {
               IOUtils.closeQuietly(inputStream);
               IOUtils.closeQuietly(inputStream2);
            }
         }
      }
   }

   private static ByteBuffer readIconImage(InputStream is) throws IOException {
      BufferedImage bufferedImage = ImageIO.read(is);
      int[] ints = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[])null, 0, bufferedImage.getWidth());
      ByteBuffer byteBuffer = ByteBuffer.allocate(4 * ints.length);

      for (int i : ints) {
         byteBuffer.putInt(i << 8 | i >> 24 & 0xFF);
      }

      ((Buffer)byteBuffer).flip();
      return byteBuffer;
   }

   public static void checkDisplayMode() {
      try {
         if (minecraft.isFullScreen()) {
            if (fullscreenModeChecked) {
               return;
            }

            fullscreenModeChecked = true;
            desktopModeChecked = false;
            DisplayMode displayMode = Display.getDisplayMode();
            Dimension dimension = getFullscreenDimension();
            if (dimension == null) {
               return;
            }

            if (displayMode.getWidth() == dimension.width && displayMode.getHeight() == dimension.height) {
               return;
            }

            DisplayMode displayMode2 = getDisplayMode(dimension);
            if (displayMode2 == null) {
               return;
            }

            Display.setDisplayMode(displayMode2);
            minecraft.displayWidth = Display.getDisplayMode().getWidth();
            minecraft.displayHeight = Display.getDisplayMode().getHeight();
            if (minecraft.displayWidth <= 0) {
               minecraft.displayWidth = 1;
            }

            if (minecraft.displayHeight <= 0) {
               minecraft.displayHeight = 1;
            }

            if (minecraft.currentScreen != null) {
               ScaledResolution scaledResolution = new ScaledResolution(minecraft);
               int i = scaledResolution.getScaledWidth();
               int j = scaledResolution.getScaledHeight();
               minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
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
      } catch (Exception exception) {
         exception.printStackTrace();
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

   public static Object[] addObjectToArray(Object[] arr, Object obj) {
      if (arr == null) {
         throw new NullPointerException("The given array is NULL");
      }

      int i = arr.length;
      int j = i + 1;
      Object[] objects = (Object[])Array.newInstance(arr.getClass().getComponentType(), j);
      System.arraycopy(arr, 0, objects, 0, i);
      objects[i] = obj;
      return objects;
   }

   public static Object[] addObjectToArray(Object[] arr, Object obj, int index) {
      ArrayList arrayList = new ArrayList<>(Arrays.asList(arr));
      arrayList.add(index, obj);
      Object[] objects = (Object[])Array.newInstance(arr.getClass().getComponentType(), arrayList.size());
      return arrayList.toArray(objects);
   }

   public static Object[] addObjectsToArray(Object[] arr, Object[] objs) {
      if (arr == null) {
         throw new NullPointerException("The given array is NULL");
      }

      if (objs.length == 0) {
         return arr;
      }

      int i = arr.length;
      int j = i + objs.length;
      Object[] objects = (Object[])Array.newInstance(arr.getClass().getComponentType(), j);
      System.arraycopy(arr, 0, objects, 0, i);
      System.arraycopy(objs, 0, objects, i, objs.length);
      return objects;
   }

   public static boolean isCustomItems() {
      return gameSettings.ofCustomItems;
   }

   public static void drawFps() {
      int i = Minecraft.getDebugFPS();
      String string = getUpdates(minecraft.debug);
      int j = minecraft.renderGlobal.getCountActiveRenderers();
      int k = minecraft.renderGlobal.getCountEntitiesRendered();
      int l = minecraft.renderGlobal.getCountTileEntitiesRendered();
      String string2 = "" + i + " fps, C: " + j + ", E: " + k + "+" + l + ", U: " + string;
      minecraft.fontRendererObj.drawStringNoShadow(string2, 2.0F, 2.0F, -2039584);
   }

   private static String getUpdates(String str) {
      int i = str.indexOf(40);
      if (i < 0) {
         return "";
      }

      int j = str.indexOf(32, i);
      return j < 0 ? "" : str.substring(i + 1, j);
   }

   public static int getBitsOs() {
      String string = System.getenv("ProgramFiles(X86)");
      return string != null ? 64 : 32;
   }

   public static int getBitsJre() {
      String[] strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         String string2 = System.getProperty(string);
         if (string2 != null && string2.contains("64")) {
            return 64;
         }
      }

      return 32;
   }

   public static boolean isNotify64BitJava() {
      return notify64BitJava;
   }

   public static void setNotify64BitJava(boolean flag) {
      notify64BitJava = flag;
   }

   public static void showGuiMessage(String line1, String line2) {
      GuiMessage gui = new GuiMessage(minecraft.currentScreen, line1, line2);
      minecraft.displayGuiScreen(gui);
   }

   public static int[] addIntToArray(int[] intArray, int intValue) {
      return addIntsToArray(intArray, new int[]{intValue});
   }

   public static int[] addIntsToArray(int[] intArray, int[] copyFrom) {
      if (intArray != null && copyFrom != null) {
         int i = intArray.length;
         int j = i + copyFrom.length;
         int[] ints = new int[j];
         System.arraycopy(intArray, 0, ints, 0, i);

         for (int k = 0; k < copyFrom.length; k++) {
            ints[k + i] = copyFrom[k];
         }

         return ints;
      } else {
         throw new NullPointerException("The given array is NULL");
      }
   }

   public static DynamicTexture getMojangLogoTexture(DynamicTexture texDefault) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation("textures/gui/title/mojang.png");
         InputStream inputStream = getResourceStream(resourceLocation);
         if (inputStream == null) {
            return texDefault;
         }

         BufferedImage bufferedImage = ImageIO.read(inputStream);
         return bufferedImage == null ? texDefault : new DynamicTexture(bufferedImage);
      } catch (Exception exception) {
         warn(exception.getClass().getName() + ": " + exception.getMessage());
         return texDefault;
      }
   }

   public static void writeFile(File file, String str) throws IOException {
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      byte[] bytes = str.getBytes("ASCII");
      fileOutputStream.write(bytes);
      fileOutputStream.close();
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
      return !isDynamicLights() ? false : (isShaders() ? Shaders.isDynamicHandLight() : true);
   }
}
