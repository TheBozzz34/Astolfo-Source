package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class LinuxVirtualMemory extends AbstractVirtualMemory {
   private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(LinuxVirtualMemory::queryMemInfo, Memoizer.defaultExpiration());
   private final Supplier<Pair<Long, Long>> inOut = Memoizer.memoize(LinuxVirtualMemory::queryVmStat, Memoizer.defaultExpiration());
   private static final long a = 4966177384096L;

   @Override
   public long y() {
      return this.usedTotal.get().getA();
   }

   @Override
   public long n() {
      return this.usedTotal.get().getB();
   }

   @Override
   public long L() {
      return this.inOut.get().getA();
   }

   @Override
   public long I() {
      return this.inOut.get().getB();
   }

   private static Pair<Long, Long> queryMemInfo() {
      long var3 = 0L;
      long var5 = 0L;

      for (String var9 : FileUtil.readFile(ProcPath.MEMINFO)) {
         String[] var10 = ParseUtil.Q.split(var9);
         if (var10.length > 1) {
            switch (var10[0]) {
               case "SwapTotal:":
                  var5 = parseMeminfo(var10);
                  break;
               case "SwapFree:":
                  var3 = parseMeminfo(var10);
            }
         }
      }

      return new Pair<>(var5 - var3, var5);
   }

   private static Pair<Long, Long> queryVmStat() {
      long var3 = 0L;
      long var5 = 0L;

      for (String var9 : FileUtil.readFile(ProcPath.VMSTAT)) {
         String[] var10 = ParseUtil.Q.split(var9);
         if (var10.length > 1) {
            switch (var10[0]) {
               case "pgpgin":
                  var3 = ParseUtil.parseLongOrDefault(var10[1], 0L);
                  break;
               case "pgpgout":
                  var5 = ParseUtil.parseLongOrDefault(var10[1], 0L);
            }
         }
      }

      return new Pair<>(var3, var5);
   }

   private static long parseMeminfo(String[] var0) {
      if (var0.length < 2) {
         return 0L;
      }

      long var4 = ParseUtil.parseLongOrDefault(var0[1], 0L);
      if (var0.length > 2 && "kB".equals(var0[2])) {
         var4 *= 1024L;
      }

      return var4;
   }
}
