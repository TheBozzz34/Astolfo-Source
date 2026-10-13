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
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> {
      this.h = 0;
      this.B.clear();
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         List var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
         var5.remove(mc.thePlayer.sendQueue.getPlayerInfo(mc.session.getUsername()));

         for (Object var7$raw : var5) {
            NetworkPlayerInfo var7 = (NetworkPlayerInfo)var7$raw;
            String var8 = this.m(var7);
            if (this.h < 150) {
               if (!this.B.contains(var7)) {
                  this.B.add(var7);
               }
            } else if (this.join.getValue() && !this.B.contains(var7)) {
               if (this.f.hasElapsed(1000.0F)) {
                  String var9 = this.j.get(RandomUtils.RANDOM.nextInt(this.j.size())).replace("{user}", var8);
                  mc.thePlayer.sendChatMessage(var9);
                  if (!this.skip.getValue()) {
                     this.B.add(var7);
                  }

                  this.f.reset();
               }

               if (this.skip.getValue()) {
                  this.B.add(var7);
               }
            }
         }

         if (this.leave.getValue()) {
            for (NetworkPlayerInfo var11 : this.B) {
               String var12 = this.m(var11);
               if (!var5.contains(var11) && this.h > 150) {
                  if (this.f.hasElapsed(1000.0F)) {
                     String var13 = this.O.get(RandomUtils.RANDOM.nextInt(this.j.size())).replace("{user}", var12);
                     mc.thePlayer.sendChatMessage(var13);
                     if (!this.skip.getValue()) {
                        this.B.remove(var11);
                     }

                     this.f.reset();
                  }

                  if (this.skip.getValue()) {
                     this.B.remove(var11);
                  }
               }
            }
         }

         this.h++;
      }
   };
   private static final long d = 85481621302117L;

   public Announcer(Category var1) {
      super(var1);
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

   private String m(NetworkPlayerInfo var1) {
      return var1.getDisplayName() != null
         ? var1.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(var1.getPlayerTeam(), var1.getGameProfile().getName());
   }
}
