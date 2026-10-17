package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdVirtualMemory extends AbstractVirtualMemory {
   private final Supplier<Long> used = Memoizer.memoize(FreeBsdVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(FreeBsdVirtualMemory::querySwapTotal, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesIn = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesOut = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());

   @Override
   public long getSwapUsed() {
      return this.used.get();
   }

   @Override
   public long getSwapTotal() {
      return this.total.get();
   }

   @Override
   public long getSwapPagesIn() {
      return this.pagesIn.get();
   }

   @Override
   public long getSwapPagesOut() {
      return this.pagesOut.get();
   }

   private static long querySwapUsed() {
      String string = ExecutingCommand.getAnswerAt("swapinfo -k", 1);
      String[] strings = ParseUtil.whitespaces.split(string);
      return strings.length < 5 ? 0L : ParseUtil.parseLongOrDefault(strings[2], 0L) << 10;
   }

   private static long querySwapTotal() {
      return BsdSysctlUtil.sysctl("vm.swap_total", 0L);
   }

   private static long queryPagesIn() {
      return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsin", 0L);
   }

   private static long queryPagesOut() {
      return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsout", 0L);
   }
}
