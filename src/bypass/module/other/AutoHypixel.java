package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.DeathEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.combat.AutoArmor;
import bypass.module.hidden.PlayerListTracker;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.setting.StringSetting;
import bypass.util.GameUtil;
import bypass.util.MathExpressionParser;
import bypass.util.PathFinder;
import bypass.util.Stopwatch;
import bypass.util.Vector3;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public final class AutoHypixel extends Module {
   public static final MapSetting autoPit = new MapSettingBuilder("Auto-Pit")
      .ordered()
      .description("autopit")
      .add("Bounty-Finder", new BooleanSetting("Bounty-Finder", "Finds for bountied players", false))
      .add("Bounty-Notifier", new BooleanSetting("Bounty-Notifier", "Notifies bountied players", false))
      .add("Quick-Math", new BooleanSetting("Quick-Math", "Auto solves quick math", true))
      .add("Death-TP", new BooleanSetting("Death-TP", "Teleports to your last position back when dead", false))
      .add("Bounty", new NumberSetting<>("Bounty", "Minimun bounty needed", 1000, 1, 5100, " gold"))
      .build();
   private final MapSetting autoGG = new MapSettingBuilder("Auto-GG")
      .ordered()
      .description("autopit")
      .add("Enabled", new BooleanSetting("Enabled", "Enables Auto-GG", false))
      .add("Text", new StringSetting("Text", "Text to say", "gg"))
      .build();
   public static final MapSetting autoChecker = new MapSettingBuilder("Auto-Checker")
      .ordered()
      .description("auto checker")
      .add("Enabled", new BooleanSetting("Enabled", "Enables Auto-Checker", false))
      .add("Render", new BooleanSetting("Render", "Renders GUI", true))
      .add("Denick", new BooleanSetting("Denick", "Tries to denick", true))
      .add("API-Key", new StringSetting("API-Key", "Api key", "api key"))
      .add("Auto-API-Key", new BooleanSetting("Auto-API-Key", "Generates new API-Key every log", true))
      .add("Game-Mode", new EnumSetting<>("Game-Mode", "game mode", AutoHypixelAutoCheckerGameMode.SW))
      .add("Pos-X", new NumberSetting<>("Pos-X", "Pos", 300.0F, 1.0F, 50000.0F))
      .add("Pos-Y", new NumberSetting<>("Pos-Y", "Pos", 300.0F, 1.0F, 50000.0F))
      .add("Rows", new NumberSetting<>("Rows", " rows", 10, 1, 20))
      .build();
   private final MapSetting autoCraft = new MapSettingBuilder("Auto-Craft")
      .ordered()
      .description("auto craft uhc")
      .add("Enabled", new BooleanSetting("Enabled", "Enables Auto-Craft", false))
      .add("Golden-Head", new BooleanSetting("Golden-Head", "Enables golden heads autocraft", false))
      .add("Iron-Pack", new BooleanSetting("Iron-Pack", "Enables iron pack autocraft", false))
      .add("Gold-Pack", new BooleanSetting("Gold-Pack", "Enables gold pack autocraft", false))
      .add("Quick-Pick", new BooleanSetting("Quick-Pick", "Enables quick pick autocraft", false))
      .build();
   private final BooleanSetting autoTip = new BooleanSetting("Auto-Tip", "Auto tip", true);
   private final BooleanSetting autoPlay = new BooleanSetting("Auto-Play", "Auto plays", true);
   private final BooleanSetting bannedLookup = new BooleanSetting("Banned-Lookup", "Tries to find the banned player in your lobby", true);
   private final BooleanSetting cleanChat = new BooleanSetting("Clean-Chat", "cleaner chat", true);
   private final BooleanSetting autoBack = new BooleanSetting("Auto-Back", "Auto backs when kicked out of the game", true);
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay between each message", 3000, 1, 5000, "ms");
   private final List<String> winMessages = Arrays.asList(
      "1st Killer - ",
      "1st Place - ",
      "Winner: ",
      " - Damage Dealt - ",
      "Winning Team -",
      "1st - ",
      "Winners: ",
      "Winning Team: ",
      " won the game!",
      "Top Seeker: ",
      "1st Place: ",
      "Last team standing!",
      "Winner #1 (",
      "Top Survivors",
      "Winners - "
   );
   private final List<String> spamMessages = Arrays.asList(
      "STREAK! of ", "BOUNTY! bump ", "MULTI KILL! ", "KILL! ", "PIT LEVEL UP! ", "BOUNTY CLAIMED! ", "The game starts in "
   );
   public static List<EntityPlayer> bountiedPlayers = new CopyOnWriteArrayList<>();
   private final Stopwatch actionTimer = new Stopwatch();
   private final Stopwatch bountyScanTimer = new Stopwatch();
   private final Stopwatch playTimer = new Stopwatch();
   private String pendingPlayCommand = null;
   private boolean apiKeyRequested;
   private boolean apiKeyReceived;
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> {
      bountiedPlayers.clear();
      this.actionTimer.reset();
      pitSpawnY = 0.0;
      this.apiKeyRequested = this.apiKeyReceived = false;
      this.pendingPlayCommand = null;
   };
   private String apiKeyPlayer = "";
   @EventHandler(DeathEvent.class)
   private Listener<DeathEvent> onDeath = var0 -> {};
   public static double pitSpawnY;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      long var2 = f ^ 46024758410688L;
      long var10001 = var2 ^ 7074644046134L;
      int var4 = (int)((var2 ^ 7074644046134L) >>> 56);
      int var5 = (int)((var2 ^ 7074644046134L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (!GameUtil.isOnHypixel()) {
         toasts.B("AutoHypixel disable", "Disabling AutoHypixel due to being in other server");
         this.setEnabled(false);
      } else {
         if (var1x.getState() == EventState.PRE && cooldownTicks > 0) {
            cooldownTicks--;
         }

         if (autoChecker.getSetting("Auto-API-Key").asBoolean()
            && (!this.apiKeyPlayer.equals(mc.thePlayer.getName()) && mc.thePlayer.ticksExisted > 20 || this.apiKeyRequested && !this.apiKeyReceived)
            && this.actionTimer.hasElapsed(1000.0F)) {
            mc.thePlayer.sendChatMessage("/api new");
            this.apiKeyRequested = true;
            this.apiKeyPlayer = mc.thePlayer.getName();
            this.actionTimer.reset();
         }

         if (this.playTimer.hasElapsed(this.delay.getValue().intValue()) && this.autoPlay.getValue() && this.pendingPlayCommand != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(this.pendingPlayCommand));
            this.pendingPlayCommand = null;
         }

         if (GameUtil.scoreboardTitleContains("PIT") && this.bountyScanTimer.hasElapsed(1500.0F)) {
            if (pitSpawnY == 0.0) {
               for (Entity var16 : mc.theWorld.loadedEntityList) {
                  if (var16 instanceof EntityArmorStand && var16.getDisplayName().getUnformattedText().contains("PRESTIGE")) {
                     pitSpawnY = var16.posY - 5.0;
                  }
               }

               return;
            }

            bountiedPlayers.removeIf(var0 -> var0.posY > pitSpawnY || var0.getDisplayName().getUnformattedText().split(" ").length <= 2);

            for (EntityPlayer var9 : mc.theWorld.playerEntities) {
               String var10 = StringUtils.stripControlCodes(var9.getDisplayName().getUnformattedText()).replaceAll("[ ](?=[ ])|[^-_,A-Za-z0-9 ]+", "");
               String var11 = var10.substring(var10.lastIndexOf(" ") + 1);
               if (var11.endsWith("g")
                  && !var11.equals(var9.getName())
                  && var9.posY < pitSpawnY
                  && !bountiedPlayers.contains(var9)
                  && !client.getRelationManager().z(var9)
                  && var9 != mc.thePlayer) {
                  String var12 = StringUtils.stripControlCodes(var11.replace("g", ""));

                  try {
                     int var13 = Integer.parseInt(var12);
                     if (var13 >= autoPit.getSetting("Bounty").asInt()) {
                        bountiedPlayers.add(var9);
                        if (autoPit.getSetting("Bounty-Notifier").asBoolean()) {
                           toasts.B("Found bountied player", String.format("%s has %s bounty", var9.getName(), var11));
                        }
                     }
                  } catch (NumberFormatException var14) {
                  }
               }
            }

            if (this.actionTimer.hasElapsed(10000.0F) && autoPit.getSetting("Bounty-Finder").asBoolean() && bountiedPlayers.isEmpty()) {
               mc.thePlayer.sendChatMessage("/play pit");
               toasts.D("Joining new pit game");
            }

            this.bountyScanTimer.reset();
         }
      }
   };
   public static int cooldownTicks;
   private final Stopwatch unusedTimer = new Stopwatch();
   private final Stopwatch unusedTimer2 = new Stopwatch();
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() != PacketDirection.RECEIVED) {
         if (var1x.getPacket() instanceof C01PacketChatMessage && !((C01PacketChatMessage)var1x.getPacket()).getMessage().startsWith("/")) {
            this.actionTimer.reset();
         }
      } else {
         if (var1x.getPacket() instanceof S02PacketChat) {
            S02PacketChat var5 = (S02PacketChat)var1x.getPacket();
            String var6 = EnumChatFormatting.getTextWithoutFormattingCodes(var5.getChatComponent().getUnformattedText());
            if (var6 == null) {
               return;
            }

            if (this.autoGG.getSetting("Enabled").asBoolean()
               && this.actionTimer.hasElapsed(this.delay.getValue().intValue())
               && this.winMessages.stream().anyMatch(var6::contains)
               && var6.startsWith(" ")) {
               mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(this.autoGG.getSetting("Text").asString()));
            }

            if (autoChecker.getSetting("Auto-API-Key").asBoolean() && var5.getChatComponent().getUnformattedText().contains("Your new API key is ")) {
               String[] var7 = var5.getChatComponent().getUnformattedText().split("Your new API key is ");
               ((StringSetting)autoChecker.getSetting("API-Key")).setValue(var7[1]);
               toasts.g("Auto-Stats", "Gathered new API key");
               var1x.cancel();
               this.apiKeyReceived = true;
               this.apiKeyRequested = false;
            }

            if (this.bannedLookup.getValue() && var6.equals("A player has been removed from your game.")) {
               PlayerListTracker.printRecentDisconnects();
            }

            if (this.autoPlay.getValue()) {
               for (IChatComponent var8 : var5.getChatComponent()) {
                  ClickEvent var9 = var8.getChatStyle().getChatClickEvent();
                  if (var9 != null && var9.getAction() == ClickEvent.Action.RUN_COMMAND && var9.getValue().contains("/play")) {
                     this.pendingPlayCommand = var9.getValue();
                     toasts.G("Joining game", "Joining game in " + this.delay.getValue() / 1000 + " seconds", this.delay.getValue());
                     this.playTimer.reset();
                  }
               }
            }

            if (this.autoCraft.getSetting("Enabled").asBoolean()) {
               for (IChatComponent var19 : var5.getChatComponent()) {
                  ClickEvent var22 = var19.getChatStyle().getChatClickEvent();
                  if (var22 != null && var22.getAction() == ClickEvent.Action.RUN_COMMAND && var22.getValue() != null && !AutoArmor.c()) {
                     if (var22.getValue().contains("item IRON_INGOTS") && this.autoCraft.getSetting("Iron-Pack").asBoolean()) {
                        mc.thePlayer.sendChatMessage(var22.getValue());
                        this.actionTimer.reset();
                     }

                     if (var22.getValue().contains("item GOLDEN_HEAD") && this.autoCraft.getSetting("Golden-Head").asBoolean()) {
                        mc.thePlayer.sendChatMessage(var22.getValue());
                        this.actionTimer.reset();
                     }

                     if (var22.getValue().contains("item GOLD_PACK") && this.autoCraft.getSetting("Gold-Pack").asBoolean()) {
                        mc.thePlayer.sendChatMessage(var22.getValue());
                        this.actionTimer.reset();
                     }

                     if (var22.getValue().contains("item EFFICIENCY_PICKAXE") && this.autoCraft.getSetting("Quick-Pick").asBoolean()) {
                        mc.thePlayer.sendChatMessage(var22.getValue());
                        this.actionTimer.reset();
                     }
                  }
               }
            }

            if (this.autoTip.getValue()
               && this.actionTimer.hasElapsed(this.delay.getValue().intValue())
               && var6.contains("Gain XP and coins by » CLICKING HERE! «")) {
               mc.thePlayer.sendChatMessage("/tip all");
               var1x.cancel();
            }

            if (this.autoBack.getValue()
               && mc.thePlayer != null
               && mc.theWorld != null
               && (
                  var5.getChatComponent().getFormattedText().contains("§cA kick occurred in your connection")
                     || var5.getChatComponent().getFormattedText().startsWith("§c") && var5.getChatComponent().getFormattedText().contains("so you were put")
               )) {
               mc.thePlayer.sendQueue.u(new C01PacketChatMessage("/back"));
            }

            if (autoPit.getSetting("Quick-Math").asBoolean()
               && mc.thePlayer != null
               && mc.theWorld != null
               && GameUtil.scoreboardTitleContains("PIT")
               && var5.getChatComponent().getFormattedText().contains("§r§d§lQUICK MATHS! §r§7Solve")) {
               String[] var16 = var6.split("Solve: ");
               if (var16.length == 2) {
                  try {
                     mc.thePlayer.sendQueue.u(new C01PacketChatMessage("/ac " + (int)evaluate(var16[1].replace("x", "*"))));
                  } catch (RuntimeException var10) {
                  }
               }
            }

            if (this.cleanChat.getValue()) {
               for (String var20 : this.spamMessages) {
                  if (var6.contains(var20)) {
                     var1x.cancel();
                  }
               }
            }

            if (var6.contains("Please don't spam the command!")
               || var6.contains("You already tipped everyone")
               || var6.contains("You already tipped everyone that has boosters active")) {
               var1x.cancel();
            }
         }

         if (var1x.getPacket() instanceof S2DPacketOpenWindow) {
            S2DPacketOpenWindow var11 = (S2DPacketOpenWindow)var1x.getPacket();
            if (this.autoCraft.getSetting("Enabled").asBoolean()
               && var11.getGuiId() != null
               && var11.getGuiId().equals("minecraft:chest")
               && var11.getWindowTitle().getUnformattedText().equals("Crafting Table")
               && !this.actionTimer.hasElapsed(500.0F)) {
               mc.playerController.windowClick(var11.getWindowId(), 24, 0, 1, mc.thePlayer);
               mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(var11.getWindowId()));
               var1x.cancel();
            }
         }

         if (var1x.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook var12 = (S08PacketPlayerPosLook)var1x.getPacket();
            if (autoPit.getSetting("Death-TP").asBoolean()
               && mc.thePlayer != null
               && mc.theWorld != null
               && GameUtil.scoreboardTitleContains("PIT")
               && Math.abs(pitSpawnY - var12.getY()) < 4.0
               && this.actionTimer.hasElapsed(1000.0F)) {
               mc.thePlayer
                  .sendQueue
                  .u(new C03PacketPlayer.C06PacketPlayerPosLook(var12.getX(), var12.getY(), var12.getZ(), var12.getYaw(), var12.getPitch(), false));

               for (Vector3 var21 : PathFinder.i(
                  new Vector3(var12.getX(), var12.getY(), var12.getZ()), new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), 8.9
               )) {
                  mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(var21.getX(), var21.getY(), var21.getZ(), false));
               }

               var1x.cancel();
               this.actionTimer.reset();
            }
         }
      }
   };
   private static final long f = 104434831927723L;

   public AutoHypixel(Category var1) {
      super(var1);
      this.addSettings(
         autoPit, autoChecker, this.autoGG, this.autoCraft, this.autoTip, this.delay, this.autoPlay, this.autoBack, this.cleanChat, this.bannedLookup
      );
   }

   @Override
   public void onDisable() {
      bountiedPlayers.clear();
      super.onDisable();
   }

   public static double evaluate(String var0) {
      return new MathExpressionParser(var0).parse();
   }
}
