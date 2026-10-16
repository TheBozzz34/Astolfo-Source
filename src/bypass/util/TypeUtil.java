package bypass.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class TypeUtil {
   public static boolean isInteger(Object object) {
      return object.getClass().equals(int.class) || object.getClass().equals(Integer.class);
   }

   public static boolean isBoolean(Object object) {
      return object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class);
   }

   public static boolean isCharacter(Object object) {
      return object.getClass().equals(char.class) || object.getClass().equals(Character.class);
   }

   public static boolean isByte(Object object) {
      return object.getClass().equals(byte.class) || object.getClass().equals(Byte.class);
   }

   public static boolean isLong(Object object) {
      return object.getClass().equals(long.class) || object.getClass().equals(Long.class);
   }

   public static boolean isShort(Object object) {
      return object.getClass().equals(short.class) || object.getClass().equals(Short.class);
   }

   public static boolean isDouble(Object object) {
      return object.getClass().equals(double.class) || object.getClass().equals(Double.class);
   }

   public static boolean isFloat(Object object) {
      return object.getClass().equals(float.class) || object.getClass().equals(Float.class);
   }

   public static boolean isVoid(Object object) {
      return object.getClass().equals(void.class) || object.getClass().equals(Void.class);
   }

   public static boolean isString(Object object) {
      return object.getClass().equals(String.class);
   }

   public static boolean isEnum(Object object) {
      return object.getClass().equals(Enum.class) || object instanceof Enum || object.getClass().isAssignableFrom(Enum.class);
   }

   public static boolean isNumeric(Object object) {
      return isInteger(object)
         || isCharacter(object)
         || isByte(object)
         || isLong(object)
         || isShort(object)
         || isDouble(object)
         || isFloat(object)
         || object.getClass().equals(BigInteger.class)
         || object.getClass().equals(BigDecimal.class)
         || object.getClass().equals(Number.class);
   }
}
