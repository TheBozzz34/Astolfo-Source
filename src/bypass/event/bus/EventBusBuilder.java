package bypass.event.bus;

import bypass.event.Event;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBusBuilder<A extends AnnotatedElement> {
   public final Map<Class<? extends Event>, List<Subscription<?, ?>>> n = new HashMap<>();
   private MemberScanner<A> V;
   private ListenerFilter<A> o;
   private InvocationStrategy e;

   public EventBusBuilder<A> e(MemberScanner<A> var1) {
      this.V = var1;
      return this;
   }

   public EventBusBuilder<A> j(ListenerFilter<A> var1) {
      this.o = var1;
      return this;
   }

   public EventBusBuilder<A> v(InvocationStrategy var1) {
      this.e = var1;
      return this;
   }

   public AnnotatedEventBus<A> v() {
      return new AnnotatedEventBus<>(this.n, this.V, this.o, this.e);
   }
}
