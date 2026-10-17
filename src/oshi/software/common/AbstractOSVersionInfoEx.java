package oshi.software.common;

import oshi.software.os.OperatingSystemVersion;

@Deprecated
public class AbstractOSVersionInfoEx implements OperatingSystemVersion {
   public String version;
   public String codeName;
   public String versionStr;
   public String buildNumber;

   @Override
   public String getVersion() {
      return this.version;
   }

   @Override
   public void setVersion(String version) {
      this.version = version;
   }

   @Override
   public String getCodeName() {
      return this.codeName;
   }

   @Override
   public void setCodeName(String codeName) {
      this.codeName = codeName;
   }

   @Override
   public String getBuildNumber() {
      return this.buildNumber;
   }

   @Override
   public void setBuildNumber(String buildNumber) {
      this.buildNumber = buildNumber;
   }

   @Override
   public String toString() {
      if (this.versionStr == null) {
         StringBuilder stringBuilder = new StringBuilder(this.getVersion() != null ? this.getVersion() : "Unknown");
         if (this.getCodeName().length() > 0) {
            stringBuilder.append(" (").append(this.getCodeName()).append(')');
         }

         if (this.getBuildNumber().length() > 0) {
            stringBuilder.append(" build ").append(this.getBuildNumber());
         }

         this.versionStr = stringBuilder.toString();
      }

      return this.versionStr;
   }
}
