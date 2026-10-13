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

   OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2);

   OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2, boolean var3);

   List<OSProcess> getProcesses(Collection<Integer> var1);

   List<OSProcess> getProcesses(Collection<Integer> var1, boolean var2);

   OSProcess getProcess(int var1);

   OSProcess getProcess(int var1, boolean var2);

   OSProcess[] getChildProcesses(int var1, int var2, OperatingSystem.ProcessSort var3);

   long getProcessAffinityMask(int var1);

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
      private static final long a = 77598499163485L;
      private static final long g = -3017974897860149207L;

      public OSVersionInfo(String var1, String var2, String var3) {
         this.version = var1;
         this.codeName = var2;
         this.buildNumber = var3;
         StringBuilder var7 = new StringBuilder(this.getVersion() != null ? this.getVersion() : "unknown");
         if (!Util.isBlank(this.getCodeName())) {
            var7.append(" (").append(this.getCodeName()).append((char)((int)g));
         }

         if (!Util.isBlank(this.getBuildNumber())) {
            var7.append(" build ").append(this.getBuildNumber());
         }

         this.versionStr = var7.toString();
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
