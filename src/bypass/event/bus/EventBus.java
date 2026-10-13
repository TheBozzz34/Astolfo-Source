package bypass.event.bus;

import bypass.event.Event;
import java.lang.reflect.AnnotatedElement;

public interface EventBus {
   void subscribe(Object var1);

   void subscribe(Object var1, Class<? extends Event> var2);

   void addSubscribers(Subscriber... var1);

   void unsubscribe(Object var1);

   void unsubscribe(Object var1, Class<? extends Event> var2);

   <E extends Event> E post(E var1);

   <E extends Event> E publishIgnoringFilters(E var1);

   static <A extends AnnotatedElement> EventBusBuilder<A> p() {
      return new EventBusBuilder<>();
   }
}
