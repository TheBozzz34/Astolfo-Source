package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class SolarisGraphicsCard extends AbstractGraphicsCard {
   public SolarisGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<SolarisGraphicsCard> getGraphicsCards() {
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("prtconf -pv");
      if (list.isEmpty()) {
         return arrayList;
      }

      String string = "";
      String string2 = "";
      String string3 = "";
      String string4 = "";
      ArrayList arrayList2 = new ArrayList();

      for (Object object : list) {
         String string5 = (String)object;
         if (string5.contains("Node 0x")) {
            if ("0003".equals(string4)) {
               arrayList.add(
                  new SolarisGraphicsCard(
                     string.isEmpty() ? "unknown" : string,
                     string3.isEmpty() ? "unknown" : string3,
                     string2.isEmpty() ? "unknown" : string2,
                     arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2),
                     0L
                  )
               );
            }

            string = "";
            string2 = "unknown";
            string3 = "unknown";
            string4 = "";
            arrayList2.clear();
         } else {
            String[] strings = string5.trim().split(":", 2);
            if (strings.length == 2) {
               if (strings[0].equals("model")) {
                  string = ParseUtil.getSingleQuoteStringValue(string5);
               } else if (strings[0].equals("name")) {
                  if (string.isEmpty()) {
                     string = ParseUtil.getSingleQuoteStringValue(string5);
                  }
               } else if (strings[0].equals("vendor-id")) {
                  string2 = "0x" + string5.substring(string5.length() - 4);
               } else if (strings[0].equals("device-id")) {
                  string3 = "0x" + string5.substring(string5.length() - 4);
               } else if (strings[0].equals("revision-id")) {
                  arrayList2.add(string5.trim());
               } else if (strings[0].equals("class-code")) {
                  string4 = string5.substring(string5.length() - 8, string5.length() - 4);
               }
            }
         }
      }

      if ("0003".equals(string4)) {
         arrayList.add(
            new SolarisGraphicsCard(
               string.isEmpty() ? "unknown" : string,
               string3.isEmpty() ? "unknown" : string3,
               string2.isEmpty() ? "unknown" : string2,
               arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2),
               0L
            )
         );
      }

      return arrayList;
   }
}
