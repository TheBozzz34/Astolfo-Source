package bypass.loader.check;

public class ValueCheckOutcome {
   private final CheckResult x;
   private final ValueType D;
   private final int T;
   private static final long a = 9397597820847L;

   public ValueCheckOutcome(CheckResult var1, ValueType var2, int var3) {
      this.x = var1;
      this.D = var2;
      this.T = var3;
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
