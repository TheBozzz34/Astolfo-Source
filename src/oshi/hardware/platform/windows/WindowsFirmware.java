package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32Bios;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Quintet;

@Immutable
public final class WindowsFirmware extends AbstractFirmware {
   private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(
      WindowsFirmware::queryManufNameDescVersRelease
   );

   @Override
   public String R() {
      return this.manufNameDescVersRelease.get().y();
   }

   @Override
   public String getName() {
      return this.manufNameDescVersRelease.get().x();
   }

   @Override
   public String getDescription() {
      return this.manufNameDescVersRelease.get().q();
   }

   @Override
   public String P() {
      return this.manufNameDescVersRelease.get().J();
   }

   @Override
   public String getReleaseDate() {
      return this.manufNameDescVersRelease.get().s();
   }

   private static Quintet<String, String, String, String, String> queryManufNameDescVersRelease() {
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;
      String string5 = null;
      WmiResult wmiResult = Win32Bios.queryBiosInfo();
      if (wmiResult.getResultCount() > 0) {
         string = WmiUtil.getString(wmiResult, Win32Bios.BiosProperty.MANUFACTURER, 0);
         string2 = WmiUtil.getString(wmiResult, Win32Bios.BiosProperty.NAME, 0);
         string3 = WmiUtil.getString(wmiResult, Win32Bios.BiosProperty.DESCRIPTION, 0);
         string4 = WmiUtil.getString(wmiResult, Win32Bios.BiosProperty.VERSION, 0);
         string5 = WmiUtil.getDateString(wmiResult, Win32Bios.BiosProperty.RELEASEDATE, 0);
      }

      return new Quintet<>(
         Util.isBlank(string) ? "unknown" : string,
         Util.isBlank(string2) ? "unknown" : string2,
         Util.isBlank(string3) ? "unknown" : string3,
         Util.isBlank(string4) ? "unknown" : string4,
         Util.isBlank(string5) ? "unknown" : string5
      );
   }
}
