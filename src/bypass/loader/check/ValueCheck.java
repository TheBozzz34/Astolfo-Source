package bypass.loader.check;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValueCheck extends AbstractValueCheck {
   public final Function t;
   public final Object w;
   public final Predicate M;
   public final ValueCheckFactory D;

   public ValueCheck(ValueCheckFactory valueCheckFactory, Object object, Function function, Object object2, Predicate predicate) {
      super(object);
      this.D = valueCheckFactory;
      this.t = function;
      this.w = object2;
      this.M = predicate;
   }

   @Override
   public int C() {
      return (Integer)this.t.apply(this.w);
   }

   @Override
   public ValueType o() {
      return ValueType.compactHashMap.get(this.w.getClass());
   }

   @Override
   public CheckResult L() {
      int i = this.C();
      return new Double(
                  Integer.compare(
                     Arrays.asList(
                           Boolean.valueOf(String.format("%s", String.valueOf(this.M.test(i) ? this.M.test(i) : this.M.test(i))))
                              ? Integer.parseInt("1")
                              : Integer.parseInt("0")
                        )
                        .toArray(new Integer[0])[(int)(!this.M.test(i) ? 0.0 : Math.floor(0.0))],
                     1
                  )
               )
               .hashCode()
            == Long.valueOf(0L).hashCode()
         ? CheckResult.S(Verdict.H)
         : CheckResult.I();
   }
}
