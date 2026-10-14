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

   public Session.Type t(String string) {
      return string.contains("@") ? Session.Type.MOJANG : Session.Type.LEGACY;
   }

   public void A(Alt alt) {
      this.login(alt, true);
   }

   public void h(Alt alt) throws Throwable {
      try {
         MicrosoftAuth.I_void(alt.y);
         Pair pair = MicrosoftAuth.m(alt.y);
         alt.j = (String)pair.getRight();
         alt.U = ((UUID)pair.getLeft()).toString();
      } catch (Throwable throwable) {
         try {
            Pair pair2 = MicrosoftAuth.I(alt.Z);
            String string = (String)pair2.getRight();
            String string2 = MicrosoftAuth.x((String)pair2.getLeft());
            Pair pair3 = MicrosoftAuth.Q(string2);
            String string3 = MicrosoftAuth.P((String)pair3.getRight(), (String)pair3.getLeft());
            MicrosoftAuth.I_void(string3);
            Pair pair4 = MicrosoftAuth.m(string3);
            alt.U = ((UUID)pair4.getLeft()).toString();
            alt.j = (String)pair4.getRight();
            alt.y = string3;
            alt.Z = string;
         } catch (Throwable throwable2) {
            throwable2.addSuppressed(throwable);
            throw throwable2;
         }
      }
   }

   public void login(Alt alt, boolean bl) {
      long l = b ^ 26176853168693L;
      long m = l ^ 85801790765280L;
      if (!alt.V_boolean()) {
         if (bl) {
            this.i(alt.o);
         }
      } else if (alt.F) {
         FileUtil.w.execute(() -> {
            long n = b ^ 138249014319450L;
            long l2 = n ^ 40111037821839L;
            Notification notification = client.getNotificationManager().i("Microsoft Authentication", "Logging in...", NotificationType.INFO, 10000);
            notification.K();

            try {
               this.h(alt);
            } catch (Throwable throwable) {
               notification.E();
               toasts.k("Microsoft Account", "Login failed");
               throwable.printStackTrace();
               return;
            }

            mc.session = new Session(alt.j, alt.U, alt.y, "mojang");
            Toasts.TOASTS.g("Microsoft Account", "Logged successfully to " + alt.j);
            notification.E();
            this.lastLoggedAlt = alt;
         });
      } else {
         Notification notification2 = client.getNotificationManager().i("Authentication", "Logging in...", NotificationType.INFO, 10000);
         if (bl) {
            notification2.K();
         }

         FileUtil.w
            .execute(
               () -> {
                  if (alt.D()) {
                     if (AlteningScreen.r().V() == AccountType.MOJANG) {
                        try {
                           AlteningScreen.r().g(AccountType.THEALTENING);
                        } catch (NoSuchFieldException | IllegalAccessException value) {
                           value.printStackTrace();
                        }
                     }
                  } else if (AlteningScreen.r().V() == AccountType.THEALTENING) {
                     try {
                        AlteningScreen.r().g(AccountType.MOJANG);
                     } catch (NoSuchFieldException | IllegalAccessException value2) {
                        value2.printStackTrace();
                     }
                  }

                  UserAuthentication userAuthentication = new YggdrasilAuthenticationService(Proxy.NO_PROXY, alt.U).createUserAuthentication(Agent.MINECRAFT);
                  boolean bl2 = false;
                  if (!StringUtils.isNullOrEmpty(alt.y) && alt.T == Session.Type.MOJANG) {
                     ImmutableMap immutableMap = new Builder<String, String>()
                        .put("username", alt.j)
                        .put("displayName", alt.o)
                        .put("uuid", alt.U)
                        .put("accessToken", alt.y)
                        .build();
                     userAuthentication.loadFromStorage(immutableMap);
                     bl2 = this.p(userAuthentication, "Access Token Login", alt);
                  }

                  if (!bl2) {
                     userAuthentication.logOut();
                     userAuthentication.setUsername(alt.j);
                     userAuthentication.setPassword(alt.D() ? "cool" : alt.z);
                     bl2 = this.p(userAuthentication, AlteningScreen.r().V() == AccountType.THEALTENING ? "The Altening Login" : "Password Login", alt);
                  }

                  if (bl2) {
                     if (bl) {
                        mc.session = new Session(
                           userAuthentication.getSelectedProfile().getName(), userAuthentication.getSelectedProfile().getId().toString(), userAuthentication.getAuthenticatedToken(), alt.T.toString()
                        );
                     }

                     alt.y = userAuthentication.getAuthenticatedToken();
                     alt.U = userAuthentication.getSelectedProfile().getId().toString();
                     alt.o = userAuthentication.getSelectedProfile().getName();
                     alt.I = false;
                  }

                  notification2.E();
               }
            );
      }
   }

   private void i(String string) {
      long l = b ^ 69518627364719L;
      long m = l ^ 113239469618618L;
      mc.session = new Session(string, mc.session.getPlayerID(), mc.session.getToken(), Session.Type.LEGACY.toString());
      client.getNotificationManager().i("Offline login", "Username set to: " + string, NotificationType.INFO, 5000).K();
   }

   private boolean p(UserAuthentication userAuthentication, String string, Alt alt) {
      long l = b ^ 130190865920030L;
      long m = l ^ 52432475937483L;

      try {
         userAuthentication.logIn();
         client.getNotificationManager().i(string, "Logged in to " + userAuthentication.getSelectedProfile().getName(), NotificationType.SUCCESS, 5000).K();
         if (!this.t) {
            toasts.G("Mojang Account", "Mojang has closed support for this type of account, they might not work", 15000);
            this.t = true;
         }

         this.lastLoggedAlt = alt;
         return true;
      } catch (AuthenticationUnavailableException authenticationUnavailableException) {
         client.getNotificationManager().i(string, "Failed to connect to Mojang's authentication servers", NotificationType.DANGER, 5000).K();
      } catch (UserMigratedException userMigratedException) {
         client.getNotificationManager().i(string, "User account is migrated", NotificationType.DANGER, 5000).K();
      } catch (AuthenticationException authenticationException) {
         if (!string.equals("Access Token Login")) {
            alt.I = true;
         }

         client.getNotificationManager().i(string, "Bad login", NotificationType.DANGER, 5000).K();
      }

      return false;
   }
}
