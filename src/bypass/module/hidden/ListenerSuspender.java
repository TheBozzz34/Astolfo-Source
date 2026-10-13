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
   private Listener<TickEvent> onTick = var1 -> {
      AnnotatedEventBus var5 = (AnnotatedEventBus)client.getEventBus();
      HashMap var6 = (HashMap)var5.N;
      if (DisconnectHandler.disconnected) {
         ArrayList var7 = new ArrayList();

         label79:
         for (Object var9$raw : var6.entrySet()) {
            Entry var9 = (Entry)var9$raw;
            for (Object var11$raw : (List)var9.getValue()) {
               Subscription var11 = (Subscription)var11$raw;
               if (var11.t().getClass() == this.getClass()
                  || var11.t().getClass() == HUD.class
                  || var11.t().getClass() == KeybindHandler.class
                  || var11.t().getClass() == ChatCommandHandler.class
                  || var11.t().getClass() == BotDetector.class
                  || var11.t().getClass() == HypixelPacketHandler.class
                  || var11.t().getClass() == PlayerListTracker.class
                  || var11.t().getClass() == InventoryPacketHandler.class
                  || var11.t().getClass() == MiddleClickFriends.class
                  || var11.t().getClass() == NotificationManager.class
                  || var11.t().getClass() == GUI.class) {
                  continue label79;
               }
            }

            var7.add(var9.getKey());
            j.put((Class<? extends Event>)var9.getKey(), (List<Subscription<?, ?>>)var9.getValue());
         }

         for (Object var15$raw : var7) {
            Class var15 = (Class)var15$raw;
            if (!var15.equals(RenderAfterScreenEvent.class)) {
               var6.remove(var15);
            }
         }
      } else if (!j.isEmpty()) {
         for (Entry var14 : j.entrySet()) {
            var6.put(var14.getKey(), var14.getValue());
         }

         j.clear();
      }
   };
   private static final long c = 33985041570576L;

   public ListenerSuspender() {
      client.getEventBus().subscribe(this);
   }
}
