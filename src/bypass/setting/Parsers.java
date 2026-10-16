package bypass.setting;

import bypass.setting.parser.BooleanParser;
import bypass.setting.parser.CharacterParser;
import bypass.setting.parser.ClassParser;
import bypass.setting.parser.EnumParser;
import bypass.setting.parser.NumberParser;
import bypass.setting.parser.StringParser;
import java.util.HashMap;
import java.util.Map;

public class Parsers {
   private Map<Class<?>, Parser<?>> parsers = new HashMap<>();

   public Parsers() {
      this.parsers.put(Class.class, new ClassParser());
      this.parsers.put(Boolean.class, new BooleanParser());
      this.parsers.put(Enum.class, new EnumParser());
      this.parsers.put(Number.class, new NumberParser());
      this.parsers.put(Character.class, new CharacterParser());
      this.parsers.put(String.class, new StringParser());
   }

   public Parser<?> get(Class<?> class2) {
      return this.parsers.get(class2);
   }
}
