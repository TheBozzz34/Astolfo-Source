package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class ClassParser implements Parser<Class> {
   @Override
   public Optional<Class> parse(String string, Class<? extends Class> class2) {
      Class class3;
      try {
         class3 = Class.forName(string);
      } catch (ClassNotFoundException classNotFoundException) {
         classNotFoundException.printStackTrace();
         return Optional.empty();
      }

      return Optional.ofNullable(class3);
   }
}
