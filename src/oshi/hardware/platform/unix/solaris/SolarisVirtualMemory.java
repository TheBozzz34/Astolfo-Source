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
      long l = 0L;

      for (String string : ExecutingCommand.runNative("kstat -p cpu_stat:::pgpgin")) {
         l += ParseUtil.parseLastLong(string, 0L);
      }

      return l;
   }

   private static long queryPagesOut() {
      long l = 0L;

      for (String string : ExecutingCommand.runNative("kstat -p cpu_stat:::pgpgout")) {
         l += ParseUtil.parseLastLong(string, 0L);
      }

      return l;
   }

   private static Pair<Long, Long> querySwapInfo() {
      long l = 0L;
      long m = 0L;
      String string = ExecutingCommand.getAnswerAt("swap -lk", 1);
      Matcher matcher2 = SWAP_INFO.matcher(string);
      if (matcher2.matches()) {
         l = ParseUtil.parseLongOrDefault(matcher2.group(1), 0L) << 10;
         m = l - (ParseUtil.parseLongOrDefault(matcher2.group(2), 0L) << 10);
      }

      return new Pair<>(m, l);
   }
}
