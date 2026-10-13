package bypass.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class TypeUtil {
   private static final long a = 68418313599959L;

   public static boolean q(Object var0) {
      return var0.getClass().equals(int.class) || var0.getClass().equals(Integer.class);
   }

   public static boolean F(Object var0) {
      return var0.getClass().equals(boolean.class) || var0.getClass().equals(Boolean.class);
   }

   public static boolean w(Object var0) {
      return var0.getClass().equals(char.class) || var0.getClass().equals(Character.class);
   }

   public static boolean d(Object var0) {
      return var0.getClass().equals(byte.class) || var0.getClass().equals(Byte.class);
   }

   public static boolean P(Object var0) {
      return var0.getClass().equals(long.class) || var0.getClass().equals(Long.class);
   }

   public static boolean Y(Object var0) {
      return var0.getClass().equals(short.class) || var0.getClass().equals(Short.class);
   }

   public static boolean o(Object var0) {
      return var0.getClass().equals(double.class) || var0.getClass().equals(Double.class);
   }

   public static boolean a(Object var0) {
      return var0.getClass().equals(float.class) || var0.getClass().equals(Float.class);
   }

   public static boolean b(Object var0) {
      return var0.getClass().equals(void.class) || var0.getClass().equals(Void.class);
   }

   public static boolean c(Object var0) {
      return var0.getClass().equals(String.class);
   }

   public static boolean s(Object var0) {
      return var0.getClass().equals(Enum.class) || var0 instanceof Enum || var0.getClass().isAssignableFrom(Enum.class);
   }

   public static boolean Z(Object var0) {
      return q(var0)
         || w(var0)
         || d(var0)
         || P(var0)
         || Y(var0)
         || o(var0)
         || a(var0)
         || var0.getClass().equals(BigInteger.class)
         || var0.getClass().equals(BigDecimal.class)
         || var0.getClass().equals(Number.class);
   }
}
