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
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN && !mc.gameSettings.n) {
         HUDWatermarkMode hudWatermarkMode = this.getWatermarkMode();
         float f = hudScale.getValue();
         double d = renderEvent.getResolution().getScaleFactor() / Math.pow(renderEvent.getResolution().getScaleFactor(), 2.0);
         GlStateManager.pushMatrix();
         GlStateManager.scale(d * f, d * f, d * f);
         if (isWatermarkEnabled()) {
            String string = getWatermarkText();
            if (isPowerBranding()) {
               RenderUtil.drawRect(2.0, 2.0, 55.0, 19.0, new Color(1, 1, 1, 150).getRGB());
               RenderUtil.drawRect(2.3F, 2.3F, 4.0, 18.3F, client.getColorSetting().getValue().getRGB());
               GuiMainMenu.M.drawStringWithShadow("P", 6.0F, 3.5F, client.getColorSetting().getValue().getRGB());
               GuiMainMenu.M.drawStringWithShadow("ower", 6 + GuiMainMenu.M.getStringWidth("P") + 1, 3.5F, -1);
            } else {
               string = string.replace("&", "§");
               string = string.replace("%fps%", String.valueOf(Minecraft.B()));
               string = string.replace("%time%", this.timeFormat.format(new Date()));
               if (this.information.getSetting("Ping").asBoolean()
                  && mc.thePlayer.getPlayerInfo() != null
                  && mc.thePlayer.getPlayerInfo().getResponseTime() > 1) {
                  string = string.replace("%ping%", mc.thePlayer.getPlayerInfo().getResponseTime() + "ms");
               } else {
                  string = string.replace("%ping%", "");
               }

               if (mc.getCurrentServerData() != null) {
                  string = string.replace("%ip%", mc.getCurrentServerData().serverIP);
               }

               string = string.replace("%user%", mc.mouseHelper.Y().getAccount().getUsername());
               if (DisconnectHandler.disconnected) {
                  string = string + EnumChatFormatting.GRAY + " - DISCONNECTED";
               }

               if (!StringUtils.isNullOrEmpty(string)) {
                  switch (hudWatermarkMode) {
                     case COLOR:
                        client.getCustomFontRenderer().drawStringWithShadow("§l" + string.charAt(0), 1.0F, 1.0F, client.getColor().getRGB());
                        client.getCustomFontRenderer()
                           .drawStringWithShadow(
                              string.substring(1), 2 + client.getCustomFontRenderer().getStringWidth(string.substring(0, 1)), 1.0F, Color.WHITE.getRGB()
                           );
                        break;
                     case WHITE:
                        client.getCustomFontRenderer().drawStringWithShadow(string, 1.0F, 1.0F, -1);
                        break;
                     case BREATH:
                        Color color = client.getColorSetting().getValue();
                        float g = 1.0F
                           - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
                        int i = new Color(
                              MathHelper.clamp_int((int)(color.getRed() * g), 0, 255),
                              MathHelper.clamp_int((int)(color.getGreen() * g), 0, 255),
                              MathHelper.clamp_int((int)(color.getBlue() * g), 0, 255)
                           )
                           .getRGB();
                        client.getCustomFontRenderer().drawStringWithShadow("§l" + string.charAt(0), 1.0F, 1.0F, i);
                        client.getCustomFontRenderer()
                           .drawStringWithShadow(
                              string.substring(1), 2 + client.getCustomFontRenderer().getStringWidth(string.substring(0, 1)), 1.0F, Color.WHITE.getRGB()
                           );
                        break;
                     case SOLID:
                        client.getCustomFontRenderer().drawStringWithShadow(string, 1.0F, 1.0F, client.getColorSetting().getValue().getRGB());
                        break;
                     case RAINBOW:
                        int j = Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.4F, 0.8F);
                        client.getCustomFontRenderer().drawStringWithShadow("§l" + string.charAt(0), 1.0F, 1.0F, j);
                        client.getCustomFontRenderer()
                           .drawStringWithShadow(
                              string.substring(1), 2 + client.getCustomFontRenderer().getStringWidth(string.substring(0, 1)), 1.0F, Color.WHITE.getRGB()
                           );
                        break;
                     case RETARD:
                        GL11.glPushMatrix();
                        String string2 = "%s ︳ %s";
                        int k = Math.min(
                           mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1
                              ? mc.thePlayer.getPlayerInfo().getResponseTime()
                              : (
                                 mc.getCurrentServerData() != null && mc.getCurrentServerData().pingToServer > 0L
                                    ? (int)mc.getCurrentServerData().pingToServer
                                    : 0
                              ),
                           999
                        );
                        if (k > 0) {
                           string2 = string2 + " ︳ ping: %sms";
                        }

                        if (!ViaFabric.INSTANCE.isNative()) {
                           string2 = string2 + " ︳ version: " + ViaFabric.INSTANCE.getVersion().getName();
                        }

                        string2 = String.format(
                           string2,
                           client.getName() + (isDevelopment ? " [DEVELOPMENT]" : (client.getVersion().contains("BETA") ? " [BETA]" : "")),
                           mc.mouseHelper.Y().getAccount().getUsername(),
                           k
                        );
                        RenderUtil.drawRect(3.0, 3.0, 10 + client.getFontRenderer().getStringWidth(string2), 18.0, new Color(1, 1, 1, 80).getRGB());
                        RenderUtil.drawRect(3.0, 3.0, 10 + client.getFontRenderer().getStringWidth(string2), 4.0, client.getColorSetting().getValue().getRGB());
                        RenderUtil.drawRect(
                           3.0, 4.0, 10 + client.getFontRenderer().getStringWidth(string2), 5.0, client.getColorSetting().getValue().darker().getRGB()
                        );
                        RenderUtil.drawRect(
                           3.0, 5.0, 10 + client.getFontRenderer().getStringWidth(string2), 6.0, client.getColorSetting().getValue().darker().darker().getRGB()
                        );
                        client.getFontRenderer().O(string2, 7, 8, -1);
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
               if (hudWatermarkMode == HUDWatermarkMode.RETARD) {
                  this.getTabGui().setY(20.0F);
                  this.getTabGui().setX(3.0F);
               } else if (hudWatermarkMode == HUDWatermarkMode.ROTATE) {
                  this.getTabGui().setX(0.0F);
                  this.getTabGui().setY(25.0F);
               } else {
                  this.getTabGui().setX(0.0F);
                  this.getTabGui().setY(13.0F);
               }
            } else {
               this.getTabGui().setX(0.0F);
               this.getTabGui().setY(2.0F);
            }

            this.getTabGui().render(renderEvent.getResolution(), mc);
         }

         if (this.array.getSetting("Enabled").asBoolean()) {
            this.renderArrayList();
         }

         this.drawLeftInfo();
         this.drawRightInfo(renderEvent.getGuiIngame());
         GlStateManager.popMatrix();
         if (this.information.getSetting("Direction").asBoolean()) {
            NumberSetting numberSetting = (NumberSetting)this.information.getSetting("Pos-X");
            NumberSetting numberSetting2 = (NumberSetting)this.information.getSetting("Pos-Y");
            if (this.compass == null) {
               this.compass = new CompassHud(numberSetting.asFloat(), numberSetting2.asFloat(), 75, 20);
            } else {
               int m = renderEvent.getResolution().getScaledHeight();
               int i2 = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
               int i3 = m - Mouse.getY() * m / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  numberSetting.setValue(this.compass.getX());
                  numberSetting2.setValue(this.compass.getYPos());
               } else if ((Float)numberSetting.getValue() != this.compass.getX() || (Float)numberSetting2.getValue() != this.compass.getYPos()) {
                  if (this.compass.getX() != (Float)numberSetting.getValue()) {
                     this.compass.setX((Float)numberSetting.getValue());
                  }

                  if (this.compass.getYPos() != (Float)numberSetting2.getValue()) {
                     this.compass.setY((Float)numberSetting2.getValue());
                  }
               }

               this.compass.setScreenWidth(renderEvent.getResolution().getScaledWidth());
               this.compass.render(i2, i3);
               if (mc.currentScreen instanceof GuiChat && this.compass.isDragging() && Mouse.isButtonDown(0)) {
                  this.compass.updateDragPosition(i2, i3);
               }

               if (Mouse.isButtonDown(0)
                  && this.compass
                     .isMouseInRect(
                        (int)this.compass.getX() - 75, (int)this.compass.getYPos(), this.compass.getWidth() + 75, this.compass.getHeight(), i2, i3
                     )) {
                  this.compass.setDragging(true, i2, i3);
               } else {
                  this.compass.setDraggingNoOffset(false);
               }
            }
         } else {
            this.compass = null;
         }

         boolean bl = graphs.getSetting("Show-Average").asBoolean();
         if (graphs.getSetting("Outgoing").asBoolean()) {
            NumberSetting numberSetting3 = (NumberSetting)graphs.getSetting("Pos-X-1");
            NumberSetting numberSetting4 = (NumberSetting)graphs.getSetting("Pos-Y-1");
            if (this.outgoingGraph == null) {
               this.outgoingGraph = new PacketGraphHud(numberSetting3.asFloat(), numberSetting4.asFloat(), 150, 39);
            } else {
               int i4 = renderEvent.getResolution().getScaledHeight();
               int i5 = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
               int i6 = i4 - Mouse.getY() * i4 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  numberSetting3.setValue(this.outgoingGraph.getX());
                  numberSetting4.setValue(this.outgoingGraph.getYPos());
               } else if ((Float)numberSetting3.getValue() != this.outgoingGraph.getX() || (Float)numberSetting4.getValue() != this.outgoingGraph.getYPos()) {
                  if (this.outgoingGraph.getX() != (Float)numberSetting3.getValue()) {
                     this.outgoingGraph.setX((Float)numberSetting3.getValue());
                  }

                  if (this.outgoingGraph.getYPos() != (Float)numberSetting4.getValue()) {
                     this.outgoingGraph.setY((Float)numberSetting4.getValue());
                  }
               }

               this.outgoingGraph.setSnapshots(this.packetHistory);
               this.outgoingGraph.setShowAverage(bl);
               this.outgoingGraph.render(i5, i6);
               if (mc.currentScreen instanceof GuiChat && this.outgoingGraph.isDragging() && Mouse.isButtonDown(0)) {
                  this.outgoingGraph.updateDragPosition(i5, i6);
               }

               if (Mouse.isButtonDown(0) && this.outgoingGraph.isHovered(i5, i6)) {
                  this.outgoingGraph.setDragging(true, i5, i6);
               } else {
                  this.outgoingGraph.setDraggingNoOffset(false);
               }
            }
         } else {
            this.outgoingGraph = null;
         }

         if (graphs.getSetting("Incoming").asBoolean()) {
            NumberSetting numberSetting5 = (NumberSetting)graphs.getSetting("Pos-X-2");
            NumberSetting numberSetting6 = (NumberSetting)graphs.getSetting("Pos-Y-2");
            if (this.incomingGraph == null) {
               this.incomingGraph = new PacketGraphHud(numberSetting5.asFloat(), numberSetting6.asFloat(), 150, 39);
            } else {
               int i7 = renderEvent.getResolution().getScaledHeight();
               int i8 = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
               int i9 = i7 - Mouse.getY() * i7 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  numberSetting5.setValue(this.incomingGraph.getX());
                  numberSetting6.setValue(this.incomingGraph.getYPos());
               } else if ((Float)numberSetting5.getValue() != this.incomingGraph.getX() || (Float)numberSetting6.getValue() != this.incomingGraph.getYPos()) {
                  if (this.incomingGraph.getX() != (Float)numberSetting5.getValue()) {
                     this.incomingGraph.setX((Float)numberSetting5.getValue());
                  }

                  if (this.incomingGraph.getYPos() != (Float)numberSetting6.getValue()) {
                     this.incomingGraph.setY((Float)numberSetting6.getValue());
                  }
               }

               this.incomingGraph.setSnapshots(this.packetHistory);
               this.incomingGraph.setShowIncoming(true);
               this.incomingGraph.setShowAverage(bl);
               this.incomingGraph.render(i8, i9);
               if (mc.currentScreen instanceof GuiChat && this.incomingGraph.isDragging() && Mouse.isButtonDown(0)) {
                  this.incomingGraph.updateDragPosition(i8, i9);
               }

               if (Mouse.isButtonDown(0) && this.incomingGraph.isHovered(i8, i9)) {
                  this.incomingGraph.setDragging(true, i8, i9);
               } else {
                  this.incomingGraph.setDraggingNoOffset(false);
               }
            }
         } else {
            this.incomingGraph = null;
         }

         if (client.getModuleManager().get(AutoHypixel.class).isEnabled()
            && AutoHypixel.autoChecker.getSetting("Enabled").asBoolean()
            && GameUtil.isOnHypixel()
            && AutoHypixel.autoChecker.getSetting("Render").asBoolean()) {
            NumberSetting numberSetting7 = (NumberSetting)AutoHypixel.autoChecker.getSetting("Pos-X");
            NumberSetting numberSetting8 = (NumberSetting)AutoHypixel.autoChecker.getSetting("Pos-Y");
            EnumSetting enumSetting = (EnumSetting)AutoHypixel.autoChecker.getSetting("Game-Mode");
            NumberSetting numberSetting9 = (NumberSetting)AutoHypixel.autoChecker.getSetting("Rows");
            if (this.statsHud == null) {
               this.statsHud = new StatsHud(numberSetting7.asFloat(), numberSetting8.asFloat(), 300, 100);
            } else {
               int i10 = renderEvent.getResolution().getScaledHeight();
               int i11 = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
               int i12 = i10 - Mouse.getY() * i10 / mc.displayHeight - 1;
               if (Mouse.isButtonDown(0)) {
                  numberSetting7.setValue(this.statsHud.getX());
                  numberSetting8.setValue(this.statsHud.getYPos());
               } else if ((Float)numberSetting7.getValue() != this.statsHud.getX() || (Float)numberSetting8.getValue() != this.statsHud.getYPos()) {
                  if (this.statsHud.getX() != (Float)numberSetting7.getValue()) {
                     this.statsHud.setX((Float)numberSetting7.getValue());
                  }

                  if (this.statsHud.getYPos() != (Float)numberSetting8.getValue()) {
                     this.statsHud.setY((Float)numberSetting8.getValue());
                  }
               }

               if ((Float)numberSetting7.getValue() > renderEvent.getResolution().getScaledWidth()) {
                  numberSetting7.setValue(1.0F);
               }

               if ((Float)numberSetting8.getValue() > renderEvent.getResolution().getScaledHeight()) {
                  numberSetting8.setValue(30.0F);
               }

               this.statsHud.setGameMode((AutoHypixelAutoCheckerGameMode)enumSetting.getValue());
               this.statsHud.setMaxPlayers((Integer)numberSetting9.getValue());
               this.statsHud.render(i11, i12);
               if (mc.currentScreen instanceof GuiChat && this.statsHud.isDragging() && Mouse.isButtonDown(0)) {
                  this.statsHud.updateDragPosition(i11, i12);
               }

               if (Mouse.isButtonDown(0) && this.statsHud.isHovered(i11, i12)) {
                  this.statsHud.setDragging(true, i11, i12);
               } else {
                  this.statsHud.setDraggingNoOffset(false);
               }
            }
         } else {
            this.statsHud = null;
         }

         this.drawArmor(renderEvent.getResolution());
      }
   };
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE) {
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
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      boolean bl = this.isGraphEnabled();
      switch (packetEvent.getDirection()) {
         case OUTGOING:
            if (bl) {
               if (client.getModuleManager().get(KillAura.class).isEnabled()) {
                  if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
                     C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
                     if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.INTERACT) {
                        return;
                     }
                  }

                  if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement || packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
                     return;
                  }
               }

               this.outgoingPackets.add(packetEvent.getPacket());
            }
            break;
         case RECEIVED:
            if (packetEvent.getPacket() instanceof S03PacketTimeUpdate) {
               this.recordTickRate();
            }

            if (bl) {
               this.incomingPackets.add(packetEvent.getPacket());
            }
      }
   };
   private final List<ModuleInfo> arrayModules = new ArrayList<>();
   private final Stopwatch logoLetterTimer = new Stopwatch();
   private int logoLetterIndex;
   private final ArrayList<String> leftInfoLines = new ArrayList<>();
   private final ArrayList<String> rightInfoLines = new ArrayList<>();

   public HUD(Category category) {
      super(category);
      this.addSettings(gui, chatSettings, this.information, this.array, watermark, graphs, client.getColorSetting(), hudScale, tabGui, toolTips, cFont);
      this.setEnabled(true);
      this.setHidden(false);
      flagColors = Arrays.asList(HUDFlagColor.values());
   }

   private boolean isGraphEnabled() {
      return graphs.getSetting("Outgoing").asBoolean() || graphs.getSetting("Incoming").asBoolean();
   }

   private void drawArmor(ScaledResolution scaledResolution) {
      byte b = 0;

      for (int i = 3; i >= 0; i--) {
         ItemStack itemStack = mc.thePlayer.getCurrentArmor(i);
         if (itemStack != null) {
            if (itemStack.getEnchantmentTagList() != null) {
               NBTTagList nbtTagList = itemStack.getEnchantmentTagList();

               for (int j = 0; j < nbtTagList.tagCount(); j++) {
                  NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(j);
                  GlStateManager.pushMatrix();
                  GlStateManager.scale(0.5F, 0.5F, 0.5F);
                  if (Objects.nonNull(Enchantment.getEnchantmentById(nbtTagCompound.getShort("id")))) {
                     String string = Enchantment.getEnchantmentById(nbtTagCompound.getShort("id")).getTranslatedName(nbtTagCompound.getShort("lvl")).substring(0, 2).toLowerCase()
                        + ""
                        + nbtTagCompound.getShort("lvl");
                     mc.fontRendererObj
                        .drawStringWithShadow(
                           string,
                           (scaledResolution.getScaledWidth() / 2 + 34 + b) * 2,
                           (scaledResolution.getScaledHeight() - (mc.thePlayer.capabilities.isCreativeMode ? 60 : 70) + 4 * j) * 2,
                           -587202561
                        );
                  }

                  GlStateManager.popMatrix();
               }
            }

            ESP.renderItem(
               itemStack,
               scaledResolution.getScaledWidth() / 2 + 30 + b,
               scaledResolution.getScaledHeight() - (mc.thePlayer.capabilities.isCreativeMode ? 40 : 57)
            );
            b += 15;
         }
      }
   }

   private void renderArrayList() {
      int i = 0;
      float f = this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? 8.0F : -3.0F;
      float g = RenderUtil.getScaledWidth();
      float h = RenderUtil.getScaledHeight();
      long l = Minecraft.getSystemTime();
      if (Util.getOSType() != Util.EnumOS.WINDOWS) {
         l = System.nanoTime() / 1000000L;
      }

      HUDArrayAnimationMode hudArrayAnimationMode = (HUDArrayAnimationMode)this.array.getSetting("Animation-").getValue();
      boolean bl = hudArrayAnimationMode != HUDArrayAnimationMode.OFF;
      this.arrayModules.clear();

      for (ModuleInfo moduleInfo : client.getModuleManager().getList()) {
         if (moduleInfo instanceof Module && ((Module)moduleInfo).isEnabled() && ((Module)moduleInfo).isHidden()
            || moduleInfo instanceof Module && ((Module)moduleInfo).animation.isRunning() && hudArrayAnimationMode != HUDArrayAnimationMode.OFF && ((Module)moduleInfo).isHidden()) {
            this.arrayModules.add(moduleInfo);
         }
      }

      switch ((HUDArraySortMode)this.array.getSetting("Sort").getValue()) {
         case WIDTH:
            this.arrayModules
               .sort(
                  (moduleInfo2, moduleInfo3) -> client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(moduleInfo2.getDisplayName()))
                        < client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(moduleInfo3.getDisplayName()))
                     ? 1
                     : -1
               );
            break;
         case ABC:
            this.arrayModules.sort((moduleInfo4, moduleInfo5) -> moduleInfo4.getDisplayName().compareToIgnoreCase(moduleInfo5.getDisplayName()));
      }

      for (int j = 0; j < this.arrayModules.size(); j++) {
         ModuleInfo moduleInfo6 = this.arrayModules.get(j);
         Module module = (Module)moduleInfo6;
         int k = client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(moduleInfo6.getDisplayName()));
         int m = (int)(g - (int)(bl ? module.animation.getValue(0.0, k + 1) : k + 1));
         if (this.array.getSetting("Bar").getValue() == HUDArrayBarMode.NONE
            || this.array.getSetting("Bar").getValue() == HUDArrayBarMode.LEFT
            || this.array.getSetting("Bar").getValue() == HUDArrayBarMode.FULL) {
            m += 2;
         }

         int n = (int)(
            (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? h : 0.0F)
               - (int)(
                  (
                        this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN
                           ? f
                           : -f - 6.0F + (hudArrayAnimationMode == HUDArrayAnimationMode.IN_VERTICAL ? module.animation.getValue(f, 0.0) : 0.0)
                     )
                     + client.getCustomFontRenderer().getStringHeight(module.getName()) / 2
               )
         );
         int i2 = this.getArrayColor(l, moduleInfo6.getCategory(), i, j);
         int i3 = 11 - this.array.getSetting("Spacing").asInt();
         int i4 = this.array.getSetting("Background-").asInt();
         if (i4 > 0) {
            RenderUtil.drawRect(m - 5.5F, n + 2, g, n + i3 + 2, new Color(1, 1, 1, i4).getRGB());
         }

         switch ((HUDArrayBarMode)this.array.getSetting("Bar").getValue()) {
            case RIGHT:
               RenderUtil.drawRect(g - 1.0F, n + 2, g, n + i3 + 2, i2);
               break;
            case LEFT:
               RenderUtil.drawRect(m - 5.5F, n + 2, m - 4.5F, n + i3 + 2, i2);
               break;
            case FULL:
               RenderUtil.drawRect(m - 5.5F, n + 2, m - 4.5F, n + i3 + 2, i2);
               if (j < this.arrayModules.size() - 1) {
                  ModuleInfo moduleInfo7 = this.arrayModules.get(j + 1);
                  Module module2 = (Module)moduleInfo7;
                  if (moduleInfo7 != null) {
                     int i5 = client.getCustomFontRenderer().getStringWidth(StringUtils.stripControlCodes(moduleInfo7.getDisplayName()));
                     if (i5 > k) {
                        int i6 = (int)(g - (int)(bl ? module.animation.getValue(0.0, i5 + 1) : i5 + 1));
                        int i7 = (int)(
                           (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? h : 0.0F)
                              - (int)(
                                 (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? f : -f - 6.0F)
                                    + client.getCustomFontRenderer().getStringHeight(moduleInfo7.getName()) / 2
                              )
                        );
                        RenderUtil.drawRect(i6 - 2.5F, i7 + i3 + 2.0F, i6 + Math.abs(k - i5) - 1.5F, i7 + i3 + 3.0F, i2);
                     } else {
                        float f2 = k - i5;
                        RenderUtil.drawRect(m - 5.5F, n + i3 + 2.0F, m + module2.animation.getValue(0.0, f2) - 4.5, n + i3 + 3.0F, i2);
                     }
                  }
               } else {
                  i3 = (int)(i3 + 2.0F);
                  RenderUtil.drawRect(m - 5.5F, n + i3, g, n + i3 + 1, i2);
               }
         }

         client.getCustomFontRenderer().drawStringWithShadow(moduleInfo6.getDisplayName(), m - 3, n + 1, i2);
         HUDArrayMode hudArrayMode = (HUDArrayMode)this.array.getSetting("Mode").getValue();
         l -= hudArrayMode == HUDArrayMode.RAINBOW ? 100L : 280L;
         f = (float)(f + (bl ? module.animation.getValue() * i3 : i3));
         if (++i >= flagColors.size()) {
            i = 0;
         }
      }

      arrayListBottom = f;
   }

   private void recordTickRate() {
      if (this.lastTimeUpdate != -1L) {
         float f = (float)(System.currentTimeMillis() - this.lastTimeUpdate) / 1000.0F;
         tickRates[this.tickRateIndex % tickRates.length] = MathHelper.clamp_float(20.0F / f, 0.0F, 20.0F);
         this.tickRateIndex++;
      }

      this.lastTimeUpdate = System.currentTimeMillis();
   }

   public static float getTps() {
      float f = 0.0F;
      float g = 0.0F;

      for (float h : tickRates) {
         if (h > 0.0F) {
            g += h;
            f++;
         }
      }

      return MathHelper.clamp_float(g / f, 0.0F, 20.0F);
   }

   public static int step(boolean bl, int i) {
      return bl ? --i : ++i;
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

   public int indexOfFlagColor(HUDFlagColor hudFlagColor) {
      int i = 1;
      HUDFlagColor[] hudFlagColors = HUDFlagColor.values();

      for (int j = 0; j < hudFlagColors.length; j++) {
         HUDFlagColor hudFlagColor2 = hudFlagColors[j];
         if (hudFlagColor2 == hudFlagColor) {
            i = j;
            break;
         }
      }

      return i;
   }

   public void drawAstolfoLogo() {
      int i = client.getColorSetting().getValue().getRGB();
      int j = client.getColorSetting().getValue().darker().getRGB();
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
      GlStateManager.disableDepth();
      GlStateManager.translate(12.0F, 12.0F, 0.0F);
      GlStateManager.rotate(mc.thePlayer.ticksExisted % 360 * 3, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-12.0F, -12.0F, 0.0F);
      RenderUtil.drawCircle(12.0, 12.0, 10.0, -1728053248);
      String string = String.valueOf("Astolfo".charAt(this.logoLetterIndex)).toUpperCase();
      float f = GuiMainMenu.P.getStringWidth((java.lang.String)(string));
      float g = GuiMainMenu.P.getStringHeight((java.lang.String)(string));
      GuiMainMenu.P.drawString(EnumChatFormatting.BOLD + string, 2.0F + (20.0F - f) / 2.0F - 1.0F, 4.0F + (20.0F - g) / 2.0F - 2.0F, j);
      GlStateManager.pushMatrix();
      GL11.glEnable(2848);
      GL11.glLineWidth(4.0F);
      RenderUtil.drawArc(12.0, 12.0, 180, mc.thePlayer.ticksExisted % 360 * 5, 10.0, i);
      GL11.glLineWidth(0.5F);
      RenderUtil.drawArc(12.0, 12.0, 180, mc.thePlayer.ticksExisted % 360 * 5, 11.0, j);
      GlStateManager.popMatrix();
      GL11.glEnable(2884);
      GlStateManager.popMatrix();
   }

   private int getArrayColor(long l, Category category, int i, int j) {
      HUDArrayMode hudArrayMode = (HUDArrayMode)this.array.getSetting("Mode").getValue();
      Color color = ((ColorSetting)this.array.getSetting("Color")).getValue();
      switch (hudArrayMode) {
         case STATIC:
            return color.getRGB();
         case WAVY:
            float f = 1.0F - MathHelper.abs(MathHelper.sin((float)l % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
            return new Color(
                  MathHelper.clamp_int((int)(color.getRed() * f), 0, 255),
                  MathHelper.clamp_int((int)(color.getGreen() * f), 0, 255),
                  MathHelper.clamp_int((int)(color.getBlue() * f), 0, 255)
               )
               .getRGB();
         case RAINBOW:
            return this.pulsingColor(l, 1.0F).getRGB();
         case RAINBOW_2:
            return rainbow(j * 2 * 200000000L, 1.0F).getRGB();
         case CATEGORY:
            return category.getColor();
         case TRANS:
            return HUDFlagColor.colorOf(HUDFlagColor.values()[i]).getRGB();
         default:
            return client.getColorSetting().getValue().getRGB();
      }
   }

   private double toBlocksPerSecond(double d) {
      return d / 0.05F;
   }

   private void drawLeftInfo() {
      this.leftInfoLines.clear();
      double d = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double e = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      double d2 = MathHelper.sqrt_double(d * d + e * e) * mc.timer.timerSpeed;
      String string = "%.2f";
      String string2 = String.format(string + " blocks/sec", this.toBlocksPerSecond(d2));
      if (this.information.getSetting("Cords").asBoolean()) {
         this.leftInfoLines.add(String.format("%s, %s, %s", (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ));
      }

      if (!(mc.currentScreen instanceof GuiChat) && this.information.getSetting("Speed").asBoolean()) {
         this.leftInfoLines.add(string2);
      }

      if (this.information.getSetting("FPS").asBoolean()) {
         this.leftInfoLines.add("FPS: " + Minecraft.B());
      }

      byte b = 0;

      for (String string3 : this.leftInfoLines) {
         client.getCustomFontRenderer()
            .drawStringWithShadow(
               string3,
               1.0F,
               (int)(
                  RenderUtil.getScaledHeight() - client.getCustomFontRenderer().getStringHeight(string3) - b - (mc.currentScreen instanceof GuiChat ? 15 : 0)
               ),
               Color.WHITE.getRGB()
            );
         b += 10;
      }
   }

   private void drawRightInfo(GuiIngame guiIngame) {
      this.rightInfoLines.clear();
      String string;
      if (isPowerBranding()) {
         string = String.format(
            "§7Power build - §f%s - §a%s",
            isDevelopment
               ? String.valueOf(this.buildDate.getYear()).substring(2) + "" + this.buildDate.getMonthValue() + "" + this.buildDate.getDayOfMonth()
               : client.getVersion(),
            mc.mouseHelper.Y().getAccount().getUsername()
         );
      } else {
         string = String.format(
            "§7%s - §f§l%s §r§7- %s",
            isDevelopment ? "Development" : (client.getVersion().contains("BETA") ? "Beta" : "Release"),
            isDevelopment
               ? String.valueOf(this.buildDate.getYear()).substring(2) + "" + this.buildDate.getMonthValue() + "" + this.buildDate.getDayOfMonth()
               : client.getVersion().replace("BETA", ""),
            mc.mouseHelper.Y().getAccount().getUsername()
         );
      }

      if (this.information.getSetting("Version").asBoolean()) {
         this.rightInfoLines.add(string);
      }

      int i = mc.thePlayer.getPlayerInfo() != null && mc.thePlayer.getPlayerInfo().getResponseTime() > 1
         ? mc.thePlayer.getPlayerInfo().getResponseTime()
         : (mc.getCurrentServerData() != null && mc.getCurrentServerData().pingToServer > 0L ? (int)mc.getCurrentServerData().pingToServer : 0);
      if (this.information.getSetting("Ping").asBoolean() && i > 0) {
         this.rightInfoLines.add(String.format("Ping: §7%sms", Math.min(i, 999)));
      }

      if (!mc.isSingleplayer() && this.information.getSetting("TPS").asBoolean()) {
         this.rightInfoLines.add(String.format("TPS: §7%s", MathUtil.ONE_DECIMAL.format(getTps())));
      }

      byte b = 0;

      for (String string2 : this.rightInfoLines) {
         client.getCustomFontRenderer()
            .drawStringWithShadow(
               string2,
               (int)(RenderUtil.getScaledWidth() - client.getCustomFontRenderer().getStringWidth(string2) - 1.0F)
                  - (!string2.contains("Development") && !string2.contains("Beta") && !string2.contains("Release") ? 0 : 3),
               (int)(
                  (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? 11.0F : RenderUtil.getScaledHeight())
                     - client.getCustomFontRenderer().getStringHeight(string2)
                     - b
                     - (mc.currentScreen instanceof GuiChat && this.array.getSetting("Position").getValue() == HUDArrayPositionMode.UP ? 15 : 0)
               ),
               Color.WHITE.getRGB()
            );
         b += this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? -10 : 10;
      }

      if (this.information.getSetting("Potions").asBoolean()) {
         for (PotionEffect potionEffect : mc.thePlayer.getActivePotionEffects()) {
            Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
            String string3 = I18n.format(potion.getName())
               + " §7"
               + (potionEffect.getAmplifier() + 1 == 1 ? "" : potionEffect.getAmplifier() + 1 + " ")
               + "- "
               + Potion.getDurationString(potionEffect);
            int j = (int)(RenderUtil.getScaledWidth() - client.getCustomFontRenderer().getStringWidth(string3) - 1.0F);
            int k = (int)(
               (this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? 11.0F : RenderUtil.getScaledHeight())
                  - client.getCustomFontRenderer().getStringHeight(string3)
                  - b
                  - (mc.currentScreen instanceof GuiChat && this.array.getSetting("Position").getValue() == HUDArrayPositionMode.UP ? 15 : 0)
            );
            client.getCustomFontRenderer().drawStringWithShadow(string3, j, k, potion.getLiquidColor());
            if (potion.hasStatusIcon()) {
               GL11.glPushMatrix();
               GL11.glScalef(0.5F, 0.5F, 0.5F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               mc.getTextureManager().bindTexture(GuiContainer.inventoryBackground);
               int m = potion.getStatusIconIndex();
               guiIngame.drawTexturedModalRect((j - 10) * 2, k * 2 + 3, 0 + m % 8 * 18, 198 + m / 8 * 18, 18, 18);
               GL11.glPopMatrix();
            }

            b += this.array.getSetting("Position").getValue() == HUDArrayPositionMode.DOWN ? -10 : 10;
         }
      }
   }

   private Color pulsingColor(long l, float f) {
      float g = 1.0F - MathHelper.abs(MathHelper.sin((float)l % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.5F);
      long m = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(g, 0.5F, 1.0F)), 16);
      Color color = new Color((int)m);
      return new Color(color.getRed() / 255.0F * f, color.getGreen() / 255.0F * f, color.getBlue() / 255.0F * f, color.getAlpha() / 255.0F);
   }

   public static Color rainbow(long l, float f) {
      float g = (float)(System.nanoTime() * 2L + l) / 1.0E10F % 1.0F;
      long m = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(g, 1.0F, 1.0F)), 16);
      Color color = new Color((int)m);
      return new Color(color.getRed() / 255.0F * f, color.getGreen() / 255.0F * f, color.getBlue() / 255.0F * f, color.getAlpha() / 255.0F);
   }

   public TabGui getTabGui() {
      if (this.tabGuiInstance == null) {
         this.tabGuiInstance = new TabGui();
      }

      return this.tabGuiInstance;
   }
}
