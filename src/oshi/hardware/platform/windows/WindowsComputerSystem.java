package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32Bios;
import oshi.driver.windows.wmi.Win32ComputerSystem;
import oshi.driver.windows.wmi.Win32ComputerSystemProduct;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@Immutable
public final class WindowsComputerSystem extends AbstractComputerSystem {
   private final Supplier<Pair<String, String>> manufacturerModel = Memoizer.memoize(WindowsComputerSystem::queryManufacturerModel);
   private final Supplier<String> serialNumber = Memoizer.memoize(WindowsComputerSystem::querySystemSerialNumber);
   private static final long b = 137708785727110L;

   @Override
   public String V() {
      return this.manufacturerModel.get().getA();
   }

   @Override
   public String z() {
      return this.manufacturerModel.get().getB();
   }

   @Override
   public String k() {
      return this.serialNumber.get();
   }

   @Override
   public Firmware createFirmware() {
      return new WindowsFirmware();
   }

   @Override
   public Baseboard createBaseboard() {
      return new WindowsBaseboard();
   }

   private static Pair<String, String> queryManufacturerModel() {
      String string = null;
      String string2 = null;
      WmiResult wmiResult = Win32ComputerSystem.queryComputerSystem();
      if (wmiResult.getResultCount() > 0) {
         string = WmiUtil.getString(wmiResult, Win32ComputerSystem.ComputerSystemProperty.MANUFACTURER, 0);
         string2 = WmiUtil.getString(wmiResult, Win32ComputerSystem.ComputerSystemProperty.MODEL, 0);
      }

      return new Pair<>(Util.isBlank(string) ? "unknown" : string, Util.isBlank(string2) ? "unknown" : string2);
   }

   private static String querySystemSerialNumber() {
      String string;
      return ((string = querySerialFromBios()) != null || (string = querySerialFromCsProduct()) != null) && !Util.isBlank(string) ? string : "unknown";
   }

   private static String querySerialFromBios() {
      WmiResult wmiResult = Win32Bios.k();
      return wmiResult.getResultCount() > 0 ? WmiUtil.getString(wmiResult, Win32Bios.BiosSerialProperty.SERIALNUMBER, 0) : null;
   }

   private static String querySerialFromCsProduct() {
      WmiResult wmiResult = Win32ComputerSystemProduct.queryIdentifyingNumber();
      return wmiResult.getResultCount() > 0 ? WmiUtil.getString(wmiResult, Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0) : null;
   }
}
