package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.Arrays;

public class SqueakKillMessages {
   private static final long a = 55828385972815L;

   public SqueakKillMessages() {
      FinalKillTracker.c
         .addAll(
            Arrays.asList(
               "(\\w{1,16}) was squeaked from a distance by (\\w{1,16}).*",
               "(\\w{1,16}) was hit by frozen cheese from (\\w{1,16}).*",
               "(\\w{1,16}) was chewed up by (\\w{1,16}).*",
               "(\\w{1,16}) was chemically cheesed by (\\w{1,16}).*",
               "(\\w{1,16}) was turned into cheese whiz by (\\w{1,16}).*",
               "(\\w{1,16}) was magically squeaked by (\\w{1,16}).*",
               "(\\w{1,16}) starved to death\\.",
               "(\\w{1,16}) hit the ground too hard\\.",
               "(\\w{1,16}) blew up\\.",
               "(\\w{1,16}) exploded\\.",
               "(\\w{1,16}) tried to swim in lava\\.",
               "(\\w{1,16}) went up in flames\\.",
               "(\\w{1,16}) burned to death\\.",
               "(\\w{1,16}) suffocated in a wall\\.",
               "(\\w{1,16}) suffocated\\.",
               "(\\w{1,16}) fell out of the world\\.",
               "(\\w{1,16}) had a block fall on them\\.",
               "(\\w{1,16}) drowned\\.",
               "(\\w{1,16}) died from a cactus\\."
            )
         );
   }
}
