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
   String getVendor();

   @Deprecated
   String getName();

   @Deprecated
   String getFamily();

   @Deprecated
   String getModel();

   @Deprecated
   String y();

   @Deprecated
   String R();

   @Deprecated
   String getIdentifier();

   @Deprecated
   boolean isCpu64bit();

   @Deprecated
   long getVendorFreq();

   long getMaxFreq();

   long[] getCurrentFreq();

   CentralProcessor.LogicalProcessor[] getLogicalProcessors();

   double getSystemCpuLoadBetweenTicks(long[] longs);

   long[] getSystemCpuLoadTicks();

   double[] getSystemLoadAverage(int i);

   double[] getProcessorCpuLoadBetweenTicks(long[][] longs);

   long[][] getProcessorCpuLoadTicks();

   int getLogicalProcessorCount();

   int getPhysicalProcessorCount();

   int getPhysicalPackageCount();

   long getContextSwitches();

   long getInterrupts();

   class LogicalProcessor {
      private final int processorNumber;
      private final int physicalProcessorNumber;
      private final int physicalPackageNumber;
      private final int numaNode;
      private final int processorGroup;

      public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber) {
         this(processorNumber, physicalProcessorNumber, physicalPackageNumber, 0, 0);
      }

      public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber, int numaNode) {
         this(processorNumber, physicalProcessorNumber, physicalPackageNumber, numaNode, 0);
      }

      public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber, int numaNode, int processorGroup) {
         this.processorNumber = processorNumber;
         this.physicalProcessorNumber = physicalProcessorNumber;
         this.physicalPackageNumber = physicalPackageNumber;
         this.numaNode = numaNode;
         this.processorGroup = processorGroup;
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

      public int getProcessorGroup() {
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
      private final Supplier<String> microArchictecture = Memoizer.memoize(this::queryMicroarchitecture);

      public ProcessorIdentifier(String cpuVendor, String cpuName, String cpuFamily, String cpuModel, String cpuStepping, String processorID, boolean cpu64bit) {
         this.cpuVendor = cpuVendor;
         this.cpuName = cpuName;
         this.cpuFamily = cpuFamily;
         this.cpuModel = cpuModel;
         this.cpuStepping = cpuStepping;
         this.processorID = processorID;
         this.cpu64bit = cpu64bit;
         StringBuilder stringBuilder = new StringBuilder();
         if (cpuVendor.contentEquals("GenuineIntel")) {
            stringBuilder.append(cpu64bit ? "Intel64" : "x86");
         } else {
            stringBuilder.append(cpuVendor);
         }

         stringBuilder.append(" Family ").append(cpuFamily);
         stringBuilder.append(" Model ").append(cpuModel);
         stringBuilder.append(" Stepping ").append(cpuStepping);
         this.cpuIdentifier = stringBuilder.toString();
         Pattern pattern = Pattern.compile("@ (.*)$");
         Matcher matcher2 = pattern.matcher(cpuName);
         if (matcher2.find()) {
            String string = matcher2.group(1);
            this.cpuVendorFreq = ParseUtil.parseHertz(string);
         } else {
            this.cpuVendorFreq = -1L;
         }
      }

      public String getVendor() {
         return this.cpuVendor;
      }

      public String getName() {
         return this.cpuName;
      }

      public String getFamily() {
         return this.cpuFamily;
      }

      public String getModel() {
         return this.cpuModel;
      }

      public String getStepping() {
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

      private String queryMicroarchitecture() {
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

      TickType(int value) {
         this.index = value;
      }

      public int getIndex() {
         return this.index;
      }
   }
}
