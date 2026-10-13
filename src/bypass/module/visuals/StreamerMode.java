package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.game.TickEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.StringSetting;
import java.util.ArrayList;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

public final class StreamerMode extends Module {
   public static final BooleanSetting scrambleNames = new BooleanSetting("Scramble-Names", "Scrambles other people usernames", true);
   public static final BooleanSetting hideSkins = new BooleanSetting("Hide-Skins", "Hides skins", true);
   public static final StringSetting spoofName = new StringSetting("Spoof-Name", "Name you're spoofing", "You");
   public static final ArrayList<String> d = new ArrayList<>();
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var0 -> d.clear();
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = var0 -> {
      if (var0.getState() == EventState.PRE && mc.thePlayer != null && mc.theWorld != null) {
         for (EntityPlayer var5 : mc.theWorld.playerEntities) {
            if (var5 != mc.thePlayer && !d.contains(var5.getName())) {
               d.add(var5.getName());
            }
         }
      }
   };
   private static final long c = 62595111665192L;

   public StreamerMode(Category var1) {
      super(var1);
      this.addSettings(scrambleNames, hideSkins, spoofName);
   }

   public static String t(NetworkPlayerInfo var0) {
      return var0.getDisplayName() != null
         ? var0.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(var0.getPlayerTeam(), var0.getGameProfile().getName());
   }
}
