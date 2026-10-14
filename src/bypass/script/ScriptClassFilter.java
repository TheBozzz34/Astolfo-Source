package bypass.script;

import jdk.nashorn.api.scripting.ClassFilter;

public class ScriptClassFilter implements ClassFilter {
   public boolean exposeToScripts(String string) {
      return string.contains("org.lwjgl") && !string.equals("org.lwjgl.Sys")
         || string.equals("java.lang.Math")
         || string.equals("java.lang.Array")
         || string.equals("java.util.Timer")
         || string.equals("java.util.concurrent.TimeUnit");
   }
}
