package bypass.account;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AuthServiceSwitcher {
   private final FieldSetter userAuthFields = new FieldSetter("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
   private final FieldSetter sessionServiceFields = new FieldSetter("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
   private AccountType accountType;

   public void setAccountType(AccountType accountType) throws NoSuchFieldException, IllegalAccessException {
      if (this.accountType != accountType) {
         this.reflectionFields(accountType.domain);
         this.accountType = accountType;
      }
   }

   private void reflectionFields(String string) throws NoSuchFieldException, IllegalAccessException {
      HashMap hashMap = new HashMap();
      String string2 = string.contains("thealtening") ? "http" : "https";
      hashMap.put("ROUTE_AUTHENTICATE", this.createUrl(string2 + "://authserver." + string + ".com/authenticate"));
      hashMap.put("ROUTE_INVALIDATE", this.createUrl(string2 + "://authserver" + string + "com/invalidate"));
      hashMap.put("ROUTE_REFRESH", this.createUrl(string2 + "://authserver." + string + ".com/refresh"));
      hashMap.put("ROUTE_VALIDATE", this.createUrl(string2 + "://authserver." + string + ".com/validate"));
      hashMap.put("ROUTE_SIGNOUT", this.createUrl(string2 + "://authserver." + string + ".com/signout"));
      hashMap.forEach((object, object2) -> {
         try {
            this.userAuthFields.setStaticFinalField((java.lang.String)(object), object2);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      });
      this.userAuthFields.setStaticFinalField("BASE_URL", string2 + "://authserver." + string + ".com/");
      this.sessionServiceFields.setStaticFinalField("BASE_URL", string2 + "://sessionserver." + string + ".com/session/minecraft/");
      this.sessionServiceFields.setStaticFinalField("JOIN_URL", this.createUrl(string2 + "://sessionserver." + string + ".com/session/minecraft/join"));
      this.sessionServiceFields.setStaticFinalField("CHECK_URL", this.createUrl(string2 + "://sessionserver." + string + ".com/session/minecraft/hasJoined"));
      this.sessionServiceFields.setStaticFinalField("WHITELISTED_DOMAINS", new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"});
   }

   private URL createUrl(String string) {
      try {
         return new URL(string);
      } catch (MalformedURLException malformedURLException) {
         throw new Error("Couldn't create constant for " + string, malformedURLException);
      }
   }

   public AccountType getAccountType() {
      if (this.accountType == null) {
         this.accountType = AccountType.MOJANG;
      }

      return this.accountType;
   }
}
