package bypass.util.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<A> extends Consumer<A>, Checked {
   @Override
   void accept(A var1);
}
