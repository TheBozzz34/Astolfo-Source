package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class NumberParser implements Parser<Number> {
   private static final long a = 39344228287756L;

   @Override
   public Optional<Number> parse(String string, Class<? extends Number> class2) {
      try {
         if (class2 == Byte.class) {
            return Optional.ofNullable(Byte.parseByte(string));
         } else if (class2 == Short.class) {
            return Optional.ofNullable(Short.parseShort(string));
         } else if (class2 == Integer.class) {
            return Optional.ofNullable(Integer.parseInt(string.replace(".0", "")));
         } else if (class2 == Long.class) {
            return Optional.ofNullable(Long.parseLong(string));
         } else if (class2 == Float.class) {
            return Optional.ofNullable(Float.parseFloat(string));
         } else {
            return class2 == Double.class ? Optional.ofNullable(Double.parseDouble(string)) : Optional.empty();
         }
      } catch (Exception exception) {
         return Optional.empty();
      }
   }
}
