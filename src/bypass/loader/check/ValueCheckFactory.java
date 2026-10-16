package bypass.loader.check;

import bypass.util.collection.ShapedMap;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ValueCheckFactory implements Initializable {
   INSTANCE;

   private final ShapedMap<Object, ValueCheckView> checkCache = new ShapedMap<>();
   private static final ValueCheckFactory[] VALUES = new ValueCheckFactory[]{INSTANCE};

   @Override
   public void initialize() {
   }

   public <T> AbstractValueCheck createCheck(T object, Function<T, Integer> function, Predicate<Integer> predicate) {
      return new ValueCheck(this, object, function, object, predicate);
   }
}
