package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.ClearWater;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.CustomSky;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.Lang;
import net.minecraft.src.NaturalTextures;
import net.minecraft.src.RandomMobs;
import net.minecraft.src.Reflector;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import shadersmod.client.Shaders;

public class GameSettings {
   private static final Logger logger = LogManager.getLogger();
   private static final Gson gson = new Gson();
   private static final ParameterizedType typeListString = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   private static final String[] GUISCALES = new String[]{
      "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"
   };
   private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] STREAM_COMPRESSIONS = new String[]{
      "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
   };
   private static final String[] STREAM_CHAT_MODES = new String[]{
      "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
   };
   private static final String[] STREAM_CHAT_FILTER_MODES = new String[]{
      "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
   };
   private static final String[] STREAM_MIC_MODES = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   private static final String[] CLOUDS_TYPES = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public float mouseSensitivity = 0.5F;
   public boolean invertMouse;
   public int renderDistanceChunks = -1;
   public boolean viewBobbing = true;
   public boolean anaglyph;
   public boolean fboEnable = true;
   public int limitFramerate = 120;
   public int clouds = 2;
   public boolean fancyGraphics = true;
   public int ambientOcclusion = 2;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
   public boolean chatColours = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public float chatOpacity = 1.0F;
   public boolean snooperEnabled = true;
   public boolean fullScreen;
   public boolean enableVsync = true;
   public boolean useVbo = false;
   public boolean allowBlockAlternatives = true;
   public boolean reducedDebugInfo = false;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
   public boolean touchscreen;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public float chatScale = 1.0F;
   public float chatWidth = 1.0F;
   public float chatHeightUnfocused = 0.44366196F;
   public float chatHeightFocused = 1.0F;
   public boolean showInventoryAchievementHint = true;
   public int mipmapLevels = 4;
   private Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
   public float streamBytesPerPixel = 0.5F;
   public float streamMicVolume = 1.0F;
   public float streamGameVolume = 1.0F;
   public float streamKbps = 0.5412844F;
   public float streamFps = 0.31690142F;
   public int streamCompression = 1;
   public boolean streamSendMetadata = true;
   public String streamPreferredServer = "";
   public int streamChatEnabled = 0;
   public int streamChatUserFilter = 0;
   public int streamMicToggleBehavior = 0;
   public boolean useNativeTransport = true;
   public boolean entityShadows = true;
   public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
   public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
   public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
   public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
   public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
   public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
   public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
   public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
   public KeyBinding keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
   public KeyBinding keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
   public KeyBinding keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
   public KeyBinding keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
   public KeyBinding[] keyBindsHotbar = new KeyBinding[]{
      new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
      new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
      new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
      new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
      new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
      new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
      new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
      new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
      new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
   };
   public KeyBinding[] keyBindings;
   public Minecraft mc;
   private File optionsFile;
   public EnumDifficulty difficulty;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public String lastServer;
   public boolean smoothCamera;
   public boolean debugCamEnable;
   public float fovSetting;
   public float gammaSetting;
   public float saturation;
   public int guiScale;
   public int particleSetting;
   public String language;
   public boolean forceUnicodeFont;
   public int ofFogType = 1;
   public float ofFogStart = 0.8F;
   public int ofMipmapType = 0;
   public boolean ofOcclusionFancy = false;
   public boolean ofSmoothFps = false;
   public boolean ofSmoothWorld = Config.isSingleProcessor();
   public boolean ofLazyChunkLoading = Config.isSingleProcessor();
   public float ofAoLevel = 1.0F;
   public int ofAaLevel = 0;
   public int ofAfLevel = 1;
   public int ofClouds = 0;
   public float ofCloudsHeight = 0.0F;
   public int ofTrees = 0;
   public int ofRain = 0;
   public int ofDroppedItems = 0;
   public int ofBetterGrass = 3;
   public int ofAutoSaveTicks = 4000;
   public boolean ofLagometer = false;
   public boolean ofProfiler = false;
   public boolean ofShowFps = false;
   public boolean ofWeather = true;
   public boolean ofSky = true;
   public boolean ofStars = true;
   public boolean ofSunMoon = true;
   public int ofVignette = 0;
   public int ofChunkUpdates = 1;
   public boolean ofChunkUpdatesDynamic = false;
   public int ofTime = 0;
   public boolean ofClearWater = false;
   public boolean ofBetterSnow = false;
   public String ofFullscreenMode = "Default";
   public boolean ofSwampColors = true;
   public boolean ofRandomMobs = true;
   public boolean ofSmoothBiomes = true;
   public boolean ofCustomFonts = true;
   public boolean ofCustomColors = true;
   public boolean ofCustomSky = true;
   public boolean ofShowCapes = true;
   public int ofConnectedTextures = 2;
   public boolean ofCustomItems = true;
   public boolean ofNaturalTextures = false;
   public boolean ofFastMath = false;
   public boolean ofFastRender = true;
   public int ofTranslucentBlocks = 0;
   public boolean ofDynamicFov = true;
   public int ofDynamicLights = 3;
   public int ofAnimatedWater = 0;
   public int ofAnimatedLava = 0;
   public boolean ofAnimatedFire = true;
   public boolean ofAnimatedPortal = true;
   public boolean ofAnimatedRedstone = true;
   public boolean ofAnimatedExplosion = true;
   public boolean ofAnimatedFlame = true;
   public boolean ofAnimatedSmoke = true;
   public boolean ofVoidParticles = true;
   public boolean ofWaterParticles = true;
   public boolean ofRainSplash = true;
   public boolean ofPortalParticles = true;
   public boolean ofPotionParticles = true;
   public boolean ofFireworkParticles = true;
   public boolean ofDrippingWaterLava = true;
   public boolean ofAnimatedTerrain = true;
   public boolean ofAnimatedTextures = true;
   public static final int DEFAULT = 0;
   public static final int FAST = 1;
   public static final int FANCY = 2;
   public static final int OFF = 3;
   public static final int SMART = 4;
   public static final int ANIM_ON = 0;
   public static final int ANIM_GENERATED = 1;
   public static final int ANIM_OFF = 2;
   public static final String DEFAULT_STR = "Default";
   private static final int[] OF_TREES_VALUES = new int[]{0, 1, 4, 2};
   private static final int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
   private static final String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public KeyBinding ofKeyBindZoom;
   private File optionsFileOF;

   public GameSettings(Minecraft mcIn, File optionsFileIn) {
      this.keyBindings = ArrayUtils.addAll(
         new KeyBinding[]{
            this.keyBindAttack,
            this.keyBindUseItem,
            this.keyBindForward,
            this.keyBindLeft,
            this.keyBindBack,
            this.keyBindRight,
            this.keyBindJump,
            this.keyBindSneak,
            this.keyBindSprint,
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.keyBindPlayerList,
            this.keyBindPickBlock,
            this.keyBindCommand,
            this.keyBindScreenshot,
            this.keyBindTogglePerspective,
            this.keyBindSmoothCamera,
            this.keyBindStreamStartStop,
            this.keyBindStreamPauseUnpause,
            this.keyBindStreamCommercials,
            this.keyBindStreamToggleMic,
            this.keyBindFullscreen,
            this.keyBindSpectatorOutlines
         },
         this.keyBindsHotbar
      );
      this.difficulty = EnumDifficulty.NORMAL;
      this.lastServer = "";
      this.fovSetting = 70.0F;
      this.language = "en_US";
      this.forceUnicodeFont = false;
      this.mc = mcIn;
      this.optionsFile = new File(optionsFileIn, "options.txt");
      this.optionsFileOF = new File(optionsFileIn, "optionsof.txt");
      this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
      this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
      GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
      this.renderDistanceChunks = 8;
      this.loadOptions();
      Config.initGameSettings(this);
   }

   public GameSettings() {
      this.keyBindings = ArrayUtils.addAll(
         new KeyBinding[]{
            this.keyBindAttack,
            this.keyBindUseItem,
            this.keyBindForward,
            this.keyBindLeft,
            this.keyBindBack,
            this.keyBindRight,
            this.keyBindJump,
            this.keyBindSneak,
            this.keyBindSprint,
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.keyBindPlayerList,
            this.keyBindPickBlock,
            this.keyBindCommand,
            this.keyBindScreenshot,
            this.keyBindTogglePerspective,
            this.keyBindSmoothCamera,
            this.keyBindStreamStartStop,
            this.keyBindStreamPauseUnpause,
            this.keyBindStreamCommercials,
            this.keyBindStreamToggleMic,
            this.keyBindFullscreen,
            this.keyBindSpectatorOutlines
         },
         this.keyBindsHotbar
      );
      this.difficulty = EnumDifficulty.NORMAL;
      this.lastServer = "";
      this.fovSetting = 70.0F;
      this.language = "en_US";
      this.forceUnicodeFont = false;
   }

   public static String getKeyDisplayString(int key) {
      return key < 0
         ? I18n.format("key.mouseButton", key + 101)
         : (key < 256 ? Keyboard.getKeyName(key) : String.format("%c", (char)(key - 256)).toUpperCase());
   }

   public static boolean isKeyDown(KeyBinding key) {
      int i = key.getKeyCode();
      return i < -100 || i > 255
         ? false
         : (key.getKeyCode() == 0 ? false : (key.getKeyCode() < 0 ? Mouse.isButtonDown(key.getKeyCode() + 100) : Keyboard.isKeyDown(key.getKeyCode())));
   }

   public void setOptionKeyBinding(KeyBinding key, int keyCode) {
      key.setKeyCode(keyCode);
      this.saveOptions();
   }

   public void setOptionFloatValue(GameSettings.Options settingsOption, float value) {
      this.setOptionFloatValueOF(settingsOption, value);
      if (settingsOption == GameSettings.Options.SENSITIVITY) {
         this.mouseSensitivity = value;
      }

      if (settingsOption == GameSettings.Options.FOV) {
         this.fovSetting = value;
      }

      if (settingsOption == GameSettings.Options.GAMMA) {
         this.gammaSetting = value;
      }

      if (settingsOption == GameSettings.Options.FRAMERATE_LIMIT) {
         this.limitFramerate = (int)value;
         this.enableVsync = false;
         if (this.limitFramerate <= 0) {
            this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
            this.enableVsync = true;
         }

         this.updateVSync();
      }

      if (settingsOption == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.chatHeightFocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.chatHeightUnfocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_WIDTH) {
         this.chatWidth = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.MIPMAP_LEVELS) {
         int i = this.mipmapLevels;
         this.mipmapLevels = (int)value;
         if (i != value) {
            this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
            this.mc.scheduleResourcesRefresh();
         }
      }

      if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)value;
         this.mc.renderGlobal.setDisplayListEntitiesDirty();
      }

      if (settingsOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         this.streamBytesPerPixel = value;
      }

      if (settingsOption == GameSettings.Options.STREAM_VOLUME_MIC) {
         this.streamMicVolume = value;
      }

      if (settingsOption == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         this.streamGameVolume = value;
      }

      if (settingsOption == GameSettings.Options.STREAM_KBPS) {
         this.streamKbps = value;
      }

      if (settingsOption == GameSettings.Options.STREAM_FPS) {
         this.streamFps = value;
      }
   }

   public void setOptionValue(GameSettings.Options settingsOption, int value) {
      this.setOptionValueOF(settingsOption, value);
      if (settingsOption == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if (settingsOption == GameSettings.Options.GUI_SCALE) {
         this.guiScale = this.guiScale + value & 3;
      }

      if (settingsOption == GameSettings.Options.PARTICLES) {
         this.particleSetting = (this.particleSetting + value) % 3;
      }

      if (settingsOption == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (settingsOption == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + value) % 3;
      }

      if (settingsOption == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
      }

      if (settingsOption == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (settingsOption == GameSettings.Options.ANAGLYPH) {
         if (!this.anaglyph && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
            return;
         }

         this.anaglyph = !this.anaglyph;
         this.mc.refreshResources();
      }

      if (settingsOption == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.updateRenderClouds();
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
      }

      if (settingsOption == GameSettings.Options.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_SEND_METADATA) {
         this.streamSendMetadata = !this.streamSendMetadata;
      }

      if (settingsOption == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + value) % 2;
      }

      if (settingsOption == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if (settingsOption == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (settingsOption == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if (settingsOption == GameSettings.Options.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if (settingsOption == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (settingsOption == GameSettings.Options.USE_FULLSCREEN) {
         this.fullScreen = !this.fullScreen;
         if (this.mc.isFullScreen() != this.fullScreen) {
            this.mc.toggleFullscreen();
         }
      }

      if (settingsOption == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         Display.setVSyncEnabled(this.enableVsync);
      }

      if (settingsOption == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if (settingsOption == GameSettings.Options.ENTITY_SHADOWS) {
         this.entityShadows = !this.entityShadows;
      }

      this.saveOptions();
   }

   public float getOptionFloatValue(GameSettings.Options settingOption) {
      return settingOption == GameSettings.Options.CLOUD_HEIGHT
         ? this.ofCloudsHeight
         : (
            settingOption == GameSettings.Options.AO_LEVEL
               ? this.ofAoLevel
               : (
                  settingOption == GameSettings.Options.AA_LEVEL
                     ? this.ofAaLevel
                     : (
                        settingOption == GameSettings.Options.AF_LEVEL
                           ? this.ofAfLevel
                           : (
                              settingOption == GameSettings.Options.MIPMAP_TYPE
                                 ? this.ofMipmapType
                                 : (
                                    settingOption == GameSettings.Options.FRAMERATE_LIMIT
                                       ? (
                                          this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync
                                             ? 0.0F
                                             : this.limitFramerate
                                       )
                                       : (
                                          settingOption == GameSettings.Options.FOV
                                             ? this.fovSetting
                                             : (
                                                settingOption == GameSettings.Options.GAMMA
                                                   ? this.gammaSetting
                                                   : (
                                                      settingOption == GameSettings.Options.SATURATION
                                                         ? this.saturation
                                                         : (
                                                            settingOption == GameSettings.Options.SENSITIVITY
                                                               ? this.mouseSensitivity
                                                               : (
                                                                  settingOption == GameSettings.Options.CHAT_OPACITY
                                                                     ? this.chatOpacity
                                                                     : (
                                                                        settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                           ? this.chatHeightFocused
                                                                           : (
                                                                              settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                                 ? this.chatHeightUnfocused
                                                                                 : (
                                                                                    settingOption == GameSettings.Options.CHAT_SCALE
                                                                                       ? this.chatScale
                                                                                       : (
                                                                                          settingOption == GameSettings.Options.CHAT_WIDTH
                                                                                             ? this.chatWidth
                                                                                             : (
                                                                                                settingOption == GameSettings.Options.FRAMERATE_LIMIT
                                                                                                   ? this.limitFramerate
                                                                                                   : (
                                                                                                      settingOption == GameSettings.Options.MIPMAP_LEVELS
                                                                                                         ? this.mipmapLevels
                                                                                                         : (
                                                                                                            settingOption
                                                                                                                  == GameSettings.Options.RENDER_DISTANCE
                                                                                                               ? this.renderDistanceChunks
                                                                                                               : (
                                                                                                                  settingOption
                                                                                                                        == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                     ? this.streamBytesPerPixel
                                                                                                                     : (
                                                                                                                        settingOption
                                                                                                                              == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                                                           ? this.streamMicVolume
                                                                                                                           : (
                                                                                                                              settingOption
                                                                                                                                    == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                                                 ? this.streamGameVolume
                                                                                                                                 : (
                                                                                                                                    settingOption
                                                                                                                                          == GameSettings.Options.STREAM_KBPS
                                                                                                                                       ? this.streamKbps
                                                                                                                                       : (
                                                                                                                                          settingOption
                                                                                                                                                == GameSettings.Options.STREAM_FPS
                                                                                                                                             ? this.streamFps
                                                                                                                                             : 0.0F
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
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public boolean getOptionOrdinalValue(GameSettings.Options settingOption) {
      switch (settingOption) {
         case INVERT_MOUSE:
            return this.invertMouse;
         case VIEW_BOBBING:
            return this.viewBobbing;
         case ANAGLYPH:
            return this.anaglyph;
         case FBO_ENABLE:
            return this.fboEnable;
         case CHAT_COLOR:
            return this.chatColours;
         case CHAT_LINKS:
            return this.chatLinks;
         case CHAT_LINKS_PROMPT:
            return this.chatLinksPrompt;
         case SNOOPER_ENABLED:
            return this.snooperEnabled;
         case USE_FULLSCREEN:
            return this.fullScreen;
         case ENABLE_VSYNC:
            return this.enableVsync;
         case USE_VBO:
            return this.useVbo;
         case TOUCHSCREEN:
            return this.touchscreen;
         case STREAM_SEND_METADATA:
            return this.streamSendMetadata;
         case FORCE_UNICODE_FONT:
            return this.forceUnicodeFont;
         case BLOCK_ALTERNATIVES:
            return this.allowBlockAlternatives;
         case REDUCED_DEBUG_INFO:
            return this.reducedDebugInfo;
         case ENTITY_SHADOWS:
            return this.entityShadows;
         default:
            return false;
      }
   }

   private static String getTranslation(String[] strArray, int index) {
      if (index < 0 || index >= strArray.length) {
         index = 0;
      }

      return I18n.format(strArray[index]);
   }

   public String getKeyBinding(GameSettings.Options settingOption) {
      String string = this.getKeyBindingOF(settingOption);
      if (string != null) {
         return string;
      }

      String string2 = I18n.format(settingOption.getEnumString()) + ": ";
      if (settingOption.getEnumFloat()) {
         float g = this.getOptionFloatValue(settingOption);
         float h = settingOption.normalizeValue(g);
         return settingOption == GameSettings.Options.SENSITIVITY
            ? (
               h == 0.0F
                  ? string2 + I18n.format("options.sensitivity.min")
                  : (h == 1.0F ? string2 + I18n.format("options.sensitivity.max") : string2 + (int)(h * 200.0F) + "%")
            )
            : (
               settingOption == GameSettings.Options.FOV
                  ? (g == 70.0F ? string2 + I18n.format("options.fov.min") : (g == 110.0F ? string2 + I18n.format("options.fov.max") : string2 + (int)g))
                  : (
                     settingOption == GameSettings.Options.FRAMERATE_LIMIT
                        ? (
                           g == GameSettings.Options.accessValueMax(settingOption)
                              ? string2 + I18n.format("options.framerateLimit.max")
                              : string2 + (int)g + " fps"
                        )
                        : (
                           settingOption == GameSettings.Options.RENDER_CLOUDS
                              ? (
                                 g == GameSettings.Options.accessValueMin(settingOption)
                                    ? string2 + I18n.format("options.cloudHeight.min")
                                    : string2 + ((int)g + 128)
                              )
                              : (
                                 settingOption == GameSettings.Options.GAMMA
                                    ? (
                                       h == 0.0F
                                          ? string2 + I18n.format("options.gamma.min")
                                          : (h == 1.0F ? string2 + I18n.format("options.gamma.max") : string2 + "+" + (int)(h * 100.0F) + "%")
                                    )
                                    : (
                                       settingOption == GameSettings.Options.SATURATION
                                          ? string2 + (int)(h * 400.0F) + "%"
                                          : (
                                             settingOption == GameSettings.Options.CHAT_OPACITY
                                                ? string2 + (int)(h * 90.0F + 10.0F) + "%"
                                                : (
                                                   settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                      ? string2 + GuiNewChat.calculateChatboxHeight(h) + "px"
                                                      : (
                                                         settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                            ? string2 + GuiNewChat.calculateChatboxHeight(h) + "px"
                                                            : (
                                                               settingOption == GameSettings.Options.CHAT_WIDTH
                                                                  ? string2 + GuiNewChat.calculateChatboxWidth(h) + "px"
                                                                  : (
                                                                     settingOption == GameSettings.Options.RENDER_DISTANCE
                                                                        ? string2 + (int)g + " chunks"
                                                                        : (
                                                                           settingOption == GameSettings.Options.MIPMAP_LEVELS
                                                                              ? (g == 0.0F ? string2 + I18n.format("options.off") : string2 + (int)g)
                                                                              : (
                                                                                 settingOption == GameSettings.Options.STREAM_FPS
                                                                                    ? string2 + this.getStreamFps(h) + " fps"
                                                                                    : (
                                                                                       settingOption == GameSettings.Options.STREAM_KBPS
                                                                                          ? string2 + this.getStreamKbps(h) + " Kbps"
                                                                                          : (
                                                                                             settingOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                ? string2
                                                                                                   + String.format(
                                                                                                      "%.3f bpp", this.getStreamBytesPerPixel(h)
                                                                                                   )
                                                                                                : (
                                                                                                   h == 0.0F
                                                                                                      ? string2 + I18n.format("options.off")
                                                                                                      : string2 + (int)(h * 100.0F) + "%"
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

      if (settingOption.getEnumBoolean()) {
         boolean bl = this.getOptionOrdinalValue(settingOption);
         return bl ? string2 + I18n.format("options.on") : string2 + I18n.format("options.off");
      }

      if (settingOption == GameSettings.Options.GUI_SCALE) {
         return string2 + getTranslation(GUISCALES, this.guiScale);
      }

      if (settingOption == GameSettings.Options.CHAT_VISIBILITY) {
         return string2 + I18n.format(this.chatVisibility.getResourceKey());
      }

      if (settingOption == GameSettings.Options.PARTICLES) {
         return string2 + getTranslation(PARTICLES, this.particleSetting);
      }

      if (settingOption == GameSettings.Options.AMBIENT_OCCLUSION) {
         return string2 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
      }

      if (settingOption == GameSettings.Options.STREAM_COMPRESSION) {
         return string2 + getTranslation(STREAM_COMPRESSIONS, this.streamCompression);
      }

      if (settingOption == GameSettings.Options.STREAM_CHAT_ENABLED) {
         return string2 + getTranslation(STREAM_CHAT_MODES, this.streamChatEnabled);
      }

      if (settingOption == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         return string2 + getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
      }

      if (settingOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         return string2 + getTranslation(STREAM_MIC_MODES, this.streamMicToggleBehavior);
      }

      if (settingOption == GameSettings.Options.RENDER_CLOUDS) {
         return string2 + getTranslation(CLOUDS_TYPES, this.clouds);
      }

      if (settingOption == GameSettings.Options.GRAPHICS) {
         if (this.fancyGraphics) {
            return string2 + I18n.format("options.graphics.fancy");
         }

         String string3 = "options.graphics.fast";
         return string2 + I18n.format("options.graphics.fast");
      } else {
         return string2;
      }
   }

   private int getStreamFps(float f) {
      return MathHelper.floor_float(10.0F + f * 50.0F);
   }

   private int getStreamKbps(float f) {
      return MathHelper.floor_float(230.0F + f * 3270.0F);
   }

   private float getStreamBytesPerPixel(float f) {
      return 0.1F + f * 0.1F;
   }

   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         BufferedReader bufferedReader = new BufferedReader(new FileReader(this.optionsFile));
         String string = "";
         this.mapSoundLevels.clear();

         while ((string = bufferedReader.readLine()) != null) {
            try {
               String[] strings = string.split(":");
               if (strings[0].equals("mouseSensitivity")) {
                  this.mouseSensitivity = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("fov")) {
                  this.fovSetting = this.parseFloat(strings[1]) * 40.0F + 70.0F;
               }

               if (strings[0].equals("gamma")) {
                  this.gammaSetting = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("saturation")) {
                  this.saturation = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("invertYMouse")) {
                  this.invertMouse = strings[1].equals("true");
               }

               if (strings[0].equals("renderDistance")) {
                  this.renderDistanceChunks = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("guiScale")) {
                  this.guiScale = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("particles")) {
                  this.particleSetting = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("bobView")) {
                  this.viewBobbing = strings[1].equals("true");
               }

               if (strings[0].equals("anaglyph3d")) {
                  this.anaglyph = strings[1].equals("true");
               }

               if (strings[0].equals("maxFps")) {
                  this.limitFramerate = Integer.parseInt(strings[1]);
                  this.enableVsync = false;
                  if (this.limitFramerate <= 0) {
                     this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                     this.enableVsync = true;
                  }

                  this.updateVSync();
               }

               if (strings[0].equals("fboEnable")) {
                  this.fboEnable = strings[1].equals("true");
               }

               if (strings[0].equals("difficulty")) {
                  this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(strings[1]));
               }

               if (strings[0].equals("fancyGraphics")) {
                  this.fancyGraphics = strings[1].equals("true");
                  this.updateRenderClouds();
               }

               if (strings[0].equals("ao")) {
                  if (strings[1].equals("true")) {
                     this.ambientOcclusion = 2;
                  } else if (strings[1].equals("false")) {
                     this.ambientOcclusion = 0;
                  } else {
                     this.ambientOcclusion = Integer.parseInt(strings[1]);
                  }
               }

               if (strings[0].equals("renderClouds")) {
                  if (strings[1].equals("true")) {
                     this.clouds = 2;
                  } else if (strings[1].equals("false")) {
                     this.clouds = 0;
                  } else if (strings[1].equals("fast")) {
                     this.clouds = 1;
                  }
               }

               if (strings[0].equals("resourcePacks")) {
                  this.resourcePacks = gson.fromJson(string.substring(string.indexOf(58) + 1), typeListString);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if (strings[0].equals("incompatibleResourcePacks")) {
                  this.incompatibleResourcePacks = gson.fromJson(string.substring(string.indexOf(58) + 1), typeListString);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if (strings[0].equals("lastServer") && strings.length >= 2) {
                  this.lastServer = string.substring(string.indexOf(58) + 1);
               }

               if (strings[0].equals("lang") && strings.length >= 2) {
                  this.language = strings[1];
               }

               if (strings[0].equals("chatVisibility")) {
                  this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(strings[1]));
               }

               if (strings[0].equals("chatColors")) {
                  this.chatColours = strings[1].equals("true");
               }

               if (strings[0].equals("chatLinks")) {
                  this.chatLinks = strings[1].equals("true");
               }

               if (strings[0].equals("chatLinksPrompt")) {
                  this.chatLinksPrompt = strings[1].equals("true");
               }

               if (strings[0].equals("chatOpacity")) {
                  this.chatOpacity = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("snooperEnabled")) {
                  this.snooperEnabled = strings[1].equals("true");
               }

               if (strings[0].equals("fullscreen")) {
                  this.fullScreen = strings[1].equals("true");
               }

               if (strings[0].equals("enableVsync")) {
                  this.enableVsync = strings[1].equals("true");
                  this.updateVSync();
               }

               if (strings[0].equals("useVbo")) {
                  this.useVbo = strings[1].equals("true");
               }

               if (strings[0].equals("hideServerAddress")) {
                  this.hideServerAddress = strings[1].equals("true");
               }

               if (strings[0].equals("advancedItemTooltips")) {
                  this.advancedItemTooltips = strings[1].equals("true");
               }

               if (strings[0].equals("pauseOnLostFocus")) {
                  this.pauseOnLostFocus = strings[1].equals("true");
               }

               if (strings[0].equals("touchscreen")) {
                  this.touchscreen = strings[1].equals("true");
               }

               if (strings[0].equals("overrideHeight")) {
                  this.overrideHeight = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("overrideWidth")) {
                  this.overrideWidth = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("heldItemTooltips")) {
                  this.heldItemTooltips = strings[1].equals("true");
               }

               if (strings[0].equals("chatHeightFocused")) {
                  this.chatHeightFocused = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("chatHeightUnfocused")) {
                  this.chatHeightUnfocused = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("chatScale")) {
                  this.chatScale = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("chatWidth")) {
                  this.chatWidth = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("showInventoryAchievementHint")) {
                  this.showInventoryAchievementHint = strings[1].equals("true");
               }

               if (strings[0].equals("mipmapLevels")) {
                  this.mipmapLevels = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("streamBytesPerPixel")) {
                  this.streamBytesPerPixel = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("streamMicVolume")) {
                  this.streamMicVolume = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("streamSystemVolume")) {
                  this.streamGameVolume = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("streamKbps")) {
                  this.streamKbps = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("streamFps")) {
                  this.streamFps = this.parseFloat(strings[1]);
               }

               if (strings[0].equals("streamCompression")) {
                  this.streamCompression = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("streamSendMetadata")) {
                  this.streamSendMetadata = strings[1].equals("true");
               }

               if (strings[0].equals("streamPreferredServer") && strings.length >= 2) {
                  this.streamPreferredServer = string.substring(string.indexOf(58) + 1);
               }

               if (strings[0].equals("streamChatEnabled")) {
                  this.streamChatEnabled = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("streamChatUserFilter")) {
                  this.streamChatUserFilter = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("streamMicToggleBehavior")) {
                  this.streamMicToggleBehavior = Integer.parseInt(strings[1]);
               }

               if (strings[0].equals("forceUnicodeFont")) {
                  this.forceUnicodeFont = strings[1].equals("true");
               }

               if (strings[0].equals("allowBlockAlternatives")) {
                  this.allowBlockAlternatives = strings[1].equals("true");
               }

               if (strings[0].equals("reducedDebugInfo")) {
                  this.reducedDebugInfo = strings[1].equals("true");
               }

               if (strings[0].equals("useNativeTransport")) {
                  this.useNativeTransport = strings[1].equals("true");
               }

               if (strings[0].equals("entityShadows")) {
                  this.entityShadows = strings[1].equals("true");
               }

               for (KeyBinding keyBinding : this.keyBindings) {
                  if (strings[0].equals("key_" + keyBinding.getKeyDescription())) {
                     keyBinding.setKeyCode(Integer.parseInt(strings[1]));
                  }
               }

               for (SoundCategory soundCategory : SoundCategory.values()) {
                  if (strings[0].equals("soundCategory_" + soundCategory.getCategoryName())) {
                     this.mapSoundLevels.put(soundCategory, this.parseFloat(strings[1]));
                  }
               }

               for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                  if (strings[0].equals("modelPart_" + enumPlayerModelParts.getPartName())) {
                     this.setModelPartEnabled(enumPlayerModelParts, strings[1].equals("true"));
                  }
               }
            } catch (Exception exception) {
               logger.warn("Skipping bad option: " + string);
               exception.printStackTrace();
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         bufferedReader.close();
      } catch (Exception exception2) {
         logger.error("Failed to load options", exception2);
      }

      this.loadOfOptions();
   }

   private float parseFloat(String str) {
      return str.equals("true") ? 1.0F : (str.equals("false") ? 0.0F : Float.parseFloat(str));
   }

   public void saveOptions() {
      if (Reflector.FMLClientHandler.exists()) {
         Object object = Reflector.call(Reflector.FMLClientHandler_instance);
         if (object != null && Reflector.callBoolean(object, Reflector.FMLClientHandler_isLoading)) {
            return;
         }
      }

      try {
         PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFile));
         printWriter.println("invertYMouse:" + this.invertMouse);
         printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printWriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
         printWriter.println("gamma:" + this.gammaSetting);
         printWriter.println("saturation:" + this.saturation);
         printWriter.println("renderDistance:" + this.renderDistanceChunks);
         printWriter.println("guiScale:" + this.guiScale);
         printWriter.println("particles:" + this.particleSetting);
         printWriter.println("bobView:" + this.viewBobbing);
         printWriter.println("anaglyph3d:" + this.anaglyph);
         printWriter.println("maxFps:" + this.limitFramerate);
         printWriter.println("fboEnable:" + this.fboEnable);
         printWriter.println("difficulty:" + this.difficulty.getDifficultyId());
         printWriter.println("fancyGraphics:" + this.fancyGraphics);
         printWriter.println("ao:" + this.ambientOcclusion);
         switch (this.clouds) {
            case 0:
               printWriter.println("renderClouds:false");
               break;
            case 1:
               printWriter.println("renderClouds:fast");
               break;
            case 2:
               printWriter.println("renderClouds:true");
         }

         printWriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
         printWriter.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
         printWriter.println("lastServer:" + this.lastServer);
         printWriter.println("lang:" + this.language);
         printWriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         printWriter.println("chatColors:" + this.chatColours);
         printWriter.println("chatLinks:" + this.chatLinks);
         printWriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
         printWriter.println("chatOpacity:" + this.chatOpacity);
         printWriter.println("snooperEnabled:" + this.snooperEnabled);
         printWriter.println("fullscreen:" + this.fullScreen);
         printWriter.println("enableVsync:" + this.enableVsync);
         printWriter.println("useVbo:" + this.useVbo);
         printWriter.println("hideServerAddress:" + this.hideServerAddress);
         printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printWriter.println("touchscreen:" + this.touchscreen);
         printWriter.println("overrideWidth:" + this.overrideWidth);
         printWriter.println("overrideHeight:" + this.overrideHeight);
         printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printWriter.println("chatScale:" + this.chatScale);
         printWriter.println("chatWidth:" + this.chatWidth);
         printWriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
         printWriter.println("mipmapLevels:" + this.mipmapLevels);
         printWriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
         printWriter.println("streamMicVolume:" + this.streamMicVolume);
         printWriter.println("streamSystemVolume:" + this.streamGameVolume);
         printWriter.println("streamKbps:" + this.streamKbps);
         printWriter.println("streamFps:" + this.streamFps);
         printWriter.println("streamCompression:" + this.streamCompression);
         printWriter.println("streamSendMetadata:" + this.streamSendMetadata);
         printWriter.println("streamPreferredServer:" + this.streamPreferredServer);
         printWriter.println("streamChatEnabled:" + this.streamChatEnabled);
         printWriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
         printWriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         printWriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
         printWriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
         printWriter.println("useNativeTransport:" + this.useNativeTransport);
         printWriter.println("entityShadows:" + this.entityShadows);

         for (KeyBinding keyBinding : this.keyBindings) {
            printWriter.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode());
         }

         for (SoundCategory soundCategory : SoundCategory.values()) {
            printWriter.println("soundCategory_" + soundCategory.getCategoryName() + ":" + this.getSoundLevel(soundCategory));
         }

         for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
            printWriter.println("modelPart_" + enumPlayerModelParts.getPartName() + ":" + this.setModelParts.contains(enumPlayerModelParts));
         }

         printWriter.close();
      } catch (Exception exception) {
         logger.error("Failed to save options", exception);
      }

      this.saveOfOptions();
      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory sndCategory) {
      return this.mapSoundLevels.containsKey(sndCategory) ? this.mapSoundLevels.get(sndCategory) : 1.0F;
   }

   public void setSoundLevel(SoundCategory sndCategory, float soundLevel) {
      this.mc.getSoundHandler().setSoundLevel(sndCategory, soundLevel);
      this.mapSoundLevels.put(sndCategory, soundLevel);
   }

   public void sendSettingsToServer() {
      if (this.mc.thePlayer != null) {
         int i = 0;

         for (Object object : this.setModelParts) {
            i |= ((EnumPlayerModelParts)object).getPartMask();
         }

         this.mc
            .thePlayer
            .sendQueue
            .addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i));
      }
   }

   public Set<EnumPlayerModelParts> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable) {
      if (enable) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(EnumPlayerModelParts modelPart) {
      if (!this.getModelParts().contains(modelPart)) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public int shouldRenderClouds() {
      return this.renderDistanceChunks >= 4 ? this.clouds : 0;
   }

   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   private void setOptionFloatValueOF(GameSettings.Options options, float f) {
      if (options == GameSettings.Options.CLOUD_HEIGHT) {
         this.ofCloudsHeight = f;
         this.mc.renderGlobal.resetClouds();
      }

      if (options == GameSettings.Options.AO_LEVEL) {
         this.ofAoLevel = f;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.AA_LEVEL) {
         int i = (int)f;
         if (i > 0 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
            return;
         }

         int[] ints = new int[]{0, 2, 4, 6, 8, 12, 16};
         this.ofAaLevel = 0;

         for (int j = 0; j < ints.length; j++) {
            if (i >= ints[j]) {
               this.ofAaLevel = ints[j];
            }
         }

         this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
      }

      if (options == GameSettings.Options.AF_LEVEL) {
         int k = (int)f;
         if (k > 1 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
            return;
         }

         this.ofAfLevel = 1;

         while (this.ofAfLevel * 2 <= k) {
            this.ofAfLevel *= 2;
         }

         this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.MIPMAP_TYPE) {
         int l = (int)f;
         this.ofMipmapType = Config.limit(l, 0, 3);
         this.mc.refreshResources();
      }
   }

   private void setOptionValueOF(GameSettings.Options options, int i) {
      if (options == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               this.ofFogType = 2;
               if (!Config.isFancyFogAvailable()) {
                  this.ofFogType = 3;
               }
               break;
            case 2:
               this.ofFogType = 3;
               break;
            case 3:
               this.ofFogType = 1;
               break;
            default:
               this.ofFogType = 1;
         }
      }

      if (options == GameSettings.Options.FOG_START) {
         this.ofFogStart += 0.2F;
         if (this.ofFogStart > 0.81F) {
            this.ofFogStart = 0.2F;
         }
      }

      if (options == GameSettings.Options.SMOOTH_FPS) {
         this.ofSmoothFps = !this.ofSmoothFps;
      }

      if (options == GameSettings.Options.SMOOTH_WORLD) {
         this.ofSmoothWorld = !this.ofSmoothWorld;
         Config.updateThreadPriorities();
      }

      if (options == GameSettings.Options.CLOUDS) {
         this.ofClouds++;
         if (this.ofClouds > 3) {
            this.ofClouds = 0;
         }

         this.updateRenderClouds();
         this.mc.renderGlobal.resetClouds();
      }

      if (options == GameSettings.Options.TREES) {
         this.ofTrees = nextValue(this.ofTrees, OF_TREES_VALUES);
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.DROPPED_ITEMS) {
         this.ofDroppedItems++;
         if (this.ofDroppedItems > 2) {
            this.ofDroppedItems = 0;
         }
      }

      if (options == GameSettings.Options.RAIN) {
         this.ofRain++;
         if (this.ofRain > 3) {
            this.ofRain = 0;
         }
      }

      if (options == GameSettings.Options.ANIMATED_WATER) {
         this.ofAnimatedWater++;
         if (this.ofAnimatedWater == 1) {
            this.ofAnimatedWater++;
         }

         if (this.ofAnimatedWater > 2) {
            this.ofAnimatedWater = 0;
         }
      }

      if (options == GameSettings.Options.ANIMATED_LAVA) {
         this.ofAnimatedLava++;
         if (this.ofAnimatedLava == 1) {
            this.ofAnimatedLava++;
         }

         if (this.ofAnimatedLava > 2) {
            this.ofAnimatedLava = 0;
         }
      }

      if (options == GameSettings.Options.ANIMATED_FIRE) {
         this.ofAnimatedFire = !this.ofAnimatedFire;
      }

      if (options == GameSettings.Options.ANIMATED_PORTAL) {
         this.ofAnimatedPortal = !this.ofAnimatedPortal;
      }

      if (options == GameSettings.Options.ANIMATED_REDSTONE) {
         this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
      }

      if (options == GameSettings.Options.ANIMATED_EXPLOSION) {
         this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
      }

      if (options == GameSettings.Options.ANIMATED_FLAME) {
         this.ofAnimatedFlame = !this.ofAnimatedFlame;
      }

      if (options == GameSettings.Options.ANIMATED_SMOKE) {
         this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
      }

      if (options == GameSettings.Options.VOID_PARTICLES) {
         this.ofVoidParticles = !this.ofVoidParticles;
      }

      if (options == GameSettings.Options.WATER_PARTICLES) {
         this.ofWaterParticles = !this.ofWaterParticles;
      }

      if (options == GameSettings.Options.PORTAL_PARTICLES) {
         this.ofPortalParticles = !this.ofPortalParticles;
      }

      if (options == GameSettings.Options.POTION_PARTICLES) {
         this.ofPotionParticles = !this.ofPotionParticles;
      }

      if (options == GameSettings.Options.FIREWORK_PARTICLES) {
         this.ofFireworkParticles = !this.ofFireworkParticles;
      }

      if (options == GameSettings.Options.DRIPPING_WATER_LAVA) {
         this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
      }

      if (options == GameSettings.Options.ANIMATED_TERRAIN) {
         this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
      }

      if (options == GameSettings.Options.ANIMATED_TEXTURES) {
         this.ofAnimatedTextures = !this.ofAnimatedTextures;
      }

      if (options == GameSettings.Options.RAIN_SPLASH) {
         this.ofRainSplash = !this.ofRainSplash;
      }

      if (options == GameSettings.Options.LAGOMETER) {
         this.ofLagometer = !this.ofLagometer;
      }

      if (options == GameSettings.Options.SHOW_FPS) {
         this.ofShowFps = !this.ofShowFps;
      }

      if (options == GameSettings.Options.AUTOSAVE_TICKS) {
         this.ofAutoSaveTicks *= 10;
         if (this.ofAutoSaveTicks > 40000) {
            this.ofAutoSaveTicks = 40;
         }
      }

      if (options == GameSettings.Options.BETTER_GRASS) {
         this.ofBetterGrass++;
         if (this.ofBetterGrass > 3) {
            this.ofBetterGrass = 1;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.CONNECTED_TEXTURES) {
         this.ofConnectedTextures++;
         if (this.ofConnectedTextures > 3) {
            this.ofConnectedTextures = 1;
         }

         if (this.ofConnectedTextures != 2) {
            this.mc.refreshResources();
         }
      }

      if (options == GameSettings.Options.WEATHER) {
         this.ofWeather = !this.ofWeather;
      }

      if (options == GameSettings.Options.SKY) {
         this.ofSky = !this.ofSky;
      }

      if (options == GameSettings.Options.STARS) {
         this.ofStars = !this.ofStars;
      }

      if (options == GameSettings.Options.SUN_MOON) {
         this.ofSunMoon = !this.ofSunMoon;
      }

      if (options == GameSettings.Options.VIGNETTE) {
         this.ofVignette++;
         if (this.ofVignette > 2) {
            this.ofVignette = 0;
         }
      }

      if (options == GameSettings.Options.CHUNK_UPDATES) {
         this.ofChunkUpdates++;
         if (this.ofChunkUpdates > 5) {
            this.ofChunkUpdates = 1;
         }
      }

      if (options == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
         this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
      }

      if (options == GameSettings.Options.TIME) {
         this.ofTime++;
         if (this.ofTime > 2) {
            this.ofTime = 0;
         }
      }

      if (options == GameSettings.Options.CLEAR_WATER) {
         this.ofClearWater = !this.ofClearWater;
         this.updateWaterOpacity();
      }

      if (options == GameSettings.Options.PROFILER) {
         this.ofProfiler = !this.ofProfiler;
      }

      if (options == GameSettings.Options.BETTER_SNOW) {
         this.ofBetterSnow = !this.ofBetterSnow;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.SWAMP_COLORS) {
         this.ofSwampColors = !this.ofSwampColors;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.RANDOM_MOBS) {
         this.ofRandomMobs = !this.ofRandomMobs;
         RandomMobs.resetTextures();
      }

      if (options == GameSettings.Options.SMOOTH_BIOMES) {
         this.ofSmoothBiomes = !this.ofSmoothBiomes;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.CUSTOM_FONTS) {
         this.ofCustomFonts = !this.ofCustomFonts;
         this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
         this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
      }

      if (options == GameSettings.Options.CUSTOM_COLORS) {
         this.ofCustomColors = !this.ofCustomColors;
         CustomColors.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.CUSTOM_ITEMS) {
         this.ofCustomItems = !this.ofCustomItems;
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.CUSTOM_SKY) {
         this.ofCustomSky = !this.ofCustomSky;
         CustomSky.update();
      }

      if (options == GameSettings.Options.SHOW_CAPES) {
         this.ofShowCapes = !this.ofShowCapes;
      }

      if (options == GameSettings.Options.NATURAL_TEXTURES) {
         this.ofNaturalTextures = !this.ofNaturalTextures;
         NaturalTextures.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.FAST_MATH) {
         this.ofFastMath = !this.ofFastMath;
         MathHelper.fastMath = this.ofFastMath;
      }

      if (options == GameSettings.Options.FAST_RENDER) {
         if (!this.ofFastRender && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
            return;
         }

         this.ofFastRender = !this.ofFastRender;
         if (this.ofFastRender) {
            this.mc.entityRenderer.stopUseShader();
         }

         Config.updateFramebufferSize();
      }

      if (options == GameSettings.Options.TRANSLUCENT_BLOCKS) {
         if (this.ofTranslucentBlocks == 0) {
            this.ofTranslucentBlocks = 1;
         } else if (this.ofTranslucentBlocks == 1) {
            this.ofTranslucentBlocks = 2;
         } else if (this.ofTranslucentBlocks == 2) {
            this.ofTranslucentBlocks = 0;
         } else {
            this.ofTranslucentBlocks = 0;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.LAZY_CHUNK_LOADING) {
         this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
         Config.updateAvailableProcessors();
         if (!Config.isSingleProcessor()) {
            this.ofLazyChunkLoading = false;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.FULLSCREEN_MODE) {
         List list = Arrays.asList(Config.getDisplayModeNames());
         if (this.ofFullscreenMode.equals("Default")) {
            this.ofFullscreenMode = (String)list.get(0);
         } else {
            int j = list.indexOf(this.ofFullscreenMode);
            if (j < 0) {
               this.ofFullscreenMode = "Default";
            } else if (++j >= list.size()) {
               this.ofFullscreenMode = "Default";
            } else {
               this.ofFullscreenMode = (String)list.get(j);
            }
         }
      }

      if (options == GameSettings.Options.DYNAMIC_FOV) {
         this.ofDynamicFov = !this.ofDynamicFov;
      }

      if (options == GameSettings.Options.DYNAMIC_LIGHTS) {
         this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
         DynamicLights.removeLights(this.mc.renderGlobal);
      }

      if (options == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
         this.heldItemTooltips = !this.heldItemTooltips;
      }
   }

   private String getKeyBindingOF(GameSettings.Options options) {
      String string = I18n.format(options.getEnumString()) + ": ";
      if (string == null) {
         string = options.getEnumString();
      }

      if (options == GameSettings.Options.RENDER_DISTANCE) {
         int i = (int)this.getOptionFloatValue(options);
         String string2 = I18n.format("options.renderDistance.tiny");
         byte b = 2;
         if (i >= 4) {
            string2 = I18n.format("options.renderDistance.short");
            b = 4;
         }

         if (i >= 8) {
            string2 = I18n.format("options.renderDistance.normal");
            b = 8;
         }

         if (i >= 16) {
            string2 = I18n.format("options.renderDistance.far");
            b = 16;
         }

         if (i >= 32) {
            string2 = Lang.get("of.options.renderDistance.extreme");
            b = 32;
         }

         int j = this.renderDistanceChunks - b;
         String string3 = string2;
         if (j > 0) {
            string3 = string2 + "+";
         }

         return string + i + " " + string3 + "";
      } else if (options == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               return string + Lang.getFast();
            case 2:
               return string + Lang.getFancy();
            case 3:
               return string + Lang.getOff();
            default:
               return string + Lang.getOff();
         }
      } else {
         if (options == GameSettings.Options.FOG_START) {
            return string + this.ofFogStart;
         }

         if (options == GameSettings.Options.MIPMAP_TYPE) {
            switch (this.ofMipmapType) {
               case 0:
                  return string + Lang.get("of.options.mipmap.nearest");
               case 1:
                  return string + Lang.get("of.options.mipmap.linear");
               case 2:
                  return string + Lang.get("of.options.mipmap.bilinear");
               case 3:
                  return string + Lang.get("of.options.mipmap.trilinear");
               default:
                  return string + "of.options.mipmap.nearest";
            }
         } else {
            if (options == GameSettings.Options.SMOOTH_FPS) {
               return this.ofSmoothFps ? string + Lang.getOn() : string + Lang.getOff();
            }

            if (options == GameSettings.Options.SMOOTH_WORLD) {
               return this.ofSmoothWorld ? string + Lang.getOn() : string + Lang.getOff();
            }

            if (options == GameSettings.Options.CLOUDS) {
               switch (this.ofClouds) {
                  case 1:
                     return string + Lang.getFast();
                  case 2:
                     return string + Lang.getFancy();
                  case 3:
                     return string + Lang.getOff();
                  default:
                     return string + Lang.getDefault();
               }
            } else if (options == GameSettings.Options.TREES) {
               switch (this.ofTrees) {
                  case 1:
                     return string + Lang.getFast();
                  case 2:
                     return string + Lang.getFancy();
                  case 3:
                  default:
                     return string + Lang.getDefault();
                  case 4:
                     return string + Lang.get("of.general.smart");
               }
            } else if (options == GameSettings.Options.DROPPED_ITEMS) {
               switch (this.ofDroppedItems) {
                  case 1:
                     return string + Lang.getFast();
                  case 2:
                     return string + Lang.getFancy();
                  default:
                     return string + Lang.getDefault();
               }
            } else if (options == GameSettings.Options.RAIN) {
               switch (this.ofRain) {
                  case 1:
                     return string + Lang.getFast();
                  case 2:
                     return string + Lang.getFancy();
                  case 3:
                     return string + Lang.getOff();
                  default:
                     return string + Lang.getDefault();
               }
            } else if (options == GameSettings.Options.ANIMATED_WATER) {
               switch (this.ofAnimatedWater) {
                  case 1:
                     return string + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return string + Lang.getOff();
                  default:
                     return string + Lang.getOn();
               }
            } else if (options == GameSettings.Options.ANIMATED_LAVA) {
               switch (this.ofAnimatedLava) {
                  case 1:
                     return string + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return string + Lang.getOff();
                  default:
                     return string + Lang.getOn();
               }
            } else {
               if (options == GameSettings.Options.ANIMATED_FIRE) {
                  return this.ofAnimatedFire ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_PORTAL) {
                  return this.ofAnimatedPortal ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_REDSTONE) {
                  return this.ofAnimatedRedstone ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_EXPLOSION) {
                  return this.ofAnimatedExplosion ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_FLAME) {
                  return this.ofAnimatedFlame ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_SMOKE) {
                  return this.ofAnimatedSmoke ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.VOID_PARTICLES) {
                  return this.ofVoidParticles ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.WATER_PARTICLES) {
                  return this.ofWaterParticles ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.PORTAL_PARTICLES) {
                  return this.ofPortalParticles ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.POTION_PARTICLES) {
                  return this.ofPotionParticles ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.FIREWORK_PARTICLES) {
                  return this.ofFireworkParticles ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.DRIPPING_WATER_LAVA) {
                  return this.ofDrippingWaterLava ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_TERRAIN) {
                  return this.ofAnimatedTerrain ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_TEXTURES) {
                  return this.ofAnimatedTextures ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.RAIN_SPLASH) {
                  return this.ofRainSplash ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.LAGOMETER) {
                  return this.ofLagometer ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.SHOW_FPS) {
                  return this.ofShowFps ? string + Lang.getOn() : string + Lang.getOff();
               }

               if (options == GameSettings.Options.AUTOSAVE_TICKS) {
                  return this.ofAutoSaveTicks <= 40
                     ? string + Lang.get("of.options.save.default")
                     : (
                        this.ofAutoSaveTicks <= 400
                           ? string + Lang.get("of.options.save.20s")
                           : (this.ofAutoSaveTicks <= 4000 ? string + Lang.get("of.options.save.3min") : string + Lang.get("of.options.save.30min"))
                     );
               }

               if (options == GameSettings.Options.BETTER_GRASS) {
                  switch (this.ofBetterGrass) {
                     case 1:
                        return string + Lang.getFast();
                     case 2:
                        return string + Lang.getFancy();
                     default:
                        return string + Lang.getOff();
                  }
               } else if (options == GameSettings.Options.CONNECTED_TEXTURES) {
                  switch (this.ofConnectedTextures) {
                     case 1:
                        return string + Lang.getFast();
                     case 2:
                        return string + Lang.getFancy();
                     default:
                        return string + Lang.getOff();
                  }
               } else {
                  if (options == GameSettings.Options.WEATHER) {
                     return this.ofWeather ? string + Lang.getOn() : string + Lang.getOff();
                  }

                  if (options == GameSettings.Options.SKY) {
                     return this.ofSky ? string + Lang.getOn() : string + Lang.getOff();
                  }

                  if (options == GameSettings.Options.STARS) {
                     return this.ofStars ? string + Lang.getOn() : string + Lang.getOff();
                  }

                  if (options == GameSettings.Options.SUN_MOON) {
                     return this.ofSunMoon ? string + Lang.getOn() : string + Lang.getOff();
                  }

                  if (options == GameSettings.Options.VIGNETTE) {
                     switch (this.ofVignette) {
                        case 1:
                           return string + Lang.getFast();
                        case 2:
                           return string + Lang.getFancy();
                        default:
                           return string + Lang.getDefault();
                     }
                  } else {
                     if (options == GameSettings.Options.CHUNK_UPDATES) {
                        return string + this.ofChunkUpdates;
                     }

                     if (options == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
                        return this.ofChunkUpdatesDynamic ? string + Lang.getOn() : string + Lang.getOff();
                     }

                     if (options == GameSettings.Options.TIME) {
                        return this.ofTime == 1
                           ? string + Lang.get("of.options.time.dayOnly")
                           : (this.ofTime == 2 ? string + Lang.get("of.options.time.nightOnly") : string + Lang.getDefault());
                     }

                     if (options == GameSettings.Options.CLEAR_WATER) {
                        return this.ofClearWater ? string + Lang.getOn() : string + Lang.getOff();
                     }

                     if (options == GameSettings.Options.AA_LEVEL) {
                        String string4 = "";
                        if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                           string4 = " (" + Lang.get("of.general.restart") + ")";
                        }

                        return this.ofAaLevel == 0 ? string + Lang.getOff() + string4 : string + this.ofAaLevel + string4;
                     } else if (options == GameSettings.Options.AF_LEVEL) {
                        return this.ofAfLevel == 1 ? string + Lang.getOff() : string + this.ofAfLevel;
                     } else if (options == GameSettings.Options.PROFILER) {
                        return this.ofProfiler ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.BETTER_SNOW) {
                        return this.ofBetterSnow ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.SWAMP_COLORS) {
                        return this.ofSwampColors ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.RANDOM_MOBS) {
                        return this.ofRandomMobs ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.SMOOTH_BIOMES) {
                        return this.ofSmoothBiomes ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_FONTS) {
                        return this.ofCustomFonts ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_COLORS) {
                        return this.ofCustomColors ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_SKY) {
                        return this.ofCustomSky ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.SHOW_CAPES) {
                        return this.ofShowCapes ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_ITEMS) {
                        return this.ofCustomItems ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.NATURAL_TEXTURES) {
                        return this.ofNaturalTextures ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.FAST_MATH) {
                        return this.ofFastMath ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.FAST_RENDER) {
                        return this.ofFastRender ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.TRANSLUCENT_BLOCKS) {
                        return this.ofTranslucentBlocks == 1
                           ? string + Lang.getFast()
                           : (this.ofTranslucentBlocks == 2 ? string + Lang.getFancy() : string + Lang.getDefault());
                     } else if (options == GameSettings.Options.LAZY_CHUNK_LOADING) {
                        return this.ofLazyChunkLoading ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.DYNAMIC_FOV) {
                        return this.ofDynamicFov ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.DYNAMIC_LIGHTS) {
                        int k = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                        return string + getTranslation(KEYS_DYNAMIC_LIGHTS, k);
                     } else if (options == GameSettings.Options.FULLSCREEN_MODE) {
                        return this.ofFullscreenMode.equals("Default") ? string + Lang.getDefault() : string + this.ofFullscreenMode;
                     } else if (options == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
                        return this.heldItemTooltips ? string + Lang.getOn() : string + Lang.getOff();
                     } else if (options == GameSettings.Options.FRAMERATE_LIMIT) {
                        float f = this.getOptionFloatValue(options);
                        return f == 0.0F
                           ? string + Lang.get("of.options.framerateLimit.vsync")
                           : (
                              f == GameSettings.Options.accessValueMax(options)
                                 ? string + I18n.format("options.framerateLimit.max")
                                 : string + (int)f + " fps"
                           );
                     } else {
                        return null;
                     }
                  }
               }
            }
         }
      }
   }

   public void loadOfOptions() {
      try {
         File file = this.optionsFileOF;
         if (!file.exists()) {
            file = this.optionsFile;
         }

         if (!file.exists()) {
            return;
         }

         BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
         String string = "";

         while ((string = bufferedReader.readLine()) != null) {
            try {
               String[] strings = string.split(":");
               if (strings[0].equals("ofRenderDistanceChunks") && strings.length >= 2) {
                  this.renderDistanceChunks = Integer.valueOf(strings[1]);
                  this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 32);
               }

               if (strings[0].equals("ofFogType") && strings.length >= 2) {
                  this.ofFogType = Integer.valueOf(strings[1]);
                  this.ofFogType = Config.limit(this.ofFogType, 1, 3);
               }

               if (strings[0].equals("ofFogStart") && strings.length >= 2) {
                  this.ofFogStart = Float.valueOf(strings[1]);
                  if (this.ofFogStart < 0.2F) {
                     this.ofFogStart = 0.2F;
                  }

                  if (this.ofFogStart > 0.81F) {
                     this.ofFogStart = 0.8F;
                  }
               }

               if (strings[0].equals("ofMipmapType") && strings.length >= 2) {
                  this.ofMipmapType = Integer.valueOf(strings[1]);
                  this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
               }

               if (strings[0].equals("ofOcclusionFancy") && strings.length >= 2) {
                  this.ofOcclusionFancy = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofSmoothFps") && strings.length >= 2) {
                  this.ofSmoothFps = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofSmoothWorld") && strings.length >= 2) {
                  this.ofSmoothWorld = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAoLevel") && strings.length >= 2) {
                  this.ofAoLevel = Float.valueOf(strings[1]);
                  this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
               }

               if (strings[0].equals("ofClouds") && strings.length >= 2) {
                  this.ofClouds = Integer.valueOf(strings[1]);
                  this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                  this.updateRenderClouds();
               }

               if (strings[0].equals("ofCloudsHeight") && strings.length >= 2) {
                  this.ofCloudsHeight = Float.valueOf(strings[1]);
                  this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
               }

               if (strings[0].equals("ofTrees") && strings.length >= 2) {
                  this.ofTrees = Integer.valueOf(strings[1]);
                  this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
               }

               if (strings[0].equals("ofDroppedItems") && strings.length >= 2) {
                  this.ofDroppedItems = Integer.valueOf(strings[1]);
                  this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
               }

               if (strings[0].equals("ofRain") && strings.length >= 2) {
                  this.ofRain = Integer.valueOf(strings[1]);
                  this.ofRain = Config.limit(this.ofRain, 0, 3);
               }

               if (strings[0].equals("ofAnimatedWater") && strings.length >= 2) {
                  this.ofAnimatedWater = Integer.valueOf(strings[1]);
                  this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
               }

               if (strings[0].equals("ofAnimatedLava") && strings.length >= 2) {
                  this.ofAnimatedLava = Integer.valueOf(strings[1]);
                  this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
               }

               if (strings[0].equals("ofAnimatedFire") && strings.length >= 2) {
                  this.ofAnimatedFire = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedPortal") && strings.length >= 2) {
                  this.ofAnimatedPortal = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedRedstone") && strings.length >= 2) {
                  this.ofAnimatedRedstone = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedExplosion") && strings.length >= 2) {
                  this.ofAnimatedExplosion = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedFlame") && strings.length >= 2) {
                  this.ofAnimatedFlame = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedSmoke") && strings.length >= 2) {
                  this.ofAnimatedSmoke = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofVoidParticles") && strings.length >= 2) {
                  this.ofVoidParticles = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofWaterParticles") && strings.length >= 2) {
                  this.ofWaterParticles = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofPortalParticles") && strings.length >= 2) {
                  this.ofPortalParticles = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofPotionParticles") && strings.length >= 2) {
                  this.ofPotionParticles = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofFireworkParticles") && strings.length >= 2) {
                  this.ofFireworkParticles = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofDrippingWaterLava") && strings.length >= 2) {
                  this.ofDrippingWaterLava = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedTerrain") && strings.length >= 2) {
                  this.ofAnimatedTerrain = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAnimatedTextures") && strings.length >= 2) {
                  this.ofAnimatedTextures = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofRainSplash") && strings.length >= 2) {
                  this.ofRainSplash = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofLagometer") && strings.length >= 2) {
                  this.ofLagometer = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofShowFps") && strings.length >= 2) {
                  this.ofShowFps = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofAutoSaveTicks") && strings.length >= 2) {
                  this.ofAutoSaveTicks = Integer.valueOf(strings[1]);
                  this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
               }

               if (strings[0].equals("ofBetterGrass") && strings.length >= 2) {
                  this.ofBetterGrass = Integer.valueOf(strings[1]);
                  this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
               }

               if (strings[0].equals("ofConnectedTextures") && strings.length >= 2) {
                  this.ofConnectedTextures = Integer.valueOf(strings[1]);
                  this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
               }

               if (strings[0].equals("ofWeather") && strings.length >= 2) {
                  this.ofWeather = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofSky") && strings.length >= 2) {
                  this.ofSky = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofStars") && strings.length >= 2) {
                  this.ofStars = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofSunMoon") && strings.length >= 2) {
                  this.ofSunMoon = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofVignette") && strings.length >= 2) {
                  this.ofVignette = Integer.valueOf(strings[1]);
                  this.ofVignette = Config.limit(this.ofVignette, 0, 2);
               }

               if (strings[0].equals("ofChunkUpdates") && strings.length >= 2) {
                  this.ofChunkUpdates = Integer.valueOf(strings[1]);
                  this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
               }

               if (strings[0].equals("ofChunkUpdatesDynamic") && strings.length >= 2) {
                  this.ofChunkUpdatesDynamic = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofTime") && strings.length >= 2) {
                  this.ofTime = Integer.valueOf(strings[1]);
                  this.ofTime = Config.limit(this.ofTime, 0, 2);
               }

               if (strings[0].equals("ofClearWater") && strings.length >= 2) {
                  this.ofClearWater = Boolean.valueOf(strings[1]);
                  this.updateWaterOpacity();
               }

               if (strings[0].equals("ofAaLevel") && strings.length >= 2) {
                  this.ofAaLevel = Integer.valueOf(strings[1]);
                  this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
               }

               if (strings[0].equals("ofAfLevel") && strings.length >= 2) {
                  this.ofAfLevel = Integer.valueOf(strings[1]);
                  this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
               }

               if (strings[0].equals("ofProfiler") && strings.length >= 2) {
                  this.ofProfiler = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofBetterSnow") && strings.length >= 2) {
                  this.ofBetterSnow = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofSwampColors") && strings.length >= 2) {
                  this.ofSwampColors = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofRandomMobs") && strings.length >= 2) {
                  this.ofRandomMobs = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofSmoothBiomes") && strings.length >= 2) {
                  this.ofSmoothBiomes = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofCustomFonts") && strings.length >= 2) {
                  this.ofCustomFonts = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofCustomColors") && strings.length >= 2) {
                  this.ofCustomColors = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofCustomItems") && strings.length >= 2) {
                  this.ofCustomItems = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofCustomSky") && strings.length >= 2) {
                  this.ofCustomSky = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofShowCapes") && strings.length >= 2) {
                  this.ofShowCapes = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofNaturalTextures") && strings.length >= 2) {
                  this.ofNaturalTextures = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofLazyChunkLoading") && strings.length >= 2) {
                  this.ofLazyChunkLoading = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofDynamicFov") && strings.length >= 2) {
                  this.ofDynamicFov = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofDynamicLights") && strings.length >= 2) {
                  this.ofDynamicLights = Integer.valueOf(strings[1]);
                  this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
               }

               if (strings[0].equals("ofFullscreenMode") && strings.length >= 2) {
                  this.ofFullscreenMode = strings[1];
               }

               if (strings[0].equals("ofFastMath") && strings.length >= 2) {
                  this.ofFastMath = Boolean.valueOf(strings[1]);
                  MathHelper.fastMath = this.ofFastMath;
               }

               if (strings[0].equals("ofFastRender") && strings.length >= 2) {
                  this.ofFastRender = Boolean.valueOf(strings[1]);
               }

               if (strings[0].equals("ofTranslucentBlocks") && strings.length >= 2) {
                  this.ofTranslucentBlocks = Integer.valueOf(strings[1]);
                  this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
               }

               if (strings[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) {
                  this.ofKeyBindZoom.setKeyCode(Integer.parseInt(strings[1]));
               }
            } catch (Exception exception) {
               Config.dbg("Skipping bad option: " + string);
               exception.printStackTrace();
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         bufferedReader.close();
      } catch (Exception exception2) {
         Config.warn("Failed to load options");
         exception2.printStackTrace();
      }
   }

   public void saveOfOptions() {
      try {
         PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFileOF));
         printWriter.println("ofRenderDistanceChunks:" + this.renderDistanceChunks);
         printWriter.println("ofFogType:" + this.ofFogType);
         printWriter.println("ofFogStart:" + this.ofFogStart);
         printWriter.println("ofMipmapType:" + this.ofMipmapType);
         printWriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
         printWriter.println("ofSmoothFps:" + this.ofSmoothFps);
         printWriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
         printWriter.println("ofAoLevel:" + this.ofAoLevel);
         printWriter.println("ofClouds:" + this.ofClouds);
         printWriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
         printWriter.println("ofTrees:" + this.ofTrees);
         printWriter.println("ofDroppedItems:" + this.ofDroppedItems);
         printWriter.println("ofRain:" + this.ofRain);
         printWriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
         printWriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
         printWriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
         printWriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
         printWriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
         printWriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
         printWriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
         printWriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
         printWriter.println("ofVoidParticles:" + this.ofVoidParticles);
         printWriter.println("ofWaterParticles:" + this.ofWaterParticles);
         printWriter.println("ofPortalParticles:" + this.ofPortalParticles);
         printWriter.println("ofPotionParticles:" + this.ofPotionParticles);
         printWriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
         printWriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
         printWriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
         printWriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
         printWriter.println("ofRainSplash:" + this.ofRainSplash);
         printWriter.println("ofLagometer:" + this.ofLagometer);
         printWriter.println("ofShowFps:" + this.ofShowFps);
         printWriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
         printWriter.println("ofBetterGrass:" + this.ofBetterGrass);
         printWriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
         printWriter.println("ofWeather:" + this.ofWeather);
         printWriter.println("ofSky:" + this.ofSky);
         printWriter.println("ofStars:" + this.ofStars);
         printWriter.println("ofSunMoon:" + this.ofSunMoon);
         printWriter.println("ofVignette:" + this.ofVignette);
         printWriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
         printWriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
         printWriter.println("ofTime:" + this.ofTime);
         printWriter.println("ofClearWater:" + this.ofClearWater);
         printWriter.println("ofAaLevel:" + this.ofAaLevel);
         printWriter.println("ofAfLevel:" + this.ofAfLevel);
         printWriter.println("ofProfiler:" + this.ofProfiler);
         printWriter.println("ofBetterSnow:" + this.ofBetterSnow);
         printWriter.println("ofSwampColors:" + this.ofSwampColors);
         printWriter.println("ofRandomMobs:" + this.ofRandomMobs);
         printWriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
         printWriter.println("ofCustomFonts:" + this.ofCustomFonts);
         printWriter.println("ofCustomColors:" + this.ofCustomColors);
         printWriter.println("ofCustomItems:" + this.ofCustomItems);
         printWriter.println("ofCustomSky:" + this.ofCustomSky);
         printWriter.println("ofShowCapes:" + this.ofShowCapes);
         printWriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
         printWriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
         printWriter.println("ofDynamicFov:" + this.ofDynamicFov);
         printWriter.println("ofDynamicLights:" + this.ofDynamicLights);
         printWriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
         printWriter.println("ofFastMath:" + this.ofFastMath);
         printWriter.println("ofFastRender:" + this.ofFastRender);
         printWriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
         printWriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
         printWriter.close();
      } catch (Exception exception) {
         Config.warn("Failed to save options");
         exception.printStackTrace();
      }
   }

   private void updateRenderClouds() {
      switch (this.ofClouds) {
         case 1:
            this.clouds = 1;
            break;
         case 2:
            this.clouds = 2;
            break;
         case 3:
            this.clouds = 0;
            break;
         default:
            if (this.fancyGraphics) {
               this.clouds = 2;
            } else {
               this.clouds = 1;
            }
      }
   }

   public void resetSettings() {
      this.renderDistanceChunks = 8;
      this.viewBobbing = true;
      this.anaglyph = false;
      this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.enableVsync = false;
      this.updateVSync();
      this.mipmapLevels = 4;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.clouds = 2;
      this.fovSetting = 70.0F;
      this.gammaSetting = 0.0F;
      this.guiScale = 0;
      this.particleSetting = 0;
      this.heldItemTooltips = true;
      this.useVbo = false;
      this.allowBlockAlternatives = true;
      this.forceUnicodeFont = false;
      this.ofFogType = 1;
      this.ofFogStart = 0.8F;
      this.ofMipmapType = 0;
      this.ofOcclusionFancy = false;
      this.ofSmoothFps = false;
      Config.updateAvailableProcessors();
      this.ofSmoothWorld = Config.isSingleProcessor();
      this.ofLazyChunkLoading = Config.isSingleProcessor();
      this.ofFastMath = false;
      this.ofFastRender = false;
      this.ofTranslucentBlocks = 0;
      this.ofDynamicFov = true;
      this.ofDynamicLights = 3;
      this.ofAoLevel = 1.0F;
      this.ofAaLevel = 0;
      this.ofAfLevel = 1;
      this.ofClouds = 0;
      this.ofCloudsHeight = 0.0F;
      this.ofTrees = 0;
      this.ofRain = 0;
      this.ofBetterGrass = 3;
      this.ofAutoSaveTicks = 4000;
      this.ofLagometer = false;
      this.ofShowFps = false;
      this.ofProfiler = false;
      this.ofWeather = true;
      this.ofSky = true;
      this.ofStars = true;
      this.ofSunMoon = true;
      this.ofVignette = 0;
      this.ofChunkUpdates = 1;
      this.ofChunkUpdatesDynamic = false;
      this.ofTime = 0;
      this.ofClearWater = false;
      this.ofBetterSnow = false;
      this.ofFullscreenMode = "Default";
      this.ofSwampColors = true;
      this.ofRandomMobs = true;
      this.ofSmoothBiomes = true;
      this.ofCustomFonts = true;
      this.ofCustomColors = true;
      this.ofCustomItems = true;
      this.ofCustomSky = true;
      this.ofShowCapes = true;
      this.ofConnectedTextures = 2;
      this.ofNaturalTextures = false;
      this.ofAnimatedWater = 0;
      this.ofAnimatedLava = 0;
      this.ofAnimatedFire = true;
      this.ofAnimatedPortal = true;
      this.ofAnimatedRedstone = true;
      this.ofAnimatedExplosion = true;
      this.ofAnimatedFlame = true;
      this.ofAnimatedSmoke = true;
      this.ofVoidParticles = true;
      this.ofWaterParticles = true;
      this.ofRainSplash = true;
      this.ofPortalParticles = true;
      this.ofPotionParticles = true;
      this.ofFireworkParticles = true;
      this.ofDrippingWaterLava = true;
      this.ofAnimatedTerrain = true;
      this.ofAnimatedTextures = true;
      Shaders.setShaderPack(Shaders.packNameNone);
      Shaders.configAntialiasingLevel = 0;
      Shaders.uninit();
      Shaders.storeConfig();
      this.updateWaterOpacity();
      this.mc.refreshResources();
      this.saveOptions();
   }

   public void updateVSync() {
      Display.setVSyncEnabled(this.enableVsync);
   }

   private void updateWaterOpacity() {
      if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
         Config.waterOpacityChanged = true;
      }

      ClearWater.updateWaterOpacity(this, this.mc.theWorld);
   }

   public void setAllAnimations(boolean bl) {
      int i = bl ? 0 : 2;
      this.ofAnimatedWater = i;
      this.ofAnimatedLava = i;
      this.ofAnimatedFire = bl;
      this.ofAnimatedPortal = bl;
      this.ofAnimatedRedstone = bl;
      this.ofAnimatedExplosion = bl;
      this.ofAnimatedFlame = bl;
      this.ofAnimatedSmoke = bl;
      this.ofVoidParticles = bl;
      this.ofWaterParticles = bl;
      this.ofRainSplash = bl;
      this.ofPortalParticles = bl;
      this.ofPotionParticles = bl;
      this.ofFireworkParticles = bl;
      this.particleSetting = bl ? 0 : 2;
      this.ofDrippingWaterLava = bl;
      this.ofAnimatedTerrain = bl;
      this.ofAnimatedTextures = bl;
   }

   private static int nextValue(int i, int[] ints) {
      int j = indexOf(i, ints);
      if (j < 0) {
         return ints[0];
      }

      if (++j >= ints.length) {
         j = 0;
      }

      return ints[j];
   }

   private static int limit(int i, int[] ints) {
      int j = indexOf(i, ints);
      return j < 0 ? ints[0] : i;
   }

   private static int indexOf(int i, int[] ints) {
      for (int j = 0; j < ints.length; j++) {
         if (ints[j] == i) {
            return j;
         }
      }

      return -1;
   }

   public enum Options {
      INVERT_MOUSE("INVERT_MOUSE", 0, "options.invertMouse", false, true),
      SENSITIVITY("SENSITIVITY", 1, "options.sensitivity", true, false),
      FOV("FOV", 2, "options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("GAMMA", 3, "options.gamma", true, false),
      SATURATION("SATURATION", 4, "options.saturation", true, false),
      RENDER_DISTANCE("RENDER_DISTANCE", 5, "options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("VIEW_BOBBING", 6, "options.viewBobbing", false, true),
      ANAGLYPH("ANAGLYPH", 7, "options.anaglyph", false, true),
      FRAMERATE_LIMIT("FRAMERATE_LIMIT", 8, "options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
      FBO_ENABLE("FBO_ENABLE", 9, "options.fboEnable", false, true),
      RENDER_CLOUDS("RENDER_CLOUDS", 10, "options.renderClouds", false, false),
      GRAPHICS("GRAPHICS", 11, "options.graphics", false, false),
      AMBIENT_OCCLUSION("AMBIENT_OCCLUSION", 12, "options.ao", false, false),
      GUI_SCALE("GUI_SCALE", 13, "options.guiScale", false, false),
      PARTICLES("PARTICLES", 14, "options.particles", false, false),
      CHAT_VISIBILITY("CHAT_VISIBILITY", 15, "options.chat.visibility", false, false),
      CHAT_COLOR("CHAT_COLOR", 16, "options.chat.color", false, true),
      CHAT_LINKS("CHAT_LINKS", 17, "options.chat.links", false, true),
      CHAT_OPACITY("CHAT_OPACITY", 18, "options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("CHAT_LINKS_PROMPT", 19, "options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("SNOOPER_ENABLED", 20, "options.snooper", false, true),
      USE_FULLSCREEN("USE_FULLSCREEN", 21, "options.fullscreen", false, true),
      ENABLE_VSYNC("ENABLE_VSYNC", 22, "options.vsync", false, true),
      USE_VBO("USE_VBO", 23, "options.vbo", false, true),
      TOUCHSCREEN("TOUCHSCREEN", 24, "options.touchscreen", false, true),
      CHAT_SCALE("CHAT_SCALE", 25, "options.chat.scale", true, false),
      CHAT_WIDTH("CHAT_WIDTH", 26, "options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("CHAT_HEIGHT_FOCUSED", 27, "options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("CHAT_HEIGHT_UNFOCUSED", 28, "options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("MIPMAP_LEVELS", 29, "options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("FORCE_UNICODE_FONT", 30, "options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("STREAM_BYTES_PER_PIXEL", 31, "options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("STREAM_VOLUME_MIC", 32, "options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("STREAM_VOLUME_SYSTEM", 33, "options.stream.systemVolume", true, false),
      STREAM_KBPS("STREAM_KBPS", 34, "options.stream.kbps", true, false),
      STREAM_FPS("STREAM_FPS", 35, "options.stream.fps", true, false),
      STREAM_COMPRESSION("STREAM_COMPRESSION", 36, "options.stream.compression", false, false),
      STREAM_SEND_METADATA("STREAM_SEND_METADATA", 37, "options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("STREAM_CHAT_ENABLED", 38, "options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("STREAM_CHAT_USER_FILTER", 39, "options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("STREAM_MIC_TOGGLE_BEHAVIOR", 40, "options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("BLOCK_ALTERNATIVES", 41, "options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("REDUCED_DEBUG_INFO", 42, "options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("ENTITY_SHADOWS", 43, "options.entityShadows", false, true),
      FOG_FANCY("", 999, "of.options.FOG_FANCY", false, false),
      FOG_START("", 999, "of.options.FOG_START", false, false),
      MIPMAP_TYPE("", 999, "of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
      SMOOTH_FPS("", 999, "of.options.SMOOTH_FPS", false, false),
      CLOUDS("", 999, "of.options.CLOUDS", false, false),
      CLOUD_HEIGHT("", 999, "of.options.CLOUD_HEIGHT", true, false),
      TREES("", 999, "of.options.TREES", false, false),
      RAIN("", 999, "of.options.RAIN", false, false),
      ANIMATED_WATER("", 999, "of.options.ANIMATED_WATER", false, false),
      ANIMATED_LAVA("", 999, "of.options.ANIMATED_LAVA", false, false),
      ANIMATED_FIRE("", 999, "of.options.ANIMATED_FIRE", false, false),
      ANIMATED_PORTAL("", 999, "of.options.ANIMATED_PORTAL", false, false),
      AO_LEVEL("", 999, "of.options.AO_LEVEL", true, false),
      LAGOMETER("", 999, "of.options.LAGOMETER", false, false),
      SHOW_FPS("", 999, "of.options.SHOW_FPS", false, false),
      AUTOSAVE_TICKS("", 999, "of.options.AUTOSAVE_TICKS", false, false),
      BETTER_GRASS("", 999, "of.options.BETTER_GRASS", false, false),
      ANIMATED_REDSTONE("", 999, "of.options.ANIMATED_REDSTONE", false, false),
      ANIMATED_EXPLOSION("", 999, "of.options.ANIMATED_EXPLOSION", false, false),
      ANIMATED_FLAME("", 999, "of.options.ANIMATED_FLAME", false, false),
      ANIMATED_SMOKE("", 999, "of.options.ANIMATED_SMOKE", false, false),
      WEATHER("", 999, "of.options.WEATHER", false, false),
      SKY("", 999, "of.options.SKY", false, false),
      STARS("", 999, "of.options.STARS", false, false),
      SUN_MOON("", 999, "of.options.SUN_MOON", false, false),
      VIGNETTE("", 999, "of.options.VIGNETTE", false, false),
      CHUNK_UPDATES("", 999, "of.options.CHUNK_UPDATES", false, false),
      CHUNK_UPDATES_DYNAMIC("", 999, "of.options.CHUNK_UPDATES_DYNAMIC", false, false),
      TIME("", 999, "of.options.TIME", false, false),
      CLEAR_WATER("", 999, "of.options.CLEAR_WATER", false, false),
      SMOOTH_WORLD("", 999, "of.options.SMOOTH_WORLD", false, false),
      VOID_PARTICLES("", 999, "of.options.VOID_PARTICLES", false, false),
      WATER_PARTICLES("", 999, "of.options.WATER_PARTICLES", false, false),
      RAIN_SPLASH("", 999, "of.options.RAIN_SPLASH", false, false),
      PORTAL_PARTICLES("", 999, "of.options.PORTAL_PARTICLES", false, false),
      POTION_PARTICLES("", 999, "of.options.POTION_PARTICLES", false, false),
      FIREWORK_PARTICLES("", 999, "of.options.FIREWORK_PARTICLES", false, false),
      PROFILER("", 999, "of.options.PROFILER", false, false),
      DRIPPING_WATER_LAVA("", 999, "of.options.DRIPPING_WATER_LAVA", false, false),
      BETTER_SNOW("", 999, "of.options.BETTER_SNOW", false, false),
      FULLSCREEN_MODE("", 999, "of.options.FULLSCREEN_MODE", false, false),
      ANIMATED_TERRAIN("", 999, "of.options.ANIMATED_TERRAIN", false, false),
      SWAMP_COLORS("", 999, "of.options.SWAMP_COLORS", false, false),
      RANDOM_MOBS("", 999, "of.options.RANDOM_MOBS", false, false),
      SMOOTH_BIOMES("", 999, "of.options.SMOOTH_BIOMES", false, false),
      CUSTOM_FONTS("", 999, "of.options.CUSTOM_FONTS", false, false),
      CUSTOM_COLORS("", 999, "of.options.CUSTOM_COLORS", false, false),
      SHOW_CAPES("", 999, "of.options.SHOW_CAPES", false, false),
      CONNECTED_TEXTURES("", 999, "of.options.CONNECTED_TEXTURES", false, false),
      CUSTOM_ITEMS("", 999, "of.options.CUSTOM_ITEMS", false, false),
      AA_LEVEL("", 999, "of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
      AF_LEVEL("", 999, "of.options.AF_LEVEL", true, false, 1.0F, 16.0F, 1.0F),
      ANIMATED_TEXTURES("", 999, "of.options.ANIMATED_TEXTURES", false, false),
      NATURAL_TEXTURES("", 999, "of.options.NATURAL_TEXTURES", false, false),
      HELD_ITEM_TOOLTIPS("", 999, "of.options.HELD_ITEM_TOOLTIPS", false, false),
      DROPPED_ITEMS("", 999, "of.options.DROPPED_ITEMS", false, false),
      LAZY_CHUNK_LOADING("", 999, "of.options.LAZY_CHUNK_LOADING", false, false),
      CUSTOM_SKY("", 999, "of.options.CUSTOM_SKY", false, false),
      FAST_MATH("", 999, "of.options.FAST_MATH", false, false),
      FAST_RENDER("", 999, "of.options.FAST_RENDER", false, false),
      TRANSLUCENT_BLOCKS("", 999, "of.options.TRANSLUCENT_BLOCKS", false, false),
      DYNAMIC_FOV("", 999, "of.options.DYNAMIC_FOV", false, false),
      DYNAMIC_LIGHTS("", 999, "of.options.DYNAMIC_LIGHTS", false, false);

      private final boolean enumFloat;
      private final boolean enumBoolean;
      private final String enumString;
      private final float valueStep;
      private float valueMin;
      private float valueMax;
      private static final GameSettings.Options[] VALUES = new GameSettings.Options[]{
         INVERT_MOUSE,
         SENSITIVITY,
         FOV,
         GAMMA,
         SATURATION,
         RENDER_DISTANCE,
         VIEW_BOBBING,
         ANAGLYPH,
         FRAMERATE_LIMIT,
         FBO_ENABLE,
         RENDER_CLOUDS,
         GRAPHICS,
         AMBIENT_OCCLUSION,
         GUI_SCALE,
         PARTICLES,
         CHAT_VISIBILITY,
         CHAT_COLOR,
         CHAT_LINKS,
         CHAT_OPACITY,
         CHAT_LINKS_PROMPT,
         SNOOPER_ENABLED,
         USE_FULLSCREEN,
         ENABLE_VSYNC,
         USE_VBO,
         TOUCHSCREEN,
         CHAT_SCALE,
         CHAT_WIDTH,
         CHAT_HEIGHT_FOCUSED,
         CHAT_HEIGHT_UNFOCUSED,
         MIPMAP_LEVELS,
         FORCE_UNICODE_FONT,
         STREAM_BYTES_PER_PIXEL,
         STREAM_VOLUME_MIC,
         STREAM_VOLUME_SYSTEM,
         STREAM_KBPS,
         STREAM_FPS,
         STREAM_COMPRESSION,
         STREAM_SEND_METADATA,
         STREAM_CHAT_ENABLED,
         STREAM_CHAT_USER_FILTER,
         STREAM_MIC_TOGGLE_BEHAVIOR,
         BLOCK_ALTERNATIVES,
         REDUCED_DEBUG_INFO,
         ENTITY_SHADOWS
      };

      public static GameSettings.Options getEnumOptions(int ordinal) {
         for (GameSettings.Options options : values()) {
            if (options.returnEnumOrdinal() == ordinal) {
               return options;
            }
         }

         return null;
      }

      Options(String string2, int j, String string3, boolean bl, boolean bl2) {
         this(string2, j, string3, bl, bl2, 0.0F, 1.0F, 0.0F);
      }

      Options(String string2, int j, String string3, boolean bl, boolean bl2, float f, float g, float h) {
         this.enumString = string3;
         this.enumFloat = bl;
         this.enumBoolean = bl2;
         this.valueMin = f;
         this.valueMax = g;
         this.valueStep = h;
      }

      public boolean getEnumFloat() {
         return this.enumFloat;
      }

      public boolean getEnumBoolean() {
         return this.enumBoolean;
      }

      public int returnEnumOrdinal() {
         return this.ordinal();
      }

      public String getEnumString() {
         return this.enumString;
      }

      public float getValueMax() {
         return this.valueMax;
      }

      public void setValueMax(float value) {
         this.valueMax = value;
      }

      public float normalizeValue(float value) {
         return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
      }

      public float denormalizeValue(float value) {
         return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(value, 0.0F, 1.0F));
      }

      public float snapToStepClamp(float value) {
         value = this.snapToStep(value);
         return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
      }

      public float snapToStep(float value) {
         if (this.valueStep > 0.0F) {
            value = this.valueStep * Math.round(value / this.valueStep);
         }

         return value;
      }

      public static float accessValueMax(GameSettings.Options options) {
         return options.valueMax;
      }

      public static float accessValueMin(GameSettings.Options options) {
         return options.valueMin;
      }
   }
}
