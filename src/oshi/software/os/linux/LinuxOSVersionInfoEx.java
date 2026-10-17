package oshi.software.os.linux;

import java.io.File;
import java.util.List;
import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@Deprecated
public class LinuxOSVersionInfoEx extends AbstractOSVersionInfoEx {
   public LinuxOSVersionInfoEx() {
      this(null, null);
   }

   public LinuxOSVersionInfoEx(String versionId, String codeName) {
      this.setVersion(versionId);
      this.setCodeName(codeName);
      if (this.getVersion() == null) {
         this.setVersionFromReleaseFiles();
      }

      if (this.getCodeName() == null) {
         this.setCodeName("");
      }

      List list = null;
      list = FileUtil.readFile(ProcPath.VERSION);
      if (!list.isEmpty()) {
         String[] strings = ParseUtil.whitespaces.split((CharSequence)list.get(0));

         for (String string : strings) {
            if (!"Linux".equals(string) && !"version".equals(string)) {
               this.setBuildNumber(string);
               return;
            }
         }
      }

      this.setBuildNumber("");
   }

   private void setVersionFromReleaseFiles() {
      if (!this.readDistribRelease("/etc/system-release")) {
         if (!this.readOsRelease()) {
            if (!this.execLsbRelease()) {
               if (!this.readLsbRelease()) {
                  String string = LinuxOperatingSystem.getReleaseFilename();
                  if (!this.readDistribRelease(string)) {
                     if (this.getVersion() == null) {
                        this.setVersion(System.getProperty("os.version"));
                     }
                  }
               }
            }
         }
      }
   }

   private boolean readOsRelease() {
      if (new File("/etc/os-release").exists()) {
         for (String string : FileUtil.readFile("/etc/os-release")) {
            if (string.startsWith("VERSION=")) {
               string = ParseUtil.getDoubleQuoteStringValue(string).trim();
               String[] strings = string.split("[()]");
               if (strings.length <= 1) {
                  strings = string.split(", ");
               }

               if (strings.length > 0) {
                  this.version = strings[0].trim();
               }

               if (strings.length > 1) {
                  this.codeName = strings[1].trim();
               }
            } else if (string.startsWith("VERSION_ID=") && this.version == null) {
               this.version = ParseUtil.getDoubleQuoteStringValue(string).trim();
            }
         }
      }

      return this.version != null;
   }

   private boolean execLsbRelease() {
      for (String string : ExecutingCommand.runNative("lsb_release -a")) {
         if (string.startsWith("Description:")) {
            string = string.replace("Description:", "").trim();
            if (string.contains(" release ")) {
               this.version = this.parseRelease(string, " release ");
            }
         } else if (string.startsWith("Release:") && this.version == null) {
            this.version = string.replace("Release:", "").trim();
         } else if (string.startsWith("Codename:") && this.codeName == null) {
            this.codeName = string.replace("Codename:", "").trim();
         }
      }

      return this.version != null;
   }

   private boolean readLsbRelease() {
      if (new File("/etc/lsb-release").exists()) {
         for (String string : FileUtil.readFile("/etc/lsb-release")) {
            if (string.startsWith("DISTRIB_DESCRIPTION=")) {
               string = ParseUtil.getDoubleQuoteStringValue(string).trim();
               if (string.contains(" release ")) {
                  this.version = this.parseRelease(string, " release ");
               }
            } else if (string.startsWith("DISTRIB_RELEASE=") && this.version == null) {
               this.version = ParseUtil.getDoubleQuoteStringValue(string).trim();
            } else if (string.startsWith("DISTRIB_CODENAME=") && this.codeName == null) {
               this.codeName = ParseUtil.getDoubleQuoteStringValue(string).trim();
            }
         }
      }

      return this.version != null;
   }

   private boolean readDistribRelease(String filename) {
      if (new File(filename).exists()) {
         for (String string : FileUtil.readFile(filename)) {
            if (string.contains(" release ")) {
               this.version = this.parseRelease(string, " release ");
               break;
            }

            if (string.contains(" VERSION ")) {
               this.version = this.parseRelease(string, " VERSION ");
               break;
            }
         }
      }

      return this.version != null;
   }

   private String parseRelease(String line, String splitLine) {
      String[] strings = line.split(splitLine);
      if (strings.length > 1) {
         strings = strings[1].split("[()]");
         if (strings.length > 0) {
            this.version = strings[0].trim();
         }

         if (strings.length > 1) {
            this.codeName = strings[1].trim();
         }
      }

      return this.version;
   }
}
