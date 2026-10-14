package bypass.lua;

import java.lang.reflect.Method;

public class ScriptMethodLookup {
   public static String w(Class<?> class2, String string) {
      for (Method method : class2.getDeclaredMethods()) {
         if (method.isAnnotationPresent(ScriptName.class)) {
            ScriptName scriptName = method.getAnnotation(ScriptName.class);
            if (scriptName.j().equals(string)) {
               return method.getName();
            }
         }
      }

      return null;
   }
}
