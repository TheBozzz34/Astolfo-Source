package bypass.loader.check;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValueCheck extends AbstractValueCheck {
   public final Function valueFunction;
   public final Object input;
   public final Predicate predicate;
   public final ValueCheckFactory factory;

   public ValueCheck(ValueCheckFactory valueCheckFactory, Object object, Function function, Object object2, Predicate predicate) {
      super(object);
      this.factory = valueCheckFactory;
      this.valueFunction = function;
      this.input = object2;
      this.predicate = predicate;
   }

   @Override
   public int computeValue() {
      return (Integer)this.valueFunction.apply(this.input);
   }

   @Override
   public ValueType getType() {
      return ValueType.compactHashMap.get(this.input.getClass());
   }

   @Override
   public CheckResult evaluate() {
      int i = this.computeValue();
      return new Double(
                  Integer.compare(
                     Arrays.asList(
                           Boolean.valueOf(
                                 String.format("%s", String.valueOf(this.predicate.test(i) ? this.predicate.test(i) : this.predicate.test(i)))
                              )
                              ? Integer.parseInt("1")
                              : Integer.parseInt("0")
                        )
                        .toArray(new Integer[0])[(int)(!this.predicate.test(i) ? 0.0 : Math.floor(0.0))],
                     1
                  )
               )
               .hashCode()
            == Long.valueOf(0L).hashCode()
         ? CheckResult.ofVerdict(Verdict.H)
         : CheckResult.undecided();
   }
}
