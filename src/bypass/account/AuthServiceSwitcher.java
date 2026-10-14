package bypass.account;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AuthServiceSwitcher {
   private final FieldSetter K = new FieldSetter("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
   private final FieldSetter A = new FieldSetter("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
   private AccountType s;

   public void g(AccountType accountType) throws NoSuchFieldException, IllegalAccessException {
      if (this.s != accountType) {
         this.reflectionFields(accountType.f);
         this.s = accountType;
      }
   }

   private void reflectionFields(String string) throws NoSuchFieldException, IllegalAccessException {
      HashMap hashMap = new HashMap();
      String string2 = string.contains("thealtening") ? "http" : "https";
      hashMap.put("ROUTE_AUTHENTICATE", this.I(string2 + "://authserver." + string + ".com/authenticate"));
      hashMap.put("ROUTE_INVALIDATE", this.I(string2 + "://authserver" + string + "com/invalidate"));
      hashMap.put("ROUTE_REFRESH", this.I(string2 + "://authserver." + string + ".com/refresh"));
      hashMap.put("ROUTE_VALIDATE", this.I(string2 + "://authserver." + string + ".com/validate"));
      hashMap.put("ROUTE_SIGNOUT", this.I(string2 + "://authserver." + string + ".com/signout"));
      hashMap.forEach((object, object2) -> {
         try {
            this.K.k((java.lang.String)(object), object2);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      });
      this.K.k("BASE_URL", string2 + "://authserver." + string + ".com/");
      this.A.k("BASE_URL", string2 + "://sessionserver." + string + ".com/session/minecraft/");
      this.A.k("JOIN_URL", this.I(string2 + "://sessionserver." + string + ".com/session/minecraft/join"));
      this.A.k("CHECK_URL", this.I(string2 + "://sessionserver." + string + ".com/session/minecraft/hasJoined"));
      this.A.k("WHITELISTED_DOMAINS", new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"});
   }

   private URL I(String string) {
      try {
         return new URL(string);
      } catch (MalformedURLException malformedURLException) {
         throw new Error("Couldn't create constant for " + string, malformedURLException);
      }
   }

   public AccountType V() {
      if (this.s == null) {
         this.s = AccountType.MOJANG;
      }

      return this.s;
   }
}
