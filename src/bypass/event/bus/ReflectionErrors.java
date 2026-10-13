package bypass.event.bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class ReflectionErrors {
   private static final long a = 73615270588222L;

   public static void H(Exception var0) {
      if (var0 instanceof NoSuchMethodException) {
         throw new IllegalStateException("Couldn't find the method:\n" + var0.getMessage());
      }

      if (var0 instanceof IllegalAccessException) {
         throw new IllegalStateException("Unable to access method:\n" + var0.getMessage());
      }

      if (var0 instanceof InvocationTargetException) {
         x((InvocationTargetException)var0);
      }

      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else {
         throw new UndeclaredThrowableException(var0);
      }
   }

   public static void x(InvocationTargetException var0) {
      H(var0.getTargetException());
   }

   public static void H(Throwable var0) {
      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else if (var0 instanceof Error) {
         throw (Error)var0;
      } else {
         throw new UndeclaredThrowableException(var0);
      }
   }
}
