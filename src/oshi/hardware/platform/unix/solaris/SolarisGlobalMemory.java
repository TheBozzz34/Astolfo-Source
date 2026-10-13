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
   private static final long a = 79856669348290L;

   @Override
   public long d() {
      return this.availTotal.get().getA();
   }

   @Override
   public long f() {
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
      long var4 = 0L;
      long var6 = 0L;

      try (KstatUtil.KstatChain var8 = KstatUtil.openChain()) {
         Kstat var10 = var8.lookup(null, -1, "system_pages");
         if (var10 != null && var8.read(var10)) {
            var4 = KstatUtil.dataLookupLong(var10, "availrmem") * this.getPageSize();
            var6 = KstatUtil.dataLookupLong(var10, "physmem") * this.getPageSize();
         }
      }

      return new Pair<>(var4, var6);
   }
}
