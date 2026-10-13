package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class SolarisGraphicsCard extends AbstractGraphicsCard {
   private static final long b = 100221904319931L;
   private static final long i = -2370727891903184888L;

   public SolarisGraphicsCard(String var1, String var2, String var3, String var4, long var5) {
      super(var1, var2, var3, var4, var5);
   }

   public static List<SolarisGraphicsCard> getGraphicsCards() {
      ArrayList var3 = new ArrayList();
      List var4 = ExecutingCommand.runNative("prtconf -pv");
      if (var4.isEmpty()) {
         return var3;
      }

      String var5 = "";
      String var6 = "";
      String var7 = "";
      String var8 = "";
      ArrayList var9 = new ArrayList();

      for (Object var11$raw : var4) {
         String var11 = (String)var11$raw;
         if (var11.contains("Node 0x")) {
            if ("0003".equals(var8)) {
               var3.add(
                  new SolarisGraphicsCard(
                     var5.isEmpty() ? "unknown" : var5,
                     var7.isEmpty() ? "unknown" : var7,
                     var6.isEmpty() ? "unknown" : var6,
                     var9.isEmpty() ? "unknown" : String.join(", ", var9),
                     0L
                  )
               );
            }

            var5 = "";
            var6 = "unknown";
            var7 = "unknown";
            var8 = "";
            var9.clear();
         } else {
            String[] var12 = var11.trim().split(":", 2);
            if (var12.length == 2) {
               if (var12[0].equals("model")) {
                  var5 = ParseUtil.getSingleQuoteStringValue(var11);
               } else if (var12[0].equals("name")) {
                  if (var5.isEmpty()) {
                     var5 = ParseUtil.getSingleQuoteStringValue(var11);
                  }
               } else if (var12[0].equals("vendor-id")) {
                  var6 = "0x" + var11.substring(var11.length() - 4);
               } else if (var12[0].equals("device-id")) {
                  var7 = "0x" + var11.substring(var11.length() - 4);
               } else if (var12[0].equals("revision-id")) {
                  var9.add(var11.trim());
               } else if (var12[0].equals("class-code")) {
                  var8 = var11.substring(var11.length() - (int)i, var11.length() - 4);
               }
            }
         }
      }

      if ("0003".equals(var8)) {
         var3.add(
            new SolarisGraphicsCard(
               var5.isEmpty() ? "unknown" : var5,
               var7.isEmpty() ? "unknown" : var7,
               var6.isEmpty() ? "unknown" : var6,
               var9.isEmpty() ? "unknown" : String.join(", ", var9),
               0L
            )
         );
      }

      return var3;
   }
}
