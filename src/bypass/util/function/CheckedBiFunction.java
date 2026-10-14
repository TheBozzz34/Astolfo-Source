package bypass.util.function;

import java.util.function.BiFunction;

@FunctionalInterface
public interface CheckedBiFunction<A, B, R> extends BiFunction<A, B, R>, Checked {
   @Override
   R apply(A object, B object2);
}
