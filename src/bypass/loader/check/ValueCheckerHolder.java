package bypass.loader.check;

public class ValueCheckerHolder {
   private final ValueChecker checker;

   public ValueCheckerHolder(ValueChecker valueChecker) {
      this.checker = valueChecker;
   }

   public ValueChecker getChecker() {
      return this.checker;
   }
}
