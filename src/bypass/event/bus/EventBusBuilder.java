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

   public EventBusBuilder<A> e(MemberScanner<A> memberScanner) {
      this.V = memberScanner;
      return this;
   }

   public EventBusBuilder<A> j(ListenerFilter<A> listenerFilter) {
      this.o = listenerFilter;
      return this;
   }

   public EventBusBuilder<A> v(InvocationStrategy invocationStrategy) {
      this.e = invocationStrategy;
      return this;
   }

   public AnnotatedEventBus<A> v() {
      return new AnnotatedEventBus<>(this.n, this.V, this.o, this.e);
   }
}
