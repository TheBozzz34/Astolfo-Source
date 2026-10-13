package oshi.util.platform.windows;

import com.sun.jna.platform.win32.Pdh;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.Pdh.PDH_RAW_COUNTER;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONGLONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class PerfDataUtil {
   private static final DWORD_PTR PZERO = new DWORD_PTR(0L);
   private static final DWORDByReference PDH_FMT_RAW = new DWORDByReference(new DWORD(16L));
   private static final Pdh PDH = Pdh.INSTANCE;
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static final long a = 56742325962037L;
   private static final long b = 2723385511241582549L;

   private PerfDataUtil() {
   }

   public static PerfDataUtil.PerfCounter createCounter(String var0, String var1, String var2) {
      return new PerfDataUtil.PerfCounter(var0, var1, var2);
   }

   public static long updateQueryTimestamp(HANDLEByReference var0) {
      LONGLONGByReference var4 = new LONGLONGByReference();
      int var5 = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(var0.getValue(), var4) : PDH.PdhCollectQueryData(var0.getValue());

      for (int var6 = 0;
         var5 == (int)b && var6++ < 3;
         var5 = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(var0.getValue(), var4) : PDH.PdhCollectQueryData(var0.getValue())
      ) {
         Util.sleep(1 << var6);
      }

      if (var5 != 0) {
         return 0L;
      } else {
         return IS_VISTA_OR_GREATER ? ParseUtil.filetimeToUtcMs(var4.getValue().longValue(), true) : System.currentTimeMillis();
      }
   }

   public static boolean openQuery(HANDLEByReference var0) {
      int var4 = PDH.PdhOpenQuery(null, PZERO, var0);
      return var4 == 0;
   }

   public static boolean closeQuery(HANDLEByReference var0) {
      return 0 == PDH.PdhCloseQuery(var0.getValue());
   }

   public static long queryCounter(HANDLEByReference var0) {
      PDH_RAW_COUNTER var4 = new PDH_RAW_COUNTER();
      int var5 = PDH.PdhGetRawCounterValue(var0.getValue(), PDH_FMT_RAW, var4);
      return var5 != 0 ? var5 : var4.FirstValue;
   }

   public static boolean addCounter(HANDLEByReference var0, String var1, HANDLEByReference var2) {
      int var6 = IS_VISTA_OR_GREATER ? PDH.PdhAddEnglishCounter(var0.getValue(), var1, PZERO, var2) : PDH.PdhAddCounter(var0.getValue(), var1, PZERO, var2);
      return var6 == 0;
   }

   public static boolean removeCounter(HANDLEByReference var0) {
      return 0 == PDH.PdhRemoveCounter(var0.getValue());
   }

   public static class PerfCounter {
      private String object;
      private String instance;
      private String counter;
      private static final long a = 38973254344814L;

      public PerfCounter(String var1, String var2, String var3) {
         this.object = var1;
         this.instance = var2;
         this.counter = var3;
      }

      public String c() {
         return this.object;
      }

      public String getInstance() {
         return this.instance;
      }

      public String b() {
         return this.counter;
      }

      public String getCounterPath() {
         StringBuilder var4 = new StringBuilder();
         var4.append('\\').append(this.object);
         if (this.instance != null) {
            var4.append('(').append(this.instance).append(')');
         }

         var4.append('\\').append(this.counter);
         return var4.toString();
      }
   }
}
