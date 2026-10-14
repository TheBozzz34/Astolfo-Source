package bypass.module.hidden;

import bypass.auth.DisconnectHandler;
import bypass.event.Event;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.bus.AnnotatedEventBus;
import bypass.event.bus.Subscription;
import bypass.event.game.TickEvent;
import bypass.event.render.RenderAfterScreenEvent;
import bypass.module.HiddenModule;
import bypass.module.visuals.HUD;
import bypass.notification.NotificationManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public final class ListenerSuspender extends HiddenModule {
   public static HashMap<Class<? extends Event>, List<Subscription<?, ?>>> j = new HashMap<>();
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = tickEvent -> {
      AnnotatedEventBus annotatedEventBus = (AnnotatedEventBus)client.getEventBus();
      HashMap hashMap = (HashMap)annotatedEventBus.N;
      if (DisconnectHandler.disconnected) {
         ArrayList arrayList = new ArrayList();

         label79:
         for (Object object : hashMap.entrySet()) {
            Entry entry = (Entry)object;
            for (Object object2 : (List)entry.getValue()) {
               Subscription subscription = (Subscription)object2;
               if (subscription.t().getClass() == this.getClass()
                  || subscription.t().getClass() == HUD.class
                  || subscription.t().getClass() == KeybindHandler.class
                  || subscription.t().getClass() == ChatCommandHandler.class
                  || subscription.t().getClass() == BotDetector.class
                  || subscription.t().getClass() == HypixelPacketHandler.class
                  || subscription.t().getClass() == PlayerListTracker.class
                  || subscription.t().getClass() == InventoryPacketHandler.class
                  || subscription.t().getClass() == MiddleClickFriends.class
                  || subscription.t().getClass() == NotificationManager.class
                  || subscription.t().getClass() == GUI.class) {
                  continue label79;
               }
            }

            arrayList.add(entry.getKey());
            j.put((Class<? extends Event>)entry.getKey(), (List<Subscription<?, ?>>)entry.getValue());
         }

         for (Object object3 : arrayList) {
            Class class2 = (Class)object3;
            if (!class2.equals(RenderAfterScreenEvent.class)) {
               hashMap.remove(class2);
            }
         }
      } else if (!j.isEmpty()) {
         for (Entry entry2 : j.entrySet()) {
            hashMap.put(entry2.getKey(), entry2.getValue());
         }

         j.clear();
      }
   };
   private static final long c = 33985041570576L;

   public ListenerSuspender() {
      client.getEventBus().subscribe(this);
   }
}
