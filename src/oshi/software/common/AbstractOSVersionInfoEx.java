package oshi.software.common;

import oshi.software.os.OperatingSystemVersion;

@Deprecated
public class AbstractOSVersionInfoEx implements OperatingSystemVersion {
   public String version;
   public String codeName;
   public String versionStr;
   public String buildNumber;
   private static final long a = 108074897286372L;
   private static final long j = -8218758274124611543L;

   @Override
   public String getVersion() {
      return this.version;
   }

   @Override
   public void n(String var1) {
      this.version = var1;
   }

   @Override
   public String getCodeName() {
      return this.codeName;
   }

   @Override
   public void I(String var1) {
      this.codeName = var1;
   }

   @Override
   public String getBuildNumber() {
      return this.buildNumber;
   }

   @Override
   public void C(String var1) {
      this.buildNumber = var1;
   }

   @Override
   public String toString() {
      if (this.versionStr == null) {
         StringBuilder var4 = new StringBuilder(this.getVersion() != null ? this.getVersion() : "Unknown");
         if (this.getCodeName().length() > 0) {
            var4.append(" (").append(this.getCodeName()).append((char)((int)j));
         }

         if (this.getBuildNumber().length() > 0) {
            var4.append(" build ").append(this.getBuildNumber());
         }

         this.versionStr = var4.toString();
      }

      return this.versionStr;
   }
}
