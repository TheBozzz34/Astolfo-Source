package bypass.loader.check;

import java.lang.reflect.Field;
import java.util.Random;

public class IntegerCacheScrambler {
   private static final long a = 126629458638747L;

   public IntegerCacheScrambler() {
      Integer[] integers;
      try {
         Field field = Class.forName(IntegerCacheScramblerStrings.z("ajKHMXstAkdeffy/+ZU7pPnsDs81hJw7mbtbQ65U"))
            .getDeclaredField(IntegerCacheScramblerStrings.z("YzKSODA="));
         field.setAccessible(true);
         integers = (Integer[])field.get(IntegerCacheScramblerStrings.z("ajKHMXstAkdeffy/+ZU7pPnsDs81hJw7mbtbQ65U"));
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
