package oshi.util.platform.windows;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public class WmiQueryHandler {
   private static int globalTimeout = GlobalConfig.get("d", -1);
   private int wmiTimeout = globalTimeout;
   private final Set<String> failedWmiClassNames = new HashSet<>();
   private int comThreading = 0;
   private boolean securityInitialized = false;
   private static final Class<?>[] EMPTY_CLASS_ARRAY;
   private static final Object[] EMPTY_OBJECT_ARRAY;
   private static Class<? extends WmiQueryHandler> customClass;

   public static synchronized WmiQueryHandler createInstance() {
      if (customClass == null) {
         return new WmiQueryHandler();
      }

      try {
         return customClass.getConstructor(EMPTY_CLASS_ARRAY).newInstance(EMPTY_OBJECT_ARRAY);
      } catch (NoSuchMethodException | SecurityException value) {
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException value2) {
      }

      return null;
   }

   public static synchronized void setInstanceClass(Class<? extends WmiQueryHandler> instanceClass) {
      customClass = instanceClass;
   }

   public <T extends Enum<T>> WmiResult<T> queryWMI(WmiQuery<T> query) {
      WbemcliUtil wbemcliUtil = WbemcliUtil.INSTANCE;
      WbemcliUtil.INSTANCE.getClass();
      WmiResult wmiResult = wbemcliUtil.new WmiResult(query.getPropertyEnum());
      if (this.failedWmiClassNames.contains(query.getWmiClassName())) {
         return wmiResult;
      }

      boolean bl = false;

      try {
         bl = this.initCOM();
         wmiResult = query.execute(this.wmiTimeout);
      } catch (COMException comException) {
         if (!"ROOT\\OpenHardwareMonitor".equals(query.getNameSpace())) {
            int i = comException.getHresult() == null ? -1 : comException.getHresult().intValue();
            switch (i) {
               default:
                  this.handleComException(query, comException);
               case -2147217394:
               case -2147217392:
               case -2147217385:
                  this.failedWmiClassNames.add(query.getWmiClassName());
            }
         }
      } catch (TimeoutException timeoutException) {
      }

      if (bl) {
         this.unInitCOM();
      }

      return wmiResult;
   }

   public void handleComException(WmiQuery<?> query, COMException ex) {
   }

   public boolean initCOM() {
      boolean bl = false;
      bl = this.initCOM(this.getComThreading());
      if (!bl) {
         bl = this.initCOM(this.switchComThreading());
      }

      if (bl && !this.isSecurityInitialized()) {
         HRESULT hresult = Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, 0, 3, null, 0, null);
         if (COMUtils.FAILED(hresult) && hresult.intValue() != -2147417831) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("Failed to initialize security.", hresult);
         }

         this.securityInitialized = true;
      }

      return bl;
   }

   public boolean initCOM(int coInitThreading) {
      HRESULT hresult = Ole32.INSTANCE.CoInitializeEx(null, coInitThreading);
      switch (hresult.intValue()) {
         case -2147417850:
            return false;
         case 0:
         case 1:
            return true;
         default:
            throw new COMException("Failed to initialize COM library.", hresult);
      }
   }

   public void unInitCOM() {
      Ole32.INSTANCE.CoUninitialize();
   }

   public int getComThreading() {
      return this.comThreading;
   }

   public int switchComThreading() {
      if (this.comThreading == 2) {
         this.comThreading = 0;
      } else {
         this.comThreading = 2;
      }

      return this.comThreading;
   }

   public boolean isSecurityInitialized() {
      return this.securityInitialized;
   }

   public int getWmiTimeout() {
      return this.wmiTimeout;
   }

   public void setWmiTimeout(int wmiTimeout) {
      this.wmiTimeout = wmiTimeout;
   }

   static {
      if (globalTimeout != 0 && globalTimeout >= -1) {
         EMPTY_CLASS_ARRAY = new Class[0];
         EMPTY_OBJECT_ARRAY = new Object[0];
         customClass = null;
      } else {
         throw new GlobalConfig.PropertyException("d");
      }
   }
}
