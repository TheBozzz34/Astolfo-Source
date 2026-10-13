package bypass.util;

public interface SelfTyped<A> {
   default <B extends A> B self() {
      return (B)this;
   }
}
