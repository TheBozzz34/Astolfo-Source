package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32BaseBoard;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Quartet;

@Immutable
public final class WindowsBaseboard extends AbstractBaseboard {
   private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(WindowsBaseboard::queryManufModelVersSerial);
   private static final long b = 45035938544427L;

   @Override
   public String p() {
      return this.manufModelVersSerial.get().getA();
   }

   @Override
   public String g() {
      return this.manufModelVersSerial.get().getB();
   }

   @Override
   public String W() {
      return this.manufModelVersSerial.get().getC();
   }

   @Override
   public String A() {
      return this.manufModelVersSerial.get().getD();
   }

   private static Quartet<String, String, String, String> queryManufModelVersSerial() {
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;
      WmiResult var7 = Win32BaseBoard.queryBaseboardInfo();
      if (var7.getResultCount() > 0) {
         var3 = WmiUtil.getString(var7, Win32BaseBoard.BaseBoardProperty.MANUFACTURER, 0);
         var4 = WmiUtil.getString(var7, Win32BaseBoard.BaseBoardProperty.MODEL, 0);
         var5 = WmiUtil.getString(var7, Win32BaseBoard.BaseBoardProperty.VERSION, 0);
         var6 = WmiUtil.getString(var7, Win32BaseBoard.BaseBoardProperty.SERIALNUMBER, 0);
      }

      return new Quartet<>(
         Util.isBlank(var3) ? "unknown" : var3,
         Util.isBlank(var4) ? "unknown" : var4,
         Util.isBlank(var5) ? "unknown" : var5,
         Util.isBlank(var6) ? "unknown" : var6
      );
   }
}
