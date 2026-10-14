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

   public void handle(LoginResponse loginResponse, PacketOutput packetOutput) {
      if (loginResponse.success) {
         String[] strings = loginResponse.message.split(";");
         if (strings.length == 7) {
            if (client.getEventBus() == null) {
               client.setEventBus(EventBus.<Field>p().e(MemberScanner.z()).j(ListenerFilter.W()).v(InvocationStrategy.FIELD_REFLECTIVE_INVOCATION).v());
            }

            ModuleManager.C(Double.parseDouble(strings[1]));
            ModuleManager.k(Integer.parseInt(strings[2]));
            mc.mouseHelper.Y().setAccount(new AuthAccount(strings[3]));
            EntityRenderer.Z0 = Integer.parseInt(strings[4]);
            EntityRenderer.C = Integer.parseInt(strings[5]);
            EntityRenderer.ZE = Integer.parseInt(strings[6]);
         }

         LoginScreen.p = false;
         LoginScreen.V = true;
      } else {
         LoginScreen.x = loginResponse.message;
         if (loginResponse.message.equalsIgnoreCase("tokenfail")) {
            mc.autoLoginToken = "";
         }

         LoginScreen.d = true;
         LoginScreen.p = false;
      }
   }
}
