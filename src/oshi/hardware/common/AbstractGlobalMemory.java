package oshi.hardware.common;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractGlobalMemory implements GlobalMemory {
   @Override
   public PhysicalMemory[] getPhysicalMemory() {
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("dmidecode --type 17");
      int i = 0;
      String string = "unknown";
      String string2 = "";
      long l = 0L;
      long m = 0L;
      String string3 = "unknown";
      String string4 = "unknown";

      for (Object object : list) {
         String string5 = (String)object;
         if (string5.trim().contains("DMI type 17")) {
            if (i++ > 0) {
               if (l > 0L) {
                  arrayList.add(new PhysicalMemory(string + string2, l, m, string3, string4));
               }

               string = "unknown";
               string2 = "";
               l = 0L;
               m = 0L;
            }
         } else if (i > 0) {
            String[] strings = string5.trim().split(":");
            if (strings.length == 2) {
               switch (strings[0]) {
                  case "Bank Locator":
                     string = strings[1].trim();
                     break;
                  case "Locator":
                     string2 = "/" + strings[1].trim();
                     break;
                  case "Size":
                     l = ParseUtil.parseDecimalMemorySizeToBinary(strings[1].trim());
                     break;
                  case "Type":
                     string4 = strings[1].trim();
                     break;
                  case "Speed":
                     m = ParseUtil.parseHertz(strings[1]);
                     break;
                  case "Manufacturer":
                     string3 = strings[1].trim();
               }
            }
         }
      }

      if (l > 0L) {
         arrayList.add(new PhysicalMemory(string + string2, l, m, string3, string4));
      }

      return (oshi.hardware.PhysicalMemory[])(arrayList.toArray(new PhysicalMemory[0]));
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Available: ");
      stringBuilder.append(FormatUtil.formatBytes(this.getAvailable()));
      stringBuilder.append("/");
      stringBuilder.append(FormatUtil.formatBytes(this.getTotal()));
      return stringBuilder.toString();
   }
}
