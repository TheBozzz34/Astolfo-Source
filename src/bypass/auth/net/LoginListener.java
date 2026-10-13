package bypass.auth.net;

import bypass.ClientAccess;
import bypass.auth.packet.LoginResponse;

public interface LoginListener {
   long b = 18306945468379L;

   default void o(Object var1) {
      long var2 = b ^ 67237695386132L;
      long var4 = var2 ^ 55690665576259L;
      long var6 = var2 ^ 74142247015589L;
      if (var1 instanceof LoginResponse) {
         LoginResponse var9 = (LoginResponse)var1;
         if (var9.success) {
            ClientAccess.client.register(var6, ClientAccess.client.getSidebarGui());
         }
      }
   }
}
