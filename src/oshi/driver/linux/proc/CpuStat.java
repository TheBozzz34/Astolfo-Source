package oshi.driver.linux.proc;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class CpuStat {
   private static final long a = 9268179708812L;

   private CpuStat() {
   }

   public static long[] getSystemCpuLoadTicks() {
      long[] var3 = new long[CentralProcessor.TickType.values().length];
      List var5 = FileUtil.readFile(ProcPath.STAT);
      if (var5.isEmpty()) {
         return var3;
      }

      String var4 = (String)var5.get(0);
      String[] var6 = ParseUtil.Q.split(var4);
      if (var6.length <= CentralProcessor.TickType.IDLE.getIndex()) {
         return var3;
      }

      for (int var7 = 0; var7 < CentralProcessor.TickType.values().length; var7++) {
         var3[var7] = ParseUtil.parseLongOrDefault(var6[var7 + 1], 0L);
      }

      return var3;
   }

   public static long[][] getProcessorCpuLoadTicks(int var0) {
      long[][] var4 = new long[var0][CentralProcessor.TickType.values().length];
      int var5 = 0;

      for (String var8 : FileUtil.readFile(ProcPath.STAT)) {
         if (var8.startsWith("cpu") && !var8.startsWith("cpu ")) {
            String[] var9 = ParseUtil.Q.split(var8);
            if (var9.length <= CentralProcessor.TickType.IDLE.getIndex()) {
               return var4;
            }

            for (int var10 = 0; var10 < CentralProcessor.TickType.values().length; var10++) {
               var4[var5][var10] = ParseUtil.parseLongOrDefault(var9[var10 + 1], 0L);
            }

            if (++var5 >= var0) {
               break;
            }
         }
      }

      return var4;
   }

   public static long getContextSwitches() {
      for (String var5 : FileUtil.readFile(ProcPath.STAT)) {
         if (var5.startsWith("ctxt ")) {
            String[] var6 = ParseUtil.Q.split(var5);
            if (var6.length == 2) {
               return ParseUtil.parseLongOrDefault(var6[1], 0L);
            }
         }
      }

      return -1L;
   }

   public static long getInterrupts() {
      for (String var5 : FileUtil.readFile(ProcPath.STAT)) {
         if (var5.startsWith("intr ")) {
            String[] var6 = ParseUtil.Q.split(var5);
            if (var6.length > 2) {
               return ParseUtil.parseLongOrDefault(var6[1], 0L);
            }
         }
      }

      return -1L;
   }

   public static long getBootTime() {
      for (String var4 : FileUtil.readFile(ProcPath.STAT)) {
         if (var4.startsWith("btime")) {
            String[] var5 = ParseUtil.Q.split(var4);
            return ParseUtil.parseLongOrDefault(var5[1], 0L);
         }
      }

      return 0L;
   }
}
