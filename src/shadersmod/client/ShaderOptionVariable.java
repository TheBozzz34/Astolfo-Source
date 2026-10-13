package shadersmod.client;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.minecraft.src.StrUtils;

public class ShaderOptionVariable extends ShaderOption {
   private static final Pattern PATTERN_VARIABLE = Pattern.compile("^\\s*#define\\s+(\\w+)\\s+(-?[0-9\\.Ff]+|\\w+)\\s*(//.*)?$");
   private static final long b = 57492058295434L;

   public ShaderOptionVariable(String var1, String var2, String var3, String[] var4, String var5) {
      super(var1, var2, var3, var4, var3, var5);
      this.setVisible(this.getValues().length > 1);
   }

   @Override
   public String getSourceLine() {
      return "#define " + this.getName() + " " + this.getValue() + " // Shader option " + this.getValue();
   }

   @Override
   public String getValueColor(String var1) {
      return "§a";
   }

   @Override
   public boolean matchesLine(String var1) {
      Matcher var5 = PATTERN_VARIABLE.matcher(var1);
      if (!var5.matches()) {
         return false;
      }

      String var6 = var5.group(1);
      return var6.matches(this.getName());
   }

   public static ShaderOption parseOption(String var0, String var1) {
      Matcher var5 = PATTERN_VARIABLE.matcher(var0);
      if (!var5.matches()) {
         return null;
      }

      String var6 = var5.group(1);
      String var7 = var5.group(2);
      String var8 = var5.group(3);
      String var9 = StrUtils.getSegment(var8, "[", "]");
      if (var9 != null && var9.length() > 0) {
         var8 = var8.replace(var9, "").trim();
      }

      String[] var10 = parseValues(var7, var9);
      if (var6 != null && var6.length() > 0) {
         var1 = StrUtils.removePrefix(var1, "/shaders/");
         return new ShaderOptionVariable(var6, var8, var7, var10, var1);
      } else {
         return null;
      }
   }

   public static String[] parseValues(String var0, String var1) {
      String[] var5 = new String[]{var0};
      if (var1 == null) {
         return var5;
      }

      var1 = var1.trim();
      var1 = StrUtils.removePrefix(var1, "[");
      var1 = StrUtils.removeSuffix(var1, "]");
      var1 = var1.trim();
      if (var1.length() <= 0) {
         return var5;
      }

      String[] var6 = Config.tokenize(var1, " ");
      if (var6.length <= 0) {
         return var5;
      }

      if (!Arrays.asList(var6).contains(var0)) {
         var6 = (String[])Config.addObjectToArray(var6, var0, 0);
      }

      return var6;
   }
}
