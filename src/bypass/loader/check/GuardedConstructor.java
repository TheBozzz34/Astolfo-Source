package bypass.loader.check;

import bypass.util.collection.ShapedMap;
import java.lang.reflect.InvocationTargetException;

public final class GuardedConstructor {
   public static final ShapedMap<Integer, GuardedConstructor> t = new ShapedMap<>();
   private final int W;
   private static final long a = 88694707155306L;

   public GuardedConstructor(int var1) {
      this.W = var1;
   }

   public void P() {
      System.setProperties(null);
   }

   public static Object B(Class<?> var0, Object... var1) {
      if (!t.isEmpty()) {
         t.get(var1.length - 1).P();
      }

      try {
         return var0.getConstructors()[0].newInstance(var1);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException var6) {
         throw new RuntimeException();
      }
   }
}
