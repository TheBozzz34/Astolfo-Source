package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Processor {
   private Win32Processor() {
   }

   public static WmiResult<Win32Processor.VoltProperty> queryVoltage() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_Processor", Win32Processor.VoltProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public static WmiResult<Win32Processor.ProcessorIdProperty> queryProcessorId() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_Processor", Win32Processor.ProcessorIdProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public static WmiResult<Win32Processor.BitnessProperty> queryBitness() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_Processor", Win32Processor.BitnessProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum BitnessProperty {
      ADDRESSWIDTH;
   }

   public enum ProcessorIdProperty {
      PROCESSORID;
   }

   public enum VoltProperty {
      CURRENTVOLTAGE,
      VOLTAGECAPS;
   }
}
