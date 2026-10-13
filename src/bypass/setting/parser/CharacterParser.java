package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class CharacterParser implements Parser<Character> {
   private static final long a = 111415329009674L;

   @Override
   public Optional<Character> parse(String var1, Class<? extends Character> var2) {
      if (var1.length() > 1) {
         try {
            throw new Exception("Can't parse a value which is larger than I expected");
         } catch (Exception var7) {
            var7.printStackTrace();
            return Optional.empty();
         }
      } else {
         Character var6 = var1.charAt(0);
         return Optional.ofNullable(var6);
      }
   }

   public Optional<Character[]> W(String var1, Class<? extends Character> var2) {
      int var7 = var1.length();
      Character[] var8 = new Character[var7];

      for (int var9 = 0; var9 < var7; var9++) {
         var8[var9] = new Character(var1.charAt(var9));
      }

      return Optional.ofNullable(var8);
   }
}
