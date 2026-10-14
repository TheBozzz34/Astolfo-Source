package shadersmod.client;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.minecraft.src.StrUtils;

public class ShaderOptionVariable extends ShaderOption {
   private static final Pattern PATTERN_VARIABLE = Pattern.compile("^\\s*#define\\s+(\\w+)\\s+(-?[0-9\\.Ff]+|\\w+)\\s*(//.*)?$");

   public ShaderOptionVariable(String name, String description, String value, String[] values, String path) {
      super(name, description, value, values, value, path);
      this.setVisible(this.getValues().length > 1);
   }

   @Override
   public String getSourceLine() {
      return "#define " + this.getName() + " " + this.getValue() + " // Shader option " + this.getValue();
   }

   @Override
   public String getValueColor(String val) {
      return "§a";
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher2 = PATTERN_VARIABLE.matcher(line);
      if (!matcher2.matches()) {
         return false;
      }

      String string = matcher2.group(1);
      return string.matches(this.getName());
   }

   public static ShaderOption parseOption(String line, String path) {
      Matcher matcher2 = PATTERN_VARIABLE.matcher(line);
      if (!matcher2.matches()) {
         return null;
      }

      String string = matcher2.group(1);
      String string2 = matcher2.group(2);
      String string3 = matcher2.group(3);
      String string4 = StrUtils.getSegment(string3, "[", "]");
      if (string4 != null && string4.length() > 0) {
         string3 = string3.replace(string4, "").trim();
      }

      String[] strings = parseValues(string2, string4);
      if (string != null && string.length() > 0) {
         path = StrUtils.removePrefix(path, "/shaders/");
         return new ShaderOptionVariable(string, string3, string2, strings, path);
      } else {
         return null;
      }
   }

   public static String[] parseValues(String value, String valuesStr) {
      String[] strings = new String[]{value};
      if (valuesStr == null) {
         return strings;
      }

      valuesStr = valuesStr.trim();
      valuesStr = StrUtils.removePrefix(valuesStr, "[");
      valuesStr = StrUtils.removeSuffix(valuesStr, "]");
      valuesStr = valuesStr.trim();
      if (valuesStr.length() <= 0) {
         return strings;
      }

      String[] strings2 = Config.tokenize(valuesStr, " ");
      if (strings2.length <= 0) {
         return strings;
      }

      if (!Arrays.asList(strings2).contains(value)) {
         strings2 = (String[])Config.addObjectToArray(strings2, value, 0);
      }

      return strings2;
   }
}
