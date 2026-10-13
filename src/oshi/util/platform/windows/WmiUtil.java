package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.time.OffsetDateTime;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Constants;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WmiUtil {
   public static final String L = "ROOT\\OpenHardwareMonitor";
   private static final long a = 86940169811911L;

   private WmiUtil() {
   }

   public static <T extends Enum<T>> String queryToString(WmiQuery<T> var0) {
      Enum[] var4 = (Enum[])var0.getPropertyEnum().getEnumConstants();
      StringBuilder var5 = new StringBuilder("SELECT ");
      var5.append(var4[0].name());

      for (int var6 = 1; var6 < var4.length; var6++) {
         var5.append(',').append(var4[var6].name());
      }

      var5.append(" FROM ").append(var0.getWmiClassName());
      return var5.toString();
   }

   public static <T extends Enum<T>> String getString(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 8) {
         return getStr(var0, (T)var1, var2);
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "String", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   public static <T extends Enum<T>> String getDateString(WmiResult<T> var0, T var1, int var2) {
      OffsetDateTime var6 = getDateTime(var0, (T)var1, var2);
      return var6.equals(Constants.UNIX_EPOCH) ? "" : var6.toLocalDate().toString();
   }

   public static <T extends Enum<T>> OffsetDateTime getDateTime(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 101) {
         return ParseUtil.parseCimDateTimeToOffset(getStr(var0, (T)var1, var2));
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "DateTime", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   public static <T extends Enum<T>> String getRefString(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 102) {
         return getStr(var0, (T)var1, var2);
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "Reference", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   private static <T extends Enum<T>> String getStr(WmiResult<T> var0, T var1, int var2) {
      Object var6 = var0.getValue((T)var1, var2);
      if (var6 == null) {
         return "";
      } else if (var0.getVtType((T)var1) == 8) {
         return (String)var6;
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "String-mapped", var0.getCIMType((T)var1), var0.getVtType((T)var1)
            )
         );
      }
   }

   public static <T extends Enum<T>> long getUint64(WmiResult<T> var0, T var1, int var2) {
      Object var6 = var0.getValue((T)var1, var2);
      if (var6 == null) {
         return 0L;
      } else if (var0.getCIMType((T)var1) == 21 && var0.getVtType((T)var1) == 8) {
         return ParseUtil.parseLongOrDefault((String)var6, 0L);
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "UINT64", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   public static <T extends Enum<T>> int getUint32(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 19) {
         return getInt(var0, (T)var1, var2);
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "UINT32", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   public static <T extends Enum<T>> long getUint32asLong(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 19) {
         return getInt(var0, (T)var1, var2) & 4294967295L;
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "UINT32", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   public static <T extends Enum<T>> int getSint32(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 3) {
         return getInt(var0, (T)var1, var2);
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "SINT32", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   public static <T extends Enum<T>> int getUint16(WmiResult<T> var0, T var1, int var2) {
      if (var0.getCIMType((T)var1) == 18) {
         return getInt(var0, (T)var1, var2);
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "UINT16", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }

   private static <T extends Enum<T>> int getInt(WmiResult<T> var0, T var1, int var2) {
      Object var6 = var0.getValue((T)var1, var2);
      if (var6 == null) {
         return 0;
      } else if (var0.getVtType((T)var1) == 3) {
         return (Integer)var6;
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "32-bit integer", var0.getCIMType((T)var1), var0.getVtType((T)var1)
            )
         );
      }
   }

   public static <T extends Enum<T>> float getFloat(WmiResult<T> var0, T var1, int var2) {
      Object var6 = var0.getValue((T)var1, var2);
      if (var6 == null) {
         return 0.0F;
      } else if (var0.getCIMType((T)var1) == 4 && var0.getVtType((T)var1) == 4) {
         return (Float)var6;
      } else {
         throw new ClassCastException(
            String.format("%s is not a %s type. CIM Type is %d and VT type is %d", var1.name(), "Float", var0.getCIMType((T)var1), var0.getVtType((T)var1))
         );
      }
   }
}
