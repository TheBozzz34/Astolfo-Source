package oshi.driver.windows.perfmon;

import com.sun.jna.platform.win32.VersionHelpers;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ProcessorInformation {
   private static final boolean IS_WIN7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();

   private ProcessorInformation() {
   }

   public static Pair<List<String>, Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>>> queryProcessorCounters() {
      PerfCounterWildcardQuery perfCounterWildcardQuery = IS_WIN7_OR_GREATER
         ? new PerfCounterWildcardQuery<>(
            ProcessorInformation.ProcessorTickCountProperty.class,
            "Processor Information",
            "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE\"%_Total\"",
            "Processor Tick Count"
         )
         : new PerfCounterWildcardQuery<>(
            ProcessorInformation.ProcessorTickCountProperty.class,
            "Processor",
            "Win32_PerfRawData_PerfOS_Processor WHERE NOT Name=\"_Total\"",
            "Processor Tick Count"
         );
      Map map = perfCounterWildcardQuery.queryValuesWildcard();
      List list = perfCounterWildcardQuery.getInstancesFromLastQuery();
      return new Pair<>(list, map);
   }

   public static Map<ProcessorInformation.SystemTickCountProperty, Long> querySystemCounters() {
      PerfCounterQuery perfCounterQuery = new PerfCounterQuery<>(
         ProcessorInformation.SystemTickCountProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"", "System Tick Count"
      );
      return perfCounterQuery.queryValues();
   }

   public static Map<ProcessorInformation.InterruptsProperty, Long> queryInterruptCounters() {
      PerfCounterQuery perfCounterQuery = new PerfCounterQuery<>(
         ProcessorInformation.InterruptsProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"", "Interrupt Count"
      );
      return perfCounterQuery.queryValues();
   }

   public static Pair<List<String>, Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>>> queryFrequencyCounters() {
      PerfCounterWildcardQuery perfCounterWildcardQuery = new PerfCounterWildcardQuery<>(
         ProcessorInformation.ProcessorFrequencyProperty.class,
         "Processor Information",
         "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE\"%_Total\"",
         "Processor Frequency"
      );
      Map map = perfCounterWildcardQuery.queryValuesWildcard();
      List list = perfCounterWildcardQuery.getInstancesFromLastQuery();
      return new Pair<>(list, map);
   }

   public enum InterruptsProperty implements PerfCounterQuery.PdhCounterProperty {
      INTERRUPTSPERSEC("_Total", "Interrupts/sec");

      private final String instance;
      private final String counter;
      private static final ProcessorInformation.InterruptsProperty[] y = new ProcessorInformation.InterruptsProperty[]{INTERRUPTSPERSEC};

      InterruptsProperty(String instance, String counter) {
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

   public enum ProcessorFrequencyProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("^*_Total"),
      PERCENTOFMAXIMUMFREQUENCY("% of Maximum Frequency");

      private final String counter;
      private static final ProcessorInformation.ProcessorFrequencyProperty[] VALUES = new ProcessorInformation.ProcessorFrequencyProperty[]{
         NAME, PERCENTOFMAXIMUMFREQUENCY
      };

      ProcessorFrequencyProperty(String counter) {
         this.counter = counter;
      }

      @Override
      public String getCounter() {
         return this.counter;
      }
   }

   public enum ProcessorTickCountProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("^*_Total"),
      PERCENTDPCTIME("% DPC Time"),
      PERCENTINTERRUPTTIME("% Interrupt Time"),
      PERCENTPRIVILEGEDTIME("% Privileged Time"),
      PERCENTPROCESSORTIME("% Processor Time"),
      PERCENTUSERTIME("% User Time");

      private final String counter;
      private static final ProcessorInformation.ProcessorTickCountProperty[] VALUES = new ProcessorInformation.ProcessorTickCountProperty[]{
         NAME, PERCENTDPCTIME, PERCENTINTERRUPTTIME, PERCENTPRIVILEGEDTIME, PERCENTPROCESSORTIME, PERCENTUSERTIME
      };

      ProcessorTickCountProperty(String counter) {
         this.counter = counter;
      }

      @Override
      public String getCounter() {
         return this.counter;
      }
   }

   public enum SystemTickCountProperty implements PerfCounterQuery.PdhCounterProperty {
      PERCENTDPCTIME("_Total", "% DPC Time"),
      PERCENTINTERRUPTTIME("_Total", "% Interrupt Time");

      private final String instance;
      private final String counter;
      private static final ProcessorInformation.SystemTickCountProperty[] VALUES = new ProcessorInformation.SystemTickCountProperty[]{
         PERCENTDPCTIME, PERCENTINTERRUPTTIME
      };

      SystemTickCountProperty(String instance, String counter) {
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
