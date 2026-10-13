package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.StrUtils;

public class ShaderOptionVariableConst extends ShaderOptionVariable {
   private String type = null;
   private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*(float|int)\\s*([A-Za-z0-9_]+)\\s*=\\s*(-?[0-9\\.]+f?F?)\\s*;\\s*(//.*)?$");
   private static final long f = 36180113447175L;

   public ShaderOptionVariableConst(String var1, String var2, String var3, String var4, String[] var5, String var6) {
      super(var1, var3, var4, var5, var6);
      this.type = var2;
   }

   @Override
   public String getSourceLine() {
      return "const " + this.type + " " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   @Override
   public boolean matchesLine(String var1) {
      Matcher var5 = PATTERN_CONST.matcher(var1);
      if (!var5.matches()) {
         return false;
      }

      String var6 = var5.group(2);
      return var6.matches(this.getName());
   }

   public static ShaderOption f(String var0, String var1) {
      Matcher var5 = PATTERN_CONST.matcher(var0);
      if (!var5.matches()) {
         return null;
      }

      String var6 = var5.group(1);
      String var7 = var5.group(2);
      String var8 = var5.group(3);
      String var9 = var5.group(4);
      String var10 = StrUtils.getSegment(var9, "[", "]");
      if (var10 != null && var10.length() > 0) {
         var9 = var9.replace(var10, "").trim();
      }

      String[] var11 = parseValues(var8, var10);
      if (var7 != null && var7.length() > 0) {
         var1 = StrUtils.removePrefix(var1, "/shaders/");
         return new ShaderOptionVariableConst(var7, var6, var9, var8, var11, var1);
      } else {
         return null;
      }
   }
}
