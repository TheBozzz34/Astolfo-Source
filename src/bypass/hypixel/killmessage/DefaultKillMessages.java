package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.Arrays;

public class DefaultKillMessages {
   public DefaultKillMessages() {
      FinalKillTracker.killMessagePatterns
         .addAll(
            Arrays.asList(
               "(\\w{1,16}) was shot and killed by (\\w{1,16}).*",
               "(\\w{1,16}) was snowballed to death by (\\w{1,16}).*",
               "(\\w{1,16}) was killed by (\\w{1,16}).*",
               "(\\w{1,16}) was killed with a potion by (\\w{1,16}).*",
               "(\\w{1,16}) was killed with an explosion by (\\w{1,16}).*",
               "(\\w{1,16}) was killed with magic by (\\w{1,16}).*"
            )
         );
   }
}
