package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class SolarisGlobalMemory extends AbstractGlobalMemory {
   private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(this::readSystemPages, Memoizer.defaultExpiration());
   private final Supplier<Long> pageSize = Memoizer.memoize(SolarisGlobalMemory::queryPageSize);
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(SolarisGlobalMemory::createVirtualMemory);

   @Override
   public long getAvailable() {
      return this.availTotal.get().getA();
   }

   @Override
   public long getTotal() {
      return this.availTotal.get().getB();
   }

   @Override
   public long getPageSize() {
      return this.pageSize.get();
   }

   @Override
   public VirtualMemory getVirtualMemory() {
      return this.vm.get();
   }

   private static long queryPageSize() {
      return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("pagesize"), 4096L);
   }

   private static VirtualMemory createVirtualMemory() {
      return new SolarisVirtualMemory();
   }

   private Pair<Long, Long> readSystemPages() {
      long l = 0L;
      long m = 0L;

      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup(null, -1, "system_pages");
         if (kstat != null && kstatChain.read(kstat)) {
            l = KstatUtil.dataLookupLong(kstat, "availrmem") * this.getPageSize();
            m = KstatUtil.dataLookupLong(kstat, "physmem") * this.getPageSize();
         }
      }

      return new Pair<>(l, m);
   }
}
