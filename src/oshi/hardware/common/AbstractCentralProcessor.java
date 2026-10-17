package oshi.hardware.common;

import java.util.HashSet;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractCentralProcessor implements CentralProcessor {
   private final Supplier<CentralProcessor.ProcessorIdentifier> cpuid = Memoizer.memoize(this::queryProcessorId);
   private final Supplier<Long> maxFreq = Memoizer.memoize(this::queryMaxFreq);
   private final Supplier<long[]> currentFreq = Memoizer.memoize(this::queryCurrentFreq, Memoizer.defaultExpiration());
   private final Supplier<Long> contextSwitches = Memoizer.memoize(this::queryContextSwitches, Memoizer.defaultExpiration());
   private final Supplier<Long> interrupts = Memoizer.memoize(this::queryInterrupts, Memoizer.defaultExpiration());
   private final Supplier<long[]> systemCpuLoadTicks = Memoizer.memoize(this::querySystemCpuLoadTicks, Memoizer.defaultExpiration());
   private final Supplier<long[][]> processorCpuLoadTicks = Memoizer.memoize(this::queryProcessorCpuLoadTicks, Memoizer.defaultExpiration());
   private final int physicalPackageCount;
   private final int physicalProcessorCount;
   private final int logicalProcessorCount;
   private final CentralProcessor.LogicalProcessor[] logicalProcessors = this.initProcessorCounts();

   public AbstractCentralProcessor() {
      HashSet hashSet = new HashSet();
      HashSet hashSet2 = new HashSet();

      for (CentralProcessor.LogicalProcessor logicalProcessor : this.logicalProcessors) {
         int i = logicalProcessor.getPhysicalPackageNumber();
         hashSet.add(logicalProcessor.getPhysicalProcessorNumber() + ":" + i);
         hashSet2.add(i);
      }

      this.logicalProcessorCount = this.logicalProcessors.length;
      this.physicalProcessorCount = hashSet.size();
      this.physicalPackageCount = hashSet2.size();
   }

   public abstract CentralProcessor.LogicalProcessor[] initProcessorCounts();

   public abstract CentralProcessor.ProcessorIdentifier queryProcessorId();

   @Override
   public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
      return this.cpuid.get();
   }

   @Override
   public String getVendor() {
      return this.getProcessorIdentifier().getVendor();
   }

   @Override
   public String getName() {
      return this.getProcessorIdentifier().getName();
   }

   @Override
   public String getFamily() {
      return this.getProcessorIdentifier().getFamily();
   }

   @Override
   public String getModel() {
      return this.getProcessorIdentifier().getModel();
   }

   @Override
   public String y() {
      return this.getProcessorIdentifier().getStepping();
   }

   @Override
   public String R() {
      return this.getProcessorIdentifier().getProcessorID();
   }

   @Override
   public String getIdentifier() {
      return this.getProcessorIdentifier().getIdentifier();
   }

   @Override
   public boolean isCpu64bit() {
      return this.getProcessorIdentifier().isCpu64bit();
   }

   @Override
   public long getVendorFreq() {
      return this.getProcessorIdentifier().getVendorFreq();
   }

   @Override
   public long getMaxFreq() {
      return this.maxFreq.get();
   }

   public abstract long queryMaxFreq();

   @Override
   public long[] getCurrentFreq() {
      return this.currentFreq.get();
   }

   public abstract long[] queryCurrentFreq();

   @Override
   public long getContextSwitches() {
      return this.contextSwitches.get();
   }

   public abstract long queryContextSwitches();

   @Override
   public long getInterrupts() {
      return this.interrupts.get();
   }

   public abstract long queryInterrupts();

   @Override
   public CentralProcessor.LogicalProcessor[] getLogicalProcessors() {
      return this.logicalProcessors;
   }

   @Override
   public long[] getSystemCpuLoadTicks() {
      return this.systemCpuLoadTicks.get();
   }

   public abstract long[] querySystemCpuLoadTicks();

   @Override
   public long[][] getProcessorCpuLoadTicks() {
      return this.processorCpuLoadTicks.get();
   }

   public abstract long[][] queryProcessorCpuLoadTicks();

   @Override
   public double getSystemCpuLoadBetweenTicks(long[] oldTicks) {
      if (oldTicks.length != CentralProcessor.TickType.values().length) {
         throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + CentralProcessor.TickType.values().length + " elements");
      }

      long[] longs = this.getSystemCpuLoadTicks();
      long l = 0L;

      for (int i = 0; i < longs.length; i++) {
         l += longs[i] - oldTicks[i];
      }

      long m = longs[CentralProcessor.TickType.IDLE.getIndex()]
         + longs[CentralProcessor.TickType.IOWAIT.getIndex()]
         - oldTicks[CentralProcessor.TickType.IDLE.getIndex()]
         - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
      return l > 0L && m >= 0L ? (double)(l - m) / l : 0.0;
   }

   @Override
   public double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks) {
      if (oldTicks.length == this.logicalProcessorCount && oldTicks[0].length == CentralProcessor.TickType.values().length) {
         long[][] longss = this.getProcessorCpuLoadTicks();
         double[] doubles = new double[this.logicalProcessorCount];

         for (int i = 0; i < this.logicalProcessorCount; i++) {
            long l = 0L;

            for (int j = 0; j < longss[i].length; j++) {
               l += longss[i][j] - oldTicks[i][j];
            }

            long m = longss[i][CentralProcessor.TickType.IDLE.getIndex()]
               + longss[i][CentralProcessor.TickType.IOWAIT.getIndex()]
               - oldTicks[i][CentralProcessor.TickType.IDLE.getIndex()]
               - oldTicks[i][CentralProcessor.TickType.IOWAIT.getIndex()];
            doubles[i] = l > 0L && m >= 0L ? (double)(l - m) / l : 0.0;
         }

         return doubles;
      } else {
         throw new IllegalArgumentException(
            "Tick array "
               + oldTicks.length
               + " should have "
               + this.logicalProcessorCount
               + " arrays, each of which has "
               + CentralProcessor.TickType.values().length
               + " elements"
         );
      }
   }

   @Override
   public int getLogicalProcessorCount() {
      return this.logicalProcessorCount;
   }

   @Override
   public int getPhysicalProcessorCount() {
      return this.physicalProcessorCount;
   }

   @Override
   public int getPhysicalPackageCount() {
      return this.physicalPackageCount;
   }

   public static String createProcessorID(String stepping, String model, String family, String[] flags) {
      long l = 0L;
      long m = ParseUtil.parseLongOrDefault(stepping, 0L);
      long n = ParseUtil.parseLongOrDefault(model, 0L);
      long l2 = ParseUtil.parseLongOrDefault(family, 0L);
      l |= m & 15L;
      l |= (n & 15L) << 4;
      l |= (n & 240L) << 16;
      l |= (l2 & 15L) << 8;
      l |= (l2 & 240L) << 20;

      for (String string : flags) {
         switch (string) {
            case "fpu":
               l |= 4294967296L;
               break;
            case "vme":
               l |= 8589934592L;
               break;
            case "de":
               l |= 17179869184L;
               break;
            case "pse":
               l |= 34359738368L;
               break;
            case "tsc":
               l |= 68719476736L;
               break;
            case "msr":
               l |= 137438953472L;
               break;
            case "pae":
               l |= 274877906944L;
               break;
            case "mce":
               l |= 549755813888L;
               break;
            case "cx8":
               l |= 1099511627776L;
               break;
            case "apic":
               l |= 2199023255552L;
               break;
            case "sep":
               l |= 8796093022208L;
               break;
            case "mtrr":
               l |= 17592186044416L;
               break;
            case "pge":
               l |= 35184372088832L;
               break;
            case "mca":
               l |= 70368744177664L;
               break;
            case "cmov":
               l |= 140737488355328L;
               break;
            case "pat":
               l |= 281474976710656L;
               break;
            case "pse-36":
               l |= 562949953421312L;
               break;
            case "psn":
               l |= 1125899906842624L;
               break;
            case "clfsh":
               l |= 2251799813685248L;
               break;
            case "ds":
               l |= 9007199254740992L;
               break;
            case "acpi":
               l |= 18014398509481984L;
               break;
            case "mmx":
               l |= 36028797018963968L;
               break;
            case "fxsr":
               l |= 72057594037927936L;
               break;
            case "sse":
               l |= 144115188075855872L;
               break;
            case "sse2":
               l |= 288230376151711744L;
               break;
            case "ss":
               l |= 576460752303423488L;
               break;
            case "htt":
               l |= 1152921504606846976L;
               break;
            case "tm":
               l |= 2305843009213693952L;
               break;
            case "ia64":
               l |= 4611686018427387904L;
               break;
            case "pbe":
               l |= Long.MIN_VALUE;
         }
      }

      return String.format("%016X", l);
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder(this.getName());
      stringBuilder.append("\n ").append(this.getPhysicalPackageCount()).append(" physical CPU package(s)");
      stringBuilder.append("\n ").append(this.getPhysicalProcessorCount()).append(" physical CPU core(s)");
      stringBuilder.append("\n ").append(this.getLogicalProcessorCount()).append(" logical CPU(s)");
      stringBuilder.append('\n').append("Identifier: ").append(this.getProcessorIdentifier().getIdentifier());
      stringBuilder.append('\n').append("ProcessorID: ").append(this.getProcessorIdentifier().getProcessorID());
      stringBuilder.append('\n').append("Microarchitecture: ").append(this.getProcessorIdentifier().getMicroarchitecture());
      return stringBuilder.toString();
   }
}
