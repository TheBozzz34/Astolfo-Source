package oshi.driver.linux.proc;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class CpuStat {
   private CpuStat() {
   }

   public static long[] getSystemCpuLoadTicks() {
      long[] longs = new long[CentralProcessor.TickType.values().length];
      List list = FileUtil.readFile(ProcPath.STAT);
      if (list.isEmpty()) {
         return longs;
      }

      String string = (String)list.get(0);
      String[] strings = ParseUtil.Q.split(string);
      if (strings.length <= CentralProcessor.TickType.IDLE.getIndex()) {
         return longs;
      }

      for (int i = 0; i < CentralProcessor.TickType.values().length; i++) {
         longs[i] = ParseUtil.parseLongOrDefault(strings[i + 1], 0L);
      }

      return longs;
   }

   public static long[][] getProcessorCpuLoadTicks(int logicalProcessorCount) {
      long[][] longss = new long[logicalProcessorCount][CentralProcessor.TickType.values().length];
      int i = 0;

      for (String string : FileUtil.readFile(ProcPath.STAT)) {
         if (string.startsWith("cpu") && !string.startsWith("cpu ")) {
            String[] strings = ParseUtil.Q.split(string);
            if (strings.length <= CentralProcessor.TickType.IDLE.getIndex()) {
               return longss;
            }

            for (int j = 0; j < CentralProcessor.TickType.values().length; j++) {
               longss[i][j] = ParseUtil.parseLongOrDefault(strings[j + 1], 0L);
            }

            if (++i >= logicalProcessorCount) {
               break;
            }
         }
      }

      return longss;
   }

   public static long getContextSwitches() {
      for (String string : FileUtil.readFile(ProcPath.STAT)) {
         if (string.startsWith("ctxt ")) {
            String[] strings = ParseUtil.Q.split(string);
            if (strings.length == 2) {
               return ParseUtil.parseLongOrDefault(strings[1], 0L);
            }
         }
      }

      return -1L;
   }

   public static long getInterrupts() {
      for (String string : FileUtil.readFile(ProcPath.STAT)) {
         if (string.startsWith("intr ")) {
            String[] strings = ParseUtil.Q.split(string);
            if (strings.length > 2) {
               return ParseUtil.parseLongOrDefault(strings[1], 0L);
            }
         }
      }

      return -1L;
   }

   public static long getBootTime() {
      for (String string : FileUtil.readFile(ProcPath.STAT)) {
         if (string.startsWith("btime")) {
            String[] strings = ParseUtil.Q.split(string);
            return ParseUtil.parseLongOrDefault(strings[1], 0L);
         }
      }

      return 0L;
   }
}
