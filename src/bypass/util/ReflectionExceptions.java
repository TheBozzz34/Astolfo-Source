package bypass.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public final class ReflectionExceptions {
   private static final long a = 105854379820039L;

   public static void F(Exception var0) {
      if (var0 instanceof NoSuchMethodException) {
         throw new IllegalStateException("Couldn't find the method:\n" + var0.getMessage());
      }

      if (var0 instanceof IllegalAccessException) {
         throw new IllegalStateException("Unable to access method:\n" + var0.getMessage());
      }

      if (var0 instanceof InvocationTargetException) {
         N((InvocationTargetException)var0);
      }

      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else {
         throw new UndeclaredThrowableException(var0);
      }
   }

   public static void N(InvocationTargetException var0) {
      w(var0.getTargetException());
   }

   public static void w(Throwable var0) {
      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else if (var0 instanceof Error) {
         throw (Error)var0;
      } else {
         throw new UndeclaredThrowableException(var0);
      }
   }
}
