package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.Listener;
import java.lang.reflect.Field;

public class FieldListenerFilter implements ListenerFilter<Field> {
   public boolean isListener(Field field) {
      return ListenerFilter.super.isListener(field) && Listener.class.isAssignableFrom(field.getType());
   }

   public boolean listensTo(Field field, Class<? extends Event> class2) {
      return this.isListener(field) && field.getDeclaredAnnotation(EventHandler.class).value() == class2;
   }
}
