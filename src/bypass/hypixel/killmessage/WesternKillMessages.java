package bypass.hypixel.killmessage;

import bypass.hypixel.FinalKillTracker;
import java.util.ArrayList;
import java.util.Arrays;

public class WesternKillMessages {
   private static final long a = 67827385534226L;
   private static final long e = -6832808714668343290L;

   public WesternKillMessages() {
      ArrayList var10000 = FinalKillTracker.c;
      String[] var10001 = new String[(int)e];
      var10001[0] = "(\\w{1,16}) was filled full of lead by (\\w{1,16}).*";
      var10001[1] = "(\\w{1,16}) was iced by (\\w{1,16}).*";
      var10001[2] = "(\\w{1,16}) met their end by (\\w{1,16}).*";
      var10001[3] = "(\\w{1,16}) lost a drinking contest with (\\w{1,16}).*";
      var10001[4] = "(\\w{1,16}) was killed with dynamite by (\\w{1,16}).*";
      var10001[5] = "(\\w{1,16}) lost the draw to (\\w{1,16}).*";
      var10000.addAll(Arrays.asList(var10001));
   }
}
