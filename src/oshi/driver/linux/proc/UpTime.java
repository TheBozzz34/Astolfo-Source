package oshi.driver.linux.proc;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class UpTime {
   private static final long a = 29125336846065L;
   private static final long b = -8587029781063663584L;

   private UpTime() {
   }

   public static double getSystemUptimeSeconds() {
      String string = FileUtil.getStringFromFile(ProcPath.UPTIME);
      int i = string.indexOf((int)b);

      try {
         return i < 0 ? 0.0 : Double.parseDouble(string.substring(0, i));
      } catch (NumberFormatException numberFormatException) {
         return 0.0;
      }
   }
}
