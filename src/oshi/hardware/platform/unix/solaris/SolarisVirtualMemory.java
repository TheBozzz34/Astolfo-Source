package oshi.hardware.platform.unix.solaris;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class SolarisVirtualMemory extends AbstractVirtualMemory {
   private static final Pattern SWAP_INFO = Pattern.compile(".+\\s(\\d+)K\\s+(\\d+)K$");
   private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(SolarisVirtualMemory::querySwapInfo, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesIn = Memoizer.memoize(SolarisVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesOut = Memoizer.memoize(SolarisVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());
   private static final long a = 136907101969911L;

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
      return this.pagesIn.get();
   }

   @Override
   public long I() {
      return this.pagesOut.get();
   }

   private static long queryPagesIn() {
      long var3 = 0L;

      for (String var6 : ExecutingCommand.runNative("kstat -p cpu_stat:::pgpgin")) {
         var3 += ParseUtil.parseLastLong(var6, 0L);
      }

      return var3;
   }

   private static long queryPagesOut() {
      long var3 = 0L;

      for (String var6 : ExecutingCommand.runNative("kstat -p cpu_stat:::pgpgout")) {
         var3 += ParseUtil.parseLastLong(var6, 0L);
      }

      return var3;
   }

   private static Pair<Long, Long> querySwapInfo() {
      long var3 = 0L;
      long var5 = 0L;
      String var7 = ExecutingCommand.getAnswerAt("swap -lk", 1);
      Matcher var8 = SWAP_INFO.matcher(var7);
      if (var8.matches()) {
         var3 = ParseUtil.parseLongOrDefault(var8.group(1), 0L) << 10;
         var5 = var3 - (ParseUtil.parseLongOrDefault(var8.group(2), 0L) << 10);
      }

      return new Pair<>(var5, var3);
   }
}
