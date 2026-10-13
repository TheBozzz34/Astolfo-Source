package bypass.module.visuals;

import bypass.auth.DisconnectHandler;
import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.event.world.WorldLoadEvent;
import bypass.gui.TabGui;
import bypass.gui.clickgui.GuiMode;
import bypass.gui.hud.CompassHud;
import bypass.gui.hud.PacketGraphHud;
import bypass.gui.hud.PacketSnapshot;
import bypass.gui.hud.StatsHud;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.module.combat.KillAura;
import bypass.module.other.AutoHypixel;
import bypass.module.other.AutoHypixelAutoCheckerGameMode;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.setting.StringSetting;
import bypass.util.GameUtil;
import bypass.util.MathUtil;
import bypass.util.Stopwatch;
import bypass.via.ViaFabric;
import java.awt.Color;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class HUD extends Module {
   public static final BooleanSetting toolTips = new BooleanSetting("Tool-Tips", "Shows tool tips for items in the gui", false);
   public static final BooleanSetting tabGui = new BooleanSetting("Tab-Gui", "Tab gui", true);
   public static final BooleanSetting cFont = new BooleanSetting("CFont", "Enables cfont", true);
   public static final NumberSetting<Float> hudScale = new NumberSetting<>("HUD-Scale", "Scale of da hud", 2.0F, 1.0F, 4.0F);
   private static final MapSetting watermark = new MapSettingBuilder("Watermark")
      .ordered()
      .description("array options")
      .add("Display", new StringSetting("Display", "Client watermark", client.getName()))
      .add("Enabled", new BooleanSetting("Enabled", "Enables watermark", true))
      .add("Mode", new EnumSetting<>("Mode", "Watermark mode", HUDWatermarkMode.COLOR))
      .build();
   public static final MapSetting chatSettings = new MapSettingBuilder("Chat")
      .ordered()
      .description("better chat")
      .add("Smooth", new BooleanSetting("Smooth", "Enables smoothing", false))
      .add("Clear", new BooleanSetting("Clear", "Enables clear", false))
      .build();
   public static final MapSetting graphs = new MapSettingBuilder("Graphs")
      .ordered()
      .description("better chat")
      .add("Incoming", new BooleanSetting("Incoming", "Incoming packets", false))
      .add("Outgoing", new BooleanSetting("Outgoing", "Outgoing packets", false))
      .add("Show-Average", new BooleanSetting("Show-Average", "Show average", false))
      .add("Background", new ColorSetting("Background", "Background color", new Color(1, 1, 1, 50)))
      .add("Line", new ColorSetting("Line", "Line color", Color.WHITE))
      .add("Pos-X-1", new NumberSetting<>("Pos-X-1", "Pos", 150.0F, 1.0F, 50000.0F))
      .add("Pos-Y-1", new NumberSetting<>("Pos-Y-1", "Pos", 150.0F, 1.0F, 50000.0F))
      .add("Pos-X-2", new NumberSetting<>("Pos-X-2", "Pos", 350.0F, 1.0F, 50000.0F))
      .add("Pos-Y-2", new NumberSetting<>("Pos-Y-2", "Pos", 150.0F, 1.0F, 50000.0F))
      .build();
   private final MapSetting array = new MapSettingBuilder("Array")
      .ordered()
      .description("array options")
      .add("Animation-", new EnumSetting<>("Animation", "Animates array", HUDArrayAnimationMode.IN))
      .add("Spacing", new NumberSetting<>("Spacing", "Spacing of each module", 0, 0, 2))
      .add("Background-", new NumberSetting<>("Background", "Background", 60, 0, 255))
      .add("Enabled", new BooleanSetting("Enabled", "Enables array", true))
      .add("Sort", new EnumSetting<>("Sort", "sort", HUDArraySortMode.WIDTH))
      .add("Color", new ColorSetting("Color", "color", client.getColorSetting().getValue()))
      .add("Mode", new EnumSetting<>("Mode", "cool", HUDArrayMode.WAVY))
      .add("Bar", new EnumSetting<>("Bar", "cool", HUDArrayBarMode.NONE))
      .add("Position", new EnumSetting<>("Position", "Position where the array will be", HUDArrayPositionMode.UP))
      .build();
   private final MapSetting information = new MapSettingBuilder("Information")
      .ordered()
      .description("informations")
      .add("Version", new BooleanSetting("Version", "Shows Version", true))
      .add("TPS", new BooleanSetting("TPS", "Shows TPS", false))
      .add("Speed", new BooleanSetting("Speed", "Shows Speed", false))
      .add("Cords", new BooleanSetting("Cords", "Shows Cords", false))
      .add("Potions", new BooleanSetting("Potions", "Shows Potions", false))
      .add("Ping", new BooleanSetting("Ping", "Shows Ping", false))
      .add("FPS", new BooleanSetting("FPS", "Shows FPS", false))
      .add("Direction", new BooleanSetting("Direction", "Shows your facing direction", false))
      .add("Pos-X", new NumberSetting<>("Pos-X", "Pos", 150.0F, 1.0F, 50000.0F))
      .add("Pos-Y", new NumberSetting<>("Pos-Y", "Pos", 150.0F, 1.0F, 50000.0F))
      .build();
   public static final MapSetting gui = new MapSettingBuilder("GUI")
      .ordered()
      .description("Gui options")
      .add("Mode", new EnumSetting<>("Mode", "GUI mode", GuiMode.DEFAULT))
      .add("Scale", new NumberSetting<>("Scale", "Scale of gui", 1.0F, 0.5F, 2.0F))
      .add("Category-Colors", new BooleanSetting("Category-Colors", "Category Colors", true))
      .add("Left-Align", new BooleanSetting("Left-Align", "Left-Align", true))
      .build();
   private static final float[] tickRates = new float[20];
   private int tickRateIndex = 0;
   private long lastTimeUpdate;
   public static final boolean isDevelopment = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("jdwp");
   private final Stopwatch unusedTimer = new Stopwatch();
   private TabGui tabGuiInstance;
   private final Date startDate = new Date();
   private final LocalDate buildDate = this.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
   private static List<HUDFlagColor> flagColors;
   private static List<Color> unusedColors = new ArrayList<>();
   public static float arrayListBottom;
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = var1x -> {
      this.tickRateIndex = 0;
      this.lastTimeUpdate = -1L;
      Arrays.fill(tickRates, 0.0F);
   };
   private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
   private CompassHud compass;
   private PacketGraphHud outgoingGraph;
   private PacketGraphHud incomingGraph;
   private StatsHud statsHud;
   private final ArrayBlockingQueue<PacketSnapshot> packetHistory = new ArrayBlockingQueue<>(50);
   private List<Packet<?>> outgoingPackets = new ArrayList<>();
   private List<Packet<?>> incomingPackets = new ArrayList<>();
   private boolean unusedFlag;
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = var1x -> {
      long var2 = cb ^ 117444247628891L;
      long var10001 = var2 ^ 72124163405718L;
      int var4 = (int)((var2 ^ 72124163405718L) >>> 32);
      int var5 = (int)((var2 ^ 72124163405718L) << 32 >>> 48);
      int var6 = (int)(var10001 << 48 >>> 48);
      long var7 = var2 ^ 10229601044616L;
      long var9 = var2 ^ 12051091349271L;
      long var11 = (var2 ^ 63108773452726L) >>> 16;
      int var13 = (int)((var2 ^ 63108773452726L) << 48 >>> 48);
      if (var1x.getType() == RenderType.SCREEN && !mc.gameSettings.n) {
         HUDWatermarkMode var15 = this.getWatermarkMode();
         float var16 = hudScale.getValue();
         double var17 = var1x.getResolution().getScaleFactor() / Math.pow(var1x.getResolution().getScaleFactor(), 2.0);
         GlStateManager.pushMatrix();
         GlStateManager.scale(var17 * var16, var17 * var16, var17 * var16);
         if (isWatermarkEnabled()) {
            String var19 = getWatermarkText();
            if (isPowerBranding()) {
               RenderUtil.drawRect(2.0, 2.0, 55.0, 19.0, new Color(1, 1, 1, 150).getRGB());
               RenderUtil.drawRect(2.3F, 2.3F, 4.0, 18.3F, client.getColorSetting().getValue().getRGB());
               GuiMainMenu.M.drawStringWithShadow("P", 6.0F, 3.5F, client.getColorSetting().getValue().getRGB());
               GuiMainMenu.M.drawStringWithShadow("ower", 6 + GuiMainMenu.M.getStringWidth("P") + 1, 3.5F, -1);
            } else {
               var19 = var19.replace("&", "§");
               var19 = var19.replace("%fps%", String.valueOf(Minecraft.B()));
               var19 = var19.replace("%time%", this.timeFormat.format(new Date()));
               if (this.information.getSetting("Ping").asBoolean()
                  && mc.thePlayer.getPlayerInfo() != null
                  && mc.thePlayer.getPlayerInfo().getResponseTime() > 1) {
                  var19 = var19.replace("%ping%", mc.thePlayer.getPlayerInfo().getResponseTime() + "ms");
               } else {
                  var19 = var19.replace("%ping%", "");
               }

               if (mc.getCurrentServerData() != null) {
                  var19 = var19.replace("%ip%", mc.getCurrentServerData().serverIP);
               }

               var19 = var19.replace("%user%", mc.mouseHelper.Y().getAccount().getUsername());
               if (DisconnectHandler.disconnected) {
                  var19 = var19 + EnumChatFormatting.GRAY + " - DISCONNECTED";
               }

               if (!StringUtils.isNullOrEmpty(var19)) {
                  switch (var15) {
                     case COLOR:
                        client.getCustomFontRenderer().drawStringWithShadow("§l" + var19.charAt(0), 1.0F, 1.0F, client.getColor().getRGB());
                        client.getCustomFontRenderer()
                           .drawStringWithShadow(
                              var19.substring(1), 2 + client.getCustomFontRenderer().getStringWidth(var19.substring(0, 1)), 1.0F, Color.WHITE.getRGB()
                           );
                        break;
                     case WHITE:
                        client.getCustomFontRenderer().drawStringWithShadow(var19, 1.0F, 1.0F, -1);
                        break;
                     case BREATH:
                        Color var20 = client.getColorSetting().getValue();
                        float var21 = 1.0F
                           - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
                        int var22 = new Color(
                              MathHelper.clamp_int((int)(var20.getRed() * var21), 0, 255),
                              MathHelper.clamp_int((int)(var20.getGreen() * var21), 0, 255),
                              MathHelper.clamp_int((int)(var20.getBlue() * var21), 0, 255)
                           )
                           .getRGB();
                        client.getCustomFontRenderer().drawStringWithShadow("§l" + var19.charAt(0), 1.0F, 1.0F, var22);
                        client.getCustomFontRenderer()
                           .drawStringWithShadow(
                              var19.substring(1), 2 + client.getCustomFontRenderer().getStringWidth(var19.substring(0, 1)), 1.0F, Color.WHITE.getRGB()
                           );
                        break;
                     case SOLID:
                        client.getCustomFontRenderer().drawStringWithShadow(var19, 1.0F, 1.0F, client.getColorSetting().getValue().getRGB());
                        break;
                     case RAINBOW:
                        int var23 = Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.4F, 0.8F);
                        client.getCustomFontRenderer().drawStringWithShadow("§l" + var19.charAt(0), 1.0F, 1.0F, var23);
                        client.getCustomFontRenderer()
                           .drawStringWithShadow(
                              var19.substring(1), 2 + client.getCustomFontRenderer().getStringWidth(var19.substring(0, 1)), 1.0F, Color.WHITE.getRGB()
                           );
                        break;
                     case RETARD:
                        GL11.glPushMatrix();
                        String var24 = "%s ︳ %s";
                        int var25 = Math.min(
                           mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1
                              ? mc.thePlayer.getPlayerInfo().getResponseTime()
                              : (
                                 mc.getCurrentServerData() != null && mc.getCurrentServerData().pingToServer > 0L
                                    ? (int)mc.getCurrentServerData().pingToServer
                                    : 0
                              ),
                           999
                        );
                        if (var25 > 0) {
                           var24 = var24 + " ︳ ping: %sms";
                        }

                        if (!ViaFabric.INSTANCE.isNative()) {
                           var24 = var24 + " ︳ version: " + ViaFabric.INSTANCE.getVersion().getName();
                        }

                        var24 = String.format(
                           var24,
                           client.getName() + (isDevelopment ? " [DEVELOPMENT]" : (client.getVersion().contains("BETA") ? " [BETA]" : "")),
                           mc.mouseHelper.Y().getAccount().getUsername(),
                           var25
                        );
                        RenderUtil.drawRect(3.0, 3.0, 10 + client.getFontRenderer().getStringWidth(var24), 18.0, new Color(1, 1, 1, 80).getRGB());
                        RenderUtil.drawRect(3.0, 3.0, 10 + client.getFontRenderer().getStringWidth(var24), 4.0, client.getColorSetting().getValue().getRGB());
                        RenderUtil.drawRect(
                           3.0, 4.0, 10 + client.getFontRenderer().getStringWidth(var24), 5.0, client.getColorSetting().getValue().darker().getRGB()
                        );
                        RenderUtil.drawRect(
                           3.0, 5.0, 10 + client.getFontRenderer().getStringWidth(var24), 6.0, client.getColorSetting().getValue().darker().darker().getRGB()
                        );
                        client.getFontRenderer().O(var24, 7, 8, -1);
                        GL11.glPopMatrix();
                        break;
                     case ROTATE:
                        this.drawAstolfoLogo();
                  }
               }
            }
         }

         if (tabGui.getValue()) {
            if (watermark.getSetting("Enabled").asBoolean()) {
               if (var15 == HUDWatermarkMode.RETARD) {
                  this.getTabGui().P(20.0F);
                  this.getTabGui().C(3.0F);
               } else if (var15 == HUDWatermarkMode.ROTATE) {
                  this.getTabGui().C(0.0F);
                  this.getTabGui().P(25.0F);
               } else {
                  this.getTabGui().C(0.0F);
                  this.getTabGui().P(13.0F);
               }
            } else {
               this.getTabGui().C(0.0F);
               this.getTabGui().P(2.0F);
            }

            this.getTabGui().render(var1x.getResolution(), mc);
         }

         if (this.array.getSetting("Enabled").asBoolean()) {
            this.renderArrayList();
         }

         this.drawLeftInfo();
         this.drawRightInfo(var1x.getGuiIngame());
         GlStateManager.popMatrix();
         if (this.information.getSetting("Direction").asBoolean()) {
            NumberSetting var32 = (NumberSetting)this.information.getSetting("Pos-X");
            NumberSetting var34 = (NumberSetting)this.information.getSetting("Pos-Y");
            if (this.compass == null) {
               this.compass = new CompassHud(var32.asFloat(), var34.asFloat(), 75, 20);
            } else {
               int var38 = var1x.getResolution().getScaledHeight();
               int var42 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
               int var46 = var38 - Mouse.getY() * var38 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  var32.setValue(this.compass.M());
                  var34.setValue(this.compass.E_float());
               } else if ((Float)var32.getValue() != this.compass.M() || (Float)var34.getValue() != this.compass.E_float()) {
                  if (this.compass.M() != (Float)var32.getValue()) {
                     this.compass.o((Float)var32.getValue());
                  }

                  if (this.compass.E_float() != (Float)var34.getValue()) {
                     this.compass.q((Float)var34.getValue());
                  }
               }

               this.compass.i(var1x.getResolution().getScaledWidth());
               this.compass.render(var42, var46);
               if (mc.currentScreen instanceof GuiChat && this.compass.u_boolean() && Mouse.isButtonDown(0)) {
                  this.compass.T(var42, var46);
               }

               if (Mouse.isButtonDown(0)
                  && this.compass.u((int)this.compass.M() - 75, (int)this.compass.E_float(), this.compass.x() + 75, this.compass.Y(), var42, var46)) {
                  this.compass.F(true, var42, var46);
               } else {
                  this.compass.x(false);
               }
            }
         } else {
            this.compass = null;
         }

         boolean var33 = graphs.getSetting("Show-Average").asBoolean();
         if (graphs.getSetting("Outgoing").asBoolean()) {
            NumberSetting var35 = (NumberSetting)graphs.getSetting("Pos-X-1");
            NumberSetting var39 = (NumberSetting)graphs.getSetting("Pos-Y-1");
            if (this.outgoingGraph == null) {
               this.outgoingGraph = new PacketGraphHud(var35.asFloat(), var39.asFloat(), 150, 39);
            } else {
               int var43 = var1x.getResolution().getScaledHeight();
               int var47 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
               int var51 = var43 - Mouse.getY() * var43 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  var35.setValue(this.outgoingGraph.M());
                  var39.setValue(this.outgoingGraph.E_float());
               } else if ((Float)var35.getValue() != this.outgoingGraph.M() || (Float)var39.getValue() != this.outgoingGraph.E_float()) {
                  if (this.outgoingGraph.M() != (Float)var35.getValue()) {
                     this.outgoingGraph.o((Float)var35.getValue());
                  }

                  if (this.outgoingGraph.E_float() != (Float)var39.getValue()) {
                     this.outgoingGraph.q((Float)var39.getValue());
                  }
               }

               this.outgoingGraph.K(this.packetHistory);
               this.outgoingGraph.N(var33);
               this.outgoingGraph.render(var47, var51);
               if (mc.currentScreen instanceof GuiChat && this.outgoingGraph.u_boolean() && Mouse.isButtonDown(0)) {
                  this.outgoingGraph.T(var47, var51);
               }

               if (Mouse.isButtonDown(0) && this.outgoingGraph.G(var47, var51)) {
                  this.outgoingGraph.F(true, var47, var51);
               } else {
                  this.outgoingGraph.x(false);
               }
            }
         } else {
            this.outgoingGraph = null;
         }

         if (graphs.getSetting("Incoming").asBoolean()) {
            NumberSetting var36 = (NumberSetting)graphs.getSetting("Pos-X-2");
            NumberSetting var40 = (NumberSetting)graphs.getSetting("Pos-Y-2");
            if (this.incomingGraph == null) {
               this.incomingGraph = new PacketGraphHud(var36.asFloat(), var40.asFloat(), 150, 39);
            } else {
               int var44 = var1x.getResolution().getScaledHeight();
               int var48 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
               int var52 = var44 - Mouse.getY() * var44 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  var36.setValue(this.incomingGraph.M());
                  var40.setValue(this.incomingGraph.E_float());
               } else if ((Float)var36.getValue() != this.incomingGraph.M() || (Float)var40.getValue() != this.incomingGraph.E_float()) {
                  if (this.incomingGraph.M() != (Float)var36.getValue()) {
                     this.incomingGraph.o((Float)var36.getValue());
                  }

                  if (this.incomingGraph.E_float() != (Float)var40.getValue()) {
                     this.incomingGraph.q((Float)var40.getValue());
                  }
               }

               this.incomingGraph.K(this.packetHistory);
               this.incomingGraph.r(true);
               this.incomingGraph.N(var33);
               this.incomingGraph.render(var48, var52);
               if (mc.currentScreen instanceof GuiChat && this.incomingGraph.u_boolean() && Mouse.isButtonDown(0)) {
                  this.incomingGraph.T(var48, var52);
               }

               if (Mouse.isButtonDown(0) && this.incomingGraph.G(var48, var52)) {
                  this.incomingGraph.F(true, var48, var52);
               } else {
                  this.incomingGraph.x(false);
               }
            }
         } else {
            this.incomingGraph = null;
         }

         if (client.getModuleManager().get(AutoHypixel.class).isEnabled()
            && AutoHypixel.autoChecker.getSetting("Enabled").asBoolean()
            && GameUtil.isOnHypixel()
            && AutoHypixel.autoChecker.getSetting("Render").asBoolean()) {
            NumberSetting var37 = (NumberSetting)AutoHypixel.autoChecker.getSetting("Pos-X");
            NumberSetting var41 = (NumberSetting)AutoHypixel.autoChecker.getSetting("Pos-Y");
            EnumSetting var45 = (EnumSetting)AutoHypixel.autoChecker.getSetting("Game-Mode");
            NumberSetting var49 = (NumberSetting)AutoHypixel.autoChecker.getSetting("Rows");
            if (this.statsHud == null) {
               this.statsHud = new StatsHud(var37.asFloat(), var41.asFloat(), 300, 100);
            } else {
               int var53 = var1x.getResolution().getScaledHeight();
               int var54 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
               int var26 = var53 - Mouse.getY() * var53 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  var37.setValue(this.statsHud.M());
                  var41.setValue(this.statsHud.E_float());
               } else if ((Float)var37.getValue() != this.statsHud.M() || (Float)var41.getValue() != this.statsHud.E_float()) {
                  if (this.statsHud.M() != (Float)var37.getValue()) {
                     this.statsHud.o((Float)var37.getValue());
                  }

                  if (this.statsHud.E_float() != (Float)var41.getValue()) {
                     this.statsHud.q((Float)var41.getValue());
                  }
               }

               if ((Float)var37.getValue() > var1x.getResolution().getScaledWidth()) {
                  var37.setValue(1.0F);
               }

               if ((Float)var41.getValue() > var1x.getResolution().getScaledHeight()) {
                  var41.setValue(30.0F);
               }

               this.statsHud.J((AutoHypixelAutoCheckerGameMode)var45.getValue());
               this.statsHud.I((Integer)var49.getValue());
               this.statsHud.render(var54, var26);
               if (mc.currentScreen instanceof GuiChat && this.statsHud.u_boolean() && Mouse.isButtonDown(0)) {
                  this.statsHud.T(var54, var26);
               }

               if (Mouse.isButtonDown(0) && this.statsHud.G(var54, var26)) {
                  this.statsHud.F(true, var54, var26);
               } else {
                  this.statsHud.x(false);
               }
            }
         } else {
            this.statsHud = null;
         }

         this.drawArmor(var1x.getResolution());
      }
   };
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         if (!this.isGraphEnabled()) {
            return;
         }

         if (this.packetHistory.remainingCapacity() == 0) {
            this.packetHistory.remove();
         }

         this.packetHistory.add(new PacketSnapshot(this.outgoingPackets, this.incomingPackets));
         this.outgoingPackets = new ArrayList<>();
         this.incomingPackets = new ArrayList<>();
      }
   };
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var1x -> {
      long var2 = cb ^ 36484945199478L;
      long var4 = var2 ^ 71517977129530L;
      boolean var7 = this.isGraphEnabled();
      switch (var1x.getDirection()) {
         case OUTGOING:
            if (var7) {
               if (client.getModuleManager().get(KillAura.class).isEnabled()) {
                  if (var1x.getPacket() instanceof C02PacketUseEntity) {
                     C02PacketUseEntity var8 = (C02PacketUseEntity)var1x.getPacket();
                     if (var8.getAction() == C02PacketUseEntity.Action.INTERACT) {
                        return;
                     }
                  }

                  if (var1x.getPacket() instanceof C08PacketPlayerBlockPlacement || var1x.getPacket() instanceof C07PacketPlayerDigging) {
                     return;
                  }
               }

               this.outgoingPackets.add(var1x.getPacket());
            }
            break;
         case RECEIVED:
            if (var1x.getPacket() instanceof S03PacketTimeUpdate) {
               this.recordTickRate();
            }

            if (var7) {
               this.incomingPackets.add(var1x.getPacket());
            }
      }
   };
   private final List<ModuleInfo> arrayModules = new ArrayList<>();
   private final Stopwatch logoLetterTimer = new Stopwatch();
   private int logoLetterIndex;
   private final ArrayList<String> leftInfoLines = new ArrayList<>();
   private final ArrayList<String> rightInfoLines = new ArrayList<>();
   private static final long cb = 96011854057215L;

   public HUD(Category var1) {
      super(var1);
      this.addSettings(gui, chatSettings, this.information, this.array, watermark, graphs, client.getColorSetting(), hudScale, tabGui, toolTips, cFont);
      this.setEnabled(true);
      this.setHidden(false);
      flagColors = Arrays.asList(HUDFlagColor.values());
   }

   private boolean isGraphEnabled() {
      return graphs.getSetting("Outgoing").asBoolean() || graphs.getSetting("Incoming").asBoolean();
   }

   private void drawArmor(ScaledResolution var1) {
      byte var5 = 0;

      for (int var6 = 3; var6 >= 0; var6--) {
         ItemStack var7 = mc.thePlayer.getCurrentArmor(var6);
         if (var7 != null) {
            if (var7.getEnchantmentTagList() != null) {
               NBTTagList var8 = var7.getEnchantmentTagList();

               for (int var9 = 0; var9 < var8.tagCount(); var9++) {
                  NBTTagCompound var10 = var8.getCompoundTagAt(var9);
                  GlStateManager.pushMatrix();
                  GlStateManager.scale(0.5F, 0.5F, 0.5F);
                  if (Objects.nonNull(Enchantment.getEnchantmentById(var10.getShort("id")))) {
                     String var11 = Enchantment.getEnchantmentById(var10.getShort("id")).getTranslatedName(var10.getShort("lvl")).substring(0, 2).toLowerCase()
                        + ""
                        + var10.getShort("lvl");
                     mc.fontRendererObj
                        .drawStringWithShadow(
                           var11,
                           (var1.getScaledWidth() / 2 + 34 + var5) * 2,
                           (var1.getScaledHeight() - (mc.thePlayer.capabilities.isCreativeMode ? 60 : 70) + 4 * var9) * 2,
                           -587202561
                        );
                  }

                  GlStateManager.popMatrix();
               }
            }

            ESP.renderItem(var7, var1.getScaledWidth() / 2 + 30 + var5, var1.getScaledHeight() - (mc.thePlayer.capabilities.isCreativeMode ? 40 : 57));
            var5 += 15;
         }
      }
   }

   private void renderArrayList() {
      long var1 = cb ^ 42338073567816L;
      long var3 = var1 ^ 77673853882628L;
      int var6 = 0;
      float var7 = this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? 8.0F : -3.0F;
      float var8 = RenderUtil.getScaledWidth();
      float var9 = RenderUtil.getScaledHeight();
      long var10 = Minecraft.getSystemTime();
      if (Util.getOSType() != Util.EnumOS.WINDOWS) {
         var10 = System.nanoTime() / 1000000L;
      }

      HUDArrayAnimationMode var12 = (HUDArrayAnimationMode)this.array.getSetting("Animation-").getValue();
      boolean var13 = var12 != HUDArrayAnimationMode.OFF;
      this.arrayModules.clear();

      for (ModuleInfo var15 : client.getModuleManager().getList()) {
         if (var15 instanceof Module && ((Module)var15).isEnabled() && ((Module)var15).isHidden()
            || var15 instanceof Module && ((Module)var15).animation.isRunning() && var12 != HUDArrayAnimationMode.OFF && ((Module)var15).isHidden()) {
            this.arrayModules.add(var15);
         }
      }

      switch ((HUDArraySortMode)this.array.getSetting("Sort").getValue()) {
         case WIDTH:
            this.arrayModules
               .sort(
                  (var0, var1x) -> client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(var0.getDisplayName()))
                        < client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(var1x.getDisplayName()))
                     ? 1
                     : -1
               );
            break;
         case ABC:
            this.arrayModules.sort((var0, var1x) -> var0.getDisplayName().compareToIgnoreCase(var1x.getDisplayName()));
      }

      for (int var28 = 0; var28 < this.arrayModules.size(); var28++) {
         ModuleInfo var29 = this.arrayModules.get(var28);
         Module var16 = (Module)var29;
         int var17 = client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(var29.getDisplayName()));
         int var18 = (int)(var8 - (int)(var13 ? var16.animation.getValue(0.0, var17 + 1) : var17 + 1));
         if (this.array.getSetting("Bar").getValue() == HUDArrayBarMode.NONE
            || this.array.getSetting("Bar").getValue() == HUDArrayBarMode.LEFT
            || this.array.getSetting("Bar").getValue() == HUDArrayBarMode.FULL) {
            var18 += 2;
         }

         int var19 = (int)(
            (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? var9 : 0.0F)
               - (int)(
                  (
                        this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN
                           ? var7
                           : -var7 - 6.0F + (var12 == HUDArrayAnimationMode.IN_VERTICAL ? var16.animation.getValue(var7, 0.0) : 0.0)
                     )
                     + client.getCustomFontRenderer().getStringHeight(var16.getName()) / 2
               )
         );
         int var20 = this.getArrayColor(var10, var29.getCategory(), var6, var28);
         int var21 = 11 - this.array.getSetting("Spacing").asInt();
         int var22 = this.array.getSetting("Background-").asInt();
         if (var22 > 0) {
            RenderUtil.drawRect(var18 - 5.5F, var19 + 2, var8, var19 + var21 + 2, new Color(1, 1, 1, var22).getRGB());
         }

         switch ((HUDArrayBarMode)this.array.getSetting("Bar").getValue()) {
            case RIGHT:
               RenderUtil.drawRect(var8 - 1.0F, var19 + 2, var8, var19 + var21 + 2, var20);
               break;
            case LEFT:
               RenderUtil.drawRect(var18 - 5.5F, var19 + 2, var18 - 4.5F, var19 + var21 + 2, var20);
               break;
            case FULL:
               RenderUtil.drawRect(var18 - 5.5F, var19 + 2, var18 - 4.5F, var19 + var21 + 2, var20);
               if (var28 < this.arrayModules.size() - 1) {
                  ModuleInfo var23 = this.arrayModules.get(var28 + 1);
                  Module var24 = (Module)var23;
                  if (var23 != null) {
                     int var25 = client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(var23.getDisplayName()));
                     if (var25 > var17) {
                        int var26 = (int)(var8 - (int)(var13 ? var16.animation.getValue(0.0, var25 + 1) : var25 + 1));
                        int var27 = (int)(
                           (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? var9 : 0.0F)
                              - (int)(
                                 (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? var7 : -var7 - 6.0F)
                                    + client.getCustomFontRenderer().getStringHeight(var23.getName()) / 2
                              )
                        );
                        RenderUtil.drawRect(var26 - 2.5F, var27 + var21 + 2.0F, var26 + Math.abs(var17 - var25) - 1.5F, var27 + var21 + 3.0F, var20);
                     } else {
                        float var31 = var17 - var25;
                        RenderUtil.drawRect(var18 - 5.5F, var19 + var21 + 2.0F, var18 + var24.animation.getValue(0.0, var31) - 4.5, var19 + var21 + 3.0F, var20);
                     }
                  }
               } else {
                  var21 = (int)(var21 + 2.0F);
                  RenderUtil.drawRect(var18 - 5.5F, var19 + var21, var8, var19 + var21 + 1, var20);
               }
         }

         client.getCustomFontRenderer().drawStringWithShadow(var29.getDisplayName(), var18 - 3, var19 + 1, var20);
         HUDArrayMode var30 = (HUDArrayMode)this.array.getSetting("Mode").getValue();
         var10 -= var30 == HUDArrayMode.RAINBOW ? 100L : 280L;
         var7 = (float)(var7 + (var13 ? var16.animation.getValue() * var21 : var21));
         if (++var6 >= flagColors.size()) {
            var6 = 0;
         }
      }

      arrayListBottom = var7;
   }

   private void recordTickRate() {
      if (this.lastTimeUpdate != -1L) {
         float var4 = (float)(System.currentTimeMillis() - this.lastTimeUpdate) / 1000.0F;
         tickRates[this.tickRateIndex % tickRates.length] = MathHelper.clamp_float(20.0F / var4, 0.0F, 20.0F);
         this.tickRateIndex++;
      }

      this.lastTimeUpdate = System.currentTimeMillis();
   }

   public static float getTps() {
      float var3 = 0.0F;
      float var4 = 0.0F;

      for (float var8 : tickRates) {
         if (var8 > 0.0F) {
            var4 += var8;
            var3++;
         }
      }

      return MathHelper.clamp_float(var4 / var3, 0.0F, 20.0F);
   }

   public static int step(boolean var0, int var1) {
      return var0 ? --var1 : ++var1;
   }

   public static boolean isWatermarkEnabled() {
      return watermark.getSetting("Enabled").asBoolean();
   }

   public static String getWatermarkText() {
      return watermark.getSetting("Display").asString();
   }

   public static boolean isPowerBranding() {
      return getWatermarkText().equals("Power");
   }

   private HUDWatermarkMode getWatermarkMode() {
      return (HUDWatermarkMode)watermark.getSetting("Mode").getValue();
   }

   public int indexOfFlagColor(HUDFlagColor var1) {
      int var5 = 1;
      HUDFlagColor[] var6 = HUDFlagColor.values();

      for (int var7 = 0; var7 < var6.length; var7++) {
         HUDFlagColor var8 = var6[var7];
         if (var8 == var1) {
            var5 = var7;
            break;
         }
      }

      return var5;
   }

   public void drawAstolfoLogo() {
      long var1 = cb ^ 51391696122757L;
      int var4 = client.getColorSetting().getValue().getRGB();
      int var5 = client.getColorSetting().getValue().darker().getRGB();
      if (this.logoLetterTimer.hasElapsed(6000.0F)) {
         if (this.logoLetterIndex > "Astolfo".length() - 2) {
            this.logoLetterIndex = 0;
         } else {
            this.logoLetterIndex++;
         }

         this.logoLetterTimer.reset();
      }

      GL11.glDisable(2884);
      GlStateManager.pushMatrix();
      long var10000 = 3610461468011116973L ^ var1;
      GlStateManager.disableDepth();
      GlStateManager.translate(12.0F, 12.0F, 0.0F);
      GlStateManager.rotate(mc.thePlayer.ticksExisted % 360 * 3, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-12.0F, -12.0F, 0.0F);
      RenderUtil.drawCircle(12.0, 12.0, 10.0, -1728053248);
      String var6 = String.valueOf("Astolfo".charAt(this.logoLetterIndex)).toUpperCase();
      float var7 = GuiMainMenu.P.getStringWidth((java.lang.String)(var6));
      var10000 = 568332951577761163L ^ var1;
      float var8 = GuiMainMenu.P.getStringHeight((java.lang.String)(var6));
      GuiMainMenu.P.drawString(EnumChatFormatting.BOLD + var6, 2.0F + (20.0F - var7) / 2.0F - 1.0F, 4.0F + (20.0F - var8) / 2.0F - 2.0F, var5);
      GlStateManager.pushMatrix();
      GL11.glEnable(2848);
      GL11.glLineWidth(4.0F);
      RenderUtil.drawArc(12.0, 12.0, 180, mc.thePlayer.ticksExisted % 360 * 5, 10.0, var4);
      GL11.glLineWidth(0.5F);
      RenderUtil.drawArc(12.0, 12.0, 180, mc.thePlayer.ticksExisted % 360 * 5, 11.0, var5);
      GlStateManager.popMatrix();
      GL11.glEnable(2884);
      GlStateManager.popMatrix();
   }

   private int getArrayColor(long var1, Category var3, int var4, int var5) {
      HUDArrayMode var9 = (HUDArrayMode)this.array.getSetting("Mode").getValue();
      Color var10 = ((ColorSetting)this.array.getSetting("Color")).getValue();
      switch (var9) {
         case STATIC:
            return var10.getRGB();
         case WAVY:
            float var11 = 1.0F - MathHelper.abs(MathHelper.sin((float)var1 % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
            return new Color(
                  MathHelper.clamp_int((int)(var10.getRed() * var11), 0, 255),
                  MathHelper.clamp_int((int)(var10.getGreen() * var11), 0, 255),
                  MathHelper.clamp_int((int)(var10.getBlue() * var11), 0, 255)
               )
               .getRGB();
         case RAINBOW:
            return this.pulsingColor(var1, 1.0F).getRGB();
         case RAINBOW_2:
            return rainbow(var5 * 2 * 200000000L, 1.0F).getRGB();
         case CATEGORY:
            return var3.getColor();
         case TRANS:
            return HUDFlagColor.s(HUDFlagColor.values()[var4]).getRGB();
         default:
            return client.getColorSetting().getValue().getRGB();
      }
   }

   private double toBlocksPerSecond(double var1) {
      return var1 / 0.05F;
   }

   private void drawLeftInfo() {
      this.leftInfoLines.clear();
      double var4 = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double var6 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      double var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6) * mc.timer.timerSpeed;
      String var10 = "%.2f";
      String var11 = String.format(var10 + " blocks/sec", this.toBlocksPerSecond(var8));
      if (this.information.getSetting("Cords").asBoolean()) {
         this.leftInfoLines.add(String.format("%s, %s, %s", (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ));
      }

      if (!(mc.currentScreen instanceof GuiChat) && this.information.getSetting("Speed").asBoolean()) {
         this.leftInfoLines.add(var11);
      }

      if (this.information.getSetting("FPS").asBoolean()) {
         this.leftInfoLines.add("FPS: " + Minecraft.B());
      }

      byte var12 = 0;

      for (String var14 : this.leftInfoLines) {
         client.getCustomFontRenderer()
            .drawStringWithShadow(
               var14,
               1.0F,
               (int)(
                  RenderUtil.getScaledHeight() - client.getCustomFontRenderer().getStringHeight(var14) - var12 - (mc.currentScreen instanceof GuiChat ? 15 : 0)
               ),
               Color.WHITE.getRGB()
            );
         var12 += 10;
      }
   }

   private void drawRightInfo(GuiIngame var1) {
      long var2 = cb ^ 11129960585938L;
      long var4 = (var2 ^ 98588088069439L) >>> 16;
      int var6 = (int)((var2 ^ 98588088069439L) << 48 >>> 48);
      this.rightInfoLines.clear();
      String var8;
      if (isPowerBranding()) {
         var8 = String.format(
            "§7Power build - §f%s - §a%s",
            isDevelopment
               ? String.valueOf(this.buildDate.getYear()).substring(2) + "" + this.buildDate.getMonthValue() + "" + this.buildDate.getDayOfMonth()
               : client.getVersion(),
            mc.mouseHelper.Y().getAccount().getUsername()
         );
      } else {
         var8 = String.format(
            "§7%s - §f§l%s §r§7- %s",
            isDevelopment ? "Development" : (client.getVersion().contains("BETA") ? "Beta" : "Release"),
            isDevelopment
               ? String.valueOf(this.buildDate.getYear()).substring(2) + "" + this.buildDate.getMonthValue() + "" + this.buildDate.getDayOfMonth()
               : client.getVersion().replace("BETA", ""),
            mc.mouseHelper.Y().getAccount().getUsername()
         );
      }

      if (this.information.getSetting("Version").asBoolean()) {
         this.rightInfoLines.add(var8);
      }

      int var9 = mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1
         ? mc.thePlayer.getPlayerInfo().getResponseTime()
         : (mc.getCurrentServerData() != null && mc.getCurrentServerData().pingToServer > 0L ? (int)mc.getCurrentServerData().pingToServer : 0);
      if (this.information.getSetting("Ping").asBoolean() && var9 > 0) {
         this.rightInfoLines.add(String.format("Ping: §7%sms", Math.min(var9, 999)));
      }

      if (!mc.isSingleplayer() && this.information.getSetting("TPS").asBoolean()) {
         this.rightInfoLines.add(String.format("TPS: §7%s", MathUtil.ONE_DECIMAL.format(getTps())));
      }

      byte var10 = 0;

      for (String var12 : this.rightInfoLines) {
         client.getCustomFontRenderer()
            .drawStringWithShadow(
               var12,
               (int)(RenderUtil.getScaledWidth() - client.getCustomFontRenderer().getStringWidth(var12) - 1.0F)
                  - (!var12.contains("Development") && !var12.contains("Beta") && !var12.contains("Release") ? 0 : 3),
               (int)(
                  (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? 11.0F : RenderUtil.getScaledHeight())
                     - client.getCustomFontRenderer().getStringHeight(var12)
                     - var10
                     - (mc.currentScreen instanceof GuiChat && this.array.getSetting("Position").getValue() == HUDArrayPositionMode.UP ? 15 : 0)
               ),
               Color.WHITE.getRGB()
            );
         var10 += this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? -10 : 10;
      }

      if (this.information.getSetting("Potions").asBoolean()) {
         for (PotionEffect var23 : mc.thePlayer.getActivePotionEffects()) {
            Potion var13 = Potion.potionTypes[var23.getPotionID()];
            String var14 = I18n.format(var13.getName())
               + " §7"
               + (var23.getAmplifier() + 1 == 1 ? "" : var23.getAmplifier() + 1 + " ")
               + "- "
               + Potion.getDurationString(var23);
            int var15 = (int)(RenderUtil.getScaledWidth() - client.getCustomFontRenderer().getStringWidth(var14) - 1.0F);
            int var16 = (int)(
               (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? 11.0F : RenderUtil.getScaledHeight())
                  - client.getCustomFontRenderer().getStringHeight(var14)
                  - var10
                  - (mc.currentScreen instanceof GuiChat && this.array.getSetting("Position").getValue() == HUDArrayPositionMode.UP ? 15 : 0)
            );
            client.getCustomFontRenderer().drawStringWithShadow(var14, var15, var16, var13.getLiquidColor());
            if (var13.hasStatusIcon()) {
               GL11.glPushMatrix();
               GL11.glScalef(0.5F, 0.5F, 0.5F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);
               int var21 = var13.getStatusIconIndex();
               var1.drawTexturedModalRect((var15 - 10) * 2, var16 * 2 + 3, 0 + var21 % 8 * 18, 198 + var21 / 8 * 18, 18, 18);
               GL11.glPopMatrix();
            }

            var10 += this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? -10 : 10;
         }
      }
   }

   private Color pulsingColor(long var1, float var3) {
      float var6 = 1.0F - MathHelper.abs(MathHelper.sin((float)var1 % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.5F);
      long var7 = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(var6, 0.5F, 1.0F)), 16);
      Color var9 = new Color((int)var7);
      return new Color(var9.getRed() / 255.0F * var3, var9.getGreen() / 255.0F * var3, var9.getBlue() / 255.0F * var3, var9.getAlpha() / 255.0F);
   }

   public static Color rainbow(long var0, float var2) {
      float var5 = (float)(System.nanoTime() * 2L + var0) / 1.0E10F % 1.0F;
      long var6 = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(var5, 1.0F, 1.0F)), 16);
      Color var8 = new Color((int)var6);
      return new Color(var8.getRed() / 255.0F * var2, var8.getGreen() / 255.0F * var2, var8.getBlue() / 255.0F * var2, var8.getAlpha() / 255.0F);
   }

   public TabGui getTabGui() {
      if (this.tabGuiInstance == null) {
         this.tabGuiInstance = new TabGui();
      }

      return this.tabGuiInstance;
   }
}
