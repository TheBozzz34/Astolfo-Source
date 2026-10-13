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
   private static final long a = 88887668179967L;
   private static final long g = 493893901573160970L;

   @Override
   public long y() {
      return this.used.get();
   }

   @Override
   public long n() {
      return this.total.get();
   }

   @Override
   public long L() {
      return this.pagesIn.get();
   }

   @Override
   public long I() {
      return this.pagesOut.get();
   }

   private static long querySwapUsed() {
      String var3 = ExecutingCommand.getAnswerAt("swapinfo -k", 1);
      String[] var4 = ParseUtil.Q.split(var3);
      return var4.length < 5 ? 0L : ParseUtil.parseLongOrDefault(var4[2], 0L) << (int)g;
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
