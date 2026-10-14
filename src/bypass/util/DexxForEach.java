package bypass.util;

import com.github.andrewoma.dexx.collection.Function;

public class DexxForEach {
   public static <E, U> void f(Iterable<E> iterable, Function<E, U> function) {
      for (Object object : iterable) {
         function.invoke((E)(object));
      }
   }
}
