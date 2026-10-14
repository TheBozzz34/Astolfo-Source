package bypass.loader.check;

import java.lang.reflect.Field;
import java.util.Random;

public class IntegerCacheScrambler {
   public IntegerCacheScrambler() {
      Integer[] integers;
      try {
         Field field = Class.forName("java.lang.Integer$IntegerCache").getDeclaredField("cache");
         field.setAccessible(true);
         integers = (Integer[])field.get("java.lang.Integer$IntegerCache");
      } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException value) {
         Object[] objects = null;

         while (true) {
            objects = new Object[]{objects};
         }
      }

      for (int i = 0; i < integers.length; i++) {
         integers[i] = new Random().nextInt();
      }
   }
}
