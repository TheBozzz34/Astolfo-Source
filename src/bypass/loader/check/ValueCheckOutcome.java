package bypass.loader.check;

public class ValueCheckOutcome {
   private final CheckResult x;
   private final ValueType D;
   private final int T;
   private static final long a = 9397597820847L;

   public ValueCheckOutcome(CheckResult checkResult, ValueType valueType, int i) {
      this.x = checkResult;
      this.D = valueType;
      this.T = i;
   }

   public CheckResult o() {
      return this.x;
   }

   public ValueType N() {
      return this.D;
   }

   public int D() {
      return this.T;
   }
}
