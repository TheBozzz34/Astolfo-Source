package bypass.setting.parser;

import bypass.setting.Parser;
import java.util.Optional;

public class CharacterParser implements Parser<Character> {
   private static final long a = 111415329009674L;

   @Override
   public Optional<Character> parse(String string, Class<? extends Character> class2) {
      if (string.length() > 1) {
         try {
            throw new Exception("Can't parse a value which is larger than I expected");
         } catch (Exception exception) {
            exception.printStackTrace();
            return Optional.empty();
         }
      } else {
         Character character = string.charAt(0);
         return Optional.ofNullable(character);
      }
   }

   public Optional<Character[]> W(String string, Class<? extends Character> class2) {
      int i = string.length();
      Character[] characters = new Character[i];

      for (int j = 0; j < i; j++) {
         characters[j] = new Character(string.charAt(j));
      }

      return Optional.ofNullable(characters);
   }
}
