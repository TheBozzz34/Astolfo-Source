package bypass.loader.check;

public abstract class AbstractValueCheck implements ValueCheckView {
   private final Object k;

   public AbstractValueCheck(Object var1) {
      this.k = var1;
   }

   public Object Q() {
      return this.k;
   }
}
