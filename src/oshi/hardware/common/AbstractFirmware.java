package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Firmware;

@Immutable
public abstract class AbstractFirmware implements Firmware {
   @Override
   public String getName() {
      return "unknown";
   }

   @Override
   public String getDescription() {
      return "unknown";
   }

   @Override
   public String getReleaseDate() {
      return "unknown";
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("manufacturer=").append(this.getManufacturer()).append(", ");
      stringBuilder.append("name=").append(this.getName()).append(", ");
      stringBuilder.append("description=").append(this.getDescription()).append(", ");
      stringBuilder.append("version=").append(this.getVersion()).append(", ");
      stringBuilder.append("release date=").append(this.getReleaseDate() == null ? "unknown" : this.getReleaseDate());
      return stringBuilder.toString();
   }
}
