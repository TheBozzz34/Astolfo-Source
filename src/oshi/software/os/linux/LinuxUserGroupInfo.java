package oshi.software.os.linux;

import java.util.HashMap;
import java.util.Map;
import oshi.util.ExecutingCommand;
import oshi.util.tuples.Pair;

public class LinuxUserGroupInfo {
   private final Map<String, Pair<String, String>> usersIdMap = getUserMap();
   private final Map<String, String> groupsIdMap = getGroupMap();

   public Pair<String, String> getUser(String userId) {
      return this.usersIdMap.getOrDefault(userId, new Pair<>(userId, "unknown"));
   }

   public String getGroupName(String groupId) {
      return this.groupsIdMap.getOrDefault(groupId, "unknown");
   }

   private static Map<String, Pair<String, String>> getUserMap() {
      HashMap hashMap = new HashMap();

      for (String string : ExecutingCommand.runNative("getent passwd")) {
         String[] strings = string.split(":");
         if (strings.length >= 3) {
            String string2 = strings[0];
            String string3 = strings[2];
            hashMap.putIfAbsent(string3, new Pair<>(string3, string2));
         }
      }

      return hashMap;
   }

   private static Map<String, String> getGroupMap() {
      HashMap hashMap = new HashMap();

      for (String string : ExecutingCommand.runNative("getent group")) {
         String[] strings = string.split(":");
         if (strings.length > 2) {
            String string2 = strings[0];
            String string3 = strings[2];
            hashMap.putIfAbsent(string3, string2);
         }
      }

      return hashMap;
   }
}
