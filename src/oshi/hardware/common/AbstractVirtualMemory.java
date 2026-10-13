package oshi.hardware.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.util.FormatUtil;

@ThreadSafe
public abstract class AbstractVirtualMemory implements VirtualMemory {
   private static final long e = 118731116964533L;

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append("Used: ");
      var3.append(FormatUtil.formatBytes(this.y()));
      var3.append("/");
      var3.append(FormatUtil.formatBytes(this.n()));
      return var3.toString();
   }
}
