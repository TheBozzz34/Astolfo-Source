package oshi.hardware.common;

import com.sun.jna.Platform;
import java.time.LocalDate;
import oshi.SystemInfo;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.platform.linux.LinuxPowerSource;
import oshi.hardware.platform.mac.MacPowerSource;
import oshi.hardware.platform.unix.freebsd.FreeBsdPowerSource;
import oshi.hardware.platform.unix.solaris.SolarisPowerSource;
import oshi.hardware.platform.windows.WindowsPowerSource;

@ThreadSafe
public abstract class AbstractPowerSource implements PowerSource {
   private String name;
   private String deviceName;
   private double remainingCapacityPercent;
   private double timeRemainingEstimated;
   private double timeRemainingInstant;
   private double powerUsageRate;
   private double voltage;
   private double amperage;
   private boolean powerOnLine;
   private boolean charging;
   private boolean discharging;
   private PowerSource.CapacityUnits capacityUnits;
   private int currentCapacity;
   private int maxCapacity;
   private int designCapacity;
   private int cycleCount;
   private String chemistry;
   private LocalDate manufactureDate;
   private String manufacturer;
   private String serialNumber;
   private double temperature;
   private static final long b = 59213222441595L;

   public AbstractPowerSource(
      String var1,
      String var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      boolean var15,
      boolean var16,
      boolean var17,
      PowerSource.CapacityUnits var18,
      int var19,
      int var20,
      int var21,
      int var22,
      String var23,
      LocalDate var24,
      String var25,
      String var26,
      double var27
   ) {
      this.name = var1;
      this.deviceName = var2;
      this.remainingCapacityPercent = var3;
      this.timeRemainingEstimated = var5;
      this.timeRemainingInstant = var7;
      this.powerUsageRate = var9;
      this.voltage = var11;
      this.amperage = var13;
      this.powerOnLine = var15;
      this.charging = var16;
      this.discharging = var17;
      this.capacityUnits = var18;
      this.currentCapacity = var19;
      this.maxCapacity = var20;
      this.designCapacity = var21;
      this.cycleCount = var22;
      this.chemistry = var23;
      this.manufactureDate = var24;
      this.manufacturer = var25;
      this.serialNumber = var26;
      this.temperature = var27;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getDeviceName() {
      return this.deviceName;
   }

   @Override
   public double t() {
      return this.getRemainingCapacityPercent();
   }

   @Override
   public double getRemainingCapacityPercent() {
      return this.remainingCapacityPercent;
   }

   @Override
   public double l() {
      return this.getTimeRemainingEstimated();
   }

   @Override
   public double getTimeRemainingEstimated() {
      return this.timeRemainingEstimated;
   }

   @Override
   public double getTimeRemainingInstant() {
      return this.timeRemainingInstant;
   }

   @Override
   public double getPowerUsageRate() {
      return this.powerUsageRate;
   }

   @Override
   public double getVoltage() {
      return this.voltage;
   }

   @Override
   public double getAmperage() {
      return this.amperage;
   }

   @Override
   public boolean isPowerOnLine() {
      return this.powerOnLine;
   }

   @Override
   public boolean isCharging() {
      return this.charging;
   }

   @Override
   public boolean isDischarging() {
      return this.discharging;
   }

   @Override
   public PowerSource.CapacityUnits getCapacityUnits() {
      return this.capacityUnits;
   }

   @Override
   public int getCurrentCapacity() {
      return this.currentCapacity;
   }

   @Override
   public int getMaxCapacity() {
      return this.maxCapacity;
   }

   @Override
   public int getDesignCapacity() {
      return this.designCapacity;
   }

   @Override
   public int getCycleCount() {
      return this.cycleCount;
   }

   @Override
   public String getChemistry() {
      return this.chemistry;
   }

   @Override
   public LocalDate getManufactureDate() {
      return this.manufactureDate;
   }

   @Override
   public String getManufacturer() {
      return this.manufacturer;
   }

   @Override
   public String getSerialNumber() {
      return this.serialNumber;
   }

   @Override
   public double getTemperature() {
      return this.temperature;
   }

   @Override
   public boolean updateAttributes() {
      PowerSource[] var4 = getPowerSources();

      for (PowerSource var8 : var4) {
         if (var8.getName().equals(this.name)) {
            this.name = var8.getName();
            this.deviceName = var8.getDeviceName();
            this.remainingCapacityPercent = var8.getRemainingCapacityPercent();
            this.timeRemainingEstimated = var8.getTimeRemainingEstimated();
            this.timeRemainingInstant = var8.getTimeRemainingInstant();
            this.powerUsageRate = var8.getPowerUsageRate();
            this.voltage = var8.getVoltage();
            this.amperage = var8.getAmperage();
            this.powerOnLine = var8.isPowerOnLine();
            this.charging = var8.isCharging();
            this.discharging = var8.isDischarging();
            this.capacityUnits = var8.getCapacityUnits();
            this.currentCapacity = var8.getCurrentCapacity();
            this.maxCapacity = var8.getMaxCapacity();
            this.designCapacity = var8.getDesignCapacity();
            this.cycleCount = var8.getCycleCount();
            this.chemistry = var8.getChemistry();
            this.manufactureDate = var8.getManufactureDate();
            this.manufacturer = var8.getManufacturer();
            this.serialNumber = var8.getSerialNumber();
            this.temperature = var8.getTemperature();
            return true;
         }
      }

      return false;
   }

   private static PowerSource[] getPowerSources() {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case WINDOWS:
            return WindowsPowerSource.getPowerSources();
         case MACOSX:
            return MacPowerSource.Y();
         case LINUX:
            return LinuxPowerSource.x();
         case SOLARIS:
            return SolarisPowerSource.getPowerSources();
         case FREEBSD:
            return FreeBsdPowerSource.getPowerSources();
         default:
            throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
      }
   }

