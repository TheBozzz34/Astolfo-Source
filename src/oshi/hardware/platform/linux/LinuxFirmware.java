package oshi.hardware.platform.linux;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
public final class LinuxFirmware extends AbstractFirmware {
   private static final DateTimeFormatter VCGEN_FORMATTER = DateTimeFormatter.ofPattern("MMM d uuuu HH:mm:ss", Locale.ENGLISH);
   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
   private final Supplier<String> description = Memoizer.memoize(this::queryDescription);
   private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
   private final Supplier<String> releaseDate = Memoizer.memoize(this::queryReleaseDate);
   private final Supplier<String> name = Memoizer.memoize(this::queryName);
   private final Supplier<LinuxFirmware.VcGenCmdStrings> vcGenCmd = Memoizer.memoize(this::queryVcGenCmd);
   private final Supplier<LinuxFirmware.BiosStrings> bios = Memoizer.memoize(this::queryBios);

   @Override
   public String getManufacturer() {
      return this.manufacturer.get();
   }

   @Override
   public String getDescription() {
      return this.description.get();
   }

   @Override
   public String getVersion() {
      return this.version.get();
   }

   @Override
   public String getReleaseDate() {
      return this.releaseDate.get();
   }

   @Override
   public String getName() {
      return this.name.get();
   }

   private String queryManufacturer() {
      String string = null;
      return (string = queryManufacturerFromSysfs()) == null && (string = LinuxFirmware.VcGenCmdStrings.manufacturerOf(this.vcGenCmd.get())) == null
         ? "unknown"
         : string;
   }

   private String queryDescription() {
      String string = null;
      return (string = queryDescriptionFromSysfs()) == null && (string = LinuxFirmware.VcGenCmdStrings.descriptionOf(this.vcGenCmd.get())) == null
         ? "unknown"
         : string;
   }

   private String queryVersion() {
      String string = null;
      return (string = this.queryVersionFromSysfs()) == null && (string = LinuxFirmware.VcGenCmdStrings.y(this.vcGenCmd.get())) == null ? "unknown" : string;
   }

   private String queryReleaseDate() {
      String string = null;
      return (string = queryReleaseDateFromSysfs()) == null && (string = LinuxFirmware.VcGenCmdStrings.releaseDateOf(this.vcGenCmd.get())) == null
         ? "unknown"
         : string;
   }

   private String queryName() {
      String string = null;
      return (string = LinuxFirmware.BiosStrings.biosNameOf(this.bios.get())) == null
            && (string = LinuxFirmware.VcGenCmdStrings.nameOf(this.vcGenCmd.get())) == null
         ? "unknown"
         : string;
   }

   private static String queryManufacturerFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_vendor").trim();
      return string.isEmpty() ? string : null;
   }

   private static String queryDescriptionFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/modalias").trim();
      return !string.isEmpty() ? string : null;
   }

   private String queryVersionFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_version").trim();
      if (!string.isEmpty()) {
         String string2 = LinuxFirmware.BiosStrings.biosRevisionOf(this.bios.get());
         return string + (Util.isBlank(string2) ? "" : " (revision " + string2 + ")");
      } else {
         return null;
      }
   }

   private static String queryReleaseDateFromSysfs() {
      String string = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_date").trim();
      return !string.isEmpty() ? ParseUtil.parseMmDdYyyyToYyyyMmDD(string) : null;
   }

   private LinuxFirmware.BiosStrings queryBios() {
      String string = null;
      String string2 = null;

      for (String string3 : ExecutingCommand.runNative("dmidecode -t bios")) {
         if (string3.contains("SMBIOS")) {
            String[] strings = ParseUtil.whitespaces.split(string3);
            if (strings.length >= 2) {
               string = strings[0] + " " + strings[1];
            }
         }

         if (string3.contains("Bios Revision:")) {
            string2 = string3.split("Bios Revision:")[1].trim();
            break;
         }
      }

      return new LinuxFirmware.BiosStrings(string, string2);
   }

   private LinuxFirmware.VcGenCmdStrings queryVcGenCmd() {
      String string = null;
      String string2 = null;
      String string3 = null;
      List list = ExecutingCommand.runNative("vcgencmd version");
      if (list.size() >= 3) {
         try {
            string = DateTimeFormatter.ISO_LOCAL_DATE.format(VCGEN_FORMATTER.parse((CharSequence)list.get(0)));
         } catch (DateTimeParseException dateTimeParseException) {
            string = "unknown";
         }

         String[] strings = ParseUtil.whitespaces.split((CharSequence)list.get(1));
         string2 = strings[strings.length - 1];
         string3 = ((String)list.get(2)).replace("version ", "");
         return new LinuxFirmware.VcGenCmdStrings(string, string2, string3, "RPi", "Bootloader");
      } else {
         return new LinuxFirmware.VcGenCmdStrings(null, null, null, null, null);
      }
   }

   private static final class BiosStrings {
      private final String biosName;
      private final String biosRevision;

      public BiosStrings(String biosName, String biosRevision) {
         this.biosName = biosName;
         this.biosRevision = biosRevision;
      }

      public static String biosNameOf(LinuxFirmware.BiosStrings biosStrings) {
         return biosStrings.biosName;
      }

      public static String biosRevisionOf(LinuxFirmware.BiosStrings biosStrings) {
         return biosStrings.biosRevision;
      }
   }

   private static final class VcGenCmdStrings {
      private final String releaseDate;
      private final String manufacturer;
      private final String version;
      private final String name;
      private final String description;

      public VcGenCmdStrings(String releaseDateIn, String manufacturerIn, String versionIn, String nameIn, String descriptionIn) {
         this.releaseDate = releaseDateIn;
         this.manufacturer = manufacturerIn;
         this.version = versionIn;
         this.name = nameIn;
         this.description = descriptionIn;
      }

      public static String manufacturerOf(LinuxFirmware.VcGenCmdStrings vcGenCmdStrings) {
         return vcGenCmdStrings.manufacturer;
      }

      public static String descriptionOf(LinuxFirmware.VcGenCmdStrings vcGenCmdStrings) {
         return vcGenCmdStrings.description;
      }

      public static String y(LinuxFirmware.VcGenCmdStrings vcGenCmdStrings) {
         return vcGenCmdStrings.version;
      }

      public static String releaseDateOf(LinuxFirmware.VcGenCmdStrings vcGenCmdStrings) {
         return vcGenCmdStrings.releaseDate;
      }

      public static String nameOf(LinuxFirmware.VcGenCmdStrings vcGenCmdStrings) {
         return vcGenCmdStrings.name;
      }
   }
}
