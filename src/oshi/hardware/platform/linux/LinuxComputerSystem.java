package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@Immutable
public final class LinuxComputerSystem extends AbstractComputerSystem {
   private final Supplier<String> manufacturer = Memoizer.memoize(LinuxComputerSystem::y);
   private final Supplier<String> model = Memoizer.memoize(LinuxComputerSystem::h);
   private final Supplier<String> serialNumber = Memoizer.memoize(LinuxComputerSystem::N);

   @Override
   public String V() {
      return this.manufacturer.get();
   }

   @Override
   public String z() {
      return this.model.get();
   }

   @Override
   public String k() {
      return this.serialNumber.get();
   }

   @Override
   public Firmware createFirmware() {
      return new LinuxFirmware();
   }

   @Override
   public Baseboard createBaseboard() {
      return new LinuxBaseboard();
   }

   private static String y() {
      String string = null;
      return (string = queryManufacturerFromSysfs()) == null && (string = queryManufacturerFromProcCpu()) == null ? "unknown" : string;
   }

   private static String h() {
      String string = null;
      return (string = queryModelFromSysfs()) == null && (string = queryModelFromDeviceTree()) == null && (string = queryModelFromLshw()) == null ? "unknown" : string;
   }

   private static String N() {
      String string = null;
      return (string = querySerialFromSysfs()) == null
            && (string = querySerialFromDmiDecode()) == null
            && (string = querySerialFromLshal()) == null
            && (string = querySerialFromLshw()) == null
         ? "unknown"
         : string;
   }

   private static String queryManufacturerFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/sys_vendor").trim();
      return !string.isEmpty() ? string : null;
   }

   private static String queryManufacturerFromProcCpu() {
      for (String string : FileUtil.readFile(ProcPath.CPUINFO)) {
         if (string.startsWith("CPU implementer")) {
            int i = ParseUtil.parseLastInt(string, 0);
            switch (i) {
               case 65:
                  return "ARM";
               case 66:
                  return "Broadcom";
               case 67:
                  return "Cavium";
               case 68:
                  return "DEC";
               case 69:
               case 70:
               case 71:
               case 72:
               case 73:
               case 74:
               case 75:
               case 76:
               case 77:
               case 79:
               case 82:
               case 84:
               case 85:
               case 87:
               case 88:
               case 89:
               case 90:
               case 91:
               case 92:
               case 93:
               case 94:
               case 95:
               case 96:
               case 97:
               case 98:
               case 99:
               case 100:
               case 101:
               case 103:
               case 104:
               default:
                  return null;
               case 78:
                  return "Nvidia";
               case 80:
                  return "APM";
               case 81:
                  return "Qualcomm";
               case 83:
                  return "Samsung";
               case 86:
                  return "Marvell";
               case 102:
                  return "Faraday";
               case 105:
                  return "Intel";
            }
         }
      }

      return null;
   }

   private static String queryModelFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_name").trim();
      String string2 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_version").trim();
      if (string.isEmpty()) {
         return !string2.isEmpty() ? string2 : null;
      } else {
         return !string2.isEmpty() && !"None".equals(string2) ? string + " (version: " + string2 + ")" : string;
      }
   }

   private static String queryModelFromDeviceTree() {
      String string = FileUtil.getStringFromFile("/sys/firmware/devicetree/base/model");
      return !string.isEmpty() ? string.replace("Machine: ", "") : null;
   }

   private static String queryModelFromLshw() {
      String string = "product:";

      for (String string2 : ExecutingCommand.runNative("lshw -C system")) {
         if (string2.contains(string)) {
            return string2.split(string)[1].trim();
         }
      }

      return null;
   }

   private static String querySerialFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_serial");
      if (!string.isEmpty() && !"None".equals(string)) {
         return null;
      }

      string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial");
      return !string.isEmpty() && !"None".equals(string) ? string : null;
   }

   private static String querySerialFromDmiDecode() {
      String string = "Serial Number:";

      for (String string2 : ExecutingCommand.runNative("dmidecode -t system")) {
         if (string2.contains(string)) {
            return string2.split(string)[1].trim();
         }
      }

      return null;
   }

   private static String querySerialFromLshal() {
      String string = "system.hardware.serial =";

      for (String string2 : ExecutingCommand.runNative("lshal")) {
         if (string2.contains(string)) {
            return ParseUtil.getSingleQuoteStringValue(string2);
         }
      }

      return null;
   }

   private static String querySerialFromLshw() {
      String string = "serial:";

      for (String string2 : ExecutingCommand.runNative("lshw -C system")) {
         if (string2.contains(string)) {
            return string2.split(string)[1].trim();
         }
      }

      return null;
   }
}
