package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class LinuxDisplay extends AbstractDisplay {
   private static final long a = 124581279143371L;

   public LinuxDisplay(byte[] var1) {
      super(var1);
   }

   public static Display[] getDisplays() {
      List var3 = ExecutingCommand.runNative("xrandr --verbose");
      if (var3.isEmpty()) {
         return new Display[0];
      }

      ArrayList var4 = new ArrayList();
      StringBuilder var5 = null;

      for (Object var7$raw : var3) {
         String var7 = (String)var7$raw;
         if (var7.contains("EDID")) {
            var5 = new StringBuilder();
         } else if (var5 != null) {
            var5.append(var7.trim());
            if (var5.length() >= 256) {
               String var8 = var5.toString();
               byte[] var9 = ParseUtil.hexStringToByteArray(var8);
               if (var9.length >= 128) {
                  var4.add(new LinuxDisplay(var9));
               }

               var5 = null;
            }
         }
      }

      return (oshi.hardware.Display[])(var4.toArray(new Display[0]));
   }
}
