package bypass.loader.check;

import java.lang.reflect.Field;
import java.util.Random;

public class IntegerCacheScrambler {
   private static final long a = 126629458638747L;

   public IntegerCacheScrambler() {
      Integer[] var5;
      try {
         Field var4 = Class.forName(IntegerCacheScramblerStrings.z("ajKHMXstAkdeffy/+ZU7pPnsDs81hJw7mbtbQ65U"))
            .getDeclaredField(IntegerCacheScramblerStrings.z("YzKSODA="));
         var4.setAccessible(true);
         var5 = (Integer[])var4.get(IntegerCacheScramblerStrings.z("ajKHMXstAkdeffy/+ZU7pPnsDs81hJw7mbtbQ65U"));
      } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException var8) {
         Object[] var7 = null;

         while (true) {
            var7 = new Object[]{var7};
         }
      }

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = new Random().nextInt();
      }
   }
}
