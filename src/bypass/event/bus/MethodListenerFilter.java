package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import java.lang.reflect.Method;

public class MethodListenerFilter implements ListenerFilter<Method> {
   private static final long a = 93278108644666L;

   public boolean isListener(Method var1) {
      Class[] var6 = var1.getParameterTypes();
      return ListenerFilter.super.isListener(var1) && var6.length == 1 && Event.class.isAssignableFrom(var6[0]);
   }

   public boolean listensTo(Method var1, Class<? extends Event> var2) {
      return this.isListener(var1) && var1.getDeclaredAnnotation(EventHandler.class).value() == var2;
   }
}
