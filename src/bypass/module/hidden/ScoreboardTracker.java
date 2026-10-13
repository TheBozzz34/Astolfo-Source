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
   public static ScoreboardInfo N = new ScoreboardInfo(null);
   private String Q = null;
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderEvent> onRender = var1 -> {
      if (var1.getType() == RenderType.SCREEN) {
         N = new ScoreboardInfo(null);
         if (mc.theWorld == null || !GameUtil.isInHypixelGame()) {
            return;
         }

         Scoreboard var5 = mc.theWorld.getScoreboard();
         if (var5 == null) {
            return;
         }

         N = new ScoreboardInfo(var5);
         String var6 = N.s();
         if (var6 != null && !var6.equals(this.Q)) {
            String var7 = N.s();
            if (var7 != null) {
               FinalKillTracker.setGame(var7);
            }

            FinalKillTracker.K();
         }

         this.Q = var6;
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.RECEIVED && var0.getPacket() instanceof S02PacketChat) {
         if (!r()) {
            return;
         }

         S02PacketChat var4 = (S02PacketChat)var0.getPacket();
         String var5 = EnumChatFormatting.getTextWithoutFormattingCodes(var4.getChatComponent().getUnformattedText());
         String var6 = var4.getChatComponent().getFormattedText();
         if (FinalKillTracker.onChatMessage(var6, var5)) {
            var0.setCancelled(true);
         }
      }
   };
   private static final long c = 29572904651931L;

   public ScoreboardTracker() {
      client.getEventBus().subscribe(this);
   }

   public static boolean r() {
      return N.s() != null;
   }

   public static ScoreboardInfo q() {
      return N;
   }
}
