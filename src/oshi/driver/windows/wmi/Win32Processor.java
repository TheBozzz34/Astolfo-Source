package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Processor {
   private static final long a = 66072440488844L;

   private Win32Processor() {
   }

   public static WmiResult<Win32Processor.VoltProperty> queryVoltage() {
      WmiQuery var2 = new WmiQuery<>("Win32_Processor", Win32Processor.VoltProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public static WmiResult<Win32Processor.ProcessorIdProperty> queryProcessorId() {
      WmiQuery var2 = new WmiQuery<>("Win32_Processor", Win32Processor.ProcessorIdProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public static WmiResult<Win32Processor.BitnessProperty> queryBitness() {
      WmiQuery var2 = new WmiQuery<>("Win32_Processor", Win32Processor.BitnessProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
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
