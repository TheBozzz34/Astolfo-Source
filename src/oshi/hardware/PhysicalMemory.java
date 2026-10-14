package oshi.hardware;

import oshi.annotation.concurrent.Immutable;
import oshi.util.FormatUtil;

@Immutable
public class PhysicalMemory {
   private final String bankLabel;
   private final long capacity;
   private final long clockSpeed;
   private final String manufacturer;
   private final String memoryType;

   public PhysicalMemory(String bankLabel, long capacity, long clockSpeed, String manufacturer, String memoryType) {
      this.bankLabel = bankLabel;
      this.capacity = capacity;
      this.clockSpeed = clockSpeed;
      this.manufacturer = manufacturer;
      this.memoryType = memoryType;
   }

   public String getBankLabel() {
      return this.bankLabel;
   }

   public long getCapacity() {
      return this.capacity;
   }

   public long getClockSpeed() {
      return this.clockSpeed;
   }

   public String getManufacturer() {
      return this.manufacturer;
   }

   public String getMemoryType() {
      return this.memoryType;
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Bank label: " + this.getBankLabel());
      stringBuilder.append(", Capacity: " + FormatUtil.formatBytes(this.getCapacity()));
      stringBuilder.append(", Clock speed: " + FormatUtil.formatHertz(this.getClockSpeed()));
      stringBuilder.append(", Manufacturer: " + this.getManufacturer());
      stringBuilder.append(", Memory type: " + this.getMemoryType());
      return stringBuilder.toString();
   }
}
