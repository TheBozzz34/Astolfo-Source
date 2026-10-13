package bypass.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface CheckedFunction<A, R> extends Function<A, R>, Checked {
   @Override
   R apply(A var1);
}
