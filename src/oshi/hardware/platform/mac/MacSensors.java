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
      IOConnect var4 = SmcUtil.smcOpen();
      double var5 = SmcUtil.smcGetFloat(var4, "TC0P");
      SmcUtil.smcClose(var4);
      return var5 > 0.0 ? var5 : 0.0;
   }

   @Override
   public int[] queryFanSpeeds() {
      IOConnect var4 = SmcUtil.smcOpen();
      if (this.numFans == 0) {
         this.numFans = (int)SmcUtil.smcGetLong(var4, "FNum");
      }

      int[] var5 = new int[this.numFans];

      for (int var6 = 0; var6 < this.numFans; var6++) {
         var5[var6] = (int)SmcUtil.smcGetFloat(var4, String.format("F%dAc", var6));
      }

      SmcUtil.smcClose(var4);
      return var5;
   }

   @Override
   public double queryCpuVoltage() {
      IOConnect var3 = SmcUtil.smcOpen();
      double var4 = SmcUtil.smcGetFloat(var3, "VC0C") / 1000.0;
      SmcUtil.smcClose(var3);
      return var4;
   }
}
