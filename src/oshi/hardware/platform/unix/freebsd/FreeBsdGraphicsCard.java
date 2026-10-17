package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class FreeBsdGraphicsCard extends AbstractGraphicsCard {
   public FreeBsdGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<FreeBsdGraphicsCard> getGraphicsCards() {
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("pciconf -lv");
      if (list.isEmpty()) {
         return arrayList;
      }

      String string = "unknown";
      String string2 = "unknown";
      String string3 = "unknown";
      String string4 = "";
      String string5 = "unknown";

      for (Object object : list) {
         String string6 = (String)object;
         if (string6.contains("class=0x")) {
            if ("0x03".equals(string4)) {
               arrayList.add(
                  new FreeBsdGraphicsCard(
                     string.isEmpty() ? "unknown" : string,
                     string3.isEmpty() ? "unknown" : string3,
                     string2.isEmpty() ? "unknown" : string2,
                     string5.isEmpty() ? "unknown" : string5,
                     0L
                  )
               );
            }

            String[] strings = ParseUtil.whitespaces.split(string6);

            for (String string7 : strings) {
               String[] strings2 = string7.split("=");
               if (strings2.length > 1) {
                  if (strings2[0].equals("class") && strings2[1].length() >= 4) {
                     string4 = strings2[1].substring(0, 4);
                  } else if (strings2[0].equals("chip") && strings2[1].length() >= 10) {
                     string3 = strings2[1].substring(0, 6);
                     string2 = "0x" + strings2[1].substring(6, 10);
                  } else if (strings2[0].contains("rev")) {
                     string5 = string7;
                  }
               }
            }

            string = "unknown";
         } else {
            String[] strings3 = string6.trim().split("=", 2);
            if (strings3.length == 2) {
               String string8 = strings3[0].trim();
               if (string8.equals("vendor")) {
                  string2 = ParseUtil.getSingleQuoteStringValue(string6) + (string2.equals("unknown") ? "" : " (" + string2 + ")");
               } else if (string8.equals("device")) {
                  string = ParseUtil.getSingleQuoteStringValue(string6);
               }
            }
         }
      }

      if ("0x03".equals(string4)) {
         arrayList.add(
            new FreeBsdGraphicsCard(
               string.isEmpty() ? "unknown" : string, string3.isEmpty() ? "unknown" : string3, string2.isEmpty() ? "unknown" : string2, string5.isEmpty() ? "unknown" : string5, 0L
            )
         );
      }

      return arrayList;
   }
}
