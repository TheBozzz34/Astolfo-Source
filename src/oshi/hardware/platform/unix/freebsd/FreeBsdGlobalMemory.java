package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdGlobalMemory extends AbstractGlobalMemory {
   private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(FreeBsdGlobalMemory::queryPhysMem);
   private final Supplier<Long> pageSize = Memoizer.memoize(FreeBsdGlobalMemory::queryPageSize);
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(FreeBsdGlobalMemory::createVirtualMemory);

   @Override
   public long d() {
      return this.available.get();
   }

   @Override
   public long f() {
      return this.total.get();
   }

   @Override
   public long getPageSize() {
      return this.pageSize.get();
   }

   @Override
   public VirtualMemory getVirtualMemory() {
      return this.vm.get();
   }

   private long queryVmStats() {
      long l = BsdSysctlUtil.sysctl("vm.stats.vm.v_inactive_count", 0L);
      long m = BsdSysctlUtil.sysctl("vm.stats.vm.v_cache_count", 0L);
      long n = BsdSysctlUtil.sysctl("vm.stats.vm.v_free_count", 0L);
      return (l + m + n) * this.getPageSize();
   }

   private static long queryPhysMem() {
      return BsdSysctlUtil.sysctl("hw.physmem", 0L);
   }

   private static long queryPageSize() {
      return BsdSysctlUtil.sysctl("hw.pagesize", 4096L);
   }

   private static VirtualMemory createVirtualMemory() {
      return new FreeBsdVirtualMemory();
   }
}
