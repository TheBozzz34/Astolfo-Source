package bypass.event.bus;

import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.Listener;
import java.lang.reflect.AnnotatedElement;

public final class Subscriber<O, E extends Event> implements Subscription<O, E> {
   private final O f;
   private final SubscriptionFilter[] y;
   private final int j;
   private final Class<E> N;
   private Listener<E> e;
   private static final long a = 5444996386492L;

   public Subscriber(O object, AnnotatedElement annotatedElement, EventHandler eventHandler, EventFilters eventFilters, InvocationStrategy invocationStrategy) {
      this(
         (O)object,
         (Class<E>)eventHandler.value(),
         invocationStrategy.createDispatcher((Class<E>)eventHandler.value(), object, annotatedElement),
         eventHandler.priority().J(),
         eventFilters != null ? eventFilters.q() : null
      );
   }

   public Subscriber(O object, Class<E> class2, Listener<E> listener, int i, Class<? extends SubscriptionFilter>... classs) {
      this.f = (O)object;
      this.N = class2;
      this.e = listener;
      this.j = i;
      this.y = this.k(classs);
   }

   private SubscriptionFilter[] k(Class<? extends SubscriptionFilter>[] classs) {
      if (classs == null) {
         return null;
      }

      try {
         SubscriptionFilter[] subscriptionFilters = new SubscriptionFilter[classs.length];

         for (int i = 0; i < classs.length; i++) {
            subscriptionFilters[i] = (SubscriptionFilter)classs[i].newInstance();
         }

         return subscriptionFilters;
      } catch (InstantiationException instantiationException) {
         throw new RuntimeException("One or multiple of the specified filter classes can't be instantiated:\n" + instantiationException.getMessage());
      } catch (IllegalAccessException illegalAccessException) {
         throw new RuntimeException("Access exception encountered creating filters for method/field in \"" + this.t().toString() + "\":\n" + illegalAccessException.getMessage());
      }
   }

   @Override
   public O t() {
      return this.f;
   }

   @Override
   public SubscriptionFilter[] i() {
      return this.y;
   }

   @Override
   public void C(E event) {
      this.e.accept((E)event);
   }

   @Override
   public Class<E> V() {
      return this.N;
   }

   @Override
   public int l() {
      return this.j;
   }

   @Override
   public Listener<E> P() {
      return this.e;
   }
}
