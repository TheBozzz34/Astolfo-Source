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

   public WindowsGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<WindowsGraphicsCard> getGraphicsCards() {
      ArrayList arrayList = new ArrayList();
      if (IS_VISTA_OR_GREATER) {
         WmiResult wmiResult = Win32VideoController.queryVideoController();

         for (int i = 0; i < wmiResult.getResultCount(); i++) {
            String string = WmiUtil.getString(wmiResult, Win32VideoController.VideoControllerProperty.NAME, i);
            Pair pair = ParseUtil.parsePnPDeviceIdToVendorProductId(WmiUtil.getString(wmiResult, Win32VideoController.VideoControllerProperty.PNPDEVICEID, i));
            String string2 = pair == null ? "unknown" : (String)pair.getB();
            String string3 = WmiUtil.getString(wmiResult, Win32VideoController.VideoControllerProperty.ADAPTERCOMPATIBILITY, i);
            if (pair != null) {
               if (Util.isBlank(string3)) {
                  string2 = (String)pair.getA();
               } else {
                  string3 = string3 + " (" + (String)pair.getA() + ")";
               }
            }

            String string4 = WmiUtil.getString(wmiResult, Win32VideoController.VideoControllerProperty.DRIVERVERSION, i);
            if (!Util.isBlank(string4)) {
               string4 = "DriverVersion=" + string4;
            } else {
               string4 = "unknown";
            }

            long l = WmiUtil.getUint32asLong(wmiResult, Win32VideoController.VideoControllerProperty.ADAPTERRAM, i);
            arrayList.add(new WindowsGraphicsCard(Util.isBlank(string) ? "unknown" : string, string2, Util.isBlank(string3) ? "unknown" : string3, string4, l));
         }
      }

      return arrayList;
   }
}
