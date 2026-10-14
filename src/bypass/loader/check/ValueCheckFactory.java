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

   public <T> AbstractValueCheck Z(T object, Function<T, Integer> function, Predicate<Integer> predicate) {
      return new ValueCheck(this, object, function, object, predicate);
   }
}
