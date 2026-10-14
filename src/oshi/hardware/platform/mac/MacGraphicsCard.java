package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class MacGraphicsCard extends AbstractGraphicsCard {
   public MacGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<MacGraphicsCard> getGraphicsCards() {
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("system_profiler SPDisplaysDataType");
      String string = "unknown";
      String string2 = "unknown";
      String string3 = "unknown";
      ArrayList arrayList2 = new ArrayList();
      long l = 0L;
      int i = 0;

      for (Object object : list) {
         String string4 = (String)object;
         String[] strings = string4.trim().split(":", 2);
         if (strings.length == 2) {
            String string5 = strings[0].toLowerCase();
            if (string5.equals("chipset model")) {
               if (i++ > 0) {
                  arrayList.add(new MacGraphicsCard(string, string2, string3, arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2), l));
                  arrayList2.clear();
               }

               string = strings[1].trim();
            } else if (string5.equals("device id")) {
               string2 = strings[1].trim();
            } else if (string5.equals("vendor")) {
               string3 = strings[1].trim();
            } else if (string5.contains("version") || string5.contains("revision")) {
               arrayList2.add(string4.trim());
            } else if (string5.startsWith("vram")) {
               l = ParseUtil.parseDecimalMemorySizeToBinary(strings[1].trim());
            }
         }
      }

      arrayList.add(new MacGraphicsCard(string, string2, string3, arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2), l));
      return arrayList;
   }
}
