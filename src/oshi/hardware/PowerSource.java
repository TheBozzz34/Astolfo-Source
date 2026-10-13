package oshi.hardware;

import java.time.LocalDate;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface PowerSource {
   String getName();

   String getDeviceName();

   @Deprecated
   double t();

   double getRemainingCapacityPercent();

   @Deprecated
   double l();

   double getTimeRemainingEstimated();

   double getTimeRemainingInstant();

   double getPowerUsageRate();

   double getVoltage();

   double getAmperage();

   boolean isPowerOnLine();

   boolean isCharging();

   boolean isDischarging();

   PowerSource.CapacityUnits getCapacityUnits();

   int getCurrentCapacity();

   int getMaxCapacity();

   int getDesignCapacity();

   int getCycleCount();

   String getChemistry();

   LocalDate getManufactureDate();

   String getManufacturer();

   String getSerialNumber();

   double getTemperature();

   boolean updateAttributes();

   enum CapacityUnits {
      MWH,
      MAH,
      RELATIVE;
   }
}
