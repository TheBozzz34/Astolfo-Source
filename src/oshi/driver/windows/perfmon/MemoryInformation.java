package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class MemoryInformation {
   private MemoryInformation() {
   }

   public static Map<MemoryInformation.PageSwapProperty, Long> queryPageSwaps() {
      PerfCounterQuery perfCounterQuery = new PerfCounterQuery<>(MemoryInformation.PageSwapProperty.class, "Memory", "Win32_PerfRawData_PerfOS_Memory");
      return perfCounterQuery.queryValues();
   }

   public enum PageSwapProperty implements PerfCounterQuery.PdhCounterProperty {
      PAGESINPUTPERSEC(null, "Pages Input/sec"),
      PAGESOUTPUTPERSEC(null, "Pages Output/sec");

      private final String instance;
      private final String counter;

      PageSwapProperty(String instance, String counter) {
         this.instance = instance;
         this.counter = counter;
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
