package bypass.event.bus;

import bypass.event.Event;
import java.lang.reflect.AnnotatedElement;

public interface EventBus {
   void subscribe(Object object);

   void subscribe(Object object, Class<? extends Event> class2);

   void addSubscribers(Subscriber... subscribers);

   void unsubscribe(Object object);

   void unsubscribe(Object object, Class<? extends Event> class2);

   <E extends Event> E post(E event);

   <E extends Event> E publishIgnoringFilters(E event);

   static <A extends AnnotatedElement> EventBusBuilder<A> p() {
      return new EventBusBuilder<>();
   }
}
