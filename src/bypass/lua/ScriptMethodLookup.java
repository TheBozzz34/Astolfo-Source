package bypass.lua;

import java.lang.reflect.Method;

public class ScriptMethodLookup {
   private static final long a = 126897665403133L;

   public static String w(Class<?> var0, String var1) {
      for (Method var7 : var0.getDeclaredMethods()) {
         if (var7.isAnnotationPresent(ScriptName.class)) {
            ScriptName var8 = var7.getAnnotation(ScriptName.class);
            if (var8.j().equals(var1)) {
               return var7.getName();
            }
         }
      }

      return null;
   }
}
