package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@Immutable
public final class LinuxGraphicsCard extends AbstractGraphicsCard {
   private static final long b = 116089374216401L;

   public LinuxGraphicsCard(String var1, String var2, String var3, String var4, long var5) {
      super(var1, var2, var3, var4, var5);
   }

   public static List<LinuxGraphicsCard> getGraphicsCards() {
      List var3 = getGraphicsCardsFromLspci();
      if (var3.isEmpty()) {
         var3 = getGraphicsCardsFromLshw();
      }

      return var3;
   }

   private static List<LinuxGraphicsCard> getGraphicsCardsFromLspci() {
      ArrayList var3 = new ArrayList();
      List var4 = ExecutingCommand.runNative("lspci -vnnm");
      String var5 = "unknown";
      String var6 = "unknown";
      String var7 = "unknown";
      ArrayList var8 = new ArrayList();
      boolean var9 = false;
      String var10 = null;

      for (Object var12$raw : var4) {
         String var12 = (String)var12$raw;
         String[] var13 = var12.trim().split(":", 2);
         String var14 = var13[0];
         if (var14.equals("Class") && var12.contains("VGA")) {
            var9 = true;
         } else if (var14.equals("Device") && !var9 && var13.length > 1) {
            var10 = var13[1].trim();
         }

         if (var9) {
            if (var13.length < 2) {
               var3.add(new LinuxGraphicsCard(var5, var6, var7, var8.isEmpty() ? "unknown" : String.join(", ", var8), queryLspciMemorySize(var10)));
               var8.clear();
               var9 = false;
            } else if (var14.equals("Device")) {
               Pair var15 = ParseUtil.parseLspciMachineReadable(var13[1].trim());
               if (var15 != null) {
                  var5 = (String)var15.getA();
                  var6 = "0x" + (String)var15.getB();
               }
            } else if (var14.equals("Vendor")) {
               Pair var16 = ParseUtil.parseLspciMachineReadable(var13[1].trim());
               if (var16 != null) {
                  var7 = (String)var16.getA() + " (0x" + (String)var16.getB() + ")";
               } else {
                  var7 = var13[1].trim();
               }
            } else if (var14.equals("Rev:")) {
               var8.add(var12.trim());
            }
         }
      }

      if (var9) {
         var3.add(new LinuxGraphicsCard(var5, var6, var7, var8.isEmpty() ? "unknown" : String.join(", ", var8), queryLspciMemorySize(var10)));
      }

      return var3;
   }

   private static long queryLspciMemorySize(String var0) {
      long var4 = 0L;

      for (String var8 : ExecutingCommand.runNative("lspci -v -s " + var0)) {
         if (var8.contains(" prefetchable")) {
            var4 += ParseUtil.parseLspciMemorySize(var8);
         }
      }

      return var4;
   }

   private static List<LinuxGraphicsCard> getGraphicsCardsFromLshw() {
      ArrayList var3 = new ArrayList();
      List var4 = ExecutingCommand.runNative("lshw -C display");
      String var5 = "unknown";
      String var6 = "unknown";
      String var7 = "unknown";
      ArrayList var8 = new ArrayList();
      long var9 = 0L;
      int var11 = 0;

      for (Object var13$raw : var4) {
         String var13 = (String)var13$raw;
         String[] var14 = var13.trim().split(":");
         if (var14[0].startsWith("*-display")) {
            if (var11++ > 0) {
               var3.add(new LinuxGraphicsCard(var5, var6, var7, var8.isEmpty() ? "unknown" : String.join(", ", var8), var9));
               var8.clear();
            }
         } else if (var14.length == 2) {
            String var15 = var14[0];
            if (var15.equals("product")) {
               var5 = var14[1].trim();
            } else if (var15.equals("vendor")) {
               var7 = var14[1].trim();
            } else if (var15.equals("version")) {
               var8.add(var13.trim());
            } else if (var15.startsWith("resources")) {
               var9 = ParseUtil.parseLshwResourceString(var14[1].trim());
            }
         }
      }

      var3.add(new LinuxGraphicsCard(var5, var6, var7, var8.isEmpty() ? "unknown" : String.join(", ", var8), var9));
      return var3;
   }
}
