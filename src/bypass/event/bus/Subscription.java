package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import java.lang.reflect.AnnotatedElement;

public interface Subscription<O, E extends Event> {
   EventPriority g = EventPriority.NORMAL;

   void C(E event);

   Class<? extends E> V();

   SubscriptionFilter[] i();

   O t();

   int l();

   Listener<E> P();

   static <O> Subscriber<O, ?> l(O object, AnnotatedElement annotatedElement, InvocationStrategy invocationStrategy) {
      return new Subscriber<>(
         (O)object,
         annotatedElement,
         annotatedElement.getDeclaredAnnotation(EventHandler.class),
         annotatedElement.getDeclaredAnnotation(EventFilters.class),
         invocationStrategy
      );
   }

   static <O, E extends Event> Subscriber<O, E> N(O object, Class<E> class2, Listener<E> listener, Class<? extends SubscriptionFilter<E>>... classs) {
      return K((O)object, class2, listener, g, classs);
   }

   static <O, E extends Event> Subscriber<O, E> K(
      O object, Class<E> class2, Listener<E> listener, EventPriority eventPriority, Class<? extends SubscriptionFilter<E>>... classs
   ) {
      return new Subscriber<>((O)object, class2, listener, eventPriority.J(), classs);
   }
}
