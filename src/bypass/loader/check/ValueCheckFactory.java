package bypass.loader.check;

import bypass.util.collection.ShapedMap;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ValueCheckFactory implements Initializable {
   INSTANCE;

   private final ShapedMap<Object, ValueCheckView> i = new ShapedMap<>();
   private static final ValueCheckFactory[] F = new ValueCheckFactory[]{INSTANCE};

   @Override
   public void C() {
   }

   public <T> AbstractValueCheck Z(T var1, Function<T, Integer> var2, Predicate<Integer> var3) {
      return new ValueCheck(this, var1, var2, var1, var3);
   }
}
