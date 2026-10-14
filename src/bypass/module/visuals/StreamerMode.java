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
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> d.clear();
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = tickEvent -> {
      if (tickEvent.getState() == EventState.PRE && mc.thePlayer != null && mc.theWorld != null) {
         for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer != mc.thePlayer && !d.contains(entityPlayer.getName())) {
               d.add(entityPlayer.getName());
            }
         }
      }
   };
   private static final long c = 62595111665192L;

   public StreamerMode(Category category) {
      super(category);
      this.addSettings(scrambleNames, hideSkins, spoofName);
   }

   public static String t(NetworkPlayerInfo networkPlayerInfo) {
      return networkPlayerInfo.getDisplayName() != null
         ? networkPlayerInfo.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
   }
}
