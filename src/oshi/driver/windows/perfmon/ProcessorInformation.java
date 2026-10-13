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
   private static final long a = 42424246662570L;

   private ProcessorInformation() {
   }

   public static Pair<List<String>, Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>>> queryProcessorCounters() {
      PerfCounterWildcardQuery var4 = IS_WIN7_OR_GREATER
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
      Map var5 = var4.queryValuesWildcard();
      List var6 = var4.getInstancesFromLastQuery();
      return new Pair<>(var6, var5);
   }

   public static Map<ProcessorInformation.SystemTickCountProperty, Long> querySystemCounters() {
      PerfCounterQuery var2 = new PerfCounterQuery<>(
         ProcessorInformation.SystemTickCountProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"", "System Tick Count"
      );
      return var2.queryValues();
   }

   public static Map<ProcessorInformation.InterruptsProperty, Long> queryInterruptCounters() {
      PerfCounterQuery var2 = new PerfCounterQuery<>(
         ProcessorInformation.InterruptsProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"", "Interrupt Count"
      );
      return var2.queryValues();
   }

   public static Pair<List<String>, Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>>> queryFrequencyCounters() {
      PerfCounterWildcardQuery var3 = new PerfCounterWildcardQuery<>(
         ProcessorInformation.ProcessorFrequencyProperty.class,
         "Processor Information",
         "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE\"%_Total\"",
         "Processor Frequency"
      );
      Map var4 = var3.queryValuesWildcard();
      List var5 = var3.getInstancesFromLastQuery();
      return new Pair<>(var5, var4);
   }

   public enum InterruptsProperty implements PerfCounterQuery.PdhCounterProperty {
      INTERRUPTSPERSEC("_Total", "Interrupts/sec");

      private final String S;
      private final String w;
      private static final ProcessorInformation.InterruptsProperty[] y = new ProcessorInformation.InterruptsProperty[]{INTERRUPTSPERSEC};

      InterruptsProperty(String var3, String var4) {
         this.S = var3;
         this.w = var4;
      }

      @Override
      public String getInstance() {
         return this.S;
      }

      @Override
      public String getCounter() {
         return this.w;
      }
   }

   public enum ProcessorFrequencyProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("^*_Total"),
      PERCENTOFMAXIMUMFREQUENCY("% of Maximum Frequency");

      private final String counter;
      private static final ProcessorInformation.ProcessorFrequencyProperty[] s = new ProcessorInformation.ProcessorFrequencyProperty[]{
         NAME, PERCENTOFMAXIMUMFREQUENCY
      };

      ProcessorFrequencyProperty(String var3) {
         this.counter = var3;
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
      private static final ProcessorInformation.ProcessorTickCountProperty[] B = new ProcessorInformation.ProcessorTickCountProperty[]{
         NAME, PERCENTDPCTIME, PERCENTINTERRUPTTIME, PERCENTPRIVILEGEDTIME, PERCENTPROCESSORTIME, PERCENTUSERTIME
      };

      ProcessorTickCountProperty(String var3) {
         this.counter = var3;
      }

      @Override
      public String getCounter() {
         return this.counter;
      }
   }

   public enum SystemTickCountProperty implements PerfCounterQuery.PdhCounterProperty {
      PERCENTDPCTIME("_Total", "% DPC Time"),
      PERCENTINTERRUPTTIME("_Total", "% Interrupt Time");

      private final String s;
      private final String n;
      private static final ProcessorInformation.SystemTickCountProperty[] u = new ProcessorInformation.SystemTickCountProperty[]{
         PERCENTDPCTIME, PERCENTINTERRUPTTIME
      };

      SystemTickCountProperty(String var3, String var4) {
         this.s = var3;
         this.n = var4;
      }

      @Override
      public String getInstance() {
         return this.s;
      }

      @Override
      public String getCounter() {
         return this.n;
      }
   }
}
