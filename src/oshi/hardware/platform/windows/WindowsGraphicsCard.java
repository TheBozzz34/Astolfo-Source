package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32VideoController;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@Immutable
public final class WindowsGraphicsCard extends AbstractGraphicsCard {
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static final long b = 1101206258170L;

   public WindowsGraphicsCard(String var1, String var2, String var3, String var4, long var5) {
      super(var1, var2, var3, var4, var5);
   }

   public static List<WindowsGraphicsCard> getGraphicsCards() {
      ArrayList var3 = new ArrayList();
      if (IS_VISTA_OR_GREATER) {
         WmiResult var4 = Win32VideoController.queryVideoController();

         for (int var5 = 0; var5 < var4.getResultCount(); var5++) {
            String var6 = WmiUtil.getString(var4, Win32VideoController.VideoControllerProperty.NAME, var5);
            Pair var7 = ParseUtil.parsePnPDeviceIdToVendorProductId(WmiUtil.getString(var4, Win32VideoController.VideoControllerProperty.PNPDEVICEID, var5));
            String var8 = var7 == null ? "unknown" : (String)var7.getB();
            String var9 = WmiUtil.getString(var4, Win32VideoController.VideoControllerProperty.ADAPTERCOMPATIBILITY, var5);
            if (var7 != null) {
               if (Util.isBlank(var9)) {
                  var8 = (String)var7.getA();
               } else {
                  var9 = var9 + " (" + (String)var7.getA() + ")";
               }
            }

            String var10 = WmiUtil.getString(var4, Win32VideoController.VideoControllerProperty.DRIVERVERSION, var5);
            if (!Util.isBlank(var10)) {
               var10 = "DriverVersion=" + var10;
            } else {
               var10 = "unknown";
            }

            long var11 = WmiUtil.getUint32asLong(var4, Win32VideoController.VideoControllerProperty.ADAPTERRAM, var5);
            var3.add(new WindowsGraphicsCard(Util.isBlank(var6) ? "unknown" : var6, var8, Util.isBlank(var9) ? "unknown" : var9, var10, var11));
         }
      }

      return var3;
   }
}
