package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.gui.hud.PlayerListHud;
import bypass.hypixel.FinalKillTracker;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.Denicker;
import bypass.module.hidden.ScoreboardTracker;
import bypass.relation.Relation;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

public final class PlayerList extends Module {
   private PlayerListHud hud;
   private final NumberSetting<Float> posX = new NumberSetting<>("Pos-X", "Pos", 150.0F, 1.0F, 50000.0F);
   private final NumberSetting<Float> posY = new NumberSetting<>("Pos-Y", "Pos", 150.0F, 1.0F, 50000.0F);
   private final BooleanSetting killCounter = new BooleanSetting("Kill-Counter", "Counts kills", true);
   public static final Map<String, Integer> killCounts = Collections.synchronizedMap(new LinkedHashMap<>());
   public static final String RED_TEAM = EnumChatFormatting.RED + "Red";
   public static final String GREEN_TEAM = EnumChatFormatting.GREEN + "Green";
   public static final String YELLOW_TEAM = EnumChatFormatting.YELLOW + "Yellow";
   public static final String BLUE_TEAM = EnumChatFormatting.BLUE + "Blue";
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN && this.hud != null) {
         if (Mouse.isButtonDown(0)) {
            this.posX.setValue(this.hud.getX());
            this.posY.setValue(this.hud.getYPos());
         } else if (this.posX.getValue() != this.hud.getX() || this.posY.getValue() != this.hud.getYPos()) {
            if (this.hud.getX() != this.posX.getValue()) {
               this.hud.setX(this.posX.getValue());
            }

            if (this.hud.getYPos() != this.posY.getValue()) {
               this.hud.setY(this.posY.getValue());
            }
         }

         int i = renderEvent.getResolution().getScaledHeight();
         int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
         int k = i - Mouse.getY() * i / mc.displayHeight - 1;
         this.hud.getPlayerTags().clear();
         if (this.killCounter.getValue()) {
            if (ScoreboardTracker.isInGame()) {
               if (!ScoreboardTracker.current.areWallsUp() && !ScoreboardTracker.current.areAllWithersAlive()) {
                  this.hud.getPlayerTags().put(RED_TEAM, String.valueOf(FinalKillTracker.getTeamFinals(0)));
                  this.hud.getPlayerTags().put(GREEN_TEAM, String.valueOf(FinalKillTracker.getTeamFinals(1)));
                  this.hud.getPlayerTags().put(YELLOW_TEAM, String.valueOf(FinalKillTracker.getTeamFinals(2)));
                  this.hud.getPlayerTags().put(BLUE_TEAM, String.valueOf(FinalKillTracker.getTeamFinals(3)));
               }
            } else {
               killCounts.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(entry -> {
                  String string = this.hud.getPlayerTags().putIfAbsent(entry.getKey(), entry.getValue() + " " + (entry.getValue() > 1 ? "kills" : "kill"));
               });
            }
         }

         try {
            for (Relation relation : client.getRelationManager().getRelations().values()) {
               if (!relation.isFriend()) {
                  this.hud.getPlayerTags().putIfAbsent(relation.getName(), "enemy");
               }
            }
         } catch (Exception exception) {
         }

         for (String string2 : Denicker.nicks.keySet()) {
            this.hud.getPlayerTags().putIfAbsent(string2, "nicked");
         }

         this.hud.render(j, k);
         if (mc.currentScreen instanceof GuiChat && this.hud.isDragging() && Mouse.isButtonDown(0)) {
            this.hud.updateDragPosition(j, k);
         }

         if (Mouse.isButtonDown(0) && this.hud.isHovered(j, k)) {
            this.hud.setDragging(true, j, k);
         } else {
            this.hud.setDraggingNoOffset(false);
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED && packetEvent.getPacket() instanceof S02PacketChat) {
         S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
         String string = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());
         if (string == null) {
            return;
         }

         if (this.killCounter.getValue()
            && (s02PacketChat.getChatComponent().getFormattedText().startsWith("§r") || s02PacketChat.getChatComponent().getFormattedText().startsWith("§e"))
            && !string.startsWith(" ")
            && s02PacketChat.getChatComponent().getFormattedText().contains("by ")
            && s02PacketChat.getChatComponent().getFormattedText().endsWith("§r")) {
            String[] strings = string.split("by ");

            try {
               synchronized (killCounts) {
                  String string2 = GuiIngame.V(strings[1].split(" ")[0]);
                  int i = killCounts.containsKey(string2) ? killCounts.get(string2) : 0;
                  killCounts.put(string2, i + 1);
               }
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }
   };

   public PlayerList(Category category) {
      super(category);
      this.addSettings(this.posX, this.posY, this.killCounter);
   }

   @Override
   public void onEnable() {
      this.hud = new PlayerListHud(this.posX.asFloat(), this.posY.asFloat(), 150, 15);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.hud = null;
      super.onDisable();
   }
}
