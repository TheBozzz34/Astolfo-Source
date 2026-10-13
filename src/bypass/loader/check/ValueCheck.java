package bypass.loader.check;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValueCheck extends AbstractValueCheck {
   public final Function t;
   public final Object w;
   public final Predicate M;
   public final ValueCheckFactory D;
   private static final long a = 60285585691743L;

   public ValueCheck(ValueCheckFactory var1, Object var2, Function var3, Object var4, Predicate var5) {
      super(var2);
      this.D = var1;
      this.t = var3;
      this.w = var4;
      this.M = var5;
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
      int var5 = this.C();
      return new Double(
                  Integer.compare(
                     Arrays.asList(
                           Boolean.valueOf(String.format("%s", String.valueOf(this.M.test(var5) ? this.M.test(var5) : this.M.test(var5))))
                              ? Integer.parseInt("1")
                              : Integer.parseInt("0")
                        )
                        .toArray(new Integer[0])[(int)(!this.M.test(var5) ? 0.0 : Math.floor(0.0))],
                     1
                  )
               )
               .hashCode()
            == Long.valueOf(0L).hashCode()
         ? CheckResult.S(Verdict.H)
         : CheckResult.I();
   }
}
