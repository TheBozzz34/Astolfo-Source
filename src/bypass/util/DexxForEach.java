package bypass.util;

import com.github.andrewoma.dexx.collection.Function;

public class DexxForEach {
   private static final long a = 118632293034372L;

   public static <E, U> void f(Iterable<E> var0, Function<E, U> var1) {
      for (Object var6 : var0) {
         var1.invoke((E)(var6));
      }
   }
}
