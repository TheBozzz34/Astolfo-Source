package oshi.driver.windows.perfmon;

import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ProcessInformation {
   private ProcessInformation() {
   }

   public static Pair<List<String>, Map<ProcessInformation.ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
      PerfCounterWildcardQuery perfCounterWildcardQuery = new PerfCounterWildcardQuery<>(
         ProcessInformation.ProcessPerformanceProperty.class, "Process", "Win32_Process WHERE NOT Name LIKE\"%_Total\"", "Process Information"
      );
      Map map = perfCounterWildcardQuery.queryValuesWildcard();
      List list = perfCounterWildcardQuery.getInstancesFromLastQuery();
      return new Pair<>(list, map);
   }

   public static Map<ProcessInformation.HandleCountProperty, List<Long>> queryHandles() {
      PerfCounterWildcardQuery perfCounterWildcardQuery = new PerfCounterWildcardQuery<>(ProcessInformation.HandleCountProperty.class, "Process", "Win32_Process");
      return perfCounterWildcardQuery.queryValuesWildcard();
   }

   public enum HandleCountProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("_Total"),
      HANDLECOUNT("Handle Count");

      private final String counter;
      private static final ProcessInformation.HandleCountProperty[] VALUES = new ProcessInformation.HandleCountProperty[]{NAME, HANDLECOUNT};

      HandleCountProperty(String counter) {
         this.counter = counter;
      }

      @Override
      public String getCounter() {
         return this.counter;
      }
   }

   public enum ProcessPerformanceProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("^*_Total"),
      PRIORITY("Priority Base"),
      CREATIONDATE("Elapsed Time"),
      PROCESSID("ID Process"),
      PARENTPROCESSID("Creating Process ID"),
      READTRANSFERCOUNT("IO Read Bytes/sec"),
      WRITETRANSFERCOUNT("IO Write Bytes/sec"),
      PRIVATEPAGECOUNT("Working Set - Private");

      private final String counter;
      private static final ProcessInformation.ProcessPerformanceProperty[] VALUES = new ProcessInformation.ProcessPerformanceProperty[]{
         NAME, PRIORITY, CREATIONDATE, PROCESSID, PARENTPROCESSID, READTRANSFERCOUNT, WRITETRANSFERCOUNT, PRIVATEPAGECOUNT
      };

      ProcessPerformanceProperty(String counter) {
         this.counter = counter;
      }

      @Override
      public String getCounter() {
         return this.counter;
      }
   }
}
