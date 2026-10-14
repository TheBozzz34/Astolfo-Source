package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.minecraft.src.Lang;
import net.minecraft.src.StrUtils;

public class ShaderOptionSwitch extends ShaderOption {
   private static final Pattern PATTERN_DEFINE = Pattern.compile("^\\s*(//)?\\s*#define\\s+([A-Za-z0-9_]+)\\s*(//.*)?$");
   private static final Pattern PATTERN_IFDEF = Pattern.compile("^\\s*#if(n)?def\\s+([A-Za-z0-9_]+)(\\s*)?$");

   public ShaderOptionSwitch(String name, String description, String value, String path) {
      super(name, description, value, new String[]{"true", "false"}, value, path);
   }

   @Override
   public String getSourceLine() {
      return isTrue(this.getValue()) ? "#define " + this.getName() + " // Shader option ON" : "//#define " + this.getName() + " // Shader option OFF";
   }

   @Override
   public String getValueText(String val) {
      return isTrue(val) ? Lang.getOn() : Lang.getOff();
   }

   @Override
   public String getValueColor(String val) {
      return isTrue(val) ? "§a" : "§c";
   }

   public static ShaderOption parseOption_ShaderOption(String string, String string2) {
      Matcher matcher2 = PATTERN_DEFINE.matcher(string);
      if (!matcher2.matches()) {
         return null;
      } else {
         String string3 = matcher2.group(1);
         String string4 = matcher2.group(2);
         String string5 = matcher2.group(3);
         if (string4 != null && string4.length() > 0) {
            boolean bl = Config.equals(string3, "//");
            boolean bl2 = !bl;
            string2 = StrUtils.removePrefix(string2, "/shaders/");
            return new ShaderOptionSwitch(string4, string5, String.valueOf(bl2), string2);
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher2 = PATTERN_DEFINE.matcher(line);
      if (!matcher2.matches()) {
         return false;
      }

      String string = matcher2.group(2);
      return string.matches(this.getName());
   }

   @Override
   public boolean checkUsed() {
      return true;
   }

   @Override
   public boolean isUsedInLine(String line) {
      Matcher matcher2 = PATTERN_IFDEF.matcher(line);
      if (matcher2.matches()) {
         String string = matcher2.group(2);
         if (string.equals(this.getName())) {
            return true;
         }
      }

      return false;
   }

   public static boolean isTrue(String val) {
      return Boolean.valueOf(val);
   }
}
