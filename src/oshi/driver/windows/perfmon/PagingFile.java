package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class PagingFile {
   private static final long a = 130876006062552L;

   private PagingFile() {
   }

   public static Map<PagingFile.PagingPercentProperty, Long> querySwapUsed() {
      PerfCounterQuery var2 = new PerfCounterQuery<>(PagingFile.PagingPercentProperty.class, "Paging File", "Win32_PerfRawData_PerfOS_PagingFile");
      return var2.queryValues();
   }

   public enum PagingPercentProperty implements PerfCounterQuery.PdhCounterProperty {
      PERCENTUSAGE("_Total", "% Usage");

      private final String instance;
      private final String counter;

      PagingPercentProperty(String var3, String var4) {
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
