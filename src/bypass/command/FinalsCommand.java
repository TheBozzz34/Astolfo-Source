package bypass.command;

import bypass.hypixel.FinalKillTracker;
import bypass.module.hidden.ScoreboardTracker;
import java.util.Collections;
import net.minecraft.util.EnumChatFormatting;

public class FinalsCommand extends Command {
   public FinalsCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Collections.singletonList("final"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (!ScoreboardTracker.isInGame()) {
         toasts.danger("You must be in a Mega Walls game for this");
      } else {
         if (strings.length < 2) {
            this.printTeamFinals(EnumChatFormatting.RED, "Red", String.valueOf(FinalKillTracker.getTeamFinals(0)));
            this.printTeamFinals(EnumChatFormatting.GREEN, "Green", String.valueOf(FinalKillTracker.getTeamFinals(1)));
            this.printTeamFinals(EnumChatFormatting.YELLOW, "Yellow", String.valueOf(FinalKillTracker.getTeamFinals(2)));
            this.printTeamFinals(EnumChatFormatting.BLUE, "Blue", String.valueOf(FinalKillTracker.getTeamFinals(3)));
         } else if (strings.length == 2) {
         }
      }
   }

   private void printTeamFinals(EnumChatFormatting enumChatFormatting, String string, String string2) {
      chat.createBuilder(false, false).append(".").append(string).color(enumChatFormatting).append(" - ").append(String.valueOf(string2)).build().send();
   }
}
