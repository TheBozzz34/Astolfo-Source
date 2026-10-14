package oshi.util.platform.linux;

import java.io.File;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public final class ProcPath {
   public static final String PROC = queryProcConfig();
   public static final String ASOUND = PROC + "/asound/";
   public static final String CPUINFO = PROC + "/cpuinfo";
   public static final String DISKSTATS = PROC + "/diskstats";
   public static final String MEMINFO = PROC + "/meminfo";
   public static final String MOUNTS = PROC + "/mounts";
   public static final String PID_CMDLINE = PROC + "/%d/cmdline";
   public static final String PID_CWD = PROC + "/%d/cwd";
   public static final String PID_EXE = PROC + "/%d/exe";
   public static final String PID_FD = PROC + "/%d/fd";
   public static final String PID_IO = PROC + "/%d/io";
   public static final String PID_STAT = PROC + "/%d/stat";
   public static final String PID_STATUS = PROC + "/%d/status";
   public static final String SELF_STAT = PROC + "/self/stat";
   public static final String STAT = PROC + "/stat";
   public static final String SYS_FS_FILE_NR = PROC + "/sys/fs/file-nr";
   public static final String UPTIME = PROC + "/uptime";
   public static final String VERSION = PROC + "/version";
   public static final String VMSTAT = PROC + "/vmstat";

   private ProcPath() {
   }

   private static String queryProcConfig() {
      String string = GlobalConfig.get("", "/proc");
      string = '/' + string.replaceAll("/$|^/", "");
      if (!new File(string).exists()) {
         throw new GlobalConfig.PropertyException("", "The path does not exist");
      } else {
         return string;
      }
   }
}
