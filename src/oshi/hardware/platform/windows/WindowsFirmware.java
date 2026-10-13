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
   private static final long b = 89004851525334L;

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
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;
      String var7 = null;
      WmiResult var8 = Win32Bios.queryBiosInfo();
      if (var8.getResultCount() > 0) {
         var3 = WmiUtil.getString(var8, Win32Bios.BiosProperty.MANUFACTURER, 0);
         var4 = WmiUtil.getString(var8, Win32Bios.BiosProperty.NAME, 0);
         var5 = WmiUtil.getString(var8, Win32Bios.BiosProperty.DESCRIPTION, 0);
         var6 = WmiUtil.getString(var8, Win32Bios.BiosProperty.VERSION, 0);
         var7 = WmiUtil.getDateString(var8, Win32Bios.BiosProperty.RELEASEDATE, 0);
      }

      return new Quintet<>(
         Util.isBlank(var3) ? "unknown" : var3,
         Util.isBlank(var4) ? "unknown" : var4,
         Util.isBlank(var5) ? "unknown" : var5,
         Util.isBlank(var6) ? "unknown" : var6,
         Util.isBlank(var7) ? "unknown" : var7
      );
   }
}
