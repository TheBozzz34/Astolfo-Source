package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;

@Immutable
public abstract class AbstractBaseboard implements Baseboard {
   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("manufacturer=").append(this.p()).append(", ");
      stringBuilder.append("model=").append(this.g()).append(", ");
      stringBuilder.append("version=").append(this.W()).append(", ");
      stringBuilder.append("serial number=").append(this.A());
      return stringBuilder.toString();
   }
}
