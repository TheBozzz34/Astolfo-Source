package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;

@Immutable
public abstract class AbstractBaseboard implements Baseboard {
   private static final long a = 17247668159673L;

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append("manufacturer=").append(this.p()).append(", ");
      var3.append("model=").append(this.g()).append(", ");
      var3.append("version=").append(this.W()).append(", ");
      var3.append("serial number=").append(this.A());
      return var3.toString();
   }
}
