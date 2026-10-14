package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class PagingFile {
   private PagingFile() {
   }

   public static Map<PagingFile.PagingPercentProperty, Long> querySwapUsed() {
      PerfCounterQuery perfCounterQuery = new PerfCounterQuery<>(PagingFile.PagingPercentProperty.class, "Paging File", "Win32_PerfRawData_PerfOS_PagingFile");
      return perfCounterQuery.queryValues();
   }

   public enum PagingPercentProperty implements PerfCounterQuery.PdhCounterProperty {
      PERCENTUSAGE("_Total", "% Usage");

      private final String instance;
      private final String counter;

      PagingPercentProperty(String instance, String counter) {
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
