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
      String var3 = null;
      String var4 = null;
      WmiResult var5 = Win32ComputerSystem.queryComputerSystem();
      if (var5.getResultCount() > 0) {
         var3 = WmiUtil.getString(var5, Win32ComputerSystem.ComputerSystemProperty.MANUFACTURER, 0);
         var4 = WmiUtil.getString(var5, Win32ComputerSystem.ComputerSystemProperty.MODEL, 0);
      }

      return new Pair<>(Util.isBlank(var3) ? "unknown" : var3, Util.isBlank(var4) ? "unknown" : var4);
   }

   private static String querySystemSerialNumber() {
      String var3;
      return ((var3 = querySerialFromBios()) != null || (var3 = querySerialFromCsProduct()) != null) && !Util.isBlank(var3) ? var3 : "unknown";
   }

   private static String querySerialFromBios() {
      WmiResult var3 = Win32Bios.k();
      return var3.getResultCount() > 0 ? WmiUtil.getString(var3, Win32Bios.BiosSerialProperty.SERIALNUMBER, 0) : null;
   }

   private static String querySerialFromCsProduct() {
      WmiResult var3 = Win32ComputerSystemProduct.queryIdentifyingNumber();
      return var3.getResultCount() > 0 ? WmiUtil.getString(var3, Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0) : null;
   }
}
