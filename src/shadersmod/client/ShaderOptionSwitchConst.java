package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.StrUtils;

public class ShaderOptionSwitchConst extends ShaderOptionSwitch {
   private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*bool\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\s*;\\s*(//.*)?$");
   private static final long d = 84659284011603L;

   public ShaderOptionSwitchConst(String name, String description, String value, String path) {
      super(name, description, value, path);
   }

   @Override
   public String getSourceLine() {
      return "const bool " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   public static ShaderOption parseOption(String line, String path) {
      Matcher matcher2 = PATTERN_CONST.matcher(line);
      if (!matcher2.matches()) {
         return null;
      } else {
         String string = matcher2.group(1);
         String string2 = matcher2.group(2);
         String string3 = matcher2.group(3);
         if (string != null && string.length() > 0) {
            path = StrUtils.removePrefix(path, "/shaders/");
            ShaderOptionSwitchConst shaderOptionSwitchConst = new ShaderOptionSwitchConst(string, string3, string2, path);
            shaderOptionSwitchConst.setVisible(false);
            return shaderOptionSwitchConst;
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher2 = PATTERN_CONST.matcher(line);
      if (!matcher2.matches()) {
         return false;
      }

      String string = matcher2.group(1);
      return string.matches(this.getName());
   }

   @Override
   public boolean checkUsed() {
      return false;
   }
}
