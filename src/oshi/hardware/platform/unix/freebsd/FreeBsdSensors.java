package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;

@ThreadSafe
public final class FreeBsdSensors extends AbstractSensors {
   private static final long b = 26308654529878L;

   @Override
   public double queryCpuTemperature() {
      return queryKldloadCoretemp();
   }

   private static double queryKldloadCoretemp() {
      String var3 = "dev.cpu.%d.temperature";
      IntByReference var4 = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory var5 = new Memory(var4.getValue());
      int var6 = 0;
      double var7 = 0.0;

      while (0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(var3, var6), var5, var4, null, 0)) {
         var7 += var5.getInt(0L) / 10.0 - 273.15;
         var6++;
      }

      return var6 > 0 ? var7 / var6 : Double.NaN;
   }

   @Override
   public int[] queryFanSpeeds() {
      return new int[0];
   }

   @Override
   public double queryCpuVoltage() {
      return 0.0;
   }
}
