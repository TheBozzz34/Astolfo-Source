package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.Arrays;

public class BbqAndBuggedKillMessages {
   private static final long a = 131593924655325L;

   public BbqAndBuggedKillMessages() {
      FinalKillTracker.c
         .addAll(
            Arrays.asList(
               "(\\w{1,16}) was glazed in BBQ sauce by (\\w{1,16}).*",
               "(\\w{1,16}) was sprinkled with chilli powder by (\\w{1,16}).*",
               "(\\w{1,16}) was sliced up by (\\w{1,16}).*",
               "(\\w{1,16}) was overcooked by (\\w{1,16}).*",
               "(\\w{1,16}) was deep fried by (\\w{1,16}).*",
               "(\\w{1,16}) was boiled by (\\w{1,16}).*",
               "(\\w{1,16}) was injected with malware by (\\w{1,16}).*",
               "(\\w{1,16}) was DDoS'd by (\\w{1,16}).*",
               "(\\w{1,16}) was deleted by (\\w{1,16}).*",
               "(\\w{1,16}) was purged by an antivirus owned by (\\w{1,16}).*",
               "(\\w{1,16}) was fragmented by (\\w{1,16}).*",
               "(\\w{1,16}) was corrupted by (\\w{1,16}).*"
            )
         );
   }
}
