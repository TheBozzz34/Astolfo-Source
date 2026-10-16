package bypass.event.bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class ReflectionErrors {
   public static void rethrowReflectionException(Exception exception) {
      if (exception instanceof NoSuchMethodException) {
         throw new IllegalStateException("Couldn't find the method:\n" + exception.getMessage());
      }

      if (exception instanceof IllegalAccessException) {
         throw new IllegalStateException("Unable to access method:\n" + exception.getMessage());
      }

      if (exception instanceof InvocationTargetException) {
         rethrowTargetException((InvocationTargetException)exception);
      }

      if (exception instanceof RuntimeException) {
         throw (RuntimeException)exception;
      } else {
         throw new UndeclaredThrowableException(exception);
      }
   }

   public static void rethrowTargetException(InvocationTargetException invocationTargetException) {
      rethrowUnchecked(invocationTargetException.getTargetException());
   }

   public static void rethrowUnchecked(Throwable throwable) {
      if (throwable instanceof RuntimeException) {
         throw (RuntimeException)throwable;
      } else if (throwable instanceof Error) {
         throw (Error)throwable;
      } else {
         throw new UndeclaredThrowableException(throwable);
      }
   }
}
