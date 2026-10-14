package oshi.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class LsofUtil {
   private LsofUtil() {
   }

   public static Map<Integer, String> getCwdMap(int pid) {
      List list = ExecutingCommand.runNative("lsof -Fn -d cwd" + (pid < 0 ? "" : " -p " + pid));
      HashMap hashMap = new HashMap();
      Integer integer = -1;

      for (Object object : list) {
         String string = (String)object;
         if (!string.isEmpty()) {
            switch (string.charAt(0)) {
               case 'f':
               default:
                  break;
               case 'n':
                  hashMap.put(integer, string.substring(1));
                  break;
               case 'p':
                  integer = ParseUtil.parseIntOrDefault(string.substring(1), -1);
            }
         }
      }

      return hashMap;
   }
}
