package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class MemoryInformation {
   private static final long b = 63415472418172L;

   private MemoryInformation() {
   }

   public static Map<MemoryInformation.PageSwapProperty, Long> queryPageSwaps() {
      PerfCounterQuery var2 = new PerfCounterQuery<>(MemoryInformation.PageSwapProperty.class, "Memory", "Win32_PerfRawData_PerfOS_Memory");
      return var2.queryValues();
   }

   public enum PageSwapProperty implements PerfCounterQuery.PdhCounterProperty {
      PAGESINPUTPERSEC(null, "Pages Input/sec"),
      PAGESOUTPUTPERSEC(null, "Pages Output/sec");

      private final String instance;
      private final String counter;

      PageSwapProperty(String var3, String var4) {
         this.instance = var3;
         this.counter = var4;
      }

      @Override
      public String getInstance() {
         return this.instance;
      }

      @Override
      public String getCounter() {
         return this.counter;
      }
   }
}