   @Override
   public String toString() {
      StringBuilder var5 = new StringBuilder();
      var5.append("Name: ").append(this.getName()).append(", ");
      var5.append("Device Name: ").append(this.getDeviceName()).append(",\n ");
      var5.append("RemainingCapacityPercent: ").append(this.getRemainingCapacityPercent() * 100.0).append("%, ");
      var5.append("Time Remaining: ").append(formatTimeRemaining(this.getTimeRemainingEstimated())).append(", ");
      var5.append("Time Remaining Instant: ").append(formatTimeRemaining(this.getTimeRemainingInstant())).append(",\n ");
      var5.append("Power Usage Rate: ").append(this.getPowerUsageRate()).append("mW, ");
      var5.append("Voltage: ").append(this.getVoltage()).append("V, ");
      var5.append("Amperage: ").append(this.getAmperage()).append("mA,\n ");
      var5.append("Power OnLine: ").append(this.isPowerOnLine()).append(", ");
      var5.append("Charging: ").append(this.isCharging()).append(", ");
      var5.append("Discharging: ").append(this.isDischarging()).append(",\n ");
      var5.append("Capacity Units: ").append(this.getCapacityUnits()).append(", ");
      var5.append("Current Capacity: ").append(this.getCurrentCapacity()).append(", ");
      var5.append("Max Capacity: ").append(this.getMaxCapacity()).append(", ");
      var5.append("Design Capacity: ").append(this.getDesignCapacity()).append(",\n ");
      var5.append("Cycle Count: ").append(this.getCycleCount()).append(", ");
      var5.append("Chemistry: ").append(this.getChemistry()).append(", ");
      var5.append("Manufacture Date: ").append(this.getManufactureDate() != null ? this.getManufactureDate() : "unknown").append(", ");
      var5.append("Manufacturer: ").append(this.getManufacturer()).append(",\n ");
      var5.append("SerialNumber: ").append(this.getSerialNumber()).append(", ");
      var5.append("Temperature: ");
      if (this.getTemperature() > 0.0) {
         var5.append(this.getTemperature()).append("°C");
      } else {
         var5.append("unknown");
      }

      return var5.toString();
   }

   private static String formatTimeRemaining(double var0) {
      long var2 = b ^ 135901085588239L;
      String var5;
      if (var0 < -1.5) {
         var5 = "Charging";
      } else if (var0 < 0.0) {
         var5 = "Unknown";
      } else {
         int var6 = (int)(var0 / 3600.0);
         int var7 = (int)(var0 % 3600.0 / 60.0);
         long var10000 = 4148454402148095858L ^ var2;
         var5 = String.format("%d:%02d", var6, var7);
      }

      return var5;
   }
}
