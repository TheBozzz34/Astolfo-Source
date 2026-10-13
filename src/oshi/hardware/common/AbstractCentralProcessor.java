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
   private static final long a = 40000205445972L;

   public AbstractCentralProcessor() {
      HashSet var4 = new HashSet();
      HashSet var5 = new HashSet();

      for (CentralProcessor.LogicalProcessor var9 : this.logicalProcessors) {
         int var10 = var9.getPhysicalPackageNumber();
         var4.add(var9.getPhysicalProcessorNumber() + ":" + var10);
         var5.add(var10);
      }

      this.logicalProcessorCount = this.logicalProcessors.length;
      this.physicalProcessorCount = var4.size();
      this.physicalPackageCount = var5.size();
   }

   public abstract CentralProcessor.LogicalProcessor[] initProcessorCounts();

   public abstract CentralProcessor.ProcessorIdentifier queryProcessorId();

   @Override
   public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
      return this.cpuid.get();
   }

   @Override
   public String P() {
      return this.getProcessorIdentifier().H();
   }

   @Override
   public String getName() {
      return this.getProcessorIdentifier().O();
   }

   @Override
   public String v() {
      return this.getProcessorIdentifier().d();
   }

   @Override
   public String Z() {
      return this.getProcessorIdentifier().V();
   }

   @Override
   public String y() {
      return this.getProcessorIdentifier().r();
   }

   @Override
   public String R() {
      return this.getProcessorIdentifier().getProcessorID();
   }

   @Override
   public String w() {
      return this.getProcessorIdentifier().getIdentifier();
   }

   @Override
   public boolean isCpu64bit() {
      return this.getProcessorIdentifier().isCpu64bit();
   }

   @Override
   public long g() {
      return this.getProcessorIdentifier().getVendorFreq();
   }

   @Override
   public long K() {
      return this.maxFreq.get();
   }

   public abstract long queryMaxFreq();

   @Override
   public long[] o() {
      return this.currentFreq.get();
   }

   public abstract long[] queryCurrentFreq();

   @Override
   public long Q() {
      return this.contextSwitches.get();
   }

   public abstract long queryContextSwitches();

   @Override
   public long m() {
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
   public double getSystemCpuLoadBetweenTicks(long[] var1) {
      if (var1.length != CentralProcessor.TickType.values().length) {
         throw new IllegalArgumentException("Tick array " + var1.length + " should have " + CentralProcessor.TickType.values().length + " elements");
      }

      long[] var5 = this.getSystemCpuLoadTicks();
      long var6 = 0L;

      for (int var8 = 0; var8 < var5.length; var8++) {
         var6 += var5[var8] - var1[var8];
      }

      long var10 = var5[CentralProcessor.TickType.IDLE.getIndex()]
         + var5[CentralProcessor.TickType.IOWAIT.getIndex()]
         - var1[CentralProcessor.TickType.IDLE.getIndex()]
         - var1[CentralProcessor.TickType.IOWAIT.getIndex()];
      return var6 > 0L && var10 >= 0L ? (double)(var6 - var10) / var6 : 0.0;
   }

   @Override
   public double[] getProcessorCpuLoadBetweenTicks(long[][] var1) {
      if (var1.length == this.logicalProcessorCount && var1[0].length == CentralProcessor.TickType.values().length) {
         long[][] var5 = this.getProcessorCpuLoadTicks();
         double[] var6 = new double[this.logicalProcessorCount];

         for (int var7 = 0; var7 < this.logicalProcessorCount; var7++) {
            long var8 = 0L;

            for (int var10 = 0; var10 < var5[var7].length; var10++) {
               var8 += var5[var7][var10] - var1[var7][var10];
            }

            long var12 = var5[var7][CentralProcessor.TickType.IDLE.getIndex()]
               + var5[var7][CentralProcessor.TickType.IOWAIT.getIndex()]
               - var1[var7][CentralProcessor.TickType.IDLE.getIndex()]
               - var1[var7][CentralProcessor.TickType.IOWAIT.getIndex()];
            var6[var7] = var8 > 0L && var12 >= 0L ? (double)(var8 - var12) / var8 : 0.0;
         }

         return var6;
      } else {
         throw new IllegalArgumentException(
            "Tick array "
               + var1.length
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

   public static String createProcessorID(String var0, String var1, String var2, String[] var3) {
      long var7 = 0L;
      long var9 = ParseUtil.parseLongOrDefault(var0, 0L);
      long var11 = ParseUtil.parseLongOrDefault(var1, 0L);
      long var13 = ParseUtil.parseLongOrDefault(var2, 0L);
      var7 |= var9 & 15L;
      var7 |= (var11 & 15L) << 4;
      var7 |= (var11 & 240L) << 16;
      var7 |= (var13 & 15L) << 8;
      var7 |= (var13 & 240L) << 20;

      for (String var18 : var3) {
         switch (var18) {
            case "fpu":
               var7 |= 4294967296L;
               break;
            case "vme":
               var7 |= 8589934592L;
               break;
            case "de":
               var7 |= 17179869184L;
               break;
            case "pse":
               var7 |= 34359738368L;
               break;
            case "tsc":
               var7 |= 68719476736L;
               break;
            case "msr":
               var7 |= 137438953472L;
               break;
            case "pae":
               var7 |= 274877906944L;
               break;
            case "mce":
               var7 |= 549755813888L;
               break;
            case "cx8":
               var7 |= 1099511627776L;
               break;
            case "apic":
               var7 |= 2199023255552L;
               break;
            case "sep":
               var7 |= 8796093022208L;
               break;
            case "mtrr":
               var7 |= 17592186044416L;
               break;
            case "pge":
               var7 |= 35184372088832L;
               break;
            case "mca":
               var7 |= 70368744177664L;
               break;
            case "cmov":
               var7 |= 140737488355328L;
               break;
            case "pat":
               var7 |= 281474976710656L;
               break;
            case "pse-36":
               var7 |= 562949953421312L;
               break;
            case "psn":
               var7 |= 1125899906842624L;
               break;
            case "clfsh":
               var7 |= 2251799813685248L;
               break;
            case "ds":
               var7 |= 9007199254740992L;
               break;
            case "acpi":
               var7 |= 18014398509481984L;
               break;
            case "mmx":
               var7 |= 36028797018963968L;
               break;
            case "fxsr":
               var7 |= 72057594037927936L;
               break;
            case "sse":
               var7 |= 144115188075855872L;
               break;
            case "sse2":
               var7 |= 288230376151711744L;
               break;
            case "ss":
               var7 |= 576460752303423488L;
               break;
            case "htt":
               var7 |= 1152921504606846976L;
               break;
            case "tm":
               var7 |= 2305843009213693952L;
               break;
            case "ia64":
               var7 |= 4611686018427387904L;
               break;
            case "pbe":
               var7 |= Long.MIN_VALUE;
         }
      }

      return String.format("%016X", var7);
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder(this.getName());
      var3.append("\n ").append(this.getPhysicalPackageCount()).append(" physical CPU package(s)");
      var3.append("\n ").append(this.getPhysicalProcessorCount()).append(" physical CPU core(s)");
      var3.append("\n ").append(this.getLogicalProcessorCount()).append(" logical CPU(s)");
      var3.append('\n').append("Identifier: ").append(this.getProcessorIdentifier().getIdentifier());
      var3.append('\n').append("ProcessorID: ").append(this.getProcessorIdentifier().getProcessorID());
      var3.append('\n').append("Microarchitecture: ").append(this.getProcessorIdentifier().getMicroarchitecture());
      return var3.toString();
   }
}
