package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public final class Announcer extends Module {
   private final BooleanSetting skip = new BooleanSetting("Skip", "Skip players if delay not good", true);
   private List<String> j = Arrays.asList("Hello {user}", "Welcome {user}", "Nice to see you, {user}");
   private List<String> O = Arrays.asList("Bye {user}", "See you later, {user}", "Good bye {user}", "Hasta la vista, {user}");
   private final BooleanSetting join = new BooleanSetting("Join", "Welcomes players that join", true);
   private final BooleanSetting leave = new BooleanSetting("Leave", "Welcomes players that leave", true);
   private Stopwatch f = new Stopwatch();
   private List<NetworkPlayerInfo> B = new CopyOnWriteArrayList<>();
   private int h;
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      this.h = 0;
      this.B.clear();
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE) {
         List list = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
         list.remove(mc.thePlayer.sendQueue.getPlayerInfo(mc.session.getUsername()));

         for (Object object : list) {
            NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)object;
            String string = this.m(networkPlayerInfo);
            if (this.h < 150) {
               if (!this.B.contains(networkPlayerInfo)) {
                  this.B.add(networkPlayerInfo);
               }
            } else if (this.join.getValue() && !this.B.contains(networkPlayerInfo)) {
               if (this.f.hasElapsed(1000.0F)) {
                  String string2 = this.j.get(RandomUtils.RANDOM.nextInt(this.j.size())).replace("{user}", string);
                  mc.thePlayer.sendChatMessage(string2);
                  if (!this.skip.getValue()) {
                     this.B.add(networkPlayerInfo);
                  }

                  this.f.reset();
               }

               if (this.skip.getValue()) {
                  this.B.add(networkPlayerInfo);
               }
            }
         }

         if (this.leave.getValue()) {
            for (NetworkPlayerInfo networkPlayerInfo2 : this.B) {
               String string3 = this.m(networkPlayerInfo2);
               if (!list.contains(networkPlayerInfo2) && this.h > 150) {
                  if (this.f.hasElapsed(1000.0F)) {
                     String string4 = this.O.get(RandomUtils.RANDOM.nextInt(this.j.size())).replace("{user}", string3);
                     mc.thePlayer.sendChatMessage(string4);
                     if (!this.skip.getValue()) {
                        this.B.remove(networkPlayerInfo2);
                     }

                     this.f.reset();
                  }

                  if (this.skip.getValue()) {
                     this.B.remove(networkPlayerInfo2);
                  }
               }
            }
         }

         this.h++;
      }
   };
   private static final long d = 85481621302117L;

   public Announcer(Category category) {
      super(category);
      this.addSettings(this.join, this.leave, this.skip);
   }

   @Override
   public void onEnable() {
      this.h = 0;
      this.f.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.B.clear();
      super.onDisable();
   }

   private String m(NetworkPlayerInfo networkPlayerInfo) {
      return networkPlayerInfo.getDisplayName() != null
         ? networkPlayerInfo.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
   }
}
