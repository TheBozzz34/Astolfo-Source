package bypass.account;

import bypass.ClientAccess;
import bypass.Toasts;
import bypass.gui.screen.AlteningScreen;
import bypass.notification.Notification;
import bypass.notification.NotificationType;
import bypass.util.FileUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public enum AltLogin implements ClientAccess {
   AUTH;

   public Alt lastLoggedAlt;
   private boolean t;
   private static final AltLogin[] C = new AltLogin[]{AUTH};
   private static final long b = 35082389018510L;

   public String x() {
      return UUID.randomUUID().toString();
   }

   public Session.Type t(String var1) {
      return var1.contains("@") ? Session.Type.MOJANG : Session.Type.LEGACY;
   }

   public void A(Alt var1) {
      this.login(var1, true);
   }

   public void h(Alt var1) throws Throwable {
      try {
         MicrosoftAuth.I_void(var1.y);
         Pair var2 = MicrosoftAuth.m(var1.y);
         var1.j = (String)var2.getRight();
         var1.U = ((UUID)var2.getLeft()).toString();
      } catch (Throwable var10) {
         try {
            Pair var3 = MicrosoftAuth.I(var1.Z);
            String var4 = (String)var3.getRight();
            String var5 = MicrosoftAuth.x((String)var3.getLeft());
            Pair var6 = MicrosoftAuth.Q(var5);
            String var7 = MicrosoftAuth.P((String)var6.getRight(), (String)var6.getLeft());
            MicrosoftAuth.I_void(var7);
            Pair var8 = MicrosoftAuth.m(var7);
            var1.U = ((UUID)var8.getLeft()).toString();
            var1.j = (String)var8.getRight();
            var1.y = var7;
            var1.Z = var4;
         } catch (Throwable var9) {
            var9.addSuppressed(var10);
            throw var9;
         }
      }
   }

   public void login(Alt var1, boolean var2) {
      long var3 = b ^ 26176853168693L;
      long var5 = var3 ^ 85801790765280L;
      if (!var1.V_boolean()) {
         if (var2) {
            this.i(var1.o);
         }
      } else if (var1.F) {
         FileUtil.w.execute(() -> {
            long var2x = b ^ 138249014319450L;
            long var4 = var2x ^ 40111037821839L;
            Notification var6 = client.getNotificationManager().i("Microsoft Authentication", "Logging in...", NotificationType.INFO, 10000);
            var6.K();

            try {
               this.h(var1);
            } catch (Throwable var8x) {
               var6.E();
               toasts.k("Microsoft Account", "Login failed");
               var8x.printStackTrace();
               return;
            }

            mc.session = new Session(var1.j, var1.U, var1.y, "mojang");
            Toasts.TOASTS.g("Microsoft Account", "Logged successfully to " + var1.j);
            var6.E();
            this.lastLoggedAlt = var1;
         });
      } else {
         Notification var8 = client.getNotificationManager().i("Authentication", "Logging in...", NotificationType.INFO, 10000);
         if (var2) {
            var8.K();
         }

         FileUtil.w
            .execute(
               () -> {
                  if (var1.D()) {
                     if (AlteningScreen.r().V() == AccountType.MOJANG) {
                        try {
                           AlteningScreen.r().g(AccountType.THEALTENING);
                        } catch (NoSuchFieldException | IllegalAccessException var11) {
                           var11.printStackTrace();
                        }
                     }
                  } else if (AlteningScreen.r().V() == AccountType.THEALTENING) {
                     try {
                        AlteningScreen.r().g(AccountType.MOJANG);
                     } catch (NoSuchFieldException | IllegalAccessException var10) {
                        var10.printStackTrace();
                     }
                  }

                  UserAuthentication var7 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, var1.U).createUserAuthentication(Agent.MINECRAFT);
                  boolean var8x = false;
                  if (!StringUtils.isNullOrEmpty(var1.y) && var1.T == Session.Type.MOJANG) {
                     ImmutableMap var9 = new Builder<String, String>()
                        .put("username", var1.j)
                        .put("displayName", var1.o)
                        .put("uuid", var1.U)
                        .put("accessToken", var1.y)
                        .build();
                     var7.loadFromStorage(var9);
                     var8x = this.p(var7, "Access Token Login", var1);
                  }

                  if (!var8x) {
                     var7.logOut();
                     var7.setUsername(var1.j);
                     var7.setPassword(var1.D() ? "cool" : var1.z);
                     var8x = this.p(var7, AlteningScreen.r().V() == AccountType.THEALTENING ? "The Altening Login" : "Password Login", var1);
                  }

                  if (var8x) {
                     if (var2) {
                        mc.session = new Session(
                           var7.getSelectedProfile().getName(), var7.getSelectedProfile().getId().toString(), var7.getAuthenticatedToken(), var1.T.toString()
                        );
                     }

                     var1.y = var7.getAuthenticatedToken();
                     var1.U = var7.getSelectedProfile().getId().toString();
                     var1.o = var7.getSelectedProfile().getName();
                     var1.I = false;
                  }

                  var8.E();
               }
            );
      }
   }

   private void i(String var1) {
      long var2 = b ^ 69518627364719L;
      long var4 = var2 ^ 113239469618618L;
      mc.session = new Session(var1, mc.session.getPlayerID(), mc.session.getToken(), Session.Type.LEGACY.toString());
      client.getNotificationManager().i("Offline login", "Username set to: " + var1, NotificationType.INFO, 5000).K();
   }

   private boolean p(UserAuthentication var1, String var2, Alt var3) {
      long var4 = b ^ 130190865920030L;
      long var6 = var4 ^ 52432475937483L;

      try {
         var1.logIn();
         client.getNotificationManager().i(var2, "Logged in to " + var1.getSelectedProfile().getName(), NotificationType.SUCCESS, 5000).K();
         if (!this.t) {
            toasts.G("Mojang Account", "Mojang has closed support for this type of account, they might not work", 15000);
            this.t = true;
         }

         this.lastLoggedAlt = var3;
         return true;
      } catch (AuthenticationUnavailableException var10) {
         client.getNotificationManager().i(var2, "Failed to connect to Mojang's authentication servers", NotificationType.DANGER, 5000).K();
      } catch (UserMigratedException var11) {
         client.getNotificationManager().i(var2, "User account is migrated", NotificationType.DANGER, 5000).K();
      } catch (AuthenticationException var12) {
         if (!var2.equals("Access Token Login")) {
            var3.I = true;
         }

         client.getNotificationManager().i(var2, "Bad login", NotificationType.DANGER, 5000).K();
      }

      return false;
   }
}
