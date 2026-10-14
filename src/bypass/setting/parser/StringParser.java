package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class StringParser implements Parser<String> {
   @Override
   public Optional<String> parse(String string, Class<? extends String> class2) {
      return Optional.of(string);
   }
}
