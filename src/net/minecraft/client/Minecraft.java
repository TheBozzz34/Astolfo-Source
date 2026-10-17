package net.minecraft.client;

import bypass.Astolfo;
import bypass.auth.DisconnectHandler;
import bypass.event.EventState;
import bypass.event.game.KeyPressEvent;
import bypass.event.game.MouseClickEvent;
import bypass.event.game.TickEvent;
import bypass.event.player.DeathEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.gui.screen.LoginScreen;
import bypass.loader.LoadStage;
import bypass.loader.check.ValueCheckers;
import bypass.module.exploits.HideWindow;
import bypass.module.other.FreeLook;
import bypass.render.SplashScreen;
import bypass.util.Stopwatch;
import bypass.via.ViaFabric;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IThreadListener, IPlayerUsage {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
   public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;
   public static byte[] memoryReserve = new byte[10485760];
   private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
   private final File fileResourcepacks;
   private final PropertyMap twitchDetails;
   private final PropertyMap profileProperties;
   private ServerData currentServerData;
   private TextureManager renderEngine;
   private static Minecraft theMinecraft;
   public PlayerControllerMP playerController;
   private boolean fullscreen;
   private boolean enableGLErrorChecking = true;
   private boolean hasCrashed;
   private CrashReport crashReporter;
   public int displayWidth;
   public int displayHeight;
   private boolean connectedToRealms = false;
   public Timer timer = new Timer(20.0F);
   public Timer disablerTimer = new Timer(20.0F);
   private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
   public WorldClient theWorld;
   public RenderGlobal renderGlobal;
   private RenderManager renderManager;
   private RenderItem renderItem;
   private ItemRenderer itemRenderer;
   public EntityPlayerSP thePlayer;
   private Entity renderViewEntity;
   public Entity pointedEntity;
   public EffectRenderer effectRenderer;
   public Session session;
   private boolean isGamePaused;
   public FontRenderer fontRendererObj;
   public FontRenderer standardGalacticFontRenderer;
   public GuiScreen currentScreen;
   public LoadingScreenRenderer loadingScreen;
   public EntityRenderer entityRenderer;
   private int leftClickCounter;
   private int tempDisplayWidth;
   private int tempDisplayHeight;
   private IntegratedServer theIntegratedServer;
   public GuiAchievement guiAchievement;
   public GuiIngame ingameGUI;
   public boolean skipRenderWorld;
   public MovingObjectPosition objectMouseOver;
   public GameSettings gameSettings;
   public MouseHelper mouseHelper;
   public final File mcDataDir;
   private final File fileAssets;
   private final String launchedVersion;
   private final Proxy proxy;
   private ISaveFormat saveLoader;
   private static int debugFPS;
   public int rightClickDelayTimer;
   private String serverName;
   private int serverPort;
   public boolean inGameHasFocus;
   public long systemTime = getSystemTime();
   private int joinPlayerCounter;
   public final FrameTimer frameTimer = new FrameTimer();
   public long startNanoTime = System.nanoTime();
   private final boolean jvm64bit;
   private final boolean isDemo;
   private NetworkManager myNetworkManager;
   private boolean integratedServerIsRunning;
   public final Profiler mcProfiler = new Profiler();
   private long debugCrashKeyPressTime = -1L;
   private IReloadableResourceManager mcResourceManager;
   private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
   private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
   private final DefaultResourcePack mcDefaultResourcePack;
   private ResourcePackRepository mcResourcePackRepository;
   private LanguageManager mcLanguageManager;
   private Framebuffer framebufferMc;
   private TextureMap textureMapBlocks;
   private SoundHandler mcSoundHandler;
   private MusicTicker mcMusicTicker;
   private ResourceLocation mojangLogo;
   private final MinecraftSessionService sessionService;
   private SkinManager skinManager;
   private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
   private long field_175615_aJ = 0L;
   private final Thread mcThread = Thread.currentThread();
   private ModelManager modelManager;
   private BlockRendererDispatcher blockRenderDispatcher;
   public volatile boolean running = true;
   public String debug = "";
   public boolean field_175613_B = false;
   public boolean field_175614_C = false;
   public boolean field_175611_D = false;
   public boolean renderChunksMany = true;
   public long debugUpdateTime = getSystemTime();
   public int fpsCounter;
   public long prevFrameTime = -1L;
   private String debugProfilerName = "root";
   public static boolean hypixelInHostsFile;
   public String autoLoginToken = "";
   public boolean unusedFlag = false;
   public Stopwatch loginScreenTimer = new Stopwatch();
   public static boolean forceServerReport;
   private static String lastReportedUsername = "";
   private String lastReportedServerIp = "";

   public Minecraft(GameConfiguration gameConfig) {
      theMinecraft = this;
      this.mcDataDir = gameConfig.folderInfo.mcDataDir;
      this.fileAssets = gameConfig.folderInfo.assetsDir;
      this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
      this.launchedVersion = gameConfig.gameInfo.version;
      this.twitchDetails = gameConfig.userInfo.userProperties;
      this.profileProperties = gameConfig.userInfo.profileProperties;
      this.mcDefaultResourcePack = new DefaultResourcePack(
         new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex).getResourceMap()
      );
      this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
      this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
      this.session = gameConfig.userInfo.session;
      logger.info("Setting user: " + this.session.getUsername());
      logger.info("(Session ID is " + this.session.getSessionID() + ")");
      this.isDemo = gameConfig.gameInfo.isDemo;
      this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
      this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
      this.tempDisplayWidth = gameConfig.displayInfo.width;
      this.tempDisplayHeight = gameConfig.displayInfo.height;
      this.fullscreen = gameConfig.displayInfo.fullscreen;
      this.jvm64bit = isJvm64bit();
      this.theIntegratedServer = new IntegratedServer(this);
      this.autoLoginToken = gameConfig.autoLoginToken;
      if (this.autoLoginToken == null) {
         this.autoLoginToken = "";
      }

      if (gameConfig.serverInfo.serverName != null) {
         this.serverName = gameConfig.serverInfo.serverName;
         this.serverPort = gameConfig.serverInfo.serverPort;
      }

      ImageIO.setUseCache(false);
      Bootstrap.register();
   }

   public void run() {
      this.running = true;

      try {
         this.startGame();
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Initializing game");
         crashReport.makeCategory("Initialization");
         this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashReport));
         return;
      }

      try {
         while (this.running) {
            if (this.hasCrashed && this.crashReporter != null) {
               this.displayCrashReport(this.crashReporter);
            } else {
               try {
                  this.runGameLoop();
               } catch (OutOfMemoryError outOfMemoryError) {
                  this.freeMemory();
                  this.displayGuiScreen(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }
         }

         return;
      } catch (MinecraftError minecraftError) {
      } catch (ReportedException reportedException) {
         this.addGraphicsAndWorldToCrashReport(reportedException.getCrashReport());
         this.freeMemory();
         logger.fatal("Reported exception thrown!", reportedException);
         this.displayCrashReport(reportedException.getCrashReport());
      } catch (Throwable throwable2) {
         CrashReport crashReport2 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable2));
         this.freeMemory();
         logger.fatal("Unreported exception thrown!", throwable2);
         this.displayCrashReport(crashReport2);
      } finally {
         this.shutdownMinecraftApplet();
      }
   }

   private void startGame() throws LWJGLException, IOException {
      SplashScreen.setProgress(1);
      this.gameSettings = new GameSettings(this, this.mcDataDir);
      this.defaultResourcePacks.add(this.mcDefaultResourcePack);
      this.startTimerHackThread();
      if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
         this.displayWidth = this.gameSettings.overrideWidth;
         this.displayHeight = this.gameSettings.overrideHeight;
      }

      logger.info("LWJGL Version: " + Sys.getVersion());
      this.setWindowIcon();
      this.setInitialDisplayMode();
      this.createDisplay();
      OpenGlHelper.initializeTextures();
      this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
      this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.registerMetadataSerializers();
      this.mcResourcePackRepository = new ResourcePackRepository(
         this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings
      );
      this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
      this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
      this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
      this.refreshResources();
      this.renderEngine = new TextureManager(this.mcResourceManager);
      SplashScreen.setProgress(2);
      this.mcResourceManager.registerReloadListener(this.renderEngine);
      this.drawSplashScreen(this.renderEngine);
      this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
      this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
      this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
      this.mcMusicTicker = new MusicTicker(this);
      this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
      SplashScreen.setProgress(3);
      Astolfo.INSTANCE.setFontRenderer(new FontRenderer(this.gameSettings, new ResourceLocation("client/font/bit.png"), this.renderEngine, false));
      if (this.gameSettings.language != null) {
         this.fontRendererObj.setUnicodeFlag(this.isUnicode());
         this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
      }

      this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
      this.mcResourceManager.registerReloadListener(this.fontRendererObj);
      this.mcResourceManager.registerReloadListener(Astolfo.INSTANCE.getFontRenderer());
      this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
      this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
      this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
      AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
         @Override
         public String formatString(String str) {
            try {
               return String.format(str, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
            } catch (Exception exception) {
               return "Error: " + exception.getLocalizedMessage();
            }
         }
      });
      this.mouseHelper = new MouseHelper();
      this.checkGLError("Pre startup");
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0);
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(1029);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.checkGLError("Startup");
      this.textureMapBlocks = new TextureMap("textures");
      this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
      this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
      this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
      this.modelManager = new ModelManager(this.textureMapBlocks);
      this.mcResourceManager.registerReloadListener(this.modelManager);
      this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
      this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
      SplashScreen.setProgress(4);
      this.itemRenderer = new ItemRenderer(this);
      this.mcResourceManager.registerReloadListener(this.renderItem);
      this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
      this.mcResourceManager.registerReloadListener(this.entityRenderer);
      this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
      this.renderGlobal = new RenderGlobal(this);
      this.mcResourceManager.registerReloadListener(this.renderGlobal);
      this.guiAchievement = new GuiAchievement(this);
      GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
      this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
      SplashScreen.setProgress(5);
      this.checkGLError("Post startup");
      ValueCheckers.INSTANCE.initialize();
      Astolfo.INSTANCE.load();
      SplashScreen.setProgress(6);
      this.ingameGUI = new GuiIngame(this);
      this.displayGuiScreen(new LoginScreen());
      this.renderEngine.deleteTexture(this.mojangLogo);
      this.mojangLogo = null;
      SplashScreen.setProgress(7);
      this.loadingScreen = new LoadingScreenRenderer(this);
      if (this.gameSettings.fullScreen && !this.fullscreen) {
         this.toggleFullscreen();
      }

      try {
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
      } catch (OpenGLException openGLException) {
         this.gameSettings.enableVsync = false;
         this.gameSettings.saveOptions();
      }

      this.renderGlobal.makeEntityOutlineShader();
   }

   private void registerMetadataSerializers() {
      this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
   }

   private void createDisplay() throws LWJGLException {
      Display.setResizable(true);
      Display.setTitle("Minecraft 1.8.8");

      try {
         Display.create(new PixelFormat().withDepthBits(24));
      } catch (LWJGLException lwjglException) {
         logger.error("Couldn't set pixel format", lwjglException);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException interruptedException) {
         }

         if (this.fullscreen) {
            this.updateDisplayMode();
         }

         Display.create();
      }
   }

   private void setInitialDisplayMode() throws LWJGLException {
      if (this.fullscreen) {
         Display.setFullscreen(true);
         DisplayMode displayMode = Display.getDisplayMode();
         this.displayWidth = Math.max(1, displayMode.getWidth());
         this.displayHeight = Math.max(1, displayMode.getHeight());
      } else {
         Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
      }
   }

   private void setWindowIcon() {
      Util.EnumOS enumOS = Util.getOSType();
      if (enumOS != Util.EnumOS.OSX) {
         InputStream inputStream = null;
         InputStream inputStream2 = null;

         try {
            inputStream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
            inputStream2 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
            if (inputStream != null && inputStream2 != null) {
               Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(inputStream), this.readImageToBuffer(inputStream2)});
            }
         } catch (IOException ioException) {
            logger.error("Couldn't set icon", ioException);
         } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStream2);
         }
      }
   }

   private static boolean isJvm64bit() {
      String[] strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for (String string : strings) {
         String string2 = System.getProperty(string);
         if (string2 != null && string2.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public Framebuffer getFramebuffer() {
      return this.framebufferMc;
   }

   public String getVersion() {
      return this.launchedVersion;
   }

   private void startTimerHackThread() {
      Thread thread2 = new Thread("Timer hack thread") {
         @Override
         public void run() {
            while (Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException interruptedException) {
               }
            }
         }
      };
      thread2.setDaemon(true);
      thread2.start();
   }

   public void crashed(CrashReport crash) {
      this.hasCrashed = true;
      this.crashReporter = crash;
   }

   public void displayCrashReport(CrashReport crashReportIn) {
      File file = new File(getMinecraft().mcDataDir, "crash-reports");
      File file2 = new File(file, "astolfo-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".zar");
      Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
      if (crashReportIn.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
         System.exit(-1);
      } else if (crashReportIn.saveToFile(file2)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }
   }

   public boolean isUnicode() {
      return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
   }

   public void refreshResources() {
      ArrayList arrayList = Lists.newArrayList(this.defaultResourcePacks);

      for (ResourcePackRepository.Entry entry : this.mcResourcePackRepository.getRepositoryEntries()) {
         arrayList.add(entry.getResourcePack());
      }

      if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
         arrayList.add(this.mcResourcePackRepository.getResourcePackInstance());
      }

      try {
         this.mcResourceManager.reloadResources(arrayList);
      } catch (RuntimeException runtimeException) {
         logger.info("Caught error stitching, removing all assigned resourcepacks", runtimeException);
         arrayList.clear();
         arrayList.addAll(this.defaultResourcePacks);
         this.mcResourcePackRepository.setRepositories(Collections.emptyList());
         this.mcResourceManager.reloadResources(arrayList);
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.incompatibleResourcePacks.clear();
         this.gameSettings.saveOptions();
      }

      this.mcLanguageManager.parseLanguageMetadata(arrayList);
      if (this.renderGlobal != null) {
         this.renderGlobal.loadRenderers();
      }
   }

   private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
      BufferedImage bufferedImage = ImageIO.read(imageStream);
      int[] ints = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[])null, 0, bufferedImage.getWidth());
      ByteBuffer byteBuffer = ByteBuffer.allocate(4 * ints.length);

      for (int i : ints) {
         byteBuffer.putInt(i << 8 | i >> 24 & 0xFF);
      }

      ((Buffer)byteBuffer).flip();
      return byteBuffer;
   }

   private void updateDisplayMode() throws LWJGLException {
      HashSet hashSet = Sets.newHashSet();
      Collections.addAll(hashSet, Display.getAvailableDisplayModes());
      DisplayMode displayMode = Display.getDesktopDisplayMode();
      if (!hashSet.contains(displayMode) && Util.getOSType() == Util.EnumOS.OSX) {
         for (DisplayMode displayMode2 : macDisplayModes) {
            boolean bl = true;

            for (Object object : hashSet) {
               DisplayMode displayMode3 = (DisplayMode)object;
               if (displayMode3.getBitsPerPixel() == 32 && displayMode3.getWidth() == displayMode2.getWidth() && displayMode3.getHeight() == displayMode2.getHeight()) {
                  bl = false;
                  break;
               }
            }

            if (!bl) {
               for (Object object2 : hashSet) {
                  DisplayMode displayMode4 = (DisplayMode)object2;
                  if (displayMode4.getBitsPerPixel() == 32 && displayMode4.getWidth() == displayMode2.getWidth() / 2 && displayMode4.getHeight() == displayMode2.getHeight() / 2) {
                     displayMode = displayMode4;
                     break;
                  }
               }
            }
         }
      }

      Display.setDisplayMode(displayMode);
      this.displayWidth = displayMode.getWidth();
      this.displayHeight = displayMode.getHeight();
   }

   private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
      SplashScreen.render(textureManagerInstance);
   }

   public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha) {
      float f = 0.00390625F;
      float g = 0.00390625F;
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(posX, posY + height, 0.0).tex(texU * f, (texV + height) * g).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(posX + width, posY + height, 0.0).tex((texU + width) * f, (texV + height) * g).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(posX + width, posY, 0.0).tex((texU + width) * f, texV * g).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(posX, posY, 0.0).tex(texU * f, texV * g).color(red, green, blue, alpha).endVertex();
      Tessellator.getInstance().draw();
   }

   public ISaveFormat getSaveLoader() {
      return this.saveLoader;
   }

   public void displayGuiScreen(GuiScreen guiScreenIn) {
      if (this.currentScreen != null) {
         this.currentScreen.onGuiClosed();
      }

      if (guiScreenIn == null && this.theWorld == null) {
         guiScreenIn = new GuiMainMenu();
      } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
         Astolfo.INSTANCE.getEventBus().post(new DeathEvent());
         guiScreenIn = new GuiGameOver();
      }

      if (guiScreenIn instanceof GuiMainMenu) {
         this.gameSettings.showDebugInfo = false;
         this.ingameGUI.getChatGUI().clearChatMessages();
      }

      this.currentScreen = guiScreenIn;
      if (guiScreenIn != null) {
         this.setIngameNotInFocus();
         ScaledResolution scaledResolution = new ScaledResolution(this);
         int i = scaledResolution.getScaledWidth();
         int j = scaledResolution.getScaledHeight();
         guiScreenIn.setWorldAndResolution(this, i, j);
         this.skipRenderWorld = false;
      } else {
         this.mcSoundHandler.resumeSounds();
         this.setIngameFocus();
      }
   }

   public static int getLoadState() {
      int i = 0;

      for (LoadStage loadStage : Astolfo.INSTANCE.getLoaderGuard().getStageList().getStageContainer().getOwnedContainer().getValue()) {
         i += loadStage.getState();
      }

      return i;
   }

   private void checkGLError(String message) {
      if (this.enableGLErrorChecking) {
         int i = GL11.glGetError();
         if (i != 0) {
            String string = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + message);
            logger.error(i + ": " + string);
         }
      }
   }

   public void shutdownMinecraftApplet() {
      try {
         logger.info("Stopping!");

         try {
            this.loadWorld((WorldClient)null);
         } catch (Throwable throwable) {
         }

         this.mcSoundHandler.unloadSounds();
      } finally {
         Display.destroy();
         if (!this.hasCrashed) {
            System.exit(0);
         }
      }

      System.gc();
   }

   private void runGameLoop() throws IOException {
      long l = System.nanoTime();
      this.mcProfiler.startSection("root");
      if (Display.isCreated() && Display.isCloseRequested()) {
         this.shutdown();
      }

      if (this.isGamePaused && this.theWorld != null) {
         float f = this.timer.renderPartialTicks;
         this.timer.updateTimer();
         this.timer.renderPartialTicks = f;
      } else {
         this.timer.updateTimer();
      }

      this.mcProfiler.startSection("scheduledExecutables");
      synchronized (this.scheduledTasks) {
         while (!this.scheduledTasks.isEmpty()) {
            Util.runTask(this.scheduledTasks.poll(), logger);
         }
      }

      this.mcProfiler.endSection();
      long m = System.nanoTime();
      this.mcProfiler.startSection("tick");

      for (int i = 0; i < this.timer.elapsedTicks; i++) {
         this.runTick();
      }

      this.mcProfiler.endStartSection("preRenderErrors");
      long n = System.nanoTime() - m;
      this.checkGLError("Pre render");
      this.mcProfiler.endStartSection("sound");
      this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
      this.mcProfiler.endSection();
      this.mcProfiler.startSection("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640);
      this.framebufferMc.bindFramebuffer(true);
      this.mcProfiler.startSection("display");
      GlStateManager.enableTexture2D();
      if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
         this.gameSettings.thirdPersonView = 0;
      }

      this.mcProfiler.endSection();
      if (!this.skipRenderWorld) {
         this.mcProfiler.endStartSection("gameRenderer");
         this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, l);
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
      if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
         if (!this.mcProfiler.profilingEnabled) {
            this.mcProfiler.clearProfiling();
         }

         this.mcProfiler.profilingEnabled = true;
         this.displayDebugInfo(n);
      } else {
         this.mcProfiler.profilingEnabled = false;
         this.prevFrameTime = System.nanoTime();
      }

      this.guiAchievement.updateAchievementWindow();
      this.framebufferMc.unbindFramebuffer();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
      GlStateManager.popMatrix();
      this.mcProfiler.startSection("root");
      this.updateDisplay();
      Thread.yield();
      this.mcProfiler.startSection("stream");
      this.mcProfiler.startSection("update");
      this.mcProfiler.endStartSection("submit");
      this.mcProfiler.endSection();
      this.mcProfiler.endSection();
      this.checkGLError("Post render");
      this.fpsCounter++;
      this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
      long l2 = System.nanoTime();
      this.frameTimer.addFrame(l2 - this.startNanoTime);
      this.startNanoTime = l2;

      while (getSystemTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.debug = String.format(
            "%d fps (%d chunk update%s) T: %s%s%s%s%s",
            debugFPS,
            RenderChunk.renderChunksUpdated,
            RenderChunk.renderChunksUpdated != 1 ? "s" : "",
            this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate,
            this.gameSettings.enableVsync ? " vsync" : "",
            this.gameSettings.fancyGraphics ? "" : " fast",
            this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"),
            OpenGlHelper.useVbo() ? " vbo" : ""
         );
         RenderChunk.renderChunksUpdated = 0;
         this.debugUpdateTime += 1000L;
         this.fpsCounter = 0;
         this.usageSnooper.addMemoryStatsToSnooper();
         if (!this.usageSnooper.isSnooperRunning()) {
            this.usageSnooper.startSnooper();
         }
      }

      if (this.isFramerateLimitBelowMax()) {
         this.mcProfiler.startSection("fpslimit_wait");
         Display.sync(this.getLimitFramerate());
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
   }

   public void updateDisplay() {
      this.mcProfiler.startSection("display_update");
      Display.update();
      this.mcProfiler.endSection();
      this.checkWindowResize();
   }

   public void checkWindowResize() {
      if (!this.fullscreen && Display.wasResized()) {
         int i = this.displayWidth;
         int j = this.displayHeight;
         this.displayWidth = Display.getWidth();
         this.displayHeight = Display.getHeight();
         if (this.displayWidth != i || this.displayHeight != j) {
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }

            this.resize(this.displayWidth, this.displayHeight);
         }
      }
   }

   public int getLimitFramerate() {
      return this.theWorld == null && this.currentScreen != null ? 60 : this.gameSettings.limitFramerate;
   }

   public boolean isFramerateLimitBelowMax() {
      return this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
   }

   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.renderGlobal.deleteAllDisplayLists();
      } catch (Throwable throwable) {
      }

      try {
         System.gc();
         this.loadWorld((WorldClient)null);
      } catch (Throwable throwable2) {
      }

      System.gc();
   }

   private void updateDebugProfilerName(int keyCount) {
      List list = this.mcProfiler.getProfilingData(this.debugProfilerName);
      if (list != null && !list.isEmpty()) {
         Profiler.Result result = (Profiler.Result)list.remove(0);
         if (keyCount == 0) {
            if (result.field_76331_c.length() > 0) {
               int i = this.debugProfilerName.lastIndexOf(".");
               if (i >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, i);
               }
            }
         } else {
            keyCount--;
            if (keyCount < list.size() && !((Profiler.Result)list.get(keyCount)).field_76331_c.equals("unspecified")) {
               if (this.debugProfilerName.length() > 0) {
                  this.debugProfilerName = this.debugProfilerName + ".";
               }

               this.debugProfilerName = this.debugProfilerName + ((Profiler.Result)list.get(keyCount)).field_76331_c;
            }
         }
      }
   }

   private void displayDebugInfo(long elapsedTicksTime) {
      if (this.mcProfiler.profilingEnabled) {
         List list = this.mcProfiler.getProfilingData(this.debugProfilerName);
         Profiler.Result result = (Profiler.Result)list.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         short s = 160;
         int i = this.displayWidth - s - 10;
         int j = this.displayHeight - s * 2;
         GlStateManager.enableBlend();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         worldRenderer.pos(i - s * 1.1F, j - s * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         worldRenderer.pos(i - s * 1.1F, j + s * 2, 0.0).color(200, 0, 0, 0).endVertex();
         worldRenderer.pos(i + s * 1.1F, j + s * 2, 0.0).color(200, 0, 0, 0).endVertex();
         worldRenderer.pos(i + s * 1.1F, j - s * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         tessellator.draw();
         GlStateManager.disableBlend();
         double d = 0.0;

         for (int k = 0; k < list.size(); k++) {
            Profiler.Result result2 = (Profiler.Result)list.get(k);
            int l = MathHelper.floor_double(result2.field_76332_a / 4.0) + 1;
            worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int m = result2.getColor();
            int n = m >> 16 & 0xFF;
            int i2 = m >> 8 & 0xFF;
            int i3 = m & 0xFF;
            worldRenderer.pos(i, j, 0.0).color(n, i2, i3, 255).endVertex();

            for (int i4 = l; i4 >= 0; i4--) {
               float f = (float)((d + result2.field_76332_a * i4 / l) * Math.PI * 2.0 / 100.0);
               float g = MathHelper.sin(f) * s;
               float h = MathHelper.cos(f) * s * 0.5F;
               worldRenderer.pos(i + g, j - h, 0.0).color(n, i2, i3, 255).endVertex();
            }

            tessellator.draw();
            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i5 = l; i5 >= 0; i5--) {
               float f2 = (float)((d + result2.field_76332_a * i5 / l) * Math.PI * 2.0 / 100.0);
               float f3 = MathHelper.sin(f2) * s;
               float f4 = MathHelper.cos(f2) * s * 0.5F;
               worldRenderer.pos(i + f3, j - f4, 0.0).color(n >> 1, i2 >> 1, i3 >> 1, 255).endVertex();
               worldRenderer.pos(i + f3, j - f4 + 10.0F, 0.0).color(n >> 1, i2 >> 1, i3 >> 1, 255).endVertex();
            }

            tessellator.draw();
            d += result2.field_76332_a;
         }

         DecimalFormat decimalFormat = new DecimalFormat("##0.00");
         GlStateManager.enableTexture2D();
         String string = "";
         if (!result.field_76331_c.equals("unspecified")) {
            string = string + "[0] ";
         }

         if (result.field_76331_c.length() == 0) {
            string = string + "ROOT ";
         } else {
            string = string + result.field_76331_c + " ";
         }

         int i6 = 16777215;
         this.fontRendererObj.drawStringWithShadow(string, i - s, j - s / 2 - 16, i6);
         this.fontRendererObj
            .drawStringWithShadow(
               string = decimalFormat.format(result.field_76330_b) + "%", i + s - this.fontRendererObj.getStringWidth(string), j - s / 2 - 16, i6
            );

         for (int i7 = 0; i7 < list.size(); i7++) {
            Profiler.Result result3 = (Profiler.Result)list.get(i7);
            String string2 = "";
            if (result3.field_76331_c.equals("unspecified")) {
               string2 = string2 + "[?] ";
            } else {
               string2 = string2 + "[" + (i7 + 1) + "] ";
            }

            string2 = string2 + result3.field_76331_c;
            this.fontRendererObj.drawStringWithShadow(string2, i - s, j + s / 2 + i7 * 8 + 20, result3.getColor());
            this.fontRendererObj
               .drawStringWithShadow(
                  string2 = decimalFormat.format(result3.field_76332_a) + "%",
                  i + s - 50 - this.fontRendererObj.getStringWidth(string2),
                  j + s / 2 + i7 * 8 + 20,
                  result3.getColor()
               );
            this.fontRendererObj
               .drawStringWithShadow(
                  string2 = decimalFormat.format(result3.field_76330_b) + "%",
                  i + s - this.fontRendererObj.getStringWidth(string2),
                  j + s / 2 + i7 * 8 + 20,
                  result3.getColor()
               );
         }
      }
   }

   public void shutdown() {
      this.running = false;
   }

   public void setIngameFocus() {
      if (Display.isActive() && !this.inGameHasFocus) {
         this.inGameHasFocus = true;
         this.mouseHelper.grabMouseCursor();
         this.displayGuiScreen((GuiScreen)null);
         this.leftClickCounter = 10000;
      }
   }

   public void setIngameNotInFocus() {
      if (this.inGameHasFocus) {
         KeyBinding.unPressAllKeys();
         this.inGameHasFocus = false;
         this.mouseHelper.ungrabMouseCursor();
      }
   }

   public void displayInGameMenu() {
      if (this.currentScreen == null) {
         this.displayGuiScreen(new GuiIngameMenu());
         if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
            this.mcSoundHandler.pauseSounds();
         }
      }
   }

   private void sendClickBlockToController(boolean leftClick) {
      if (!leftClick) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
         if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.objectMouseOver.getBlockPos();
            if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air
               && this.playerController.onPlayerDamageBlock(blockPos, this.objectMouseOver.sideHit)) {
               this.effectRenderer.addBlockHitEffects(blockPos, this.objectMouseOver.sideHit);
               this.thePlayer.swingItem();
            }
         } else {
            this.playerController.resetBlockRemoving();
         }
      }
   }

   public void clickMouse() {
      if (this.leftClickCounter <= 0) {
         boolean bl = ViaFabric.INSTANCE.isNative();
         if (bl) {
            this.thePlayer.swingItem();
         }

         if (this.objectMouseOver == null) {
            logger.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.isNotCreative()) {
               this.leftClickCounter = 10;
            }
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                  break;
               case BLOCK:
                  BlockPos blockPos = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                     this.playerController.clickBlock(blockPos, this.objectMouseOver.sideHit);
                     break;
                  }
               case MISS:
                  if (this.playerController.isNotCreative() && !bl) {
                     this.leftClickCounter = 10;
                  }
               default:
                  if (this.playerController.isNotCreative()) {
                     this.leftClickCounter = 10;
                  }
            }

            if (!bl) {
               this.thePlayer.swingItem();
            }
         }
      }

      this.leftClickCounter = 0;
   }

   public void rightClickMouse() {
      if (!this.playerController.getIsHittingBlock()) {
         this.rightClickDelayTimer = 4;
         boolean bl = true;
         ItemStack itemStack = this.thePlayer.inventory.getCurrentItem();
         if (this.objectMouseOver == null) {
            logger.warn("Null returned as 'hitResult', this shouldn't happen!");
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                     bl = false;
                  } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                     bl = false;
                  }
                  break;
               case BLOCK:
                  BlockPos blockPos = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                     int i = itemStack != null ? itemStack.stackSize : 0;
                     if (this.playerController
                        .onPlayerRightClick(this.thePlayer, this.theWorld, itemStack, blockPos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                        bl = false;
                        this.thePlayer.swingItem();
                     }

                     if (itemStack == null) {
                        return;
                     }

                     if (itemStack.stackSize == 0) {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                     } else if (itemStack.stackSize != i || this.playerController.isInCreativeMode()) {
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                     }
                  }
            }
         }

         if (bl) {
            ItemStack itemStack2 = this.thePlayer.inventory.getCurrentItem();
            if (itemStack2 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemStack2)) {
               this.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
         }
      }
   }

   public void toggleFullscreen() {
      try {
         this.fullscreen = !this.fullscreen;
         this.gameSettings.fullScreen = this.fullscreen;
         if (this.fullscreen) {
            this.updateDisplayMode();
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }
         } else {
            Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
            this.displayWidth = this.tempDisplayWidth;
            this.displayHeight = this.tempDisplayHeight;
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }
         }

         if (this.currentScreen != null) {
            this.resize(this.displayWidth, this.displayHeight);
         } else {
            this.updateFramebufferSize();
         }

         Display.setResizable(true);
         Display.setFullscreen(this.fullscreen);
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
         this.updateDisplay();
      } catch (Exception exception) {
         logger.error("Couldn't toggle fullscreen", exception);
      }
   }

   private void resize(int width, int height) {
      this.displayWidth = Math.max(1, width);
      this.displayHeight = Math.max(1, height);
      if (this.currentScreen != null) {
         ScaledResolution scaledResolution = new ScaledResolution(this);
         this.currentScreen.onResize(this, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
      }

      this.loadingScreen = new LoadingScreenRenderer(this);
      this.updateFramebufferSize();
   }

   private void updateFramebufferSize() {
      this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
      if (this.entityRenderer != null) {
         this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
      }
   }

   public MusicTicker getMusicTicker() {
      return this.mcMusicTicker;
   }

   public void runTick() throws IOException {
      if (Astolfo.INSTANCE.getEventBus() != null) {
         Astolfo.INSTANCE.getEventBus().post(new TickEvent(EventState.PRE));
      }

      if (this.rightClickDelayTimer > 0) {
         this.rightClickDelayTimer--;
      }

      this.mcProfiler.startSection("gui");
      if (!this.isGamePaused) {
         this.ingameGUI.updateTick();
      }

      this.mcProfiler.endSection();
      this.entityRenderer.getMouseOver(1.0F);
      this.mcProfiler.startSection("gameMode");
      if (!this.isGamePaused && this.theWorld != null) {
         this.playerController.updateController();
      }

      this.mcProfiler.endStartSection("textures");

      try {
         if (this.thePlayer != null && this.theWorld != null && this.currentScreen == null) {
            if (Astolfo.INSTANCE.getModuleManager().get(FreeLook.class).isEnabled() && (!FreeLook.mouseWheel.getValue() || Mouse.isButtonDown(2))) {
               if (!EntityRenderer.freelookActive) {
                  EntityRenderer.freelookPitch = this.thePlayer.rotationPitch;
                  EntityRenderer.freelookYaw = this.thePlayer.rotationYaw;
               }

               EntityRenderer.freelookActive = true;
               this.gameSettings.thirdPersonView = 1;
            } else if (EntityRenderer.freelookActive) {
               EntityRenderer.freelookActive = false;
               this.gameSettings.thirdPersonView = 0;
            }
         }
      } catch (Exception exception) {
      }

      if (!this.isGamePaused) {
         this.renderEngine.tick();
      }

      if (this.currentScreen == null && this.thePlayer != null) {
         if (this.thePlayer.getHealth() <= 0.0F) {
            this.displayGuiScreen((GuiScreen)null);
         } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
            this.displayGuiScreen(new GuiSleepMP());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
         this.displayGuiScreen((GuiScreen)null);
      }

      if (this.currentScreen != null) {
         this.leftClickCounter = 10000;
      }

      if (this.currentScreen != null) {
         try {
            this.currentScreen.handleInput();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Updating screen events");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Affected screen");
            crashReportCategory.addCrashSectionCallable("Screen name", new Minecraft.asm());
            throw new ReportedException(crashReport);
         }

         if (this.currentScreen != null) {
            try {
               if (this.currentScreen instanceof LoginScreen) {
                  this.loginScreenTimer.reset();
               }

               this.currentScreen.updateScreen();
            } catch (Throwable throwable2) {
               CrashReport crashReport2 = CrashReport.makeCrashReport(throwable2, "Ticking screen");
               CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Affected screen");
               crashReportCategory2.addCrashSectionCallable("Screen name", new Minecraft.ajr());
               throw new ReportedException(crashReport2);
            }
         }
      }

      if (this.currentScreen == null || this.currentScreen.allowUserInput) {
         this.mcProfiler.endStartSection("mouse");

         while (Mouse.next()) {
            int i = Mouse.getEventButton();
            KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
               Astolfo.INSTANCE.getEventBus().post(new MouseClickEvent(i));
               if (this.thePlayer.isSpectator() && i == 2) {
                  this.ingameGUI.getSpectatorGui().func_175261_b();
               } else {
                  KeyBinding.onTick(i - 100);
               }
            }

            long l = getSystemTime() - this.systemTime;
            if (l <= 200L) {
               int j = Mouse.getEventDWheel();
               if (j != 0) {
                  if (this.thePlayer.isSpectator()) {
                     j = j < 0 ? -1 : 1;
                     if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                        this.ingameGUI.getSpectatorGui().func_175259_b(-j);
                     } else {
                        float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + j * 0.005F, 0.0F, 0.2F);
                        this.thePlayer.capabilities.setFlySpeed(f);
                     }
                  } else {
                     this.thePlayer.inventory.changeCurrentItem(j);
                  }
               }

               if (this.currentScreen == null) {
                  if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                     this.setIngameFocus();
                  }
               } else if (this.currentScreen != null) {
                  this.currentScreen.handleMouseInput();
               }
            }
         }

         if (this.leftClickCounter > 0) {
            this.leftClickCounter--;
         }

         this.mcProfiler.endStartSection("keyboard");

         while (Keyboard.next()) {
            int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());
            if (Keyboard.getEventKeyState()) {
               KeyBinding.onTick(k);
            }

            if (this.debugCrashKeyPressTime > 0L) {
               if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                  throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
               }

               if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                  this.debugCrashKeyPressTime = -1L;
               }
            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
               this.debugCrashKeyPressTime = getSystemTime();
            }

            this.dispatchKeypresses();
            if (Keyboard.getEventKeyState()) {
               if (k == 62 && this.entityRenderer != null) {
                  this.entityRenderer.switchUseShader();
               }

               Astolfo.INSTANCE.getEventBus().post(new KeyPressEvent(k));
               if (this.currentScreen != null) {
                  this.currentScreen.handleKeyboardEvent();
               } else {
                  if (k == 1) {
                     this.displayInGameMenu();
                  }

                  if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null) {
                     this.ingameGUI.getChatGUI().clearChatMessages();
                  }

                  if (k == 31 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (k == 17 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 18 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 47 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 38 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 22 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 20 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (k == 33 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                  }

                  if (k == 30 && Keyboard.isKeyDown(61)) {
                     this.renderGlobal.loadRenderers();
                  }

                  if (k == 35 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                     this.gameSettings.saveOptions();
                  }

                  if (k == 48 && Keyboard.isKeyDown(61)) {
                     this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                  }

                  if (k == 25 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                     this.gameSettings.saveOptions();
                  }

                  if (k == 59) {
                     this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                  }

                  if (k == 61) {
                     this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                     this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                     this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
                  }

                  if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                     this.gameSettings.thirdPersonView++;
                     if (this.gameSettings.thirdPersonView > 2) {
                        this.gameSettings.thirdPersonView = 0;
                     }

                     if (this.gameSettings.thirdPersonView == 0) {
                        this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                     } else if (this.gameSettings.thirdPersonView == 1) {
                        this.entityRenderer.loadEntityShader((Entity)null);
                     }

                     this.renderGlobal.setDisplayListEntitiesDirty();
                  }

                  if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                     this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                  }
               }

               if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                  if (k == 11) {
                     this.updateDebugProfilerName(0);
                  }

                  for (int m = 0; m < 9; m++) {
                     if (k == 2 + m) {
                        this.updateDebugProfilerName(m + 1);
                     }
                  }
               }
            }
         }

         for (int n = 0; n < 9; n++) {
            if (this.gameSettings.keyBindsHotbar[n].isPressed()) {
               if (this.thePlayer.isSpectator()) {
                  this.ingameGUI.getSpectatorGui().func_175260_a(n);
               } else {
                  this.thePlayer.inventory.currentItem = n;
               }
            }
         }

         boolean bl = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

         while (this.gameSettings.keyBindInventory.isPressed()) {
            if (this.playerController.isRidingHorse()) {
               this.thePlayer.v_void();
            } else if (Astolfo.INSTANCE.getModuleManager().get(HideWindow.class).isEnabled() && HideWindow.hiddenChest != null) {
               HideWindow.hiddenChest.inventorySlots = this.thePlayer.inventoryContainer;
               this.thePlayer.displayGUIChest(HideWindow.hiddenChest.lowerChestInventory);
               this.thePlayer.openContainer.windowId = HideWindow.hiddenWindowId;
            } else {
               this.displayGuiScreen(new GuiInventory(this.thePlayer));
               this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
         }

         while (this.gameSettings.keyBindDrop.isPressed()) {
            if (!this.thePlayer.isSpectator()) {
               this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }
         }

         while (this.gameSettings.keyBindChat.isPressed() && bl) {
            this.displayGuiScreen(new GuiChat());
         }

         if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && bl) {
            this.displayGuiScreen(new GuiChat("/"));
         }

         if (this.thePlayer.isUsingItem()) {
            if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
               this.playerController.onStoppedUsingItem(this.thePlayer);
            }

            while (this.gameSettings.keyBindAttack.isPressed()) {
            }

            while (this.gameSettings.keyBindUseItem.isPressed()) {
            }

            while (this.gameSettings.keyBindPickBlock.isPressed()) {
            }
         } else {
            while (this.gameSettings.keyBindAttack.isPressed()) {
               this.clickMouse();
            }

            while (this.gameSettings.keyBindUseItem.isPressed()) {
               this.rightClickMouse();
            }

            while (this.gameSettings.keyBindPickBlock.isPressed()) {
               this.middleClickMouse();
            }
         }

         if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
            this.rightClickMouse();
         }

         this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
      }

      if (this.theWorld != null) {
         if (this.thePlayer != null) {
            this.joinPlayerCounter++;
            if (this.joinPlayerCounter == 30) {
               this.joinPlayerCounter = 0;
               this.theWorld.joinEntityInSurroundings(this.thePlayer);
            }
         }

         this.mcProfiler.endStartSection("gameRenderer");
         if (!this.isGamePaused) {
            this.entityRenderer.updateRenderer();
         }

         this.mcProfiler.endStartSection("levelRenderer");
         if (!this.isGamePaused) {
            this.renderGlobal.updateClouds();
         }

         this.mcProfiler.endStartSection("level");
         if (!this.isGamePaused) {
            if (this.theWorld.getLastLightningBolt() > 0) {
               this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
            }

            this.theWorld.updateEntities();
         }
      } else if (this.entityRenderer.Q_boolean()) {
         this.entityRenderer.stopUseShader();
      }

      if (!this.isGamePaused) {
         this.mcMusicTicker.update();
         this.mcSoundHandler.update();
      }

      if (this.theWorld != null) {
         if (!this.isGamePaused) {
            this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

            try {
               this.theWorld.tick();
            } catch (Throwable throwable3) {
               CrashReport crashReport3 = CrashReport.makeCrashReport(throwable3, "Exception in world tick");
               if (this.theWorld == null) {
                  CrashReportCategory crashReportCategory3 = crashReport3.makeCategory("Affected level");
                  crashReportCategory3.addCrashSection("Problem", "Level is null!");
               } else {
                  this.theWorld.addWorldInfoToCrashReport(crashReport3);
               }

               throw new ReportedException(crashReport3);
            }
         }

         this.mcProfiler.endStartSection("animateTick");
         if (!this.isGamePaused && this.theWorld != null) {
            this.theWorld
               .doVoidFogParticles(
                  MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ)
               );
         }

         this.mcProfiler.endStartSection("particles");
         if (!this.isGamePaused) {
            this.effectRenderer.updateEffects();
         }
      } else if (this.myNetworkManager != null) {
         this.mcProfiler.endStartSection("pendingConnection");
         this.myNetworkManager.processReceivedPackets();
      }

      this.mcProfiler.endSection();
      this.systemTime = getSystemTime();
      if (Astolfo.INSTANCE.getEventBus() != null) {
         Astolfo.INSTANCE.getEventBus().post(new TickEvent(EventState.POST));
      }
   }

   public void setBorderlessFullscreen(boolean bl) {
      try {
         if (bl) {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
            Display.setDisplayMode(Display.getDesktopDisplayMode());
            Display.setLocation(0, 0);
            Display.setFullscreen(false);
            Display.setResizable(false);
         } else {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
            Display.setDisplayMode(new DisplayMode(getMinecraft().displayWidth, getMinecraft().displayHeight));
            Display.setResizable(true);
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int i = (int)((dimension.getWidth() - Display.getWidth()) / 2.0);
            int j = (int)((dimension.getHeight() - Display.getHeight()) / 2.0);
            Display.setLocation(i, j);
         }
      } catch (LWJGLException lwjglException) {
         lwjglException.printStackTrace();
      }
   }

   private void checkHostsFileForHypixel() {
      try {
         if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String string = new String(Files.readAllBytes(Paths.get("c:\\windows\\system32\\drivers\\etc\\hosts")), StandardCharsets.UTF_8);
            if (string.contains("hypixel.net")) {
               hypixelInHostsFile = true;
            }
         }
      } catch (IOException ioException) {
      }
   }

   public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
      this.loadWorld((WorldClient)null);
      System.gc();
      ISaveHandler iSaveHandler = this.saveLoader.getSaveLoader(folderName, false);
      WorldInfo worldInfo = iSaveHandler.loadWorldInfo();
      if (worldInfo == null && worldSettingsIn != null) {
         worldInfo = new WorldInfo(worldSettingsIn, folderName);
         iSaveHandler.saveWorldInfo(worldInfo);
      }

      if (worldSettingsIn == null) {
         worldSettingsIn = new WorldSettings(worldInfo);
      }

      try {
         this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
         this.theIntegratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Starting integrated server");
         crashReportCategory.addCrashSection("Level ID", folderName);
         crashReportCategory.addCrashSection("Level Name", worldName);
         throw new ReportedException(crashReport);
      }

      this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

      while (!this.theIntegratedServer.serverIsInRunLoop()) {
         String string = this.theIntegratedServer.getUserMessage();
         if (string != null) {
            this.loadingScreen.displayLoadingString(I18n.format(string));
         } else {
            this.loadingScreen.displayLoadingString("");
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException interruptedException) {
         }
      }

      this.displayGuiScreen((GuiScreen)null);
      SocketAddress socketAddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
      NetworkManager networkManager = NetworkManager.provideLocalClient(socketAddress);
      networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, this, (GuiScreen)null));
      networkManager.sendPacket(new C00Handshake(47, socketAddress.toString(), 0, EnumConnectionState.LOGIN));
      networkManager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
      this.myNetworkManager = networkManager;
   }

   public void loadWorld(WorldClient worldClientIn) {
      this.loadWorld(worldClientIn, "");
   }

   public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
      this.checkHostsFileForHypixel();
      if (worldClientIn == null) {
         NetHandlerPlayClient netHandlerPlayClient = this.getNetHandler();
         if (netHandlerPlayClient != null) {
            netHandlerPlayClient.cleanup();
         }

         if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
            this.theIntegratedServer.initiateShutdown();
            this.theIntegratedServer.setStaticInstance();
         }

         this.theIntegratedServer = null;
         this.guiAchievement.clearAchievements();
         this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
      }

      this.renderViewEntity = null;
      this.myNetworkManager = null;
      if (this.loadingScreen != null) {
         this.loadingScreen.resetProgressAndMessage(loadingMessage);
         this.loadingScreen.displayLoadingString("");
      }

      if (worldClientIn == null && this.theWorld != null) {
         this.mcResourcePackRepository.clearResourcePack();
         this.ingameGUI.resetPlayersOverlayFooterHeader();
         this.setServerData((ServerData)null);
         this.integratedServerIsRunning = false;
      }

      this.mcSoundHandler.stopSounds();
      this.theWorld = worldClientIn;
      if (worldClientIn != null) {
         if (this.renderGlobal != null) {
            this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
         }

         if (this.effectRenderer != null) {
            this.effectRenderer.clearEffects(worldClientIn);
         }

         if (this.thePlayer == null) {
            this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
            this.playerController.flipPlayer(this.thePlayer);
         }

         Astolfo.INSTANCE.getEventBus().post(new WorldLoadEvent());
         this.thePlayer.preparePlayerToSpawn();
         worldClientIn.spawnEntityInWorld(this.thePlayer);
         this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
         this.playerController.setPlayerCapabilities(this.thePlayer);
         this.renderViewEntity = this.thePlayer;
      } else {
         this.saveLoader.flushCache();
         this.thePlayer = null;
      }

      if (worldClientIn != null) {
         reportServerChange(false);
      }

      System.gc();
      this.systemTime = 0L;
   }

   public static void reportServerChange(boolean bl) {
      if (!StringUtils.isNullOrEmpty(ServerData.currentServerIp)
         && (
            !lastReportedUsername.equals(getMinecraft().session.getUsername())
               || !ServerData.currentServerIp.equals(getMinecraft().lastReportedServerIp)
               || forceServerReport
               || bl
         )
         && !getMinecraft().integratedServerIsRunning
         && !getMinecraft().isSingleplayer()
         && !DisconnectHandler.disconnected) {
         if (hypixelInHostsFile && ServerData.currentServerIp.toLowerCase().endsWith("hypixel.net")) {
         }

         lastReportedUsername = getMinecraft().session.getUsername();
         getMinecraft().lastReportedServerIp = ServerData.currentServerIp;
         forceServerReport = false;
      }
   }

   public void setDimensionAndSpawnPlayer(int dimension) {
      this.theWorld.setInitialSpawnLocation();
      this.theWorld.removeAllEntities();
      int i = 0;
      String string = null;
      if (this.thePlayer != null) {
         i = this.thePlayer.getEntityId();
         this.theWorld.removeEntity(this.thePlayer);
         string = this.thePlayer.getClientBrand();
      }

      this.renderViewEntity = null;
      EntityPlayerSP entityPlayerSP = this.thePlayer;
      this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
      this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityPlayerSP.getDataWatcher().getAllWatched());
      this.thePlayer.dimension = dimension;
      this.renderViewEntity = this.thePlayer;
      this.thePlayer.preparePlayerToSpawn();
      this.thePlayer.setClientBrand(string);
      this.theWorld.spawnEntityInWorld(this.thePlayer);
      this.playerController.flipPlayer(this.thePlayer);
      this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
      this.thePlayer.setEntityId(i);
      this.playerController.setPlayerCapabilities(this.thePlayer);
      this.thePlayer.setReducedDebug(entityPlayerSP.hasReducedDebug());
      if (this.currentScreen instanceof GuiGameOver) {
         this.displayGuiScreen((GuiScreen)null);
      }
   }

   public final boolean isDemo() {
      return this.isDemo;
   }

   public NetHandlerPlayClient getNetHandler() {
      return this.thePlayer != null ? this.thePlayer.sendQueue : null;
   }

   public static boolean isGuiEnabled() {
      return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
   }

   public static boolean isFancyGraphicsEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
   }

   public static boolean isAmbientOcclusionEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
   }

   private void middleClickMouse() {
      if (this.objectMouseOver != null) {
         boolean bl = this.thePlayer.capabilities.isCreativeMode;
         int i = 0;
         boolean bl2 = false;
         TileEntity tileEntity = null;
         Item item;
         if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.objectMouseOver.getBlockPos();
            Block block = this.theWorld.getBlockState(blockPos).getBlock();
            if (block.getMaterial() == Material.air) {
               return;
            }

            item = block.getItem(this.theWorld, blockPos);
            if (item == null) {
               return;
            }

            if (bl && GuiScreen.isCtrlKeyDown()) {
               tileEntity = this.theWorld.getTileEntity(blockPos);
            }

            Block block2 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
            i = block2.getDamageValue(this.theWorld, blockPos);
            bl2 = item.getHasSubtypes();
         } else {
            if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !bl) {
               return;
            }

            if (this.objectMouseOver.entityHit instanceof EntityPainting) {
               item = Items.painting;
            } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
               item = Items.lead;
            } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
               EntityItemFrame entityItemFrame = (EntityItemFrame)this.objectMouseOver.entityHit;
               ItemStack itemStack = entityItemFrame.getDisplayedItem();
               if (itemStack == null) {
                  item = Items.item_frame;
               } else {
                  item = itemStack.getItem();
                  i = itemStack.getMetadata();
                  bl2 = true;
               }
            } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
               EntityMinecart entityMinecart = (EntityMinecart)this.objectMouseOver.entityHit;
               switch (entityMinecart.getMinecartType()) {
                  case FURNACE:
                     item = Items.furnace_minecart;
                     break;
                  case CHEST:
                     item = Items.chest_minecart;
                     break;
                  case TNT:
                     item = Items.tnt_minecart;
                     break;
                  case HOPPER:
                     item = Items.hopper_minecart;
                     break;
                  case COMMAND_BLOCK:
                     item = Items.command_block_minecart;
                     break;
                  default:
                     item = Items.minecart;
               }
            } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
               item = Items.boat;
            } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
               item = Items.armor_stand;
            } else {
               item = Items.spawn_egg;
               i = EntityList.getEntityID(this.objectMouseOver.entityHit);
               bl2 = true;
               if (!EntityList.entityEggs.containsKey(i)) {
                  return;
               }
            }
         }

         InventoryPlayer inventoryPlayer = this.thePlayer.inventory;
         if (tileEntity == null) {
            inventoryPlayer.setCurrentItem(item, i, bl2, bl);
         } else {
            ItemStack itemStack2 = this.pickBlockWithNBT(item, i, tileEntity);
            inventoryPlayer.setInventorySlotContents(inventoryPlayer.currentItem, itemStack2);
         }

         if (bl) {
            int j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryPlayer.currentItem;
            this.playerController.sendSlotPacket(inventoryPlayer.getStackInSlot(inventoryPlayer.currentItem), j);
         }
      }
   }

   private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn) {
      ItemStack itemStack = new ItemStack(itemIn, 1, meta);
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      tileEntityIn.writeToNBT(nbtTagCompound);
      if (itemIn == Items.skull && nbtTagCompound.hasKey("Owner")) {
         NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Owner");
         NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
         nbtTagCompound3.setTag("SkullOwner", nbtTagCompound2);
         itemStack.setTagCompound(nbtTagCompound3);
         return itemStack;
      } else {
         itemStack.setTagInfo("BlockEntityTag", nbtTagCompound);
         NBTTagCompound nbtTagCompound4 = new NBTTagCompound();
         NBTTagList nbtTagList = new NBTTagList();
         nbtTagList.appendTag(new NBTTagString("(+NBT)"));
         nbtTagCompound4.setTag("Lore", nbtTagList);
         itemStack.setTagInfo("display", nbtTagCompound4);
         return itemStack;
      }
   }

   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
      theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.launchedVersion;
         }
      });
      theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>() {
         public String call() {
            return Sys.getVersion();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>() {
         public String call() {
            return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
         }
      });
      theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getLogText();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>() {
         public String call() {
            return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
         }
      });
      theCrash.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               public String call() throws Exception {
                  String string = ClientBrandRetriever.getClientModName();
                  return !string.equals("vanilla")
                     ? "Definitely; Client brand changed to '" + string + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and client brand is untouched."
                     );
               }
            }
         );
      theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Client (map_client.txt)";
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>() {
         public String call() throws Exception {
            StringBuilder stringBuilder = new StringBuilder();

            for (Object object : Minecraft.this.gameSettings.resourcePacks) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append(", ");
               }

               stringBuilder.append(object);
               if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(object)) {
                  stringBuilder.append(" (incompatible)");
               }
            }

            return stringBuilder.toString();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
         }
      });
      theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getCpu();
         }
      });
      if (this.theWorld != null) {
         this.theWorld.addWorldInfoToCrashReport(theCrash);
      }

      return theCrash;
   }

   public static Minecraft getMinecraft() {
      return theMinecraft;
   }

   public ListenableFuture<Object> scheduleResourcesRefresh() {
      return this.addScheduledTask(new Runnable() {
         @Override
         public void run() {
            Minecraft.this.refreshResources();
         }
      });
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
      playerSnooper.addClientStat("fps", debugFPS);
      playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
      playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
      playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
      playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      playerSnooper.addClientStat("current_action", this.getCurrentAction());
      String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      playerSnooper.addClientStat("endianness", string);
      playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
      int i = 0;

      for (ResourcePackRepository.Entry entry : this.mcResourcePackRepository.getRepositoryEntries()) {
         playerSnooper.addClientStat("resource_pack[" + i++ + "]", entry.getResourcePackName());
      }

      if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
         playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
      }
   }

   private String getCurrentAction() {
      return this.theIntegratedServer != null
         ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer")
         : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
   }

   @Override
   public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
      playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
      playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
      playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
      playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
      ContextCapabilities contextCapabilities = GLContext.getCapabilities();
      playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", contextCapabilities.GL_ARB_arrays_of_arrays);
      playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", contextCapabilities.GL_ARB_base_instance);
      playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", contextCapabilities.GL_ARB_blend_func_extended);
      playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", contextCapabilities.GL_ARB_clear_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", contextCapabilities.GL_ARB_color_buffer_float);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", contextCapabilities.GL_ARB_compatibility);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", contextCapabilities.GL_ARB_compressed_texture_pixel_storage);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextCapabilities.GL_ARB_compute_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextCapabilities.GL_ARB_copy_buffer);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextCapabilities.GL_ARB_copy_image);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextCapabilities.GL_ARB_depth_buffer_float);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextCapabilities.GL_ARB_compute_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextCapabilities.GL_ARB_copy_buffer);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextCapabilities.GL_ARB_copy_image);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextCapabilities.GL_ARB_depth_buffer_float);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", contextCapabilities.GL_ARB_depth_clamp);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", contextCapabilities.GL_ARB_depth_texture);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", contextCapabilities.GL_ARB_draw_buffers);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", contextCapabilities.GL_ARB_draw_buffers_blend);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", contextCapabilities.GL_ARB_draw_elements_base_vertex);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", contextCapabilities.GL_ARB_draw_indirect);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", contextCapabilities.GL_ARB_draw_instanced);
      playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", contextCapabilities.GL_ARB_explicit_attrib_location);
      playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", contextCapabilities.GL_ARB_explicit_uniform_location);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", contextCapabilities.GL_ARB_fragment_layer_viewport);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", contextCapabilities.GL_ARB_fragment_program);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", contextCapabilities.GL_ARB_fragment_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", contextCapabilities.GL_ARB_fragment_program_shadow);
      playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", contextCapabilities.GL_ARB_framebuffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", contextCapabilities.GL_ARB_framebuffer_sRGB);
      playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", contextCapabilities.GL_ARB_geometry_shader4);
      playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", contextCapabilities.GL_ARB_gpu_shader5);
      playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", contextCapabilities.GL_ARB_half_float_pixel);
      playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", contextCapabilities.GL_ARB_half_float_vertex);
      playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", contextCapabilities.GL_ARB_instanced_arrays);
      playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", contextCapabilities.GL_ARB_map_buffer_alignment);
      playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", contextCapabilities.GL_ARB_map_buffer_range);
      playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", contextCapabilities.GL_ARB_multisample);
      playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", contextCapabilities.GL_ARB_multitexture);
      playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", contextCapabilities.GL_ARB_occlusion_query2);
      playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", contextCapabilities.GL_ARB_pixel_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", contextCapabilities.GL_ARB_seamless_cube_map);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", contextCapabilities.GL_ARB_shader_objects);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", contextCapabilities.GL_ARB_shader_stencil_export);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", contextCapabilities.GL_ARB_shader_texture_lod);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", contextCapabilities.GL_ARB_shadow);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", contextCapabilities.GL_ARB_shadow_ambient);
      playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", contextCapabilities.GL_ARB_stencil_texturing);
      playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", contextCapabilities.GL_ARB_sync);
      playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", contextCapabilities.GL_ARB_tessellation_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", contextCapabilities.GL_ARB_texture_border_clamp);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", contextCapabilities.GL_ARB_texture_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", contextCapabilities.GL_ARB_texture_cube_map);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", contextCapabilities.GL_ARB_texture_cube_map_array);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", contextCapabilities.GL_ARB_texture_non_power_of_two);
      playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", contextCapabilities.GL_ARB_uniform_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", contextCapabilities.GL_ARB_vertex_blend);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", contextCapabilities.GL_ARB_vertex_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", contextCapabilities.GL_ARB_vertex_program);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", contextCapabilities.GL_ARB_vertex_shader);
      playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", contextCapabilities.GL_EXT_bindable_uniform);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", contextCapabilities.GL_EXT_blend_equation_separate);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", contextCapabilities.GL_EXT_blend_func_separate);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", contextCapabilities.GL_EXT_blend_minmax);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", contextCapabilities.GL_EXT_blend_subtract);
      playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", contextCapabilities.GL_EXT_draw_instanced);
      playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", contextCapabilities.GL_EXT_framebuffer_multisample);
      playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", contextCapabilities.GL_EXT_framebuffer_object);
      playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", contextCapabilities.GL_EXT_framebuffer_sRGB);
      playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", contextCapabilities.GL_EXT_geometry_shader4);
      playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", contextCapabilities.GL_EXT_gpu_program_parameters);
      playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", contextCapabilities.GL_EXT_gpu_shader4);
      playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", contextCapabilities.GL_EXT_multi_draw_arrays);
      playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", contextCapabilities.GL_EXT_packed_depth_stencil);
      playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", contextCapabilities.GL_EXT_paletted_texture);
      playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", contextCapabilities.GL_EXT_rescale_normal);
      playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", contextCapabilities.GL_EXT_separate_shader_objects);
      playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", contextCapabilities.GL_EXT_shader_image_load_store);
      playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", contextCapabilities.GL_EXT_shadow_funcs);
      playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", contextCapabilities.GL_EXT_shared_texture_palette);
      playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", contextCapabilities.GL_EXT_stencil_clear_tag);
      playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", contextCapabilities.GL_EXT_stencil_two_side);
      playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", contextCapabilities.GL_EXT_stencil_wrap);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", contextCapabilities.GL_EXT_texture_3d);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", contextCapabilities.GL_EXT_texture_array);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", contextCapabilities.GL_EXT_texture_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", contextCapabilities.GL_EXT_texture_integer);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", contextCapabilities.GL_EXT_texture_lod_bias);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", contextCapabilities.GL_EXT_texture_sRGB);
      playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", contextCapabilities.GL_EXT_vertex_shader);
      playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", contextCapabilities.GL_EXT_vertex_weighting);
      playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
   }

   public static int getGLMaximumTextureSize() {
      for (int i = 16384; i > 0; i >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (ByteBuffer)null);
         int j = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (j != 0) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public boolean isSnooperEnabled() {
      return this.gameSettings.snooperEnabled;
   }

   public void setServerData(ServerData serverDataIn) {
      this.currentServerData = serverDataIn;
   }

   public ServerData getCurrentServerData() {
      return this.currentServerData;
   }

   public boolean isIntegratedServerRunning() {
      return this.integratedServerIsRunning;
   }

   public boolean isSingleplayer() {
      return this.integratedServerIsRunning && this.theIntegratedServer != null;
   }

   public IntegratedServer getIntegratedServer() {
      return this.theIntegratedServer;
   }

   public static void stopIntegratedServer() {
      if (theMinecraft != null) {
         IntegratedServer integratedServer = theMinecraft.getIntegratedServer();
         if (integratedServer != null) {
            integratedServer.stopServer();
         }
      }
   }

   public PlayerUsageSnooper getPlayerUsageSnooper() {
      return this.usageSnooper;
   }

   public static long getSystemTime() {
      return Sys.getTime() * 1000L / Sys.getTimerResolution();
   }

   public boolean isFullScreen() {
      return this.fullscreen;
   }

   public Session getSession() {
      return this.session;
   }

   public PropertyMap getTwitchDetails() {
      return this.twitchDetails;
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.profileProperties.putAll(gameProfile.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.renderEngine;
   }

   public IResourceManager getResourceManager() {
      return this.mcResourceManager;
   }

   public ResourcePackRepository getResourcePackRepository() {
      return this.mcResourcePackRepository;
   }

   public LanguageManager getLanguageManager() {
      return this.mcLanguageManager;
   }

   public TextureMap getTextureMapBlocks() {
      return this.textureMapBlocks;
   }

   public boolean isJava64bit() {
      return this.jvm64bit;
   }

   public boolean isGamePaused() {
      return this.isGamePaused;
   }

   public SoundHandler getSoundHandler() {
      return this.mcSoundHandler;
   }

   public MusicTicker.MusicType getAmbientMusicType() {
      return this.thePlayer != null
         ? (
            this.thePlayer.worldObj.provider instanceof WorldProviderHell
               ? MusicTicker.MusicType.NETHER
               : (
                  this.thePlayer.worldObj.provider instanceof WorldProviderEnd
                     ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END)
                     : (
                        this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying
                           ? MusicTicker.MusicType.CREATIVE
                           : MusicTicker.MusicType.GAME
                     )
               )
         )
         : MusicTicker.MusicType.MENU;
   }

   public void dispatchKeypresses() {
      int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
      if (i != 0
         && !Keyboard.isRepeatEvent()
         && (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
         && Keyboard.getEventKeyState()) {
         if (i == this.gameSettings.keyBindFullscreen.getKeyCode()) {
            this.toggleFullscreen();
         } else if (i == this.gameSettings.keyBindScreenshot.getKeyCode()) {
            this.ingameGUI
               .getChatGUI()
               .printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
         }
      }
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public void setRenderViewEntity(Entity viewingEntity) {
      this.renderViewEntity = viewingEntity;
      this.entityRenderer.loadEntityShader(viewingEntity);
   }

   public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
      Validate.notNull(callableToSchedule);
      if (!this.isCallingFromMinecraftThread()) {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(callableToSchedule);
         synchronized (this.scheduledTasks) {
            this.scheduledTasks.add(listenableFutureTask);
            return listenableFutureTask;
         }
      } else {
         try {
            return Futures.immediateFuture((V)callableToSchedule.call());
         } catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
         }
      }
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.addScheduledTask(Executors.callable(runnableToSchedule));
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.mcThread;
   }

   public BlockRendererDispatcher getBlockRendererDispatcher() {
      return this.blockRenderDispatcher;
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   public RenderItem getRenderItem() {
      return this.renderItem;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public static int getDebugFPS() {
      return debugFPS;
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public static Map<String, String> getSessionInfo() {
      HashMap hashMap = Maps.newHashMap();
      hashMap.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
      hashMap.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
      hashMap.put("X-Minecraft-Version", "1.8.8");
      return hashMap;
   }

   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   public void setConnectedToRealms(boolean isConnected) {
      this.connectedToRealms = isConnected;
   }

   public class ajr implements Callable<String> {
      public String call() throws Exception {
         return Minecraft.this.currentScreen.getClass().getCanonicalName();
      }
   }

   public class asm implements Callable<String> {
      public String call() throws Exception {
         return Minecraft.this.currentScreen.getClass().getCanonicalName();
      }
   }
}
