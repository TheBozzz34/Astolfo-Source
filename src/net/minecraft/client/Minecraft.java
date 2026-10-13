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
   private boolean F = false;
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
   private boolean l;
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
   public boolean R;
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
   private String xh;
   private int serverPort;
   public boolean inGameHasFocus;
   public long systemTime = getSystemTime();
   private int joinPlayerCounter;
   public final FrameTimer frameTimer = new FrameTimer();
   public long startNanoTime = System.nanoTime();
   private final boolean x1;
   private final boolean x_;
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
   public String xQ = "";
   public boolean a = false;
   public boolean xw = false;
   public boolean xe = false;
   public boolean renderChunksMany = true;
   public long debugUpdateTime = getSystemTime();
   public int fpsCounter;
   public long prevFrameTime = -1L;
   private String debugProfilerName = "root";
   public static boolean c;
   public String autoLoginToken = "";
   public boolean xx = false;
   public Stopwatch J = new Stopwatch();
   public static boolean xG;
   private static String x7 = "";
   private String xc = "";
   private static final long ab = 118377550742639L;

   public Minecraft(GameConfiguration var1) {
      theMinecraft = this;
      this.mcDataDir = var1.folderInfo.mcDataDir;
      this.fileAssets = var1.folderInfo.assetsDir;
      this.fileResourcepacks = var1.folderInfo.resourcePacksDir;
      this.launchedVersion = var1.gameInfo.version;
      this.twitchDetails = var1.userInfo.userProperties;
      this.profileProperties = var1.userInfo.profileProperties;
      this.mcDefaultResourcePack = new DefaultResourcePack(new ResourceIndex(var1.folderInfo.assetsDir, var1.folderInfo.assetIndex).getResourceMap());
      this.proxy = var1.userInfo.proxy == null ? Proxy.NO_PROXY : var1.userInfo.proxy;
      this.sessionService = new YggdrasilAuthenticationService(var1.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
      this.session = var1.userInfo.session;
      logger.info("Setting user: " + this.session.getUsername());
      logger.info("(Session ID is " + this.session.getSessionID() + ")");
      this.x_ = var1.gameInfo.isDemo;
      this.displayWidth = var1.displayInfo.width > 0 ? var1.displayInfo.width : 1;
      this.displayHeight = var1.displayInfo.height > 0 ? var1.displayInfo.height : 1;
      this.tempDisplayWidth = var1.displayInfo.width;
      this.tempDisplayHeight = var1.displayInfo.height;
      this.fullscreen = var1.displayInfo.fullscreen;
      this.x1 = isJvm64bit();
      this.theIntegratedServer = new IntegratedServer(this);
      this.autoLoginToken = var1.autoLoginToken;
      if (this.autoLoginToken == null) {
         this.autoLoginToken = "";
      }

      if (var1.serverInfo.serverName != null) {
         this.xh = var1.serverInfo.serverName;
         this.serverPort = var1.serverInfo.serverPort;
      }

      ImageIO.setUseCache(false);
      Bootstrap.register();
   }

   public void run() {
      this.running = true;

      try {
         this.startGame();
      } catch (Throwable var14) {
         CrashReport var5 = CrashReport.makeCrashReport(var14, "Initializing game");
         var5.makeCategory("Initialization");
         this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(var5));
         return;
      }

      try {
         while (this.running) {
            if (this.hasCrashed && this.crashReporter != null) {
               this.displayCrashReport(this.crashReporter);
            } else {
               try {
                  this.runGameLoop();
               } catch (OutOfMemoryError var13) {
                  this.freeMemory();
                  this.displayGuiScreen(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }
         }

         return;
      } catch (MinecraftError var15) {
      } catch (ReportedException var16) {
         this.addGraphicsAndWorldToCrashReport(var16.getCrashReport());
         this.freeMemory();
         logger.fatal("Reported exception thrown!", var16);
         this.displayCrashReport(var16.getCrashReport());
      } catch (Throwable var17) {
         CrashReport var19 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var17));
         this.freeMemory();
         logger.fatal("Unreported exception thrown!", var17);
         this.displayCrashReport(var19);
      } finally {
         this.shutdownMinecraftApplet();
      }
   }

   private void startGame() throws LWJGLException, IOException {
      long var1 = ab ^ 111160500533317L;
      SplashScreen.e(1);
      this.gameSettings = new GameSettings(this, this.mcDataDir);
      this.defaultResourcePacks.add(this.mcDefaultResourcePack);
      this.startTimerHackThread();
      if (this.gameSettings.z > 0 && this.gameSettings.a > 0) {
         this.displayWidth = this.gameSettings.a;
         this.displayHeight = this.gameSettings.z;
      }

      logger.info("LWJGL Version: " + Sys.getVersion());
      this.setWindowIcon();
      this.setInitialDisplayMode();
      this.createDisplay();
      OpenGlHelper.initializeTextures();
      this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
      this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.h();
      this.mcResourcePackRepository = new ResourcePackRepository(
         this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings
      );
      this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
      this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
      this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
      this.refreshResources();
      this.renderEngine = new TextureManager(this.mcResourceManager);
      SplashScreen.e(2);
      this.mcResourceManager.registerReloadListener(this.renderEngine);
      this.drawSplashScreen(this.renderEngine);
      this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
      this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
      this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
      this.mcMusicTicker = new MusicTicker(this);
      this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
      SplashScreen.e(3);
      Astolfo.INSTANCE.setFontRenderer(new FontRenderer(this.gameSettings, new ResourceLocation("client/font/bit.png"), this.renderEngine, false));
      if (this.gameSettings.language != null) {
         this.fontRendererObj.setUnicodeFlag(this.isUnicode());
         this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
      }

      this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
      this.mcResourceManager.registerReloadListener(this.fontRendererObj);
      long var10000 = 5133493022400536350L ^ var1;
      this.mcResourceManager.registerReloadListener(Astolfo.INSTANCE.getFontRenderer());
      this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
      this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
      this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
      AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
         private static final long a = 107355880922902L;

         @Override
         public String formatString(String var1) {
            try {
               return String.format(var1, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
            } catch (Exception var5) {
               return "Error: " + var5.getLocalizedMessage();
            }
         }
      });
      this.mouseHelper = new MouseHelper();
      this.checkGLError("Pre startup");
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0);
      var10000 = 3210313489529356661L ^ var1;
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlpha();
      var10000 = 4321534424054031229L ^ var1;
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(1029);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      var10000 = 5527693030937335371L ^ var1;
      GlStateManager.matrixMode(5888);
      this.checkGLError("Startup");
      this.textureMapBlocks = new TextureMap("textures");
      this.textureMapBlocks.setMipmapLevels(this.gameSettings.g);
      this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
      this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.g > 0);
      this.modelManager = new ModelManager(this.textureMapBlocks);
      var10000 = 7752571634966964805L ^ var1;
      this.mcResourceManager.registerReloadListener(this.modelManager);
      this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
      this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
      SplashScreen.e(4);
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
      SplashScreen.e(5);
      this.checkGLError("Post startup");
      ValueCheckers.INSTANCE.C();
      Astolfo.INSTANCE.load();
      SplashScreen.e(6);
      this.ingameGUI = new GuiIngame(this);
      var10000 = 7979188994558015629L ^ var1;
      this.displayGuiScreen(new LoginScreen());
      this.renderEngine.deleteTexture(this.mojangLogo);
      this.mojangLogo = null;
      SplashScreen.e(7);
      this.loadingScreen = new LoadingScreenRenderer(this);
      if (this.gameSettings.fullScreen && !this.fullscreen) {
         this.toggleFullscreen();
      }

      try {
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
      } catch (OpenGLException var5) {
         this.gameSettings.enableVsync = false;
         this.gameSettings.saveOptions();
      }

      this.renderGlobal.makeEntityOutlineShader();
   }

   private void h() {
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
      } catch (LWJGLException var7) {
         logger.error("Couldn't set pixel format", var7);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var6) {
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
         DisplayMode var4 = Display.getDisplayMode();
         this.displayWidth = Math.max(1, var4.getWidth());
         this.displayHeight = Math.max(1, var4.getHeight());
      } else {
         Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
      }
   }

   private void setWindowIcon() {
      Util.EnumOS var4 = Util.getOSType();
      if (var4 != Util.EnumOS.OSX) {
         InputStream var5 = null;
         InputStream var6 = null;

         try {
            var5 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
            var6 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
            if (var5 != null && var6 != null) {
               Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(var5), this.readImageToBuffer(var6)});
            }
         } catch (IOException var11) {
            logger.error("Couldn't set icon", var11);
         } finally {
            IOUtils.closeQuietly(var5);
            IOUtils.closeQuietly(var6);
         }
      }
   }

   private static boolean isJvm64bit() {
      String[] var3 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for (String var7 : var3) {
         String var8 = System.getProperty(var7);
         if (var8 != null && var8.contains("64")) {
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
      Thread var3 = new Thread("Timer hack thread") {
         private static final long a = 125533108795996L;

         @Override
         public void run() {
            while (Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var5) {
               }
            }
         }
      };
      var3.setDaemon(true);
      var3.start();
   }

   public void crashed(CrashReport var1) {
      this.hasCrashed = true;
      this.crashReporter = var1;
   }

   public void displayCrashReport(CrashReport var1) {
      File var5 = new File(getMinecraft().mcDataDir, "crash-reports");
      File var6 = new File(var5, "astolfo-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".zar");
      Bootstrap.printToSYSOUT(var1.getCompleteReport());
      if (var1.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + var1.getFile());
         System.exit(-1);
      } else if (var1.saveToFile(var6)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + var6.getAbsolutePath());
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
      ArrayList var4 = Lists.newArrayList(this.defaultResourcePacks);

      for (ResourcePackRepository.Entry var6 : this.mcResourcePackRepository.getRepositoryEntries()) {
         var4.add(var6.getResourcePack());
      }

      if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
         var4.add(this.mcResourcePackRepository.getResourcePackInstance());
      }

      try {
         this.mcResourceManager.reloadResources(var4);
      } catch (RuntimeException var7) {
         logger.info("Caught error stitching, removing all assigned resourcepacks", var7);
         var4.clear();
         var4.addAll(this.defaultResourcePacks);
         this.mcResourcePackRepository.setRepositories(Collections.emptyList());
         this.mcResourceManager.reloadResources(var4);
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.incompatibleResourcePacks.clear();
         this.gameSettings.saveOptions();
      }

      this.mcLanguageManager.parseLanguageMetadata(var4);
      if (this.renderGlobal != null) {
         this.renderGlobal.loadRenderers();
      }
   }

   private ByteBuffer readImageToBuffer(InputStream var1) throws IOException {
      BufferedImage var5 = ImageIO.read(var1);
      int[] var6 = var5.getRGB(0, 0, var5.getWidth(), var5.getHeight(), (int[])null, 0, var5.getWidth());
      ByteBuffer var7 = ByteBuffer.allocate(4 * var6.length);

      for (int var11 : var6) {
         var7.putInt(var11 << 8 | var11 >> 24 & 0xFF);
      }

      ((Buffer)var7).flip();
      return var7;
   }

   private void updateDisplayMode() throws LWJGLException {
      HashSet var4 = Sets.newHashSet();
      Collections.addAll(var4, Display.getAvailableDisplayModes());
      DisplayMode var5 = Display.getDesktopDisplayMode();
      if (!var4.contains(var5) && Util.getOSType() == Util.EnumOS.OSX) {
         for (DisplayMode var7 : macDisplayModes) {
            boolean var8 = true;

            for (Object var10$raw : var4) {
               DisplayMode var10 = (DisplayMode)var10$raw;
               if (var10.getBitsPerPixel() == 32 && var10.getWidth() == var7.getWidth() && var10.getHeight() == var7.getHeight()) {
                  var8 = false;
                  break;
               }
            }

            if (!var8) {
               for (Object var12$raw : var4) {
                  DisplayMode var12 = (DisplayMode)var12$raw;
                  if (var12.getBitsPerPixel() == 32 && var12.getWidth() == var7.getWidth() / 2 && var12.getHeight() == var7.getHeight() / 2) {
                     var5 = var12;
                     break;
                  }
               }
            }
         }
      }

      Display.setDisplayMode(var5);
      this.displayWidth = var5.getWidth();
      this.displayHeight = var5.getHeight();
   }

   private void drawSplashScreen(TextureManager var1) throws LWJGLException {
      SplashScreen.h(var1);
   }

   public void draw(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      float var13 = 0.00390625F;
      float var14 = 0.00390625F;
      WorldRenderer var15 = Tessellator.getInstance().getWorldRenderer();
      var15.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      var15.pos(var1, var2 + var6, 0.0).tex(var3 * var13, (var4 + var6) * var14).color(var7, var8, var9, var10).endVertex();
      var15.pos(var1 + var5, var2 + var6, 0.0).tex((var3 + var5) * var13, (var4 + var6) * var14).color(var7, var8, var9, var10).endVertex();
      var15.pos(var1 + var5, var2, 0.0).tex((var3 + var5) * var13, var4 * var14).color(var7, var8, var9, var10).endVertex();
      var15.pos(var1, var2, 0.0).tex(var3 * var13, var4 * var14).color(var7, var8, var9, var10).endVertex();
      Tessellator.getInstance().draw();
   }

   public ISaveFormat getSaveLoader() {
      return this.saveLoader;
   }

   public void displayGuiScreen(GuiScreen var1) {
      if (this.currentScreen != null) {
         this.currentScreen.onGuiClosed();
      }

      if (var1 == null && this.theWorld == null) {
         var1 = new GuiMainMenu();
      } else if (var1 == null && this.thePlayer.getHealth() <= 0.0F) {
         Astolfo.INSTANCE.getEventBus().post(new DeathEvent());
         var1 = new GuiGameOver();
      }

      if (var1 instanceof GuiMainMenu) {
         this.gameSettings.n = false;
         this.ingameGUI.getChatGUI().clearChatMessages();
      }

      this.currentScreen = var1;
      if (var1 != null) {
         this.v();
         ScaledResolution var5 = new ScaledResolution(this);
         int var6 = var5.getScaledWidth();
         int var7 = var5.getScaledHeight();
         var1.setWorldAndResolution(this, var6, var7);
         this.R = false;
      } else {
         this.mcSoundHandler.resumeSounds();
         this.setIngameFocus();
      }
   }

   public static int getLoadState() {
      long var0 = ab ^ 21823722746508L;
      long var2 = var0 ^ 126182652174405L;
      int var5 = 0;

      for (LoadStage var7 : Astolfo.INSTANCE.x().e().a().L().getValue()) {
         var5 += var7.getState();
      }

      return var5;
   }

   private void checkGLError(String var1) {
      if (this.enableGLErrorChecking) {
         int var5 = GL11.glGetError();
         if (var5 != 0) {
            String var6 = GLU.gluErrorString(var5);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + var1);
            logger.error(var5 + ": " + var6);
         }
      }
   }

   public void shutdownMinecraftApplet() {
      try {
         logger.info("Stopping!");

         try {
            this.loadWorld((WorldClient)null);
         } catch (Throwable var8) {
         }

         this.mcSoundHandler.y();
      } finally {
         Display.destroy();
         if (!this.hasCrashed) {
            System.exit(0);
         }
      }

      System.gc();
   }

   private void runGameLoop() throws IOException {
      long var1 = ab ^ 71358527994590L;
      long var4 = System.nanoTime();
      this.mcProfiler.startSection("root");
      if (Display.isCreated() && Display.isCloseRequested()) {
         this.shutdown();
      }

      if (this.l && this.theWorld != null) {
         float var6 = this.timer.renderPartialTicks;
         this.timer.updateTimer();
         this.timer.renderPartialTicks = var6;
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
      long var14 = System.nanoTime();
      this.mcProfiler.startSection("tick");

      for (int var8 = 0; var8 < this.timer.elapsedTicks; var8++) {
         this.runTick();
      }

      this.mcProfiler.endStartSection("preRenderErrors");
      long var15 = System.nanoTime() - var14;
      this.checkGLError("Pre render");
      this.mcProfiler.endStartSection("sound");
      long var10000 = 6304076640069422054L ^ var1;
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
      if (!this.R) {
         this.mcProfiler.endStartSection("gameRenderer");
         this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, var4);
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
      if (this.gameSettings.n && this.gameSettings.Zj && !this.gameSettings.Zn) {
         if (!this.mcProfiler.profilingEnabled) {
            this.mcProfiler.clearProfiling();
         }

         this.mcProfiler.profilingEnabled = true;
         this.displayDebugInfo(var15);
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
      this.l = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
      long var10 = System.nanoTime();
      this.frameTimer.addFrame(var10 - this.startNanoTime);
      this.startNanoTime = var10;

      while (getSystemTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.xQ = String.format(
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

      if (this.M()) {
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
         int var4 = this.displayWidth;
         int var5 = this.displayHeight;
         this.displayWidth = Display.getWidth();
         this.displayHeight = Display.getHeight();
         if (this.displayWidth != var4 || this.displayHeight != var5) {
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

   public boolean M() {
      return this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
   }

   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.renderGlobal.deleteAllDisplayLists();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         this.loadWorld((WorldClient)null);
      } catch (Throwable var2) {
      }

      System.gc();
   }

   private void updateDebugProfilerName(int var1) {
      List var5 = this.mcProfiler.getProfilingData(this.debugProfilerName);
      if (var5 != null && !var5.isEmpty()) {
         Profiler.Result var6 = (Profiler.Result)var5.remove(0);
         if (var1 == 0) {
            if (var6.field_76331_c.length() > 0) {
               int var7 = this.debugProfilerName.lastIndexOf(".");
               if (var7 >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, var7);
               }
            }
         } else {
            var1--;
            if (var1 < var5.size() && !((Profiler.Result)var5.get(var1)).field_76331_c.equals("unspecified")) {
               if (this.debugProfilerName.length() > 0) {
                  this.debugProfilerName = this.debugProfilerName + ".";
               }

               this.debugProfilerName = this.debugProfilerName + ((Profiler.Result)var5.get(var1)).field_76331_c;
            }
         }
      }
   }

   private void displayDebugInfo(long var1) {
      long var3 = ab ^ 81036638229059L;
      if (this.mcProfiler.profilingEnabled) {
         List var6 = this.mcProfiler.getProfilingData(this.debugProfilerName);
         Profiler.Result var7 = (Profiler.Result)var6.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         long var10000 = 4770871202371725874L ^ var3;
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         var10000 = 218634553137331039L ^ var3;
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator var8 = Tessellator.getInstance();
         WorldRenderer var9 = var8.getWorldRenderer();
         short var10 = 160;
         int var11 = this.displayWidth - var10 - 10;
         int var12 = this.displayHeight - var10 * 2;
         GlStateManager.enableBlend();
         var9.begin(7, DefaultVertexFormats.POSITION_COLOR);
         var9.pos(var11 - var10 * 1.1F, var12 - var10 * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         var9.pos(var11 - var10 * 1.1F, var12 + var10 * 2, 0.0).color(200, 0, 0, 0).endVertex();
         var9.pos(var11 + var10 * 1.1F, var12 + var10 * 2, 0.0).color(200, 0, 0, 0).endVertex();
         var9.pos(var11 + var10 * 1.1F, var12 - var10 * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         var8.draw();
         GlStateManager.disableBlend();
         double var13 = 0.0;

         for (int var15 = 0; var15 < var6.size(); var15++) {
            Profiler.Result var16 = (Profiler.Result)var6.get(var15);
            int var17 = MathHelper.floor_double(var16.field_76332_a / 4.0) + 1;
            var9.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int var18 = var16.getColor();
            int var19 = var18 >> 16 & 0xFF;
            int var20 = var18 >> 8 & 0xFF;
            int var21 = var18 & 0xFF;
            var9.pos(var11, var12, 0.0).color(var19, var20, var21, 255).endVertex();

            for (int var22 = var17; var22 >= 0; var22--) {
               float var23 = (float)((var13 + var16.field_76332_a * var22 / var17) * Math.PI * 2.0 / 100.0);
               float var24 = MathHelper.sin(var23) * var10;
               float var25 = MathHelper.cos(var23) * var10 * 0.5F;
               var9.pos(var11 + var24, var12 - var25, 0.0).color(var19, var20, var21, 255).endVertex();
            }

            var8.draw();
            var9.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int var38 = var17; var38 >= 0; var38--) {
               float var39 = (float)((var13 + var16.field_76332_a * var38 / var17) * Math.PI * 2.0 / 100.0);
               float var40 = MathHelper.sin(var39) * var10;
               float var41 = MathHelper.cos(var39) * var10 * 0.5F;
               var9.pos(var11 + var40, var12 - var41, 0.0).color(var19 >> 1, var20 >> 1, var21 >> 1, 255).endVertex();
               var9.pos(var11 + var40, var12 - var41 + 10.0F, 0.0).color(var19 >> 1, var20 >> 1, var21 >> 1, 255).endVertex();
            }

            var8.draw();
            var13 += var16.field_76332_a;
         }

         DecimalFormat var26 = new DecimalFormat("##0.00");
         GlStateManager.enableTexture2D();
         String var27 = "";
         if (!var7.field_76331_c.equals("unspecified")) {
            var27 = var27 + "[0] ";
         }

         if (var7.field_76331_c.length() == 0) {
            var27 = var27 + "ROOT ";
         } else {
            var27 = var27 + var7.field_76331_c + " ";
         }

         int var30 = 16777215;
         this.fontRendererObj.drawStringWithShadow(var27, var11 - var10, var12 - var10 / 2 - 16, var30);
         this.fontRendererObj
            .drawStringWithShadow(
               var27 = var26.format(var7.field_76330_b) + "%", var11 + var10 - this.fontRendererObj.getStringWidth(var27), var12 - var10 / 2 - 16, var30
            );

         for (int var31 = 0; var31 < var6.size(); var31++) {
            Profiler.Result var32 = (Profiler.Result)var6.get(var31);
            String var33 = "";
            if (var32.field_76331_c.equals("unspecified")) {
               var33 = var33 + "[?] ";
            } else {
               var33 = var33 + "[" + (var31 + 1) + "] ";
            }

            var33 = var33 + var32.field_76331_c;
            this.fontRendererObj.drawStringWithShadow(var33, var11 - var10, var12 + var10 / 2 + var31 * 8 + 20, var32.getColor());
            this.fontRendererObj
               .drawStringWithShadow(
                  var33 = var26.format(var32.field_76332_a) + "%",
                  var11 + var10 - 50 - this.fontRendererObj.getStringWidth(var33),
                  var12 + var10 / 2 + var31 * 8 + 20,
                  var32.getColor()
               );
            this.fontRendererObj
               .drawStringWithShadow(
                  var33 = var26.format(var32.field_76330_b) + "%",
                  var11 + var10 - this.fontRendererObj.getStringWidth(var33),
                  var12 + var10 / 2 + var31 * 8 + 20,
                  var32.getColor()
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

   public void v() {
      if (this.inGameHasFocus) {
         KeyBinding.unPressAllKeys();
         this.inGameHasFocus = false;
         this.mouseHelper.ungrabMouseCursor();
      }
   }

   public void g() {
      if (this.currentScreen == null) {
         this.displayGuiScreen(new GuiIngameMenu());
         if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
            this.mcSoundHandler.L();
         }
      }
   }

   private void E(boolean var1) {
      if (!var1) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
         if (var1 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var5 = this.objectMouseOver.getBlockPos();
            if (this.theWorld.getBlockState(var5).getBlock().getMaterial() != Material.air
               && this.playerController.onPlayerDamageBlock(var5, this.objectMouseOver.sideHit)) {
               this.effectRenderer.addBlockHitEffects(var5, this.objectMouseOver.sideHit);
               this.thePlayer.swingItem();
            }
         } else {
            this.playerController.g();
         }
      }
   }

   public void clickMouse() {
      if (this.leftClickCounter <= 0) {
         boolean var4 = ViaFabric.INSTANCE.isNative();
         if (var4) {
            this.thePlayer.swingItem();
         }

         if (this.objectMouseOver == null) {
            logger.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.y()) {
               this.leftClickCounter = 10;
            }
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                  break;
               case BLOCK:
                  BlockPos var6 = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(var6).getBlock().getMaterial() != Material.air) {
                     this.playerController.clickBlock(var6, this.objectMouseOver.sideHit);
                     break;
                  }
               case MISS:
                  if (this.playerController.y() && !var4) {
                     this.leftClickCounter = 10;
                  }
               default:
                  if (this.playerController.y()) {
                     this.leftClickCounter = 10;
                  }
            }

            if (!var4) {
               this.thePlayer.swingItem();
            }
         }
      }

      this.leftClickCounter = 0;
   }

   public void rightClickMouse() {
      if (!this.playerController.getIsHittingBlock()) {
         this.rightClickDelayTimer = 4;
         boolean var4 = true;
         ItemStack var5 = this.thePlayer.inventory.getCurrentItem();
         if (this.objectMouseOver == null) {
            logger.warn("Null returned as 'hitResult', this shouldn't happen!");
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                     var4 = false;
                  } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                     var4 = false;
                  }
                  break;
               case BLOCK:
                  BlockPos var6 = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(var6).getBlock().getMaterial() != Material.air) {
                     int var7 = var5 != null ? var5.stackSize : 0;
                     if (this.playerController
                        .onPlayerRightClick(this.thePlayer, this.theWorld, var5, var6, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                        var4 = false;
                        this.thePlayer.swingItem();
                     }

                     if (var5 == null) {
                        return;
                     }

                     if (var5.stackSize == 0) {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                     } else if (var5.stackSize != var7 || this.playerController.isInCreativeMode()) {
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                     }
                  }
            }
         }

         if (var4) {
            ItemStack var8 = this.thePlayer.inventory.getCurrentItem();
            if (var8 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var8)) {
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
      } catch (Exception var5) {
         logger.error("Couldn't toggle fullscreen", var5);
      }
   }

   private void resize(int var1, int var2) {
      this.displayWidth = Math.max(1, var1);
      this.displayHeight = Math.max(1, var2);
      if (this.currentScreen != null) {
         ScaledResolution var6 = new ScaledResolution(this);
         this.currentScreen.onResize(this, var6.getScaledWidth(), var6.getScaledHeight());
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
      long var1 = ab ^ 106564788937888L;
      long var3 = var1 ^ 66201089088892L;
      if (Astolfo.INSTANCE.getEventBus() != null) {
         Astolfo.INSTANCE.getEventBus().post(new TickEvent(EventState.PRE));
      }

      if (this.rightClickDelayTimer > 0) {
         this.rightClickDelayTimer--;
      }

      this.mcProfiler.startSection("gui");
      if (!this.l) {
         this.ingameGUI.updateTick();
      }

      this.mcProfiler.endSection();
      this.entityRenderer.getMouseOver(1.0F);
      this.mcProfiler.startSection("gameMode");
      if (!this.l && this.theWorld != null) {
         this.playerController.u();
      }

      this.mcProfiler.endStartSection("textures");

      try {
         if (this.thePlayer != null && this.theWorld != null && this.currentScreen == null) {
            if (Astolfo.INSTANCE.getModuleManager().get(FreeLook.class).isEnabled() && (!FreeLook.mouseWheel.getValue() || Mouse.isButtonDown(2))) {
               if (!EntityRenderer.q) {
                  EntityRenderer.r = this.thePlayer.rotationPitch;
                  EntityRenderer.S = this.thePlayer.rotationYaw;
               }

               EntityRenderer.q = true;
               this.gameSettings.thirdPersonView = 1;
            } else if (EntityRenderer.q) {
               EntityRenderer.q = false;
               this.gameSettings.thirdPersonView = 0;
            }
         }
      } catch (Exception var14) {
      }

      if (!this.l) {
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
         } catch (Throwable var12) {
            CrashReport var7 = CrashReport.makeCrashReport(var12, "Updating screen events");
            CrashReportCategory var8 = var7.makeCategory("Affected screen");
            var8.addCrashSectionCallable("Screen name", new Minecraft.asm());
            throw new ReportedException(var7);
         }

         if (this.currentScreen != null) {
            try {
               if (this.currentScreen instanceof LoginScreen) {
                  this.J.reset();
               }

               this.currentScreen.updateScreen();
            } catch (Throwable var11) {
               CrashReport var18 = CrashReport.makeCrashReport(var11, "Ticking screen");
               CrashReportCategory var22 = var18.makeCategory("Affected screen");
               var22.addCrashSectionCallable("Screen name", new Minecraft.ajr());
               throw new ReportedException(var18);
            }
         }
      }

      if (this.currentScreen == null || this.currentScreen.allowUserInput) {
         this.mcProfiler.endStartSection("mouse");

         while (Mouse.next()) {
            int var6 = Mouse.getEventButton();
            KeyBinding.setKeyBindState(var6 - 100, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
               Astolfo.INSTANCE.getEventBus().post(new MouseClickEvent(var6));
               if (this.thePlayer.isSpectator() && var6 == 2) {
                  this.ingameGUI.getSpectatorGui().func_175261_b();
               } else {
                  KeyBinding.onTick(var6 - 100);
               }
            }

            long var19 = getSystemTime() - this.systemTime;
            if (var19 <= 200L) {
               int var9 = Mouse.getEventDWheel();
               if (var9 != 0) {
                  if (this.thePlayer.isSpectator()) {
                     var9 = var9 < 0 ? -1 : 1;
                     if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                        this.ingameGUI.getSpectatorGui().func_175259_b(-var9);
                     } else {
                        float var10 = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + var9 * 0.005F, 0.0F, 0.2F);
                        this.thePlayer.capabilities.setFlySpeed(var10);
                     }
                  } else {
                     this.thePlayer.inventory.changeCurrentItem(var9);
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
            int var15 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyBinding.setKeyBindState(var15, Keyboard.getEventKeyState());
            if (Keyboard.getEventKeyState()) {
               KeyBinding.onTick(var15);
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
               if (var15 == 62 && this.entityRenderer != null) {
                  this.entityRenderer.E();
               }

               Astolfo.INSTANCE.getEventBus().post(new KeyPressEvent(var15));
               if (this.currentScreen != null) {
                  this.currentScreen.I();
               } else {
                  if (var15 == 1) {
                     this.g();
                  }

                  if (var15 == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null) {
                     this.ingameGUI.getChatGUI().clearChatMessages();
                  }

                  if (var15 == 31 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (var15 == 17 && Keyboard.isKeyDown(61)) {
                  }

                  if (var15 == 18 && Keyboard.isKeyDown(61)) {
                  }

                  if (var15 == 47 && Keyboard.isKeyDown(61)) {
                  }

                  if (var15 == 38 && Keyboard.isKeyDown(61)) {
                  }

                  if (var15 == 22 && Keyboard.isKeyDown(61)) {
                  }

                  if (var15 == 20 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (var15 == 33 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                  }

                  if (var15 == 30 && Keyboard.isKeyDown(61)) {
                     this.renderGlobal.loadRenderers();
                  }

                  if (var15 == 35 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                     this.gameSettings.saveOptions();
                  }

                  if (var15 == 48 && Keyboard.isKeyDown(61)) {
                     this.renderManager.W(!this.renderManager.isDebugBoundingBox());
                  }

                  if (var15 == 25 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.G = !this.gameSettings.G;
                     this.gameSettings.saveOptions();
                  }

                  if (var15 == 59) {
                     this.gameSettings.Zn = !this.gameSettings.Zn;
                  }

                  if (var15 == 61) {
                     this.gameSettings.n = !this.gameSettings.n;
                     this.gameSettings.Zj = GuiScreen.isShiftKeyDown();
                     this.gameSettings.A_ = GuiScreen.isAltKeyDown();
                  }

                  if (this.gameSettings.ZP.h()) {
                     this.gameSettings.thirdPersonView++;
                     if (this.gameSettings.thirdPersonView > 2) {
                        this.gameSettings.thirdPersonView = 0;
                     }

                     if (this.gameSettings.thirdPersonView == 0) {
                        this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                     } else if (this.gameSettings.thirdPersonView == 1) {
                        this.entityRenderer.loadEntityShader((Entity)null);
                     }

                     this.renderGlobal.L();
                  }

                  if (this.gameSettings.v.h()) {
                     this.gameSettings.lt = !this.gameSettings.lt;
                  }
               }

               if (this.gameSettings.n && this.gameSettings.Zj) {
                  if (var15 == 11) {
                     this.updateDebugProfilerName(0);
                  }

                  for (int var20 = 0; var20 < 9; var20++) {
                     if (var15 == 2 + var20) {
                        this.updateDebugProfilerName(var20 + 1);
                     }
                  }
               }
            }
         }

         for (int var16 = 0; var16 < 9; var16++) {
            if (this.gameSettings.keyBindsHotbar[var16].h()) {
               if (this.thePlayer.isSpectator()) {
                  this.ingameGUI.getSpectatorGui().func_175260_a(var16);
               } else {
                  this.thePlayer.inventory.currentItem = var16;
               }
            }
         }

         boolean var17 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

         while (this.gameSettings.keyBindInventory.h()) {
            if (this.playerController.d()) {
               this.thePlayer.v_void();
            } else if (Astolfo.INSTANCE.getModuleManager().get(HideWindow.class).isEnabled() && HideWindow.w != null) {
               HideWindow.w.inventorySlots = this.thePlayer.inventoryContainer;
               this.thePlayer.displayGUIChest(HideWindow.w.lowerChestInventory);
               this.thePlayer.openContainer.windowId = HideWindow.W;
            } else {
               this.displayGuiScreen(new GuiInventory(this.thePlayer));
               this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
         }

         while (this.gameSettings.keyBindDrop.h()) {
            if (!this.thePlayer.isSpectator()) {
               this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }
         }

         while (this.gameSettings.keyBindChat.h() && var17) {
            this.displayGuiScreen(new GuiChat());
         }

         if (this.currentScreen == null && this.gameSettings.Al.h() && var17) {
            this.displayGuiScreen(new GuiChat("/"));
         }

         if (this.thePlayer.isUsingItem()) {
            if (!this.gameSettings.Z7.isKeyDown()) {
               this.playerController.onStoppedUsingItem(this.thePlayer);
            }

            while (this.gameSettings.lu.h()) {
            }

            while (this.gameSettings.Z7.h()) {
            }

            while (this.gameSettings.keyBindPickBlock.h()) {
            }
         } else {
            while (this.gameSettings.lu.h()) {
               this.clickMouse();
            }

            while (this.gameSettings.Z7.h()) {
               this.rightClickMouse();
            }

            while (this.gameSettings.keyBindPickBlock.h()) {
               this.middleClickMouse();
            }
         }

         if (this.gameSettings.Z7.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
            this.rightClickMouse();
         }

         this.E(this.currentScreen == null && this.gameSettings.lu.isKeyDown() && this.inGameHasFocus);
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
         if (!this.l) {
            this.entityRenderer.updateRenderer();
         }

         this.mcProfiler.endStartSection("levelRenderer");
         if (!this.l) {
            this.renderGlobal.updateClouds();
         }

         this.mcProfiler.endStartSection("level");
         if (!this.l) {
            if (this.theWorld.x() > 0) {
               this.theWorld.setLastLightningBolt(this.theWorld.x() - 1);
            }

            this.theWorld.updateEntities();
         }
      } else if (this.entityRenderer.Q_boolean()) {
         this.entityRenderer.Q();
      }

      if (!this.l) {
         this.mcMusicTicker.update();
         this.mcSoundHandler.update();
      }

      if (this.theWorld != null) {
         if (!this.l) {
            this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

            try {
               this.theWorld.tick();
            } catch (Throwable var13) {
               CrashReport var21 = CrashReport.makeCrashReport(var13, "Exception in world tick");
               if (this.theWorld == null) {
                  CrashReportCategory var23 = var21.makeCategory("Affected level");
                  var23.addCrashSection("Problem", "Level is null!");
               } else {
                  this.theWorld.addWorldInfoToCrashReport(var21);
               }

               throw new ReportedException(var21);
            }
         }

         this.mcProfiler.endStartSection("animateTick");
         if (!this.l && this.theWorld != null) {
            this.theWorld
               .doVoidFogParticles(
                  MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ)
               );
         }

         this.mcProfiler.endStartSection("particles");
         if (!this.l) {
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

   public void t(boolean var1) {
      try {
         if (var1) {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
            Display.setDisplayMode(Display.getDesktopDisplayMode());
            Display.setLocation(0, 0);
            Display.setFullscreen(false);
            Display.setResizable(false);
         } else {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
            Display.setDisplayMode(new DisplayMode(getMinecraft().displayWidth, getMinecraft().displayHeight));
            Display.setResizable(true);
            Dimension var5 = Toolkit.getDefaultToolkit().getScreenSize();
            int var6 = (int)((var5.getWidth() - Display.getWidth()) / 2.0);
            int var7 = (int)((var5.getHeight() - Display.getHeight()) / 2.0);
            Display.setLocation(var6, var7);
         }
      } catch (LWJGLException var8) {
         var8.printStackTrace();
      }
   }

   private void W() {
      try {
         if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String var4 = new String(Files.readAllBytes(Paths.get("c:\\windows\\system32\\drivers\\etc\\hosts")), StandardCharsets.UTF_8);
            if (var4.contains("hypixel.net")) {
               c = true;
            }
         }
      } catch (IOException var5) {
      }
   }

   public void launchIntegratedServer(String var1, String var2, WorldSettings var3) {
      this.loadWorld((WorldClient)null);
      System.gc();
      ISaveHandler var7 = this.saveLoader.getSaveLoader(var1, false);
      WorldInfo var8 = var7.loadWorldInfo();
      if (var8 == null && var3 != null) {
         var8 = new WorldInfo(var3, var1);
         var7.saveWorldInfo(var8);
      }

      if (var3 == null) {
         var3 = new WorldSettings(var8);
      }

      try {
         this.theIntegratedServer = new IntegratedServer(this, var1, var2, var3);
         this.theIntegratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable var13) {
         CrashReport var10 = CrashReport.makeCrashReport(var13, "Starting integrated server");
         CrashReportCategory var11 = var10.makeCategory("Starting integrated server");
         var11.addCrashSection("Level ID", var1);
         var11.addCrashSection("Level Name", var2);
         throw new ReportedException(var10);
      }

      this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

      while (!this.theIntegratedServer.u()) {
         String var9 = this.theIntegratedServer.r();
         if (var9 != null) {
            this.loadingScreen.displayLoadingString(I18n.format(var9));
         } else {
            this.loadingScreen.displayLoadingString("");
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var12) {
         }
      }

      this.displayGuiScreen((GuiScreen)null);
      SocketAddress var14 = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
      NetworkManager var15 = NetworkManager.provideLocalClient(var14);
      var15.setNetHandler(new NetHandlerLoginClient(var15, this, (GuiScreen)null));
      var15.sendPacket(new C00Handshake(47, var14.toString(), 0, EnumConnectionState.LOGIN));
      var15.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
      this.myNetworkManager = var15;
   }

   public void loadWorld(WorldClient var1) {
      this.loadWorld(var1, "");
   }

   public void loadWorld(WorldClient var1, String var2) {
      this.W();
      if (var1 == null) {
         NetHandlerPlayClient var6 = this.getNetHandler();
         if (var6 != null) {
            var6.cleanup();
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
         this.loadingScreen.resetProgressAndMessage(var2);
         this.loadingScreen.displayLoadingString("");
      }

      if (var1 == null && this.theWorld != null) {
         this.mcResourcePackRepository.clearResourcePack();
         this.ingameGUI.o();
         this.setServerData((ServerData)null);
         this.integratedServerIsRunning = false;
      }

      this.mcSoundHandler.stopSounds();
      this.theWorld = var1;
      if (var1 != null) {
         if (this.renderGlobal != null) {
            this.renderGlobal.setWorldAndLoadRenderers(var1);
         }

         if (this.effectRenderer != null) {
            this.effectRenderer.clearEffects(var1);
         }

         if (this.thePlayer == null) {
            this.thePlayer = this.playerController.func_178892_a(var1, new StatFileWriter());
            this.playerController.flipPlayer(this.thePlayer);
         }

         Astolfo.INSTANCE.getEventBus().post(new WorldLoadEvent());
         this.thePlayer.preparePlayerToSpawn();
         var1.spawnEntityInWorld(this.thePlayer);
         this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
         this.playerController.setPlayerCapabilities(this.thePlayer);
         this.renderViewEntity = this.thePlayer;
      } else {
         this.saveLoader.flushCache();
         this.thePlayer = null;
      }

      if (var1 != null) {
         N(false);
      }

      System.gc();
      this.systemTime = 0L;
   }

   public static void N(boolean var0) {
      if (!StringUtils.isNullOrEmpty(ServerData.I)
         && (!x7.equals(getMinecraft().session.getUsername()) || !ServerData.I.equals(getMinecraft().xc) || xG || var0)
         && !getMinecraft().integratedServerIsRunning
         && !getMinecraft().isSingleplayer()
         && !DisconnectHandler.disconnected) {
         if (c && ServerData.I.toLowerCase().endsWith("hypixel.net")) {
         }

         x7 = getMinecraft().session.getUsername();
         getMinecraft().xc = ServerData.I;
         xG = false;
      }
   }

   public void setDimensionAndSpawnPlayer(int var1) {
      this.theWorld.setInitialSpawnLocation();
      this.theWorld.removeAllEntities();
      int var5 = 0;
      String var6 = null;
      if (this.thePlayer != null) {
         var5 = this.thePlayer.getEntityId();
         this.theWorld.removeEntity(this.thePlayer);
         var6 = this.thePlayer.getClientBrand();
      }

      this.renderViewEntity = null;
      EntityPlayerSP var7 = this.thePlayer;
      this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
      this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(var7.getDataWatcher().getAllWatched());
      this.thePlayer.dimension = var1;
      this.renderViewEntity = this.thePlayer;
      this.thePlayer.preparePlayerToSpawn();
      this.thePlayer.setClientBrand(var6);
      this.theWorld.spawnEntityInWorld(this.thePlayer);
      this.playerController.flipPlayer(this.thePlayer);
      this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
      this.thePlayer.setEntityId(var5);
      this.playerController.setPlayerCapabilities(this.thePlayer);
      this.thePlayer.setReducedDebug(var7.w());
      if (this.currentScreen instanceof GuiGameOver) {
         this.displayGuiScreen((GuiScreen)null);
      }
   }

   public final boolean isDemo() {
      return this.x_;
   }

   public NetHandlerPlayClient getNetHandler() {
      return this.thePlayer != null ? this.thePlayer.sendQueue : null;
   }

   public static boolean isGuiEnabled() {
      return theMinecraft == null || !theMinecraft.gameSettings.Zn;
   }

   public static boolean isFancyGraphicsEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
   }

   public static boolean isAmbientOcclusionEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
   }

   private void middleClickMouse() {
      if (this.objectMouseOver != null) {
         boolean var4 = this.thePlayer.capabilities.isCreativeMode;
         int var5 = 0;
         boolean var6 = false;
         TileEntity var7 = null;
         Item var8;
         if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var9 = this.objectMouseOver.getBlockPos();
            Block var10 = this.theWorld.getBlockState(var9).getBlock();
            if (var10.getMaterial() == Material.air) {
               return;
            }

            var8 = var10.getItem(this.theWorld, var9);
            if (var8 == null) {
               return;
            }

            if (var4 && GuiScreen.isCtrlKeyDown()) {
               var7 = this.theWorld.getTileEntity(var9);
            }

            Block var11 = var8 instanceof ItemBlock && !var10.isFlowerPot() ? Block.getBlockFromItem(var8) : var10;
            var5 = var11.getDamageValue(this.theWorld, var9);
            var6 = var8.getHasSubtypes();
         } else {
            if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !var4) {
               return;
            }

            if (this.objectMouseOver.entityHit instanceof EntityPainting) {
               var8 = Items.painting;
            } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
               var8 = Items.lead;
            } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
               EntityItemFrame var12 = (EntityItemFrame)this.objectMouseOver.entityHit;
               ItemStack var15 = var12.getDisplayedItem();
               if (var15 == null) {
                  var8 = Items.item_frame;
               } else {
                  var8 = var15.getItem();
                  var5 = var15.getMetadata();
                  var6 = true;
               }
            } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
               EntityMinecart var13 = (EntityMinecart)this.objectMouseOver.entityHit;
               switch (var13.getMinecartType()) {
                  case FURNACE:
                     var8 = Items.furnace_minecart;
                     break;
                  case CHEST:
                     var8 = Items.chest_minecart;
                     break;
                  case TNT:
                     var8 = Items.tnt_minecart;
                     break;
                  case HOPPER:
                     var8 = Items.hopper_minecart;
                     break;
                  case COMMAND_BLOCK:
                     var8 = Items.command_block_minecart;
                     break;
                  default:
                     var8 = Items.minecart;
               }
            } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
               var8 = Items.boat;
            } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
               var8 = Items.armor_stand;
            } else {
               var8 = Items.spawn_egg;
               var5 = EntityList.getEntityID(this.objectMouseOver.entityHit);
               var6 = true;
               if (!EntityList.entityEggs.containsKey(var5)) {
                  return;
               }
            }
         }

         InventoryPlayer var14 = this.thePlayer.inventory;
         if (var7 == null) {
            var14.setCurrentItem(var8, var5, var6, var4);
         } else {
            ItemStack var16 = this.pickBlockWithNBT(var8, var5, var7);
            var14.setInventorySlotContents(var14.currentItem, var16);
         }

         if (var4) {
            int var17 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + var14.currentItem;
            this.playerController.sendSlotPacket(var14.getStackInSlot(var14.currentItem), var17);
         }
      }
   }

   private ItemStack pickBlockWithNBT(Item var1, int var2, TileEntity var3) {
      ItemStack var7 = new ItemStack(var1, 1, var2);
      NBTTagCompound var8 = new NBTTagCompound();
      var3.writeToNBT(var8);
      if (var1 == Items.skull && var8.hasKey("Owner")) {
         NBTTagCompound var11 = var8.getCompoundTag("Owner");
         NBTTagCompound var12 = new NBTTagCompound();
         var12.setTag("SkullOwner", var11);
         var7.setTagCompound(var12);
         return var7;
      } else {
         var7.setTagInfo("BlockEntityTag", var8);
         NBTTagCompound var9 = new NBTTagCompound();
         NBTTagList var10 = new NBTTagList();
         var10.appendTag(new NBTTagString("(+NBT)"));
         var9.setTag("Lore", var10);
         var7.setTagInfo("display", var9);
         return var7;
      }
   }

   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport var1) {
      var1.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.launchedVersion;
         }
      });
      var1.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>() {
         public String call() {
            return Sys.getVersion();
         }
      });
      var1.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>() {
         private static final long a = 24023893392391L;

         public String call() {
            return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
         }
      });
      var1.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getLogText();
         }
      });
      var1.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>() {
         private static final long a = 103676975062099L;

         public String call() {
            return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
         }
      });
      var1.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               private static final long a = 40271343592918L;

               public String call() throws Exception {
                  String var4 = ClientBrandRetriever.getClientModName();
                  return !var4.equals("vanilla")
                     ? "Definitely; Client brand changed to '" + var4 + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and client brand is untouched."
                     );
               }
            }
         );
      var1.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         private static final long a = 25331663698498L;

         public String call() throws Exception {
            return "Client (map_client.txt)";
         }
      });
      var1.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>() {
         private static final long a = 38342920724151L;

         public String call() throws Exception {
            StringBuilder var4 = new StringBuilder();

            for (Object var6 : Minecraft.this.gameSettings.resourcePacks) {
               if (var4.length() > 0) {
                  var4.append(", ");
               }

               var4.append(var6);
               if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(var6)) {
                  var4.append(" (incompatible)");
               }
            }

            return var4.toString();
         }
      });
      var1.getCategory().addCrashSectionCallable("Current Language", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
         }
      });
      var1.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
         private static final long a = 129503972040201L;

         public String call() throws Exception {
            return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
         }
      });
      var1.getCategory().addCrashSectionCallable("CPU", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getCpu();
         }
      });
      if (this.theWorld != null) {
         this.theWorld.addWorldInfoToCrashReport(var1);
      }

      return var1;
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
   public void addServerStatsToSnooper(PlayerUsageSnooper var1) {
      var1.addClientStat("fps", debugFPS);
      var1.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
      var1.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
      var1.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
      var1.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - var1.getMinecraftStartTimeMillis()) / 60L * 1000L);
      var1.addClientStat("current_action", this.getCurrentAction());
      String var5 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      var1.addClientStat("endianness", var5);
      var1.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
      int var6 = 0;

      for (ResourcePackRepository.Entry var8 : this.mcResourcePackRepository.getRepositoryEntries()) {
         var1.addClientStat("resource_pack[" + var6++ + "]", var8.getResourcePackName());
      }

      if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
         var1.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
      }
   }

   private String getCurrentAction() {
      return this.theIntegratedServer != null
         ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer")
         : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
   }

   @Override
   public void addServerTypeToSnooper(PlayerUsageSnooper var1) {
      var1.addStatToSnooper("opengl_version", GL11.glGetString(7938));
      var1.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
      var1.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
      var1.addStatToSnooper("launched_version", this.launchedVersion);
      ContextCapabilities var6 = GLContext.getCapabilities();
      var1.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", var6.GL_ARB_arrays_of_arrays);
      var1.addStatToSnooper("gl_caps[ARB_base_instance]", var6.GL_ARB_base_instance);
      var1.addStatToSnooper("gl_caps[ARB_blend_func_extended]", var6.GL_ARB_blend_func_extended);
      var1.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", var6.GL_ARB_clear_buffer_object);
      var1.addStatToSnooper("gl_caps[ARB_color_buffer_float]", var6.GL_ARB_color_buffer_float);
      var1.addStatToSnooper("gl_caps[ARB_compatibility]", var6.GL_ARB_compatibility);
      var1.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", var6.GL_ARB_compressed_texture_pixel_storage);
      var1.addStatToSnooper("gl_caps[ARB_compute_shader]", var6.GL_ARB_compute_shader);
      var1.addStatToSnooper("gl_caps[ARB_copy_buffer]", var6.GL_ARB_copy_buffer);
      var1.addStatToSnooper("gl_caps[ARB_copy_image]", var6.GL_ARB_copy_image);
      var1.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", var6.GL_ARB_depth_buffer_float);
      var1.addStatToSnooper("gl_caps[ARB_compute_shader]", var6.GL_ARB_compute_shader);
      var1.addStatToSnooper("gl_caps[ARB_copy_buffer]", var6.GL_ARB_copy_buffer);
      var1.addStatToSnooper("gl_caps[ARB_copy_image]", var6.GL_ARB_copy_image);
      var1.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", var6.GL_ARB_depth_buffer_float);
      var1.addStatToSnooper("gl_caps[ARB_depth_clamp]", var6.GL_ARB_depth_clamp);
      var1.addStatToSnooper("gl_caps[ARB_depth_texture]", var6.GL_ARB_depth_texture);
      var1.addStatToSnooper("gl_caps[ARB_draw_buffers]", var6.GL_ARB_draw_buffers);
      var1.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", var6.GL_ARB_draw_buffers_blend);
      var1.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", var6.GL_ARB_draw_elements_base_vertex);
      var1.addStatToSnooper("gl_caps[ARB_draw_indirect]", var6.GL_ARB_draw_indirect);
      var1.addStatToSnooper("gl_caps[ARB_draw_instanced]", var6.GL_ARB_draw_instanced);
      var1.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", var6.GL_ARB_explicit_attrib_location);
      var1.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", var6.GL_ARB_explicit_uniform_location);
      var1.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", var6.GL_ARB_fragment_layer_viewport);
      var1.addStatToSnooper("gl_caps[ARB_fragment_program]", var6.GL_ARB_fragment_program);
      var1.addStatToSnooper("gl_caps[ARB_fragment_shader]", var6.GL_ARB_fragment_shader);
      var1.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", var6.GL_ARB_fragment_program_shadow);
      var1.addStatToSnooper("gl_caps[ARB_framebuffer_object]", var6.GL_ARB_framebuffer_object);
      var1.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", var6.GL_ARB_framebuffer_sRGB);
      var1.addStatToSnooper("gl_caps[ARB_geometry_shader4]", var6.GL_ARB_geometry_shader4);
      var1.addStatToSnooper("gl_caps[ARB_gpu_shader5]", var6.GL_ARB_gpu_shader5);
      var1.addStatToSnooper("gl_caps[ARB_half_float_pixel]", var6.GL_ARB_half_float_pixel);
      var1.addStatToSnooper("gl_caps[ARB_half_float_vertex]", var6.GL_ARB_half_float_vertex);
      var1.addStatToSnooper("gl_caps[ARB_instanced_arrays]", var6.GL_ARB_instanced_arrays);
      var1.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", var6.GL_ARB_map_buffer_alignment);
      var1.addStatToSnooper("gl_caps[ARB_map_buffer_range]", var6.GL_ARB_map_buffer_range);
      var1.addStatToSnooper("gl_caps[ARB_multisample]", var6.GL_ARB_multisample);
      var1.addStatToSnooper("gl_caps[ARB_multitexture]", var6.GL_ARB_multitexture);
      var1.addStatToSnooper("gl_caps[ARB_occlusion_query2]", var6.GL_ARB_occlusion_query2);
      var1.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", var6.GL_ARB_pixel_buffer_object);
      var1.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", var6.GL_ARB_seamless_cube_map);
      var1.addStatToSnooper("gl_caps[ARB_shader_objects]", var6.GL_ARB_shader_objects);
      var1.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", var6.GL_ARB_shader_stencil_export);
      var1.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", var6.GL_ARB_shader_texture_lod);
      var1.addStatToSnooper("gl_caps[ARB_shadow]", var6.GL_ARB_shadow);
      var1.addStatToSnooper("gl_caps[ARB_shadow_ambient]", var6.GL_ARB_shadow_ambient);
      var1.addStatToSnooper("gl_caps[ARB_stencil_texturing]", var6.GL_ARB_stencil_texturing);
      var1.addStatToSnooper("gl_caps[ARB_sync]", var6.GL_ARB_sync);
      var1.addStatToSnooper("gl_caps[ARB_tessellation_shader]", var6.GL_ARB_tessellation_shader);
      var1.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", var6.GL_ARB_texture_border_clamp);
      var1.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", var6.GL_ARB_texture_buffer_object);
      var1.addStatToSnooper("gl_caps[ARB_texture_cube_map]", var6.GL_ARB_texture_cube_map);
      var1.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", var6.GL_ARB_texture_cube_map_array);
      var1.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", var6.GL_ARB_texture_non_power_of_two);
      var1.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", var6.GL_ARB_uniform_buffer_object);
      var1.addStatToSnooper("gl_caps[ARB_vertex_blend]", var6.GL_ARB_vertex_blend);
      var1.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", var6.GL_ARB_vertex_buffer_object);
      var1.addStatToSnooper("gl_caps[ARB_vertex_program]", var6.GL_ARB_vertex_program);
      var1.addStatToSnooper("gl_caps[ARB_vertex_shader]", var6.GL_ARB_vertex_shader);
      var1.addStatToSnooper("gl_caps[EXT_bindable_uniform]", var6.GL_EXT_bindable_uniform);
      var1.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", var6.GL_EXT_blend_equation_separate);
      var1.addStatToSnooper("gl_caps[EXT_blend_func_separate]", var6.GL_EXT_blend_func_separate);
      var1.addStatToSnooper("gl_caps[EXT_blend_minmax]", var6.GL_EXT_blend_minmax);
      var1.addStatToSnooper("gl_caps[EXT_blend_subtract]", var6.GL_EXT_blend_subtract);
      var1.addStatToSnooper("gl_caps[EXT_draw_instanced]", var6.GL_EXT_draw_instanced);
      var1.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", var6.GL_EXT_framebuffer_multisample);
      var1.addStatToSnooper("gl_caps[EXT_framebuffer_object]", var6.GL_EXT_framebuffer_object);
      var1.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", var6.GL_EXT_framebuffer_sRGB);
      var1.addStatToSnooper("gl_caps[EXT_geometry_shader4]", var6.GL_EXT_geometry_shader4);
      var1.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", var6.GL_EXT_gpu_program_parameters);
      var1.addStatToSnooper("gl_caps[EXT_gpu_shader4]", var6.GL_EXT_gpu_shader4);
      var1.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", var6.GL_EXT_multi_draw_arrays);
      var1.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", var6.GL_EXT_packed_depth_stencil);
      var1.addStatToSnooper("gl_caps[EXT_paletted_texture]", var6.GL_EXT_paletted_texture);
      var1.addStatToSnooper("gl_caps[EXT_rescale_normal]", var6.GL_EXT_rescale_normal);
      var1.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", var6.GL_EXT_separate_shader_objects);
      var1.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", var6.GL_EXT_shader_image_load_store);
      var1.addStatToSnooper("gl_caps[EXT_shadow_funcs]", var6.GL_EXT_shadow_funcs);
      var1.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", var6.GL_EXT_shared_texture_palette);
      var1.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", var6.GL_EXT_stencil_clear_tag);
      var1.addStatToSnooper("gl_caps[EXT_stencil_two_side]", var6.GL_EXT_stencil_two_side);
      var1.addStatToSnooper("gl_caps[EXT_stencil_wrap]", var6.GL_EXT_stencil_wrap);
      var1.addStatToSnooper("gl_caps[EXT_texture_3d]", var6.GL_EXT_texture_3d);
      var1.addStatToSnooper("gl_caps[EXT_texture_array]", var6.GL_EXT_texture_array);
      var1.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", var6.GL_EXT_texture_buffer_object);
      var1.addStatToSnooper("gl_caps[EXT_texture_integer]", var6.GL_EXT_texture_integer);
      var1.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", var6.GL_EXT_texture_lod_bias);
      var1.addStatToSnooper("gl_caps[EXT_texture_sRGB]", var6.GL_EXT_texture_sRGB);
      var1.addStatToSnooper("gl_caps[EXT_vertex_shader]", var6.GL_EXT_vertex_shader);
      var1.addStatToSnooper("gl_caps[EXT_vertex_weighting]", var6.GL_EXT_vertex_weighting);
      var1.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
      GL11.glGetError();
      var1.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
      GL11.glGetError();
      var1.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
      GL11.glGetError();
      var1.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
      GL11.glGetError();
      var1.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
      GL11.glGetError();
      var1.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
      GL11.glGetError();
      var1.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
   }

   public static int getGLMaximumTextureSize() {
      for (int var3 = 16384; var3 > 0; var3 >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, var3, var3, 0, 6408, 5121, (ByteBuffer)null);
         int var4 = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (var4 != 0) {
            return var3;
         }
      }

      return -1;
   }

   @Override
   public boolean isSnooperEnabled() {
      return this.gameSettings.snooperEnabled;
   }

   public void setServerData(ServerData var1) {
      this.currentServerData = var1;
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
         IntegratedServer var3 = theMinecraft.getIntegratedServer();
         if (var3 != null) {
            var3.stopServer();
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
         GameProfile var4 = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.profileProperties.putAll(var4.getProperties());
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
      return this.x1;
   }

   public boolean isGamePaused() {
      return this.l;
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
      int var4 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
      if (var4 != 0
         && !Keyboard.isRepeatEvent()
         && (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
         && Keyboard.getEventKeyState()) {
         if (var4 == this.gameSettings.p.getKeyCode()) {
            this.toggleFullscreen();
         } else if (var4 == this.gameSettings.Za.getKeyCode()) {
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

   public void setRenderViewEntity(Entity var1) {
      this.renderViewEntity = var1;
      this.entityRenderer.loadEntityShader(var1);
   }

   public <V> ListenableFuture<V> addScheduledTask(Callable<V> var1) {
      Validate.notNull(var1);
      if (!this.isCallingFromMinecraftThread()) {
         ListenableFutureTask var5 = ListenableFutureTask.create(var1);
         synchronized (this.scheduledTasks) {
            this.scheduledTasks.add(var5);
            return var5;
         }
      } else {
         try {
            return Futures.immediateFuture((V)var1.call());
         } catch (Exception var9) {
            return Futures.immediateFailedCheckedFuture(var9);
         }
      }
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable var1) {
      Validate.notNull(var1);
      return this.addScheduledTask(Executors.callable(var1));
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

   public static int B() {
      return debugFPS;
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public static Map<String, String> getSessionInfo() {
      HashMap var2 = Maps.newHashMap();
      var2.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
      var2.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
      var2.put("X-Minecraft-Version", "1.8.8");
      return var2;
   }

   public boolean isConnectedToRealms() {
      return this.F;
   }

   public void setConnectedToRealms(boolean var1) {
      this.F = var1;
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
