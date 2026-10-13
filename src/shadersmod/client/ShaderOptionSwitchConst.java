package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.StrUtils;

public class ShaderOptionSwitchConst extends ShaderOptionSwitch {
   private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*bool\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\s*;\\s*(//.*)?$");
   private static final long d = 84659284011603L;

   public ShaderOptionSwitchConst(String var1, String var2, String var3, String var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public String getSourceLine() {
      return "const bool " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   public static ShaderOption parseOption(String var0, String var1) {
      Matcher var5 = PATTERN_CONST.matcher(var0);
      if (!var5.matches()) {
         return null;
      } else {
         String var6 = var5.group(1);
         String var7 = var5.group(2);
         String var8 = var5.group(3);
         if (var6 != null && var6.length() > 0) {
            var1 = StrUtils.removePrefix(var1, "/shaders/");
            ShaderOptionSwitchConst var9 = new ShaderOptionSwitchConst(var6, var8, var7, var1);
            var9.setVisible(false);
            return var9;
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean matchesLine(String var1) {
      Matcher var5 = PATTERN_CONST.matcher(var1);
      if (!var5.matches()) {
         return false;
      }

      String var6 = var5.group(1);
      return var6.matches(this.getName());
   }

   @Override
   public boolean checkUsed() {
      return false;
   }
}
