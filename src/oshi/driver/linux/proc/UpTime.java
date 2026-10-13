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
      String var2 = FileUtil.getStringFromFile(ProcPath.UPTIME);
      int var3 = var2.indexOf((int)b);

      try {
         return var3 < 0 ? 0.0 : Double.parseDouble(var2.substring(0, var3));
      } catch (NumberFormatException var5) {
         return 0.0;
      }
   }
}
