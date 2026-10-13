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
   public float Zm = 0.5F;
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
   public boolean G = true;
   private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
   public boolean touchscreen;
   public int a;
   public int z;
   public boolean Z9 = true;
   public float chatScale = 1.0F;
   public float l6 = 1.0F;
   public float la = 0.44366196F;
   public float o = 1.0F;
   public boolean l8 = true;
   public int g = 4;
   private Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
   public float ZX = 0.5F;
   public float m = 1.0F;
   public float Zh = 1.0F;
   public float K = 0.5412844F;
   public float u = 0.31690142F;
   public int streamCompression = 1;
   public boolean streamSendMetadata = true;
   public String y = "";
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
   public KeyBinding Ze = new KeyBinding("key.sprint", 29, "key.categories.movement");
   public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public KeyBinding Z7 = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding lu = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding ZW = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding Al = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding Za = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding ZP = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding v = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding p = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
   public KeyBinding ly = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
   public KeyBinding ZL = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
   public KeyBinding L = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
   public KeyBinding e = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
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
   private File ZV;
   public EnumDifficulty difficulty;
   public boolean Zn;
   public int thirdPersonView;
   public boolean n;
   public boolean Zj;
   public boolean A_;
   public String lastServer;
   public boolean lt;
   public boolean ZI;
   public float b;
   public float Zu;
   public float Z_;
   public int guiScale;
   public int particleSetting;
   public String language;
   public boolean forceUnicodeFont;
   public int ofFogType = 1;
   public float ofFogStart = 0.8F;
   public int ofMipmapType = 0;
   public boolean f = false;
   public boolean R = false;
   public boolean i = Config.isSingleProcessor();
   public boolean lH = Config.isSingleProcessor();
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
   public boolean ZH = false;
   public boolean lJ = false;
   public boolean ZQ = false;
   public boolean lP = true;
   public boolean ZC = true;
   public boolean lW = true;
   public boolean d = true;
   public int ofVignette = 0;
   public int ofChunkUpdates = 1;
   public boolean X = false;
   public int ofTime = 0;
   public boolean ofClearWater = false;
   public boolean Zi = false;
   public String ofFullscreenMode = "Default";
   public boolean lD = true;
   public boolean Zb = true;
   public boolean lx = true;
   public boolean lo = true;
   public boolean s = true;
   public boolean l0 = true;
   public boolean AJ = true;
   public int ofConnectedTextures = 2;
   public boolean ZZ = true;
   public boolean ZB = false;
   public boolean lg = false;
   public boolean ZE = true;
   public int ofTranslucentBlocks = 0;
   public boolean li = true;
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
   public static final String Zd = "Default";
   private static final int[] OF_TREES_VALUES = new int[]{0, 1, 4, 2};
   private static final int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
   private static final String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public KeyBinding ofKeyBindZoom;
   private File optionsFileOF;
   private static final long ab = 110509229549634L;

   public GameSettings(Minecraft var1, File var2) {
      this.keyBindings = ArrayUtils.addAll(
         new KeyBinding[]{
            this.lu,
            this.Z7,
            this.keyBindForward,
            this.keyBindLeft,
            this.keyBindBack,
            this.keyBindRight,
            this.keyBindJump,
            this.keyBindSneak,
            this.Ze,
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.ZW,
            this.keyBindPickBlock,
            this.Al,
            this.Za,
            this.ZP,
            this.v,
            this.ly,
            this.ZL,
            this.L,
            this.e,
            this.p,
            this.keyBindSpectatorOutlines
         },
         this.keyBindsHotbar
      );
      this.difficulty = EnumDifficulty.NORMAL;
      this.lastServer = "";
      this.b = 70.0F;
      this.language = "en_US";
      this.forceUnicodeFont = false;
      this.mc = var1;
      this.ZV = new File(var2, "options.txt");
      this.optionsFileOF = new File(var2, "optionsof.txt");
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
            this.lu,
            this.Z7,
            this.keyBindForward,
            this.keyBindLeft,
            this.keyBindBack,
            this.keyBindRight,
            this.keyBindJump,
            this.keyBindSneak,
            this.Ze,
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.ZW,
            this.keyBindPickBlock,
            this.Al,
            this.Za,
            this.ZP,
            this.v,
            this.ly,
            this.ZL,
            this.L,
            this.e,
            this.p,
            this.keyBindSpectatorOutlines
         },
         this.keyBindsHotbar
      );
      this.difficulty = EnumDifficulty.NORMAL;
      this.lastServer = "";
      this.b = 70.0F;
      this.language = "en_US";
      this.forceUnicodeFont = false;
   }

   public static String getKeyDisplayString(int var0) {
      return var0 < 0
         ? I18n.format("key.mouseButton", var0 + 101)
         : (var0 < 256 ? Keyboard.getKeyName(var0) : String.format("%c", (char)(var0 - 256)).toUpperCase());
   }

   public static boolean isKeyDown(KeyBinding var0) {
      int var4 = var0.getKeyCode();
      return var4 < -100 || var4 > 255
         ? false
         : (var0.getKeyCode() == 0 ? false : (var0.getKeyCode() < 0 ? Mouse.isButtonDown(var0.getKeyCode() + 100) : Keyboard.isKeyDown(var0.getKeyCode())));
   }

   public void setOptionKeyBinding(KeyBinding var1, int var2) {
      var1.setKeyCode(var2);
      this.saveOptions();
   }

   public void setOptionFloatValue(GameSettings.Options var1, float var2) {
      this.setOptionFloatValueOF(var1, var2);
      if (var1 == GameSettings.Options.SENSITIVITY) {
         this.Zm = var2;
      }

      if (var1 == GameSettings.Options.FOV) {
         this.b = var2;
      }

      if (var1 == GameSettings.Options.GAMMA) {
         this.Zu = var2;
      }

      if (var1 == GameSettings.Options.FRAMERATE_LIMIT) {
         this.limitFramerate = (int)var2;
         this.enableVsync = false;
         if (this.limitFramerate <= 0) {
            this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
            this.enableVsync = true;
         }

         this.updateVSync();
      }

      if (var1 == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = var2;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.o = var2;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.la = var2;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (var1 == GameSettings.Options.CHAT_WIDTH) {
         this.l6 = var2;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (var1 == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = var2;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (var1 == GameSettings.Options.MIPMAP_LEVELS) {
         int var6 = this.g;
         this.g = (int)var2;
         if (var6 != var2) {
            this.mc.getTextureMapBlocks().setMipmapLevels(this.g);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.g > 0);
            this.mc.scheduleResourcesRefresh();
         }
      }

      if (var1 == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)var2;
         this.mc.renderGlobal.L();
      }

      if (var1 == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         this.ZX = var2;
      }

      if (var1 == GameSettings.Options.STREAM_VOLUME_MIC) {
         this.m = var2;
      }

      if (var1 == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         this.Zh = var2;
      }

      if (var1 == GameSettings.Options.STREAM_KBPS) {
         this.K = var2;
      }

      if (var1 == GameSettings.Options.STREAM_FPS) {
         this.u = var2;
      }
   }

   public void setOptionValue(GameSettings.Options var1, int var2) {
      this.setOptionValueOF(var1, var2);
      if (var1 == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if (var1 == GameSettings.Options.GUI_SCALE) {
         this.guiScale = this.guiScale + var2 & 3;
      }

      if (var1 == GameSettings.Options.PARTICLES) {
         this.particleSetting = (this.particleSetting + var2) % 3;
      }

      if (var1 == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (var1 == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + var2) % 3;
      }

      if (var1 == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
      }

      if (var1 == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (var1 == GameSettings.Options.ANAGLYPH) {
         if (!this.anaglyph && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
            return;
         }

         this.anaglyph = !this.anaglyph;
         this.mc.refreshResources();
      }

      if (var1 == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.updateRenderClouds();
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + var2) % 3;
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + var2) % 3);
      }

      if (var1 == GameSettings.Options.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + var2) % 3;
      }

      if (var1 == GameSettings.Options.STREAM_SEND_METADATA) {
         this.streamSendMetadata = !this.streamSendMetadata;
      }

      if (var1 == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + var2) % 3;
      }

      if (var1 == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + var2) % 3;
      }

      if (var1 == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + var2) % 2;
      }

      if (var1 == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if (var1 == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (var1 == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if (var1 == GameSettings.Options.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if (var1 == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (var1 == GameSettings.Options.USE_FULLSCREEN) {
         this.fullScreen = !this.fullScreen;
         if (this.mc.isFullScreen() != this.fullScreen) {
            this.mc.toggleFullscreen();
         }
      }

      if (var1 == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         Display.setVSyncEnabled(this.enableVsync);
      }

      if (var1 == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if (var1 == GameSettings.Options.ENTITY_SHADOWS) {
         this.entityShadows = !this.entityShadows;
      }

      this.saveOptions();
   }

   public float getOptionFloatValue(GameSettings.Options var1) {
      return var1 == GameSettings.Options.CLOUD_HEIGHT
         ? this.ofCloudsHeight
         : (
            var1 == GameSettings.Options.AO_LEVEL
               ? this.ofAoLevel
               : (
                  var1 == GameSettings.Options.AA_LEVEL
                     ? this.ofAaLevel
                     : (
                        var1 == GameSettings.Options.AF_LEVEL
                           ? this.ofAfLevel
                           : (
                              var1 == GameSettings.Options.MIPMAP_TYPE
                                 ? this.ofMipmapType
                                 : (
                                    var1 == GameSettings.Options.FRAMERATE_LIMIT
                                       ? (
                                          this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync
                                             ? 0.0F
                                             : this.limitFramerate
                                       )
                                       : (
                                          var1 == GameSettings.Options.FOV
                                             ? this.b
                                             : (
                                                var1 == GameSettings.Options.GAMMA
                                                   ? this.Zu
                                                   : (
                                                      var1 == GameSettings.Options.SATURATION
                                                         ? this.Z_
                                                         : (
                                                            var1 == GameSettings.Options.SENSITIVITY
                                                               ? this.Zm
                                                               : (
                                                                  var1 == GameSettings.Options.CHAT_OPACITY
                                                                     ? this.chatOpacity
                                                                     : (
                                                                        var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                           ? this.o
                                                                           : (
                                                                              var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                                 ? this.la
                                                                                 : (
                                                                                    var1 == GameSettings.Options.CHAT_SCALE
                                                                                       ? this.chatScale
                                                                                       : (
                                                                                          var1 == GameSettings.Options.CHAT_WIDTH
                                                                                             ? this.l6
                                                                                             : (
                                                                                                var1 == GameSettings.Options.FRAMERATE_LIMIT
                                                                                                   ? this.limitFramerate
                                                                                                   : (
                                                                                                      var1 == GameSettings.Options.MIPMAP_LEVELS
                                                                                                         ? this.g
                                                                                                         : (
                                                                                                            var1 == GameSettings.Options.RENDER_DISTANCE
                                                                                                               ? this.renderDistanceChunks
                                                                                                               : (
                                                                                                                  var1
                                                                                                                        == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                     ? this.ZX
                                                                                                                     : (
                                                                                                                        var1
                                                                                                                              == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                                                           ? this.m
                                                                                                                           : (
                                                                                                                              var1
                                                                                                                                    == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                                                 ? this.Zh
                                                                                                                                 : (
                                                                                                                                    var1
                                                                                                                                          == GameSettings.Options.STREAM_KBPS
                                                                                                                                       ? this.K
                                                                                                                                       : (
                                                                                                                                          var1
                                                                                                                                                == GameSettings.Options.STREAM_FPS
                                                                                                                                             ? this.u
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

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean getOptionOrdinalValue(GameSettings.Options var1) {
      switch (var1) {
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

   private static String getTranslation(String[] var0, int var1) {
      if (var1 < 0 || var1 >= var0.length) {
         var1 = 0;
      }

      return I18n.format(var0[var1]);
   }

   public String getKeyBinding(GameSettings.Options var1) {
      String var5 = this.getKeyBindingOF(var1);
      if (var5 != null) {
         return var5;
      }

      String var6 = I18n.format(var1.getEnumString()) + ": ";
      if (var1.getEnumFloat()) {
         float var10 = this.getOptionFloatValue(var1);
         float var8 = var1.normalizeValue(var10);
         return var1 == GameSettings.Options.SENSITIVITY
            ? (
               var8 == 0.0F
                  ? var6 + I18n.format("options.sensitivity.min")
                  : (var8 == 1.0F ? var6 + I18n.format("options.sensitivity.max") : var6 + (int)(var8 * 200.0F) + "%")
            )
            : (
               var1 == GameSettings.Options.FOV
                  ? (var10 == 70.0F ? var6 + I18n.format("options.fov.min") : (var10 == 110.0F ? var6 + I18n.format("options.fov.max") : var6 + (int)var10))
                  : (
                     var1 == GameSettings.Options.FRAMERATE_LIMIT
                        ? (var10 == GameSettings.Options.w(var1) ? var6 + I18n.format("options.framerateLimit.max") : var6 + (int)var10 + " fps")
                        : (
                           var1 == GameSettings.Options.RENDER_CLOUDS
                              ? (var10 == GameSettings.Options.I(var1) ? var6 + I18n.format("options.cloudHeight.min") : var6 + ((int)var10 + 128))
                              : (
                                 var1 == GameSettings.Options.GAMMA
                                    ? (
                                       var8 == 0.0F
                                          ? var6 + I18n.format("options.gamma.min")
                                          : (var8 == 1.0F ? var6 + I18n.format("options.gamma.max") : var6 + "+" + (int)(var8 * 100.0F) + "%")
                                    )
                                    : (
                                       var1 == GameSettings.Options.SATURATION
                                          ? var6 + (int)(var8 * 400.0F) + "%"
                                          : (
                                             var1 == GameSettings.Options.CHAT_OPACITY
                                                ? var6 + (int)(var8 * 90.0F + 10.0F) + "%"
                                                : (
                                                   var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                      ? var6 + GuiNewChat.calculateChatboxHeight(var8) + "px"
                                                      : (
                                                         var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                            ? var6 + GuiNewChat.calculateChatboxHeight(var8) + "px"
                                                            : (
                                                               var1 == GameSettings.Options.CHAT_WIDTH
                                                                  ? var6 + GuiNewChat.calculateChatboxWidth(var8) + "px"
                                                                  : (
                                                                     var1 == GameSettings.Options.RENDER_DISTANCE
                                                                        ? var6 + (int)var10 + " chunks"
                                                                        : (
                                                                           var1 == GameSettings.Options.MIPMAP_LEVELS
                                                                              ? (var10 == 0.0F ? var6 + I18n.format("options.off") : var6 + (int)var10)
                                                                              : (
                                                                                 var1 == GameSettings.Options.STREAM_FPS
                                                                                    ? var6 + this.O(var8) + " fps"
                                                                                    : (
                                                                                       var1 == GameSettings.Options.STREAM_KBPS
                                                                                          ? var6 + this.e(var8) + " Kbps"
                                                                                          : (
                                                                                             var1 == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                ? var6 + String.format("%.3f bpp", this.Y(var8))
                                                                                                : (
                                                                                                   var8 == 0.0F
                                                                                                      ? var6 + I18n.format("options.off")
                                                                                                      : var6 + (int)(var8 * 100.0F) + "%"
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

      if (var1.getEnumBoolean()) {
         boolean var9 = this.getOptionOrdinalValue(var1);
         return var9 ? var6 + I18n.format("options.on") : var6 + I18n.format("options.off");
      }

      if (var1 == GameSettings.Options.GUI_SCALE) {
         return var6 + getTranslation(GUISCALES, this.guiScale);
      }

      if (var1 == GameSettings.Options.CHAT_VISIBILITY) {
         return var6 + I18n.format(this.chatVisibility.getResourceKey());
      }

      if (var1 == GameSettings.Options.PARTICLES) {
         return var6 + getTranslation(PARTICLES, this.particleSetting);
      }

      if (var1 == GameSettings.Options.AMBIENT_OCCLUSION) {
         return var6 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
      }

      if (var1 == GameSettings.Options.STREAM_COMPRESSION) {
         return var6 + getTranslation(STREAM_COMPRESSIONS, this.streamCompression);
      }

      if (var1 == GameSettings.Options.STREAM_CHAT_ENABLED) {
         return var6 + getTranslation(STREAM_CHAT_MODES, this.streamChatEnabled);
      }

      if (var1 == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         return var6 + getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
      }

      if (var1 == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         return var6 + getTranslation(STREAM_MIC_MODES, this.streamMicToggleBehavior);
      }

      if (var1 == GameSettings.Options.RENDER_CLOUDS) {
         return var6 + getTranslation(CLOUDS_TYPES, this.clouds);
      }

      if (var1 == GameSettings.Options.GRAPHICS) {
         if (this.fancyGraphics) {
            return var6 + I18n.format("options.graphics.fancy");
         }

         String var7 = "options.graphics.fast";
         return var6 + I18n.format("options.graphics.fast");
      } else {
         return var6;
      }
   }

   private int O(float var1) {
      return MathHelper.floor_float(10.0F + var1 * 50.0F);
   }

   private int e(float var1) {
      return MathHelper.floor_float(230.0F + var1 * 3270.0F);
   }

   private float Y(float var1) {
      return 0.1F + var1 * 0.1F;
   }

   public void loadOptions() {
      try {
         if (!this.ZV.exists()) {
            return;
         }

         BufferedReader var4 = new BufferedReader(new FileReader(this.ZV));
         String var5 = "";
         this.mapSoundLevels.clear();

         while ((var5 = var4.readLine()) != null) {
            try {
               String[] var6 = var5.split(":");
               if (var6[0].equals("mouseSensitivity")) {
                  this.Zm = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("fov")) {
                  this.b = this.parseFloat(var6[1]) * 40.0F + 70.0F;
               }

               if (var6[0].equals("gamma")) {
                  this.Zu = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("saturation")) {
                  this.Z_ = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("invertYMouse")) {
                  this.invertMouse = var6[1].equals("true");
               }

               if (var6[0].equals("renderDistance")) {
                  this.renderDistanceChunks = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("guiScale")) {
                  this.guiScale = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("particles")) {
                  this.particleSetting = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("bobView")) {
                  this.viewBobbing = var6[1].equals("true");
               }

               if (var6[0].equals("anaglyph3d")) {
                  this.anaglyph = var6[1].equals("true");
               }

               if (var6[0].equals("maxFps")) {
                  this.limitFramerate = Integer.parseInt(var6[1]);
                  this.enableVsync = false;
                  if (this.limitFramerate <= 0) {
                     this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                     this.enableVsync = true;
                  }

                  this.updateVSync();
               }

               if (var6[0].equals("fboEnable")) {
                  this.fboEnable = var6[1].equals("true");
               }

               if (var6[0].equals("difficulty")) {
                  this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(var6[1]));
               }

               if (var6[0].equals("fancyGraphics")) {
                  this.fancyGraphics = var6[1].equals("true");
                  this.updateRenderClouds();
               }

               if (var6[0].equals("ao")) {
                  if (var6[1].equals("true")) {
                     this.ambientOcclusion = 2;
                  } else if (var6[1].equals("false")) {
                     this.ambientOcclusion = 0;
                  } else {
                     this.ambientOcclusion = Integer.parseInt(var6[1]);
                  }
               }

               if (var6[0].equals("renderClouds")) {
                  if (var6[1].equals("true")) {
                     this.clouds = 2;
                  } else if (var6[1].equals("false")) {
                     this.clouds = 0;
                  } else if (var6[1].equals("fast")) {
                     this.clouds = 1;
                  }
               }

               if (var6[0].equals("resourcePacks")) {
                  this.resourcePacks = gson.fromJson(var5.substring(var5.indexOf(58) + 1), typeListString);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if (var6[0].equals("incompatibleResourcePacks")) {
                  this.incompatibleResourcePacks = gson.fromJson(var5.substring(var5.indexOf(58) + 1), typeListString);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if (var6[0].equals("lastServer") && var6.length >= 2) {
                  this.lastServer = var5.substring(var5.indexOf(58) + 1);
               }

               if (var6[0].equals("lang") && var6.length >= 2) {
                  this.language = var6[1];
               }

               if (var6[0].equals("chatVisibility")) {
                  this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(var6[1]));
               }

               if (var6[0].equals("chatColors")) {
                  this.chatColours = var6[1].equals("true");
               }

               if (var6[0].equals("chatLinks")) {
                  this.chatLinks = var6[1].equals("true");
               }

               if (var6[0].equals("chatLinksPrompt")) {
                  this.chatLinksPrompt = var6[1].equals("true");
               }

               if (var6[0].equals("chatOpacity")) {
                  this.chatOpacity = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("snooperEnabled")) {
                  this.snooperEnabled = var6[1].equals("true");
               }

               if (var6[0].equals("fullscreen")) {
                  this.fullScreen = var6[1].equals("true");
               }

               if (var6[0].equals("enableVsync")) {
                  this.enableVsync = var6[1].equals("true");
                  this.updateVSync();
               }

               if (var6[0].equals("useVbo")) {
                  this.useVbo = var6[1].equals("true");
               }

               if (var6[0].equals("hideServerAddress")) {
                  this.hideServerAddress = var6[1].equals("true");
               }

               if (var6[0].equals("advancedItemTooltips")) {
                  this.advancedItemTooltips = var6[1].equals("true");
               }

               if (var6[0].equals("pauseOnLostFocus")) {
                  this.G = var6[1].equals("true");
               }

               if (var6[0].equals("touchscreen")) {
                  this.touchscreen = var6[1].equals("true");
               }

               if (var6[0].equals("overrideHeight")) {
                  this.z = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("overrideWidth")) {
                  this.a = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("heldItemTooltips")) {
                  this.Z9 = var6[1].equals("true");
               }

               if (var6[0].equals("chatHeightFocused")) {
                  this.o = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("chatHeightUnfocused")) {
                  this.la = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("chatScale")) {
                  this.chatScale = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("chatWidth")) {
                  this.l6 = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("showInventoryAchievementHint")) {
                  this.l8 = var6[1].equals("true");
               }

               if (var6[0].equals("mipmapLevels")) {
                  this.g = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("streamBytesPerPixel")) {
                  this.ZX = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("streamMicVolume")) {
                  this.m = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("streamSystemVolume")) {
                  this.Zh = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("streamKbps")) {
                  this.K = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("streamFps")) {
                  this.u = this.parseFloat(var6[1]);
               }

               if (var6[0].equals("streamCompression")) {
                  this.streamCompression = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("streamSendMetadata")) {
                  this.streamSendMetadata = var6[1].equals("true");
               }

               if (var6[0].equals("streamPreferredServer") && var6.length >= 2) {
                  this.y = var5.substring(var5.indexOf(58) + 1);
               }

               if (var6[0].equals("streamChatEnabled")) {
                  this.streamChatEnabled = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("streamChatUserFilter")) {
                  this.streamChatUserFilter = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("streamMicToggleBehavior")) {
                  this.streamMicToggleBehavior = Integer.parseInt(var6[1]);
               }

               if (var6[0].equals("forceUnicodeFont")) {
                  this.forceUnicodeFont = var6[1].equals("true");
               }

               if (var6[0].equals("allowBlockAlternatives")) {
                  this.allowBlockAlternatives = var6[1].equals("true");
               }

               if (var6[0].equals("reducedDebugInfo")) {
                  this.reducedDebugInfo = var6[1].equals("true");
               }

               if (var6[0].equals("useNativeTransport")) {
                  this.useNativeTransport = var6[1].equals("true");
               }

               if (var6[0].equals("entityShadows")) {
                  this.entityShadows = var6[1].equals("true");
               }

               for (KeyBinding var10 : this.keyBindings) {
                  if (var6[0].equals("key_" + var10.getKeyDescription())) {
                     var10.setKeyCode(Integer.parseInt(var6[1]));
                  }
               }

               for (SoundCategory var20 : SoundCategory.values()) {
                  if (var6[0].equals("soundCategory_" + var20.getCategoryName())) {
                     this.mapSoundLevels.put(var20, this.parseFloat(var6[1]));
                  }
               }

               for (EnumPlayerModelParts var21 : EnumPlayerModelParts.values()) {
                  if (var6[0].equals("modelPart_" + var21.getPartName())) {
                     this.setModelPartEnabled(var21, var6[1].equals("true"));
                  }
               }
            } catch (Exception var11) {
               logger.warn("Skipping bad option: " + var5);
               var11.printStackTrace();
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         var4.close();
      } catch (Exception var12) {
         logger.error("Failed to load options", var12);
      }

      this.loadOfOptions();
   }

   private float parseFloat(String var1) {
      return var1.equals("true") ? 1.0F : (var1.equals("false") ? 0.0F : Float.parseFloat(var1));
   }

   public void saveOptions() {
      if (Reflector.FMLClientHandler.exists()) {
         Object var4 = Reflector.call(Reflector.FMLClientHandler_instance);
         if (var4 != null && Reflector.callBoolean(var4, Reflector.FMLClientHandler_isLoading)) {
            return;
         }
      }

      try {
         PrintWriter var10 = new PrintWriter(new FileWriter(this.ZV));
         var10.println("invertYMouse:" + this.invertMouse);
         var10.println("mouseSensitivity:" + this.Zm);
         var10.println("fov:" + (this.b - 70.0F) / 40.0F);
         var10.println("gamma:" + this.Zu);
         var10.println("saturation:" + this.Z_);
         var10.println("renderDistance:" + this.renderDistanceChunks);
         var10.println("guiScale:" + this.guiScale);
         var10.println("particles:" + this.particleSetting);
         var10.println("bobView:" + this.viewBobbing);
         var10.println("anaglyph3d:" + this.anaglyph);
         var10.println("maxFps:" + this.limitFramerate);
         var10.println("fboEnable:" + this.fboEnable);
         var10.println("difficulty:" + this.difficulty.getDifficultyId());
         var10.println("fancyGraphics:" + this.fancyGraphics);
         var10.println("ao:" + this.ambientOcclusion);
         switch (this.clouds) {
            case 0:
               var10.println("renderClouds:false");
               break;
            case 1:
               var10.println("renderClouds:fast");
               break;
            case 2:
               var10.println("renderClouds:true");
         }

         var10.println("resourcePacks:" + gson.toJson(this.resourcePacks));
         var10.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
         var10.println("lastServer:" + this.lastServer);
         var10.println("lang:" + this.language);
         var10.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         var10.println("chatColors:" + this.chatColours);
         var10.println("chatLinks:" + this.chatLinks);
         var10.println("chatLinksPrompt:" + this.chatLinksPrompt);
         var10.println("chatOpacity:" + this.chatOpacity);
         var10.println("snooperEnabled:" + this.snooperEnabled);
         var10.println("fullscreen:" + this.fullScreen);
         var10.println("enableVsync:" + this.enableVsync);
         var10.println("useVbo:" + this.useVbo);
         var10.println("hideServerAddress:" + this.hideServerAddress);
         var10.println("advancedItemTooltips:" + this.advancedItemTooltips);
         var10.println("pauseOnLostFocus:" + this.G);
         var10.println("touchscreen:" + this.touchscreen);
         var10.println("overrideWidth:" + this.a);
         var10.println("overrideHeight:" + this.z);
         var10.println("heldItemTooltips:" + this.Z9);
         var10.println("chatHeightFocused:" + this.o);
         var10.println("chatHeightUnfocused:" + this.la);
         var10.println("chatScale:" + this.chatScale);
         var10.println("chatWidth:" + this.l6);
         var10.println("showInventoryAchievementHint:" + this.l8);
         var10.println("mipmapLevels:" + this.g);
         var10.println("streamBytesPerPixel:" + this.ZX);
         var10.println("streamMicVolume:" + this.m);
         var10.println("streamSystemVolume:" + this.Zh);
         var10.println("streamKbps:" + this.K);
         var10.println("streamFps:" + this.u);
         var10.println("streamCompression:" + this.streamCompression);
         var10.println("streamSendMetadata:" + this.streamSendMetadata);
         var10.println("streamPreferredServer:" + this.y);
         var10.println("streamChatEnabled:" + this.streamChatEnabled);
         var10.println("streamChatUserFilter:" + this.streamChatUserFilter);
         var10.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         var10.println("forceUnicodeFont:" + this.forceUnicodeFont);
         var10.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         var10.println("reducedDebugInfo:" + this.reducedDebugInfo);
         var10.println("useNativeTransport:" + this.useNativeTransport);
         var10.println("entityShadows:" + this.entityShadows);

         for (KeyBinding var8 : this.keyBindings) {
            var10.println("key_" + var8.getKeyDescription() + ":" + var8.getKeyCode());
         }

         for (SoundCategory var17 : SoundCategory.values()) {
            var10.println("soundCategory_" + var17.getCategoryName() + ":" + this.getSoundLevel(var17));
         }

         for (EnumPlayerModelParts var18 : EnumPlayerModelParts.values()) {
            var10.println("modelPart_" + var18.getPartName() + ":" + this.setModelParts.contains(var18));
         }

         var10.close();
      } catch (Exception var9) {
         logger.error("Failed to save options", var9);
      }

      this.saveOfOptions();
      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory var1) {
      return this.mapSoundLevels.containsKey(var1) ? this.mapSoundLevels.get(var1) : 1.0F;
   }

   public void setSoundLevel(SoundCategory var1, float var2) {
      this.mc.getSoundHandler().setSoundLevel(var1, var2);
      this.mapSoundLevels.put(var1, var2);
   }

   public void sendSettingsToServer() {
      if (this.mc.thePlayer != null) {
         int var4 = 0;

         for (Object var6 : this.setModelParts) {
            var4 |= ((EnumPlayerModelParts)var6).getPartMask();
         }

         this.mc
            .thePlayer
            .sendQueue
            .addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, var4));
      }
   }

   public Set<EnumPlayerModelParts> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(EnumPlayerModelParts var1, boolean var2) {
      if (var2) {
         this.setModelParts.add(var1);
      } else {
         this.setModelParts.remove(var1);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(EnumPlayerModelParts var1) {
      if (!this.getModelParts().contains(var1)) {
         this.setModelParts.add(var1);
      } else {
         this.setModelParts.remove(var1);
      }

      this.sendSettingsToServer();
   }

   public int shouldRenderClouds() {
      return this.renderDistanceChunks >= 4 ? this.clouds : 0;
   }

   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   private void setOptionFloatValueOF(GameSettings.Options var1, float var2) {
      if (var1 == GameSettings.Options.CLOUD_HEIGHT) {
         this.ofCloudsHeight = var2;
         this.mc.renderGlobal.resetClouds();
      }

      if (var1 == GameSettings.Options.AO_LEVEL) {
         this.ofAoLevel = var2;
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.AA_LEVEL) {
         int var6 = (int)var2;
         if (var6 > 0 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
            return;
         }

         int[] var7 = new int[]{0, 2, 4, 6, 8, 12, 16};
         this.ofAaLevel = 0;

         for (int var8 = 0; var8 < var7.length; var8++) {
            if (var6 >= var7[var8]) {
               this.ofAaLevel = var7[var8];
            }
         }

         this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
      }

      if (var1 == GameSettings.Options.AF_LEVEL) {
         int var9 = (int)var2;
         if (var9 > 1 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
            return;
         }

         this.ofAfLevel = 1;

         while (this.ofAfLevel * 2 <= var9) {
            this.ofAfLevel *= 2;
         }

         this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
         this.mc.refreshResources();
      }

      if (var1 == GameSettings.Options.MIPMAP_TYPE) {
         int var10 = (int)var2;
         this.ofMipmapType = Config.limit(var10, 0, 3);
         this.mc.refreshResources();
      }
   }

   private void setOptionValueOF(GameSettings.Options var1, int var2) {
      if (var1 == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               this.ofFogType = 2;
               if (!Config.v()) {
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

      if (var1 == GameSettings.Options.FOG_START) {
         this.ofFogStart += 0.2F;
         if (this.ofFogStart > 0.81F) {
            this.ofFogStart = 0.2F;
         }
      }

      if (var1 == GameSettings.Options.SMOOTH_FPS) {
         this.R = !this.R;
      }

      if (var1 == GameSettings.Options.SMOOTH_WORLD) {
         this.i = !this.i;
         Config.updateThreadPriorities();
      }

      if (var1 == GameSettings.Options.CLOUDS) {
         this.ofClouds++;
         if (this.ofClouds > 3) {
            this.ofClouds = 0;
         }

         this.updateRenderClouds();
         this.mc.renderGlobal.resetClouds();
      }

      if (var1 == GameSettings.Options.TREES) {
         this.ofTrees = T(this.ofTrees, OF_TREES_VALUES);
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.DROPPED_ITEMS) {
         this.ofDroppedItems++;
         if (this.ofDroppedItems > 2) {
            this.ofDroppedItems = 0;
         }
      }

      if (var1 == GameSettings.Options.RAIN) {
         this.ofRain++;
         if (this.ofRain > 3) {
            this.ofRain = 0;
         }
      }

      if (var1 == GameSettings.Options.ANIMATED_WATER) {
         this.ofAnimatedWater++;
         if (this.ofAnimatedWater == 1) {
            this.ofAnimatedWater++;
         }

         if (this.ofAnimatedWater > 2) {
            this.ofAnimatedWater = 0;
         }
      }

      if (var1 == GameSettings.Options.ANIMATED_LAVA) {
         this.ofAnimatedLava++;
         if (this.ofAnimatedLava == 1) {
            this.ofAnimatedLava++;
         }

         if (this.ofAnimatedLava > 2) {
            this.ofAnimatedLava = 0;
         }
      }

      if (var1 == GameSettings.Options.ANIMATED_FIRE) {
         this.ofAnimatedFire = !this.ofAnimatedFire;
      }

      if (var1 == GameSettings.Options.ANIMATED_PORTAL) {
         this.ofAnimatedPortal = !this.ofAnimatedPortal;
      }

      if (var1 == GameSettings.Options.ANIMATED_REDSTONE) {
         this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
      }

      if (var1 == GameSettings.Options.ANIMATED_EXPLOSION) {
         this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
      }

      if (var1 == GameSettings.Options.ANIMATED_FLAME) {
         this.ofAnimatedFlame = !this.ofAnimatedFlame;
      }

      if (var1 == GameSettings.Options.ANIMATED_SMOKE) {
         this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
      }

      if (var1 == GameSettings.Options.VOID_PARTICLES) {
         this.ofVoidParticles = !this.ofVoidParticles;
      }

      if (var1 == GameSettings.Options.WATER_PARTICLES) {
         this.ofWaterParticles = !this.ofWaterParticles;
      }

      if (var1 == GameSettings.Options.PORTAL_PARTICLES) {
         this.ofPortalParticles = !this.ofPortalParticles;
      }

      if (var1 == GameSettings.Options.POTION_PARTICLES) {
         this.ofPotionParticles = !this.ofPotionParticles;
      }

      if (var1 == GameSettings.Options.FIREWORK_PARTICLES) {
         this.ofFireworkParticles = !this.ofFireworkParticles;
      }

      if (var1 == GameSettings.Options.DRIPPING_WATER_LAVA) {
         this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
      }

      if (var1 == GameSettings.Options.ANIMATED_TERRAIN) {
         this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
      }

      if (var1 == GameSettings.Options.ANIMATED_TEXTURES) {
         this.ofAnimatedTextures = !this.ofAnimatedTextures;
      }

      if (var1 == GameSettings.Options.RAIN_SPLASH) {
         this.ofRainSplash = !this.ofRainSplash;
      }

      if (var1 == GameSettings.Options.LAGOMETER) {
         this.ZH = !this.ZH;
      }

      if (var1 == GameSettings.Options.SHOW_FPS) {
         this.ZQ = !this.ZQ;
      }

      if (var1 == GameSettings.Options.AUTOSAVE_TICKS) {
         this.ofAutoSaveTicks *= 10;
         if (this.ofAutoSaveTicks > 40000) {
            this.ofAutoSaveTicks = 40;
         }
      }

      if (var1 == GameSettings.Options.BETTER_GRASS) {
         this.ofBetterGrass++;
         if (this.ofBetterGrass > 3) {
            this.ofBetterGrass = 1;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.CONNECTED_TEXTURES) {
         this.ofConnectedTextures++;
         if (this.ofConnectedTextures > 3) {
            this.ofConnectedTextures = 1;
         }

         if (this.ofConnectedTextures != 2) {
            this.mc.refreshResources();
         }
      }

      if (var1 == GameSettings.Options.WEATHER) {
         this.lP = !this.lP;
      }

      if (var1 == GameSettings.Options.SKY) {
         this.ZC = !this.ZC;
      }

      if (var1 == GameSettings.Options.STARS) {
         this.lW = !this.lW;
      }

      if (var1 == GameSettings.Options.SUN_MOON) {
         this.d = !this.d;
      }

      if (var1 == GameSettings.Options.VIGNETTE) {
         this.ofVignette++;
         if (this.ofVignette > 2) {
            this.ofVignette = 0;
         }
      }

      if (var1 == GameSettings.Options.CHUNK_UPDATES) {
         this.ofChunkUpdates++;
         if (this.ofChunkUpdates > 5) {
            this.ofChunkUpdates = 1;
         }
      }

      if (var1 == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
         this.X = !this.X;
      }

      if (var1 == GameSettings.Options.TIME) {
         this.ofTime++;
         if (this.ofTime > 2) {
            this.ofTime = 0;
         }
      }

      if (var1 == GameSettings.Options.CLEAR_WATER) {
         this.ofClearWater = !this.ofClearWater;
         this.updateWaterOpacity();
      }

      if (var1 == GameSettings.Options.PROFILER) {
         this.lJ = !this.lJ;
      }

      if (var1 == GameSettings.Options.BETTER_SNOW) {
         this.Zi = !this.Zi;
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.SWAMP_COLORS) {
         this.lD = !this.lD;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.RANDOM_MOBS) {
         this.Zb = !this.Zb;
         RandomMobs.resetTextures();
      }

      if (var1 == GameSettings.Options.SMOOTH_BIOMES) {
         this.lx = !this.lx;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.CUSTOM_FONTS) {
         this.lo = !this.lo;
         this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
         this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
      }

      if (var1 == GameSettings.Options.CUSTOM_COLORS) {
         this.s = !this.s;
         CustomColors.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.CUSTOM_ITEMS) {
         this.ZZ = !this.ZZ;
         this.mc.refreshResources();
      }

      if (var1 == GameSettings.Options.CUSTOM_SKY) {
         this.l0 = !this.l0;
         CustomSky.L();
      }

      if (var1 == GameSettings.Options.SHOW_CAPES) {
         this.AJ = !this.AJ;
      }

      if (var1 == GameSettings.Options.NATURAL_TEXTURES) {
         this.ZB = !this.ZB;
         NaturalTextures.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.FAST_MATH) {
         this.lg = !this.lg;
         MathHelper.fastMath = this.lg;
      }

      if (var1 == GameSettings.Options.FAST_RENDER) {
         if (!this.ZE && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
            return;
         }

         this.ZE = !this.ZE;
         if (this.ZE) {
            this.mc.entityRenderer.Q();
         }

         Config.updateFramebufferSize();
      }

      if (var1 == GameSettings.Options.TRANSLUCENT_BLOCKS) {
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

      if (var1 == GameSettings.Options.LAZY_CHUNK_LOADING) {
         this.lH = !this.lH;
         Config.updateAvailableProcessors();
         if (!Config.isSingleProcessor()) {
            this.lH = false;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (var1 == GameSettings.Options.FULLSCREEN_MODE) {
         List var6 = Arrays.asList(Config.getDisplayModeNames());
         if (this.ofFullscreenMode.equals("Default")) {
            this.ofFullscreenMode = (String)var6.get(0);
         } else {
            int var7 = var6.indexOf(this.ofFullscreenMode);
            if (var7 < 0) {
               this.ofFullscreenMode = "Default";
            } else if (++var7 >= var6.size()) {
               this.ofFullscreenMode = "Default";
            } else {
               this.ofFullscreenMode = (String)var6.get(var7);
            }
         }
      }

      if (var1 == GameSettings.Options.DYNAMIC_FOV) {
         this.li = !this.li;
      }

      if (var1 == GameSettings.Options.DYNAMIC_LIGHTS) {
         this.ofDynamicLights = T(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
         DynamicLights.removeLights(this.mc.renderGlobal);
      }

      if (var1 == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
         this.Z9 = !this.Z9;
      }
   }

   private String getKeyBindingOF(GameSettings.Options var1) {
      String var5 = I18n.format(var1.getEnumString()) + ": ";
      if (var5 == null) {
         var5 = var1.getEnumString();
      }

      if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         int var13 = (int)this.getOptionFloatValue(var1);
         String var7 = I18n.format("options.renderDistance.tiny");
         byte var8 = 2;
         if (var13 >= 4) {
            var7 = I18n.format("options.renderDistance.short");
            var8 = 4;
         }

         if (var13 >= 8) {
            var7 = I18n.format("options.renderDistance.normal");
            var8 = 8;
         }

         if (var13 >= 16) {
            var7 = I18n.format("options.renderDistance.far");
            var8 = 16;
         }

         if (var13 >= 32) {
            var7 = Lang.get("of.options.renderDistance.extreme");
            var8 = 32;
         }

         int var9 = this.renderDistanceChunks - var8;
         String var10 = var7;
         if (var9 > 0) {
            var10 = var7 + "+";
         }

         return var5 + var13 + " " + var10 + "";
      } else if (var1 == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               return var5 + Lang.getFast();
            case 2:
               return var5 + Lang.getFancy();
            case 3:
               return var5 + Lang.getOff();
            default:
               return var5 + Lang.getOff();
         }
      } else {
         if (var1 == GameSettings.Options.FOG_START) {
            return var5 + this.ofFogStart;
         }

         if (var1 == GameSettings.Options.MIPMAP_TYPE) {
            switch (this.ofMipmapType) {
               case 0:
                  return var5 + Lang.get("of.options.mipmap.nearest");
               case 1:
                  return var5 + Lang.get("of.options.mipmap.linear");
               case 2:
                  return var5 + Lang.get("of.options.mipmap.bilinear");
               case 3:
                  return var5 + Lang.get("of.options.mipmap.trilinear");
               default:
                  return var5 + "of.options.mipmap.nearest";
            }
         } else {
            if (var1 == GameSettings.Options.SMOOTH_FPS) {
               return this.R ? var5 + Lang.getOn() : var5 + Lang.getOff();
            }

            if (var1 == GameSettings.Options.SMOOTH_WORLD) {
               return this.i ? var5 + Lang.getOn() : var5 + Lang.getOff();
            }

            if (var1 == GameSettings.Options.CLOUDS) {
               switch (this.ofClouds) {
                  case 1:
                     return var5 + Lang.getFast();
                  case 2:
                     return var5 + Lang.getFancy();
                  case 3:
                     return var5 + Lang.getOff();
                  default:
                     return var5 + Lang.getDefault();
               }
            } else if (var1 == GameSettings.Options.TREES) {
               switch (this.ofTrees) {
                  case 1:
                     return var5 + Lang.getFast();
                  case 2:
                     return var5 + Lang.getFancy();
                  case 3:
                  default:
                     return var5 + Lang.getDefault();
                  case 4:
                     return var5 + Lang.get("of.general.smart");
               }
            } else if (var1 == GameSettings.Options.DROPPED_ITEMS) {
               switch (this.ofDroppedItems) {
                  case 1:
                     return var5 + Lang.getFast();
                  case 2:
                     return var5 + Lang.getFancy();
                  default:
                     return var5 + Lang.getDefault();
               }
            } else if (var1 == GameSettings.Options.RAIN) {
               switch (this.ofRain) {
                  case 1:
                     return var5 + Lang.getFast();
                  case 2:
                     return var5 + Lang.getFancy();
                  case 3:
                     return var5 + Lang.getOff();
                  default:
                     return var5 + Lang.getDefault();
               }
            } else if (var1 == GameSettings.Options.ANIMATED_WATER) {
               switch (this.ofAnimatedWater) {
                  case 1:
                     return var5 + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return var5 + Lang.getOff();
                  default:
                     return var5 + Lang.getOn();
               }
            } else if (var1 == GameSettings.Options.ANIMATED_LAVA) {
               switch (this.ofAnimatedLava) {
                  case 1:
                     return var5 + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return var5 + Lang.getOff();
                  default:
                     return var5 + Lang.getOn();
               }
            } else {
               if (var1 == GameSettings.Options.ANIMATED_FIRE) {
                  return this.ofAnimatedFire ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_PORTAL) {
                  return this.ofAnimatedPortal ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_REDSTONE) {
                  return this.ofAnimatedRedstone ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_EXPLOSION) {
                  return this.ofAnimatedExplosion ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_FLAME) {
                  return this.ofAnimatedFlame ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_SMOKE) {
                  return this.ofAnimatedSmoke ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.VOID_PARTICLES) {
                  return this.ofVoidParticles ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.WATER_PARTICLES) {
                  return this.ofWaterParticles ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.PORTAL_PARTICLES) {
                  return this.ofPortalParticles ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.POTION_PARTICLES) {
                  return this.ofPotionParticles ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.FIREWORK_PARTICLES) {
                  return this.ofFireworkParticles ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.DRIPPING_WATER_LAVA) {
                  return this.ofDrippingWaterLava ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_TERRAIN) {
                  return this.ofAnimatedTerrain ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.ANIMATED_TEXTURES) {
                  return this.ofAnimatedTextures ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.RAIN_SPLASH) {
                  return this.ofRainSplash ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.LAGOMETER) {
                  return this.ZH ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.SHOW_FPS) {
                  return this.ZQ ? var5 + Lang.getOn() : var5 + Lang.getOff();
               }

               if (var1 == GameSettings.Options.AUTOSAVE_TICKS) {
                  return this.ofAutoSaveTicks <= 40
                     ? var5 + Lang.get("of.options.save.default")
                     : (
                        this.ofAutoSaveTicks <= 400
                           ? var5 + Lang.get("of.options.save.20s")
                           : (this.ofAutoSaveTicks <= 4000 ? var5 + Lang.get("of.options.save.3min") : var5 + Lang.get("of.options.save.30min"))
                     );
               }

               if (var1 == GameSettings.Options.BETTER_GRASS) {
                  switch (this.ofBetterGrass) {
                     case 1:
                        return var5 + Lang.getFast();
                     case 2:
                        return var5 + Lang.getFancy();
                     default:
                        return var5 + Lang.getOff();
                  }
               } else if (var1 == GameSettings.Options.CONNECTED_TEXTURES) {
                  switch (this.ofConnectedTextures) {
                     case 1:
                        return var5 + Lang.getFast();
                     case 2:
                        return var5 + Lang.getFancy();
                     default:
                        return var5 + Lang.getOff();
                  }
               } else {
                  if (var1 == GameSettings.Options.WEATHER) {
                     return this.lP ? var5 + Lang.getOn() : var5 + Lang.getOff();
                  }

                  if (var1 == GameSettings.Options.SKY) {
                     return this.ZC ? var5 + Lang.getOn() : var5 + Lang.getOff();
                  }

                  if (var1 == GameSettings.Options.STARS) {
                     return this.lW ? var5 + Lang.getOn() : var5 + Lang.getOff();
                  }

                  if (var1 == GameSettings.Options.SUN_MOON) {
                     return this.d ? var5 + Lang.getOn() : var5 + Lang.getOff();
                  }

                  if (var1 == GameSettings.Options.VIGNETTE) {
                     switch (this.ofVignette) {
                        case 1:
                           return var5 + Lang.getFast();
                        case 2:
                           return var5 + Lang.getFancy();
                        default:
                           return var5 + Lang.getDefault();
                     }
                  } else {
                     if (var1 == GameSettings.Options.CHUNK_UPDATES) {
                        return var5 + this.ofChunkUpdates;
                     }

                     if (var1 == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
                        return this.X ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     }

                     if (var1 == GameSettings.Options.TIME) {
                        return this.ofTime == 1
                           ? var5 + Lang.get("of.options.time.dayOnly")
                           : (this.ofTime == 2 ? var5 + Lang.get("of.options.time.nightOnly") : var5 + Lang.getDefault());
                     }

                     if (var1 == GameSettings.Options.CLEAR_WATER) {
                        return this.ofClearWater ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     }

                     if (var1 == GameSettings.Options.AA_LEVEL) {
                        String var12 = "";
                        if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                           var12 = " (" + Lang.get("of.general.restart") + ")";
                        }

                        return this.ofAaLevel == 0 ? var5 + Lang.getOff() + var12 : var5 + this.ofAaLevel + var12;
                     } else if (var1 == GameSettings.Options.AF_LEVEL) {
                        return this.ofAfLevel == 1 ? var5 + Lang.getOff() : var5 + this.ofAfLevel;
                     } else if (var1 == GameSettings.Options.PROFILER) {
                        return this.lJ ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.BETTER_SNOW) {
                        return this.Zi ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.SWAMP_COLORS) {
                        return this.lD ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.RANDOM_MOBS) {
                        return this.Zb ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.SMOOTH_BIOMES) {
                        return this.lx ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.CUSTOM_FONTS) {
                        return this.lo ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.CUSTOM_COLORS) {
                        return this.s ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.CUSTOM_SKY) {
                        return this.l0 ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.SHOW_CAPES) {
                        return this.AJ ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.CUSTOM_ITEMS) {
                        return this.ZZ ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.NATURAL_TEXTURES) {
                        return this.ZB ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.FAST_MATH) {
                        return this.lg ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.FAST_RENDER) {
                        return this.ZE ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.TRANSLUCENT_BLOCKS) {
                        return this.ofTranslucentBlocks == 1
                           ? var5 + Lang.getFast()
                           : (this.ofTranslucentBlocks == 2 ? var5 + Lang.getFancy() : var5 + Lang.getDefault());
                     } else if (var1 == GameSettings.Options.LAZY_CHUNK_LOADING) {
                        return this.lH ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.DYNAMIC_FOV) {
                        return this.li ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.DYNAMIC_LIGHTS) {
                        int var11 = r(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                        return var5 + getTranslation(KEYS_DYNAMIC_LIGHTS, var11);
                     } else if (var1 == GameSettings.Options.FULLSCREEN_MODE) {
                        return this.ofFullscreenMode.equals("Default") ? var5 + Lang.getDefault() : var5 + this.ofFullscreenMode;
                     } else if (var1 == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
                        return this.Z9 ? var5 + Lang.getOn() : var5 + Lang.getOff();
                     } else if (var1 == GameSettings.Options.FRAMERATE_LIMIT) {
                        float var6 = this.getOptionFloatValue(var1);
                        return var6 == 0.0F
                           ? var5 + Lang.get("of.options.framerateLimit.vsync")
                           : (var6 == GameSettings.Options.w(var1) ? var5 + I18n.format("options.framerateLimit.max") : var5 + (int)var6 + " fps");
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
         File var4 = this.optionsFileOF;
         if (!var4.exists()) {
            var4 = this.ZV;
         }

         if (!var4.exists()) {
            return;
         }

         BufferedReader var5 = new BufferedReader(new FileReader(var4));
         String var6 = "";

         while ((var6 = var5.readLine()) != null) {
            try {
               String[] var7 = var6.split(":");
               if (var7[0].equals("ofRenderDistanceChunks") && var7.length >= 2) {
                  this.renderDistanceChunks = Integer.valueOf(var7[1]);
                  this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 32);
               }

               if (var7[0].equals("ofFogType") && var7.length >= 2) {
                  this.ofFogType = Integer.valueOf(var7[1]);
                  this.ofFogType = Config.limit(this.ofFogType, 1, 3);
               }

               if (var7[0].equals("ofFogStart") && var7.length >= 2) {
                  this.ofFogStart = Float.valueOf(var7[1]);
                  if (this.ofFogStart < 0.2F) {
                     this.ofFogStart = 0.2F;
                  }

                  if (this.ofFogStart > 0.81F) {
                     this.ofFogStart = 0.8F;
                  }
               }

               if (var7[0].equals("ofMipmapType") && var7.length >= 2) {
                  this.ofMipmapType = Integer.valueOf(var7[1]);
                  this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
               }

               if (var7[0].equals("ofOcclusionFancy") && var7.length >= 2) {
                  this.f = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofSmoothFps") && var7.length >= 2) {
                  this.R = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofSmoothWorld") && var7.length >= 2) {
                  this.i = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAoLevel") && var7.length >= 2) {
                  this.ofAoLevel = Float.valueOf(var7[1]);
                  this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
               }

               if (var7[0].equals("ofClouds") && var7.length >= 2) {
                  this.ofClouds = Integer.valueOf(var7[1]);
                  this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                  this.updateRenderClouds();
               }

               if (var7[0].equals("ofCloudsHeight") && var7.length >= 2) {
                  this.ofCloudsHeight = Float.valueOf(var7[1]);
                  this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
               }

               if (var7[0].equals("ofTrees") && var7.length >= 2) {
                  this.ofTrees = Integer.valueOf(var7[1]);
                  this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
               }

               if (var7[0].equals("ofDroppedItems") && var7.length >= 2) {
                  this.ofDroppedItems = Integer.valueOf(var7[1]);
                  this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
               }

               if (var7[0].equals("ofRain") && var7.length >= 2) {
                  this.ofRain = Integer.valueOf(var7[1]);
                  this.ofRain = Config.limit(this.ofRain, 0, 3);
               }

               if (var7[0].equals("ofAnimatedWater") && var7.length >= 2) {
                  this.ofAnimatedWater = Integer.valueOf(var7[1]);
                  this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
               }

               if (var7[0].equals("ofAnimatedLava") && var7.length >= 2) {
                  this.ofAnimatedLava = Integer.valueOf(var7[1]);
                  this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
               }

               if (var7[0].equals("ofAnimatedFire") && var7.length >= 2) {
                  this.ofAnimatedFire = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedPortal") && var7.length >= 2) {
                  this.ofAnimatedPortal = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedRedstone") && var7.length >= 2) {
                  this.ofAnimatedRedstone = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedExplosion") && var7.length >= 2) {
                  this.ofAnimatedExplosion = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedFlame") && var7.length >= 2) {
                  this.ofAnimatedFlame = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedSmoke") && var7.length >= 2) {
                  this.ofAnimatedSmoke = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofVoidParticles") && var7.length >= 2) {
                  this.ofVoidParticles = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofWaterParticles") && var7.length >= 2) {
                  this.ofWaterParticles = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofPortalParticles") && var7.length >= 2) {
                  this.ofPortalParticles = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofPotionParticles") && var7.length >= 2) {
                  this.ofPotionParticles = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofFireworkParticles") && var7.length >= 2) {
                  this.ofFireworkParticles = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofDrippingWaterLava") && var7.length >= 2) {
                  this.ofDrippingWaterLava = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedTerrain") && var7.length >= 2) {
                  this.ofAnimatedTerrain = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAnimatedTextures") && var7.length >= 2) {
                  this.ofAnimatedTextures = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofRainSplash") && var7.length >= 2) {
                  this.ofRainSplash = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofLagometer") && var7.length >= 2) {
                  this.ZH = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofShowFps") && var7.length >= 2) {
                  this.ZQ = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofAutoSaveTicks") && var7.length >= 2) {
                  this.ofAutoSaveTicks = Integer.valueOf(var7[1]);
                  this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
               }

               if (var7[0].equals("ofBetterGrass") && var7.length >= 2) {
                  this.ofBetterGrass = Integer.valueOf(var7[1]);
                  this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
               }

               if (var7[0].equals("ofConnectedTextures") && var7.length >= 2) {
                  this.ofConnectedTextures = Integer.valueOf(var7[1]);
                  this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
               }

               if (var7[0].equals("ofWeather") && var7.length >= 2) {
                  this.lP = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofSky") && var7.length >= 2) {
                  this.ZC = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofStars") && var7.length >= 2) {
                  this.lW = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofSunMoon") && var7.length >= 2) {
                  this.d = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofVignette") && var7.length >= 2) {
                  this.ofVignette = Integer.valueOf(var7[1]);
                  this.ofVignette = Config.limit(this.ofVignette, 0, 2);
               }

               if (var7[0].equals("ofChunkUpdates") && var7.length >= 2) {
                  this.ofChunkUpdates = Integer.valueOf(var7[1]);
                  this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
               }

               if (var7[0].equals("ofChunkUpdatesDynamic") && var7.length >= 2) {
                  this.X = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofTime") && var7.length >= 2) {
                  this.ofTime = Integer.valueOf(var7[1]);
                  this.ofTime = Config.limit(this.ofTime, 0, 2);
               }

               if (var7[0].equals("ofClearWater") && var7.length >= 2) {
                  this.ofClearWater = Boolean.valueOf(var7[1]);
                  this.updateWaterOpacity();
               }

               if (var7[0].equals("ofAaLevel") && var7.length >= 2) {
                  this.ofAaLevel = Integer.valueOf(var7[1]);
                  this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
               }

               if (var7[0].equals("ofAfLevel") && var7.length >= 2) {
                  this.ofAfLevel = Integer.valueOf(var7[1]);
                  this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
               }

               if (var7[0].equals("ofProfiler") && var7.length >= 2) {
                  this.lJ = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofBetterSnow") && var7.length >= 2) {
                  this.Zi = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofSwampColors") && var7.length >= 2) {
                  this.lD = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofRandomMobs") && var7.length >= 2) {
                  this.Zb = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofSmoothBiomes") && var7.length >= 2) {
                  this.lx = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofCustomFonts") && var7.length >= 2) {
                  this.lo = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofCustomColors") && var7.length >= 2) {
                  this.s = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofCustomItems") && var7.length >= 2) {
                  this.ZZ = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofCustomSky") && var7.length >= 2) {
                  this.l0 = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofShowCapes") && var7.length >= 2) {
                  this.AJ = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofNaturalTextures") && var7.length >= 2) {
                  this.ZB = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofLazyChunkLoading") && var7.length >= 2) {
                  this.lH = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofDynamicFov") && var7.length >= 2) {
                  this.li = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofDynamicLights") && var7.length >= 2) {
                  this.ofDynamicLights = Integer.valueOf(var7[1]);
                  this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
               }

               if (var7[0].equals("ofFullscreenMode") && var7.length >= 2) {
                  this.ofFullscreenMode = var7[1];
               }

               if (var7[0].equals("ofFastMath") && var7.length >= 2) {
                  this.lg = Boolean.valueOf(var7[1]);
                  MathHelper.fastMath = this.lg;
               }

               if (var7[0].equals("ofFastRender") && var7.length >= 2) {
                  this.ZE = Boolean.valueOf(var7[1]);
               }

               if (var7[0].equals("ofTranslucentBlocks") && var7.length >= 2) {
                  this.ofTranslucentBlocks = Integer.valueOf(var7[1]);
                  this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
               }

               if (var7[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) {
                  this.ofKeyBindZoom.setKeyCode(Integer.parseInt(var7[1]));
               }
            } catch (Exception var8) {
               Config.dbg("Skipping bad option: " + var6);
               var8.printStackTrace();
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         var5.close();
      } catch (Exception var9) {
         Config.warn("Failed to load options");
         var9.printStackTrace();
      }
   }

   public void saveOfOptions() {
      try {
         PrintWriter var3 = new PrintWriter(new FileWriter(this.optionsFileOF));
         var3.println("ofRenderDistanceChunks:" + this.renderDistanceChunks);
         var3.println("ofFogType:" + this.ofFogType);
         var3.println("ofFogStart:" + this.ofFogStart);
         var3.println("ofMipmapType:" + this.ofMipmapType);
         var3.println("ofOcclusionFancy:" + this.f);
         var3.println("ofSmoothFps:" + this.R);
         var3.println("ofSmoothWorld:" + this.i);
         var3.println("ofAoLevel:" + this.ofAoLevel);
         var3.println("ofClouds:" + this.ofClouds);
         var3.println("ofCloudsHeight:" + this.ofCloudsHeight);
         var3.println("ofTrees:" + this.ofTrees);
         var3.println("ofDroppedItems:" + this.ofDroppedItems);
         var3.println("ofRain:" + this.ofRain);
         var3.println("ofAnimatedWater:" + this.ofAnimatedWater);
         var3.println("ofAnimatedLava:" + this.ofAnimatedLava);
         var3.println("ofAnimatedFire:" + this.ofAnimatedFire);
         var3.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
         var3.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
         var3.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
         var3.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
         var3.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
         var3.println("ofVoidParticles:" + this.ofVoidParticles);
         var3.println("ofWaterParticles:" + this.ofWaterParticles);
         var3.println("ofPortalParticles:" + this.ofPortalParticles);
         var3.println("ofPotionParticles:" + this.ofPotionParticles);
         var3.println("ofFireworkParticles:" + this.ofFireworkParticles);
         var3.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
         var3.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
         var3.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
         var3.println("ofRainSplash:" + this.ofRainSplash);
         var3.println("ofLagometer:" + this.ZH);
         var3.println("ofShowFps:" + this.ZQ);
         var3.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
         var3.println("ofBetterGrass:" + this.ofBetterGrass);
         var3.println("ofConnectedTextures:" + this.ofConnectedTextures);
         var3.println("ofWeather:" + this.lP);
         var3.println("ofSky:" + this.ZC);
         var3.println("ofStars:" + this.lW);
         var3.println("ofSunMoon:" + this.d);
         var3.println("ofVignette:" + this.ofVignette);
         var3.println("ofChunkUpdates:" + this.ofChunkUpdates);
         var3.println("ofChunkUpdatesDynamic:" + this.X);
         var3.println("ofTime:" + this.ofTime);
         var3.println("ofClearWater:" + this.ofClearWater);
         var3.println("ofAaLevel:" + this.ofAaLevel);
         var3.println("ofAfLevel:" + this.ofAfLevel);
         var3.println("ofProfiler:" + this.lJ);
         var3.println("ofBetterSnow:" + this.Zi);
         var3.println("ofSwampColors:" + this.lD);
         var3.println("ofRandomMobs:" + this.Zb);
         var3.println("ofSmoothBiomes:" + this.lx);
         var3.println("ofCustomFonts:" + this.lo);
         var3.println("ofCustomColors:" + this.s);
         var3.println("ofCustomItems:" + this.ZZ);
         var3.println("ofCustomSky:" + this.l0);
         var3.println("ofShowCapes:" + this.AJ);
         var3.println("ofNaturalTextures:" + this.ZB);
         var3.println("ofLazyChunkLoading:" + this.lH);
         var3.println("ofDynamicFov:" + this.li);
         var3.println("ofDynamicLights:" + this.ofDynamicLights);
         var3.println("ofFullscreenMode:" + this.ofFullscreenMode);
         var3.println("ofFastMath:" + this.lg);
         var3.println("ofFastRender:" + this.ZE);
         var3.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
         var3.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
         var3.close();
      } catch (Exception var4) {
         Config.warn("Failed to save options");
         var4.printStackTrace();
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
      this.g = 4;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.clouds = 2;
      this.b = 70.0F;
      this.Zu = 0.0F;
      this.guiScale = 0;
      this.particleSetting = 0;
      this.Z9 = true;
      this.useVbo = false;
      this.allowBlockAlternatives = true;
      this.forceUnicodeFont = false;
      this.ofFogType = 1;
      this.ofFogStart = 0.8F;
      this.ofMipmapType = 0;
      this.f = false;
      this.R = false;
      Config.updateAvailableProcessors();
      this.i = Config.isSingleProcessor();
      this.lH = Config.isSingleProcessor();
      this.lg = false;
      this.ZE = false;
      this.ofTranslucentBlocks = 0;
      this.li = true;
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
      this.ZH = false;
      this.ZQ = false;
      this.lJ = false;
      this.lP = true;
      this.ZC = true;
      this.lW = true;
      this.d = true;
      this.ofVignette = 0;
      this.ofChunkUpdates = 1;
      this.X = false;
      this.ofTime = 0;
      this.ofClearWater = false;
      this.Zi = false;
      this.ofFullscreenMode = "Default";
      this.lD = true;
      this.Zb = true;
      this.lx = true;
      this.lo = true;
      this.s = true;
      this.ZZ = true;
      this.l0 = true;
      this.AJ = true;
      this.ofConnectedTextures = 2;
      this.ZB = false;
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

   public void setAllAnimations(boolean var1) {
      int var5 = var1 ? 0 : 2;
      this.ofAnimatedWater = var5;
      this.ofAnimatedLava = var5;
      this.ofAnimatedFire = var1;
      this.ofAnimatedPortal = var1;
      this.ofAnimatedRedstone = var1;
      this.ofAnimatedExplosion = var1;
      this.ofAnimatedFlame = var1;
      this.ofAnimatedSmoke = var1;
      this.ofVoidParticles = var1;
      this.ofWaterParticles = var1;
      this.ofRainSplash = var1;
      this.ofPortalParticles = var1;
      this.ofPotionParticles = var1;
      this.ofFireworkParticles = var1;
      this.particleSetting = var1 ? 0 : 2;
      this.ofDrippingWaterLava = var1;
      this.ofAnimatedTerrain = var1;
      this.ofAnimatedTextures = var1;
   }

   private static int T(int var0, int[] var1) {
      int var5 = r(var0, var1);
      if (var5 < 0) {
         return var1[0];
      }

      if (++var5 >= var1.length) {
         var5 = 0;
      }

      return var1[var5];
   }

   private static int limit(int var0, int[] var1) {
      int var5 = r(var0, var1);
      return var5 < 0 ? var1[0] : var0;
   }

   private static int r(int var0, int[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         if (var1[var5] == var0) {
            return var5;
         }
      }

      return -1;
   }

   static {
      long var20 = ab ^ 133684629393981L;
      long var10000 = var20 ^ 842151850410685375L;
      var10000 = var20 ^ 5226438277424441941L;
      var10000 = var20 ^ 1849181583116245402L;
      var10000 = var20 ^ 842151850410685375L;
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
      private static final GameSettings.Options[] s = new GameSettings.Options[]{
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
      private static final long a = 30845914842030L;

      public static GameSettings.Options getEnumOptions(int var0) {
         for (GameSettings.Options var7 : values()) {
            if (var7.returnEnumOrdinal() == var0) {
               return var7;
            }
         }

         return null;
      }

      Options(String var3, int var4, String var5, boolean var6, boolean var7) {
         this(var3, var4, var5, var6, var7, 0.0F, 1.0F, 0.0F);
      }

      Options(String var3, int var4, String var5, boolean var6, boolean var7, float var8, float var9, float var10) {
         this.enumString = var5;
         this.enumFloat = var6;
         this.enumBoolean = var7;
         this.valueMin = var8;
         this.valueMax = var9;
         this.valueStep = var10;
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

      public void setValueMax(float var1) {
         this.valueMax = var1;
      }

      public float normalizeValue(float var1) {
         return MathHelper.clamp_float((this.snapToStepClamp(var1) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
      }

      public float denormalizeValue(float var1) {
         return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(var1, 0.0F, 1.0F));
      }

      public float snapToStepClamp(float var1) {
         var1 = this.snapToStep(var1);
         return MathHelper.clamp_float(var1, this.valueMin, this.valueMax);
      }

      public float snapToStep(float var1) {
         if (this.valueStep > 0.0F) {
            var1 = this.valueStep * Math.round(var1 / this.valueStep);
         }

         return var1;
      }

      public static float w(GameSettings.Options var0) {
         return var0.valueMax;
      }

      public static float I(GameSettings.Options var0) {
         return var0.valueMin;
      }
   }
}
