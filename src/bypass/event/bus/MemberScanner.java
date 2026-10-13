package bypass.event.bus;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public interface MemberScanner<E extends AnnotatedElement> {
   void collect(Class<?> var1, List<E> var2);

   E[] getMembers(Class<?> var1);

   boolean hides(E var1, E var2);

   static FieldScanner z() {
      return new FieldScanner();
   }

   static MethodScanner I() {
      return new MethodScanner();
   }
}
