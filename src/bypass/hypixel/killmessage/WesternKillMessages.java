package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.Arrays;

public class WesternKillMessages {
   public WesternKillMessages() {
      FinalKillTracker.killMessagePatterns
         .addAll(
            Arrays.asList(
               "(\\w{1,16}) was filled full of lead by (\\w{1,16}).*",
               "(\\w{1,16}) was iced by (\\w{1,16}).*",
               "(\\w{1,16}) met their end by (\\w{1,16}).*",
               "(\\w{1,16}) lost a drinking contest with (\\w{1,16}).*",
               "(\\w{1,16}) was killed with dynamite by (\\w{1,16}).*",
               "(\\w{1,16}) lost the draw to (\\w{1,16}).*"
            )
         );
   }
}
