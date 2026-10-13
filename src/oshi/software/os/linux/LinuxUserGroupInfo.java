package oshi.software.os.linux;

import java.util.HashMap;
import java.util.Map;
import oshi.util.ExecutingCommand;
import oshi.util.tuples.Pair;

public class LinuxUserGroupInfo {
   private final Map<String, Pair<String, String>> usersIdMap = getUserMap();
   private final Map<String, String> groupsIdMap = getGroupMap();
   private static final long a = 25708782394808L;

   public Pair<String, String> getUser(String var1) {
      return this.usersIdMap.getOrDefault(var1, new Pair<>(var1, "unknown"));
   }

   public String getGroupName(String var1) {
      return this.groupsIdMap.getOrDefault(var1, "unknown");
   }

   private static Map<String, Pair<String, String>> getUserMap() {
      HashMap var3 = new HashMap();

      for (String var6 : ExecutingCommand.runNative("getent passwd")) {
         String[] var7 = var6.split(":");
         if (var7.length >= 3) {
            String var8 = var7[0];
            String var9 = var7[2];
            var3.putIfAbsent(var9, new Pair<>(var9, var8));
         }
      }

      return var3;
   }

   private static Map<String, String> getGroupMap() {
      HashMap var3 = new HashMap();

      for (String var6 : ExecutingCommand.runNative("getent group")) {
         String[] var7 = var6.split(":");
         if (var7.length > 2) {
            String var8 = var7[0];
            String var9 = var7[2];
            var3.putIfAbsent(var9, var8);
         }
      }

      return var3;
   }
}
