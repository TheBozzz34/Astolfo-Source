package oshi.hardware.platform.unix.solaris;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
public final class SolarisComputerSystem extends AbstractComputerSystem {
   private final Supplier<SolarisComputerSystem.SmbiosStrings> smbiosStrings = Memoizer.memoize(SolarisComputerSystem::readSmbios);

   @Override
   public String getManufacturer() {
      return SolarisComputerSystem.SmbiosStrings.manufacturerOf(this.smbiosStrings.get());
   }

   @Override
   public String getModel() {
      return SolarisComputerSystem.SmbiosStrings.modelOf(this.smbiosStrings.get());
   }

   @Override
   public String getSerialNumber() {
      return SolarisComputerSystem.SmbiosStrings.serialNumberOf(this.smbiosStrings.get());
   }

   @Override
   public Firmware createFirmware() {
      return new SolarisFirmware(
         SolarisComputerSystem.SmbiosStrings.biosVendorOf(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.biosVersionOf(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.biosDateOf(this.smbiosStrings.get())
      );
   }

   @Override
   public Baseboard createBaseboard() {
      return new SolarisBaseboard(
         SolarisComputerSystem.SmbiosStrings.boardManufacturerOf(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.boardModelOf(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.boardSerialNumberOf(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.boardVersionOf(this.smbiosStrings.get())
      );
   }

   private static SolarisComputerSystem.SmbiosStrings readSmbios() {
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;
      String string5 = null;
      String string6 = null;
      String string7 = null;
      String string8 = null;
      String string9 = null;
      String string10 = null;
      int i = -1;

      for (String string11 : ExecutingCommand.runNative("smbios")) {
         if (string11.contains("SMB_TYPE_") && (i = getSmbType(string11)) == Integer.MAX_VALUE) {
            break;
         }

         switch (i) {
            case 0:
               if (string11.contains("Vendor:")) {
                  string = string11.split("Vendor:")[1].trim();
               } else if (string11.contains("VersionString:")) {
                  string2 = string11.split("VersionString:")[1].trim();
               } else if (string11.contains("Release Date:")) {
                  string3 = string11.split("Release Date:")[1].trim();
               }
               break;
            case 1:
               if (string11.contains("Manufacturer:")) {
                  string4 = string11.split("Manufacturer:")[1].trim();
               } else if (string11.contains("Product:")) {
                  string5 = string11.split("Product:")[1].trim();
               } else if (string11.contains("Serial Number:")) {
                  string6 = string11.split("Serial Number:")[1].trim();
               }
               break;
            case 2:
               if (string11.contains("Manufacturer:")) {
                  string7 = string11.split("Manufacturer:")[1].trim();
               } else if (string11.contains("Product:")) {
                  string8 = string11.split("Product:")[1].trim();
               } else if (string11.contains("Version:")) {
                  string9 = string11.split("Version:")[1].trim();
               } else if (string11.contains("Serial Number:")) {
                  string10 = string11.split("Serial Number:")[1].trim();
               }
         }
      }

      if (Util.isBlank(string6)) {
         string6 = readSerialNumber();
      }

      return new SolarisComputerSystem.SmbiosStrings(string, string2, string3, string4, string5, string6, string7, string8, string9, string10);
   }

   private static int getSmbType(String checkLine) {
      if (checkLine.contains("SMB_TYPE_BIOS")) {
         return 0;
      } else if (checkLine.contains("SMB_TYPE_SYSTEM")) {
         return 1;
      } else {
         return checkLine.contains("SMB_TYPE_BASEBOARD") ? 2 : Integer.MAX_VALUE;
      }
   }

   private static String readSerialNumber() {
      String string = ExecutingCommand.getFirstAnswer("sneep");
      if (string.isEmpty()) {
         String string2 = "chassis-sn:";

         for (String string3 : ExecutingCommand.runNative("prtconf -pv")) {
            if (string3.contains(string2)) {
               string = ParseUtil.getSingleQuoteStringValue(string3);
               break;
            }
         }
      }

      return string;
   }

   private static final class SmbiosStrings {
      private final String biosVendor;
      private final String biosVersion;
      private final String biosDate;
      private final String manufacturer;
      private final String model;
      private final String serialNumber;
      private final String boardManufacturer;
      private final String boardModel;
      private final String boardVersion;
      private final String boardSerialNumber;

      public SmbiosStrings(
         String biosVendor,
         String biosVersion,
         String biosDate,
         String manufacturer,
         String model,
         String serialNumber,
         String boardManufacturer,
         String boardModel,
         String boardVersion,
         String boardSerialNumber
      ) {
         this.biosVendor = Util.isBlank(biosVendor) ? "unknown" : biosVendor;
         this.biosVersion = Util.isBlank(biosVersion) ? "unknown" : biosVersion;
         this.biosDate = Util.isBlank(biosDate) ? "unknown" : biosDate;
         this.manufacturer = Util.isBlank(manufacturer) ? "unknown" : manufacturer;
         this.model = Util.isBlank(model) ? "unknown" : model;
         this.serialNumber = Util.isBlank(serialNumber) ? "unknown" : serialNumber;
         this.boardManufacturer = Util.isBlank(boardManufacturer) ? "unknown" : boardManufacturer;
         this.boardModel = Util.isBlank(boardModel) ? "unknown" : boardModel;
         this.boardVersion = Util.isBlank(boardVersion) ? "unknown" : boardVersion;
         this.boardSerialNumber = Util.isBlank(boardSerialNumber) ? "unknown" : boardSerialNumber;
      }

      public static String manufacturerOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.manufacturer;
      }

      public static String modelOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.model;
      }

      public static String serialNumberOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.serialNumber;
      }

      public static String biosVendorOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.biosVendor;
      }

      public static String biosVersionOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.biosVersion;
      }

      public static String biosDateOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.biosDate;
      }

      public static String boardManufacturerOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.boardManufacturer;
      }

      public static String boardModelOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.boardModel;
      }

      public static String boardSerialNumberOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.boardSerialNumber;
      }

      public static String boardVersionOf(SolarisComputerSystem.SmbiosStrings smbiosStrings2) {
         return smbiosStrings2.boardVersion;
      }
   }
}
