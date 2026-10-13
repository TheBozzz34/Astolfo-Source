package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class StringParser implements Parser<String> {
   @Override
   public Optional<String> parse(String var1, Class<? extends String> var2) {
      return Optional.of(var1);
   }
}
