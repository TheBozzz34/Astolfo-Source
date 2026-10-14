package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class FreeBsdDisplay extends AbstractDisplay {
   public FreeBsdDisplay(byte[] edid) {
      super(edid);
   }

   public static Display[] getDisplays() {
      List list = ExecutingCommand.runNative("xrandr --verbose");
      if (list.isEmpty()) {
         return new Display[0];
      }

      ArrayList arrayList = new ArrayList();
      StringBuilder stringBuilder = null;

      for (Object object : list) {
         String string = (String)object;
         if (string.contains("EDID")) {
            stringBuilder = new StringBuilder();
         } else if (stringBuilder != null) {
            stringBuilder.append(string.trim());
            if (stringBuilder.length() >= 256) {
               String string2 = stringBuilder.toString();
               byte[] bytes = ParseUtil.hexStringToByteArray(string2);
               if (bytes.length >= 128) {
                  arrayList.add(new FreeBsdDisplay(bytes));
               }

               stringBuilder = null;
            }
         }
      }

      return (oshi.hardware.Display[])(arrayList.toArray(new Display[0]));
   }
}
