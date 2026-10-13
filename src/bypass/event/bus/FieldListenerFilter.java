package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.Listener;
import java.lang.reflect.Field;

public class FieldListenerFilter implements ListenerFilter<Field> {
   private static final long a = 72795227400359L;

   public boolean isListener(Field var1) {
      return ListenerFilter.super.isListener(var1) && Listener.class.isAssignableFrom(var1.getType());
   }

   public boolean listensTo(Field var1, Class<? extends Event> var2) {
      return this.isListener(var1) && var1.getDeclaredAnnotation(EventHandler.class).value() == var2;
   }
}
