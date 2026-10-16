package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import java.lang.reflect.AnnotatedElement;

public interface Subscription<O, E extends Event> {
   EventPriority DEFAULT_PRIORITY = EventPriority.NORMAL;

   void invoke(E event);

   Class<? extends E> getEventType();

   SubscriptionFilter[] getFilters();

   O getOwner();

   int getPriority();

   Listener<E> getListener();

   static <O> Subscriber<O, ?> fromAnnotatedMember(O object, AnnotatedElement annotatedElement, InvocationStrategy invocationStrategy) {
      return new Subscriber<>(
         (O)object,
         annotatedElement,
         annotatedElement.getDeclaredAnnotation(EventHandler.class),
         annotatedElement.getDeclaredAnnotation(EventFilters.class),
         invocationStrategy
      );
   }

   static <O, E extends Event> Subscriber<O, E> create(O object, Class<E> class2, Listener<E> listener, Class<? extends SubscriptionFilter<E>>... classs) {
      return createWithPriority((O)object, class2, listener, DEFAULT_PRIORITY, classs);
   }

   static <O, E extends Event> Subscriber<O, E> createWithPriority(
      O object, Class<E> class2, Listener<E> listener, EventPriority eventPriority, Class<? extends SubscriptionFilter<E>>... classs
   ) {
      return new Subscriber<>((O)object, class2, listener, eventPriority.getLevel(), classs);
   }
}
