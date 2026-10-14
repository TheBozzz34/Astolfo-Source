package oshi.hardware.common;

import java.util.Arrays;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.Sensors;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractSensors implements Sensors {
   private final Supplier<Double> cpuTemperature = Memoizer.memoize(this::queryCpuTemperature, Memoizer.defaultExpiration());
   private final Supplier<int[]> fanSpeeds = Memoizer.memoize(this::queryFanSpeeds, Memoizer.defaultExpiration());
   private final Supplier<Double> cpuVoltage = Memoizer.memoize(this::queryCpuVoltage, Memoizer.defaultExpiration());
   private static final long a = 3265578550147L;

   @Override
   public double getCpuTemperature() {
      return this.cpuTemperature.get();
   }

   public abstract double queryCpuTemperature();

   @Override
   public int[] getFanSpeeds() {
      return this.fanSpeeds.get();
   }

   public abstract int[] queryFanSpeeds();

   @Override
   public double getCpuVoltage() {
      return this.cpuVoltage.get();
   }

   public abstract double queryCpuVoltage();

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("CPU Temperature=").append(this.getCpuTemperature()).append("°C, ");
      stringBuilder.append("Fan Speeds=").append(Arrays.toString(this.getFanSpeeds())).append(", ");
      stringBuilder.append("CPU Voltage=").append(this.getCpuVoltage());
      return stringBuilder.toString();
   }
}
