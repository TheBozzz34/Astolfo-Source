package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.VMStatistics;
import com.sun.jna.platform.mac.SystemB.XswUsage;
import com.sun.jna.ptr.IntByReference;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class MacVirtualMemory extends AbstractVirtualMemory {
   private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(MacVirtualMemory::querySwapUsage, Memoizer.defaultExpiration());
   private final Supplier<Pair<Long, Long>> inOut = Memoizer.memoize(MacVirtualMemory::queryVmStat, Memoizer.defaultExpiration());
   private static final long a = 56391752108704L;

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

   private static Pair<Long, Long> querySwapUsage() {
      long var3 = 0L;
      long var5 = 0L;
      XswUsage var7 = new XswUsage();
      if (SysctlUtil.sysctl("vm.swapusage", var7)) {
         var3 = var7.xsu_used;
         var5 = var7.xsu_total;
      }

      return new Pair<>(var3, var5);
   }

   private static Pair<Long, Long> queryVmStat() {
      long var3 = 0L;
      long var5 = 0L;
      VMStatistics var7 = new VMStatistics();
      if (0 == SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, var7, new IntByReference(var7.size() / SystemB.INT_SIZE))) {
         var3 = ParseUtil.unsignedIntToLong(var7.pageins);
         var5 = ParseUtil.unsignedIntToLong(var7.pageouts);
      }

      return new Pair<>(var3, var5);
   }
}
