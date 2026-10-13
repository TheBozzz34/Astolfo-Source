package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.ArrayList;
import java.util.Arrays;

public class DefaultKillMessages {
   private static final long a = 21580137480947L;
   private static final long e = -2721530912150913018L;

   public DefaultKillMessages() {
      ArrayList var10000 = FinalKillTracker.c;
      String[] var10001 = new String[(int)e];
      var10001[0] = "(\\w{1,16}) was shot and killed by (\\w{1,16}).*";
      var10001[1] = "(\\w{1,16}) was snowballed to death by (\\w{1,16}).*";
      var10001[2] = "(\\w{1,16}) was killed by (\\w{1,16}).*";
      var10001[3] = "(\\w{1,16}) was killed with a potion by (\\w{1,16}).*";
      var10001[4] = "(\\w{1,16}) was killed with an explosion by (\\w{1,16}).*";
      var10001[5] = "(\\w{1,16}) was killed with magic by (\\w{1,16}).*";
      var10000.addAll(Arrays.asList(var10001));
   }
}
