package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;

@Immutable
public abstract class AbstractBaseboard implements Baseboard {
   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("manufacturer=").append(this.getManufacturer()).append(", ");
      stringBuilder.append("model=").append(this.getModel()).append(", ");
      stringBuilder.append("version=").append(this.getVersion()).append(", ");
      stringBuilder.append("serial number=").append(this.getSerialNumber());
      return stringBuilder.toString();
   }
}
