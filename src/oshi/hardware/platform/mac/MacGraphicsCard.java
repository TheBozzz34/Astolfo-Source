package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class MacGraphicsCard extends AbstractGraphicsCard {
   private static final long b = 136736536686928L;

   public MacGraphicsCard(String var1, String var2, String var3, String var4, long var5) {
      super(var1, var2, var3, var4, var5);
   }

   public static List<MacGraphicsCard> getGraphicsCards() {
      ArrayList var3 = new ArrayList();
      List var4 = ExecutingCommand.runNative("system_profiler SPDisplaysDataType");
      String var5 = "unknown";
      String var6 = "unknown";
      String var7 = "unknown";
      ArrayList var8 = new ArrayList();
      long var9 = 0L;
      int var11 = 0;

      for (Object var13$raw : var4) {
         String var13 = (String)var13$raw;
         String[] var14 = var13.trim().split(":", 2);
         if (var14.length == 2) {
            String var15 = var14[0].toLowerCase();
            if (var15.equals("chipset model")) {
               if (var11++ > 0) {
                  var3.add(new MacGraphicsCard(var5, var6, var7, var8.isEmpty() ? "unknown" : String.join(", ", var8), var9));
                  var8.clear();
               }

               var5 = var14[1].trim();
            } else if (var15.equals("device id")) {
               var6 = var14[1].trim();
            } else if (var15.equals("vendor")) {
               var7 = var14[1].trim();
            } else if (var15.contains("version") || var15.contains("revision")) {
               var8.add(var13.trim());
            } else if (var15.startsWith("vram")) {
               var9 = ParseUtil.parseDecimalMemorySizeToBinary(var14[1].trim());
            }
         }
      }

      var3.add(new MacGraphicsCard(var5, var6, var7, var8.isEmpty() ? "unknown" : String.join(", ", var8), var9));
      return var3;
   }
}
