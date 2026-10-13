package bypass.script;

import jdk.nashorn.api.scripting.ClassFilter;

public class ScriptClassFilter implements ClassFilter {
   private static final long a = 122030401391772L;

   public boolean exposeToScripts(String var1) {
      return var1.contains("org.lwjgl") && !var1.equals("org.lwjgl.Sys")
         || var1.equals("java.lang.Math")
         || var1.equals("java.lang.Array")
         || var1.equals("java.util.Timer")
         || var1.equals("java.util.concurrent.TimeUnit");
   }
}
