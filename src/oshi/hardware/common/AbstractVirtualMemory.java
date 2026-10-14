package oshi.hardware.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.util.FormatUtil;

@ThreadSafe
public abstract class AbstractVirtualMemory implements VirtualMemory {
   private static final long e = 118731116964533L;

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Used: ");
      stringBuilder.append(FormatUtil.formatBytes(this.y()));
      stringBuilder.append("/");
      stringBuilder.append(FormatUtil.formatBytes(this.n()));
      return stringBuilder.toString();
   }
}
