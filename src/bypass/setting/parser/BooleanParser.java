package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class BooleanParser implements Parser<Boolean> {
   @Override
   public Optional<Boolean> parse(String string, Class<? extends Boolean> class2) {
      return Optional.ofNullable(Boolean.parseBoolean(string));
   }
}
