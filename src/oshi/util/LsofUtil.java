package oshi.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class LsofUtil {
   private static final long a = 109724697980051L;

   private LsofUtil() {
   }

   public static Map<Integer, String> getCwdMap(int var0) {
      List var4 = ExecutingCommand.runNative("lsof -Fn -d cwd" + (var0 < 0 ? "" : " -p " + var0));
      HashMap var5 = new HashMap();
      Integer var6 = -1;

      for (Object var8$raw : var4) {
         String var8 = (String)var8$raw;
         if (!var8.isEmpty()) {
            switch (var8.charAt(0)) {
               case 'f':
               default:
                  break;
               case 'n':
                  var5.put(var6, var8.substring(1));
                  break;
               case 'p':
                  var6 = ParseUtil.parseIntOrDefault(var8.substring(1), -1);
            }
         }
      }

      return var5;
   }
}
