package bypass.event.bus;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public interface MemberScanner<E extends AnnotatedElement> {
   void collect(Class<?> class2, List<E> list);

   E[] getMembers(Class<?> class2);

   boolean hides(E annotatedElement, E annotatedElement2);

   static FieldScanner z() {
      return new FieldScanner();
   }

   static MethodScanner I() {
      return new MethodScanner();
   }
}
