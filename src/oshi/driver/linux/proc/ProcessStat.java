package oshi.driver.linux.proc;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ProcessStat {
   private static final Pattern DIGITS = Pattern.compile("\\d+");

   private ProcessStat() {
   }

   public static Triplet<String, Character, Map<ProcessStat.PidStat, Long>> getPidStats(int pid) {
      String string = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, pid));
      if (string.isEmpty()) {
         return null;
      }

      int i = string.indexOf(40) + 1;
      int j = string.indexOf(41);
      String string2 = string.substring(i, j);
      Character character = string.charAt(j + 2);
      String[] strings = ParseUtil.whitespaces.split(string.substring(j + 4).trim());
      EnumMap enumMap = new EnumMap<>(ProcessStat.PidStat.class);
      ProcessStat.PidStat[] pidStats = ProcessStat.PidStat.class.getEnumConstants();

      for (int k = 3; k < pidStats.length && k - 3 < strings.length; k++) {
         enumMap.put(pidStats[k], ParseUtil.parseLongOrDefault(strings[k - 3], 0L));
      }

      return new Triplet<>(string2, character, enumMap);
   }

   public static File[] getPidFiles() {
      File file = new File(ProcPath.PROC);
      File[] files = file.listFiles(f -> DIGITS.matcher(f.getName()).matches());
      return files != null ? files : new File[0];
   }

   public enum PidStat {
      PID,
      COMM,
      STATE,
      PPID,
      PGRP,
      SESSION,
      TTY_NR,
      PTGID,
      FLAGS,
      MINFLT,
      CMINFLT,
      MAJFLT,
      CMAJFLT,
      UTIME,
      STIME,
      CUTIME,
      CSTIME,
      PRIORITY,
      NICE,
      NUM_THREADS,
      ITREALVALUE,
      STARTTIME,
      VSIZE,
      RSS,
      RSSLIM,
      STARTCODE,
      ENDCODE,
      STARTSTACK,
      KSTKESP,
      KSTKEIP,
      SIGNAL,
      BLOCKED,
      SIGIGNORE,
      SIGCATCH,
      WCHAN,
      NSWAP,
      CNSWAP,
      EXIT_SIGNAL,
      PROCESSOR,
      RT_PRIORITY,
      POLICY,
      DELAYACCT_BLKIO_TICKS,
      GUEST_TIME,
      CGUEST_TIME,
      START_DATA,
      END_DATA,
      START_BRK,
      ARG_START,
      ARG_END,
      ENV_START,
      ENV_END,
      EXIT_CODE;

      private static final ProcessStat.PidStat[] VALUES = new ProcessStat.PidStat[]{
         PID,
         COMM,
         STATE,
         PPID,
         PGRP,
         SESSION,
         TTY_NR,
         PTGID,
         FLAGS,
         MINFLT,
         CMINFLT,
         MAJFLT,
         CMAJFLT,
         UTIME,
         STIME,
         CUTIME,
         CSTIME,
         PRIORITY,
         NICE,
         NUM_THREADS,
         ITREALVALUE,
         STARTTIME,
         VSIZE,
         RSS,
         RSSLIM,
         STARTCODE,
         ENDCODE,
         STARTSTACK,
         KSTKESP,
         KSTKEIP,
         SIGNAL,
         BLOCKED,
         SIGIGNORE,
         SIGCATCH,
         WCHAN,
         NSWAP,
         CNSWAP,
         EXIT_SIGNAL,
         PROCESSOR,
         RT_PRIORITY,
         POLICY,
         DELAYACCT_BLKIO_TICKS,
         GUEST_TIME,
         CGUEST_TIME,
         START_DATA,
         END_DATA,
         START_BRK,
         ARG_START,
         ARG_END,
         ENV_START,
         ENV_END,
         EXIT_CODE
      };
   }
}
