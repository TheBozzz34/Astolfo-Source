package bypass.loader.check;

public abstract class AbstractValueCheck implements ValueCheckView {
   private final Object value;

   public AbstractValueCheck(Object object) {
      this.value = object;
   }

   public Object getValue() {
      return this.value;
   }
}
