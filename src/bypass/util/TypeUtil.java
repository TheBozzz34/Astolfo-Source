package bypass.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class TypeUtil {
   private static final long a = 68418313599959L;

   public static boolean q(Object object) {
      return object.getClass().equals(int.class) || object.getClass().equals(Integer.class);
   }

   public static boolean F(Object object) {
      return object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class);
   }

   public static boolean w(Object object) {
      return object.getClass().equals(char.class) || object.getClass().equals(Character.class);
   }

   public static boolean d(Object object) {
      return object.getClass().equals(byte.class) || object.getClass().equals(Byte.class);
   }

   public static boolean P(Object object) {
      return object.getClass().equals(long.class) || object.getClass().equals(Long.class);
   }

   public static boolean Y(Object object) {
      return object.getClass().equals(short.class) || object.getClass().equals(Short.class);
   }

   public static boolean o(Object object) {
      return object.getClass().equals(double.class) || object.getClass().equals(Double.class);
   }

   public static boolean a(Object object) {
      return object.getClass().equals(float.class) || object.getClass().equals(Float.class);
   }

   public static boolean b(Object object) {
      return object.getClass().equals(void.class) || object.getClass().equals(Void.class);
   }

   public static boolean c(Object object) {
      return object.getClass().equals(String.class);
   }

   public static boolean s(Object object) {
      return object.getClass().equals(Enum.class) || object instanceof Enum || object.getClass().isAssignableFrom(Enum.class);
   }

   public static boolean Z(Object object) {
      return q(object)
         || w(object)
         || d(object)
         || P(object)
         || Y(object)
         || o(object)
         || a(object)
         || object.getClass().equals(BigInteger.class)
         || object.getClass().equals(BigDecimal.class)
         || object.getClass().equals(Number.class);
   }
}
