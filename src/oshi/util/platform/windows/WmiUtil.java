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

   private WmiUtil() {
   }

   public static <T extends Enum<T>> String queryToString(WmiQuery<T> query) {
      Enum[] enums = (Enum[])query.getPropertyEnum().getEnumConstants();
      StringBuilder stringBuilder = new StringBuilder("SELECT ");
      stringBuilder.append(enums[0].name());

      for (int i = 1; i < enums.length; i++) {
         stringBuilder.append(',').append(enums[i].name());
      }

      stringBuilder.append(" FROM ").append(query.getWmiClassName());
      return stringBuilder.toString();
   }

   public static <T extends Enum<T>> String getString(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 8) {
         return getStr(result, property, index);
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "String", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   public static <T extends Enum<T>> String getDateString(WmiResult<T> result, T property, int index) {
      OffsetDateTime offsetDateTime = getDateTime(result, (T)property, index);
      return offsetDateTime.equals(Constants.UNIX_EPOCH) ? "" : offsetDateTime.toLocalDate().toString();
   }

   public static <T extends Enum<T>> OffsetDateTime getDateTime(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 101) {
         return ParseUtil.parseCimDateTimeToOffset(getStr(result, property, index));
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "DateTime", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   public static <T extends Enum<T>> String getRefString(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 102) {
         return getStr(result, property, index);
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "Reference", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   private static <T extends Enum<T>> String getStr(WmiResult<T> result, T property, int index) {
      Object object = result.getValue((T)property, index);
      if (object == null) {
         return "";
      } else if (result.getVtType((T)property) == 8) {
         return (String)object;
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d",
               property.name(),
               "String-mapped",
               result.getCIMType((T)property),
               result.getVtType((T)property)
            )
         );
      }
   }

   public static <T extends Enum<T>> long getUint64(WmiResult<T> result, T property, int index) {
      Object object = result.getValue((T)property, index);
      if (object == null) {
         return 0L;
      } else if (result.getCIMType((T)property) == 21 && result.getVtType((T)property) == 8) {
         return ParseUtil.parseLongOrDefault((String)object, 0L);
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d",
               property.name(),
               "UINT64",
               result.getCIMType((T)property),
               result.getVtType((T)property)
            )
         );
      }
   }

   public static <T extends Enum<T>> int getUint32(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 19) {
         return getInt(result, property, index);
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT32", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   public static <T extends Enum<T>> long getUint32asLong(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 19) {
         return getInt(result, property, index) & 4294967295L;
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT32", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   public static <T extends Enum<T>> int getSint32(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 3) {
         return getInt(result, property, index);
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "SINT32", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   public static <T extends Enum<T>> int getUint16(WmiResult<T> result, T property, int index) {
      if (result.getCIMType(property) == 18) {
         return getInt(result, property, index);
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "UINT16", result.getCIMType(property), result.getVtType(property)
            )
         );
      }
   }

   private static <T extends Enum<T>> int getInt(WmiResult<T> result, T property, int index) {
      Object object = result.getValue((T)property, index);
      if (object == null) {
         return 0;
      } else if (result.getVtType((T)property) == 3) {
         return (Integer)object;
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d",
               property.name(),
               "32-bit integer",
               result.getCIMType((T)property),
               result.getVtType((T)property)
            )
         );
      }
   }

   public static <T extends Enum<T>> float getFloat(WmiResult<T> result, T property, int index) {
      Object object = result.getValue((T)property, index);
      if (object == null) {
         return 0.0F;
      } else if (result.getCIMType((T)property) == 4 && result.getVtType((T)property) == 4) {
         return (Float)object;
      } else {
         throw new ClassCastException(
            String.format(
               "%s is not a %s type. CIM Type is %d and VT type is %d", property.name(), "Float", result.getCIMType((T)property), result.getVtType((T)property)
            )
         );
      }
   }
}
