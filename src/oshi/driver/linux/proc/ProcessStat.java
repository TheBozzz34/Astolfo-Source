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
   private static final long a = 86335578721221L;

   private ProcessStat() {
   }

   public static Triplet<String, Character, Map<ProcessStat.PidStat, Long>> getPidStats(int var0) {
      String var4 = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, var0));
      if (var4.isEmpty()) {
         return null;
      }

      int var5 = var4.indexOf(40) + 1;
      int var6 = var4.indexOf(41);
      String var7 = var4.substring(var5, var6);
      Character var8 = var4.charAt(var6 + 2);
      String[] var9 = ParseUtil.Q.split(var4.substring(var6 + 4).trim());
      EnumMap var10 = new EnumMap<>(ProcessStat.PidStat.class);
      ProcessStat.PidStat[] var11 = ProcessStat.PidStat.class.getEnumConstants();

      for (int var12 = 3; var12 < var11.length && var12 - 3 < var9.length; var12++) {
         var10.put(var11[var12], ParseUtil.parseLongOrDefault(var9[var12 - 3], 0L));
      }

      return new Triplet<>(var7, var8, var10);
   }

   public static File[] getPidFiles() {
      File var3 = new File(ProcPath.PROC);
      File[] var4 = var3.listFiles(var0 -> DIGITS.matcher(var0.getName()).matches());
      return var4 != null ? var4 : new File[0];
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

      private static final ProcessStat.PidStat[] s = new ProcessStat.PidStat[]{
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
