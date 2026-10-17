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

   @Override
   public long getSwapUsed() {
      return this.usedTotal.get().getA();
   }

   @Override
   public long getSwapTotal() {
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
      long l = 0L;
      long m = 0L;
      XswUsage xswUsage = new XswUsage();
      if (SysctlUtil.sysctl("vm.swapusage", xswUsage)) {
         l = xswUsage.xsu_used;
         m = xswUsage.xsu_total;
      }

      return new Pair<>(l, m);
   }

   private static Pair<Long, Long> queryVmStat() {
      long l = 0L;
      long m = 0L;
      VMStatistics vmStatistics = new VMStatistics();
      if (0 == SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, vmStatistics, new IntByReference(vmStatistics.size() / SystemB.INT_SIZE))) {
         l = ParseUtil.unsignedIntToLong(vmStatistics.pageins);
         m = ParseUtil.unsignedIntToLong(vmStatistics.pageouts);
      }

      return new Pair<>(l, m);
   }
}
