package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.minecraft.src.Lang;
import net.minecraft.src.StrUtils;

public class ShaderOptionSwitch extends ShaderOption {
   private static final Pattern PATTERN_DEFINE = Pattern.compile("^\\s*(//)?\\s*#define\\s+([A-Za-z0-9_]+)\\s*(//.*)?$");
   private static final Pattern PATTERN_IFDEF = Pattern.compile("^\\s*#if(n)?def\\s+([A-Za-z0-9_]+)(\\s*)?$");
   private static final long b = 78472606726798L;

   public ShaderOptionSwitch(String var1, String var2, String var3, String var4) {
      super(var1, var2, var3, new String[]{"true", "false"}, var3, var4);
   }

   @Override
   public String getSourceLine() {
      return isTrue(this.getValue()) ? "#define " + this.getName() + " // Shader option ON" : "//#define " + this.getName() + " // Shader option OFF";
   }

   @Override
   public String getValueText(String var1) {
      return isTrue(var1) ? Lang.getOn() : Lang.getOff();
   }

   @Override
   public String getValueColor(String var1) {
      return isTrue(var1) ? "§a" : "§c";
   }

   public static ShaderOption parseOption_ShaderOption(String var0, String var1) {
      Matcher var5 = PATTERN_DEFINE.matcher(var0);
      if (!var5.matches()) {
         return null;
      } else {
         String var6 = var5.group(1);
         String var7 = var5.group(2);
         String var8 = var5.group(3);
         if (var7 != null && var7.length() > 0) {
            boolean var9 = Config.equals(var6, "//");
            boolean var10 = !var9;
            var1 = StrUtils.removePrefix(var1, "/shaders/");
            return new ShaderOptionSwitch(var7, var8, String.valueOf(var10), var1);
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean matchesLine(String var1) {
      Matcher var5 = PATTERN_DEFINE.matcher(var1);
      if (!var5.matches()) {
         return false;
      }

      String var6 = var5.group(2);
      return var6.matches(this.getName());
   }

   @Override
   public boolean checkUsed() {
      return true;
   }

   @Override
   public boolean isUsedInLine(String var1) {
      Matcher var5 = PATTERN_IFDEF.matcher(var1);
      if (var5.matches()) {
         String var6 = var5.group(2);
         if (var6.equals(this.getName())) {
            return true;
         }
      }

      return false;
   }

   public static boolean isTrue(String var0) {
      return Boolean.valueOf(var0);
   }
}
