package bypass.command;

import bypass.hypixel.FinalKillTracker;
import bypass.module.hidden.ScoreboardTracker;
import java.util.Collections;
import net.minecraft.util.EnumChatFormatting;

public class FinalsCommand extends Command {
   private static final long d = 6489868230846L;

   public FinalsCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Collections.singletonList("final"));
   }

   @Override
   public void runCommand(String[] var1) {
      if (!ScoreboardTracker.r()) {
         toasts.x("You must be in a Mega Walls game for this");
      } else {
         if (var1.length < 2) {
            this.N(EnumChatFormatting.RED, "Red", String.valueOf(FinalKillTracker.getTeamFinals(0)));
            this.N(EnumChatFormatting.GREEN, "Green", String.valueOf(FinalKillTracker.getTeamFinals(1)));
            this.N(EnumChatFormatting.YELLOW, "Yellow", String.valueOf(FinalKillTracker.getTeamFinals(2)));
            this.N(EnumChatFormatting.BLUE, "Blue", String.valueOf(FinalKillTracker.getTeamFinals(3)));
         } else if (var1.length == 2) {
         }
      }
   }

   private void N(EnumChatFormatting var1, String var2, String var3) {
      chat.D(false, false).i(".").i(var2).M(var1).i(" - ").i(String.valueOf(var3)).N().b();
   }
}
