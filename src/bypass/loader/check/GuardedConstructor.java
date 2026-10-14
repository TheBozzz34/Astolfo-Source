package bypass.loader.check;

import bypass.util.collection.ShapedMap;
import java.lang.reflect.InvocationTargetException;

public final class GuardedConstructor {
   public static final ShapedMap<Integer, GuardedConstructor> t = new ShapedMap<>();
   private final int W;
   private static final long a = 88694707155306L;

   public GuardedConstructor(int i) {
      this.W = i;
   }

   public void P() {
      System.setProperties(null);
   }

   public static Object B(Class<?> class2, Object... objects) {
      if (!t.isEmpty()) {
         t.get(objects.length - 1).P();
      }

      try {
         return class2.getConstructors()[0].newInstance(objects);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException value) {
         throw new RuntimeException();
      }
   }
}
