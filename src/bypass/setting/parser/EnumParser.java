package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class EnumParser implements Parser<Enum<?>> {
   @Override
   public Optional<Enum<?>> parse(String string, Class<? extends Enum<?>> class2) {
      for (Enum enum2 : (Enum[])class2.getEnumConstants()) {
         if (enum2.name().equalsIgnoreCase(string)) {
            return Optional.ofNullable(enum2);
         }
      }

      return Optional.empty();
   }
}
