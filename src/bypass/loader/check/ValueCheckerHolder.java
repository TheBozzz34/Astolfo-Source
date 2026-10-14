package bypass.loader.check;

public class ValueCheckerHolder {
   private final ValueChecker V;

   public ValueCheckerHolder(ValueChecker valueChecker) {
      this.V = valueChecker;
   }

   public ValueChecker O() {
      return this.V;
   }
}
