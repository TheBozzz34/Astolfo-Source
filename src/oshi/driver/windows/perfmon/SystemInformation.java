package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class SystemInformation {
   private SystemInformation() {
   }

   public static Map<SystemInformation.ContextSwitchProperty, Long> queryContextSwitchCounters() {
      PerfCounterQuery perfCounterQuery = new PerfCounterQuery<>(SystemInformation.ContextSwitchProperty.class, "System", "Win32_PerfRawData_PerfOS_System");
      return perfCounterQuery.queryValues();
   }

   public enum ContextSwitchProperty implements PerfCounterQuery.PdhCounterProperty {
      CONTEXTSWITCHESPERSEC(null, "Context Switches/sec");

      private final String instance;
      private final String counter;

      ContextSwitchProperty(String instance, String counter) {
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
