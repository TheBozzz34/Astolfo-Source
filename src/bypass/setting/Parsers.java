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
   private Map<Class<?>, Parser<?>> B = new HashMap<>();

   public Parsers() {
      this.B.put(Class.class, new ClassParser());
      this.B.put(Boolean.class, new BooleanParser());
      this.B.put(Enum.class, new EnumParser());
      this.B.put(Number.class, new NumberParser());
      this.B.put(Character.class, new CharacterParser());
      this.B.put(String.class, new StringParser());
   }

   public Parser<?> get(Class<?> class2) {
      return this.B.get(class2);
   }
}
