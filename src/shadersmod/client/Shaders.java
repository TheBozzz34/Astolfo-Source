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
   public static boolean isInitializedOnce = false;
   public static boolean isShaderPackInitialized = false;
   public static ContextCapabilities capabilities;
   public static String glVersionString;
   public static String glVendorString;
   public static String glRendererString;
   public static boolean hasGlGenMipmap = false;
   public static boolean hasForge = false;
   public static int numberResetDisplayList = 0;
   public static boolean needResetModels = false;
   private static int renderDisplayWidth = 0;
   private static int renderDisplayHeight = 0;
   public static int renderWidth = 0;
   public static int renderHeight = 0;
   public static boolean isRenderingWorld = false;
   public static boolean isRenderingSky = false;
   public static boolean isCompositeRendered = false;
   public static boolean isRenderingDfb = false;
   public static boolean isShadowPass = false;
   public static boolean isSleeping;
   private static boolean isHandRenderedMain;
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
   public static long worldTime = 0L;
   public static long lastWorldTime = 0L;
   public static long diffWorldTime = 0L;
   public static float celestialAngle = 0.0F;
   public static float sunAngle = 0.0F;
   public static float shadowAngle = 0.0F;
   public static int moonPhase = 0;
   public static long systemTime = 0L;
   public static long lastSystemTime = 0L;
   public static long diffSystemTime = 0L;
   public static int frameCounter = 0;
   public static float frameTime = 0.0F;
   public static float frameTimeCounter = 0.0F;
   public static int systemTimeInt32 = 0;
   public static float rainStrength = 0.0F;
   public static float wetness = 0.0F;
   public static float wetnessHalfLife = 600.0F;
   public static float drynessHalfLife = 200.0F;
   public static float eyeBrightnessHalflife = 10.0F;
   public static boolean usewetness = false;
   public static int isEyeInWater = 0;
   public static int eyeBrightness = 0;
   public static float eyeBrightnessFadeX = 0.0F;
   public static float eyeBrightnessFadeY = 0.0F;
   public static float eyePosY = 0.0F;
   public static float centerDepth = 0.0F;
   public static float centerDepthSmooth = 0.0F;
   public static float centerDepthSmoothHalflife = 1.0F;
   public static boolean centerDepthSmoothEnabled = false;
   public static int superSamplingLevel = 1;
   public static float nightVision = 0.0F;
   public static float blindness = 0.0F;
   public static boolean updateChunksErrorRecorded = false;
   public static boolean lightmapEnabled = false;
   public static boolean fogEnabled = true;
   public static int entityAttrib = 10;
   public static int midTexCoordAttrib = 11;
   public static int tangentAttrib = 12;
   public static boolean useEntityAttrib = false;
   public static boolean useMidTexCoordAttrib = false;
   public static boolean useMultiTexCoord3Attrib = false;
   public static boolean useTangentAttrib = false;
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
   public static int shadowPassInterval = 0;
   public static boolean needResizeShadow = false;
   public static int shadowMapWidth = 1024;
   public static int shadowMapHeight = 1024;
   public static int spShadowMapWidth = 1024;
   public static int spShadowMapHeight = 1024;
   public static float shadowMapFOV = 90.0F;
   public static float shadowMapHalfPlane = 160.0F;
   public static boolean shadowMapIsOrtho = true;
   public static float shadowDistanceRenderMul = -1.0F;
   public static int shadowPassCounter = 0;
   public static int preShadowPassThirdPersonView;
   public static boolean shouldSkipDefaultShadow = false;
   public static boolean waterShadowEnabled = false;
   public static final int MaxDrawBuffers = 8;
   public static final int MaxColorBuffers = 8;
   public static final int MaxDepthBuffers = 3;
   public static final int MaxShadowColorBuffers = 8;
   public static final int MaxShadowDepthBuffers = 2;
   public static int usedColorBuffers = 0;
   public static int usedDepthBuffers = 0;
   public static int usedShadowColorBuffers = 0;
   public static int usedShadowDepthBuffers = 0;
   public static int usedColorAttachs = 0;
   public static int usedDrawBuffers = 0;
   public static int dfb = 0;
   public static int sfb = 0;
   private static int[] gbuffersFormat = new int[8];
   private static boolean[] gbuffersClear = new boolean[8];
   public static int activeProgram = 0;
   public static final int ProgramNone = 0;
   public static final int ProgramBasic = 1;
   public static final int ProgramTextured = 2;
   public static final int ProgramTexturedLit = 3;
   public static final int ProgramSkyBasic = 4;
   public static final int ProgramSkyTextured = 5;
   public static final int ProgramClouds = 6;
   public static final int ProgramTerrain = 7;
   public static final int ProgramTerrainSolid = 8;
   public static final int ProgramTerrainCutoutMip = 9;
   public static final int ProgramTerrainCutout = 10;
   public static final int ProgramDamagedBlock = 11;
   public static final int ProgramWater = 12;
   public static final int ProgramBlock = 13;
   public static final int ProgramBeaconBeam = 14;
   public static final int ProgramItem = 15;
   public static final int ProgramEntities = 16;
   public static final int ProgramArmorGlint = 17;
   public static final int ProgramSpiderEyes = 18;
   public static final int ProgramHand = 19;
   public static final int ProgramWeather = 20;
   public static final int ProgramComposite = 21;
   public static final int ProgramComposite1 = 22;
   public static final int ProgramComposite2 = 23;
   public static final int ProgramComposite3 = 24;
   public static final int ProgramComposite4 = 25;
   public static final int ProgramComposite5 = 26;
   public static final int ProgramComposite6 = 27;
   public static final int ProgramComposite7 = 28;
   public static final int ProgramFinal = 29;
   public static final int ProgramShadow = 30;
   public static final int ProgramShadowSolid = 31;
   public static final int ProgramShadowCutout = 32;
   public static final int ProgramCount = 33;
   public static final int MaxCompositePasses = 8;
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
   private static int programIDCopyDepth = 0;
   private static String[] programsDrawBufSettings = new String[33];
   private static String newDrawBufSetting = null;
   public static IntBuffer[] programsDrawBuffers = new IntBuffer[33];
   public static IntBuffer activeDrawBuffers = null;
   private static String[] programsColorAtmSettings = new String[33];
   private static String newColorAtmSetting = null;
   private static String activeColorAtmSettings = null;
   private static int[] programsCompositeMipmapSetting = new int[33];
   private static int newCompositeMipmapSetting = 0;
   private static int activeCompositeMipmapSetting = 0;
   public static Properties loadedShaders = null;
   public static Properties shadersConfig = null;
   public static ITextureObject defaultTexture = null;
   public static boolean normalMapEnabled = false;
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
   public static int configTexMagFilN = 0;
   public static int configTexMagFilS = 0;
   public static boolean configShadowClipFrustrum = true;
   public static boolean configNormalMap = true;
   public static boolean configSpecularMap = true;
   public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
   public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
   public static int configAntialiasingLevel = 0;
   public static final int texMinFilRange = 3;
   public static final int texMagFilRange = 2;
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
   private static CustomTexture[] customTexturesGbuffers = null;
   private static CustomTexture[] customTexturesComposite = null;
   private static final String[] STAGE_NAMES = new String[]{"gbuffers", "composite"};
   public static final boolean enableShadersOption = true;
   private static final boolean enableShadersDebug = true;
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
   public static int terrainIconSize = 16;
   public static int[] terrainTextureSize = new int[2];
   private static HFNoiseTexture customUniforms;
   private static boolean noiseTextureEnabled = false;
   private static int noiseTextureResolution = 256;
   public static final int[] dfbColorTexturesA = new int[16];
   public static final int[] colorTexturesToggle = new int[8];
   public static final int[] colorTextureTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
   public static final boolean[][] programsToggleColorTextures = new boolean[33][8];
   private static final int bigBufferSize = 2196;
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
   private static final Pattern gbufferFormatPattern = Pattern.compile(
      "[ \t]*const[ \t]*int[ \t]*(\\w+)Format[ \t]*=[ \t]*([RGBA0123456789FUI_SNORM]*)[ \t]*;.*"
   );
   private static final Pattern gbufferClearPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)Clear[ \t]*=[ \t]*false[ \t]*;.*");
   private static final Pattern gbufferMipmapEnabledPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*");
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

   private static IntBuffer nextIntBuffer(int size) {
      ByteBuffer buffer = bigBuffer;
      int pos = buffer.limit();
      ((Buffer)buffer).position(pos).limit(pos + size * 4);
      return buffer.asIntBuffer();
   }

   private static FloatBuffer nextFloatBuffer(int size) {
      ByteBuffer buffer = bigBuffer;
      int pos = buffer.limit();
      ((Buffer)buffer).position(pos).limit(pos + size * 4);
      return buffer.asFloatBuffer();
   }

   private static IntBuffer[] nextIntBufferArray(int count, int size) {
      IntBuffer[] intBuffers = new IntBuffer[count];

      for (int i = 0; i < count; i++) {
         intBuffers[i] = nextIntBuffer(size);
      }

      return intBuffers;
   }

   public static void loadConfig() {
      SMCLog.info("Load ShadersMod configuration.");

      try {
         if (!shaderpacksdir.exists()) {
            shaderpacksdir.mkdir();
         }
      } catch (Exception exception) {
         SMCLog.severe("Failed to open the shaderpacks directory: " + shaderpacksdir);
      }

      shadersConfig = new PropertiesOrdered();
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
      if (configFile.exists()) {
         try {
            FileReader fileReader = new FileReader(configFile);
            shadersConfig.load(fileReader);
            fileReader.close();
         } catch (Exception exception2) {
         }
      }

      if (!configFile.exists()) {
         try {
            storeConfig();
         } catch (Exception exception3) {
         }
      }

      EnumShaderOption[] enumShaderOptions = EnumShaderOption.values();

      for (int i = 0; i < enumShaderOptions.length; i++) {
         EnumShaderOption enumShaderOption = enumShaderOptions[i];
         String string = enumShaderOption.getPropertyKey();
         String string2 = enumShaderOption.getValueDefault();
         String string3 = shadersConfig.getProperty(string, string2);
         setEnumShaderOption(enumShaderOption, string3);
      }

      loadShaderPack();
   }

   private static void setEnumShaderOption(EnumShaderOption eso, String str) {
      if (str == null) {
         str = eso.getValueDefault();
      }

      switch (eso) {
         case ANTIALIASING:
            configAntialiasingLevel = Config.parseInt(str, 0);
            break;
         case NORMAL_MAP:
            configNormalMap = Config.parseBoolean(str, true);
            break;
         case SPECULAR_MAP:
            configSpecularMap = Config.parseBoolean(str, true);
            break;
         case RENDER_RES_MUL:
            configRenderResMul = Config.parseFloat(str, 1.0F);
            break;
         case SHADOW_RES_MUL:
            configShadowResMul = Config.parseFloat(str, 1.0F);
            break;
         case HAND_DEPTH_MUL:
            configHandDepthMul = Config.parseFloat(str, 0.125F);
            break;
         case CLOUD_SHADOW:
            configCloudShadow = Config.parseBoolean(str, true);
            break;
         case OLD_HAND_LIGHT:
            configOldHandLight.setPropertyValue(str);
            break;
         case OLD_LIGHTING:
            configOldLighting.setPropertyValue(str);
            break;
         case SHADER_PACK:
            currentshadername = str;
            break;
         case TWEAK_BLOCK_DAMAGE:
            configTweakBlockDamage = Config.parseBoolean(str, true);
            break;
         case SHADOW_CLIP_FRUSTRUM:
            configShadowClipFrustrum = Config.parseBoolean(str, true);
            break;
         case TEX_MIN_FIL_B:
            configTexMinFilB = Config.parseInt(str, 0);
            break;
         case TEX_MIN_FIL_N:
            configTexMinFilN = Config.parseInt(str, 0);
            break;
         case TEX_MIN_FIL_S:
            configTexMinFilS = Config.parseInt(str, 0);
            break;
         case TEX_MAG_FIL_B:
            configTexMagFilB = Config.parseInt(str, 0);
            break;
         case TEX_MAG_FIL_N:
            configTexMagFilB = Config.parseInt(str, 0);
            break;
         case TEX_MAG_FIL_S:
            configTexMagFilB = Config.parseInt(str, 0);
            break;
         default:
            throw new IllegalArgumentException("Unknown option: " + eso);
      }
   }

   public static void storeConfig() {
      SMCLog.info("Save ShadersMod configuration.");
      if (shadersConfig == null) {
         shadersConfig = new PropertiesOrdered();
      }

      EnumShaderOption[] enumShaderOptions = EnumShaderOption.values();

      for (int i = 0; i < enumShaderOptions.length; i++) {
         EnumShaderOption enumShaderOption = enumShaderOptions[i];
         String string = enumShaderOption.getPropertyKey();
         String string2 = getEnumShaderOption(enumShaderOption);
         shadersConfig.setProperty(string, string2);
      }

      try {
         FileWriter fileWriter = new FileWriter(configFile);
         shadersConfig.store(fileWriter, (String)null);
         fileWriter.close();
      } catch (Exception exception) {
         SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
      }
   }

   public static String getEnumShaderOption(EnumShaderOption eso) {
      switch (eso) {
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
            throw new IllegalArgumentException("Unknown option: " + eso);
      }
   }

   public static void setShaderPack(String par1name) {
      currentshadername = par1name;
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
      loadShaderPack();
   }

   public static void loadShaderPack() {
      boolean bl = shaderPackLoaded;
      boolean bl2 = isOldLighting();
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
         resetCustomTextures();
      }

      boolean bl3 = false;
      if (Config.isAntialiasing()) {
         SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
         bl3 = true;
      }

      if (Config.isAnisotropicFiltering()) {
         SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
         bl3 = true;
      }

      if (Config.isFastRender()) {
         SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
         bl3 = true;
      }

      String string = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault);
      if (!string.isEmpty() && !string.equals(packNameNone) && !bl3) {
         if (string.equals(packNameDefault)) {
            shaderPack = new ShaderPackDefault();
            shaderPackLoaded = true;
         } else {
            try {
               File file = new File(shaderpacksdir, string);
               if (file.isDirectory()) {
                  shaderPack = new ShaderPackFolder(string, file);
                  shaderPackLoaded = true;
               } else if (file.isFile() && string.toLowerCase().endsWith(".zip")) {
                  shaderPack = new ShaderPackZip(string, file);
                  shaderPackLoaded = true;
               }
            } catch (Exception exception) {
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
      boolean bl4 = shaderPackLoaded != bl;
      boolean bl5 = isOldLighting() != bl2;
      if (bl4 || bl5) {
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

      for (int i = -128; i <= 128; i++) {
         String string = "/shaders/world" + i;
         if (shaderPack.hasDirectory(string)) {
            shaderPackDimensions.add(i);
         }
      }

      if (shaderPackDimensions.size() > 0) {
         Integer[] integers = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
         Config.dbg("[Shaders] Worlds: " + Config.arrayToString(integers));
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
      BlockAliases.reset();
      if (shaderPack != null) {
         BlockAliases.update(shaderPack);
         String string = "/shaders/shaders.properties";

         try {
            InputStream inputStream = shaderPack.getResourceAsStream(string);
            if (inputStream == null) {
               return;
            }

            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream);
            inputStream.close();
            shaderPackClouds.loadFrom(propertiesOrdered);
            shaderPackOldHandLight.loadFrom(propertiesOrdered);
            shaderPackDynamicHandLight.loadFrom(propertiesOrdered);
            shaderPackOldLighting.loadFrom(propertiesOrdered);
            shaderPackShadowTranslucent.loadFrom(propertiesOrdered);
            shaderPackUnderwaterOverlay.loadFrom(propertiesOrdered);
            shaderPackSun.loadFrom(propertiesOrdered);
            shaderPackVignette.loadFrom(propertiesOrdered);
            shaderPackMoon.loadFrom(propertiesOrdered);
            shaderPackBackFaceSolid.loadFrom(propertiesOrdered);
            shaderPackBackFaceCutout.loadFrom(propertiesOrdered);
            shaderPackBackFaceCutoutMipped.loadFrom(propertiesOrdered);
            shaderPackBackFaceTranslucent.loadFrom(propertiesOrdered);
            shaderPackProfiles = ShaderPackParser.parseProfiles(propertiesOrdered, shaderPackOptions);
            shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(propertiesOrdered, shaderPackProfiles, shaderPackOptions);
            customTexturesGbuffers = loadCustomTextures(propertiesOrdered, 0);
            customTexturesComposite = loadCustomTextures(propertiesOrdered, 1);
         } catch (IOException ioException) {
            Config.warn("[Shaders] Error reading: " + string);
         }
      }
   }

   private static CustomTexture[] loadCustomTextures(Properties props, int stage) {
      String string = "texture." + STAGE_NAMES[stage] + ".";
      Set set = props.keySet();
      ArrayList arrayList = new ArrayList();

      for (Object object : set) {
         String string2 = (String)object;
         if (string2.startsWith(string)) {
            String string3 = string2.substring(string.length());
            String string4 = props.getProperty(string2).trim();
            int i = getTextureIndex(stage, string3);
            if (i < 0) {
               SMCLog.warning("Invalid texture name: " + string2);
            } else {
               try {
                  String string5 = "shaders/" + StrUtils.removePrefix(string4, "/");
                  InputStream inputStream = shaderPack.getResourceAsStream(string5);
                  if (inputStream == null) {
                     SMCLog.warning("Texture not found: " + string4);
                  } else {
                     IOUtils.closeQuietly(inputStream);
                     SimpleShaderTexture simpleShaderTexture = new SimpleShaderTexture(string5);
                     simpleShaderTexture.loadTexture(mc.getResourceManager());
                     CustomTexture customTexture = new CustomTexture(i, string5, simpleShaderTexture);
                     arrayList.add(customTexture);
                  }
               } catch (IOException ioException) {
                  SMCLog.warning("Error loading texture: " + string4);
                  SMCLog.warning("" + ioException.getClass().getName() + ": " + ioException.getMessage());
               }
            }
         }
      }

      return arrayList.size() <= 0 ? null : (shadersmod.client.CustomTexture[])(arrayList.toArray(new CustomTexture[arrayList.size()]));
   }

   private static int getTextureIndex(int stage, String name) {
      if (stage == 0) {
         if (name.equals("texture")) {
            return 0;
         }

         if (name.equals("lightmap")) {
            return 1;
         }

         if (name.equals("normals")) {
            return 2;
         }

         if (name.equals("specular")) {
            return 3;
         }

         if (name.equals("shadowtex0") || name.equals("watershadow")) {
            return 4;
         }

         if (name.equals("shadow")) {
            return waterShadowEnabled ? 5 : 4;
         }

         if (name.equals("shadowtex1")) {
            return 5;
         }

         if (name.equals("depthtex0")) {
            return 6;
         }

         if (name.equals("gaux1")) {
            return 7;
         }

         if (name.equals("gaux2")) {
            return 8;
         }

         if (name.equals("gaux3")) {
            return 9;
         }

         if (name.equals("gaux4")) {
            return 10;
         }

         if (name.equals("depthtex1")) {
            return 12;
         }

         if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
            return 13;
         }

         if (name.equals("shadowcolor1")) {
            return 14;
         }

         if (name.equals("noisetex")) {
            return 15;
         }
      }

      if (stage == 1) {
         if (name.equals("colortex0") || name.equals("colortex0")) {
            return 0;
         }

         if (name.equals("colortex1") || name.equals("gdepth")) {
            return 1;
         }

         if (name.equals("colortex2") || name.equals("gnormal")) {
            return 2;
         }

         if (name.equals("colortex3") || name.equals("composite")) {
            return 3;
         }

         if (name.equals("shadowtex0") || name.equals("watershadow")) {
            return 4;
         }

         if (name.equals("shadow")) {
            return waterShadowEnabled ? 5 : 4;
         }

         if (name.equals("shadowtex1")) {
            return 5;
         }

         if (name.equals("depthtex0") || name.equals("gdepthtex")) {
            return 6;
         }

         if (name.equals("colortex4") || name.equals("gaux1")) {
            return 7;
         }

         if (name.equals("colortex5") || name.equals("gaux2")) {
            return 8;
         }

         if (name.equals("colortex6") || name.equals("gaux3")) {
            return 9;
         }

         if (name.equals("colortex7") || name.equals("gaux4")) {
            return 10;
         }

         if (name.equals("depthtex1")) {
            return 11;
         }

         if (name.equals("depthtex2")) {
            return 12;
         }

         if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
            return 13;
         }

         if (name.equals("shadowcolor1")) {
            return 14;
         }

         if (name.equals("noisetex")) {
            return 15;
         }
      }

      return -1;
   }

   private static void bindCustomTextures(CustomTexture[] cts) {
      if (cts != null) {
         for (int i = 0; i < cts.length; i++) {
            CustomTexture customTexture = cts[i];
            GlStateManager.setActiveTexture(33984 + customTexture.getTextureUnit());
            ITextureObject iTextureObject = customTexture.getTexture();
            GlStateManager.bindTexture(iTextureObject.getGlTextureId());
         }
      }
   }

   private static void resetCustomTextures() {
      deleteCustomTextures(customTexturesGbuffers);
      deleteCustomTextures(customTexturesComposite);
      customTexturesGbuffers = null;
      customTexturesComposite = null;
   }

   private static void deleteCustomTextures(CustomTexture[] cts) {
      if (cts != null) {
         for (int i = 0; i < cts.length; i++) {
            CustomTexture customTexture = cts[i];
            ITextureObject iTextureObject = customTexture.getTexture();
            TextureUtil.deleteTexture(iTextureObject.getGlTextureId());
         }
      }
   }

   public static ShaderOption[] getShaderPackOptions(String screenName) {
      ShaderOption[] shaderOptions = (ShaderOption[])shaderPackOptions.clone();
      if (shaderPackGuiScreens == null) {
         if (shaderPackProfiles != null) {
            ShaderOptionProfile shaderOptionProfile = new ShaderOptionProfile(shaderPackProfiles, shaderOptions);
            shaderOptions = (ShaderOption[])Config.addObjectToArray(shaderOptions, shaderOptionProfile, 0);
         }

         return getVisibleOptions(shaderOptions);
      } else {
         String string = screenName != null ? "screen." + screenName : "screen";
         ShaderOption[] shaderOptions2 = shaderPackGuiScreens.get(string);
         if (shaderOptions2 == null) {
            return new ShaderOption[0];
         }

         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < shaderOptions2.length; i++) {
            ShaderOption shaderOption = shaderOptions2[i];
            if (shaderOption == null) {
               arrayList.add((ShaderOption)null);
            } else if (shaderOption instanceof ShaderOptionRest) {
               ShaderOption[] shaderOptions3 = getShaderOptionsRest(shaderPackGuiScreens, shaderOptions);
               arrayList.addAll(Arrays.asList(shaderOptions3));
            } else {
               arrayList.add(shaderOption);
            }
         }

         return (shadersmod.client.ShaderOption[])(arrayList.toArray(new ShaderOption[arrayList.size()]));
      }
   }

   private static ShaderOption[] getShaderOptionsRest(Map<String, ShaderOption[]> mapScreens, ShaderOption[] ops) {
      HashSet hashSet = new HashSet();

      for (String string : mapScreens.keySet()) {
         ShaderOption[] shaderOptions = (ShaderOption[])mapScreens.get(string);

         for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            if (shaderOption != null) {
               hashSet.add(shaderOption.getName());
            }
         }
      }

      ArrayList arrayList = new ArrayList();

      for (int j = 0; j < ops.length; j++) {
         ShaderOption shaderOption2 = ops[j];
         if (shaderOption2.isVisible()) {
            String string2 = shaderOption2.getName();
            if (!hashSet.contains(string2)) {
               arrayList.add(shaderOption2);
            }
         }
      }

      return (shadersmod.client.ShaderOption[])(arrayList.toArray(new ShaderOption[arrayList.size()]));
   }

   public static ShaderOption getShaderOption(String name) {
      return ShaderUtils.getShaderOption(name, shaderPackOptions);
   }

   public static ShaderOption[] getShaderPackOptions() {
      return shaderPackOptions;
   }

   private static ShaderOption[] getVisibleOptions(ShaderOption[] ops) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < ops.length; i++) {
         ShaderOption shaderOption = ops[i];
         if (shaderOption.isVisible()) {
            arrayList.add(shaderOption);
         }
      }

      return (shadersmod.client.ShaderOption[])(arrayList.toArray(new ShaderOption[arrayList.size()]));
   }

   public static void saveShaderPackOptions() {
      saveShaderPackOptions(shaderPackOptions, shaderPack);
   }

   private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp) {
      Properties properties = new Properties();
      if (shaderPackOptions != null) {
         for (int i = 0; i < sos.length; i++) {
            ShaderOption shaderOption = sos[i];
            if (shaderOption.isChanged() && shaderOption.isEnabled()) {
               properties.setProperty(shaderOption.getName(), shaderOption.getValue());
            }
         }
      }

      try {
         saveOptionProperties(sp, properties);
      } catch (IOException ioException) {
         Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
         ioException.printStackTrace();
      }
   }

   private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException {
      String string = shaderpacksdirname + "/" + sp.getName() + ".txt";
      File file = new File(Minecraft.getMinecraft().mcDataDir, string);
      if (props.isEmpty()) {
         file.delete();
      } else {
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         props.store(fileOutputStream, (String)null);
         fileOutputStream.flush();
         fileOutputStream.close();
      }
   }

   private static ShaderOption[] loadShaderPackOptions() {
      try {
         ShaderOption[] shaderOptions = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
         Properties properties = loadOptionProperties(shaderPack);

         for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            String string = properties.getProperty(shaderOption.getName());
            if (string != null) {
               shaderOption.resetValue();
               if (!shaderOption.setValue(string)) {
                  Config.warn("[Shaders] Invalid value, option: " + shaderOption.getName() + ", value: " + string);
               }
            }
         }

         return shaderOptions;
      } catch (IOException ioException) {
         Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
         ioException.printStackTrace();
         return null;
      }
   }

   private static Properties loadOptionProperties(IShaderPack sp) throws IOException {
      Properties properties = new Properties();
      String string = shaderpacksdirname + "/" + sp.getName() + ".txt";
      File file = new File(Minecraft.getMinecraft().mcDataDir, string);
      if (file.exists() && file.isFile() && file.canRead()) {
         FileInputStream fileInputStream = new FileInputStream(file);
         properties.load(fileInputStream);
         fileInputStream.close();
         return properties;
      } else {
         return properties;
      }
   }

   public static ShaderOption[] getChangedOptions(ShaderOption[] ops) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < ops.length; i++) {
         ShaderOption shaderOption = ops[i];
         if (shaderOption.isEnabled() && shaderOption.isChanged()) {
            arrayList.add(shaderOption);
         }
      }

      return (shadersmod.client.ShaderOption[])(arrayList.toArray(new ShaderOption[arrayList.size()]));
   }

   private static String applyOptions(String line, ShaderOption[] ops) {
      if (ops != null && ops.length > 0) {
         for (int i = 0; i < ops.length; i++) {
            ShaderOption shaderOption = ops[i];
            String string = shaderOption.getName();
            if (shaderOption.matchesLine(line)) {
               line = shaderOption.getSourceLine();
               break;
            }
         }

         return line;
      } else {
         return line;
      }
   }

   public static ArrayList listOfShaders() {
      ArrayList arrayList = new ArrayList();
      arrayList.add(packNameNone);
      arrayList.add(packNameDefault);

      try {
         if (!shaderpacksdir.exists()) {
            shaderpacksdir.mkdir();
         }

         File[] files = shaderpacksdir.listFiles();

         for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String string = file.getName();
            if (file.isDirectory()) {
               File file2 = new File(file, "shaders");
               if (file2.exists() && file2.isDirectory()) {
                  arrayList.add(string);
               }
            } else if (file.isFile() && string.toLowerCase().endsWith(".zip")) {
               arrayList.add(string);
            }
         }
      } catch (Exception exception) {
      }

      return arrayList;
   }

   public static String versiontostring(int vv) {
      String vs = Integer.toString(vv);
      return Integer.toString(Integer.parseInt(vs.substring(1, 3)))
         + "."
         + Integer.toString(Integer.parseInt(vs.substring(3, 5)))
         + "."
         + Integer.toString(Integer.parseInt(vs.substring(5)));
   }

   public static void checkOptifine() {
   }

   public static int checkFramebufferStatus(String location) {
      int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (i != 36053) {
         System.err.format("FramebufferStatus 0x%04X at %s\n", i, location);
      }

      return i;
   }

   public static int checkGLError(String location) {
      int i = GL11.glGetError();
      if (i != 0) {
         boolean bl = false;
         if (!bl) {
            if (i == 1286) {
               int j = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
               System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", i, GLU.gluErrorString(i), j, location);
            } else {
               System.err.format("GL error 0x%04X: %s at %s\n", i, GLU.gluErrorString(i), location);
            }
         }
      }

      return i;
   }

   public static int checkGLError(String location, String info) {
      int i = GL11.glGetError();
      if (i != 0) {
         System.err.format("GL error 0x%04x: %s at %s %s\n", i, GLU.gluErrorString(i), location, info);
      }

      return i;
   }

   public static int checkGLError(String location, String info1, String info2) {
      int i = GL11.glGetError();
      if (i != 0) {
         System.err.format("GL error 0x%04x: %s at %s %s %s\n", i, GLU.gluErrorString(i), location, info1, info2);
      }

      return i;
   }

   private static void printChatAndLogError(String str) {
      SMCLog.severe(str);
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
   }

   public static void printIntBuffer(String title, IntBuffer buf) {
      StringBuilder stringBuilder = new StringBuilder(128);
      stringBuilder.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
      int i = buf.limit();

      for (int j = 0; j < i; j++) {
         stringBuilder.append(" ").append(buf.get(j));
      }

      stringBuilder.append("]");
      SMCLog.info(stringBuilder.toString());
   }

   public static void startup(Minecraft mcIn) {
      checkShadersModInstalled();
      mc = mcIn;
      mcIn = Minecraft.getMinecraft();
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

   public static boolean isDynamicHandLight() {
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

   public static boolean isMoon() {
      return !shaderPackMoon.isFalse();
   }

   public static boolean isVignette() {
      return !shaderPackVignette.isFalse();
   }

   public static boolean isRenderBackFace(EnumWorldBlockLayer blockLayerIn) {
      switch (blockLayerIn) {
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
      if (!isInitializedOnce) {
         isInitializedOnce = true;
      }

      if (!isShaderPackInitialized) {
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
         usedDepthBuffers = 1;
         usedShadowColorBuffers = 0;
         usedShadowDepthBuffers = 0;
         usedColorAttachs = 1;
         usedDrawBuffers = 1;
         Arrays.fill(gbuffersFormat, 6408);
         Arrays.fill(gbuffersClear, true);
         Arrays.fill(shadowHardwareFilteringEnabled, false);
         Arrays.fill(shadowMipmapEnabled, false);
         Arrays.fill(shadowFilterNearest, false);
         Arrays.fill(shadowColorMipmapEnabled, false);
         Arrays.fill(shadowColorFilterNearest, false);
         centerDepthSmoothEnabled = false;
         noiseTextureEnabled = false;
         sunPathRotation = 0.0F;
         shadowIntervalSize = 2.0F;
         shadowDistanceRenderMul = -1.0F;
         aoLevel = -1.0F;
         useEntityAttrib = false;
         useMidTexCoordAttrib = false;
         useMultiTexCoord3Attrib = false;
         useTangentAttrib = false;
         waterShadowEnabled = false;
         updateChunksErrorRecorded = false;
         updateBlockLightLevel();
         ShaderProfile shaderProfile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
         String string = "";
         if (currentWorld != null) {
            int i = currentWorld.provider.getDimensionId();
            if (shaderPackDimensions.contains(i)) {
               string = "world" + i + "/";
            }
         }

         if (saveFinalShaders) {
            clearDirectory(new File(shaderpacksdir, "debug"));
         }

         for (int j = 0; j < 33; j++) {
            String string2 = programNames[j];
            if (string2.equals("")) {
               programsID[j] = programsRef[j] = 0;
               programsDrawBufSettings[j] = null;
               programsColorAtmSettings[j] = null;
               programsCompositeMipmapSetting[j] = 0;
            } else {
               newDrawBufSetting = null;
               newColorAtmSetting = null;
               newCompositeMipmapSetting = 0;
               String string3 = string + string2;
               if (shaderProfile != null && shaderProfile.isProgramDisabled(string3)) {
                  SMCLog.info("Program disabled: " + string3);
                  string2 = "<disabled>";
                  string3 = string + string2;
               }

               String string4 = "/shaders/" + string3;
               int k = setupProgram(j, string4 + ".vsh", string4 + ".fsh");
               if (k > 0) {
                  SMCLog.info("Program loaded: " + string3);
               }

               programsID[j] = programsRef[j] = k;
               programsDrawBufSettings[j] = k != 0 ? newDrawBufSetting : null;
               programsColorAtmSettings[j] = k != 0 ? newColorAtmSetting : null;
               programsCompositeMipmapSetting[j] = k != 0 ? newCompositeMipmapSetting : 0;
            }
         }

         int l = GL11.glGetInteger(34852);
         new HashMap();

         for (int m = 0; m < 33; m++) {
            Arrays.fill(programsToggleColorTextures[m], false);
            if (m == 29) {
               programsDrawBuffers[m] = null;
            } else if (programsID[m] == 0) {
               if (m == 30) {
                  programsDrawBuffers[m] = drawBuffersNone;
               } else {
                  programsDrawBuffers[m] = drawBuffersColorAtt0;
               }
            } else {
               String string5 = programsDrawBufSettings[m];
               if (string5 != null) {
                  IntBuffer intBuffer = drawBuffersBuffer[m];
                  int n = string5.length();
                  if (n > usedDrawBuffers) {
                     usedDrawBuffers = n;
                  }

                  if (n > l) {
                     n = l;
                  }

                  programsDrawBuffers[m] = intBuffer;
                  ((Buffer)intBuffer).limit(n);

                  for (int i2 = 0; i2 < n; i2++) {
                     int i3 = 0;
                     if (string5.length() > i2) {
                        int i4 = string5.charAt(i2) - '0';
                        if (m != 30) {
                           if (i4 >= 0 && i4 <= 7) {
                              programsToggleColorTextures[m][i4] = true;
                              i3 = i4 + 36064;
                              if (i4 > usedColorAttachs) {
                                 usedColorAttachs = i4;
                              }

                              if (i4 > usedColorBuffers) {
                                 usedColorBuffers = i4;
                              }
                           }
                        } else if (i4 >= 0 && i4 <= 1) {
                           i3 = i4 + 36064;
                           if (i4 > usedShadowColorBuffers) {
                              usedShadowColorBuffers = i4;
                           }
                        }
                     }

                     intBuffer.put(i2, i3);
                  }
               } else if (m != 30 && m != 31 && m != 32) {
                  programsDrawBuffers[m] = dfbDrawBuffers;
                  usedDrawBuffers = usedColorBuffers;
                  Arrays.fill(programsToggleColorTextures[m], 0, usedColorBuffers, true);
               } else {
                  programsDrawBuffers[m] = sfbDrawBuffers;
               }
            }
         }

         usedColorAttachs = usedColorBuffers;
         shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
         shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
         SMCLog.info("usedColorBuffers: " + usedColorBuffers);
         SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
         SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
         SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
         SMCLog.info("usedColorAttachs: " + usedColorAttachs);
         SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
         ((Buffer)dfbDrawBuffers).position(0).limit(usedDrawBuffers);
         ((Buffer)dfbColorTextures).position(0).limit(usedColorBuffers * 2);

         for (int i5 = 0; i5 < usedDrawBuffers; i5++) {
            dfbDrawBuffers.put(i5, 36064 + i5);
         }

         if (usedDrawBuffers > l) {
            printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + l);
         }

         ((Buffer)sfbDrawBuffers).position(0).limit(usedShadowColorBuffers);

         for (int i6 = 0; i6 < usedShadowColorBuffers; i6++) {
            sfbDrawBuffers.put(i6, 36064 + i6);
         }

         for (int i7 = 0; i7 < 33; i7++) {
            int i8 = i7;

            while (programsID[i8] == 0 && programBackups[i8] != i8) {
               i8 = programBackups[i8];
            }

            if (i8 != i7 && i7 != 30) {
               programsID[i7] = programsID[i8];
               programsDrawBufSettings[i7] = programsDrawBufSettings[i8];
               programsDrawBuffers[i7] = programsDrawBuffers[i8];
            }
         }

         resize();
         resizeShadow();
         if (noiseTextureEnabled) {
            setupNoiseTexture();
         }

         if (defaultTexture == null) {
            defaultTexture = ShadersTex.createDefaultTexture();
         }

         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         preCelestialRotate();
         postCelestialRotate();
         GlStateManager.popMatrix();
         isShaderPackInitialized = true;
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

         for (Object object : mc.getRenderManager().getEntityRenderMap().values()) {
            if (object instanceof RendererLivingEntity) {
               RendererLivingEntity rendererLivingEntity = (RendererLivingEntity)object;
               resetDisplayListModel(rendererLivingEntity.getMainModel());
            }
         }
      }
   }

   public static void resetDisplayListModel(ModelBase model) {
      if (model != null) {
         for (Object object : model.boxList) {
            if (object instanceof ModelRenderer) {
               resetDisplayListModelRenderer((ModelRenderer)object);
            }
         }
      }
   }

   public static void resetDisplayListModelRenderer(ModelRenderer mrr) {
      mrr.resetDisplayList();
      if (mrr.childModels != null) {
         int i = 0;

         for (int j = mrr.childModels.size(); i < j; i++) {
            resetDisplayListModelRenderer(mrr.childModels.get(i));
         }
      }
   }

   private static int setupProgram(int program, String vShaderPath, String fShaderPath) {
      checkGLError("pre setupProgram");
      int i = ARBShaderObjects.glCreateProgramObjectARB();
      checkGLError("create");
      if (i != 0) {
         progUseEntityAttrib = false;
         progUseMidTexCoordAttrib = false;
         progUseTangentAttrib = false;
         int j = createVertShader(vShaderPath);
         int k = createFragShader(fShaderPath);
         checkGLError("create");
         if (j == 0 && k == 0) {
            ARBShaderObjects.glDeleteObjectARB(i);
            i = 0;
         } else {
            if (j != 0) {
               ARBShaderObjects.glAttachObjectARB(i, j);
               checkGLError("attach");
            }

            if (k != 0) {
               ARBShaderObjects.glAttachObjectARB(i, k);
               checkGLError("attach");
            }

            if (progUseEntityAttrib) {
               ARBVertexShader.glBindAttribLocationARB(i, entityAttrib, "mc_Entity");
               checkGLError("mc_Entity");
            }

            if (progUseMidTexCoordAttrib) {
               ARBVertexShader.glBindAttribLocationARB(i, midTexCoordAttrib, "mc_midTexCoord");
               checkGLError("mc_midTexCoord");
            }

            if (progUseTangentAttrib) {
               ARBVertexShader.glBindAttribLocationARB(i, tangentAttrib, "at_tangent");
               checkGLError("at_tangent");
            }

            ARBShaderObjects.glLinkProgramARB(i);
            if (GL20.glGetProgrami(i, 35714) != 1) {
               SMCLog.severe("Error linking program: " + i);
            }

            printLogInfo(i, vShaderPath + ", " + fShaderPath);
            if (j != 0) {
               ARBShaderObjects.glDetachObjectARB(i, j);
               ARBShaderObjects.glDeleteObjectARB(j);
            }

            if (k != 0) {
               ARBShaderObjects.glDetachObjectARB(i, k);
               ARBShaderObjects.glDeleteObjectARB(k);
            }

            programsID[program] = i;
            useProgram(program);
            ARBShaderObjects.glValidateProgramARB(i);
            useProgram(0);
            printLogInfo(i, vShaderPath + ", " + fShaderPath);
            int l = GL20.glGetProgrami(i, 35715);
            if (l != 1) {
               String string = "\"";
               printChatAndLogError("[Shaders] Error: Invalid program " + string + programNames[program] + string);
               ARBShaderObjects.glDeleteObjectARB(i);
               i = 0;
            }
         }
      }

      return i;
   }

   private static int createVertShader(String filename) {
      int i = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (i == 0) {
         return 0;
      }

      StringBuilder stringBuilder = new StringBuilder(131072);
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(filename)));
      } catch (Exception exception) {
         try {
            bufferedReader = new BufferedReader(new FileReader(new File(filename)));
         } catch (Exception exception2) {
            ARBShaderObjects.glDeleteObjectARB(i);
            return 0;
         }
      }

      ShaderOption[] shaderOptions = getChangedOptions(shaderPackOptions);
      ArrayList arrayList = new ArrayList();
      if (bufferedReader != null) {
         try {
            bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, filename, shaderPack, 0, arrayList, 0);

            while (true) {
               String string = bufferedReader.readLine();
               if (string == null) {
                  bufferedReader.close();
                  break;
               }

               string = applyOptions(string, shaderOptions);
               stringBuilder.append(string).append('\n');
               if (string.matches("attribute [_a-zA-Z0-9]+ mc_Entity.*")) {
                  useEntityAttrib = true;
                  progUseEntityAttrib = true;
               } else if (string.matches("attribute [_a-zA-Z0-9]+ mc_midTexCoord.*")) {
                  useMidTexCoordAttrib = true;
                  progUseMidTexCoordAttrib = true;
               } else if (string.matches(".*gl_MultiTexCoord3.*")) {
                  useMultiTexCoord3Attrib = true;
               } else if (string.matches("attribute [_a-zA-Z0-9]+ at_tangent.*")) {
                  useTangentAttrib = true;
                  progUseTangentAttrib = true;
               }
            }
         } catch (Exception exception3) {
            SMCLog.severe("Couldn't read " + filename + "!");
            exception3.printStackTrace();
            ARBShaderObjects.glDeleteObjectARB(i);
            return 0;
         }
      }

      if (saveFinalShaders) {
         saveShader(filename, stringBuilder.toString());
      }

      ARBShaderObjects.glShaderSourceARB(i, stringBuilder);
      ARBShaderObjects.glCompileShaderARB(i);
      if (GL20.glGetShaderi(i, 35713) != 1) {
         SMCLog.severe("Error compiling vertex shader: " + filename);
      }

      printShaderLogInfo(i, filename, arrayList);
      return i;
   }

   private static int createFragShader(String filename) {
      int i = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (i == 0) {
         return 0;
      }

      StringBuilder stringBuilder = new StringBuilder(131072);
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(filename)));
      } catch (Exception exception) {
         try {
            bufferedReader = new BufferedReader(new FileReader(new File(filename)));
         } catch (Exception exception2) {
            ARBShaderObjects.glDeleteObjectARB(i);
            return 0;
         }
      }

      ShaderOption[] shaderOptions = getChangedOptions(shaderPackOptions);
      ArrayList arrayList = new ArrayList();
      if (bufferedReader != null) {
         try {
            bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, filename, shaderPack, 0, arrayList, 0);

            while (true) {
               String string = bufferedReader.readLine();
               if (string == null) {
                  bufferedReader.close();
                  break;
               }

               string = applyOptions(string, shaderOptions);
               stringBuilder.append(string).append('\n');
               if (!string.matches("#version .*")) {
                  if (string.matches("uniform [ _a-zA-Z0-9]+ shadow;.*")) {
                     if (usedShadowDepthBuffers < 1) {
                        usedShadowDepthBuffers = 1;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ watershadow;.*")) {
                     waterShadowEnabled = true;
                     if (usedShadowDepthBuffers < 2) {
                        usedShadowDepthBuffers = 2;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ shadowtex0;.*")) {
                     if (usedShadowDepthBuffers < 1) {
                        usedShadowDepthBuffers = 1;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ shadowtex1;.*")) {
                     if (usedShadowDepthBuffers < 2) {
                        usedShadowDepthBuffers = 2;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ shadowcolor;.*")) {
                     if (usedShadowColorBuffers < 1) {
                        usedShadowColorBuffers = 1;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ shadowcolor0;.*")) {
                     if (usedShadowColorBuffers < 1) {
                        usedShadowColorBuffers = 1;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ shadowcolor1;.*")) {
                     if (usedShadowColorBuffers < 2) {
                        usedShadowColorBuffers = 2;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ depthtex0;.*")) {
                     if (usedDepthBuffers < 1) {
                        usedDepthBuffers = 1;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ depthtex1;.*")) {
                     if (usedDepthBuffers < 2) {
                        usedDepthBuffers = 2;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ depthtex2;.*")) {
                     if (usedDepthBuffers < 3) {
                        usedDepthBuffers = 3;
                     }
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ gdepth;.*")) {
                     if (gbuffersFormat[1] == 6408) {
                        gbuffersFormat[1] = 34836;
                     }
                  } else if (usedColorBuffers < 5 && string.matches("uniform [ _a-zA-Z0-9]+ gaux1;.*")) {
                     usedColorBuffers = 5;
                  } else if (usedColorBuffers < 6 && string.matches("uniform [ _a-zA-Z0-9]+ gaux2;.*")) {
                     usedColorBuffers = 6;
                  } else if (usedColorBuffers < 7 && string.matches("uniform [ _a-zA-Z0-9]+ gaux3;.*")) {
                     usedColorBuffers = 7;
                  } else if (usedColorBuffers < 8 && string.matches("uniform [ _a-zA-Z0-9]+ gaux4;.*")) {
                     usedColorBuffers = 8;
                  } else if (usedColorBuffers < 5 && string.matches("uniform [ _a-zA-Z0-9]+ colortex4;.*")) {
                     usedColorBuffers = 5;
                  } else if (usedColorBuffers < 6 && string.matches("uniform [ _a-zA-Z0-9]+ colortex5;.*")) {
                     usedColorBuffers = 6;
                  } else if (usedColorBuffers < 7 && string.matches("uniform [ _a-zA-Z0-9]+ colortex6;.*")) {
                     usedColorBuffers = 7;
                  } else if (usedColorBuffers < 8 && string.matches("uniform [ _a-zA-Z0-9]+ colortex7;.*")) {
                     usedColorBuffers = 8;
                  } else if (string.matches("uniform [ _a-zA-Z0-9]+ centerDepthSmooth;.*")) {
                     centerDepthSmoothEnabled = true;
                  } else if (string.matches("/\\* SHADOWRES:[0-9]+ \\*/.*")) {
                     String[] strings = string.split("(:| )", 4);
                     SMCLog.info("Shadow map resolution: " + strings[2]);
                     spShadowMapWidth = spShadowMapHeight = Integer.parseInt(strings[2]);
                     shadowMapWidth = shadowMapHeight = Math.round(spShadowMapWidth * configShadowResMul);
                  } else if (string.matches("[ \t]*const[ \t]*int[ \t]*shadowMapResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings2 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow map resolution: " + strings2[1]);
                     spShadowMapWidth = spShadowMapHeight = Integer.parseInt(strings2[1]);
                     shadowMapWidth = shadowMapHeight = Math.round(spShadowMapWidth * configShadowResMul);
                  } else if (string.matches("/\\* SHADOWFOV:[0-9\\.]+ \\*/.*")) {
                     String[] strings3 = string.split("(:| )", 4);
                     SMCLog.info("Shadow map field of view: " + strings3[2]);
                     shadowMapFOV = Float.parseFloat(strings3[2]);
                     shadowMapIsOrtho = false;
                  } else if (string.matches("/\\* SHADOWHPL:[0-9\\.]+ \\*/.*")) {
                     String[] strings4 = string.split("(:| )", 4);
                     SMCLog.info("Shadow map half-plane: " + strings4[2]);
                     shadowMapHalfPlane = Float.parseFloat(strings4[2]);
                     shadowMapIsOrtho = true;
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*shadowDistance[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings5 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow map distance: " + strings5[1]);
                     shadowMapHalfPlane = Float.parseFloat(strings5[1]);
                     shadowMapIsOrtho = true;
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*shadowDistanceRenderMul[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings6 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow distance render mul: " + strings6[1]);
                     shadowDistanceRenderMul = Float.parseFloat(strings6[1]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*shadowIntervalSize[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings7 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Shadow map interval size: " + strings7[1]);
                     shadowIntervalSize = Float.parseFloat(strings7[1]);
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("Generate shadow mipmap");
                     Arrays.fill(shadowMipmapEnabled, true);
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowColorMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("Generate shadow color mipmap");
                     Arrays.fill(shadowColorMipmapEnabled, true);
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("Hardware shadow filtering enabled.");
                     Arrays.fill(shadowHardwareFilteringEnabled, true);
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering0[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowHardwareFiltering0");
                     shadowHardwareFilteringEnabled[0] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering1[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowHardwareFiltering1");
                     shadowHardwareFilteringEnabled[1] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Mipmap|shadowtexMipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex0Mipmap");
                     shadowMipmapEnabled[0] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex1Mipmap");
                     shadowMipmapEnabled[1] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Mipmap|shadowColor0Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowcolor0Mipmap");
                     shadowColorMipmapEnabled[0] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Mipmap|shadowColor1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowcolor1Mipmap");
                     shadowColorMipmapEnabled[1] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Nearest|shadowtexNearest|shadow0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex0Nearest");
                     shadowFilterNearest[0] = true;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Nearest|shadow1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                     SMCLog.info("shadowtex1Nearest");
                     shadowFilterNearest[1] = true;
                  } else if (string.matches(
                     "[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Nearest|shadowColor0Nearest|shadowColor0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*"
                  )) {
                     SMCLog.info("shadowcolor0Nearest");
                     shadowColorFilterNearest[0] = true;
                  } else if (string.matches(
                     "[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Nearest|shadowColor1Nearest|shadowColor1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*"
                  )) {
                     SMCLog.info("shadowcolor1Nearest");
                     shadowColorFilterNearest[1] = true;
                  } else if (string.matches("/\\* WETNESSHL:[0-9\\.]+ \\*/.*")) {
                     String[] strings8 = string.split("(:| )", 4);
                     SMCLog.info("Wetness halflife: " + strings8[2]);
                     wetnessHalfLife = Float.parseFloat(strings8[2]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*wetnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings9 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Wetness halflife: " + strings9[1]);
                     wetnessHalfLife = Float.parseFloat(strings9[1]);
                  } else if (string.matches("/\\* DRYNESSHL:[0-9\\.]+ \\*/.*")) {
                     String[] strings10 = string.split("(:| )", 4);
                     SMCLog.info("Dryness halflife: " + strings10[2]);
                     drynessHalfLife = Float.parseFloat(strings10[2]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*drynessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings11 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Dryness halflife: " + strings11[1]);
                     drynessHalfLife = Float.parseFloat(strings11[1]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*eyeBrightnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings12 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Eye brightness halflife: " + strings12[1]);
                     eyeBrightnessHalflife = Float.parseFloat(strings12[1]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*centerDepthHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings13 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Center depth halflife: " + strings13[1]);
                     centerDepthSmoothHalflife = Float.parseFloat(strings13[1]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*sunPathRotation[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings14 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Sun path rotation: " + strings14[1]);
                     sunPathRotation = Float.parseFloat(strings14[1]);
                  } else if (string.matches("[ \t]*const[ \t]*float[ \t]*ambientOcclusionLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings15 = string.split("(=[ \t]*|;)");
                     SMCLog.info("AO Level: " + strings15[1]);
                     aoLevel = Config.limit(Float.parseFloat(strings15[1]), 0.0F, 1.0F);
                  } else if (string.matches("[ \t]*const[ \t]*int[ \t]*superSamplingLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings16 = string.split("(=[ \t]*|;)");
                     int j = Integer.parseInt(strings16[1]);
                     if (j > 1) {
                        SMCLog.info("Super sampling level: " + j + "x");
                        superSamplingLevel = j;
                     } else {
                        superSamplingLevel = 1;
                     }
                  } else if (string.matches("[ \t]*const[ \t]*int[ \t]*noiseTextureResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                     String[] strings17 = string.split("(=[ \t]*|;)");
                     SMCLog.info("Noise texture enabled");
                     SMCLog.info("Noise texture resolution: " + strings17[1]);
                     noiseTextureResolution = Integer.parseInt(strings17[1]);
                     noiseTextureEnabled = true;
                  } else if (string.matches("[ \t]*const[ \t]*int[ \t]*\\w+Format[ \t]*=[ \t]*[RGBA0123456789FUI_SNORM]*[ \t]*;.*")) {
                     Matcher matcher2 = gbufferFormatPattern.matcher(string);
                     matcher2.matches();
                     String string2 = matcher2.group(1);
                     String string3 = matcher2.group(2);
                     int k = getBufferIndexFromString(string2);
                     int l = getTextureFormatFromString(string3);
                     if (k >= 0 && l != 0) {
                        gbuffersFormat[k] = l;
                        SMCLog.infoFormatted("%s format: %s", string2, string3);
                     }
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*\\w+Clear[ \t]*=[ \t]*false[ \t]*;.*")) {
                     if (filename.matches(".*composite[0-9]?.fsh")) {
                        Matcher matcher3 = gbufferClearPattern.matcher(string);
                        matcher3.matches();
                        String string4 = matcher3.group(1);
                        int m = getBufferIndexFromString(string4);
                        if (m >= 0) {
                           gbuffersClear[m] = false;
                           SMCLog.infoFormatted("%s clear disabled", string4);
                        }
                     }
                  } else if (string.matches("/\\* GAUX4FORMAT:RGBA32F \\*/.*")) {
                     SMCLog.info("gaux4 format : RGB32AF");
                     gbuffersFormat[7] = 34836;
                  } else if (string.matches("/\\* GAUX4FORMAT:RGB32F \\*/.*")) {
                     SMCLog.info("gaux4 format : RGB32F");
                     gbuffersFormat[7] = 34837;
                  } else if (string.matches("/\\* GAUX4FORMAT:RGB16 \\*/.*")) {
                     SMCLog.info("gaux4 format : RGB16");
                     gbuffersFormat[7] = 32852;
                  } else if (string.matches("[ \t]*const[ \t]*bool[ \t]*\\w+MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*")) {
                     if (filename.matches(".*composite[0-9]?.fsh") || filename.matches(".*final.fsh")) {
                        Matcher matcher4 = gbufferMipmapEnabledPattern.matcher(string);
                        matcher4.matches();
                        String string5 = matcher4.group(1);
                        int i2 = getBufferIndexFromString(string5);
                        if (i2 >= 0) {
                           newCompositeMipmapSetting |= 1 << i2;
                           SMCLog.infoFormatted("%s mipmap enabled", string5);
                        }
                     }
                  } else if (string.matches("/\\* DRAWBUFFERS:[0-7N]* \\*/.*")) {
                     String[] strings18 = string.split("(:| )", 4);
                     newDrawBufSetting = strings18[2];
                  }
               }
            }
         } catch (Exception exception3) {
            SMCLog.severe("Couldn't read " + filename + "!");
            exception3.printStackTrace();
            ARBShaderObjects.glDeleteObjectARB(i);
            return 0;
         }
      }

      if (saveFinalShaders) {
         saveShader(filename, stringBuilder.toString());
      }

      ARBShaderObjects.glShaderSourceARB(i, stringBuilder);
      ARBShaderObjects.glCompileShaderARB(i);
      if (GL20.glGetShaderi(i, 35713) != 1) {
         SMCLog.severe("Error compiling fragment shader: " + filename);
      }

      printShaderLogInfo(i, filename, arrayList);
      return i;
   }

   private static void saveShader(String filename, String code) {
      try {
         File file = new File(shaderpacksdir, "debug/" + filename);
         file.getParentFile().mkdirs();
         Config.writeFile(file, code);
      } catch (IOException ioException) {
         Config.warn("Error saving: " + filename);
         ioException.printStackTrace();
      }
   }

   private static void clearDirectory(File dir) {
      if (dir.exists() && dir.isDirectory()) {
         File[] files = dir.listFiles();
         if (files != null) {
            for (int i = 0; i < files.length; i++) {
               File file = files[i];
               if (file.isDirectory()) {
                  clearDirectory(file);
               }

               file.delete();
            }
         }
      }
   }

   private static boolean printLogInfo(int obj, String name) {
      IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
      ARBShaderObjects.glGetObjectParameterARB(obj, 35716, intBuffer);
      int i = intBuffer.get();
      if (i > 1) {
         ByteBuffer byteBuffer = BufferUtils.createByteBuffer(i);
         ((Buffer)intBuffer).flip();
         ARBShaderObjects.glGetInfoLogARB(obj, intBuffer, byteBuffer);
         byte[] bytes = new byte[i];
         byteBuffer.get(bytes);
         if (bytes[i - 1] == 0) {
            bytes[i - 1] = 10;
         }

         String string = new String(bytes);
         SMCLog.info("Info log: " + name + "\n" + string);
         return false;
      } else {
         return true;
      }
   }

   private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles) {
      IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
      int i = GL20.glGetShaderi(shader, 35716);
      if (i <= 1) {
         return true;
      }

      for (int j = 0; j < listFiles.size(); j++) {
         String string = (String)listFiles.get(j);
         SMCLog.info("File: " + (j + 1) + " = " + string);
      }

      String string2 = GL20.glGetShaderInfoLog(shader, i);
      SMCLog.info("Shader info log: " + name + "\n" + string2);
      return false;
   }

   public static void setDrawBuffers(IntBuffer drawBuffers) {
      if (drawBuffers == null) {
         drawBuffers = drawBuffersNone;
      }

      if (activeDrawBuffers != drawBuffers) {
         activeDrawBuffers = drawBuffers;
         GL20.glDrawBuffers(drawBuffers);
      }
   }

   public static void useProgram(int program) {
      checkGLError("pre-useProgram");
      if (isShadowPass) {
         program = 30;
         if (programsID[30] == 0) {
            normalMapEnabled = false;
            return;
         }
      }

      if (activeProgram != program) {
         activeProgram = program;
         ARBShaderObjects.glUseProgramObjectARB(programsID[program]);
         if (programsID[program] == 0) {
            normalMapEnabled = false;
         } else {
            if (checkGLError("useProgram ", programNames[program]) != 0) {
               programsID[program] = 0;
            }

            IntBuffer intBuffer = programsDrawBuffers[program];
            if (isRenderingDfb) {
               setDrawBuffers(intBuffer);
               checkGLError(programNames[program], " draw buffers = ", programsDrawBufSettings[program]);
            }

            activeCompositeMipmapSetting = programsCompositeMipmapSetting[program];
            uniformEntityColor.setProgram(programsID[activeProgram]);
            uniformEntityId.setProgram(programsID[activeProgram]);
            uniformBlockEntityId.setProgram(programsID[activeProgram]);
            switch (program) {
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
                  normalMapEnabled = true;
                  setProgramUniform1i("texture", 0);
                  setProgramUniform1i("lightmap", 1);
                  setProgramUniform1i("normals", 2);
                  setProgramUniform1i("specular", 3);
                  setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i("watershadow", 4);
                  setProgramUniform1i("shadowtex0", 4);
                  setProgramUniform1i("shadowtex1", 5);
                  setProgramUniform1i("depthtex0", 6);
                  if (customTexturesGbuffers != null) {
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
                  normalMapEnabled = false;
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
                  normalMapEnabled = false;
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
                  if (customTexturesGbuffers != null) {
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

            ItemStack itemStack = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
            Item item = itemStack != null ? itemStack.getItem() : null;
            int i = -1;
            Block block = null;
            if (item != null) {
               i = Item.itemRegistry.getIDForObject(item);
               block = Block.blockRegistry.getObjectById(i);
            }

            int j = block != null ? block.getLightValue() : 0;
            setProgramUniform1i("heldItemId", i);
            setProgramUniform1i("heldBlockLightValue", j);
            setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
            setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
            setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
            setProgramUniform1i("worldTime", (int)(worldTime % 24000L));
            setProgramUniform1i("worldDay", (int)(worldTime / 24000L));
            setProgramUniform1i("moonPhase", moonPhase);
            setProgramUniform1i("frameCounter", frameCounter);
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
            if (usedShadowDepthBuffers > 0) {
               setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
               setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
               setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
               setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
            }

            setProgramUniform1f("wetness", wetness);
            setProgramUniform1f("eyeAltitude", eyePosY);
            setProgramUniform2i("eyeBrightness", eyeBrightness & 65535, eyeBrightness >> 16);
            setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
            setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
            setProgramUniform1i("terrainIconSize", terrainIconSize);
            setProgramUniform1i("isEyeInWater", isEyeInWater);
            setProgramUniform1f("nightVision", nightVision);
            setProgramUniform1f("blindness", blindness);
            setProgramUniform1f("screenBrightness", mc.gameSettings.gammaSetting);
            setProgramUniform1i("hideGUI", mc.gameSettings.hideGUI ? 1 : 0);
            setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
            setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);
            checkGLError("useProgram ", programNames[program]);
         }
      }
   }

   public static void setProgramUniform1i(String name, int x) {
      int i = programsID[activeProgram];
      if (i != 0) {
         int j = ARBShaderObjects.glGetUniformLocationARB(i, name);
         ARBShaderObjects.glUniform1iARB(j, x);
         checkGLError(programNames[activeProgram], name);
      }
   }

   public static void setProgramUniform2i(String name, int x, int y) {
      int i = programsID[activeProgram];
      if (i != 0) {
         int j = ARBShaderObjects.glGetUniformLocationARB(i, name);
         ARBShaderObjects.glUniform2iARB(j, x, y);
         checkGLError(programNames[activeProgram], name);
      }
   }

   public static void setProgramUniform1f(String name, float x) {
      int i = programsID[activeProgram];
      if (i != 0) {
         int j = ARBShaderObjects.glGetUniformLocationARB(i, name);
         ARBShaderObjects.glUniform1fARB(j, x);
         checkGLError(programNames[activeProgram], name);
      }
   }

   public static void setProgramUniform3f(String name, float x, float y, float z) {
      int i = programsID[activeProgram];
      if (i != 0) {
         int j = ARBShaderObjects.glGetUniformLocationARB(i, name);
         ARBShaderObjects.glUniform3fARB(j, x, y, z);
         checkGLError(programNames[activeProgram], name);
      }
   }

   public static void setProgramUniformMatrix4ARB(String name, boolean transpose, FloatBuffer matrix) {
      int i = programsID[activeProgram];
      if (i != 0 && matrix != null) {
         int j = ARBShaderObjects.glGetUniformLocationARB(i, name);
         ARBShaderObjects.glUniformMatrix4ARB(j, transpose, matrix);
         checkGLError(programNames[activeProgram], name);
      }
   }

   private static int getBufferIndexFromString(String name) {
      return name.equals("colortex0") || name.equals("gcolor")
         ? 0
         : (
            name.equals("colortex1") || name.equals("gdepth")
               ? 1
               : (
                  name.equals("colortex2") || name.equals("gnormal")
                     ? 2
                     : (
                        name.equals("colortex3") || name.equals("composite")
                           ? 3
                           : (
                              name.equals("colortex4") || name.equals("gaux1")
                                 ? 4
                                 : (
                                    name.equals("colortex5") || name.equals("gaux2")
                                       ? 5
                                       : (
                                          !name.equals("colortex6") && !name.equals("gaux3")
                                             ? (!name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7)
                                             : 6
                                       )
                                 )
                           )
                     )
               )
         );
   }

   private static int getTextureFormatFromString(String par) {
      par = par.trim();

      for (int i = 0; i < formatNames.length; i++) {
         String string = formatNames[i];
         if (par.equals(string)) {
            return formatIds[i];
         }
      }

      return 0;
   }

   private static void setupNoiseTexture() {
      if (customUniforms == null) {
         customUniforms = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
      }
   }

   private static void loadEntityDataMap() {
      mapBlockToEntityData = new IdentityHashMap<>(300);
      if (mapBlockToEntityData.isEmpty()) {
         for (ResourceLocation resourceLocation : Block.blockRegistry.getKeys()) {
            Block block2 = Block.blockRegistry.getObject(resourceLocation);
            int i = Block.blockRegistry.getIDForObject(block2);
            mapBlockToEntityData.put(block2, i);
         }
      }

      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
      } catch (Exception exception) {
      }

      if (bufferedReader != null) {
         String string;
         try {
            while ((string = bufferedReader.readLine()) != null) {
               Matcher matcher2 = patternLoadEntityDataMap.matcher(string);
               if (matcher2.matches()) {
                  String string2 = matcher2.group(1);
                  String string3 = matcher2.group(2);
                  int j = Integer.parseInt(string3);
                  Block block3 = Block.getBlockFromName(string2);
                  if (block3 != null) {
                     mapBlockToEntityData.put(block3, j);
                  } else {
                     SMCLog.warning("Unknown block name %s", string2);
                  }
               } else {
                  SMCLog.warning("unmatched %s\n", string);
               }
            }
         } catch (Exception exception2) {
            SMCLog.warning("Error parsing mc_Entity_x.txt");
         }
      }

      if (bufferedReader != null) {
         try {
            bufferedReader.close();
         } catch (Exception exception3) {
         }
      }
   }

   private static IntBuffer fillIntBufferZero(IntBuffer buf) {
      int i = buf.limit();

      for (int j = buf.position(); j < i; j++) {
         buf.put(j, 0);
      }

      return buf;
   }

   public static void uninit() {
      if (isShaderPackInitialized) {
         checkGLError("Shaders.uninit pre");

         for (int i = 0; i < 33; i++) {
            if (programsRef[i] != 0) {
               ARBShaderObjects.glDeleteObjectARB(programsRef[i]);
               checkGLError("del programRef");
            }

            programsRef[i] = 0;
            programsID[i] = 0;
            programsDrawBufSettings[i] = null;
            programsDrawBuffers[i] = null;
            programsCompositeMipmapSetting[i] = 0;
         }

         if (dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
            dfb = 0;
            checkGLError("del dfb");
         }

         if (sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
            sfb = 0;
            checkGLError("del sfb");
         }

         if (dfbDepthTextures != null) {
            GlStateManager.deleteTextures(dfbDepthTextures);
            fillIntBufferZero(dfbDepthTextures);
            checkGLError("del dfbDepthTextures");
         }

         if (dfbColorTextures != null) {
            GlStateManager.deleteTextures(dfbColorTextures);
            fillIntBufferZero(dfbColorTextures);
            checkGLError("del dfbTextures");
         }

         if (sfbDepthTextures != null) {
            GlStateManager.deleteTextures(sfbDepthTextures);
            fillIntBufferZero(sfbDepthTextures);
            checkGLError("del shadow depth");
         }

         if (sfbColorTextures != null) {
            GlStateManager.deleteTextures(sfbColorTextures);
            fillIntBufferZero(sfbColorTextures);
            checkGLError("del shadow color");
         }

         if (dfbDrawBuffers != null) {
            fillIntBufferZero(dfbDrawBuffers);
         }

         if (customUniforms != null) {
            customUniforms.deleteTexture();
            customUniforms = null;
         }

         SMCLog.info("Uninit");
         shadowPassInterval = 0;
         shouldSkipDefaultShadow = false;
         isShaderPackInitialized = false;
         checkGLError("Shaders.uninit");
      }
   }

   public static void scheduleResize() {
      renderDisplayHeight = 0;
   }

   public static void scheduleResizeShadow() {
      needResizeShadow = true;
   }

   private static void resize() {
      renderDisplayWidth = mc.displayWidth;
      renderDisplayHeight = mc.displayHeight;
      renderWidth = Math.round(renderDisplayWidth * configRenderResMul);
      renderHeight = Math.round(renderDisplayHeight * configRenderResMul);
      setupFrameBuffer();
   }

   private static void resizeShadow() {
      needResizeShadow = false;
      shadowMapWidth = Math.round(spShadowMapWidth * configShadowResMul);
      shadowMapHeight = Math.round(spShadowMapHeight * configShadowResMul);
      setupShadowFrameBuffer();
   }

   private static void setupFrameBuffer() {
      if (dfb != 0) {
         EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
         GlStateManager.deleteTextures(dfbDepthTextures);
         GlStateManager.deleteTextures(dfbColorTextures);
      }

      dfb = EXTFramebufferObject.glGenFramebuffersEXT();
      GL11.glGenTextures((IntBuffer)((Buffer)dfbDepthTextures).clear().limit(usedDepthBuffers));
      GL11.glGenTextures((IntBuffer)((Buffer)dfbColorTextures).clear().limit(16));
      ((Buffer)dfbDepthTextures).position(0);
      ((Buffer)dfbColorTextures).position(0);
      ((Buffer)dfbColorTextures.get(dfbColorTexturesA)).position(0);
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
      GL20.glDrawBuffers(0);
      GL11.glReadBuffer(0);

      for (int i = 0; i < usedDepthBuffers; i++) {
         GlStateManager.bindTexture(dfbDepthTextures.get(i));
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 34891, 6409);
         GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5126, (FloatBuffer)null);
      }

      EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
      GL20.glDrawBuffers(dfbDrawBuffers);
      GL11.glReadBuffer(0);
      checkGLError("FT d");

      for (int j = 0; j < usedColorBuffers; j++) {
         GlStateManager.bindTexture(dfbColorTexturesA[j]);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[j], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, dfbColorTexturesA[j], 0);
         checkGLError("FT c");
      }

      for (int k = 0; k < usedColorBuffers; k++) {
         GlStateManager.bindTexture(dfbColorTexturesA[8 + k]);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[k], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
         checkGLError("FT ca");
      }

      int l = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (l == 36058) {
         printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

         for (int m = 0; m < usedColorBuffers; m++) {
            GlStateManager.bindTexture(dfbColorTextures.get(m));
            GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + m, 3553, dfbColorTextures.get(m), 0);
            checkGLError("FT c");
         }

         l = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (l == 36053) {
            SMCLog.info("complete");
         }
      }

      GlStateManager.bindTexture(0);
      if (l != 36053) {
         printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + l + ")");
      } else {
         SMCLog.info("Framebuffer created.");
      }
   }

   private static void setupShadowFrameBuffer() {
      if (usedShadowDepthBuffers != 0) {
         if (sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
            GlStateManager.deleteTextures(sfbDepthTextures);
            GlStateManager.deleteTextures(sfbColorTextures);
         }

         sfb = EXTFramebufferObject.glGenFramebuffersEXT();
         EXTFramebufferObject.glBindFramebufferEXT(36160, sfb);
         GL11.glDrawBuffer(0);
         GL11.glReadBuffer(0);
         GL11.glGenTextures((IntBuffer)((Buffer)sfbDepthTextures).clear().limit(usedShadowDepthBuffers));
         GL11.glGenTextures((IntBuffer)((Buffer)sfbColorTextures).clear().limit(usedShadowColorBuffers));
         ((Buffer)sfbDepthTextures).position(0);
         ((Buffer)sfbColorTextures).position(0);

         for (int i = 0; i < usedShadowDepthBuffers; i++) {
            GlStateManager.bindTexture(sfbDepthTextures.get(i));
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            int j = shadowFilterNearest[i] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, j);
            GL11.glTexParameteri(3553, 10240, j);
            if (shadowHardwareFilteringEnabled[i]) {
               GL11.glTexParameteri(3553, 34892, 34894);
            }

            GL11.glTexImage2D(3553, 0, 6402, shadowMapWidth, shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         checkGLError("FT sd");

         for (int k = 0; k < usedShadowColorBuffers; k++) {
            GlStateManager.bindTexture(sfbColorTextures.get(k));
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            int l = shadowColorFilterNearest[k] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, l);
            GL11.glTexParameteri(3553, 10240, l);
            GL11.glTexImage2D(3553, 0, 6408, shadowMapWidth, shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k, 3553, sfbColorTextures.get(k), 0);
            checkGLError("FT sc");
         }

         GlStateManager.bindTexture(0);
         if (usedShadowColorBuffers > 0) {
            GL20.glDrawBuffers(sfbDrawBuffers);
         }

         int m = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (m != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + m + ")");
         } else {
            SMCLog.info("Shadow framebuffer created.");
         }
      }
   }

   public static void beginRender(Minecraft minecraft, float partialTicks, long finishTimeNano) {
      checkGLError("pre beginRender");
      checkWorldChanged(mc.theWorld);
      mc = minecraft;
      mc.mcProfiler.startSection("init");
      entityRenderer = mc.entityRenderer;
      if (!isShaderPackInitialized) {
         try {
            init();
         } catch (IllegalStateException illegalStateException) {
            if (Config.normalize(illegalStateException.getMessage()).equals("Function is not supported")) {
               printChatAndLogError("[Shaders] Error: " + illegalStateException.getMessage());
               illegalStateException.printStackTrace();
               setShaderPack(packNameNone);
               return;
            }
         }
      }

      if (mc.displayWidth != renderDisplayWidth || mc.displayHeight != renderDisplayHeight) {
         resize();
      }

      if (needResizeShadow) {
         resizeShadow();
      }

      worldTime = mc.theWorld.getWorldTime();
      diffWorldTime = (worldTime - lastWorldTime) % 24000L;
      if (diffWorldTime < 0L) {
         diffWorldTime += 24000L;
      }

      lastWorldTime = worldTime;
      moonPhase = mc.theWorld.getMoonPhase();
      frameCounter++;
      if (frameCounter >= 720720) {
         frameCounter = 0;
      }

      systemTime = System.currentTimeMillis();
      if (lastSystemTime == 0L) {
         lastSystemTime = systemTime;
      }

      diffSystemTime = systemTime - lastSystemTime;
      lastSystemTime = systemTime;
      frameTime = (float)diffSystemTime / 1000.0F;
      frameTimeCounter = frameTimeCounter + frameTime;
      frameTimeCounter %= 3600.0F;
      rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
      float f = (float)diffSystemTime * 0.01F;
      float g = (float)Math.exp(Math.log(0.5) * f / (wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
      wetness = wetness * g + rainStrength * (1.0F - g);
      Entity entity = mc.getRenderViewEntity();
      if (entity != null) {
         isSleeping = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping();
         eyePosY = (float)entity.posY * partialTicks + (float)entity.lastTickPosY * (1.0F - partialTicks);
         eyeBrightness = entity.getBrightnessForRender(partialTicks);
         g = (float)diffSystemTime * 0.01F;
         float h = (float)Math.exp(Math.log(0.5) * g / eyeBrightnessHalflife);
         eyeBrightnessFadeX = eyeBrightnessFadeX * h + (eyeBrightness & 65535) * (1.0F - h);
         eyeBrightnessFadeY = eyeBrightnessFadeY * h + (eyeBrightness >> 16) * (1.0F - h);
         isEyeInWater = mc.gameSettings.thirdPersonView == 0 && !isSleeping && mc.thePlayer.isInsideOfMaterial(Material.water) ? 1 : 0;
         if (mc.thePlayer != null) {
            nightVision = 0.0F;
            if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
               nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(mc.thePlayer, partialTicks);
            }

            blindness = 0.0F;
            if (mc.thePlayer.isPotionActive(Potion.blindness)) {
               int i = mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
               blindness = Config.limit(i / 20.0F, 0.0F, 1.0F);
            }
         }

         Vec3 vec3 = mc.theWorld.getSkyColor(entity, partialTicks);
         vec3 = CustomColors.getWorldSkyColor(vec3, currentWorld, entity, partialTicks);
         skyColorR = (float)vec3.xCoord;
         skyColorG = (float)vec3.yCoord;
         skyColorB = (float)vec3.zCoord;
      }

      isRenderingWorld = true;
      isCompositeRendered = false;
      isHandRenderedMain = false;
      if (usedShadowDepthBuffers >= 1) {
         GlStateManager.setActiveTexture(33988);
         GlStateManager.bindTexture(sfbDepthTextures.get(0));
         if (usedShadowDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33989);
            GlStateManager.bindTexture(sfbDepthTextures.get(1));
         }
      }

      GlStateManager.setActiveTexture(33984);

      for (int j = 0; j < usedColorBuffers; j++) {
         GlStateManager.bindTexture(dfbColorTexturesA[j]);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         GlStateManager.bindTexture(dfbColorTexturesA[8 + j]);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      GlStateManager.bindTexture(0);

      for (int k = 0; k < 4 && 4 + k < usedColorBuffers; k++) {
         GlStateManager.setActiveTexture(33991 + k);
         GlStateManager.bindTexture(dfbColorTextures.get(4 + k));
      }

      GlStateManager.setActiveTexture(33990);
      GlStateManager.bindTexture(dfbDepthTextures.get(0));
      if (usedDepthBuffers >= 2) {
         GlStateManager.setActiveTexture(33995);
         GlStateManager.bindTexture(dfbDepthTextures.get(1));
         if (usedDepthBuffers >= 3) {
            GlStateManager.setActiveTexture(33996);
            GlStateManager.bindTexture(dfbDepthTextures.get(2));
         }
      }

      for (int l = 0; l < usedShadowColorBuffers; l++) {
         GlStateManager.setActiveTexture(33997 + l);
         GlStateManager.bindTexture(sfbColorTextures.get(l));
      }

      if (noiseTextureEnabled) {
         GlStateManager.setActiveTexture(33984 + customUniforms.textureUnit);
         GlStateManager.bindTexture(customUniforms.getTexID());
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      bindCustomTextures(customTexturesGbuffers);
      GlStateManager.setActiveTexture(33984);
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
      ShadersRender.renderShadowMap(entityRenderer, 0, partialTicks, finishTimeNano);
      mc.mcProfiler.endSection();
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);

      for (int m = 0; m < usedColorBuffers; m++) {
         colorTexturesToggle[m] = 0;
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + m, 3553, dfbColorTexturesA[m], 0);
      }

      checkGLError("end beginRender");
   }

   private static void checkWorldChanged(World world) {
      if (currentWorld != world) {
         World world2 = currentWorld;
         currentWorld = world;
         if (world2 != null && world != null) {
            int i = world2.provider.getDimensionId();
            int j = world.provider.getDimensionId();
            boolean bl = shaderPackDimensions.contains(i);
            boolean bl2 = shaderPackDimensions.contains(j);
            if (bl || bl2) {
               uninit();
            }
         }
      }
   }

   public static void beginRenderPass(int pass, float partialTicks, long finishTimeNano) {
      if (!isShadowPass) {
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         activeDrawBuffers = null;
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
         useProgram(2);
         checkGLError("end beginRenderPass");
      }
   }

   public static void setViewport(int vx, int vy, int vw, int vh) {
      GlStateManager.colorMask(true, true, true, true);
      if (isShadowPass) {
         GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
      } else {
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         isRenderingDfb = true;
         GlStateManager.enableCull();
         GlStateManager.enableDepth();
         setDrawBuffers(drawBuffersNone);
         useProgram(2);
         checkGLError("beginRenderPass");
      }
   }

   public static int setFogMode(int val) {
      fogMode = val;
      return val;
   }

   public static void setFogColor(float r, float g, float b) {
      fogColorR = r;
      fogColorG = g;
      fogColorB = b;
   }

   public static void setClearColor(float red, float green, float blue, float alpha) {
      GlStateManager.clearColor(red, green, blue, alpha);
      clearColorR = red;
      clearColorG = green;
      clearColorB = blue;
   }

   public static void clearRenderBuffer() {
      if (isShadowPass) {
         checkGLError("shadow clear pre");
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL20.glDrawBuffers(programsDrawBuffers[30]);
         checkFramebufferStatus("shadow clear");
         GL11.glClear(16640);
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
            GL11.glClear(16384);
         }

         for (int i = 2; i < usedColorBuffers; i++) {
            if (gbuffersClear[i]) {
               GL20.glDrawBuffers(36064 + i);
               GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               GL11.glClear(16384);
            }
         }

         setDrawBuffers(dfbDrawBuffers);
         checkFramebufferStatus("clear");
         checkGLError("clear");
      }
   }

   public static void setCamera(float partialTicks) {
      Entity entity = mc.getRenderViewEntity();
      double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double e = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      cameraPositionX = d;
      cameraPositionY = e;
      cameraPositionZ = d2;
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

   public static void setCameraShadow(float partialTicks) {
      Entity entity = mc.getRenderViewEntity();
      double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double e = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      cameraPositionX = d;
      cameraPositionY = e;
      cameraPositionZ = d2;
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
      GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      if (shadowMapIsOrtho) {
         GL11.glOrtho(-shadowMapHalfPlane, shadowMapHalfPlane, -shadowMapHalfPlane, shadowMapHalfPlane, 0.05F, 256.0);
      } else {
         GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / shadowMapHeight, 0.05F, 256.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -100.0F);
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      celestialAngle = mc.theWorld.getCelestialAngle(partialTicks);
      sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
      float f = celestialAngle * -360.0F;
      float g = shadowAngleInterval > 0.0F ? f % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;
      if (sunAngle <= 0.5) {
         GL11.glRotatef(f - g, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle;
      } else {
         GL11.glRotatef(f + 180.0F - g, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle - 0.5F;
      }

      if (shadowMapIsOrtho) {
         float h = shadowIntervalSize;
         float f2 = h / 2.0F;
         GL11.glTranslatef((float)d % h - f2, (float)e % h - f2, (float)d2 % h - f2);
      }

      float f3 = sunAngle * (float) (Math.PI * 2);
      float f4 = (float)Math.cos(f3);
      float f5 = (float)Math.sin(f3);
      float f6 = sunPathRotation * (float) (Math.PI * 2);
      float f7 = f4;
      float f8 = f5 * (float)Math.cos(f6);
      float f9 = f5 * (float)Math.sin(f6);
      if (sunAngle > 0.5) {
         f7 = -f4;
         f8 = -f8;
         f9 = -f9;
      }

      shadowLightPositionVector[0] = f7;
      shadowLightPositionVector[1] = f8;
      shadowLightPositionVector[2] = f9;
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
      checkGLError("setCamera");
   }

   public static void preCelestialRotate() {
      GL11.glRotatef(sunPathRotation * 1.0F, 0.0F, 0.0F, 1.0F);
      checkGLError("preCelestialRotate");
   }

   public static void postCelestialRotate() {
      FloatBuffer floatBuffer = tempMatrixDirectBuffer;
      ((Buffer)floatBuffer).clear();
      GL11.glGetFloat(2982, floatBuffer);
      floatBuffer.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
      SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
      System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
      setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
      setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
      setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
      checkGLError("postCelestialRotate");
   }

   public static void setUpPosition() {
      FloatBuffer floatBuffer = tempMatrixDirectBuffer;
      ((Buffer)floatBuffer).clear();
      GL11.glGetFloat(2982, floatBuffer);
      floatBuffer.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
      setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
   }

   public static void genCompositeMipmap() {
      if (hasGlGenMipmap) {
         for (int i = 0; i < usedColorBuffers; i++) {
            if ((activeCompositeMipmapSetting & 1 << i) != 0) {
               GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
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
      if (!isShadowPass) {
         checkGLError("pre-renderCompositeFinal");
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
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
         if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
               GlStateManager.setActiveTexture(33989);
               GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
         }

         for (int i = 0; i < usedColorBuffers; i++) {
            GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
            GlStateManager.bindTexture(dfbColorTexturesA[i]);
         }

         GlStateManager.setActiveTexture(33990);
         GlStateManager.bindTexture(dfbDepthTextures.get(0));
         if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
               GlStateManager.setActiveTexture(33996);
               GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
         }

         for (int j = 0; j < usedShadowColorBuffers; j++) {
            GlStateManager.setActiveTexture(33997 + j);
            GlStateManager.bindTexture(sfbColorTextures.get(j));
         }

         if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + customUniforms.textureUnit);
            GlStateManager.bindTexture(customUniforms.getTexID());
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
         }

         bindCustomTextures(customTexturesComposite);
         GlStateManager.setActiveTexture(33984);
         boolean bl = true;

         for (int k = 0; k < usedColorBuffers; k++) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k, 3553, dfbColorTexturesA[8 + k], 0);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
         GL20.glDrawBuffers(dfbDrawBuffers);
         checkGLError("pre-composite");

         for (int l = 0; l < 8; l++) {
            if (programsID[21 + l] != 0) {
               useProgram(21 + l);
               checkGLError(programNames[21 + l]);
               if (activeCompositeMipmapSetting != 0) {
                  genCompositeMipmap();
               }

               drawComposite();

               for (int m = 0; m < usedColorBuffers; m++) {
                  if (programsToggleColorTextures[21 + l][m]) {
                     int n = colorTexturesToggle[m];
                     int i2 = colorTexturesToggle[m] = 8 - n;
                     GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[m]);
                     GlStateManager.bindTexture(dfbColorTexturesA[i2 + m]);
                     EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + m, 3553, dfbColorTexturesA[n + m], 0);
                  }
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         checkGLError("composite");
         isRenderingDfb = false;
         mc.getFramebuffer().bindFramebuffer(true);
         OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().framebufferTexture, 0);
         GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
         if (EntityRenderer.anaglyphEnable) {
            boolean bl2 = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(bl2, !bl2, !bl2, true);
         }

         GlStateManager.depthMask(true);
         GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
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
         checkGLError("final");
         if (activeCompositeMipmapSetting != 0) {
            genCompositeMipmap();
         }

         drawComposite();
         checkGLError("renderCompositeFinal");
         isCompositeRendered = true;
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         useProgram(0);
      }
   }

   public static void endRender() {
      if (isShadowPass) {
         checkGLError("shadow endRender");
      } else {
         if (!isCompositeRendered) {
            renderFinal();
         }

         isRenderingWorld = false;
         GlStateManager.colorMask(true, true, true, true);
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

   public static void setSkyColor(Vec3 v3color) {
      skyColorR = (float)v3color.xCoord;
      skyColorG = (float)v3color.yCoord;
      skyColorB = (float)v3color.zCoord;
      setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
   }

   public static void drawHorizon() {
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
      float f = mc.gameSettings.renderDistanceChunks * 16;
      double d = f * 0.9238;
      double e = f * 0.3826;
      double d2 = -e;
      double d3 = -d;
      double d4 = 16.0;
      double d5 = -cameraPositionY;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(d2, d5, d3).endVertex();
      worldRenderer.pos(d2, d4, d3).endVertex();
      worldRenderer.pos(d3, d4, d2).endVertex();
      worldRenderer.pos(d3, d5, d2).endVertex();
      worldRenderer.pos(d3, d5, d2).endVertex();
      worldRenderer.pos(d3, d4, d2).endVertex();
      worldRenderer.pos(d3, d4, e).endVertex();
      worldRenderer.pos(d3, d5, e).endVertex();
      worldRenderer.pos(d3, d5, e).endVertex();
      worldRenderer.pos(d3, d4, e).endVertex();
      worldRenderer.pos(d2, d4, e).endVertex();
      worldRenderer.pos(d2, d5, e).endVertex();
      worldRenderer.pos(d2, d5, e).endVertex();
      worldRenderer.pos(d2, d4, e).endVertex();
      worldRenderer.pos(e, d4, d).endVertex();
      worldRenderer.pos(e, d5, d).endVertex();
      worldRenderer.pos(e, d5, d).endVertex();
      worldRenderer.pos(e, d4, d).endVertex();
      worldRenderer.pos(d, d4, e).endVertex();
      worldRenderer.pos(d, d5, e).endVertex();
      worldRenderer.pos(d, d5, e).endVertex();
      worldRenderer.pos(d, d4, e).endVertex();
      worldRenderer.pos(d, d4, d2).endVertex();
      worldRenderer.pos(d, d5, d2).endVertex();
      worldRenderer.pos(d, d5, d2).endVertex();
      worldRenderer.pos(d, d4, d2).endVertex();
      worldRenderer.pos(e, d4, d3).endVertex();
      worldRenderer.pos(e, d5, d3).endVertex();
      worldRenderer.pos(e, d5, d3).endVertex();
      worldRenderer.pos(e, d4, d3).endVertex();
      worldRenderer.pos(d2, d4, d3).endVertex();
      worldRenderer.pos(d2, d5, d3).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void preSkyList() {
      setUpPosition();
      GL11.glColor3f(fogColorR, fogColorG, fogColorB);
      drawHorizon();
      GL11.glColor3f(skyColorR, skyColorG, skyColorB);
   }

   public static void endSky() {
      isRenderingSky = false;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(lightmapEnabled ? 3 : 2);
      popEntity();
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

   public static boolean shouldRenderClouds(GameSettings gs) {
      if (!shaderPackLoaded) {
         return true;
      }

      checkGLError("shouldRenderClouds");
      return isShadowPass ? configCloudShadow : gs.clouds > 0;
   }

   public static void beginClouds() {
      fogEnabled = true;
      pushEntity(-3, 0);
      useProgram(6);
   }

   public static void endClouds() {
      disableFog();
      popEntity();
      useProgram(lightmapEnabled ? 3 : 2);
   }

   public static void beginEntities() {
      if (isRenderingWorld) {
         useProgram(16);
         resetDisplayListModels();
      }
   }

   public static void nextEntity(Entity entity) {
      if (isRenderingWorld) {
         useProgram(16);
         setEntityId(entity);
      }
   }

   public static void setEntityId(Entity entity) {
      if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined()) {
         int i = EntityList.getEntityID(entity);
         uniformEntityId.setValue(i);
      }
   }

   public static void beginSpiderEyes() {
      if (isRenderingWorld && programsID[18] != programsID[0]) {
         useProgram(18);
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
      }
   }

   public static void endEntities() {
      if (isRenderingWorld) {
         useProgram(lightmapEnabled ? 3 : 2);
      }
   }

   public static void setEntityColor(float r, float g, float b, float a) {
      if (isRenderingWorld && !isShadowPass) {
         uniformEntityColor.setValue(r, g, b, a);
      }
   }

   public static void beginLivingDamage() {
      if (isRenderingWorld) {
         ShadersTex.bindTexture(defaultTexture);
         if (!isShadowPass) {
            setDrawBuffers(drawBuffersColorAtt0);
         }
      }
   }

   public static void endLivingDamage() {
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

   public static void nextBlockEntity(TileEntity tileEntity) {
      if (isRenderingWorld) {
         checkGLError("nextBlockEntity");
         useProgram(13);
         setBlockEntityId(tileEntity);
      }
   }

   public static void setBlockEntityId(TileEntity tileEntity) {
      if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined()) {
         Block block = tileEntity.getBlockType();
         int i = Block.getIdFromBlock(block);
         uniformBlockEntityId.setValue(i);
      }
   }

   public static void endBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("endBlockEntities");
         useProgram(lightmapEnabled ? 3 : 2);
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
      }
   }

   public static void beginLitParticles() {
      useProgram(3);
   }

   public static void beginParticles() {
      useProgram(2);
   }

   public static void endParticles() {
      useProgram(3);
   }

   public static void readCenterDepth() {
      if (!isShadowPass && centerDepthSmoothEnabled) {
         ((Buffer)tempDirectFloatBuffer).clear();
         GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
         centerDepth = tempDirectFloatBuffer.get(0);
         float f = (float)diffSystemTime * 0.01F;
         float g = (float)Math.exp(Math.log(0.5) * f / centerDepthSmoothHalflife);
         centerDepthSmooth = centerDepthSmooth * g + centerDepth * (1.0F - g);
      }
   }

   public static void beginWeather() {
      if (!isShadowPass) {
         if (usedDepthBuffers >= 3) {
            GlStateManager.setActiveTexture(33996);
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            GlStateManager.setActiveTexture(33984);
         }

         GlStateManager.enableDepth();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableAlpha();
         useProgram(20);
      }
   }

   public static void endWeather() {
      GlStateManager.disableBlend();
      useProgram(3);
   }

   public static void preWater() {
      if (usedDepthBuffers >= 2) {
         GlStateManager.setActiveTexture(33995);
         checkGLError("pre copy depth");
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
         checkGLError("copy depth");
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

   public static void endWater() {
      if (isRenderingWorld) {
         if (isShadowPass) {
         }

         useProgram(lightmapEnabled ? 3 : 2);
      }
   }

   public static void beginProjectRedHalo() {
      if (isRenderingWorld) {
         useProgram(1);
      }
   }

   public static void endProjectRedHalo() {
      if (isRenderingWorld) {
         useProgram(3);
      }
   }

   public static void applyHandDepth() {
      if (configHandDepthMul != 1.0) {
         GL11.glScaled(1.0, 1.0, configHandDepthMul);
      }
   }

   public static void beginHand() {
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

   public static void beginFPOverlay() {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
   }

   public static void endFPOverlay() {
   }

   public static void glEnableWrapper(int cap) {
      GL11.glEnable(cap);
      if (cap == 3553) {
         enableTexture2D();
      } else if (cap == 2912) {
         enableFog();
      }
   }

   public static void glDisableWrapper(int cap) {
      GL11.glDisable(cap);
      if (cap == 3553) {
         disableTexture2D();
      } else if (cap == 2912) {
         disableFog();
      }
   }

   public static void z(int i) {
      GL11.glEnable(i);
      enableTexture2D();
   }

   public static void sglDisableT2D(int cap) {
      GL11.glDisable(cap);
      disableTexture2D();
   }

   public static void sglEnableFog(int cap) {
      GL11.glEnable(cap);
      enableFog();
   }

   public static void sglDisableFog(int cap) {
      GL11.glDisable(cap);
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

   public static void setFog(int fogModeIn) {
      GlStateManager.setFog(fogModeIn);
      fogModeIn = fogModeIn;
      if (fogEnabled) {
         setProgramUniform1i("fogMode", fogModeIn);
      }
   }

   public static void sglFogi(int pname, int param) {
      GL11.glFogi(pname, param);
      if (pname == 2917) {
         fogMode = param;
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

   public static void disableLightmap() {
      lightmapEnabled = false;
      if (activeProgram == 3) {
         useProgram(2);
      }
   }

   public static int getEntityData() {
      return entityData[entityDataIndex * 2];
   }

   public static int getEntityData2() {
      return entityData[entityDataIndex * 2 + 1];
   }

   public static int setEntityData1(int data1) {
      entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & 65535 | data1 << 16;
      return data1;
   }

   public static int setEntityData2(int data2) {
      entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | data2 & 65535;
      return data2;
   }

   public static void pushEntity(int data0, int data1) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = data0 & 65535 | data1 << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void pushEntity(int data0) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = data0 & 65535;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void pushEntity(Block block) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = Block.blockRegistry.getIDForObject(block) & 65535 | block.getRenderType() << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void popEntity() {
      entityData[entityDataIndex * 2] = 0;
      entityData[entityDataIndex * 2 + 1] = 0;
      entityDataIndex--;
   }

   public static void mcProfilerEndSection() {
      mc.mcProfiler.endSection();
   }

   public static String getShaderPackName() {
      return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
   }

   public static InputStream getShaderPackResourceStream(String path) {
      return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
   }

   public static void nextAntialiasingLevel() {
      configAntialiasingLevel += 2;
      configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
      if (configAntialiasingLevel > 4) {
         configAntialiasingLevel = 0;
      }

      configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
   }

   public static void checkShadersModInstalled() {
      try {
         Class class2 = Class.forName("shadersmod.transform.SMCClassTransformer");
      } catch (Throwable throwable) {
         return;
      }

      throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
   }

   public static void resourcesReloaded() {
      loadShaderPackResources();
   }

   private static void loadShaderPackResources() {
      shaderPackResources = new HashMap<>();
      if (shaderPackLoaded) {
         ArrayList arrayList = new ArrayList();
         String string = "/shaders/lang/";
         String string2 = "en_US";
         String string3 = ".lang";
         arrayList.add(string + string2 + string3);
         if (!Config.getGameSettings().language.equals(string2)) {
            arrayList.add(string + Config.getGameSettings().language + string3);
         }

         try {
            for (Object object : arrayList) {
               String string4 = (String)object;
               InputStream inputStream = shaderPack.getResourceAsStream(string4);
               if (inputStream != null) {
                  Properties properties = new Properties();
                  Lang.loadLocaleData(inputStream, properties);
                  inputStream.close();

                  for (Object object2 : properties.keySet()) {
                     String string5 = properties.getProperty((String)object2);
                     shaderPackResources.put((String)object2, string5);
                  }
               }
            }
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }
   }

   public static String translate(String key, String def) {
      String string = shaderPackResources.get(key);
      return string == null ? def : string;
   }

   public static boolean isProgramPath(String program) {
      if (program == null) {
         return false;
      }

      if (program.length() <= 0) {
         return false;
      }

      int i = program.lastIndexOf("/");
      if (i >= 0) {
         program = program.substring(i + 1);
      }

      return Arrays.asList(programNames).contains(program);
   }

   public static void setItemToRenderMain(ItemStack itemToRenderMain) {
      itemToRenderMainTranslucent = isTranslucentBlock(itemToRenderMain);
   }

   public static boolean isItemToRenderMainTranslucent() {
      return itemToRenderMainTranslucent;
   }

   private static boolean isTranslucentBlock(ItemStack stack) {
      if (stack == null) {
         return false;
      }

      Item item = stack.getItem();
      if (item == null) {
         return false;
      }

      if (!(item instanceof ItemBlock)) {
         return false;
      }

      ItemBlock itemBlock = (ItemBlock)item;
      Block block = itemBlock.getBlock();
      if (block == null) {
         return false;
      }

      EnumWorldBlockLayer enumWorldBlockLayer = block.getBlockLayer();
      return enumWorldBlockLayer == EnumWorldBlockLayer.TRANSLUCENT;
   }

   public static boolean e_boolean() {
      return isHandRenderedMain;
   }

   public static void setHandRenderedMain(boolean isHandRenderedMainIn) {
   }

   public static float getShadowRenderDistance() {
      return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
   }

   static {
      ((Buffer)drawBuffersNone).limit(0);
      ((Buffer)drawBuffersColorAtt0.put(36064)).position(0).limit(1);
   }
}
