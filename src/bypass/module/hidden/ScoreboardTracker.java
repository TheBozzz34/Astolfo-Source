package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.hypixel.FinalKillTracker;
import bypass.module.HiddenModule;
import bypass.util.GameUtil;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

public final class ScoreboardTracker extends HiddenModule {
   public static ScoreboardInfo current = new ScoreboardInfo(null);
   private String lastGameId = null;
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN) {
         current = new ScoreboardInfo(null);
         if (mc.theWorld == null || !GameUtil.isInHypixelGame()) {
            return;
         }

         Scoreboard scoreboard = mc.theWorld.getScoreboard();
         if (scoreboard == null) {
            return;
         }

         current = new ScoreboardInfo(scoreboard);
         String string = current.getGameId();
         if (string != null && !string.equals(this.lastGameId)) {
            String string2 = current.getGameId();
            if (string2 != null) {
               FinalKillTracker.setGame(string2);
            }

            FinalKillTracker.onGameChanged();
         }

         this.lastGameId = string;
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED && packetEvent.getPacket() instanceof S02PacketChat) {
         if (!isInGame()) {
            return;
         }

         S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
         String string = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());
         String string2 = s02PacketChat.getChatComponent().getFormattedText();
         if (FinalKillTracker.onChatMessage(string2, string)) {
            packetEvent.setCancelled(true);
         }
      }
   };

   public ScoreboardTracker() {
      client.getEventBus().subscribe(this);
   }

   public static boolean isInGame() {
      return current.getGameId() != null;
   }

   public static ScoreboardInfo getScoreboardInfo() {
      return current;
   }
}
