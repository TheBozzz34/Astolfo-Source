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
   private static final long c = 119714965384373L;
   private static final long j = -7979277928561115104L;

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String var4 = SysctlUtil.sysctl("machdep.cpu.vendor", "");
      String var5 = SysctlUtil.sysctl("machdep.cpu.brand_string", "");
      int var6 = SysctlUtil.sysctl("machdep.cpu.stepping", -1);
      String var7 = var6 < 0 ? "" : Integer.toString(var6);
      var6 = SysctlUtil.sysctl("machdep.cpu.model", -1);
      String var8 = var6 < 0 ? "" : Integer.toString(var6);
      var6 = SysctlUtil.sysctl("machdep.cpu.family", -1);
      String var9 = var6 < 0 ? "" : Integer.toString(var6);
      long var10 = 0L;
      var10 |= SysctlUtil.sysctl("machdep.cpu.signature", 0);
      var10 |= (SysctlUtil.sysctl("machdep.cpu.feature_bits", 0L) & -7841752443500147431L) << (int)j;
      String var12 = String.format("%016X", var10);
      boolean var13 = SysctlUtil.sysctl("hw.cpu64bit_capable", 0) != 0;
      return new CentralProcessor.ProcessorIdentifier(var4, var5, var9, var8, var7, var12, var13);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      int var4 = SysctlUtil.sysctl("hw.logicalcpu", 1);
      int var5 = SysctlUtil.sysctl("hw.physicalcpu", 1);
      int var6 = SysctlUtil.sysctl("hw.packages", 1);
      CentralProcessor.LogicalProcessor[] var7 = new CentralProcessor.LogicalProcessor[var4];

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] = new CentralProcessor.LogicalProcessor(var8, var8 * var5 / var4, var8 * var6 / var4);
      }

      return var7;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] var4 = new long[CentralProcessor.TickType.values().length];
      int var5 = SystemB.INSTANCE.mach_host_self();
      HostCpuLoadInfo var6 = new HostCpuLoadInfo();
      if (0 != SystemB.INSTANCE.host_statistics(var5, 3, var6, new IntByReference(var6.size()))) {
         return var4;
      }

      var4[CentralProcessor.TickType.USER.getIndex()] = var6.cpu_ticks[0];
      var4[CentralProcessor.TickType.NICE.getIndex()] = var6.cpu_ticks[3];
      var4[CentralProcessor.TickType.SYSTEM.getIndex()] = var6.cpu_ticks[1];
      var4[CentralProcessor.TickType.IDLE.getIndex()] = var6.cpu_ticks[2];
      return var4;
   }

   @Override
   public long[] queryCurrentFreq() {
      long[] var3 = new long[this.getLogicalProcessorCount()];
      Arrays.fill(var3, SysctlUtil.sysctl("hw.cpufrequency", -7841752443500147431L));
      return var3;
   }

   @Override
   public long queryMaxFreq() {
      return SysctlUtil.sysctl("hw.cpufrequency_max", -7841752443500147431L);
   }

   @Override
   public double[] getSystemLoadAverage(int var1) {
      if (var1 >= 1 && var1 <= 3) {
         double[] var5 = new double[var1];
         int var6 = SystemB.INSTANCE.getloadavg(var5, var1);
         if (var6 < var1) {
            Arrays.fill(var5, -1.0);
         }

         return var5;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   @Override
   public long[][] queryProcessorCpuLoadTicks() {
      long[][] var4 = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      int var5 = SystemB.INSTANCE.mach_host_self();
      IntByReference var6 = new IntByReference();
      PointerByReference var7 = new PointerByReference();
      IntByReference var8 = new IntByReference();
      if (0 != SystemB.INSTANCE.host_processor_info(var5, 2, var6, var7, var8)) {
         return var4;
      }

      int[] var9 = var7.getValue().getIntArray(0L, var8.getValue());

      for (int var10 = 0; var10 < var6.getValue(); var10++) {
         int var11 = var10 * 4;
         var4[var10][CentralProcessor.TickType.USER.getIndex()] = FormatUtil.getUnsignedInt(var9[var11 + 0]);
         var4[var10][CentralProcessor.TickType.NICE.getIndex()] = FormatUtil.getUnsignedInt(var9[var11 + 3]);
         var4[var10][CentralProcessor.TickType.SYSTEM.getIndex()] = FormatUtil.getUnsignedInt(var9[var11 + 1]);
         var4[var10][CentralProcessor.TickType.IDLE.getIndex()] = FormatUtil.getUnsignedInt(var9[var11 + 2]);
      }

      return var4;
   }

   @Override
   public long queryContextSwitches() {
      int var4 = SystemB.INSTANCE.mach_host_self();
      VMMeter var5 = new VMMeter();
      return 0 != SystemB.INSTANCE.host_statistics(var4, 2, var5, new IntByReference(var5.size()))
         ? -7841752443500147431L
         : ParseUtil.unsignedIntToLong(var5.v_swtch);
   }

   @Override
   public long queryInterrupts() {
      int var4 = SystemB.INSTANCE.mach_host_self();
      VMMeter var5 = new VMMeter();
      return 0 != SystemB.INSTANCE.host_statistics(var4, 2, var5, new IntByReference(var5.size()))
         ? -7841752443500147431L
         : ParseUtil.unsignedIntToLong(var5.v_intr);
   }
}
