package bypass.gui.tabgui;

import java.util.List;

public interface ListAccessor<T> extends ValueAccessor<List<T>> {
   long a = 111981402652216L;

   default T Z(int i) {
      return this.L().get(i);
   }

   default T w() {
      return this.L().size() > 0 ? this.L().get(this.L().size() - 1) : null;
   }

   default void s(T object) {
      this.L().add((T)object);
   }
}
