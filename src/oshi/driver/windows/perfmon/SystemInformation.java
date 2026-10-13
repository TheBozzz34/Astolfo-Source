package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class SystemInformation {
   private static final long a = 118265608339849L;

   private SystemInformation() {
   }

   public static Map<SystemInformation.ContextSwitchProperty, Long> queryContextSwitchCounters() {
      PerfCounterQuery var2 = new PerfCounterQuery<>(SystemInformation.ContextSwitchProperty.class, "System", "Win32_PerfRawData_PerfOS_System");
      return var2.queryValues();
   }

   public enum ContextSwitchProperty implements PerfCounterQuery.PdhCounterProperty {
      CONTEXTSWITCHESPERSEC(null, "Context Switches/sec");

      private final String instance;
      private final String counter;

      ContextSwitchProperty(String var3, String var4) {
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
