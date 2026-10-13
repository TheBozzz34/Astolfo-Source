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
   private Listener<RenderAfterScreenEvent> onRenderAfterScreen = var1 -> {
      if (!this.C.isEmpty()) {
         int var5 = var1.getHeight() - 40;

         for (int var6 = 0; var6 < this.C.size(); var6++) {
            Notification var7 = this.C.get(var6);
            if (var7 != null && var7.h != null) {
               if (var7.h.getValue() >= 1.0) {
                  if (!var7.v.isRunning()) {
                     var7.v.backwards().start();
                  }

                  if (var7.v.getValue() <= 0.0) {
                     this.C.remove(var7);
                  }
               } else if (!var7.I()) {
                  var7.N();
               }

               int var8 = Math.max(client.getCustomFontRenderer().getStringWidth(var7.p()), client.getCustomFontRenderer().getStringWidth(var7.O())) + 30;
               int var9 = var7.O().isEmpty() ? 10 : 20;
               int var10 = (int)(var1.getWidth() - var7.v.getValue(0.0, var8 + 2));
               if (mc.currentScreen instanceof DefaultClickGuiScreen) {
                  var10 = (int)var7.v.getValue(0.0, var8)
                     - Math.max(client.getCustomFontRenderer().getStringWidth(var7.p()), client.getCustomFontRenderer().getStringWidth(var7.O()))
                     - 30;
               }

               GL11.glEnableClientState(32884);
               GL11.glDisable(3553);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               RenderUtil.drawRect(var10 - 2, var5 - 4, var10 + var8 + 2, var5 + var9 + 4, 1879048192);
               if (var7.h.getValue() <= 1.0) {
                  RenderUtil.drawRect(var10 - 2, var5 + var9 + 2, (float)(var10 + var7.h.getValue(var8 + 2, 0.0)), var5 + var9 + 4, var7.G().color);
               }

               RenderUtil.setColor(var7.G().color);
               Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(String.format("client/contexts/%s.png", var7.G().name())));
               int var11 = var7.O().isEmpty() ? 8 : 16;
               Gui.drawModalRectWithCustomSizedTexture(var10 + 1, var5 + 1, 0.0F, 0.0F, var11, var11, var11, var11);
               client.getCustomFontRenderer().drawStringWithShadow(var7.p(), var10 + 20, var5 - 1.5F, -3355444);
               if (!var7.O().isEmpty()) {
                  client.getCustomFontRenderer().drawStringWithShadow(var7.O(), var10 + 20, var5 + 9, -6710887);
               }

               GL11.glDisableClientState(32884);
               GL11.glEnable(3553);
               GL11.glDisable(3042);
               var5 = (int)(var5 - (var7.v.getValue() * 12.0 + var9));
            } else {
               this.C.remove(var7);
            }
         }
      }
   };
   private static final long b = 137825866971641L;

   public NotificationManager() {
      Astolfo.INSTANCE.getEventBus().subscribe(this);
   }

   public Notification i(String var1, String var2, NotificationType var3, int var4) {
      return new Notification(var1, var2, var3, var4);
   }
}
