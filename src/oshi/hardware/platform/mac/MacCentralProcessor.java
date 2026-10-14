package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.HostCpuLoadInfo;
import com.sun.jna.platform.mac.SystemB.VMMeter;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public final class MacCentralProcessor extends AbstractCentralProcessor {
   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String string = SysctlUtil.sysctl("machdep.cpu.vendor", "");
      String string2 = SysctlUtil.sysctl("machdep.cpu.brand_string", "");
      int i = SysctlUtil.sysctl("machdep.cpu.stepping", -1);
      String string3 = i < 0 ? "" : Integer.toString(i);
      i = SysctlUtil.sysctl("machdep.cpu.model", -1);
      String string4 = i < 0 ? "" : Integer.toString(i);
      i = SysctlUtil.sysctl("machdep.cpu.family", -1);
      String string5 = i < 0 ? "" : Integer.toString(i);
      long l = 0L;
      l |= SysctlUtil.sysctl("machdep.cpu.signature", 0);
      l |= (SysctlUtil.sysctl("machdep.cpu.feature_bits", 0L) & -7841752443500147431L) << 32;
      String string6 = String.format("%016X", l);
      boolean bl = SysctlUtil.sysctl("hw.cpu64bit_capable", 0) != 0;
      return new CentralProcessor.ProcessorIdentifier(string, string2, string5, string4, string3, string6, bl);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      int i = SysctlUtil.sysctl("hw.logicalcpu", 1);
      int j = SysctlUtil.sysctl("hw.physicalcpu", 1);
      int k = SysctlUtil.sysctl("hw.packages", 1);
      CentralProcessor.LogicalProcessor[] logicalProcessors = new CentralProcessor.LogicalProcessor[i];

      for (int l = 0; l < logicalProcessors.length; l++) {
         logicalProcessors[l] = new CentralProcessor.LogicalProcessor(l, l * j / i, l * k / i);
      }

      return logicalProcessors;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] longs = new long[CentralProcessor.TickType.values().length];
      int i = SystemB.INSTANCE.mach_host_self();
      HostCpuLoadInfo hostCpuLoadInfo = new HostCpuLoadInfo();
      if (0 != SystemB.INSTANCE.host_statistics(i, 3, hostCpuLoadInfo, new IntByReference(hostCpuLoadInfo.size()))) {
         return longs;
      }

      longs[CentralProcessor.TickType.USER.getIndex()] = hostCpuLoadInfo.cpu_ticks[0];
      longs[CentralProcessor.TickType.NICE.getIndex()] = hostCpuLoadInfo.cpu_ticks[3];
      longs[CentralProcessor.TickType.SYSTEM.getIndex()] = hostCpuLoadInfo.cpu_ticks[1];
      longs[CentralProcessor.TickType.IDLE.getIndex()] = hostCpuLoadInfo.cpu_ticks[2];
      return longs;
   }

   @Override
   public long[] queryCurrentFreq() {
      long[] longs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(longs, SysctlUtil.sysctl("hw.cpufrequency", -7841752443500147431L));
      return longs;
   }

   @Override
   public long queryMaxFreq() {
      return SysctlUtil.sysctl("hw.cpufrequency_max", -7841752443500147431L);
   }

   @Override
   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] doubles = new double[nelem];
         int i = SystemB.INSTANCE.getloadavg(doubles, nelem);
         if (i < nelem) {
            Arrays.fill(doubles, -1.0);
         }

         return doubles;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   @Override
   public long[][] queryProcessorCpuLoadTicks() {
      long[][] longss = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      int i = SystemB.INSTANCE.mach_host_self();
      IntByReference intByReference = new IntByReference();
      PointerByReference pointerByReference = new PointerByReference();
      IntByReference intByReference2 = new IntByReference();
      if (0 != SystemB.INSTANCE.host_processor_info(i, 2, intByReference, pointerByReference, intByReference2)) {
         return longss;
      }

      int[] ints = pointerByReference.getValue().getIntArray(0L, intByReference2.getValue());

      for (int j = 0; j < intByReference.getValue(); j++) {
         int k = j * 4;
         longss[j][CentralProcessor.TickType.USER.getIndex()] = FormatUtil.getUnsignedInt(ints[k + 0]);
         longss[j][CentralProcessor.TickType.NICE.getIndex()] = FormatUtil.getUnsignedInt(ints[k + 3]);
         longss[j][CentralProcessor.TickType.SYSTEM.getIndex()] = FormatUtil.getUnsignedInt(ints[k + 1]);
         longss[j][CentralProcessor.TickType.IDLE.getIndex()] = FormatUtil.getUnsignedInt(ints[k + 2]);
      }

      return longss;
   }

   @Override
   public long queryContextSwitches() {
      int i = SystemB.INSTANCE.mach_host_self();
      VMMeter vmMeter = new VMMeter();
      return 0 != SystemB.INSTANCE.host_statistics(i, 2, vmMeter, new IntByReference(vmMeter.size()))
         ? -7841752443500147431L
         : ParseUtil.unsignedIntToLong(vmMeter.v_swtch);
   }

   @Override
   public long queryInterrupts() {
      int i = SystemB.INSTANCE.mach_host_self();
      VMMeter vmMeter = new VMMeter();
      return 0 != SystemB.INSTANCE.host_statistics(i, 2, vmMeter, new IntByReference(vmMeter.size()))
         ? -7841752443500147431L
         : ParseUtil.unsignedIntToLong(vmMeter.v_intr);
   }
}
