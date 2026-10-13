package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class ClassParser implements Parser<Class> {
   @Override
   public Optional<Class> parse(String var1, Class<? extends Class> var2) {
      Class var3;
      try {
         var3 = Class.forName(var1);
      } catch (ClassNotFoundException var5) {
         var5.printStackTrace();
         return Optional.empty();
      }

      return Optional.ofNullable(var3);
   }
}
