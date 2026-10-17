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
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      bountiedPlayers.clear();
      this.actionTimer.reset();
      pitSpawnY = 0.0;
      this.apiKeyRequested = this.apiKeyReceived = false;
      this.pendingPlayCommand = null;
   };
   private String apiKeyPlayer = "";
   @EventHandler(DeathEvent.class)
   private Listener<DeathEvent> onDeath = deathEvent -> {};
   public static double pitSpawnY;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (!GameUtil.isOnHypixel()) {
         toasts.infoMessage("AutoHypixel disable", "Disabling AutoHypixel due to being in other server");
         this.setEnabled(false);
      } else {
         if (updateEvent.getState() == EventState.PRE && cooldownTicks > 0) {
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
               for (Entity entity : mc.theWorld.loadedEntityList) {
                  if (entity instanceof EntityArmorStand && entity.getDisplayName().getUnformattedText().contains("PRESTIGE")) {
                     pitSpawnY = entity.posY - 5.0;
                  }
               }

               return;
            }

            bountiedPlayers.removeIf(entityPlayer -> entityPlayer.posY > pitSpawnY || entityPlayer.getDisplayName().getUnformattedText().split(" ").length <= 2);

            for (EntityPlayer entityPlayer2 : mc.theWorld.playerEntities) {
               String string = StringUtils.stripControlCodes(entityPlayer2.getDisplayName().getUnformattedText()).replaceAll("[ ](?=[ ])|[^-_,A-Za-z0-9 ]+", "");
               String string2 = string.substring(string.lastIndexOf(" ") + 1);
               if (string2.endsWith("g")
                  && !string2.equals(entityPlayer2.getName())
                  && entityPlayer2.posY < pitSpawnY
                  && !bountiedPlayers.contains(entityPlayer2)
                  && !client.getRelationManager().isFriend(entityPlayer2)
                  && entityPlayer2 != mc.thePlayer) {
                  String string3 = StringUtils.stripControlCodes(string2.replace("g", ""));

                  try {
                     int i = Integer.parseInt(string3);
                     if (i >= autoPit.getSetting("Bounty").asInt()) {
                        bountiedPlayers.add(entityPlayer2);
                        if (autoPit.getSetting("Bounty-Notifier").asBoolean()) {
                           toasts.infoMessage("Found bountied player", String.format("%s has %s bounty", entityPlayer2.getName(), string2));
                        }
                     }
                  } catch (NumberFormatException numberFormatException) {
                  }
               }
            }

            if (this.actionTimer.hasElapsed(10000.0F) && autoPit.getSetting("Bounty-Finder").asBoolean() && bountiedPlayers.isEmpty()) {
               mc.thePlayer.sendChatMessage("/play pit");
               toasts.info("Joining new pit game");
            }

            this.bountyScanTimer.reset();
         }
      }
   };
   public static int cooldownTicks;
   private final Stopwatch unusedTimer = new Stopwatch();
   private final Stopwatch unusedTimer2 = new Stopwatch();
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() != PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof C01PacketChatMessage && !((C01PacketChatMessage)packetEvent.getPacket()).getMessage().startsWith("/")) {
            this.actionTimer.reset();
         }
      } else {
         if (packetEvent.getPacket() instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
            String string = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());
            if (string == null) {
               return;
            }

            if (this.autoGG.getSetting("Enabled").asBoolean()
               && this.actionTimer.hasElapsed(this.delay.getValue().intValue())
               && this.winMessages.stream().anyMatch(string::contains)
               && string.startsWith(" ")) {
               mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(this.autoGG.getSetting("Text").asString()));
            }

            if (autoChecker.getSetting("Auto-API-Key").asBoolean() && s02PacketChat.getChatComponent().getUnformattedText().contains("Your new API key is ")) {
               String[] strings = s02PacketChat.getChatComponent().getUnformattedText().split("Your new API key is ");
               ((StringSetting)autoChecker.getSetting("API-Key")).setValue(strings[1]);
               toasts.successMessage("Auto-Stats", "Gathered new API key");
               packetEvent.cancel();
               this.apiKeyReceived = true;
               this.apiKeyRequested = false;
            }

            if (this.bannedLookup.getValue() && string.equals("A player has been removed from your game.")) {
               PlayerListTracker.printRecentDisconnects();
            }

            if (this.autoPlay.getValue()) {
               for (IChatComponent iChatComponent : s02PacketChat.getChatComponent()) {
                  ClickEvent clickEvent = iChatComponent.getChatStyle().getChatClickEvent();
                  if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND && clickEvent.getValue().contains("/play")) {
                     this.pendingPlayCommand = clickEvent.getValue();
                     toasts.infoTimed("Joining game", "Joining game in " + this.delay.getValue() / 1000 + " seconds", this.delay.getValue());
                     this.playTimer.reset();
                  }
               }
            }

            if (this.autoCraft.getSetting("Enabled").asBoolean()) {
               for (IChatComponent iChatComponent2 : s02PacketChat.getChatComponent()) {
                  ClickEvent clickEvent2 = iChatComponent2.getChatStyle().getChatClickEvent();
                  if (clickEvent2 != null && clickEvent2.getAction() == ClickEvent.Action.RUN_COMMAND && clickEvent2.getValue() != null && !AutoArmor.isInventoryFull()) {
                     if (clickEvent2.getValue().contains("item IRON_INGOTS") && this.autoCraft.getSetting("Iron-Pack").asBoolean()) {
                        mc.thePlayer.sendChatMessage(clickEvent2.getValue());
                        this.actionTimer.reset();
                     }

                     if (clickEvent2.getValue().contains("item GOLDEN_HEAD") && this.autoCraft.getSetting("Golden-Head").asBoolean()) {
                        mc.thePlayer.sendChatMessage(clickEvent2.getValue());
                        this.actionTimer.reset();
                     }

                     if (clickEvent2.getValue().contains("item GOLD_PACK") && this.autoCraft.getSetting("Gold-Pack").asBoolean()) {
                        mc.thePlayer.sendChatMessage(clickEvent2.getValue());
                        this.actionTimer.reset();
                     }

                     if (clickEvent2.getValue().contains("item EFFICIENCY_PICKAXE") && this.autoCraft.getSetting("Quick-Pick").asBoolean()) {
                        mc.thePlayer.sendChatMessage(clickEvent2.getValue());
                        this.actionTimer.reset();
                     }
                  }
               }
            }

            if (this.autoTip.getValue()
               && this.actionTimer.hasElapsed(this.delay.getValue().intValue())
               && string.contains("Gain XP and coins by » CLICKING HERE! «")) {
               mc.thePlayer.sendChatMessage("/tip all");
               packetEvent.cancel();
            }

            if (this.autoBack.getValue()
               && mc.thePlayer != null
               && mc.theWorld != null
               && (
                  s02PacketChat.getChatComponent().getFormattedText().contains("§cA kick occurred in your connection")
                     || s02PacketChat.getChatComponent().getFormattedText().startsWith("§c") && s02PacketChat.getChatComponent().getFormattedText().contains("so you were put")
               )) {
               mc.thePlayer.sendQueue.sendPacketNoEvent(new C01PacketChatMessage("/back"));
            }

            if (autoPit.getSetting("Quick-Math").asBoolean()
               && mc.thePlayer != null
               && mc.theWorld != null
               && GameUtil.scoreboardTitleContains("PIT")
               && s02PacketChat.getChatComponent().getFormattedText().contains("§r§d§lQUICK MATHS! §r§7Solve")) {
               String[] strings2 = string.split("Solve: ");
               if (strings2.length == 2) {
                  try {
                     mc.thePlayer.sendQueue.sendPacketNoEvent(new C01PacketChatMessage("/ac " + (int)evaluate(strings2[1].replace("x", "*"))));
                  } catch (RuntimeException runtimeException) {
                  }
               }
            }

            if (this.cleanChat.getValue()) {
               for (String string2 : this.spamMessages) {
                  if (string.contains(string2)) {
                     packetEvent.cancel();
                  }
               }
            }

            if (string.contains("Please don't spam the command!")
               || string.contains("You already tipped everyone")
               || string.contains("You already tipped everyone that has boosters active")) {
               packetEvent.cancel();
            }
         }

         if (packetEvent.getPacket() instanceof S2DPacketOpenWindow) {
            S2DPacketOpenWindow s2DPacketOpenWindow = (S2DPacketOpenWindow)packetEvent.getPacket();
            if (this.autoCraft.getSetting("Enabled").asBoolean()
               && s2DPacketOpenWindow.getGuiId() != null
               && s2DPacketOpenWindow.getGuiId().equals("minecraft:chest")
               && s2DPacketOpenWindow.getWindowTitle().getUnformattedText().equals("Crafting Table")
               && !this.actionTimer.hasElapsed(500.0F)) {
               mc.playerController.windowClick(s2DPacketOpenWindow.getWindowId(), 24, 0, 1, mc.thePlayer);
               mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(s2DPacketOpenWindow.getWindowId()));
               packetEvent.cancel();
            }
         }

         if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)packetEvent.getPacket();
            if (autoPit.getSetting("Death-TP").asBoolean()
               && mc.thePlayer != null
               && mc.theWorld != null
               && GameUtil.scoreboardTitleContains("PIT")
               && Math.abs(pitSpawnY - s08PacketPlayerPosLook.getY()) < 4.0
               && this.actionTimer.hasElapsed(1000.0F)) {
               mc.thePlayer
                  .sendQueue
                  .sendPacketNoEvent(
                     new C03PacketPlayer.C06PacketPlayerPosLook(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), false)
                  );

               for (Vector3 vector3 : PathFinder.findPath(
                  new Vector3(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ()), new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), 8.9
               )) {
                  mc.thePlayer.sendQueue.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector3.getX(), vector3.getY(), vector3.getZ(), false));
               }

               packetEvent.cancel();
               this.actionTimer.reset();
            }
         }
      }
   };

   public AutoHypixel(Category category) {
      super(category);
      this.addSettings(
         autoPit, autoChecker, this.autoGG, this.autoCraft, this.autoTip, this.delay, this.autoPlay, this.autoBack, this.cleanChat, this.bannedLookup
      );
   }

   @Override
   public void onDisable() {
      bountiedPlayers.clear();
      super.onDisable();
   }

   public static double evaluate(String string) {
      return new MathExpressionParser(string).parse();
   }
}
