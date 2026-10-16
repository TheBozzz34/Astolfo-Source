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
   public Map<Class<? extends Event>, List<Subscription<?, ?>>> subscriptionMap;
   private final MemberScanner<A> memberScanner;
   private final ListenerFilter<A> listenerFilter;
   private InvocationStrategy invocationStrategy;

   public AnnotatedEventBus(
      Map<Class<? extends Event>, List<Subscription<?, ?>>> map,
      MemberScanner<A> memberScanner,
      ListenerFilter<A> listenerFilter,
      InvocationStrategy invocationStrategy
   ) {
      this.subscriptionMap = map;
      this.memberScanner = memberScanner;
      this.listenerFilter = listenerFilter;
      this.invocationStrategy = invocationStrategy;
   }

   public MemberScanner<A> getMemberScanner() {
      return this.memberScanner;
   }

   @Override
   public void subscribe(Object object) {
      Objects.requireNonNull(object, "The object must not be null");

      for (AnnotatedElement annotatedElement : this.memberScanner.getMembers(object.getClass())) {
         if (this.listenerFilter.isListener((A)annotatedElement)) {
            Subscriber subscriber = Subscription.fromAnnotatedMember(object, annotatedElement, this.invocationStrategy);
            this.addSubscription(subscriber);
         }
      }
   }

   @Override
   public void subscribe(Object object, Class<? extends Event> class2) {
      Objects.requireNonNull(object, "The object must not be null");
      Objects.requireNonNull(class2, "You forgot to specify the event");

      for (AnnotatedElement annotatedElement : this.memberScanner.getMembers(object.getClass())) {
         if (this.listenerFilter.listensTo((A)annotatedElement, class2)) {
            Subscriber subscriber = Subscription.fromAnnotatedMember(object, annotatedElement, this.invocationStrategy);
            this.addSubscription(subscriber);
         }
      }
   }

   @Override
   public void addSubscribers(Subscriber... subscribers) {
      Objects.requireNonNull(subscribers, "The object must not be null");
      if (subscribers.length != 0) {
         for (Subscriber subscriber : subscribers) {
            this.addSubscription(subscriber);
         }
      }
   }

   @Override
   public void unsubscribe(Object object) {
      Objects.requireNonNull(object, "The object must not be null");
      this.getSubscriptionMap()
         .keySet()
         .forEach(
            class2 -> this.findSubscriptions((Class<? extends Event>)class2).ifPresent(list -> list.removeIf(subscription -> subscription.getOwner().equals(object)))
         );
   }

   @Override
   public void unsubscribe(Object object, Class<? extends Event> class2) {
      Objects.requireNonNull(object, "The object must not be null");
      Objects.requireNonNull(class2, "The class of the event can't be null");
      if (this.getSubscriptionMap().containsKey(class2)) {
         this.getSubscriptionMap().get(class2).removeIf(subscription -> subscription.getOwner().equals(object));
      }
   }

   @Override
   public <E extends Event> E post(E event) {
      List list = this.getSubscriptionMap().get(event.getClass());
      if (list != null) {
         label29:
         for (Object object : list) {
            Subscription subscription = (Subscription)object;
            Subscription subscription2 = subscription;
            if (subscription2.getFilters() != null) {
               for (SubscriptionFilter subscriptionFilter : subscription2.getFilters()) {
                  if (!subscriptionFilter.test(subscription2, (E)event)) {
                     continue label29;
                  }
               }
            }

            subscription2.invoke(event);
         }
      }

      return (E)event;
   }

   @Override
   public <E extends Event> E publishIgnoringFilters(E event) {
      List list = this.getSubscriptionMap().get(event.getClass());
      if (list != null) {
         list.forEach(object -> ((bypass.event.bus.Subscription)object).invoke(event));
      }

      return (E)event;
   }

   private Optional<List<Subscription<?, ?>>> findSubscriptions(Class<? extends Event> class2) {
      return Optional.ofNullable(this.subscriptionMap.get(class2));
   }

   public <O> void addSubscription(Subscription<O, ?> subscription) {
      Class class2 = subscription.getEventType();
      List list = (java.util.List)(this.findSubscriptions(class2).orElse(new CopyOnWriteArrayList<>()));
      list.add(subscription);
      list.sort(Comparator.comparing((java.util.function.Function<Subscription, java.lang.Integer>)Subscription::getPriority));
      if (!this.getSubscriptionMap().containsKey(class2)) {
         this.getSubscriptionMap().put(class2, list);
      }
   }

   public Map<Class<? extends Event>, List<Subscription<?, ?>>> getSubscriptionMap() {
      return this.subscriptionMap;
   }
}
