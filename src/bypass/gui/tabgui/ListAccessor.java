package bypass.gui.tabgui;

import java.util.List;

public interface ListAccessor<T> extends ValueAccessor<List<T>> {
   default T getElement(int i) {
      return this.getValue().get(i);
   }

   default T getLast() {
      return this.getValue().size() > 0 ? this.getValue().get(this.getValue().size() - 1) : null;
   }

   default void addElement(T object) {
      this.getValue().add((T)object);
   }
}
