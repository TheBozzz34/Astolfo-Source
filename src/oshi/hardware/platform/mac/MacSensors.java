package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKit.IOConnect;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.platform.mac.SmcUtil;

@ThreadSafe
public final class MacSensors extends AbstractSensors {
   private int numFans = 0;
   private static final long b = 91761440604788L;

   @Override
   public double queryCpuTemperature() {
      IOConnect ioConnect = SmcUtil.smcOpen();
      double d = SmcUtil.smcGetFloat(ioConnect, "TC0P");
      SmcUtil.smcClose(ioConnect);
      return d > 0.0 ? d : 0.0;
   }

   @Override
   public int[] queryFanSpeeds() {
      IOConnect ioConnect = SmcUtil.smcOpen();
      if (this.numFans == 0) {
         this.numFans = (int)SmcUtil.smcGetLong(ioConnect, "FNum");
      }

      int[] ints = new int[this.numFans];

      for (int i = 0; i < this.numFans; i++) {
         ints[i] = (int)SmcUtil.smcGetFloat(ioConnect, String.format("F%dAc", i));
      }

      SmcUtil.smcClose(ioConnect);
      return ints;
   }

   @Override
   public double queryCpuVoltage() {
      IOConnect ioConnect = SmcUtil.smcOpen();
      double d = SmcUtil.smcGetFloat(ioConnect, "VC0C") / 1000.0;
      SmcUtil.smcClose(ioConnect);
      return d;
   }
}
