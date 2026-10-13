package bypass.auth.handler;

import bypass.auth.AuthAccount;
import bypass.auth.net.PacketOutput;
import bypass.auth.packet.LoginResponse;
import bypass.auth.packet.PacketId;
import bypass.event.bus.EventBus;
import bypass.event.bus.InvocationStrategy;
import bypass.event.bus.ListenerFilter;
import bypass.event.bus.MemberScanner;
import bypass.gui.screen.LoginScreen;
import bypass.module.ModuleManager;
import java.lang.reflect.Field;
import net.minecraft.client.renderer.EntityRenderer;

public class LoginResponseHandler extends PacketHandler<LoginResponse> {
   private static final long b = 64020631058990L;

   public LoginResponseHandler() {
      super(PacketId.LOGIN_RESPONSE);
   }

   public void handle(LoginResponse var1, PacketOutput var2) {
      if (var1.success) {
         String[] var6 = var1.message.split(";");
         if (var6.length == 7) {
            if (client.getEventBus() == null) {
               client.setEventBus(EventBus.<Field>p().e(MemberScanner.z()).j(ListenerFilter.W()).v(InvocationStrategy.FIELD_REFLECTIVE_INVOCATION).v());
            }

            ModuleManager.C(Double.parseDouble(var6[1]));
            ModuleManager.k(Integer.parseInt(var6[2]));
            mc.mouseHelper.Y().setAccount(new AuthAccount(var6[3]));
            EntityRenderer.Z0 = Integer.parseInt(var6[4]);
            EntityRenderer.C = Integer.parseInt(var6[5]);
            EntityRenderer.ZE = Integer.parseInt(var6[6]);
         }

         LoginScreen.p = false;
         LoginScreen.V = true;
      } else {
         LoginScreen.x = var1.message;
         if (var1.message.equalsIgnoreCase("tokenfail")) {
            mc.autoLoginToken = "";
         }

         LoginScreen.d = true;
         LoginScreen.p = false;
      }
   }
}
