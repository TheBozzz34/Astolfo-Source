package bypass;

import bypass.account.AltManager;
import bypass.account.AlteningManager;
import bypass.chat.ChatManager;
import bypass.command.CommandManager;
import bypass.event.bus.EventBus;
import bypass.font.FontManager;
import bypass.gui.clickgui.defaultgui.DefaultPanelManager;
import bypass.gui.clickgui.sidebar.SidebarGui;
import bypass.gui.clickgui.transparent.TransparentPanelManager;
import bypass.loader.check.LoaderGuard;
import bypass.manager.Loadable;
import bypass.module.ModuleManager;
import bypass.notification.NotificationManager;
import bypass.relation.RelationManager;
import bypass.render.BaseColor;
import bypass.render.font.CustomFontRenderer;
import bypass.script.ScriptManager;
import bypass.server.ServerManager;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.user.AstolfoUserManager;
import bypass.via.ViaFabric;
import java.awt.Color;
import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;

public enum Astolfo implements Loadable {
   INSTANCE;

   private List<Loadable> loadables;
   private LoaderGuard B;
   private RelationManager relationManager;
   private EventBus eventBus;
   private ModuleManager moduleManager;
   private AstolfoUserManager userManager;
   private CommandManager commandManager;
   private TransparentPanelManager transparentPanelManager;
   private DefaultPanelManager defaultPanelManager;
   private SidebarGui sidebarGui;
   private CustomFontRenderer customFontRenderer;
   private FontRenderer fontRenderer;
   private AltManager altManager;
   private NotificationManager notificationManager;
   private AlteningManager alteningManager;
   private FontManager fontManager;
   private ChatManager chatManager;
   private ServerManager serverManager;
   private ScriptManager scriptManager;
   private final ColorSetting color = new ColorSetting("Color", "client color", BaseColor.PURPLE.z());
   private final BooleanSetting cFont = new BooleanSetting("CFont", "custom font", true);
   private final BooleanSetting sense = new BooleanSetting("Sense", "Advanced technology", true);
   private File directory;
   private boolean J = true;
   public static boolean failedAndRunning;
   private String configName = "default";
   private Proxy proxy = Proxy.NO_PROXY;
   private static final Astolfo[] V = new Astolfo[]{INSTANCE};
   private static final long b = 49654332244602L;

   Astolfo() {
      this.loadables = new ArrayList<>();
   }

   @Override
   public void load() {
      long var1 = b ^ 56709646454107L;
      long var3 = var1 ^ 22030267799943L;
      long var5 = var1 ^ 123509045112395L;
      this.register(var5, this.x());
      ViaFabric.INSTANCE.init();
      this.serverManager = new ServerManager();
      Runtime.getRuntime().addShutdownHook(new Thread(this::a));
   }

   @Override
   public void a() {
      this.loadables.forEach(Loadable::a);
   }

   public void register(long var1, Loadable var3) {
      if (!this.loadables.contains(var3)) {
         this.loadables.add(var3);
         var3.load();
      }
   }

   public String getName() {
      return Character.toString('A')
         + Character.toString('s')
         + Character.toString('t')
         + Character.toString('o')
         + Character.toString('l')
         + Character.toString('f')
         + Character.toString('o');
   }

   public String getVersion() {
      return "latest";
   }

   public CommandManager getCommandManager() {
      if (this.commandManager == null) {
         this.commandManager = new CommandManager();
      }

      return this.commandManager;
   }

   public RelationManager getRelationManager() {
      if (this.relationManager == null) {
         this.relationManager = new RelationManager(this.directory);
      }

      return this.relationManager;
   }

   public ScriptManager getScriptManager() {
      if (this.scriptManager == null) {
         this.scriptManager = new ScriptManager();
      }

      return this.scriptManager;
   }

   public AltManager getAltManager() {
      if (this.altManager == null) {
         this.altManager = new AltManager(this.directory);
      }

      return this.altManager;
   }

   public AlteningManager getAlteningManager() {
      if (this.alteningManager == null) {
         this.alteningManager = new AlteningManager(this.directory);
      }

      return this.alteningManager;
   }

   public FontManager getFontManager() {
      if (this.fontManager == null) {
         this.fontManager = new FontManager(this.directory);
      }

      return this.fontManager;
   }

   public ChatManager getChatManager() {
      if (this.chatManager == null) {
         this.chatManager = new ChatManager(this.directory);
      }

      return this.chatManager;
   }

   public ModuleManager getModuleManager() {
      if (this.moduleManager == null) {
         this.moduleManager = new ModuleManager();
      }

      return this.moduleManager;
   }

   public AstolfoUserManager getUserManager() {
      if (this.userManager == null) {
         this.userManager = new AstolfoUserManager();
      }

      return this.userManager;
   }

   public TransparentPanelManager getTransparentPanelManager() {
      if (this.transparentPanelManager == null) {
         this.transparentPanelManager = new TransparentPanelManager();
      }

      return this.transparentPanelManager;
   }

   public DefaultPanelManager getDefaultPanelManager() {
      if (this.defaultPanelManager == null) {
         this.defaultPanelManager = new DefaultPanelManager();
      }

      return this.defaultPanelManager;
   }

   public SidebarGui getSidebarGui() {
      if (this.sidebarGui == null) {
         this.sidebarGui = new SidebarGui();
      }

      return this.sidebarGui;
   }

   public NotificationManager getNotificationManager() {
      if (this.notificationManager == null && this.eventBus != null) {
         this.notificationManager = new NotificationManager();
      }

      return this.notificationManager;
   }

   public LoaderGuard x() {
      if (this.B == null) {
         this.B = new LoaderGuard();
      }

      return this.B;
   }

   public ServerManager getServerManager() {
      return this.serverManager;
   }

   public LoaderGuard W(LoaderGuard var1) {
      this.B = var1;
      return var1;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public void setProxy(Proxy var1) {
      this.proxy = var1;
   }

   public File getDirectory() {
      return this.directory;
   }

   public void setDirectory(File var1) {
      this.directory = var1;
   }

   public EventBus getEventBus() {
      return this.eventBus;
   }

   public void setEventBus(EventBus var1) {
      this.eventBus = var1;
   }

   public CustomFontRenderer getCustomFontRenderer() {
      return this.customFontRenderer;
   }

   public void setCustomFontRenderer(CustomFontRenderer var1) {
      this.customFontRenderer = var1;
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public void setFontRenderer(FontRenderer var1) {
      this.fontRenderer = var1;
   }

   public Color getColor() {
      return this.getColorSetting().getValue();
   }

   public BooleanSetting getCFontSetting() {
      return this.cFont;
   }

   public BooleanSetting getSenseSetting() {
      return this.sense;
   }

   public ColorSetting getColorSetting() {
      return this.color;
   }

   public void J(boolean var1) {
      this.J = var1;
   }

   public String getConfigName() {
      return this.configName;
   }

   public void setConfigName(String var1) {
      this.configName = var1;
   }
}
