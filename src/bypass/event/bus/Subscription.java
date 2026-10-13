package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import java.lang.reflect.AnnotatedElement;

public interface Subscription<O, E extends Event> {
   EventPriority g = EventPriority.NORMAL;

   void C(E var1);

   Class<? extends E> V();

   SubscriptionFilter[] i();

   O t();

   int l();

   Listener<E> P();

   static <O> Subscriber<O, ?> l(O var0, AnnotatedElement var1, InvocationStrategy var2) {
      return new Subscriber<>((O)var0, var1, var1.getDeclaredAnnotation(EventHandler.class), var1.getDeclaredAnnotation(EventFilters.class), var2);
   }

   static <O, E extends Event> Subscriber<O, E> N(O var0, Class<E> var1, Listener<E> var2, Class<? extends SubscriptionFilter<E>>... var3) {
      return K((O)var0, var1, var2, g, var3);
   }

   static <O, E extends Event> Subscriber<O, E> K(O var0, Class<E> var1, Listener<E> var2, EventPriority var3, Class<? extends SubscriptionFilter<E>>... var4) {
      return new Subscriber<>((O)var0, var1, var2, var3.J(), var4);
   }
}
