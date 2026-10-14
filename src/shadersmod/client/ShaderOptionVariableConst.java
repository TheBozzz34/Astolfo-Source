package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.StrUtils;

public class ShaderOptionVariableConst extends ShaderOptionVariable {
   private String type = null;
   private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*(float|int)\\s*([A-Za-z0-9_]+)\\s*=\\s*(-?[0-9\\.]+f?F?)\\s*;\\s*(//.*)?$");

   public ShaderOptionVariableConst(String name, String typeIn, String description, String value, String[] values, String path) {
      super(name, description, value, values, path);
      this.type = typeIn;
   }

   @Override
   public String getSourceLine() {
      return "const " + this.type + " " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher2 = PATTERN_CONST.matcher(line);
      if (!matcher2.matches()) {
         return false;
      }

      String string = matcher2.group(2);
      return string.matches(this.getName());
   }

   public static ShaderOption f(String string, String string2) {
      Matcher matcher2 = PATTERN_CONST.matcher(string);
      if (!matcher2.matches()) {
         return null;
      }

      String string3 = matcher2.group(1);
      String string4 = matcher2.group(2);
      String string5 = matcher2.group(3);
      String string6 = matcher2.group(4);
      String string7 = StrUtils.getSegment(string6, "[", "]");
      if (string7 != null && string7.length() > 0) {
         string6 = string6.replace(string7, "").trim();
      }

      String[] strings = parseValues(string5, string7);
      if (string4 != null && string4.length() > 0) {
         string2 = StrUtils.removePrefix(string2, "/shaders/");
         return new ShaderOptionVariableConst(string4, string3, string6, string5, strings, string2);
      } else {
         return null;
      }
   }
}
