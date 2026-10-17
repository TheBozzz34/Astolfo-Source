package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import java.util.Map;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.MemoryInformation;
import oshi.driver.windows.perfmon.PagingFile;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.Memoizer;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class WindowsVirtualMemory extends AbstractVirtualMemory {
   private final long pageSize;
   private final Supplier<Long> used = Memoizer.memoize(this::querySwapUsed, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(this::querySwapTotal, Memoizer.defaultExpiration());
   private final Supplier<Pair<Long, Long>> swapInOut = Memoizer.memoize(WindowsVirtualMemory::queryPageSwaps, Memoizer.defaultExpiration());

   public WindowsVirtualMemory(long pageSize) {
      this.pageSize = pageSize;
   }

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
      return this.swapInOut.get().getA();
   }

   @Override
   public long getSwapPagesOut() {
      return this.swapInOut.get().getB();
   }

   private long querySwapUsed() {
      return PagingFile.querySwapUsed().getOrDefault(PagingFile.PagingPercentProperty.PERCENTUSAGE, 0L) * this.pageSize;
   }

   private long querySwapTotal() {
      PERFORMANCE_INFORMATION performancE_INFORMATION = new PERFORMANCE_INFORMATION();
      return !Psapi.INSTANCE.GetPerformanceInfo(performancE_INFORMATION, performancE_INFORMATION.size()) ? 0L : this.pageSize * (performancE_INFORMATION.CommitLimit.longValue() - performancE_INFORMATION.PhysicalTotal.longValue());
   }

   private static Pair<Long, Long> queryPageSwaps() {
      Map map = MemoryInformation.queryPageSwaps();
      return new Pair(
         map.getOrDefault(MemoryInformation.PageSwapProperty.PAGESINPUTPERSEC, 0L),
         map.getOrDefault(MemoryInformation.PageSwapProperty.PAGESOUTPUTPERSEC, 0L)
      );
   }
}
