package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class EnumParser implements Parser<Enum<?>> {
   private static final long a = 115948171567273L;

   @Override
   public Optional<Enum<?>> parse(String var1, Class<? extends Enum<?>> var2) {
      for (Enum var9 : (Enum[])var2.getEnumConstants()) {
         if (var9.name().equalsIgnoreCase(var1)) {
            return Optional.ofNullable(var9);
         }
      }

      return Optional.empty();
   }
}
