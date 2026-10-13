package bypass.account;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AuthServiceSwitcher {
   private final FieldSetter K = new FieldSetter("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
   private final FieldSetter A = new FieldSetter("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
   private AccountType s;

   public void g(AccountType var1) throws NoSuchFieldException, IllegalAccessException {
      if (this.s != var1) {
         this.reflectionFields(var1.f);
         this.s = var1;
      }
   }

   private void reflectionFields(String var1) throws NoSuchFieldException, IllegalAccessException {
      HashMap var2 = new HashMap();
      String var3 = var1.contains("thealtening") ? "http" : "https";
      var2.put("ROUTE_AUTHENTICATE", this.I(var3 + "://authserver." + var1 + ".com/authenticate"));
      var2.put("ROUTE_INVALIDATE", this.I(var3 + "://authserver" + var1 + "com/invalidate"));
      var2.put("ROUTE_REFRESH", this.I(var3 + "://authserver." + var1 + ".com/refresh"));
      var2.put("ROUTE_VALIDATE", this.I(var3 + "://authserver." + var1 + ".com/validate"));
      var2.put("ROUTE_SIGNOUT", this.I(var3 + "://authserver." + var1 + ".com/signout"));
      var2.forEach((var1x, var2x) -> {
         try {
            this.K.k((java.lang.String)(var1x), var2x);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      });
      this.K.k("BASE_URL", var3 + "://authserver." + var1 + ".com/");
      this.A.k("BASE_URL", var3 + "://sessionserver." + var1 + ".com/session/minecraft/");
      this.A.k("JOIN_URL", this.I(var3 + "://sessionserver." + var1 + ".com/session/minecraft/join"));
      this.A.k("CHECK_URL", this.I(var3 + "://sessionserver." + var1 + ".com/session/minecraft/hasJoined"));
      this.A.k("WHITELISTED_DOMAINS", new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"});
   }

   private URL I(String var1) {
      try {
         return new URL(var1);
      } catch (MalformedURLException var3) {
         throw new Error("Couldn't create constant for " + var1, var3);
      }
   }

   public AccountType V() {
      if (this.s == null) {
         this.s = AccountType.MOJANG;
      }

      return this.s;
   }
}
