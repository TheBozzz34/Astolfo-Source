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

   @Override
   public String getManufacturer() {
      return this.manufModelVersSerial.get().getA();
   }

   @Override
   public String getModel() {
      return this.manufModelVersSerial.get().getB();
   }

   @Override
   public String getVersion() {
      return this.manufModelVersSerial.get().getC();
   }

   @Override
   public String getSerialNumber() {
      return this.manufModelVersSerial.get().getD();
   }

   private static Quartet<String, String, String, String> queryManufModelVersSerial() {
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;
      WmiResult wmiResult = Win32BaseBoard.queryBaseboardInfo();
      if (wmiResult.getResultCount() > 0) {
         string = WmiUtil.getString(wmiResult, Win32BaseBoard.BaseBoardProperty.MANUFACTURER, 0);
         string2 = WmiUtil.getString(wmiResult, Win32BaseBoard.BaseBoardProperty.MODEL, 0);
         string3 = WmiUtil.getString(wmiResult, Win32BaseBoard.BaseBoardProperty.VERSION, 0);
         string4 = WmiUtil.getString(wmiResult, Win32BaseBoard.BaseBoardProperty.SERIALNUMBER, 0);
      }

      return new Quartet<>(
         Util.isBlank(string) ? "unknown" : string,
         Util.isBlank(string2) ? "unknown" : string2,
         Util.isBlank(string3) ? "unknown" : string3,
         Util.isBlank(string4) ? "unknown" : string4
      );
   }
}
