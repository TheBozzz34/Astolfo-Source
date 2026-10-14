package oshi.software.os;

import java.util.Collection;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Util;

@ThreadSafe
public interface OperatingSystem {
   String getFamily();

   String getManufacturer();

   @Deprecated
   OperatingSystemVersion getVersion();

   OperatingSystem.OSVersionInfo getVersionInfo();

   FileSystem getFileSystem();

   OSProcess[] getProcesses();

   OSProcess[] getProcesses(int i, OperatingSystem.ProcessSort processSort);

   OSProcess[] getProcesses(int i, OperatingSystem.ProcessSort processSort, boolean bl);

   List<OSProcess> getProcesses(Collection<Integer> collection);

   List<OSProcess> getProcesses(Collection<Integer> collection, boolean bl);

   OSProcess getProcess(int i);

   OSProcess getProcess(int i, boolean bl);

   OSProcess[] getChildProcesses(int i, int j, OperatingSystem.ProcessSort processSort);

   long getProcessAffinityMask(int i);

   int getProcessId();

   int getProcessCount();

   int getThreadCount();

   int getBitness();

   long getSystemUptime();

   long getSystemBootTime();

   boolean isElevated();

   NetworkParams getNetworkParams();

   OSService[] getServices();

   class OSVersionInfo {
      private final String version;
      private final String codeName;
      private final String buildNumber;
      private final String versionStr;

      public OSVersionInfo(String version, String codeName, String buildNumber) {
         this.version = version;
         this.codeName = codeName;
         this.buildNumber = buildNumber;
         StringBuilder stringBuilder = new StringBuilder(this.getVersion() != null ? this.getVersion() : "unknown");
         if (!Util.isBlank(this.getCodeName())) {
            stringBuilder.append(" (").append(this.getCodeName()).append(')');
         }

         if (!Util.isBlank(this.getBuildNumber())) {
            stringBuilder.append(" build ").append(this.getBuildNumber());
         }

         this.versionStr = stringBuilder.toString();
      }

      public String getVersion() {
         return this.version;
      }

      public String getCodeName() {
         return this.codeName;
      }

      public String getBuildNumber() {
         return this.buildNumber;
      }

      @Override
      public String toString() {
         return this.versionStr;
      }
   }

   enum ProcessSort {
      CPU,
      MEMORY,
      OLDEST,
      NEWEST,
      PID,
      PARENTPID,
      NAME;
   }
}
