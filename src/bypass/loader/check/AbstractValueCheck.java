package bypass.loader.check;

public abstract class AbstractValueCheck implements ValueCheckView {
   private final Object k;

   public AbstractValueCheck(Object object) {
      this.k = object;
   }

   public Object Q() {
      return this.k;
   }
}
