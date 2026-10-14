package bypass.util.container;

public abstract class Mutator<T> extends Remover<T> {
   public abstract void q(T... objects);
}
