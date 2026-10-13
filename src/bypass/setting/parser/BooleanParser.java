package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class BooleanParser implements Parser<Boolean> {
   @Override
   public Optional<Boolean> parse(String var1, Class<? extends Boolean> var2) {
      return Optional.ofNullable(Boolean.parseBoolean(var1));
   }
}
