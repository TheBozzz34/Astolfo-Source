package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Firmware;

@Immutable
public abstract class AbstractFirmware implements Firmware {
   private static final long a = 138758548213852L;

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
      StringBuilder var4 = new StringBuilder();
      var4.append("manufacturer=").append(this.R()).append(", ");
      var4.append("name=").append(this.getName()).append(", ");
      var4.append("description=").append(this.getDescription()).append(", ");
      var4.append("version=").append(this.P()).append(", ");
      var4.append("release date=").append(this.getReleaseDate() == null ? "unknown" : this.getReleaseDate());
      return var4.toString();
   }
}
