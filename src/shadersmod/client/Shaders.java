package shadersmod.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.Lang;
import net.minecraft.src.PropertiesOrdered;
import net.minecraft.src.Reflector;
import net.minecraft.src.StrUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import shadersmod.common.SMCLog;
import shadersmod.uniform.ShaderUniformFloat4;
import shadersmod.uniform.ShaderUniformInt;

public class Shaders {
   public static Minecraft mc;
   public static EntityRenderer entityRenderer;
   public static boolean B5 = false;
   public static boolean T = false;
   public static ContextCapabilities capabilities;
   public static String glVersionString;
   public static String glVendorString;
   public static String glRendererString;
   public static boolean hasGlGenMipmap = false;
   public static boolean Pu = false;
   public static int numberResetDisplayList = 0;
   public static boolean needResetModels = false;
   private static int O9 = 0;
   private static int eK = 0;
   public static int renderWidth = 0;
   public static int renderHeight = 0;
   public static boolean isRenderingWorld = false;
   public static boolean isRenderingSky = false;
   public static boolean es = false;
   public static boolean Oo = false;
   public static boolean isShadowPass = false;
   public static boolean uH;
   private static boolean t;
   public static boolean renderItemKeepDepthMask = false;
   public static boolean itemToRenderMainTranslucent = false;
   public static float[] sunPosition = new float[4];
   public static float[] moonPosition = new float[4];
   public static float[] shadowLightPosition = new float[4];
   public static float[] upPosition = new float[4];
   public static float[] shadowLightPositionVector = new float[4];
   public static float[] upPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
   public static float[] sunPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
   public static float[] moonPosModelView = new float[]{0.0F, -100.0F, 0.0F, 0.0F};
   private static float[] tempMat = new float[16];
   public static float clearColorR;
   public static float clearColorG;
   public static float clearColorB;
   public static float skyColorR;
   public static float skyColorG;
   public static float skyColorB;
   public static long Ol = 0L;
   public static long u2 = 0L;
   public static long B9 = 0L;
   public static float celestialAngle = 0.0F;
   public static float sunAngle = 0.0F;
   public static float shadowAngle = 0.0F;
   public static int A = 0;
   public static long ug = 0L;
   public static long BU = 0L;
   public static long diffSystemTime = 0L;
   public static int uy = 0;
   public static float frameTime = 0.0F;
   public static float frameTimeCounter = 0.0F;
   public static int OT = 0;
   public static float rainStrength = 0.0F;
   public static float wetness = 0.0F;
   public static float wetnessHalfLife = 600.0F;
   public static float drynessHalfLife = 200.0F;
   public static float eyeBrightnessHalflife = 10.0F;
   public static boolean C = false;
   public static int u1 = 0;
   public static int e1 = 0;
   public static float eyeBrightnessFadeX = 0.0F;
   public static float eyeBrightnessFadeY = 0.0F;
   public static float eyePosY = 0.0F;
   public static float centerDepth = 0.0F;
   public static float centerDepthSmooth = 0.0F;
   public static float centerDepthSmoothHalflife = 1.0F;
   public static boolean centerDepthSmoothEnabled = false;
   public static int M = 1;
   public static float nightVision = 0.0F;
   public static float blindness = 0.0F;
   public static boolean m = false;
   public static boolean lightmapEnabled = false;
   public static boolean fogEnabled = true;
   public static int entityAttrib = 10;
   public static int midTexCoordAttrib = 11;
   public static int tangentAttrib = 12;
   public static boolean Bp = false;
   public static boolean u_ = false;
   public static boolean uv = false;
   public static boolean eg = false;
   public static boolean progUseEntityAttrib = false;
   public static boolean progUseMidTexCoordAttrib = false;
   public static boolean progUseTangentAttrib = false;
   public static int atlasSizeX = 0;
   public static int atlasSizeY = 0;
   public static ShaderUniformFloat4 uniformEntityColor = new ShaderUniformFloat4("entityColor");
   public static ShaderUniformInt uniformEntityId = new ShaderUniformInt("entityId");
   public static ShaderUniformInt uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
   public static double previousCameraPositionX;
   public static double previousCameraPositionY;
   public static double previousCameraPositionZ;
   public static double cameraPositionX;
   public static double cameraPositionY;
   public static double cameraPositionZ;
   public static int OO = 0;
   public static boolean R = false;
   public static int Ow = 1024;
   public static int e5 = 1024;
   public static int eS = 1024;
   public static int uc = 1024;
   public static float shadowMapFOV = 90.0F;
   public static float shadowMapHalfPlane = 160.0F;
   public static boolean Bj = true;
   public static float shadowDistanceRenderMul = -1.0F;
   public static int uU = 0;
   public static int er;
   public static boolean shouldSkipDefaultShadow = false;
   public static boolean waterShadowEnabled = false;
   public static final int uY = 8;
   public static final int uz = 8;
   public static final int OL = 3;
   public static final int OE = 8;
   public static final int F = 2;
   public static int usedColorBuffers = 0;
   public static int ew = 0;
   public static int uQ = 0;
   public static int OF = 0;
   public static int uo = 0;
   public static int Oh = 0;
   public static int OU = 0;
   public static int n = 0;
   private static int[] gbuffersFormat = new int[8];
   private static boolean[] gbuffersClear = new boolean[8];
   public static int activeProgram = 0;
   public static final int W = 0;
   public static final int u9 = 1;
   public static final int et = 2;
   public static final int eC = 3;
   public static final int S = 4;
   public static final int ul = 5;
   public static final int d = 6;
   public static final int e7 = 7;
   public static final int ei = 8;
   public static final int BX = 9;
   public static final int O = 10;
   public static final int uR = 11;
   public static final int BL = 12;
   public static final int eG = 13;
   public static final int ud = 14;
   public static final int eu = 15;
   public static final int BV = 16;
   public static final int ey = 17;
   public static final int u7 = 18;
   public static final int eT = 19;
   public static final int r = 20;
   public static final int l = 21;
   public static final int ON = 22;
   public static final int eO = 23;
   public static final int uk = 24;
   public static final int OG = 25;
   public static final int u0 = 26;
   public static final int Bw = 27;
   public static final int uG = 28;
   public static final int v = 29;
   public static final int g = 30;
   public static final int eA = 31;
   public static final int Ox = 32;
   public static final int Bz = 33;
   public static final int Be = 8;
   private static final String[] programNames = new String[]{
      "",
      "gbuffers_basic",
      "gbuffers_textured",
      "gbuffers_textured_lit",
      "gbuffers_skybasic",
      "gbuffers_skytextured",
      "gbuffers_clouds",
      "gbuffers_terrain",
      "gbuffers_terrain_solid",
      "gbuffers_terrain_cutout_mip",
      "gbuffers_terrain_cutout",
      "gbuffers_damagedblock",
      "gbuffers_water",
      "gbuffers_block",
      "gbuffers_beaconbeam",
      "gbuffers_item",
      "gbuffers_entities",
      "gbuffers_armor_glint",
      "gbuffers_spidereyes",
      "gbuffers_hand",
      "gbuffers_weather",
      "composite",
      "composite1",
      "composite2",
      "composite3",
      "composite4",
      "composite5",
      "composite6",
      "composite7",
      "final",
      "shadow",
      "shadow_solid",
      "shadow_cutout"
   };
   private static final int[] programBackups = new int[]{0, 0, 1, 2, 1, 2, 2, 3, 7, 7, 7, 7, 7, 7, 2, 3, 3, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30};
   public static int[] programsID = new int[33];
   private static int[] programsRef = new int[33];
   private static int B8 = 0;
   private static String[] programsDrawBufSettings = new String[33];
   private static String eq = null;
   public static IntBuffer[] programsDrawBuffers = new IntBuffer[33];
   public static IntBuffer activeDrawBuffers = null;
   private static String[] programsColorAtmSettings = new String[33];
   private static String eV = null;
   private static String Bq = null;
   private static int[] programsCompositeMipmapSetting = new int[33];
   private static int OA = 0;
   private static int activeCompositeMipmapSetting = 0;
   public static Properties loadedShaders = null;
   public static Properties shadersConfig = null;
   public static ITextureObject defaultTexture = null;
   public static boolean en = false;
   public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
   public static boolean[] shadowMipmapEnabled = new boolean[2];
   public static boolean[] shadowFilterNearest = new boolean[2];
   public static boolean[] shadowColorMipmapEnabled = new boolean[8];
   public static boolean[] shadowColorFilterNearest = new boolean[8];
   public static boolean configTweakBlockDamage = true;
   public static boolean configCloudShadow = true;
   public static float configHandDepthMul = 0.125F;
   public static float configRenderResMul = 1.0F;
   public static float configShadowResMul = 1.0F;
   public static int configTexMinFilB = 0;
   public static int configTexMinFilN = 0;
   public static int configTexMinFilS = 0;
   public static int configTexMagFilB = 0;
   public static int ub = 0;
   public static int L = 0;
   public static boolean configShadowClipFrustrum = true;
   public static boolean configNormalMap = true;
   public static boolean configSpecularMap = true;
   public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
   public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
   public static int configAntialiasingLevel = 0;
   public static final int eQ = 3;
   public static final int eN = 2;
   public static final String[] texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
   public static final String[] texMagFilDesc = new String[]{"Nearest", "Linear"};
   public static final int[] texMinFilValue = new int[]{9728, 9984, 9986};
   public static final int[] texMagFilValue = new int[]{9728, 9729};
   public static IShaderPack shaderPack = null;
   public static boolean shaderPackLoaded = false;
   public static File currentshader;
   public static String currentshadername;
   public static String packNameNone = "OFF";
   public static String packNameDefault = "(internal)";
   public static String shaderpacksdirname = "shaderpacks";
   public static String optionsfilename = "optionsshaders.txt";
   public static File shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
   public static File shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, shaderpacksdirname);
   public static File configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsfilename);
   public static ShaderOption[] shaderPackOptions = null;
   public static ShaderProfile[] shaderPackProfiles = null;
   public static Map<String, ShaderOption[]> shaderPackGuiScreens = null;
   public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
   public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
   public static PropertyDefaultTrueFalse shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
   public static PropertyDefaultTrueFalse shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
   public static PropertyDefaultTrueFalse shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
   public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
   public static PropertyDefaultTrueFalse shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
   public static PropertyDefaultTrueFalse shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
   public static PropertyDefaultTrueFalse shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
   public static PropertyDefaultTrueFalse shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
   public static PropertyDefaultTrueFalse shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
   public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
   public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
   private static Map<String, String> shaderPackResources = new HashMap<>();
   private static World currentWorld = null;
   private static List<Integer> shaderPackDimensions = new ArrayList<>();
   private static CustomTexture[] k = null;
   private static CustomTexture[] e4 = null;
   private static final String[] STAGE_NAMES = new String[]{"gbuffers", "composite"};
   public static final boolean OB = true;
   private static final boolean ue = true;
   private static final boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
   public static float blockLightLevel05 = 0.5F;
   public static float blockLightLevel06 = 0.6F;
   public static float blockLightLevel08 = 0.8F;
   public static float aoLevel = -1.0F;
   public static float sunPathRotation = 0.0F;
   public static float shadowAngleInterval = 0.0F;
   public static int fogMode = 0;
   public static float fogColorR;
   public static float fogColorG;
   public static float fogColorB;
   public static float shadowIntervalSize = 2.0F;
   public static int Bb = 16;
   public static int[] terrainTextureSize = new int[2];
   private static HFNoiseTexture customUniforms;
   private static boolean E = false;
   private static int uT = 256;
   public static final int[] dfbColorTexturesA = new int[16];
   public static final int[] colorTexturesToggle = new int[8];
   public static final int[] colorTextureTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
   public static final boolean[][] programsToggleColorTextures = new boolean[33][8];
   private static final int H = 2196;
   private static final ByteBuffer bigBuffer = (ByteBuffer)((Buffer)BufferUtils.createByteBuffer(2196)).limit(0);
   public static final float[] faProjection = new float[16];
   public static final float[] faProjectionInverse = new float[16];
   public static final float[] faModelView = new float[16];
   public static final float[] faModelViewInverse = new float[16];
   public static final float[] faShadowProjection = new float[16];
   public static final float[] faShadowProjectionInverse = new float[16];
   public static final float[] faShadowModelView = new float[16];
   public static final float[] faShadowModelViewInverse = new float[16];
   public static final FloatBuffer projection = nextFloatBuffer(16);
   public static final FloatBuffer projectionInverse = nextFloatBuffer(16);
   public static final FloatBuffer modelView = nextFloatBuffer(16);
   public static final FloatBuffer modelViewInverse = nextFloatBuffer(16);
   public static final FloatBuffer shadowProjection = nextFloatBuffer(16);
   public static final FloatBuffer shadowProjectionInverse = nextFloatBuffer(16);
   public static final FloatBuffer shadowModelView = nextFloatBuffer(16);
   public static final FloatBuffer shadowModelViewInverse = nextFloatBuffer(16);
   public static final FloatBuffer previousProjection = nextFloatBuffer(16);
   public static final FloatBuffer previousModelView = nextFloatBuffer(16);
   public static final FloatBuffer tempMatrixDirectBuffer = nextFloatBuffer(16);
   public static final FloatBuffer tempDirectFloatBuffer = nextFloatBuffer(16);
   public static final IntBuffer dfbColorTextures = nextIntBuffer(16);
   public static final IntBuffer dfbDepthTextures = nextIntBuffer(3);
   public static final IntBuffer sfbColorTextures = nextIntBuffer(8);
   public static final IntBuffer sfbDepthTextures = nextIntBuffer(2);
   public static final IntBuffer dfbDrawBuffers = nextIntBuffer(8);
   public static final IntBuffer sfbDrawBuffers = nextIntBuffer(8);
   public static final IntBuffer drawBuffersNone = nextIntBuffer(8);
   public static final IntBuffer drawBuffersAll = nextIntBuffer(8);
   public static final IntBuffer drawBuffersClear0 = nextIntBuffer(8);
   public static final IntBuffer drawBuffersClear1 = nextIntBuffer(8);
   public static final IntBuffer drawBuffersClearColor = nextIntBuffer(8);
   public static final IntBuffer drawBuffersColorAtt0 = nextIntBuffer(8);
   public static final IntBuffer[] drawBuffersBuffer = nextIntBufferArray(33, 8);
   public static Map<Block, Integer> mapBlockToEntityData;
   private static final Pattern Os = Pattern.compile("[ \t]*const[ \t]*int[ \t]*(\\w+)Format[ \t]*=[ \t]*([RGBA0123456789FUI_SNORM]*)[ \t]*;.*");
   private static final Pattern ee = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)Clear[ \t]*=[ \t]*false[ \t]*;.*");
   private static final Pattern i = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*");
   private static final String[] formatNames = new String[]{
      "R8",
      "RG8",
      "RGB8",
      "RGBA8",
      "R8_SNORM",
      "RG8_SNORM",
      "RGB8_SNORM",
      "RGBA8_SNORM",
      "R16",
      "RG16",
      "RGB16",
      "RGBA16",
      "R16_SNORM",
      "RG16_SNORM",
      "RGB16_SNORM",
      "RGBA16_SNORM",
      "R32F",
      "RG32F",
      "RGB32F",
      "RGBA32F",
      "R32I",
      "RG32I",
      "RGB32I",
      "RGBA32I",
      "R32UI",
      "RG32UI",
      "RGB32UI",
      "RGBA32UI",
      "R3_G3_B2",
      "RGB5_A1",
      "RGB10_A2",
      "R11F_G11F_B10F"
   };
   private static final int[] formatIds = new int[]{
      33321,
      33323,
      32849,
      32856,
      36756,
      36757,
      36758,
      36759,
      33322,
      33324,
      32852,
      32859,
      36760,
      36761,
      36762,
      36763,
      33326,
      33328,
      34837,
      34836,
      33333,
      33339,
      36227,
      36226,
      33334,
      33340,
      36209,
      36208,
      10768,
      32855,
      32857,
      35898
   };
   private static final Pattern patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
   public static int[] entityData = new int[32];
   public static int entityDataIndex = 0;
   private static final long ab = 63134867788987L;

   private static IntBuffer nextIntBuffer(int var0) {
      ByteBuffer var1 = bigBuffer;
      int var2 = var1.limit();
      ((Buffer)var1).position(var2).limit(var2 + var0 * 4);
      return var1.asIntBuffer();
   }

   private static FloatBuffer nextFloatBuffer(int var0) {
      ByteBuffer var1 = bigBuffer;
      int var2 = var1.limit();
      ((Buffer)var1).position(var2).limit(var2 + var0 * 4);
      return var1.asFloatBuffer();
   }

   private static IntBuffer[] nextIntBufferArray(int var0, int var1) {
      IntBuffer[] var4 = new IntBuffer[var0];

      for (int var5 = 0; var5 < var0; var5++) {
         var4[var5] = nextIntBuffer(var1);
      }

      return var4;
   }

   public static void loadConfig() {
      SMCLog.info("Load ShadersMod configuration.");

      try {
         if (!shaderpacksdir.exists()) {
            shaderpacksdir.mkdir();
         }
      } catch (Exception var11) {
         SMCLog.severe("Failed to open the shaderpacks directory: " + shaderpacksdir);
      }

      shadersConfig = new PropertiesOrdered();
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
      if (configFile.exists()) {
         try {
            FileReader var3 = new FileReader(configFile);
            shadersConfig.load(var3);
            var3.close();
         } catch (Exception var10) {
         }
      }

      if (!configFile.exists()) {
         try {
            storeConfig();
         } catch (Exception var9) {
         }
      }

      EnumShaderOption[] var12 = EnumShaderOption.values();

      for (int var4 = 0; var4 < var12.length; var4++) {
         EnumShaderOption var5 = var12[var4];
         String var6 = var5.getPropertyKey();
         String var7 = var5.getValueDefault();
         String var8 = shadersConfig.getProperty(var6, var7);
         setEnumShaderOption(var5, var8);
      }

      loadShaderPack();
   }

   private static void setEnumShaderOption(EnumShaderOption var0, String var1) {
      if (var1 == null) {
         var1 = var0.getValueDefault();
      }

      switch (var0) {
         case ANTIALIASING:
            configAntialiasingLevel = Config.parseInt(var1, 0);
            break;
         case NORMAL_MAP:
            configNormalMap = Config.parseBoolean(var1, true);
            break;
         case SPECULAR_MAP:
            configSpecularMap = Config.parseBoolean(var1, true);
            break;
         case RENDER_RES_MUL:
            configRenderResMul = Config.parseFloat(var1, 1.0F);
            break;
         case SHADOW_RES_MUL:
            configShadowResMul = Config.parseFloat(var1, 1.0F);
            break;
         case HAND_DEPTH_MUL:
            configHandDepthMul = Config.parseFloat(var1, 0.125F);
            break;
         case CLOUD_SHADOW:
            configCloudShadow = Config.parseBoolean(var1, true);
            break;
         case OLD_HAND_LIGHT:
            configOldHandLight.setPropertyValue(var1);
            break;
         case OLD_LIGHTING:
            configOldLighting.setPropertyValue(var1);
            break;
         case SHADER_PACK:
            currentshadername = var1;
            break;
         case TWEAK_BLOCK_DAMAGE:
            configTweakBlockDamage = Config.parseBoolean(var1, true);
            break;
         case SHADOW_CLIP_FRUSTRUM:
            configShadowClipFrustrum = Config.parseBoolean(var1, true);
            break;
         case TEX_MIN_FIL_B:
            configTexMinFilB = Config.parseInt(var1, 0);
            break;
         case TEX_MIN_FIL_N:
            configTexMinFilN = Config.parseInt(var1, 0);
            break;
         case TEX_MIN_FIL_S:
            configTexMinFilS = Config.parseInt(var1, 0);
            break;
         case TEX_MAG_FIL_B:
            configTexMagFilB = Config.parseInt(var1, 0);
            break;
         case TEX_MAG_FIL_N:
            configTexMagFilB = Config.parseInt(var1, 0);
            break;
         case TEX_MAG_FIL_S:
            configTexMagFilB = Config.parseInt(var1, 0);
            break;
         default:
            throw new IllegalArgumentException("Unknown option: " + var0);
      }
   }

   public static void storeConfig() {
      SMCLog.info("Save ShadersMod configuration.");
      if (shadersConfig == null) {
         shadersConfig = new PropertiesOrdered();
      }

      EnumShaderOption[] var3 = EnumShaderOption.values();

      for (int var4 = 0; var4 < var3.length; var4++) {
         EnumShaderOption var5 = var3[var4];
         String var6 = var5.getPropertyKey();
         String var7 = getEnumShaderOption(var5);
         shadersConfig.setProperty(var6, var7);
      }

      try {
         FileWriter var9 = new FileWriter(configFile);
         shadersConfig.store(var9, (String)null);
         var9.close();
      } catch (Exception var8) {
         SMCLog.severe("Error saving configuration: " + var8.getClass().getName() + ": " + var8.getMessage());
      }
   }

   public static String getEnumShaderOption(EnumShaderOption var0) {
      switch (var0) {
         case ANTIALIASING:
            return Integer.toString(configAntialiasingLevel);
         case NORMAL_MAP:
            return Boolean.toString(configNormalMap);
         case SPECULAR_MAP:
            return Boolean.toString(configSpecularMap);
         case RENDER_RES_MUL:
            return Float.toString(configRenderResMul);
         case SHADOW_RES_MUL:
            return Float.toString(configShadowResMul);
         case HAND_DEPTH_MUL:
            return Float.toString(configHandDepthMul);
         case CLOUD_SHADOW:
            return Boolean.toString(configCloudShadow);
         case OLD_HAND_LIGHT:
            return configOldHandLight.getPropertyValue();
         case OLD_LIGHTING:
            return configOldLighting.getPropertyValue();
         case SHADER_PACK:
            return currentshadername;
         case TWEAK_BLOCK_DAMAGE:
            return Boolean.toString(configTweakBlockDamage);
         case SHADOW_CLIP_FRUSTRUM:
            return Boolean.toString(configShadowClipFrustrum);
         case TEX_MIN_FIL_B:
            return Integer.toString(configTexMinFilB);
         case TEX_MIN_FIL_N:
            return Integer.toString(configTexMinFilN);
         case TEX_MIN_FIL_S:
            return Integer.toString(configTexMinFilS);
         case TEX_MAG_FIL_B:
            return Integer.toString(configTexMagFilB);
         case TEX_MAG_FIL_N:
            return Integer.toString(configTexMagFilB);
         case TEX_MAG_FIL_S:
            return Integer.toString(configTexMagFilB);
         default:
            throw new IllegalArgumentException("Unknown option: " + var0);
      }
   }

   public static void setShaderPack(String var0) {
      currentshadername = var0;
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), var0);
      loadShaderPack();
   }

   public static void loadShaderPack() {
      boolean var3 = shaderPackLoaded;
      boolean var4 = isOldLighting();
      shaderPackLoaded = false;
      if (shaderPack != null) {
         shaderPack.close();
         shaderPack = null;
         shaderPackResources.clear();
         shaderPackDimensions.clear();
         shaderPackOptions = null;
         shaderPackProfiles = null;
         shaderPackGuiScreens = null;
         shaderPackClouds.resetValue();
         shaderPackOldHandLight.resetValue();
         shaderPackDynamicHandLight.resetValue();
         shaderPackOldLighting.resetValue();
         I();
      }

      boolean var5 = false;
      if (Config.isAntialiasing()) {
         SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
         var5 = true;
      }

      if (Config.pL()) {
         SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
         var5 = true;
      }

      if (Config.isFastRender()) {
         SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
         var5 = true;
      }

      String var6 = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault);
      if (!var6.isEmpty() && !var6.equals(packNameNone) && !var5) {
         if (var6.equals(packNameDefault)) {
            shaderPack = new ShaderPackDefault();
            shaderPackLoaded = true;
         } else {
            try {
               File var7 = new File(shaderpacksdir, var6);
               if (var7.isDirectory()) {
                  shaderPack = new ShaderPackFolder(var6, var7);
                  shaderPackLoaded = true;
               } else if (var7.isFile() && var6.toLowerCase().endsWith(".zip")) {
                  shaderPack = new ShaderPackZip(var6, var7);
                  shaderPackLoaded = true;
               }
            } catch (Exception var9) {
            }
         }
      }

      if (shaderPack != null) {
         SMCLog.info("Loaded shaderpack: " + getShaderPackName());
      } else {
         SMCLog.info("No shaderpack loaded.");
         shaderPack = new ShaderPackNone();
      }

      loadShaderPackResources();
      loadShaderPackDimensions();
      shaderPackOptions = loadShaderPackOptions();
      loadShaderPackProperties();
      boolean var10 = shaderPackLoaded != var3;
      boolean var8 = isOldLighting() != var4;
      if (var10 || var8) {
         DefaultVertexFormats.updateVertexFormats();
         if (Reflector.LightUtil.exists()) {
            Reflector.LightUtil_itemConsumer.setValue(null);
            Reflector.LightUtil_tessellator.setValue(null);
         }

         updateBlockLightLevel();
         mc.scheduleResourcesRefresh();
      }
   }

   private static void loadShaderPackDimensions() {
      shaderPackDimensions.clear();

      for (int var3 = -128; var3 <= 128; var3++) {
         String var4 = "/shaders/world" + var3;
         if (shaderPack.hasDirectory(var4)) {
            shaderPackDimensions.add(var3);
         }
      }

      if (shaderPackDimensions.size() > 0) {
         Integer[] var5 = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
         Config.dbg("[Shaders] Worlds: " + Config.arrayToString(var5));
      }
   }

   private static void loadShaderPackProperties() {
      shaderPackClouds.resetValue();
      shaderPackOldHandLight.resetValue();
      shaderPackDynamicHandLight.resetValue();
      shaderPackOldLighting.resetValue();
      shaderPackShadowTranslucent.resetValue();
      shaderPackUnderwaterOverlay.resetValue();
      shaderPackSun.resetValue();
      shaderPackMoon.resetValue();
      shaderPackVignette.resetValue();
      shaderPackBackFaceSolid.resetValue();
      shaderPackBackFaceCutout.resetValue();
      shaderPackBackFaceCutoutMipped.resetValue();
      shaderPackBackFaceTranslucent.resetValue();
      BlockAliases.Q();
      if (shaderPack != null) {
         BlockAliases.update(shaderPack);
         String var3 = "/shaders/shaders.properties";

         try {
            InputStream var4 = shaderPack.getResourceAsStream(var3);
            if (var4 == null) {
               return;
            }

            PropertiesOrdered var5 = new PropertiesOrdered();
            var5.load(var4);
            var4.close();
            shaderPackClouds.loadFrom(var5);
            shaderPackOldHandLight.loadFrom(var5);
            shaderPackDynamicHandLight.loadFrom(var5);
            shaderPackOldLighting.loadFrom(var5);
            shaderPackShadowTranslucent.loadFrom(var5);
            shaderPackUnderwaterOverlay.loadFrom(var5);
            shaderPackSun.loadFrom(var5);
            shaderPackVignette.loadFrom(var5);
            shaderPackMoon.loadFrom(var5);
            shaderPackBackFaceSolid.loadFrom(var5);
            shaderPackBackFaceCutout.loadFrom(var5);
            shaderPackBackFaceCutoutMipped.loadFrom(var5);
            shaderPackBackFaceTranslucent.loadFrom(var5);
            shaderPackProfiles = ShaderPackParser.parseProfiles(var5, shaderPackOptions);
            shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(var5, shaderPackProfiles, shaderPackOptions);
            k = loadCustomTextures(var5, 0);
            e4 = loadCustomTextures(var5, 1);
         } catch (IOException var6) {
            Config.warn("[Shaders] Error reading: " + var3);
         }
      }
   }

   private static CustomTexture[] loadCustomTextures(Properties var0, int var1) {
      String var5 = "texture." + STAGE_NAMES[var1] + ".";
      Set var6 = var0.keySet();
      ArrayList var7 = new ArrayList();

      for (Object var9 : var6) {
         String var10 = (String)var9;
         if (var10.startsWith(var5)) {
            String var11 = var10.substring(var5.length());
            String var12 = var0.getProperty(var10).trim();
            int var13 = getTextureIndex(var1, var11);
            if (var13 < 0) {
               SMCLog.warning("Invalid texture name: " + var10);
            } else {
               try {
                  String var14 = "shaders/" + StrUtils.removePrefix(var12, "/");
                  InputStream var15 = shaderPack.getResourceAsStream(var14);
                  if (var15 == null) {
                     SMCLog.warning("Texture not found: " + var12);
                  } else {
                     IOUtils.closeQuietly(var15);
                     SimpleShaderTexture var16 = new SimpleShaderTexture(var14);
                     var16.loadTexture(mc.getResourceManager());
                     CustomTexture var17 = new CustomTexture(var13, var14, var16);
                     var7.add(var17);
                  }
               } catch (IOException var18) {
                  SMCLog.warning("Error loading texture: " + var12);
                  SMCLog.warning("" + var18.getClass().getName() + ": " + var18.getMessage());
               }
            }
         }
      }

      return var7.size() <= 0 ? null : (shadersmod.client.CustomTexture[])(var7.toArray(new CustomTexture[var7.size()]));
   }

   private static int getTextureIndex(int var0, String var1) {
      if (var0 == 0) {
         if (var1.equals("texture")) {
            return 0;
         }

         if (var1.equals("lightmap")) {
            return 1;
         }

         if (var1.equals("normals")) {
            return 2;
         }

         if (var1.equals("specular")) {
            return 3;
         }

         if (var1.equals("shadowtex0") || var1.equals("watershadow")) {
            return 4;
         }

         if (var1.equals("shadow")) {
            return waterShadowEnabled ? 5 : 4;
         }

         if (var1.equals("shadowtex1")) {
            return 5;
         }

         if (var1.equals("depthtex0")) {
            return 6;
         }

         if (var1.equals("gaux1")) {
            return 7;
         }

         if (var1.equals("gaux2")) {
            return 8;
         }

         if (var1.equals("gaux3")) {
            return 9;
         }

         if (var1.equals("gaux4")) {
            return 10;
         }

         if (var1.equals("depthtex1")) {
            return 12;
         }

         if (var1.equals("shadowcolor0") || var1.equals("shadowcolor")) {
            return 13;
         }

         if (var1.equals("shadowcolor1")) {
            return 14;
         }

         if (var1.equals("noisetex")) {
            return 15;
         }
      }

      if (var0 == 1) {
         if (var1.equals("colortex0") || var1.equals("colortex0")) {
            return 0;
         }

         if (var1.equals("colortex1") || var1.equals("gdepth")) {
            return 1;
         }

         if (var1.equals("colortex2") || var1.equals("gnormal")) {
            return 2;
         }

         if (var1.equals("colortex3") || var1.equals("composite")) {
            return 3;
         }

         if (var1.equals("shadowtex0") || var1.equals("watershadow")) {
            return 4;
         }

         if (var1.equals("shadow")) {
            return waterShadowEnabled ? 5 : 4;
         }

         if (var1.equals("shadowtex1")) {
            return 5;
         }

         if (var1.equals("depthtex0") || var1.equals("gdepthtex")) {
            return 6;
         }

         if (var1.equals("colortex4") || var1.equals("gaux1")) {
            return 7;
         }

         if (var1.equals("colortex5") || var1.equals("gaux2")) {
            return 8;
         }

         if (var1.equals("colortex6") || var1.equals("gaux3")) {
            return 9;
         }

         if (var1.equals("colortex7") || var1.equals("gaux4")) {
            return 10;
         }

         if (var1.equals("depthtex1")) {
            return 11;
         }

         if (var1.equals("depthtex2")) {
            return 12;
         }

         if (var1.equals("shadowcolor0") || var1.equals("shadowcolor")) {
            return 13;
         }

         if (var1.equals("shadowcolor1")) {
            return 14;
         }

         if (var1.equals("noisetex")) {
            return 15;
         }
      }

      return -1;
   }

   private static void bindCustomTextures(CustomTexture[] var0) {
      if (var0 != null) {
         for (int var4 = 0; var4 < var0.length; var4++) {
            CustomTexture var5 = var0[var4];
            GlStateManager.setActiveTexture(33984 + var5.getTextureUnit());
            ITextureObject var6 = var5.getTexture();
            GlStateManager.bindTexture(var6.getGlTextureId());
         }
      }
   }

   private static void I() {
      deleteCustomTextures(k);
      deleteCustomTextures(e4);
      k = null;
      e4 = null;
   }

   private static void deleteCustomTextures(CustomTexture[] var0) {
      if (var0 != null) {
         for (int var4 = 0; var4 < var0.length; var4++) {
            CustomTexture var5 = var0[var4];
            ITextureObject var6 = var5.getTexture();
            TextureUtil.deleteTexture(var6.getGlTextureId());
         }
      }
   }

   public static ShaderOption[] getShaderPackOptions(String var0) {
      ShaderOption[] var4 = (ShaderOption[])shaderPackOptions.clone();
      if (shaderPackGuiScreens == null) {
         if (shaderPackProfiles != null) {
            ShaderOptionProfile var11 = new ShaderOptionProfile(shaderPackProfiles, var4);
            var4 = (ShaderOption[])Config.addObjectToArray(var4, var11, 0);
         }

         return L(var4);
      } else {
         String var5 = var0 != null ? "screen." + var0 : "screen";
         ShaderOption[] var6 = shaderPackGuiScreens.get(var5);
         if (var6 == null) {
            return new ShaderOption[0];
         }

         ArrayList var7 = new ArrayList();

         for (int var8 = 0; var8 < var6.length; var8++) {
            ShaderOption var9 = var6[var8];
            if (var9 == null) {
               var7.add((ShaderOption)null);
            } else if (var9 instanceof ShaderOptionRest) {
               ShaderOption[] var10 = getShaderOptionsRest(shaderPackGuiScreens, var4);
               var7.addAll(Arrays.asList(var10));
            } else {
               var7.add(var9);
            }
         }

         return (shadersmod.client.ShaderOption[])(var7.toArray(new ShaderOption[var7.size()]));
      }
   }

   private static ShaderOption[] getShaderOptionsRest(Map<String, ShaderOption[]> var0, ShaderOption[] var1) {
      HashSet var5 = new HashSet();

      for (String var7 : var0.keySet()) {
         ShaderOption[] var8 = (ShaderOption[])var0.get(var7);

         for (int var9 = 0; var9 < var8.length; var9++) {
            ShaderOption var10 = var8[var9];
            if (var10 != null) {
               var5.add(var10.getName());
            }
         }
      }

      ArrayList var11 = new ArrayList();

      for (int var12 = 0; var12 < var1.length; var12++) {
         ShaderOption var13 = var1[var12];
         if (var13.V()) {
            String var14 = var13.getName();
            if (!var5.contains(var14)) {
               var11.add(var13);
            }
         }
      }

      return (shadersmod.client.ShaderOption[])(var11.toArray(new ShaderOption[var11.size()]));
   }

   public static ShaderOption getShaderOption(String var0) {
      return ShaderUtils.getShaderOption(var0, shaderPackOptions);
   }

   public static ShaderOption[] getShaderPackOptions() {
      return shaderPackOptions;
   }

   private static ShaderOption[] L(ShaderOption[] var0) {
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var0.length; var5++) {
         ShaderOption var6 = var0[var5];
         if (var6.V()) {
            var4.add(var6);
         }
      }

      return (shadersmod.client.ShaderOption[])(var4.toArray(new ShaderOption[var4.size()]));
   }

   public static void saveShaderPackOptions() {
      saveShaderPackOptions(shaderPackOptions, shaderPack);
   }

   private static void saveShaderPackOptions(ShaderOption[] var0, IShaderPack var1) {
      Properties var5 = new Properties();
      if (shaderPackOptions != null) {
         for (int var6 = 0; var6 < var0.length; var6++) {
            ShaderOption var7 = var0[var6];
            if (var7.isChanged() && var7.isEnabled()) {
               var5.setProperty(var7.getName(), var7.getValue());
            }
         }
      }

      try {
         saveOptionProperties(var1, var5);
      } catch (IOException var8) {
         Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
         var8.printStackTrace();
      }
   }

   private static void saveOptionProperties(IShaderPack var0, Properties var1) throws IOException {
      String var5 = shaderpacksdirname + "/" + var0.getName() + ".txt";
      File var6 = new File(Minecraft.getMinecraft().mcDataDir, var5);
      if (var1.isEmpty()) {
         var6.delete();
      } else {
         FileOutputStream var7 = new FileOutputStream(var6);
         var1.store(var7, (String)null);
         var7.flush();
         var7.close();
      }
   }

   private static ShaderOption[] loadShaderPackOptions() {
      try {
         ShaderOption[] var3 = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
         Properties var4 = loadOptionProperties(shaderPack);

         for (int var5 = 0; var5 < var3.length; var5++) {
            ShaderOption var6 = var3[var5];
            String var7 = var4.getProperty(var6.getName());
            if (var7 != null) {
               var6.resetValue();
               if (!var6.setValue(var7)) {
                  Config.warn("[Shaders] Invalid value, option: " + var6.getName() + ", value: " + var7);
               }
            }
         }

         return var3;
      } catch (IOException var8) {
         Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
         var8.printStackTrace();
         return null;
      }
   }

   private static Properties loadOptionProperties(IShaderPack var0) throws IOException {
      Properties var4 = new Properties();
      String var5 = shaderpacksdirname + "/" + var0.getName() + ".txt";
      File var6 = new File(Minecraft.getMinecraft().mcDataDir, var5);
      if (var6.exists() && var6.isFile() && var6.canRead()) {
         FileInputStream var7 = new FileInputStream(var6);
         var4.load(var7);
         var7.close();
         return var4;
      } else {
         return var4;
      }
   }

   public static ShaderOption[] getChangedOptions(ShaderOption[] var0) {
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var0.length; var5++) {
         ShaderOption var6 = var0[var5];
         if (var6.isEnabled() && var6.isChanged()) {
            var4.add(var6);
         }
      }

      return (shadersmod.client.ShaderOption[])(var4.toArray(new ShaderOption[var4.size()]));
   }

   private static String applyOptions(String var0, ShaderOption[] var1) {
      if (var1 != null && var1.length > 0) {
         for (int var5 = 0; var5 < var1.length; var5++) {
            ShaderOption var6 = var1[var5];
            String var7 = var6.getName();
            if (var6.matchesLine(var0)) {
               var0 = var6.getSourceLine();
               break;
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static ArrayList listOfShaders() {
      ArrayList var3 = new ArrayList();
      var3.add(packNameNone);
      var3.add(packNameDefault);

      try {
         if (!shaderpacksdir.exists()) {
            shaderpacksdir.mkdir();
         }

         File[] var4 = shaderpacksdir.listFiles();

         for (int var5 = 0; var5 < var4.length; var5++) {
            File var6 = var4[var5];
            String var7 = var6.getName();
            if (var6.isDirectory()) {
               File var8 = new File(var6, "shaders");
               if (var8.exists() && var8.isDirectory()) {
                  var3.add(var7);
               }
            } else if (var6.isFile() && var7.toLowerCase().endsWith(".zip")) {
               var3.add(var7);
            }
         }
      } catch (Exception var9) {
      }

      return var3;
   }

   public static String versiontostring(int var0) {
      String var1 = Integer.toString(var0);
      return Integer.toString(Integer.parseInt(var1.substring(1, 3)))
         + "."
         + Integer.toString(Integer.parseInt(var1.substring(3, 5)))
         + "."
         + Integer.toString(Integer.parseInt(var1.substring(5)));
   }

   public static void UA() {
   }

   public static int checkFramebufferStatus(String var0) {
      int var4 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (var4 != 36053) {
         System.err.format("FramebufferStatus 0x%04X at %s\n", var4, var0);
      }

      return var4;
   }

   public static int checkGLError(String var0) {
      int var4 = GL11.glGetError();
      if (var4 != 0) {
         boolean var5 = false;
         if (!var5) {
            if (var4 == 1286) {
               int var6 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
               System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", var4, GLU.gluErrorString(var4), var6, var0);
            } else {
               System.err.format("GL error 0x%04X: %s at %s\n", var4, GLU.gluErrorString(var4), var0);
            }
         }
      }

      return var4;
   }

   public static int checkGLError(String var0, String var1) {
      int var5 = GL11.glGetError();
      if (var5 != 0) {
         System.err.format("GL error 0x%04x: %s at %s %s\n", var5, GLU.gluErrorString(var5), var0, var1);
      }

      return var5;
   }

   public static int checkGLError(String var0, String var1, String var2) {
      int var6 = GL11.glGetError();
      if (var6 != 0) {
         System.err.format("GL error 0x%04x: %s at %s %s %s\n", var6, GLU.gluErrorString(var6), var0, var1, var2);
      }

      return var6;
   }

   private static void printChatAndLogError(String var0) {
      SMCLog.severe(var0);
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(var0));
   }

   public static void printIntBuffer(String var0, IntBuffer var1) {
      StringBuilder var5 = new StringBuilder(128);
      var5.append(var0).append(" [pos ").append(var1.position()).append(" lim ").append(var1.limit()).append(" cap ").append(var1.capacity()).append(" :");
      int var6 = var1.limit();

      for (int var7 = 0; var7 < var6; var7++) {
         var5.append(" ").append(var1.get(var7));
      }

      var5.append("]");
      SMCLog.info(var5.toString());
   }

   public static void startup(Minecraft var0) {
      checkShadersModInstalled();
      mc = var0;
      var0 = Minecraft.getMinecraft();
      capabilities = GLContext.getCapabilities();
      glVersionString = GL11.glGetString(7938);
      glVendorString = GL11.glGetString(7936);
      glRendererString = GL11.glGetString(7937);
      SMCLog.info("ShadersMod version: 2.4.12");
      SMCLog.info("OpenGL Version: " + glVersionString);
      SMCLog.info("Vendor:  " + glVendorString);
      SMCLog.info("Renderer: " + glRendererString);
      SMCLog.info(
         "Capabilities: "
            + (capabilities.OpenGL20 ? " 2.0 " : " - ")
            + (capabilities.OpenGL21 ? " 2.1 " : " - ")
            + (capabilities.OpenGL30 ? " 3.0 " : " - ")
            + (capabilities.OpenGL32 ? " 3.2 " : " - ")
            + (capabilities.OpenGL40 ? " 4.0 " : " - ")
      );
      SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(34852));
      SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
      SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(34930));
      hasGlGenMipmap = capabilities.OpenGL30;
      loadConfig();
   }

   public static void updateBlockLightLevel() {
      if (isOldLighting()) {
         blockLightLevel05 = 0.5F;
         blockLightLevel06 = 0.6F;
         blockLightLevel08 = 0.8F;
      } else {
         blockLightLevel05 = 1.0F;
         blockLightLevel06 = 1.0F;
         blockLightLevel08 = 1.0F;
      }
   }

   public static boolean j_boolean() {
      return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (!shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true);
   }

   public static boolean x() {
      return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
   }

   public static boolean isOldLighting() {
      return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
   }

   public static boolean isRenderShadowTranslucent() {
      return !shaderPackShadowTranslucent.isFalse();
   }

   public static boolean isUnderwaterOverlay() {
      return !shaderPackUnderwaterOverlay.isFalse();
   }

   public static boolean i_boolean() {
      return !shaderPackSun.isFalse();
   }

   public static boolean r() {
      return !shaderPackMoon.isFalse();
   }

   public static boolean l() {
      return !shaderPackVignette.isFalse();
   }

   public static boolean isRenderBackFace(EnumWorldBlockLayer var0) {
      switch (var0) {
         case SOLID:
            return shaderPackBackFaceSolid.isTrue();
         case CUTOUT:
            return shaderPackBackFaceCutout.isTrue();
         case CUTOUT_MIPPED:
            return shaderPackBackFaceCutoutMipped.isTrue();
         case TRANSLUCENT:
            return shaderPackBackFaceTranslucent.isTrue();
         default:
            return false;
      }
   }

   public static void init() {
      if (!B5) {
         B5 = true;
      }

      if (!T) {
         checkGLError("Shaders.init pre");
         if (getShaderPackName() != null) {
         }

         if (!capabilities.OpenGL20) {
            printChatAndLogError("No OpenGL 2.0");
         }

         if (!capabilities.GL_EXT_framebuffer_object) {
            printChatAndLogError("No EXT_framebuffer_object");
         }

         ((Buffer)dfbDrawBuffers).position(0).limit(8);
         ((Buffer)dfbColorTextures).position(0).limit(16);
         ((Buffer)dfbDepthTextures).position(0).limit(3);
         ((Buffer)sfbDrawBuffers).position(0).limit(8);
         ((Buffer)sfbDepthTextures).position(0).limit(2);
         ((Buffer)sfbColorTextures).position(0).limit(8);
         usedColorBuffers = 4;
         ew = 1;
         uQ = 0;
         OF = 0;
         uo = 1;
         Oh = 1;
         Arrays.fill(gbuffersFormat, 6408);
         Arrays.fill(gbuffersClear, true);
         Arrays.fill(shadowHardwareFilteringEnabled, false);
         Arrays.fill(shadowMipmapEnabled, false);
         Arrays.fill(shadowFilterNearest, false);
         Arrays.fill(shadowColorMipmapEnabled, false);
         Arrays.fill(shadowColorFilterNearest, false);
         centerDepthSmoothEnabled = false;
         E = false;
         sunPathRotation = 0.0F;
         shadowIntervalSize = 2.0F;
         shadowDistanceRenderMul = -1.0F;
         aoLevel = -1.0F;
         Bp = false;
         u_ = false;
         uv = false;
         eg = false;
         waterShadowEnabled = false;
         m = false;
         updateBlockLightLevel();
         ShaderProfile var4 = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
         String var5 = "";
         if (currentWorld != null) {
            int var6 = currentWorld.provider.getDimensionId();
            if (shaderPackDimensions.contains(var6)) {
               var5 = "world" + var6 + "/";
            }
         }

         if (saveFinalShaders) {
            clearDirectory(new File(shaderpacksdir, "debug"));
         }

         for (int var14 = 0; var14 < 33; var14++) {
            String var7 = programNames[var14];
            if (var7.equals("")) {
               programsID[var14] = programsRef[var14] = 0;
               programsDrawBufSettings[var14] = null;
               programsColorAtmSettings[var14] = null;
               programsCompositeMipmapSetting[var14] = 0;
            } else {
               eq = null;
               eV = null;
               OA = 0;
               String var8 = var5 + var7;
               if (var4 != null && var4.isProgramDisabled(var8)) {
                  SMCLog.info("Program disabled: " + var8);
                  var7 = "<disabled>";
                  var8 = var5 + var7;
               }

               String var9 = "/shaders/" + var8;
               int var10 = setupProgram(var14, var9 + ".vsh", var9 + ".fsh");
               if (var10 > 0) {
                  SMCLog.info("Program loaded: " + var8);
               }

               programsID[var14] = programsRef[var14] = var10;
               programsDrawBufSettings[var14] = var10 != 0 ? eq : null;
               programsColorAtmSettings[var14] = var10 != 0 ? eV : null;
               programsCompositeMipmapSetting[var14] = var10 != 0 ? OA : 0;
            }
         }

         int var15 = GL11.glGetInteger(34852);
         new HashMap();

         for (int var17 = 0; var17 < 33; var17++) {
            Arrays.fill(programsToggleColorTextures[var17], false);
            if (var17 == 29) {
               programsDrawBuffers[var17] = null;
            } else if (programsID[var17] == 0) {
               if (var17 == 30) {
                  programsDrawBuffers[var17] = drawBuffersNone;
               } else {
                  programsDrawBuffers[var17] = drawBuffersColorAtt0;
               }
            } else {
               String var21 = programsDrawBufSettings[var17];
               if (var21 != null) {
                  IntBuffer var23 = drawBuffersBuffer[var17];
                  int var24 = var21.length();
                  if (var24 > Oh) {
                     Oh = var24;
                  }

                  if (var24 > var15) {
                     var24 = var15;
                  }

                  programsDrawBuffers[var17] = var23;
                  ((Buffer)var23).limit(var24);

                  for (int var11 = 0; var11 < var24; var11++) {
                     int var12 = 0;
                     if (var21.length() > var11) {
                        int var13 = var21.charAt(var11) - '0';
                        if (var17 != 30) {
                           if (var13 >= 0 && var13 <= 7) {
                              programsToggleColorTextures[var17][var13] = true;
                              var12 = var13 + 36064;
                              if (var13 > uo) {
                                 uo = var13;
                              }

                              if (var13 > usedColorBuffers) {
                                 usedColorBuffers = var13;
                              }
                           }
                        } else if (var13 >= 0 && var13 <= 1) {
                           var12 = var13 + 36064;
                           if (var13 > uQ) {
                              uQ = var13;
                           }
                        }
                     }

                     var23.put(var11, var12);
                  }
               } else if (var17 != 30 && var17 != 31 && var17 != 32) {
                  programsDrawBuffers[var17] = dfbDrawBuffers;
                  Oh = usedColorBuffers;
                  Arrays.fill(programsToggleColorTextures[var17], 0, usedColorBuffers, true);
               } else {
                  programsDrawBuffers[var17] = sfbDrawBuffers;
               }
            }
         }

         uo = usedColorBuffers;
         OO = OF > 0 ? 1 : 0;
         shouldSkipDefaultShadow = OF > 0;
         SMCLog.info("usedColorBuffers: " + usedColorBuffers);
         SMCLog.info("usedDepthBuffers: " + ew);
         SMCLog.info("usedShadowColorBuffers: " + uQ);
         SMCLog.info("usedShadowDepthBuffers: " + OF);
         SMCLog.info("usedColorAttachs: " + uo);
         SMCLog.info("usedDrawBuffers: " + Oh);
         ((Buffer)dfbDrawBuffers).position(0).limit(Oh);
         ((Buffer)dfbColorTextures).position(0).limit(usedColorBuffers * 2);

         for (int var18 = 0; var18 < Oh; var18++) {
            dfbDrawBuffers.put(var18, 36064 + var18);
         }

         if (Oh > var15) {
            printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + Oh + ", available: " + var15);
         }

         ((Buffer)sfbDrawBuffers).position(0).limit(uQ);

         for (int var19 = 0; var19 < uQ; var19++) {
            sfbDrawBuffers.put(var19, 36064 + var19);
         }

         for (int var20 = 0; var20 < 33; var20++) {
            int var22 = var20;

            while (programsID[var22] == 0 && programBackups[var22] != var22) {
               var22 = programBackups[var22];
            }

            if (var22 != var20 && var20 != 30) {
               programsID[var20] = programsID[var22];
               programsDrawBufSettings[var20] = programsDrawBufSettings[var22];
               programsDrawBuffers[var20] = programsDrawBuffers[var22];
            }
         }

         c();
         z();
         if (E) {
            F();
         }

         if (defaultTexture == null) {
            defaultTexture = ShadersTex.createDefaultTexture();
         }

         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         preCelestialRotate();
         postCelestialRotate();
         GlStateManager.popMatrix();
         T = true;
         loadEntityDataMap();
         resetDisplayList();
         checkGLError("Shaders.init");
      }
   }

   public static void resetDisplayList() {
      numberResetDisplayList++;
      needResetModels = true;
      SMCLog.info("Reset world renderers");
      mc.renderGlobal.loadRenderers();
   }

   public static void resetDisplayListModels() {
      if (needResetModels) {
         needResetModels = false;
         SMCLog.info("Reset model renderers");

         for (Object var4 : mc.getRenderManager().getEntityRenderMap().values()) {
            if (var4 instanceof RendererLivingEntity) {
               RendererLivingEntity var5 = (RendererLivingEntity)var4;
               resetDisplayListModel(var5.getMainModel());
            }
         }
      }
   }

   public static void resetDisplayListModel(ModelBase var0) {
      if (var0 != null) {
         for (Object var5 : var0.boxList) {
            if (var5 instanceof ModelRenderer) {
               resetDisplayListModelRenderer((ModelRenderer)var5);
            }
         }
      }
   }

   public static void resetDisplayListModelRenderer(ModelRenderer var0) {
      var0.resetDisplayList();
      if (var0.childModels != null) {
         int var4 = 0;

         for (int var5 = var0.childModels.size(); var4 < var5; var4++) {
            resetDisplayListModelRenderer(var0.childModels.get(var4));
         }
      }
   }

   private static int setupProgram(int var0, String var1, String var2) {
      checkGLError("pre setupProgram");
      int var6 = ARBShaderObjects.glCreateProgramObjectARB();
      checkGLError("create");
      if (var6 != 0) {
         progUseEntityAttrib = false;
         progUseMidTexCoordAttrib = false;
         progUseTangentAttrib = false;
         int var7 = createVertShader(var1);
         int var8 = createFragShader(var2);
         checkGLError("create");
         if (var7 == 0 && var8 == 0) {
            ARBShaderObjects.glDeleteObjectARB(var6);
            var6 = 0;
         } else {
            if (var7 != 0) {
               ARBShaderObjects.glAttachObjectARB(var6, var7);
               checkGLError("attach");
            }

            if (var8 != 0) {
               ARBShaderObjects.glAttachObjectARB(var6, var8);
               checkGLError("attach");
            }

            if (progUseEntityAttrib) {
               ARBVertexShader.glBindAttribLocationARB(var6, entityAttrib, "mc_Entity");
               checkGLError("mc_Entity");
            }

            if (progUseMidTexCoordAttrib) {
               ARBVertexShader.glBindAttribLocationARB(var6, midTexCoordAttrib, "mc_midTexCoord");
               checkGLError("mc_midTexCoord");
            }

            if (progUseTangentAttrib) {
               ARBVertexShader.glBindAttribLocationARB(var6, tangentAttrib, "at_tangent");
               checkGLError("at_tangent");
            }

            ARBShaderObjects.glLinkProgramARB(var6);
            if (GL20.glGetProgrami(var6, 35714) != 1) {
               SMCLog.severe("Error linking program: " + var6);
            }

            printLogInfo(var6, var1 + ", " + var2);
            if (var7 != 0) {
               ARBShaderObjects.glDetachObjectARB(var6, var7);
               ARBShaderObjects.glDeleteObjectARB(var7);
            }

            if (var8 != 0) {
               ARBShaderObjects.glDetachObjectARB(var6, var8);
               ARBShaderObjects.glDeleteObjectARB(var8);
            }

            programsID[var0] = var6;
            useProgram(var0);
            ARBShaderObjects.glValidateProgramARB(var6);
            useProgram(0);
            printLogInfo(var6, var1 + ", " + var2);
            int var9 = GL20.glGetProgrami(var6, 35715);
            if (var9 != 1) {
               String var10 = "\"";
               printChatAndLogError("[Shaders] Error: Invalid program " + var10 + programNames[var0] + var10);
               ARBShaderObjects.glDeleteObjectARB(var6);
               var6 = 0;
            }
         }
      }

      return var6;
   }

   private static int createVertShader(String var0) {
      int var4 = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (var4 == 0) {
         return 0;
      }

      StringBuilder var5 = new StringBuilder(131072);
      BufferedReader var6 = null;

      try {
         var6 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(var0)));
      } catch (Exception var11) {
         try {
            var6 = new BufferedReader(new FileReader(new File(var0)));
         } catch (Exception var10) {
            ARBShaderObjects.glDeleteObjectARB(var4);
            return 0;
         }
      }

      ShaderOption[] var7 = getChangedOptions(shaderPackOptions);
      ArrayList var8 = new ArrayList();
      if (var6 != null) {
         try {
            var6 = ShaderPackParser.resolveIncludes(var6, var0, shaderPack, 0, var8, 0);

            while (true) {
               String var9 = var6.readLine();
               if (var9 == null) {
                  var6.close();
                  break;
               }

               var9 = applyOptions(var9, var7);
               var5.append(var9).append('\n');
               if (var9.matches("attribute [_a-zA-Z0-9]+ mc_Entity.*")) {
                  Bp = true;
                  progUseEntityAttrib = true;
               } else if (var9.matches("attribute [_a-zA-Z0-9]+ mc_midTexCoord.*")) {
                  u_ = true;
                  progUseMidTexCoordAttrib = true;
               } else if (var9.matches(".*gl_MultiTexCoord3.*")) {
                  uv = true;
               } else if (var9.matches("attribute [_a-zA-Z0-9]+ at_tangent.*")) {
                  eg = true;
                  progUseTangentAttrib = true;
               }
            }
         } catch (Exception var12) {
            SMCLog.severe("Couldn't read " + var0 + "!");
            var12.printStackTrace();
            ARBShaderObjects.glDeleteObjectARB(var4);
            return 0;
         }
      }

      if (saveFinalShaders) {
         saveShader(var0, var5.toString());
      }

      ARBShaderObjects.glShaderSourceARB(var4, var5);
      ARBShaderObjects.glCompileShaderARB(var4);
      if (GL20.glGetShaderi(var4, 35713) != 1) {
         SMCLog.severe("Error compiling vertex shader: " + var0);
      }

      printShaderLogInfo(var4, var0, var8);
      return var4;
   }

   private static int createFragShader(String var0) {
      int var4 = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (var4 == 0) {
         return 0;
      }

      StringBuilder var5 = new StringBuilder(131072);
      BufferedReader var6 = null;

      try {
         var6 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(var0)));
      } catch (Exception var16) {
         try {
            var6 = new BufferedReader(new FileReader(new File(var0)));
         } catch (Exception var15) {
            ARBShaderObjects.glDeleteObjectARB(var4);
            return 0;
         }
      }

      ShaderOption[] var7 = getChangedOptions(shaderPackOptions);
      ArrayList var8 = new ArrayList();
      if (var6 != null) {
         try {
            var6 = ShaderPackParser.resolveIncludes(var6, var0, shaderPack, 0, var8, 0);

            while (true) {
               String var9 = var6.readLine();
               if (var9 == null) {
                  var6.close();
                  break;
               }

               var9 = applyOptions(var9, var7);
               var5.append(var9).append('\n');
               if (!var9.matches("#version .*")) {
                  if (var9.matches("uniform [ _a-zA-Z0-9]+ shadow;.*")) {
                     if (OF < 1) {
                        OF = 1;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ watershadow;.*")) {
                     waterShadowEnabled = true;
                     if (OF < 2) {
                        OF = 2;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ shadowtex0;.*")) {
                     if (OF < 1) {
                        OF = 1;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ shadowtex1;.*")) {
                     if (OF < 2) {
                        OF = 2;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ shadowcolor;.*")) {
                     if (uQ < 1) {
                        uQ = 1;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ shadowcolor0;.*")) {
                     if (uQ < 1) {
                        uQ = 1;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ shadowcolor1;.*")) {
                     if (uQ < 2) {
                        uQ = 2;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ depthtex0;.*")) {
                     if (ew < 1) {
                        ew = 1;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ depthtex1;.*")) {
                     if (ew < 2) {
                        ew = 2;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ depthtex2;.*")) {
                     if (ew < 3) {
                        ew = 3;
                     }
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ gdepth;.*")) {
                     if (gbuffersFormat[1] == 6408) {
                        gbuffersFormat[1] = 34836;
                     }
                  } else if (usedColorBuffers < 5 && var9.matches("uniform [ _a-zA-Z0-9]+ gaux1;.*")) {
                     usedColorBuffers = 5;
                  } else if (usedColorBuffers < 6 && var9.matches("uniform [ _a-zA-Z0-9]+ gaux2;.*")) {
                     usedColorBuffers = 6;
                  } else if (usedColorBuffers < 7 && var9.matches("uniform [ _a-zA-Z0-9]+ gaux3;.*")) {
                     usedColorBuffers = 7;
                  } else if (usedColorBuffers < 8 && var9.matches("uniform [ _a-zA-Z0-9]+ gaux4;.*")) {
                     usedColorBuffers = 8;
                  } else if (usedColorBuffers < 5 && var9.matches("uniform [ _a-zA-Z0-9]+ colortex4;.*")) {
                     usedColorBuffers = 5;
                  } else if (usedColorBuffers < 6 && var9.matches("uniform [ _a-zA-Z0-9]+ colortex5;.*")) {
                     usedColorBuffers = 6;
                  } else if (usedColorBuffers < 7 && var9.matches("uniform [ _a-zA-Z0-9]+ colortex6;.*")) {
                     usedColorBuffers = 7;
                  } else if (usedColorBuffers < 8 && var9.matches("uniform [ _a-zA-Z0-9]+ colortex7;.*")) {
                     usedColorBuffers = 8;
                  } else if (var9.matches("uniform [ _a-zA-Z0-9]+ centerDepthSmooth;.*")) {
                     centerDepthSmoothEnabled = true;
                  } else if (var9.matches("/\\* SHADOWRES:[0-9]+ \\*/.*")) {
                     String[] var10 = var9.split("(:| )", 4);
                     SMCLog.info("Shadow map resolution: " + var10[2]);
                     eS = uc = Integer.parseInt(var10[2]);
                     Ow = e5 = Math.round(eS * configShadowResMul);
                  } else if (var9.matches("[ \t]*const[ \t]*int[ \t]*shadowMapResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var21 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow map resolution: " + var21[1]);
                     eS = uc = Integer.parseInt(var21[1]);
                     Ow = e5 = Math.round(eS * configShadowResMul);
                  } else if (var9.matches("/\\* SHADOWFOV:[0-9\\.]+ \\*/.*")) {
                     String[] var22 = var9.split("(:| )", 4);
                     SMCLog.info("Shadow map field of view: " + var22[2]);
                     shadowMapFOV = Float.parseFloat(var22[2]);
                     Bj = false;
                  } else if (var9.matches("/\\* SHADOWHPL:[0-9\\.]+ \\*/.*")) {
                     String[] var23 = var9.split("(:| )", 4);
                     SMCLog.info("Shadow map half-plane: " + var23[2]);
                     shadowMapHalfPlane = Float.parseFloat(var23[2]);
                     Bj = true;
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*shadowDistance[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var24 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow map distance: " + var24[1]);
                     shadowMapHalfPlane = Float.parseFloat(var24[1]);
                     Bj = true;
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*shadowDistanceRenderMul[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var25 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow distance render mul: " + var25[1]);
                     shadowDistanceRenderMul = Float.parseFloat(var25[1]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*shadowIntervalSize[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var26 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow map interval size: " + var26[1]);
                     shadowIntervalSize = Float.parseFloat(var26[1]);
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("Generate shadow mipmap");
                     Arrays.fill(shadowMipmapEnabled, true);
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowColorMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("Generate shadow color mipmap");
                     Arrays.fill(shadowColorMipmapEnabled, true);
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("Hardware shadow filtering enabled.");
                     Arrays.fill(shadowHardwareFilteringEnabled, true);
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering0[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowHardwareFiltering0");
                     shadowHardwareFilteringEnabled[0] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering1[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowHardwareFiltering1");
                     shadowHardwareFilteringEnabled[1] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Mipmap|shadowtexMipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex0Mipmap");
                     shadowMipmapEnabled[0] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex1Mipmap");
                     shadowMipmapEnabled[1] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Mipmap|shadowColor0Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowcolor0Mipmap");
                     shadowColorMipmapEnabled[0] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Mipmap|shadowColor1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowcolor1Mipmap");
                     shadowColorMipmapEnabled[1] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Nearest|shadowtexNearest|shadow0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex0Nearest");
                     shadowFilterNearest[0] = true;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Nearest|shadow1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex1Nearest");
                     shadowFilterNearest[1] = true;
                  } else if (var9.matches(
                     "[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Nearest|shadowColor0Nearest|shadowColor0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*"
                  )) {
                     SMCLog.info("shadowcolor0Nearest");
                     shadowColorFilterNearest[0] = true;
                  } else if (var9.matches(
                     "[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Nearest|shadowColor1Nearest|shadowColor1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*"
                  )) {
                     SMCLog.info("shadowcolor1Nearest");
                     shadowColorFilterNearest[1] = true;
                  } else if (var9.matches("/\\* WETNESSHL:[0-9\\.]+ \\*/.*")) {
                     String[] var27 = var9.split("(:| )", 4);
                     SMCLog.info("Wetness halflife: " + var27[2]);
                     wetnessHalfLife = Float.parseFloat(var27[2]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*wetnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var28 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Wetness halflife: " + var28[1]);
                     wetnessHalfLife = Float.parseFloat(var28[1]);
                  } else if (var9.matches("/\\* DRYNESSHL:[0-9\\.]+ \\*/.*")) {
                     String[] var29 = var9.split("(:| )", 4);
                     SMCLog.info("Dryness halflife: " + var29[2]);
                     drynessHalfLife = Float.parseFloat(var29[2]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*drynessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var30 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Dryness halflife: " + var30[1]);
                     drynessHalfLife = Float.parseFloat(var30[1]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*eyeBrightnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var31 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Eye brightness halflife: " + var31[1]);
                     eyeBrightnessHalflife = Float.parseFloat(var31[1]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*centerDepthHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var32 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Center depth halflife: " + var32[1]);
                     centerDepthSmoothHalflife = Float.parseFloat(var32[1]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*sunPathRotation[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var33 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Sun path rotation: " + var33[1]);
                     sunPathRotation = Float.parseFloat(var33[1]);
                  } else if (var9.matches("[ \t]*const[ \t]*float[ \t]*ambientOcclusionLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var34 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("AO Level: " + var34[1]);
                     aoLevel = Config.limit(Float.parseFloat(var34[1]), 0.0F, 1.0F);
                  } else if (var9.matches("[ \t]*const[ \t]*int[ \t]*superSamplingLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var35 = var9.split("(=[ \t]*|;)");
                     int var11 = Integer.parseInt(var35[1]);
                     if (var11 > 1) {
                        SMCLog.info("Super sampling level: " + var11 + "x");
                        M = var11;
                     } else {
                        M = 1;
                     }
                  } else if (var9.matches("[ \t]*const[ \t]*int[ \t]*noiseTextureResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] var36 = var9.split("(=[ \t]*|;)");
                     SMCLog.info("Noise texture enabled");
                     SMCLog.info("Noise texture resolution: " + var36[1]);
                     uT = Integer.parseInt(var36[1]);
                     E = true;
                  } else if (var9.matches("[ \t]*const[ \t]*int[ \t]*\\w+Format[ \t]*=[ \t]*[RGBA0123456789FUI_SNORM]*[ \t]*;.*")) {
                     Matcher var37 = Os.matcher(var9);
                     var37.matches();
                     String var41 = var37.group(1);
                     String var12 = var37.group(2);
                     int var13 = getBufferIndexFromString(var41);
                     int var14 = getTextureFormatFromString(var12);
                     if (var13 >= 0 && var14 != 0) {
                        gbuffersFormat[var13] = var14;
                        SMCLog.v("%s format: %s", var41, var12);
                     }
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*\\w+Clear[ \t]*=[ \t]*false[ \t]*;.*")) {
                     if (var0.matches(".*composite[0-9]?.fsh")) {
                        Matcher var38 = ee.matcher(var9);
                        var38.matches();
                        String var42 = var38.group(1);
                        int var44 = getBufferIndexFromString(var42);
                        if (var44 >= 0) {
                           gbuffersClear[var44] = false;
                           SMCLog.v("%s clear disabled", var42);
                        }
                     }
                  } else if (var9.matches("/\\* GAUX4FORMAT:RGBA32F \\*/.*")) {
                     SMCLog.info("gaux4 format : RGB32AF");
                     gbuffersFormat[7] = 34836;
                  } else if (var9.matches("/\\* GAUX4FORMAT:RGB32F \\*/.*")) {
                     SMCLog.info("gaux4 format : RGB32F");
                     gbuffersFormat[7] = 34837;
                  } else if (var9.matches("/\\* GAUX4FORMAT:RGB16 \\*/.*")) {
                     SMCLog.info("gaux4 format : RGB16");
                     gbuffersFormat[7] = 32852;
                  } else if (var9.matches("[ \t]*const[ \t]*bool[ \t]*\\w+MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*")) {
                     if (var0.matches(".*composite[0-9]?.fsh") || var0.matches(".*final.fsh")) {
                        Matcher var39 = i.matcher(var9);
                        var39.matches();
                        String var43 = var39.group(1);
                        int var45 = getBufferIndexFromString(var43);
                        if (var45 >= 0) {
                           OA |= 1 << var45;
                           SMCLog.v("%s mipmap enabled", var43);
                        }
                     }
                  } else if (var9.matches("/\\* DRAWBUFFERS:[0-7N]* \\*/.*")) {
                     String[] var40 = var9.split("(:| )", 4);
                     eq = var40[2];
                  }
               }
            }
         } catch (Exception var17) {
            SMCLog.severe("Couldn't read " + var0 + "!");
            var17.printStackTrace();
            ARBShaderObjects.glDeleteObjectARB(var4);
            return 0;
         }
      }

      if (saveFinalShaders) {
         saveShader(var0, var5.toString());
      }

      ARBShaderObjects.glShaderSourceARB(var4, var5);
      ARBShaderObjects.glCompileShaderARB(var4);
      if (GL20.glGetShaderi(var4, 35713) != 1) {
         SMCLog.severe("Error compiling fragment shader: " + var0);
      }

      printShaderLogInfo(var4, var0, var8);
      return var4;
   }

   private static void saveShader(String var0, String var1) {
      try {
         File var4 = new File(shaderpacksdir, "debug/" + var0);
         var4.getParentFile().mkdirs();
         Config.writeFile(var4, var1);
      } catch (IOException var5) {
         Config.warn("Error saving: " + var0);
         var5.printStackTrace();
      }
   }

   private static void clearDirectory(File var0) {
      if (var0.exists() && var0.isDirectory()) {
         File[] var4 = var0.listFiles();
         if (var4 != null) {
            for (int var5 = 0; var5 < var4.length; var5++) {
               File var6 = var4[var5];
               if (var6.isDirectory()) {
                  clearDirectory(var6);
               }

               var6.delete();
            }
         }
      }
   }

   private static boolean printLogInfo(int var0, String var1) {
      IntBuffer var5 = BufferUtils.createIntBuffer(1);
      ARBShaderObjects.glGetObjectParameterARB(var0, 35716, var5);
      int var6 = var5.get();
      if (var6 > 1) {
         ByteBuffer var7 = BufferUtils.createByteBuffer(var6);
         ((Buffer)var5).flip();
         ARBShaderObjects.glGetInfoLogARB(var0, var5, var7);
         byte[] var8 = new byte[var6];
         var7.get(var8);
         if (var8[var6 - 1] == 0) {
            var8[var6 - 1] = 10;
         }

         String var9 = new String(var8);
         SMCLog.info("Info log: " + var1 + "\n" + var9);
         return false;
      } else {
         return true;
      }
   }

   private static boolean printShaderLogInfo(int var0, String var1, List<String> var2) {
      IntBuffer var6 = BufferUtils.createIntBuffer(1);
      int var7 = GL20.glGetShaderi(var0, 35716);
      if (var7 <= 1) {
         return true;
      }

      for (int var8 = 0; var8 < var2.size(); var8++) {
         String var9 = (String)var2.get(var8);
         SMCLog.info("File: " + (var8 + 1) + " = " + var9);
      }

      String var10 = GL20.glGetShaderInfoLog(var0, var7);
      SMCLog.info("Shader info log: " + var1 + "\n" + var10);
      return false;
   }

   public static void setDrawBuffers(IntBuffer var0) {
      if (var0 == null) {
         var0 = drawBuffersNone;
      }

      if (activeDrawBuffers != var0) {
         activeDrawBuffers = var0;
         GL20.glDrawBuffers(var0);
      }
   }

   public static void useProgram(int var0) {
      checkGLError("pre-useProgram");
      if (isShadowPass) {
         var0 = 30;
         if (programsID[30] == 0) {
            en = false;
            return;
         }
      }

      if (activeProgram != var0) {
         activeProgram = var0;
         ARBShaderObjects.glUseProgramObjectARB(programsID[var0]);
         if (programsID[var0] == 0) {
            en = false;
         } else {
            if (checkGLError("useProgram ", programNames[var0]) != 0) {
               programsID[var0] = 0;
            }

            IntBuffer var4 = programsDrawBuffers[var0];
            if (Oo) {
               setDrawBuffers(var4);
               checkGLError(programNames[var0], " draw buffers = ", programsDrawBufSettings[var0]);
            }

            activeCompositeMipmapSetting = programsCompositeMipmapSetting[var0];
            uniformEntityColor.setProgram(programsID[activeProgram]);
            uniformEntityId.setProgram(programsID[activeProgram]);
            uniformBlockEntityId.setProgram(programsID[activeProgram]);
            switch (var0) {
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 16:
               case 18:
               case 19:
               case 20:
                  en = true;
                  setProgramUniform1i("texture", 0);
                  setProgramUniform1i("lightmap", 1);
                  setProgramUniform1i("normals", 2);
                  setProgramUniform1i("specular", 3);
                  setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i("watershadow", 4);
                  setProgramUniform1i("shadowtex0", 4);
                  setProgramUniform1i("shadowtex1", 5);
                  setProgramUniform1i("depthtex0", 6);
                  if (k != null) {
                     setProgramUniform1i("gaux1", 7);
                     setProgramUniform1i("gaux2", 8);
                     setProgramUniform1i("gaux3", 9);
                     setProgramUniform1i("gaux4", 10);
                  }

                  setProgramUniform1i("depthtex1", 12);
                  setProgramUniform1i("shadowcolor", 13);
                  setProgramUniform1i("shadowcolor0", 13);
                  setProgramUniform1i("shadowcolor1", 14);
                  setProgramUniform1i("noisetex", 15);
                  break;
               case 14:
               case 15:
               case 17:
               default:
                  en = false;
                  break;
               case 21:
               case 22:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
                  en = false;
                  setProgramUniform1i("gcolor", 0);
                  setProgramUniform1i("gdepth", 1);
                  setProgramUniform1i("gnormal", 2);
                  setProgramUniform1i("composite", 3);
                  setProgramUniform1i("gaux1", 7);
                  setProgramUniform1i("gaux2", 8);
                  setProgramUniform1i("gaux3", 9);
                  setProgramUniform1i("gaux4", 10);
                  setProgramUniform1i("colortex0", 0);
                  setProgramUniform1i("colortex1", 1);
                  setProgramUniform1i("colortex2", 2);
                  setProgramUniform1i("colortex3", 3);
                  setProgramUniform1i("colortex4", 7);
                  setProgramUniform1i("colortex5", 8);
                  setProgramUniform1i("colortex6", 9);
                  setProgramUniform1i("colortex7", 10);
                  setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i("watershadow", 4);
                  setProgramUniform1i("shadowtex0", 4);
                  setProgramUniform1i("shadowtex1", 5);
                  setProgramUniform1i("gdepthtex", 6);
                  setProgramUniform1i("depthtex0", 6);
                  setProgramUniform1i("depthtex1", 11);
                  setProgramUniform1i("depthtex2", 12);
                  setProgramUniform1i("shadowcolor", 13);
                  setProgramUniform1i("shadowcolor0", 13);
                  setProgramUniform1i("shadowcolor1", 14);
                  setProgramUniform1i("noisetex", 15);
                  break;
               case 30:
               case 31:
               case 32:
                  setProgramUniform1i("tex", 0);
                  setProgramUniform1i("texture", 0);
                  setProgramUniform1i("lightmap", 1);
                  setProgramUniform1i("normals", 2);
                  setProgramUniform1i("specular", 3);
                  setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i("watershadow", 4);
                  setProgramUniform1i("shadowtex0", 4);
                  setProgramUniform1i("shadowtex1", 5);
                  if (k != null) {
                     setProgramUniform1i("gaux1", 7);
                     setProgramUniform1i("gaux2", 8);
                     setProgramUniform1i("gaux3", 9);
                     setProgramUniform1i("gaux4", 10);
                  }

                  setProgramUniform1i("shadowcolor", 13);
                  setProgramUniform1i("shadowcolor0", 13);
                  setProgramUniform1i("shadowcolor1", 14);
                  setProgramUniform1i("noisetex", 15);
            }

            ItemStack var5 = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
            Item var6 = var5 != null ? var5.getItem() : null;
            int var7 = -1;
            Block var8 = null;
            if (var6 != null) {
               var7 = Item.itemRegistry.getIDForObject(var6);
               var8 = Block.blockRegistry.getObjectById(var7);
            }

            int var9 = var8 != null ? var8.getLightValue() : 0;
            setProgramUniform1i("heldItemId", var7);
            setProgramUniform1i("heldBlockLightValue", var9);
            setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
            setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
            setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
            setProgramUniform1i("worldTime", (int)(Ol % 24000L));
            setProgramUniform1i("worldDay", (int)(Ol / 24000L));
            setProgramUniform1i("moonPhase", A);
            setProgramUniform1i("frameCounter", uy);
            setProgramUniform1f("frameTime", frameTime);
            setProgramUniform1f("frameTimeCounter", frameTimeCounter);
            setProgramUniform1f("sunAngle", sunAngle);
            setProgramUniform1f("shadowAngle", shadowAngle);
            setProgramUniform1f("rainStrength", rainStrength);
            setProgramUniform1f("aspectRatio", (float)renderWidth / renderHeight);
            setProgramUniform1f("viewWidth", renderWidth);
            setProgramUniform1f("viewHeight", renderHeight);
            setProgramUniform1f("near", 0.05F);
            setProgramUniform1f("far", mc.gameSettings.renderDistanceChunks * 16);
            setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
            setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
            setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
            setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
            setProgramUniform3f("previousCameraPosition", (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
            setProgramUniform3f("cameraPosition", (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
            setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
            setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
            setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
            setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
            setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
            setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
            if (OF > 0) {
               setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
               setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
               setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
               setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
            }

            setProgramUniform1f("wetness", wetness);
            setProgramUniform1f("eyeAltitude", eyePosY);
            setProgramUniform2i("eyeBrightness", e1 & 65535, e1 >> 16);
            setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
            setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
            setProgramUniform1i("terrainIconSize", Bb);
            setProgramUniform1i("isEyeInWater", u1);
            setProgramUniform1f("nightVision", nightVision);
            setProgramUniform1f("blindness", blindness);
            setProgramUniform1f("screenBrightness", mc.gameSettings.Zu);
            setProgramUniform1i("hideGUI", mc.gameSettings.Zn ? 1 : 0);
            setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
            setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);
            checkGLError("useProgram ", programNames[var0]);
         }
      }
   }

   public static void setProgramUniform1i(String var0, int var1) {
      int var5 = programsID[activeProgram];
      if (var5 != 0) {
         int var6 = ARBShaderObjects.glGetUniformLocationARB(var5, var0);
         ARBShaderObjects.glUniform1iARB(var6, var1);
         checkGLError(programNames[activeProgram], var0);
      }
   }

   public static void setProgramUniform2i(String var0, int var1, int var2) {
      int var6 = programsID[activeProgram];
      if (var6 != 0) {
         int var7 = ARBShaderObjects.glGetUniformLocationARB(var6, var0);
         ARBShaderObjects.glUniform2iARB(var7, var1, var2);
         checkGLError(programNames[activeProgram], var0);
      }
   }

   public static void setProgramUniform1f(String var0, float var1) {
      int var5 = programsID[activeProgram];
      if (var5 != 0) {
         int var6 = ARBShaderObjects.glGetUniformLocationARB(var5, var0);
         ARBShaderObjects.glUniform1fARB(var6, var1);
         checkGLError(programNames[activeProgram], var0);
      }
   }

   public static void setProgramUniform3f(String var0, float var1, float var2, float var3) {
      int var7 = programsID[activeProgram];
      if (var7 != 0) {
         int var8 = ARBShaderObjects.glGetUniformLocationARB(var7, var0);
         ARBShaderObjects.glUniform3fARB(var8, var1, var2, var3);
         checkGLError(programNames[activeProgram], var0);
      }
   }

   public static void setProgramUniformMatrix4ARB(String var0, boolean var1, FloatBuffer var2) {
      int var5 = programsID[activeProgram];
      if (var5 != 0 && var2 != null) {
         int var6 = ARBShaderObjects.glGetUniformLocationARB(var5, var0);
         ARBShaderObjects.glUniformMatrix4ARB(var6, var1, var2);
         checkGLError(programNames[activeProgram], var0);
      }
   }

   private static int getBufferIndexFromString(String var0) {
      return var0.equals("colortex0") || var0.equals("gcolor")
         ? 0
         : (
            var0.equals("colortex1") || var0.equals("gdepth")
               ? 1
               : (
                  var0.equals("colortex2") || var0.equals("gnormal")
                     ? 2
                     : (
                        var0.equals("colortex3") || var0.equals("composite")
                           ? 3
                           : (
                              var0.equals("colortex4") || var0.equals("gaux1")
                                 ? 4
                                 : (
                                    var0.equals("colortex5") || var0.equals("gaux2")
                                       ? 5
                                       : (
                                          !var0.equals("colortex6") && !var0.equals("gaux3")
                                             ? (!var0.equals("colortex7") && !var0.equals("gaux4") ? -1 : 7)
                                             : 6
                                       )
                                 )
                           )
                     )
               )
         );
   }

   private static int getTextureFormatFromString(String var0) {
      var0 = var0.trim();

      for (int var4 = 0; var4 < formatNames.length; var4++) {
         String var5 = formatNames[var4];
         if (var0.equals(var5)) {
            return formatIds[var4];
         }
      }

      return 0;
   }

   private static void F() {
      if (customUniforms == null) {
         customUniforms = new HFNoiseTexture(uT, uT);
      }
   }

   private static void loadEntityDataMap() {
      mapBlockToEntityData = new IdentityHashMap<>(300);
      if (mapBlockToEntityData.isEmpty()) {
         for (ResourceLocation var4 : Block.blockRegistry.getKeys()) {
            Block var5 = Block.blockRegistry.getObject(var4);
            int var6 = Block.blockRegistry.getIDForObject(var5);
            mapBlockToEntityData.put(var5, var6);
         }
      }

      BufferedReader var13 = null;

      try {
         var13 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
      } catch (Exception var11) {
      }

      if (var13 != null) {
         String var14;
         try {
            while ((var14 = var13.readLine()) != null) {
               Matcher var15 = patternLoadEntityDataMap.matcher(var14);
               if (var15.matches()) {
                  String var16 = var15.group(1);
                  String var7 = var15.group(2);
                  int var8 = Integer.parseInt(var7);
                  Block var9 = Block.getBlockFromName(var16);
                  if (var9 != null) {
                     mapBlockToEntityData.put(var9, var8);
                  } else {
                     SMCLog.warning("Unknown block name %s", var16);
                  }
               } else {
                  SMCLog.warning("unmatched %s\n", var14);
               }
            }
         } catch (Exception var12) {
            SMCLog.warning("Error parsing mc_Entity_x.txt");
         }
      }

      if (var13 != null) {
         try {
            var13.close();
         } catch (Exception var10) {
         }
      }
   }

   private static IntBuffer fillIntBufferZero(IntBuffer var0) {
      int var4 = var0.limit();

      for (int var5 = var0.position(); var5 < var4; var5++) {
         var0.put(var5, 0);
      }

      return var0;
   }

   public static void uninit() {
      long var0 = ab ^ 79845210814306L;
      if (T) {
         checkGLError("Shaders.uninit pre");

         for (int var3 = 0; var3 < 33; var3++) {
            if (programsRef[var3] != 0) {
               ARBShaderObjects.glDeleteObjectARB(programsRef[var3]);
               checkGLError("del programRef");
            }

            programsRef[var3] = 0;
            programsID[var3] = 0;
            programsDrawBufSettings[var3] = null;
            programsDrawBuffers[var3] = null;
            programsCompositeMipmapSetting[var3] = 0;
         }

         if (OU != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(OU);
            OU = 0;
            long var10000 = 1512024894183728989L ^ var0;
            checkGLError("del dfb");
         }

         if (n != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(n);
            n = 0;
            long var4 = 6022770432562883124L ^ var0;
            checkGLError("del sfb");
         }

         if (dfbDepthTextures != null) {
            GlStateManager.deleteTextures(dfbDepthTextures);
            fillIntBufferZero(dfbDepthTextures);
            long var5 = 7123761486053897550L ^ var0;
            checkGLError("del dfbDepthTextures");
         }

         if (dfbColorTextures != null) {
            GlStateManager.deleteTextures(dfbColorTextures);
            fillIntBufferZero(dfbColorTextures);
            long var6 = 3815955104418448548L ^ var0;
            checkGLError("del dfbTextures");
         }

         if (sfbDepthTextures != null) {
            GlStateManager.deleteTextures(sfbDepthTextures);
            fillIntBufferZero(sfbDepthTextures);
            long var7 = 2415412740170792913L ^ var0;
            checkGLError("del shadow depth");
         }

         if (sfbColorTextures != null) {
            GlStateManager.deleteTextures(sfbColorTextures);
            fillIntBufferZero(sfbColorTextures);
            long var8 = 2367456498113225048L ^ var0;
            checkGLError("del shadow color");
         }

         if (dfbDrawBuffers != null) {
            fillIntBufferZero(dfbDrawBuffers);
         }

         if (customUniforms != null) {
            customUniforms.C();
            customUniforms = null;
         }

         SMCLog.info("Uninit");
         OO = 0;
         long var9 = 6137282802183992436L ^ var0;
         shouldSkipDefaultShadow = false;
         T = false;
         checkGLError("Shaders.uninit");
      }
   }

   public static void L() {
      eK = 0;
   }

   public static void Ue() {
      R = true;
   }

   private static void c() {
      O9 = mc.displayWidth;
      eK = mc.displayHeight;
      renderWidth = Math.round(O9 * configRenderResMul);
      renderHeight = Math.round(eK * configRenderResMul);
      setupFrameBuffer();
   }

   private static void z() {
      R = false;
      Ow = Math.round(eS * configShadowResMul);
      e5 = Math.round(uc * configShadowResMul);
      setupShadowFrameBuffer();
   }

   private static void setupFrameBuffer() {
      long var0 = ab ^ 70205496216623L;
      if (OU != 0) {
         EXTFramebufferObject.glDeleteFramebuffersEXT(OU);
         GlStateManager.deleteTextures(dfbDepthTextures);
         GlStateManager.deleteTextures(dfbColorTextures);
      }

      OU = EXTFramebufferObject.glGenFramebuffersEXT();
      GL11.glGenTextures((IntBuffer)((Buffer)dfbDepthTextures).clear().limit(ew));
      GL11.glGenTextures((IntBuffer)((Buffer)dfbColorTextures).clear().limit(16));
      ((Buffer)dfbDepthTextures).position(0);
      long var10000 = 6861680710719545432L ^ var0;
      ((Buffer)dfbColorTextures).position(0);
      ((Buffer)dfbColorTextures.get(dfbColorTexturesA)).position(0);
      EXTFramebufferObject.glBindFramebufferEXT(36160, OU);
      GL20.glDrawBuffers(0);
      GL11.glReadBuffer(0);

      for (int var3 = 0; var3 < ew; var3++) {
         GlStateManager.bindTexture(dfbDepthTextures.get(var3));
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 34891, 6409);
         GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5126, (FloatBuffer)null);
      }

      EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
      GL20.glDrawBuffers(dfbDrawBuffers);
      var10000 = 2338009669225048793L ^ var0;
      GL11.glReadBuffer(0);
      checkGLError("FT d");

      for (int var5 = 0; var5 < usedColorBuffers; var5++) {
         GlStateManager.bindTexture(dfbColorTexturesA[var5]);
         GL11.glTexParameteri(3553, 10242, 10496);
         var10000 = 387916199797233478L ^ var0;
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[var5], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var5, 3553, dfbColorTexturesA[var5], 0);
         checkGLError("FT c");
      }

      for (int var6 = 0; var6 < usedColorBuffers; var6++) {
         GlStateManager.bindTexture(dfbColorTexturesA[8 + var6]);
         GL11.glTexParameteri(3553, 10242, 10496);
         var10000 = 412326745786729193L ^ var0;
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[var6], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
         checkGLError("FT ca");
      }

      int var7 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (var7 == 36058) {
         printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

         for (int var4 = 0; var4 < usedColorBuffers; var4++) {
            GlStateManager.bindTexture(dfbColorTextures.get(var4));
            GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            var10000 = 4708018777945534559L ^ var0;
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var4, 3553, dfbColorTextures.get(var4), 0);
            checkGLError("FT c");
         }

         var7 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (var7 == 36053) {
            SMCLog.info("complete");
         }
      }

      GlStateManager.bindTexture(0);
      if (var7 != 36053) {
         printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + var7 + ")");
      } else {
         SMCLog.info("Framebuffer created.");
      }
   }

   private static void setupShadowFrameBuffer() {
      long var0 = ab ^ 125555370002537L;
      if (OF != 0) {
         if (n != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(n);
            GlStateManager.deleteTextures(sfbDepthTextures);
            GlStateManager.deleteTextures(sfbColorTextures);
         }

         n = EXTFramebufferObject.glGenFramebuffersEXT();
         EXTFramebufferObject.glBindFramebufferEXT(36160, n);
         GL11.glDrawBuffer(0);
         GL11.glReadBuffer(0);
         GL11.glGenTextures((IntBuffer)((Buffer)sfbDepthTextures).clear().limit(OF));
         GL11.glGenTextures((IntBuffer)((Buffer)sfbColorTextures).clear().limit(uQ));
         ((Buffer)sfbDepthTextures).position(0);
         ((Buffer)sfbColorTextures).position(0);

         for (int var3 = 0; var3 < OF; var3++) {
            GlStateManager.bindTexture(sfbDepthTextures.get(var3));
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            int var4 = shadowFilterNearest[var3] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, var4);
            GL11.glTexParameteri(3553, 10240, var4);
            if (shadowHardwareFilteringEnabled[var3]) {
               GL11.glTexParameteri(3553, 34892, 34894);
            }

            GL11.glTexImage2D(3553, 0, 6402, Ow, e5, 0, 6402, 5126, (FloatBuffer)null);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         checkGLError("FT sd");

         for (int var5 = 0; var5 < uQ; var5++) {
            GlStateManager.bindTexture(sfbColorTextures.get(var5));
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            int var7 = shadowColorFilterNearest[var5] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, var7);
            long var10000 = 6996969119230614836L ^ var0;
            GL11.glTexParameteri(3553, 10240, var7);
            GL11.glTexImage2D(3553, 0, 6408, Ow, e5, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var5, 3553, sfbColorTextures.get(var5), 0);
            checkGLError("FT sc");
         }

         GlStateManager.bindTexture(0);
         if (uQ > 0) {
            GL20.glDrawBuffers(sfbDrawBuffers);
         }

         int var6 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (var6 != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + var6 + ")");
         } else {
            SMCLog.info("Shadow framebuffer created.");
         }
      }
   }

   public static void beginRender(Minecraft var0, float var1, long var2) {
      long var4 = ab ^ 73294047454880L;
      checkGLError("pre beginRender");
      checkWorldChanged(mc.theWorld);
      mc = var0;
      mc.mcProfiler.startSection("init");
      entityRenderer = mc.entityRenderer;
      if (!T) {
         try {
            init();
         } catch (IllegalStateException var12) {
            if (Config.normalize(var12.getMessage()).equals("Function is not supported")) {
               printChatAndLogError("[Shaders] Error: " + var12.getMessage());
               var12.printStackTrace();
               setShaderPack(packNameNone);
               return;
            }
         }
      }

      if (mc.displayWidth != O9 || mc.displayHeight != eK) {
         c();
      }

      if (R) {
         z();
      }

      Ol = mc.theWorld.getWorldTime();
      B9 = (Ol - u2) % 24000L;
      if (B9 < 0L) {
         B9 += 24000L;
      }

      u2 = Ol;
      A = mc.theWorld.f();
      uy++;
      if (uy >= 720720) {
         uy = 0;
      }

      ug = System.currentTimeMillis();
      if (BU == 0L) {
         BU = ug;
      }

      diffSystemTime = ug - BU;
      BU = ug;
      frameTime = (float)diffSystemTime / 1000.0F;
      frameTimeCounter = frameTimeCounter + frameTime;
      frameTimeCounter %= 3600.0F;
      rainStrength = var0.theWorld.getRainStrength(var1);
      float var7 = (float)diffSystemTime * 0.01F;
      float var8 = (float)Math.exp(Math.log(0.5) * var7 / (wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
      wetness = wetness * var8 + rainStrength * (1.0F - var8);
      Entity var9 = mc.getRenderViewEntity();
      if (var9 != null) {
         uH = var9 instanceof EntityLivingBase && ((EntityLivingBase)var9).isPlayerSleeping();
         eyePosY = (float)var9.posY * var1 + (float)var9.lastTickPosY * (1.0F - var1);
         e1 = var9.getBrightnessForRender(var1);
         var8 = (float)diffSystemTime * 0.01F;
         float var10 = (float)Math.exp(Math.log(0.5) * var8 / eyeBrightnessHalflife);
         eyeBrightnessFadeX = eyeBrightnessFadeX * var10 + (e1 & 65535) * (1.0F - var10);
         eyeBrightnessFadeY = eyeBrightnessFadeY * var10 + (e1 >> 16) * (1.0F - var10);
         u1 = mc.gameSettings.thirdPersonView == 0 && !uH && mc.thePlayer.isInsideOfMaterial(Material.water) ? 1 : 0;
         if (mc.thePlayer != null) {
            nightVision = 0.0F;
            if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
               nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(mc.thePlayer, var1);
            }

            blindness = 0.0F;
            if (mc.thePlayer.isPotionActive(Potion.blindness)) {
               int var11 = mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
               blindness = Config.limit(var11 / 20.0F, 0.0F, 1.0F);
            }
         }

         Vec3 var18 = mc.theWorld.getSkyColor(var9, var1);
         var18 = CustomColors.getWorldSkyColor(var18, currentWorld, var9, var1);
         skyColorR = (float)var18.xCoord;
         skyColorG = (float)var18.yCoord;
         skyColorB = (float)var18.zCoord;
      }

      isRenderingWorld = true;
      es = false;
      t = false;
      if (OF >= 1) {
         GlStateManager.setActiveTexture(33988);
         GlStateManager.bindTexture(sfbDepthTextures.get(0));
         if (OF >= 2) {
            GlStateManager.setActiveTexture(33989);
            GlStateManager.bindTexture(sfbDepthTextures.get(1));
         }
      }

      GlStateManager.setActiveTexture(33984);

      for (int var14 = 0; var14 < usedColorBuffers; var14++) {
         GlStateManager.bindTexture(dfbColorTexturesA[var14]);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         GlStateManager.bindTexture(dfbColorTexturesA[8 + var14]);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      GlStateManager.bindTexture(0);

      for (int var15 = 0; var15 < 4 && 4 + var15 < usedColorBuffers; var15++) {
         GlStateManager.setActiveTexture(33991 + var15);
         GlStateManager.bindTexture(dfbColorTextures.get(4 + var15));
      }

      GlStateManager.setActiveTexture(33990);
      GlStateManager.bindTexture(dfbDepthTextures.get(0));
      if (ew >= 2) {
         GlStateManager.setActiveTexture(33995);
         GlStateManager.bindTexture(dfbDepthTextures.get(1));
         if (ew >= 3) {
            GlStateManager.setActiveTexture(33996);
            GlStateManager.bindTexture(dfbDepthTextures.get(2));
         }
      }

      for (int var16 = 0; var16 < uQ; var16++) {
         GlStateManager.setActiveTexture(33997 + var16);
         GlStateManager.bindTexture(sfbColorTextures.get(var16));
      }

      if (E) {
         GlStateManager.setActiveTexture(33984 + customUniforms.X);
         GlStateManager.bindTexture(customUniforms.y());
         long var10000 = 734723517477624750L ^ var4;
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      bindCustomTextures(k);
      GlStateManager.setActiveTexture(33984);
      long var20 = 5077011468779051961L ^ var4;
      previousCameraPositionX = cameraPositionX;
      previousCameraPositionY = cameraPositionY;
      previousCameraPositionZ = cameraPositionZ;
      ((Buffer)previousProjection).position(0);
      ((Buffer)projection).position(0);
      previousProjection.put(projection);
      ((Buffer)previousProjection).position(0);
      ((Buffer)projection).position(0);
      ((Buffer)previousModelView).position(0);
      ((Buffer)modelView).position(0);
      previousModelView.put(modelView);
      ((Buffer)previousModelView).position(0);
      ((Buffer)modelView).position(0);
      checkGLError("beginRender");
      ShadersRender.renderShadowMap(entityRenderer, 0, var1, var2);
      var20 = 6861774100958202583L ^ var4;
      mc.mcProfiler.endSection();
      EXTFramebufferObject.glBindFramebufferEXT(36160, OU);

      for (int var17 = 0; var17 < usedColorBuffers; var17++) {
         colorTexturesToggle[var17] = 0;
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var17, 3553, dfbColorTexturesA[var17], 0);
      }

      checkGLError("end beginRender");
   }

   private static void checkWorldChanged(World var0) {
      if (currentWorld != var0) {
         World var4 = currentWorld;
         currentWorld = var0;
         if (var4 != null && var0 != null) {
            int var5 = var4.provider.getDimensionId();
            int var6 = var0.provider.getDimensionId();
            boolean var7 = shaderPackDimensions.contains(var5);
            boolean var8 = shaderPackDimensions.contains(var6);
            if (var7 || var8) {
               uninit();
            }
         }
      }
   }

   public static void beginRenderPass(int var0, float var1, long var2) {
      long var4 = ab ^ 130101795345342L;
      if (!isShadowPass) {
         EXTFramebufferObject.glBindFramebufferEXT(36160, OU);
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         long var10000 = 309093164530693827L ^ var4;
         activeDrawBuffers = null;
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
         useProgram(2);
         checkGLError("end beginRenderPass");
      }
   }

   public static void setViewport(int var0, int var1, int var2, int var3) {
      long var4 = ab ^ 131649848471774L;
      GlStateManager.colorMask(true, true, true, true);
      if (isShadowPass) {
         GL11.glViewport(0, 0, Ow, e5);
      } else {
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         EXTFramebufferObject.glBindFramebufferEXT(36160, OU);
         long var10000 = 505195019553366868L ^ var4;
         Oo = true;
         GlStateManager.enableCull();
         GlStateManager.enableDepth();
         setDrawBuffers(drawBuffersNone);
         useProgram(2);
         checkGLError("beginRenderPass");
      }
   }

   public static int setFogMode(int var0) {
      fogMode = var0;
      return var0;
   }

   public static void setFogColor(float var0, float var1, float var2) {
      fogColorR = var0;
      fogColorG = var1;
      fogColorB = var2;
   }

   public static void v(float var0, float var1, float var2, float var3) {
      GlStateManager.clearColor(var0, var1, var2, var3);
      clearColorR = var0;
      clearColorG = var1;
      clearColorB = var2;
   }

   public static void clearRenderBuffer() {
      long var0 = ab ^ 15768985337581L;
      if (isShadowPass) {
         checkGLError("shadow clear pre");
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         long var10000 = 6039386424057679433L ^ var0;
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL20.glDrawBuffers(programsDrawBuffers[30]);
         checkFramebufferStatus("shadow clear");
         GL11.glClear(16640);
         var10000 = 1674043447363706120L ^ var0;
         checkGLError("shadow clear");
      } else {
         checkGLError("clear pre");
         if (gbuffersClear[0]) {
            GL20.glDrawBuffers(36064);
            GL11.glClear(16384);
         }

         if (gbuffersClear[1]) {
            GL20.glDrawBuffers(36065);
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            long var5 = 8306966841824056243L ^ var0;
            GL11.glClear(16384);
         }

         for (int var3 = 2; var3 < usedColorBuffers; var3++) {
            if (gbuffersClear[var3]) {
               GL20.glDrawBuffers(36064 + var3);
               GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               long var6 = 8306966841824056243L ^ var0;
               GL11.glClear(16384);
            }
         }

         setDrawBuffers(dfbDrawBuffers);
         checkFramebufferStatus("clear");
         checkGLError("clear");
      }
   }

   public static void setCamera(float var0) {
      Entity var3 = mc.getRenderViewEntity();
      double var4 = var3.lastTickPosX + (var3.posX - var3.lastTickPosX) * var0;
      double var6 = var3.lastTickPosY + (var3.posY - var3.lastTickPosY) * var0;
      double var8 = var3.lastTickPosZ + (var3.posZ - var3.lastTickPosZ) * var0;
      cameraPositionX = var4;
      cameraPositionY = var6;
      cameraPositionZ = var8;
      GL11.glGetFloat(2983, (FloatBuffer)((Buffer)projection).position(0));
      SMath.invertMat4FBFA(
         (FloatBuffer)((Buffer)projectionInverse).position(0), (FloatBuffer)((Buffer)projection).position(0), faProjectionInverse, faProjection
      );
      ((Buffer)projection).position(0);
      ((Buffer)projectionInverse).position(0);
      GL11.glGetFloat(2982, (FloatBuffer)((Buffer)modelView).position(0));
      SMath.invertMat4FBFA((FloatBuffer)((Buffer)modelViewInverse).position(0), (FloatBuffer)((Buffer)modelView).position(0), faModelViewInverse, faModelView);
      ((Buffer)modelView).position(0);
      ((Buffer)modelViewInverse).position(0);
      checkGLError("setCamera");
   }

   public static void setCameraShadow(float var0) {
      long var1 = ab ^ 127266551363225L;
      Entity var4 = mc.getRenderViewEntity();
      double var5 = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * var0;
      double var7 = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * var0;
      double var9 = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * var0;
      cameraPositionX = var5;
      cameraPositionY = var7;
      cameraPositionZ = var9;
      GL11.glGetFloat(2983, (FloatBuffer)((Buffer)projection).position(0));
      SMath.invertMat4FBFA(
         (FloatBuffer)((Buffer)projectionInverse).position(0), (FloatBuffer)((Buffer)projection).position(0), faProjectionInverse, faProjection
      );
      ((Buffer)projection).position(0);
      ((Buffer)projectionInverse).position(0);
      GL11.glGetFloat(2982, (FloatBuffer)((Buffer)modelView).position(0));
      SMath.invertMat4FBFA((FloatBuffer)((Buffer)modelViewInverse).position(0), (FloatBuffer)((Buffer)modelView).position(0), faModelViewInverse, faModelView);
      ((Buffer)modelView).position(0);
      ((Buffer)modelViewInverse).position(0);
      GL11.glViewport(0, 0, Ow, e5);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      if (Bj) {
         GL11.glOrtho(-shadowMapHalfPlane, shadowMapHalfPlane, -shadowMapHalfPlane, shadowMapHalfPlane, 0.05F, 256.0);
      } else {
         GLU.gluPerspective(shadowMapFOV, (float)Ow / e5, 0.05F, 256.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -100.0F);
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      celestialAngle = mc.theWorld.getCelestialAngle(var0);
      sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
      float var11 = celestialAngle * -360.0F;
      float var12 = shadowAngleInterval > 0.0F ? var11 % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;
      if (sunAngle <= 0.5) {
         GL11.glRotatef(var11 - var12, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle;
      } else {
         GL11.glRotatef(var11 + 180.0F - var12, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle - 0.5F;
      }

      if (Bj) {
         float var13 = shadowIntervalSize;
         float var14 = var13 / 2.0F;
         GL11.glTranslatef((float)var5 % var13 - var14, (float)var7 % var13 - var14, (float)var9 % var13 - var14);
      }

      float var20 = sunAngle * (float) (Math.PI * 2);
      float var21 = (float)Math.cos(var20);
      float var15 = (float)Math.sin(var20);
      float var16 = sunPathRotation * (float) (Math.PI * 2);
      float var17 = var21;
      float var18 = var15 * (float)Math.cos(var16);
      float var19 = var15 * (float)Math.sin(var16);
      if (sunAngle > 0.5) {
         var17 = -var21;
         var18 = -var18;
         var19 = -var19;
      }

      shadowLightPositionVector[0] = var17;
      shadowLightPositionVector[1] = var18;
      long var10000 = 7699016750714902762L ^ var1;
      shadowLightPositionVector[2] = var19;
      shadowLightPositionVector[3] = 0.0F;
      GL11.glGetFloat(2983, (FloatBuffer)((Buffer)shadowProjection).position(0));
      SMath.invertMat4FBFA(
         (FloatBuffer)((Buffer)shadowProjectionInverse).position(0),
         (FloatBuffer)((Buffer)shadowProjection).position(0),
         faShadowProjectionInverse,
         faShadowProjection
      );
      ((Buffer)shadowProjection).position(0);
      ((Buffer)shadowProjectionInverse).position(0);
      GL11.glGetFloat(2982, (FloatBuffer)((Buffer)shadowModelView).position(0));
      SMath.invertMat4FBFA(
         (FloatBuffer)((Buffer)shadowModelViewInverse).position(0),
         (FloatBuffer)((Buffer)shadowModelView).position(0),
         faShadowModelViewInverse,
         faShadowModelView
      );
      ((Buffer)shadowModelView).position(0);
      ((Buffer)shadowModelViewInverse).position(0);
      setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
      setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
      setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
      setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
      setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
      setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
      setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
      setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
      setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
      setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
      mc.gameSettings.thirdPersonView = 1;
      var10000 = 2309449956565160364L ^ var1;
      checkGLError("setCamera");
   }

   public static void preCelestialRotate() {
      GL11.glRotatef(sunPathRotation * 1.0F, 0.0F, 0.0F, 1.0F);
      checkGLError("preCelestialRotate");
   }

   public static void postCelestialRotate() {
      FloatBuffer var3 = tempMatrixDirectBuffer;
      ((Buffer)var3).clear();
      GL11.glGetFloat(2982, var3);
      var3.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
      SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
      System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
      setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
      setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
      setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
      checkGLError("postCelestialRotate");
   }

   public static void setUpPosition() {
      FloatBuffer var2 = tempMatrixDirectBuffer;
      ((Buffer)var2).clear();
      GL11.glGetFloat(2982, var2);
      var2.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
      setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
   }

   public static void genCompositeMipmap() {
      if (hasGlGenMipmap) {
         for (int var3 = 0; var3 < usedColorBuffers; var3++) {
            if ((activeCompositeMipmapSetting & 1 << var3) != 0) {
               GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[var3]);
               GL11.glTexParameteri(3553, 10241, 9987);
               GL30.glGenerateMipmap(3553);
            }
         }

         GlStateManager.setActiveTexture(33984);
      }
   }

   public static void drawComposite() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(7);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(1.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.0F, 1.0F, 0.0F);
      GL11.glEnd();
   }

   public static void renderFinal() {
      long var0 = ab ^ 17978729602710L;
      if (!isShadowPass) {
         checkGLError("pre-renderCompositeFinal");
         GL11.glPushMatrix();
         long var10000 = 998735974898649231L ^ var0;
         GL11.glLoadIdentity();
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         var10000 = 7116232101340627096L ^ var0;
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(519);
         GlStateManager.depthMask(false);
         GlStateManager.disableLighting();
         if (OF >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (OF >= 2) {
               GlStateManager.setActiveTexture(33989);
               GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
         }

         for (int var3 = 0; var3 < usedColorBuffers; var3++) {
            GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[var3]);
            GlStateManager.bindTexture(dfbColorTexturesA[var3]);
         }

         GlStateManager.setActiveTexture(33990);
         GlStateManager.bindTexture(dfbDepthTextures.get(0));
         if (ew >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (ew >= 3) {
               GlStateManager.setActiveTexture(33996);
               GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
         }

         for (int var8 = 0; var8 < uQ; var8++) {
            GlStateManager.setActiveTexture(33997 + var8);
            GlStateManager.bindTexture(sfbColorTextures.get(var8));
         }

         if (E) {
            GlStateManager.setActiveTexture(33984 + customUniforms.X);
            GlStateManager.bindTexture(customUniforms.y());
            var10000 = 734669298374681496L ^ var0;
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
         }

         bindCustomTextures(e4);
         GlStateManager.setActiveTexture(33984);
         boolean var9 = true;

         for (int var4 = 0; var4 < usedColorBuffers; var4++) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var4, 3553, dfbColorTexturesA[8 + var4], 0);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
         GL20.glDrawBuffers(dfbDrawBuffers);
         var10000 = 773634885329984478L ^ var0;
         checkGLError("pre-composite");

         for (int var10 = 0; var10 < 8; var10++) {
            if (programsID[21 + var10] != 0) {
               useProgram(21 + var10);
               checkGLError(programNames[21 + var10]);
               if (activeCompositeMipmapSetting != 0) {
                  genCompositeMipmap();
               }

               drawComposite();

               for (int var5 = 0; var5 < usedColorBuffers; var5++) {
                  if (programsToggleColorTextures[21 + var10][var5]) {
                     int var6 = colorTexturesToggle[var5];
                     int var7 = colorTexturesToggle[var5] = 8 - var6;
                     GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[var5]);
                     GlStateManager.bindTexture(dfbColorTexturesA[var7 + var5]);
                     var10000 = 6861719743422132961L ^ var0;
                     EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var5, 3553, dfbColorTexturesA[var6 + var5], 0);
                  }
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         checkGLError("composite");
         Oo = false;
         mc.getFramebuffer().bindFramebuffer(true);
         OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().framebufferTexture, 0);
         GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
         if (EntityRenderer.anaglyphEnable) {
            boolean var11 = EntityRenderer.A != 0;
            GlStateManager.colorMask(var11, !var11, !var11, true);
         }

         GlStateManager.depthMask(true);
         GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
         var10000 = 5068354993410679468L ^ var0;
         GL11.glClear(16640);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(519);
         GlStateManager.depthMask(false);
         checkGLError("pre-final");
         useProgram(29);
         var10000 = 6402819679801177298L ^ var0;
         checkGLError("final");
         if (activeCompositeMipmapSetting != 0) {
            genCompositeMipmap();
         }

         drawComposite();
         checkGLError("renderCompositeFinal");
         var10000 = 1989893603841680982L ^ var0;
         es = true;
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         var10000 = 3887535756157482460L ^ var0;
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         useProgram(0);
      }
   }

   public static void endRender() {
      long var0 = ab ^ 12783656768106L;
      if (isShadowPass) {
         checkGLError("shadow endRender");
      } else {
         if (!es) {
            renderFinal();
         }

         isRenderingWorld = false;
         GlStateManager.colorMask(true, true, true, true);
         long var10000 = 5323070698108694879L ^ var0;
         useProgram(0);
         RenderHelper.disableStandardItemLighting();
         checkGLError("endRender end");
      }
   }

   public static void beginSky() {
      isRenderingSky = true;
      fogEnabled = true;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(5);
      pushEntity(-2, 0);
   }

   public static void setSkyColor(Vec3 var0) {
      skyColorR = (float)var0.xCoord;
      skyColorG = (float)var0.yCoord;
      skyColorB = (float)var0.zCoord;
      setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
   }

   public static void drawHorizon() {
      WorldRenderer var2 = Tessellator.getInstance().getWorldRenderer();
      float var3 = mc.gameSettings.renderDistanceChunks * 16;
      double var4 = var3 * 0.9238;
      double var6 = var3 * 0.3826;
      double var8 = -var6;
      double var10 = -var4;
      double var12 = 16.0;
      double var14 = -cameraPositionY;
      var2.begin(7, DefaultVertexFormats.POSITION);
      var2.pos(var8, var14, var10).endVertex();
      var2.pos(var8, var12, var10).endVertex();
      var2.pos(var10, var12, var8).endVertex();
      var2.pos(var10, var14, var8).endVertex();
      var2.pos(var10, var14, var8).endVertex();
      var2.pos(var10, var12, var8).endVertex();
      var2.pos(var10, var12, var6).endVertex();
      var2.pos(var10, var14, var6).endVertex();
      var2.pos(var10, var14, var6).endVertex();
      var2.pos(var10, var12, var6).endVertex();
      var2.pos(var8, var12, var6).endVertex();
      var2.pos(var8, var14, var6).endVertex();
      var2.pos(var8, var14, var6).endVertex();
      var2.pos(var8, var12, var6).endVertex();
      var2.pos(var6, var12, var4).endVertex();
      var2.pos(var6, var14, var4).endVertex();
      var2.pos(var6, var14, var4).endVertex();
      var2.pos(var6, var12, var4).endVertex();
      var2.pos(var4, var12, var6).endVertex();
      var2.pos(var4, var14, var6).endVertex();
      var2.pos(var4, var14, var6).endVertex();
      var2.pos(var4, var12, var6).endVertex();
      var2.pos(var4, var12, var8).endVertex();
      var2.pos(var4, var14, var8).endVertex();
      var2.pos(var4, var14, var8).endVertex();
      var2.pos(var4, var12, var8).endVertex();
      var2.pos(var6, var12, var10).endVertex();
      var2.pos(var6, var14, var10).endVertex();
      var2.pos(var6, var14, var10).endVertex();
      var2.pos(var6, var12, var10).endVertex();
      var2.pos(var8, var12, var10).endVertex();
      var2.pos(var8, var14, var10).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void preSkyList() {
      setUpPosition();
      GL11.glColor3f(fogColorR, fogColorG, fogColorB);
      drawHorizon();
      GL11.glColor3f(skyColorR, skyColorG, skyColorB);
   }

   public static void a() {
      isRenderingSky = false;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(lightmapEnabled ? 3 : 2);
      w();
   }

   public static void beginUpdateChunks() {
      checkGLError("beginUpdateChunks1");
      checkFramebufferStatus("beginUpdateChunks1");
      if (!isShadowPass) {
         useProgram(7);
      }

      checkGLError("beginUpdateChunks2");
      checkFramebufferStatus("beginUpdateChunks2");
   }

   public static void endUpdateChunks() {
      checkGLError("endUpdateChunks1");
      checkFramebufferStatus("endUpdateChunks1");
      if (!isShadowPass) {
         useProgram(7);
      }

      checkGLError("endUpdateChunks2");
      checkFramebufferStatus("endUpdateChunks2");
   }

   public static boolean shouldRenderClouds(GameSettings var0) {
      if (!shaderPackLoaded) {
         return true;
      }

      checkGLError("shouldRenderClouds");
      return isShadowPass ? configCloudShadow : var0.clouds > 0;
   }

   public static void beginClouds() {
      fogEnabled = true;
      pushEntity(-3, 0);
      useProgram(6);
   }

   public static void Z() {
      disableFog();
      w();
      useProgram(lightmapEnabled ? 3 : 2);
   }

   public static void k() {
      if (isRenderingWorld) {
         useProgram(16);
         resetDisplayListModels();
      }
   }

   public static void nextEntity(Entity var0) {
      if (isRenderingWorld) {
         useProgram(16);
         setEntityId(var0);
      }
   }

   public static void setEntityId(Entity var0) {
      if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined()) {
         int var4 = EntityList.getEntityID(var0);
         uniformEntityId.setValue(var4);
      }
   }

   public static void s() {
      long var0 = ab ^ 71003333919708L;
      if (isRenderingWorld && programsID[18] != programsID[0]) {
         useProgram(18);
         GlStateManager.enableAlpha();
         long var10000 = 2197345534300876200L ^ var0;
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
      }
   }

   public static void Q() {
      if (isRenderingWorld) {
         useProgram(lightmapEnabled ? 3 : 2);
      }
   }

   public static void setEntityColor(float var0, float var1, float var2, float var3) {
      if (isRenderingWorld && !isShadowPass) {
         uniformEntityColor.setValue(var0, var1, var2, var3);
      }
   }

   public static void V() {
      if (isRenderingWorld) {
         ShadersTex.bindTexture(defaultTexture);
         if (!isShadowPass) {
            setDrawBuffers(drawBuffersColorAtt0);
         }
      }
   }

   public static void U3() {
      if (isRenderingWorld && !isShadowPass) {
         setDrawBuffers(programsDrawBuffers[16]);
      }
   }

   public static void beginBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("beginBlockEntities");
         useProgram(13);
      }
   }

   public static void nextBlockEntity(TileEntity var0) {
      if (isRenderingWorld) {
         checkGLError("nextBlockEntity");
         useProgram(13);
         setBlockEntityId(var0);
      }
   }

   public static void setBlockEntityId(TileEntity var0) {
      if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined()) {
         Block var4 = var0.getBlockType();
         int var5 = Block.getIdFromBlock(var4);
         uniformBlockEntityId.setValue(var5);
      }
   }

   public static void endBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("endBlockEntities");
         useProgram(lightmapEnabled ? 3 : 2);
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
      }
   }

   public static void UT() {
      useProgram(3);
   }

   public static void S() {
      useProgram(2);
   }

   public static void U() {
      useProgram(3);
   }

   public static void readCenterDepth() {
      if (!isShadowPass && centerDepthSmoothEnabled) {
         ((Buffer)tempDirectFloatBuffer).clear();
         GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
         centerDepth = tempDirectFloatBuffer.get(0);
         float var3 = (float)diffSystemTime * 0.01F;
         float var4 = (float)Math.exp(Math.log(0.5) * var3 / centerDepthSmoothHalflife);
         centerDepthSmooth = centerDepthSmooth * var4 + centerDepth * (1.0F - var4);
      }
   }

   public static void beginWeather() {
      long var0 = ab ^ 120369262714035L;
      if (!isShadowPass) {
         if (ew >= 3) {
            GlStateManager.setActiveTexture(33996);
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            GlStateManager.setActiveTexture(33984);
         }

         GlStateManager.enableDepth();
         GlStateManager.enableBlend();
         long var10000 = 6731685338014607356L ^ var0;
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableAlpha();
         var10000 = 1191769473033416247L ^ var0;
         useProgram(20);
      }
   }

   public static void i() {
      GlStateManager.disableBlend();
      useProgram(3);
   }

   public static void preWater() {
      long var0 = ab ^ 85790925100858L;
      if (ew >= 2) {
         GlStateManager.setActiveTexture(33995);
         checkGLError("pre copy depth");
         long var10000 = 734737146079160884L ^ var0;
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
         checkGLError("copy depth");
         var10000 = 7041425754489737367L ^ var0;
         GlStateManager.setActiveTexture(33984);
      }

      ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
   }

   public static void beginWater() {
      if (isRenderingWorld) {
         if (!isShadowPass) {
            useProgram(12);
            GlStateManager.enableBlend();
            GlStateManager.depthMask(true);
         } else {
            GlStateManager.depthMask(true);
         }
      }
   }

   public static void u() {
      if (isRenderingWorld) {
         if (isShadowPass) {
         }

         useProgram(lightmapEnabled ? 3 : 2);
      }
   }

   public static void M() {
      if (isRenderingWorld) {
         useProgram(1);
      }
   }

   public static void UD() {
      if (isRenderingWorld) {
         useProgram(3);
      }
   }

   public static void applyHandDepth() {
      if (configHandDepthMul != 1.0) {
         GL11.glScaled(1.0, 1.0, configHandDepthMul);
      }
   }

   public static void t() {
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      useProgram(19);
      checkGLError("beginHand");
      checkFramebufferStatus("beginHand");
   }

   public static void endHand() {
      checkGLError("pre endHand");
      checkFramebufferStatus("pre endHand");
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GlStateManager.blendFunc(770, 771);
      checkGLError("endHand");
   }

   public static void b() {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
   }

   public static void D() {
   }

   public static void glEnableWrapper(int var0) {
      GL11.glEnable(var0);
      if (var0 == 3553) {
         enableTexture2D();
      } else if (var0 == 2912) {
         enableFog();
      }
   }

   public static void glDisableWrapper(int var0) {
      GL11.glDisable(var0);
      if (var0 == 3553) {
         disableTexture2D();
      } else if (var0 == 2912) {
         disableFog();
      }
   }

   public static void z(int var0) {
      GL11.glEnable(var0);
      enableTexture2D();
   }

   public static void d(int var0) {
      GL11.glDisable(var0);
      disableTexture2D();
   }

   public static void w(int var0) {
      GL11.glEnable(var0);
      enableFog();
   }

   public static void X(int var0) {
      GL11.glDisable(var0);
      disableFog();
   }

   public static void enableTexture2D() {
      if (isRenderingSky) {
         useProgram(5);
      } else if (activeProgram == 1) {
         useProgram(lightmapEnabled ? 3 : 2);
      }
   }

   public static void disableTexture2D() {
      if (isRenderingSky) {
         useProgram(4);
      } else if (activeProgram == 2 || activeProgram == 3) {
         useProgram(1);
      }
   }

   public static void beginLeash() {
      useProgram(1);
   }

   public static void endLeash() {
      useProgram(16);
   }

   public static void enableFog() {
      fogEnabled = true;
      setProgramUniform1i("fogMode", fogMode);
   }

   public static void disableFog() {
      fogEnabled = false;
      setProgramUniform1i("fogMode", 0);
   }

   public static void setFog(int var0) {
      GlStateManager.setFog(var0);
      var0 = var0;
      if (fogEnabled) {
         setProgramUniform1i("fogMode", var0);
      }
   }

   public static void sglFogi(int var0, int var1) {
      GL11.glFogi(var0, var1);
      if (var0 == 2917) {
         fogMode = var1;
         if (fogEnabled) {
            setProgramUniform1i("fogMode", fogMode);
         }
      }
   }

   public static void enableLightmap() {
      lightmapEnabled = true;
      if (activeProgram == 2) {
         useProgram(3);
      }
   }

   public static void q() {
      lightmapEnabled = false;
      if (activeProgram == 3) {
         useProgram(2);
      }
   }

   public static int e() {
      return entityData[entityDataIndex * 2];
   }

   public static int O() {
      return entityData[entityDataIndex * 2 + 1];
   }

   public static int setEntityData1(int var0) {
      entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & 65535 | var0 << 16;
      return var0;
   }

   public static int setEntityData2(int var0) {
      entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | var0 & 65535;
      return var0;
   }

   public static void pushEntity(int var0, int var1) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = var0 & 65535 | var1 << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void pushEntity(int var0) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = var0 & 65535;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void pushEntity(Block var0) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = Block.blockRegistry.getIDForObject(var0) & 65535 | var0.getRenderType() << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void w() {
      entityData[entityDataIndex * 2] = 0;
      entityData[entityDataIndex * 2 + 1] = 0;
      entityDataIndex--;
   }

   public static void UM() {
      mc.mcProfiler.endSection();
   }

   public static String getShaderPackName() {
      return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
   }

   public static InputStream getShaderPackResourceStream(String var0) {
      return shaderPack == null ? null : shaderPack.getResourceAsStream(var0);
   }

   public static void p() {
      configAntialiasingLevel += 2;
      configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
      if (configAntialiasingLevel > 4) {
         configAntialiasingLevel = 0;
      }

      configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
   }

   public static void checkShadersModInstalled() {
      try {
         Class var2 = Class.forName("shadersmod.transform.SMCClassTransformer");
      } catch (Throwable var3) {
         return;
      }

      throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
   }

   public static void j() {
      loadShaderPackResources();
   }

   private static void loadShaderPackResources() {
      shaderPackResources = new HashMap<>();
      if (shaderPackLoaded) {
         ArrayList var3 = new ArrayList();
         String var4 = "/shaders/lang/";
         String var5 = "en_US";
         String var6 = ".lang";
         var3.add(var4 + var5 + var6);
         if (!Config.getGameSettings().language.equals(var5)) {
            var3.add(var4 + Config.getGameSettings().language + var6);
         }

         try {
            for (Object var8$raw : var3) {
               String var8 = (String)var8$raw;
               InputStream var9 = shaderPack.getResourceAsStream(var8);
               if (var9 != null) {
                  Properties var10 = new Properties();
                  Lang.loadLocaleData(var9, var10);
                  var9.close();

                  for (Object var12 : var10.keySet()) {
                     String var13 = var10.getProperty((String)var12);
                     shaderPackResources.put((String)var12, var13);
                  }
               }
            }
         } catch (IOException var14) {
            var14.printStackTrace();
         }
      }
   }

   public static String translate(String var0, String var1) {
      String var5 = shaderPackResources.get(var0);
      return var5 == null ? var1 : var5;
   }

   public static boolean isProgramPath(String var0) {
      if (var0 == null) {
         return false;
      }

      if (var0.length() <= 0) {
         return false;
      }

      int var4 = var0.lastIndexOf("/");
      if (var4 >= 0) {
         var0 = var0.substring(var4 + 1);
      }

      return Arrays.asList(programNames).contains(var0);
   }

   public static void setItemToRenderMain(ItemStack var0) {
      itemToRenderMainTranslucent = isTranslucentBlock(var0);
   }

   public static boolean B() {
      return itemToRenderMainTranslucent;
   }

   private static boolean isTranslucentBlock(ItemStack var0) {
      if (var0 == null) {
         return false;
      }

      Item var4 = var0.getItem();
      if (var4 == null) {
         return false;
      }

      if (!(var4 instanceof ItemBlock)) {
         return false;
      }

      ItemBlock var5 = (ItemBlock)var4;
      Block var6 = var5.getBlock();
      if (var6 == null) {
         return false;
      }

      EnumWorldBlockLayer var7 = var6.getBlockLayer();
      return var7 == EnumWorldBlockLayer.TRANSLUCENT;
   }

   public static boolean e_boolean() {
      return t;
   }

   public static void i(boolean var0) {
   }

   public static float getShadowRenderDistance() {
      return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
   }

   static {
      long var20 = ab ^ 91608607703590L;
      long var10000 = var20 ^ 544419979890919214L;
      var10000 = var20 ^ 7812023991140429084L;
      var10000 = var20 ^ 6320112555119363601L;
      var10000 = var20 ^ 2655837378711085884L;
      var10000 = var20 ^ 544419979890919214L;
      var10000 = var20 ^ 68226855131217410L;
      var10000 = var20 ^ 2827192271558403028L;
      var10000 = var20 ^ 2655837378711085884L;
      var10000 = var20 ^ 4831168503603022563L;
      var10000 = var20 ^ 5504230643410408599L;
      var10000 = var20 ^ 7373072745129095002L;
      var10000 = var20 ^ 68226855131217410L;
      var10000 = var20 ^ 5785552349126031010L;
      var10000 = var20 ^ 8972023153538091991L;
      var10000 = var20 ^ 2421980635482735378L;
      var10000 = var20 ^ 1044973580171526748L;
      var10000 = var20 ^ 2042879519140371541L;
      var10000 = var20 ^ 2984929199314433441L;
      var10000 = var20 ^ 7499487394422094133L;
      var10000 = var20 ^ 2965979504338688709L;
      var10000 = var20 ^ 544419979890919214L;
      var10000 = var20 ^ 4940302251437861389L;
      var10000 = var20 ^ 6957652175896650740L;
      var10000 = var20 ^ 1822633623428256533L;
      var10000 = var20 ^ 9163765529376408879L;
      var10000 = var20 ^ 544419979890919214L;
      var10000 = var20 ^ 6964397835528112753L;
      var10000 = var20 ^ 5979578095196603030L;
      var10000 = var20 ^ 5673542862280887710L;
      var10000 = var20 ^ 320253734608519914L;
      var10000 = var20 ^ 8651368235784120778L;
      var10000 = var20 ^ 8826366180325447344L;
      var10000 = var20 ^ 1876521461926273020L;
      var10000 = var20 ^ 9112170562877100439L;
      var10000 = var20 ^ 5696653797118286118L;
      var10000 = var20 ^ 7374114477324888235L;
      var10000 = var20 ^ 544419979890919214L;
      var10000 = var20 ^ 2123567894657586886L;
      var10000 = var20 ^ 2655837378711085884L;
      var10000 = var20 ^ 222503622802339211L;
      var10000 = var20 ^ 68226855131217410L;
      var10000 = 7026406214775344419L ^ var20;
      var10000 = 2623098770566077606L ^ var20;
      var10000 = 4831417236672200385L ^ var20;
      var10000 = 7417312728121725734L ^ var20;
      var10000 = 1792239448786536506L ^ var20;
      var10000 = 916531899561562555L ^ var20;
      var10000 = 916531899561562555L ^ var20;
      var10000 = 916531899561562555L ^ var20;
      var10000 = 916531899561562555L ^ var20;
      var10000 = 7417312728121725734L ^ var20;
      var10000 = 7026406214775344419L ^ var20;
      var10000 = 3079934229643387761L ^ var20;
      var10000 = 7417312728121725734L ^ var20;
      var10000 = 7417312728121725734L ^ var20;
      var10000 = 5989404966085867037L ^ var20;
      var10000 = 5989404966085867037L ^ var20;
      ((Buffer)drawBuffersNone).limit(0);
      ((Buffer)drawBuffersColorAtt0.put(36064)).position(0).limit(1);
   }
}
