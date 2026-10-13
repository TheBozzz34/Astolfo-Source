package oshi.hardware;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
public interface CentralProcessor {
   CentralProcessor.ProcessorIdentifier getProcessorIdentifier();

   @Deprecated
   String P();

   @Deprecated
   String getName();

   @Deprecated
   String v();

   @Deprecated
   String Z();

   @Deprecated
   String y();

   @Deprecated
   String R();

   @Deprecated
   String w();

   @Deprecated
   boolean isCpu64bit();

   @Deprecated
   long g();

   long K();

   long[] o();

   CentralProcessor.LogicalProcessor[] getLogicalProcessors();

   double getSystemCpuLoadBetweenTicks(long[] var1);

   long[] getSystemCpuLoadTicks();

   double[] getSystemLoadAverage(int var1);

   double[] getProcessorCpuLoadBetweenTicks(long[][] var1);

   long[][] getProcessorCpuLoadTicks();

   int getLogicalProcessorCount();

   int getPhysicalProcessorCount();

   int getPhysicalPackageCount();

   long Q();

   long m();

   class LogicalProcessor {
      private final int processorNumber;
      private final int physicalProcessorNumber;
      private final int physicalPackageNumber;
      private final int numaNode;
      private final int processorGroup;

      public LogicalProcessor(int var1, int var2, int var3) {
         this(var1, var2, var3, 0, 0);
      }

      public LogicalProcessor(int var1, int var2, int var3, int var4) {
         this(var1, var2, var3, var4, 0);
      }

      public LogicalProcessor(int var1, int var2, int var3, int var4, int var5) {
         this.processorNumber = var1;
         this.physicalProcessorNumber = var2;
         this.physicalPackageNumber = var3;
         this.numaNode = var4;
         this.processorGroup = var5;
      }

      public int getProcessorNumber() {
         return this.processorNumber;
      }

      public int getPhysicalProcessorNumber() {
         return this.physicalProcessorNumber;
      }

      public int getPhysicalPackageNumber() {
         return this.physicalPackageNumber;
      }

      public int getNumaNode() {
         return this.numaNode;
      }

      public int z() {
         return this.processorGroup;
      }
   }

   final class ProcessorIdentifier {
      private final String cpuVendor;
      private final String cpuName;
      private final String cpuFamily;
      private final String cpuModel;
      private final String cpuStepping;
      private final String processorID;
      private final String cpuIdentifier;
      private final boolean cpu64bit;
      private final long cpuVendorFreq;
      private final Supplier<String> microArchictecture;
      private static final long a = 83851945464550L;

      public ProcessorIdentifier(String var1, String var2, String var3, String var4, String var5, String var6, boolean var7) {
         long var8 = a ^ 56485931112025L;
         this.microArchictecture = Memoizer.memoize(this::h);
         this.cpuVendor = var1;
         this.cpuName = var2;
         this.cpuFamily = var3;
         this.cpuModel = var4;
         this.cpuStepping = var5;
         this.processorID = var6;
         this.cpu64bit = var7;
         StringBuilder var11 = new StringBuilder();
         if (var1.contentEquals("GenuineIntel")) {
            var11.append(var7 ? "Intel64" : "x86");
         } else {
            var11.append(var1);
         }

         var11.append(" Family ").append(var3);
         var11.append(" Model ").append(var4);
         var11.append(" Stepping ").append(var5);
         this.cpuIdentifier = var11.toString();
         long var10000 = 7486698780836764118L ^ var8;
         Pattern var12 = Pattern.compile("@ (.*)$");
         Matcher var13 = var12.matcher(var2);
         if (var13.find()) {
            String var14 = var13.group(1);
            this.cpuVendorFreq = ParseUtil.parseHertz(var14);
         } else {
            this.cpuVendorFreq = -1L;
         }
      }

      public String H() {
         return this.cpuVendor;
      }

      public String O() {
         return this.cpuName;
      }

      public String d() {
         return this.cpuFamily;
      }

      public String V() {
         return this.cpuModel;
      }

      public String r() {
         return this.cpuStepping;
      }

      public String getProcessorID() {
         return this.processorID;
      }

      public String getIdentifier() {
         return this.cpuIdentifier;
      }

      public boolean isCpu64bit() {
         return this.cpu64bit;
      }

      public long getVendorFreq() {
         return this.cpuVendorFreq;
      }

      public String getMicroarchitecture() {
         return this.microArchictecture.get();
      }

      private String h() {
         return "unknown";
      }

      @Override
      public String toString() {
         return this.getIdentifier();
      }
   }

   enum TickType {
      USER(0),
      NICE(1),
      SYSTEM(2),
      IDLE(3),
      IOWAIT(4),
      IRQ(5),
      SOFTIRQ(6),
      STEAL(7);

      private int index;

      TickType(int var3) {
         this.index = var3;
      }

      public int getIndex() {
         return this.index;
      }
   }
}
