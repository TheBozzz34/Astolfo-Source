package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.Arrays;

public class FireAndLoveKillMessages {
   public FireAndLoveKillMessages() {
      FinalKillTracker.c
         .addAll(
            Arrays.asList(
               "(\\w{1,16}) was struck down by (\\w{1,16}).*",
               "(\\w{1,16}) was turned to dust by (\\w{1,16}).*",
               "(\\w{1,16}) was turned to ash by (\\w{1,16}).*",
               "(\\w{1,16}) was melted by (\\w{1,16}).*",
               "(\\w{1,16}) was incinerated by (\\w{1,16}).*",
               "(\\w{1,16}) was vaporized by (\\w{1,16}).*",
               "(\\w{1,16}) was struck with Cupid's arrow by (\\w{1,16}).*",
               "(\\w{1,16}) was given the cold shoulder by (\\w{1,16}).*",
               "(\\w{1,16}) was hugged too hard by (\\w{1,16}).*",
               "(\\w{1,16}) drank a love potion from (\\w{1,16}).*",
               "(\\w{1,16}) was hit by a love bomb from (\\w{1,16}).*",
               "(\\w{1,16}) was no match for (\\w{1,16}).*"
            )
         );
   }
}
