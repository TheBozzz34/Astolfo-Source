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
      long l = 0L;
      long m = 0L;

      for (String string : FileUtil.readFile(ProcPath.MEMINFO)) {
         String[] strings = ParseUtil.Q.split(string);
         if (strings.length > 1) {
            switch (strings[0]) {
               case "SwapTotal:":
                  m = parseMeminfo(strings);
                  break;
               case "SwapFree:":
                  l = parseMeminfo(strings);
            }
         }
      }

      return new Pair<>(m - l, m);
   }

   private static Pair<Long, Long> queryVmStat() {
      long l = 0L;
      long m = 0L;

      for (String string : FileUtil.readFile(ProcPath.VMSTAT)) {
         String[] strings = ParseUtil.Q.split(string);
         if (strings.length > 1) {
            switch (strings[0]) {
               case "pgpgin":
                  l = ParseUtil.parseLongOrDefault(strings[1], 0L);
                  break;
               case "pgpgout":
                  m = ParseUtil.parseLongOrDefault(strings[1], 0L);
            }
         }
      }

      return new Pair<>(l, m);
   }

   private static long parseMeminfo(String[] memorySplit) {
      if (memorySplit.length < 2) {
         return 0L;
      }

      long l = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
      if (memorySplit.length > 2 && "kB".equals(memorySplit[2])) {
         l *= 1024L;
      }

      return l;
   }
}
