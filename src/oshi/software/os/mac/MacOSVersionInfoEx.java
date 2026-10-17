package oshi.software.os.mac;

import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@Deprecated
public class MacOSVersionInfoEx extends AbstractOSVersionInfoEx {
   private int osxVersionNumber = -1;

   public MacOSVersionInfoEx() {
      this.setVersion(System.getProperty("os.version"));
      this.setCodeName(this.parseCodeName());
      this.setBuildNumber(SysctlUtil.sysctl("kern.osversion", ""));
   }

   public int getOsxVersionNumber() {
      return this.osxVersionNumber;
   }

   private String parseCodeName() {
      if (ParseUtil.getFirstIntValue(this.getVersion()) == 10) {
         this.osxVersionNumber = ParseUtil.getNthIntValue(this.getVersion(), 2);
         switch (this.osxVersionNumber) {
            case 0:
               return "Cheetah";
            case 1:
               return "Puma";
            case 2:
               return "Jaguar";
            case 3:
               return "Panther";
            case 4:
               return "Tiger";
            case 5:
               return "Leopard";
            case 6:
               return "Snow Leopard";
            case 7:
               return "Lion";
            case 8:
               return "Mountain Lion";
            case 9:
               return "Mavericks";
            case 10:
               return "Yosemite";
            case 11:
               return "El Capitan";
            case 12:
               return "Sierra";
            case 13:
               return "High Sierra";
            case 14:
               return "Mojave";
            case 15:
               return "Catalina";
         }
      }

      return "";
   }
}
