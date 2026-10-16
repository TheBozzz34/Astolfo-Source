package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.Arrays;

public class HolyAndPirateKillMessages {
   public HolyAndPirateKillMessages() {
      FinalKillTracker.killMessagePatterns
         .addAll(
            Arrays.asList(
               "(\\w{1,16}) was smote from afar by (\\w{1,16}).*",
               "(\\w{1,16}) was justly ended by (\\w{1,16}).*",
               "(\\w{1,16}) was purified by (\\w{1,16}).*",
               "(\\w{1,16}) was killed with holy water by (\\w{1,16}).*",
               "(\\w{1,16}) was dealt vengeful justice by (\\w{1,16}).*",
               "(\\w{1,16}) was returned to dust by (\\w{1,16}).*",
               "(\\w{1,16}) be shot and killed by (\\w{1,16}).*",
               "(\\w{1,16}) be snowballed to death by (\\w{1,16}).*",
               "(\\w{1,16}) be sent to Davy Jones' locker by (\\w{1,16}).*",
               "(\\w{1,16}) be killed with rum by (\\w{1,16}).*",
               "(\\w{1,16}) be shot with cannon by (\\w{1,16}).*",
               "(\\w{1,16}) be killed with magic by (\\w{1,16}).*"
            )
         );
   }
}
