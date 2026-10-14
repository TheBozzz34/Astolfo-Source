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
   public LinuxGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<LinuxGraphicsCard> getGraphicsCards() {
      List list = getGraphicsCardsFromLspci();
      if (list.isEmpty()) {
         list = getGraphicsCardsFromLshw();
      }

      return list;
   }

   private static List<LinuxGraphicsCard> getGraphicsCardsFromLspci() {
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("lspci -vnnm");
      String string = "unknown";
      String string2 = "unknown";
      String string3 = "unknown";
      ArrayList arrayList2 = new ArrayList();
      boolean bl = false;
      String string4 = null;

      for (Object object : list) {
         String string5 = (String)object;
         String[] strings = string5.trim().split(":", 2);
         String string6 = strings[0];
         if (string6.equals("Class") && string5.contains("VGA")) {
            bl = true;
         } else if (string6.equals("Device") && !bl && strings.length > 1) {
            string4 = strings[1].trim();
         }

         if (bl) {
            if (strings.length < 2) {
               arrayList.add(new LinuxGraphicsCard(string, string2, string3, arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2), queryLspciMemorySize(string4)));
               arrayList2.clear();
               bl = false;
            } else if (string6.equals("Device")) {
               Pair pair = ParseUtil.parseLspciMachineReadable(strings[1].trim());
               if (pair != null) {
                  string = (String)pair.getA();
                  string2 = "0x" + (String)pair.getB();
               }
            } else if (string6.equals("Vendor")) {
               Pair pair2 = ParseUtil.parseLspciMachineReadable(strings[1].trim());
               if (pair2 != null) {
                  string3 = (String)pair2.getA() + " (0x" + (String)pair2.getB() + ")";
               } else {
                  string3 = strings[1].trim();
               }
            } else if (string6.equals("Rev:")) {
               arrayList2.add(string5.trim());
            }
         }
      }

      if (bl) {
         arrayList.add(new LinuxGraphicsCard(string, string2, string3, arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2), queryLspciMemorySize(string4)));
      }

      return arrayList;
   }

   private static long queryLspciMemorySize(String lookupDevice) {
      long l = 0L;

      for (String string : ExecutingCommand.runNative("lspci -v -s " + lookupDevice)) {
         if (string.contains(" prefetchable")) {
            l += ParseUtil.parseLspciMemorySize(string);
         }
      }

      return l;
   }

   private static List<LinuxGraphicsCard> getGraphicsCardsFromLshw() {
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("lshw -C display");
      String string = "unknown";
      String string2 = "unknown";
      String string3 = "unknown";
      ArrayList arrayList2 = new ArrayList();
      long l = 0L;
      int i = 0;

      for (Object object : list) {
         String string4 = (String)object;
         String[] strings = string4.trim().split(":");
         if (strings[0].startsWith("*-display")) {
            if (i++ > 0) {
               arrayList.add(new LinuxGraphicsCard(string, string2, string3, arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2), l));
               arrayList2.clear();
            }
         } else if (strings.length == 2) {
            String string5 = strings[0];
            if (string5.equals("product")) {
               string = strings[1].trim();
            } else if (string5.equals("vendor")) {
               string3 = strings[1].trim();
            } else if (string5.equals("version")) {
               arrayList2.add(string4.trim());
            } else if (string5.startsWith("resources")) {
               l = ParseUtil.parseLshwResourceString(strings[1].trim());
            }
         }
      }

      arrayList.add(new LinuxGraphicsCard(string, string2, string3, arrayList2.isEmpty() ? "unknown" : String.join(", ", arrayList2), l));
      return arrayList;
   }
}
