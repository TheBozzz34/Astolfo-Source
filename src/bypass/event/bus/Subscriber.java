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

   public Subscriber(O var1, AnnotatedElement var2, EventHandler var3, EventFilters var4, InvocationStrategy var5) {
      this((O)var1, (Class<E>)var3.value(), var5.createDispatcher((Class<E>)var3.value(), var1, var2), var3.priority().J(), var4 != null ? var4.q() : null);
   }

   public Subscriber(O var1, Class<E> var2, Listener<E> var3, int var4, Class<? extends SubscriptionFilter>... var5) {
      this.f = (O)var1;
      this.N = var2;
      this.e = var3;
      this.j = var4;
      this.y = this.k(var5);
   }

   private SubscriptionFilter[] k(Class<? extends SubscriptionFilter>[] var1) {
      if (var1 == null) {
         return null;
      }

      try {
         SubscriptionFilter[] var5 = new SubscriptionFilter[var1.length];

         for (int var6 = 0; var6 < var1.length; var6++) {
            var5[var6] = (SubscriptionFilter)var1[var6].newInstance();
         }

         return var5;
      } catch (InstantiationException var7) {
         throw new RuntimeException("One or multiple of the specified filter classes can't be instantiated:\n" + var7.getMessage());
      } catch (IllegalAccessException var8) {
         throw new RuntimeException("Access exception encountered creating filters for method/field in \"" + this.t().toString() + "\":\n" + var8.getMessage());
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
   public void C(E var1) {
      this.e.accept((E)var1);
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
