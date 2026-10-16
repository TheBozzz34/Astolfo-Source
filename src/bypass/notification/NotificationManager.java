package bypass.notification;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.render.RenderAfterScreenEvent;
import bypass.gui.clickgui.defaultgui.DefaultClickGuiScreen;
import bypass.manager.Manager;
import bypass.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class NotificationManager extends Manager<Notification> implements ClientAccess {
   @EventHandler(RenderAfterScreenEvent.class)
   private Listener<RenderAfterScreenEvent> onRenderAfterScreen = renderAfterScreenEvent -> {
      if (!this.items.isEmpty()) {
         int i = renderAfterScreenEvent.getHeight() - 40;

         for (int j = 0; j < this.items.size(); j++) {
            Notification notification = this.items.get(j);
            if (notification != null && notification.timerAnimation != null) {
               if (notification.timerAnimation.getValue() >= 1.0) {
                  if (!notification.slideAnimation.isRunning()) {
                     notification.slideAnimation.backwards().start();
                  }

                  if (notification.slideAnimation.getValue() <= 0.0) {
                     this.items.remove(notification);
                  }
               } else if (!notification.isTimerRunning()) {
                  notification.start();
               }

               int k = Math.max(
                     client.getCustomFontRenderer().getStringWidth(notification.getTitle()), client.getCustomFontRenderer().getStringWidth(notification.getMessage())
                  )
                  + 30;
               int l = notification.getMessage().isEmpty() ? 10 : 20;
               int m = (int)(renderAfterScreenEvent.getWidth() - notification.slideAnimation.getValue(0.0, k + 2));
               if (mc.currentScreen instanceof DefaultClickGuiScreen) {
                  m = (int)notification.slideAnimation.getValue(0.0, k)
                     - Math.max(
                        client.getCustomFontRenderer().getStringWidth(notification.getTitle()), client.getCustomFontRenderer().getStringWidth(notification.getMessage())
                     )
                     - 30;
               }

               GL11.glEnableClientState(32884);
               GL11.glDisable(3553);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               RenderUtil.drawRect(m - 2, i - 4, m + k + 2, i + l + 4, 1879048192);
               if (notification.timerAnimation.getValue() <= 1.0) {
                  RenderUtil.drawRect(
                     m - 2, i + l + 2, (float)(m + notification.timerAnimation.getValue(k + 2, 0.0)), i + l + 4, notification.getType().color
                  );
               }

               RenderUtil.setColor(notification.getType().color);
               Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(String.format("client/contexts/%s.png", notification.getType().name())));
               int n = notification.getMessage().isEmpty() ? 8 : 16;
               Gui.drawModalRectWithCustomSizedTexture(m + 1, i + 1, 0.0F, 0.0F, n, n, n, n);
               client.getCustomFontRenderer().drawStringWithShadow(notification.getTitle(), m + 20, i - 1.5F, -3355444);
               if (!notification.getMessage().isEmpty()) {
                  client.getCustomFontRenderer().drawStringWithShadow(notification.getMessage(), m + 20, i + 9, -6710887);
               }

               GL11.glDisableClientState(32884);
               GL11.glEnable(3553);
               GL11.glDisable(3042);
               i = (int)(i - (notification.slideAnimation.getValue() * 12.0 + l));
            } else {
               this.items.remove(notification);
            }
         }
      }
   };

   public NotificationManager() {
      Astolfo.INSTANCE.getEventBus().subscribe(this);
   }

   public Notification createNotification(String string, String string2, NotificationType notificationType, int i) {
      return new Notification(string, string2, notificationType, i);
   }
}
