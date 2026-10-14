package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;

@ThreadSafe
public final class FreeBsdSensors extends AbstractSensors {
   @Override
   public double queryCpuTemperature() {
      return queryKldloadCoretemp();
   }

   private static double queryKldloadCoretemp() {
      String string = "dev.cpu.%d.temperature";
      IntByReference intByReference = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory memory = new Memory(intByReference.getValue());
      int i = 0;
      double e = 0.0;

      while (0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(string, i), memory, intByReference, null, 0)) {
         e += memory.getInt(0L) / 10.0 - 273.15;
         i++;
      }

      return i > 0 ? e / i : Double.NaN;
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
