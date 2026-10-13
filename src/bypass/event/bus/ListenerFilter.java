package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import java.lang.reflect.AnnotatedElement;

public interface ListenerFilter<A extends AnnotatedElement> {
   default boolean isListener(A var1) {
      return var1.isAnnotationPresent(EventHandler.class);
   }

   boolean listensTo(A var1, Class<? extends Event> var2);

   static FieldListenerFilter W() {
      return new FieldListenerFilter();
   }

   static MethodListenerFilter q() {
      return new MethodListenerFilter();
   }
}
