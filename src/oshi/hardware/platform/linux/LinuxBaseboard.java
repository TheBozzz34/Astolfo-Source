package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.linux.ProcPath;

@Immutable
public final class LinuxBaseboard extends AbstractBaseboard {
   private final Supplier<String> manufacturer = Memoizer.memoize(LinuxBaseboard::queryManufacturer);
   private final Supplier<String> model = Memoizer.memoize(LinuxBaseboard::queryModel);
   private final Supplier<String> version = Memoizer.memoize(LinuxBaseboard::queryVersion);
   private final Supplier<String> serialNumber = Memoizer.memoize(LinuxBaseboard::querySerialNumber);

   @Override
   public String getManufacturer() {
      return this.manufacturer.get();
   }

   @Override
   public String getModel() {
      return this.model.get();
   }

   @Override
   public String getVersion() {
      return this.version.get();
   }

   @Override
   public String getSerialNumber() {
      return this.serialNumber.get();
   }

   private static String queryManufacturer() {
      String string = null;
      return (string = queryManufacturerFromSysfs()) == null && (string = LinuxBaseboard.ProcCpuStrings.manufacturerOf(queryProcCpu())) == null ? "unknown" : string;
   }

   private static String queryModel() {
      String string = null;
      return (string = queryModelFromSysfs()) == null && (string = LinuxBaseboard.ProcCpuStrings.modelOf(queryProcCpu())) == null ? "unknown" : string;
   }

   private static String queryVersion() {
      String string = null;
      return (string = queryVersionFromSysfs()) == null && (string = LinuxBaseboard.ProcCpuStrings.versionOf(queryProcCpu())) == null ? "unknown" : string;
   }

   private static String querySerialNumber() {
      String string = null;
      return (string = querySerialFromSysfs()) == null && (string = LinuxBaseboard.ProcCpuStrings.serialNumberOf(queryProcCpu())) == null ? "unknown" : string;
   }

   private static String queryManufacturerFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_vendor").trim();
      return !string.isEmpty() ? string : null;
   }

   private static String queryModelFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_name").trim();
      return !string.isEmpty() ? string : null;
   }

   private static String queryVersionFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_version").trim();
      return !string.isEmpty() ? string : null;
   }

   private static String querySerialFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial").trim();
      return !string.isEmpty() ? string : null;
   }

   private static LinuxBaseboard.ProcCpuStrings queryProcCpu() {
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;

      for (String string5 : FileUtil.readFile(ProcPath.CPUINFO)) {
         String[] strings = ParseUtil.whitespacesColonWhitespace.split(string5);
         if (strings.length >= 2) {
            switch (strings[0]) {
               case "Hardware":
                  string2 = strings[1];
                  break;
               case "Revision":
                  string3 = strings[1];
                  if (string3.length() > 1) {
                     string = queryBoardManufacturer(string3.charAt(1));
                  }
                  break;
               case "Serial":
                  string4 = strings[1];
            }
         }
      }

      return new LinuxBaseboard.ProcCpuStrings(string, string2, string3, string4);
   }

   private static String queryBoardManufacturer(char digit) {
      switch (digit) {
         case '0':
            return "Sony UK";
         case '1':
            return "Egoman";
         case '2':
            return "Embest";
         case '3':
            return "Sony Japan";
         case '4':
            return "Embest";
         case '5':
            return "Stadium";
         default:
            return "unknown";
      }
   }

   private static final class ProcCpuStrings {
      private final String manufacturer;
      private final String model;
      private final String version;
      private final String serialNumber;

      public ProcCpuStrings(String manufacturerIn, String modelIn, String versionIn, String serialNumberIn) {
         this.manufacturer = Util.isBlank(manufacturerIn) ? "unknown" : manufacturerIn;
         this.model = Util.isBlank(modelIn) ? "unknown" : modelIn;
         this.version = Util.isBlank(versionIn) ? "unknown" : versionIn;
         this.serialNumber = Util.isBlank(serialNumberIn) ? "unknown" : serialNumberIn;
      }

      public static String manufacturerOf(LinuxBaseboard.ProcCpuStrings procCpuStrings) {
         return procCpuStrings.manufacturer;
      }

      public static String modelOf(LinuxBaseboard.ProcCpuStrings procCpuStrings) {
         return procCpuStrings.model;
      }

      public static String versionOf(LinuxBaseboard.ProcCpuStrings procCpuStrings) {
         return procCpuStrings.version;
      }

      public static String serialNumberOf(LinuxBaseboard.ProcCpuStrings procCpuStrings) {
         return procCpuStrings.serialNumber;
      }
   }
}
