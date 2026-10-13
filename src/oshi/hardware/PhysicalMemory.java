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
   private static final long a = 59422294488775L;

   public PhysicalMemory(String var1, long var2, long var4, String var6, String var7) {
      this.bankLabel = var1;
      this.capacity = var2;
      this.clockSpeed = var4;
      this.manufacturer = var6;
      this.memoryType = var7;
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
      StringBuilder var3 = new StringBuilder();
      var3.append("Bank label: " + this.getBankLabel());
      var3.append(", Capacity: " + FormatUtil.formatBytes(this.getCapacity()));
      var3.append(", Clock speed: " + FormatUtil.formatHertz(this.getClockSpeed()));
      var3.append(", Manufacturer: " + this.getManufacturer());
      var3.append(", Memory type: " + this.getMemoryType());
      return var3.toString();
   }
}
