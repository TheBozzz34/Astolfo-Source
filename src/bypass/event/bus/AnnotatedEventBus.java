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

   public AnnotatedEventBus(Map<Class<? extends Event>, List<Subscription<?, ?>>> var1, MemberScanner<A> var2, ListenerFilter<A> var3, InvocationStrategy var4) {
      this.N = var1;
      this.B = var2;
      this.C = var3;
      this.t = var4;
   }

   public MemberScanner<A> N() {
      return this.B;
   }

   @Override
   public void subscribe(Object var1) {
      Objects.requireNonNull(var1, "The object must not be null");

      for (AnnotatedElement var8 : this.B.getMembers(var1.getClass())) {
         if (this.C.isListener((A)var8)) {
            Subscriber var9 = Subscription.l(var1, var8, this.t);
            this.b(var9);
         }
      }
   }

   @Override
   public void subscribe(Object var1, Class<? extends Event> var2) {
      Objects.requireNonNull(var1, "The object must not be null");
      Objects.requireNonNull(var2, "You forgot to specify the event");

      for (AnnotatedElement var9 : this.B.getMembers(var1.getClass())) {
         if (this.C.listensTo((A)var9, var2)) {
            Subscriber var10 = Subscription.l(var1, var9, this.t);
            this.b(var10);
         }
      }
   }

   @Override
   public void addSubscribers(Subscriber... var1) {
      Objects.requireNonNull(var1, "The object must not be null");
      if (var1.length != 0) {
         for (Subscriber var8 : var1) {
            this.b(var8);
         }
      }
   }

   @Override
   public void unsubscribe(Object var1) {
      Objects.requireNonNull(var1, "The object must not be null");
      this.g().keySet().forEach(var2 -> this.N((Class<? extends Event>)var2).ifPresent(var1xx -> var1xx.removeIf(var1xxx -> var1xxx.t().equals(var1))));
   }

   @Override
   public void unsubscribe(Object var1, Class<? extends Event> var2) {
      Objects.requireNonNull(var1, "The object must not be null");
      Objects.requireNonNull(var2, "The class of the event can't be null");
      if (this.g().containsKey(var2)) {
         this.g().get(var2).removeIf(var1x -> var1x.t().equals(var1));
      }
   }

   @Override
   public <E extends Event> E post(E var1) {
      List var5 = this.g().get(var1.getClass());
      if (var5 != null) {
         label29:
         for (Object var7$raw : var5) {
            Subscription var7 = (Subscription)var7$raw;
            Subscription var8 = var7;
            if (var8.i() != null) {
               for (SubscriptionFilter var12 : var8.i()) {
                  if (!var12.test(var8, (E)var1)) {
                     continue label29;
                  }
               }
            }

            var8.C(var1);
         }
      }

      return (E)var1;
   }

   @Override
   public <E extends Event> E publishIgnoringFilters(E var1) {
      List var5 = this.g().get(var1.getClass());
      if (var5 != null) {
         var5.forEach(var1x -> ((bypass.event.bus.Subscription)var1x).C(var1));
      }

      return (E)var1;
   }

   private Optional<List<Subscription<?, ?>>> N(Class<? extends Event> var1) {
      return Optional.ofNullable(this.N.get(var1));
   }

   public <O> void b(Subscription<O, ?> var1) {
      Class var6 = var1.V();
      List var7 = (java.util.List)(this.N(var6).orElse(new CopyOnWriteArrayList<>()));
      var7.add(var1);
      var7.sort(Comparator.comparing((java.util.function.Function<Subscription, java.lang.Integer>)Subscription::l));
      if (!this.g().containsKey(var6)) {
         this.g().put(var6, var7);
      }
   }

   public Map<Class<? extends Event>, List<Subscription<?, ?>>> g() {
      return this.N;
   }
}
