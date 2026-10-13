package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class FreeBsdGraphicsCard extends AbstractGraphicsCard {
   private static final long b = 85341970654616L;

   public FreeBsdGraphicsCard(String var1, String var2, String var3, String var4, long var5) {
      super(var1, var2, var3, var4, var5);
   }

   public static List<FreeBsdGraphicsCard> getGraphicsCards() {
      long var0 = b ^ 140393935783230L;
      ArrayList var3 = new ArrayList();
      List var4 = ExecutingCommand.runNative("pciconf -lv");
      if (var4.isEmpty()) {
         return var3;
      }

      String var5 = "unknown";
      String var6 = "unknown";
      String var7 = "unknown";
      String var8 = "";
      long var10000 = 5222384442898738441L ^ var0;
      String var9 = "unknown";

      for (Object var11$raw : var4) {
         String var11 = (String)var11$raw;
         if (var11.contains("class=0x")) {
            if ("0x03".equals(var8)) {
               var3.add(
                  new FreeBsdGraphicsCard(
                     var5.isEmpty() ? "unknown" : var5,
                     var7.isEmpty() ? "unknown" : var7,
                     var6.isEmpty() ? "unknown" : var6,
                     var9.isEmpty() ? "unknown" : var9,
                     0L
                  )
               );
            }

            String[] var18 = ParseUtil.Q.split(var11);

            for (String var16 : var18) {
               String[] var17 = var16.split("=");
               if (var17.length > 1) {
                  if (var17[0].equals("class") && var17[1].length() >= 4) {
                     var8 = var17[1].substring(0, 4);
                  } else if (var17[0].equals("chip") && var17[1].length() >= 10) {
                     var7 = var17[1].substring(0, 6);
                     var6 = "0x" + var17[1].substring(6, 10);
                  } else if (var17[0].contains("rev")) {
                     var9 = var16;
                  }
               }
            }

            var5 = "unknown";
         } else {
            String[] var12 = var11.trim().split("=", 2);
            if (var12.length == 2) {
               String var13 = var12[0].trim();
               if (var13.equals("vendor")) {
                  var6 = ParseUtil.getSingleQuoteStringValue(var11) + (var6.equals("unknown") ? "" : " (" + var6 + ")");
               } else if (var13.equals("device")) {
                  var5 = ParseUtil.getSingleQuoteStringValue(var11);
               }
            }
         }
      }

      if ("0x03".equals(var8)) {
         var3.add(
            new FreeBsdGraphicsCard(
               var5.isEmpty() ? "unknown" : var5, var7.isEmpty() ? "unknown" : var7, var6.isEmpty() ? "unknown" : var6, var9.isEmpty() ? "unknown" : var9, 0L
            )
         );
      }

      return var3;
   }
}
