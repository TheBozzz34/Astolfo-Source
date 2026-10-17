package oshi.hardware.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.util.FormatUtil;

@ThreadSafe
public abstract class AbstractVirtualMemory implements VirtualMemory {
   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Used: ");
      stringBuilder.append(FormatUtil.formatBytes(this.getSwapUsed()));
      stringBuilder.append("/");
      stringBuilder.append(FormatUtil.formatBytes(this.getSwapTotal()));
      return stringBuilder.toString();
   }
}
