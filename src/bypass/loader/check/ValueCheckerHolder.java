package bypass.loader.check;

public class ValueCheckerHolder {
   private final ValueChecker V;

   public ValueCheckerHolder(ValueChecker var1) {
      this.V = var1;
   }

   public ValueChecker O() {
      return this.V;
   }
}
