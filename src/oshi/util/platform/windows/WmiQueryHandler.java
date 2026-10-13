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
   private static final long c = 132665169590587L;

   public static synchronized WmiQueryHandler createInstance() {
      if (customClass == null) {
         return new WmiQueryHandler();
      }

      try {
         return customClass.getConstructor(EMPTY_CLASS_ARRAY).newInstance(EMPTY_OBJECT_ARRAY);
      } catch (NoSuchMethodException | SecurityException var4) {
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException var5) {
      }

      return null;
   }

   public static synchronized void setInstanceClass(Class<? extends WmiQueryHandler> var0) {
      customClass = var0;
   }

   public <T extends Enum<T>> WmiResult<T> queryWMI(WmiQuery<T> var1) {
      WbemcliUtil var10002 = WbemcliUtil.INSTANCE;
      WbemcliUtil.INSTANCE.getClass();
      WmiResult var5 = var10002.new WmiResult(var1.getPropertyEnum());
      if (this.failedWmiClassNames.contains(var1.getWmiClassName())) {
         return var5;
      }

      boolean var6 = false;

      try {
         var6 = this.initCOM();
         var5 = var1.execute(this.wmiTimeout);
      } catch (COMException var9) {
         if (!"ROOT\\OpenHardwareMonitor".equals(var1.getNameSpace())) {
            int var8 = var9.getHresult() == null ? -1 : var9.getHresult().intValue();
            switch (var8) {
               default:
                  this.handleComException(var1, var9);
               case -2147217394:
               case -2147217392:
               case -2147217385:
                  this.failedWmiClassNames.add(var1.getWmiClassName());
            }
         }
      } catch (TimeoutException var10) {
      }

      if (var6) {
         this.unInitCOM();
      }

      return var5;
   }

   public void handleComException(WmiQuery<?> var1, COMException var2) {
   }

   public boolean initCOM() {
      boolean var4 = false;
      var4 = this.initCOM(this.getComThreading());
      if (!var4) {
         var4 = this.initCOM(this.switchComThreading());
      }

      if (var4 && !this.isSecurityInitialized()) {
         HRESULT var5 = Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, 0, 3, null, 0, null);
         if (COMUtils.FAILED(var5) && var5.intValue() != -2147417831) {
            Ole32.INSTANCE.CoUninitialize();
            throw new COMException("Failed to initialize security.", var5);
         }

         this.securityInitialized = true;
      }

      return var4;
   }

   public boolean initCOM(int var1) {
      HRESULT var5 = Ole32.INSTANCE.CoInitializeEx(null, var1);
      switch (var5.intValue()) {
         case -2147417850:
            return false;
         case 0:
         case 1:
            return true;
         default:
            throw new COMException("Failed to initialize COM library.", var5);
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

   public int n() {
      return this.wmiTimeout;
   }

   public void setWmiTimeout(int var1) {
      this.wmiTimeout = var1;
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
