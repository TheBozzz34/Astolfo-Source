package bypass.event.bus;

import bypass.event.Event;
import java.lang.reflect.AnnotatedElement;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class AnnotatedEventBus<A extends AnnotatedElement> implements EventBus {
   public Map<Class<? extends Event>, List<Subscription<?, ?>>> N;
   private final MemberScanner<A> B;
   private final ListenerFilter<A> C;
   private InvocationStrategy t;
   private static final long a = 59978824583516L;

   public AnnotatedEventBus(
      Map<Class<? extends Event>, List<Subscription<?, ?>>> map,
      MemberScanner<A> memberScanner,
      ListenerFilter<A> listenerFilter,
      InvocationStrategy invocationStrategy
   ) {
      this.N = map;
      this.B = memberScanner;
      this.C = listenerFilter;
      this.t = invocationStrategy;
   }

   public MemberScanner<A> N() {
      return this.B;
   }

   @Override
   public void subscribe(Object object) {
      Objects.requireNonNull(object, "The object must not be null");

      for (AnnotatedElement annotatedElement : this.B.getMembers(object.getClass())) {
         if (this.C.isListener((A)annotatedElement)) {
            Subscriber subscriber = Subscription.l(object, annotatedElement, this.t);
            this.b(subscriber);
         }
      }
   }

   @Override
   public void subscribe(Object object, Class<? extends Event> class2) {
      Objects.requireNonNull(object, "The object must not be null");
      Objects.requireNonNull(class2, "You forgot to specify the event");

      for (AnnotatedElement annotatedElement : this.B.getMembers(object.getClass())) {
         if (this.C.listensTo((A)annotatedElement, class2)) {
            Subscriber subscriber = Subscription.l(object, annotatedElement, this.t);
            this.b(subscriber);
         }
      }
   }

   @Override
   public void addSubscribers(Subscriber... subscribers) {
      Objects.requireNonNull(subscribers, "The object must not be null");
      if (subscribers.length != 0) {
         for (Subscriber subscriber : subscribers) {
            this.b(subscriber);
         }
      }
   }

   @Override
   public void unsubscribe(Object object) {
      Objects.requireNonNull(object, "The object must not be null");
      this.g().keySet().forEach(class2 -> this.N((Class<? extends Event>)class2).ifPresent(list -> list.removeIf(subscription -> subscription.t().equals(object))));
   }

   @Override
   public void unsubscribe(Object object, Class<? extends Event> class2) {
      Objects.requireNonNull(object, "The object must not be null");
      Objects.requireNonNull(class2, "The class of the event can't be null");
      if (this.g().containsKey(class2)) {
         this.g().get(class2).removeIf(subscription -> subscription.t().equals(object));
      }
   }

   @Override
   public <E extends Event> E post(E event) {
      List list = this.g().get(event.getClass());
      if (list != null) {
         label29:
         for (Object object : list) {
            Subscription subscription = (Subscription)object;
            Subscription subscription2 = subscription;
            if (subscription2.i() != null) {
               for (SubscriptionFilter subscriptionFilter : subscription2.i()) {
                  if (!subscriptionFilter.test(subscription2, (E)event)) {
                     continue label29;
                  }
               }
            }

            subscription2.C(event);
         }
      }

      return (E)event;
   }

   @Override
   public <E extends Event> E publishIgnoringFilters(E event) {
      List list = this.g().get(event.getClass());
      if (list != null) {
         list.forEach(object -> ((bypass.event.bus.Subscription)object).C(event));
      }

      return (E)event;
   }

   private Optional<List<Subscription<?, ?>>> N(Class<? extends Event> class2) {
      return Optional.ofNullable(this.N.get(class2));
   }

   public <O> void b(Subscription<O, ?> subscription) {
      Class class2 = subscription.V();
      List list = (java.util.List)(this.N(class2).orElse(new CopyOnWriteArrayList<>()));
      list.add(subscription);
      list.sort(Comparator.comparing((java.util.function.Function<Subscription, java.lang.Integer>)Subscription::l));
      if (!this.g().containsKey(class2)) {
         this.g().put(class2, list);
      }
   }

   public Map<Class<? extends Event>, List<Subscription<?, ?>>> g() {
      return this.N;
   }
}
