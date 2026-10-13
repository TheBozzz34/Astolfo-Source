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
   private static final long b = 37222365512170L;

   @Override
   public PhysicalMemory[] getPhysicalMemory() {
      ArrayList var4 = new ArrayList();
      List var5 = ExecutingCommand.runNative("dmidecode --type 17");
      int var6 = 0;
      String var7 = "unknown";
      String var8 = "";
      long var9 = 0L;
      long var11 = 0L;
      String var13 = "unknown";
      String var14 = "unknown";

      for (Object var16$raw : var5) {
         String var16 = (String)var16$raw;
         if (var16.trim().contains("DMI type 17")) {
            if (var6++ > 0) {
               if (var9 > 0L) {
                  var4.add(new PhysicalMemory(var7 + var8, var9, var11, var13, var14));
               }

               var7 = "unknown";
               var8 = "";
               var9 = 0L;
               var11 = 0L;
            }
         } else if (var6 > 0) {
            String[] var17 = var16.trim().split(":");
            if (var17.length == 2) {
               switch (var17[0]) {
                  case "Bank Locator":
                     var7 = var17[1].trim();
                     break;
                  case "Locator":
                     var8 = "/" + var17[1].trim();
                     break;
                  case "Size":
                     var9 = ParseUtil.parseDecimalMemorySizeToBinary(var17[1].trim());
                     break;
                  case "Type":
                     var14 = var17[1].trim();
                     break;
                  case "Speed":
                     var11 = ParseUtil.parseHertz(var17[1]);
                     break;
                  case "Manufacturer":
                     var13 = var17[1].trim();
               }
            }
         }
      }

      if (var9 > 0L) {
         var4.add(new PhysicalMemory(var7 + var8, var9, var11, var13, var14));
      }

      return (oshi.hardware.PhysicalMemory[])(var4.toArray(new PhysicalMemory[0]));
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append("Available: ");
      var3.append(FormatUtil.formatBytes(this.d()));
      var3.append("/");
      var3.append(FormatUtil.formatBytes(this.f()));
      return var3.toString();
   }
}
