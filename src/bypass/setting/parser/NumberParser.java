package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class NumberParser implements Parser<Number> {
   private static final long a = 39344228287756L;

   @Override
   public Optional<Number> parse(String var1, Class<? extends Number> var2) {
      try {
         if (var2 == Byte.class) {
            return Optional.ofNullable(Byte.parseByte(var1));
         } else if (var2 == Short.class) {
            return Optional.ofNullable(Short.parseShort(var1));
         } else if (var2 == Integer.class) {
            return Optional.ofNullable(Integer.parseInt(var1.replace(".0", "")));
         } else if (var2 == Long.class) {
            return Optional.ofNullable(Long.parseLong(var1));
         } else if (var2 == Float.class) {
            return Optional.ofNullable(Float.parseFloat(var1));
         } else {
            return var2 == Double.class ? Optional.ofNullable(Double.parseDouble(var1)) : Optional.empty();
         }
      } catch (Exception var7) {
         return Optional.empty();
      }
   }
}
