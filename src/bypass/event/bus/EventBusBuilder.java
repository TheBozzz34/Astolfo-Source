package bypass.event.bus;

import bypass.event.Event;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBusBuilder<A extends AnnotatedElement> {
   public final Map<Class<? extends Event>, List<Subscription<?, ?>>> subscriptionMap = new HashMap<>();
   private MemberScanner<A> memberScanner;
   private ListenerFilter<A> listenerFilter;
   private InvocationStrategy invocationStrategy;

   public EventBusBuilder<A> withMemberScanner(MemberScanner<A> memberScanner) {
      this.memberScanner = memberScanner;
      return this;
   }

   public EventBusBuilder<A> withListenerFilter(ListenerFilter<A> listenerFilter) {
      this.listenerFilter = listenerFilter;
      return this;
   }

   public EventBusBuilder<A> withInvocationStrategy(InvocationStrategy invocationStrategy) {
      this.invocationStrategy = invocationStrategy;
      return this;
   }

   public AnnotatedEventBus<A> build() {
      return new AnnotatedEventBus<>(this.subscriptionMap, this.memberScanner, this.listenerFilter, this.invocationStrategy);
   }
}
