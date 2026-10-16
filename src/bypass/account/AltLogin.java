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
   private boolean mojangWarningShown;
   private static final AltLogin[] VALUES = new AltLogin[]{AUTH};

   public String generateClientToken() {
      return UUID.randomUUID().toString();
   }

   public Session.Type getSessionType(String string) {
      return string.contains("@") ? Session.Type.MOJANG : Session.Type.LEGACY;
   }

   public void loginAlt(Alt alt) {
      this.login(alt, true);
   }

   public void authenticateMicrosoft(Alt alt) throws Throwable {
      try {
         MicrosoftAuth.I_void(alt.accessToken);
         Pair pair = MicrosoftAuth.getProfile(alt.accessToken);
         alt.username = (String)pair.getRight();
         alt.clientToken = ((UUID)pair.getLeft()).toString();
      } catch (Throwable throwable) {
         try {
            Pair pair2 = MicrosoftAuth.refreshMicrosoftToken(alt.refreshToken);
            String string = (String)pair2.getRight();
            String string2 = MicrosoftAuth.authXboxLive((String)pair2.getLeft());
            Pair pair3 = MicrosoftAuth.authXsts(string2);
            String string3 = MicrosoftAuth.authMinecraft((String)pair3.getRight(), (String)pair3.getLeft());
            MicrosoftAuth.I_void(string3);
            Pair pair4 = MicrosoftAuth.getProfile(string3);
            alt.clientToken = ((UUID)pair4.getLeft()).toString();
            alt.username = (String)pair4.getRight();
            alt.accessToken = string3;
            alt.refreshToken = string;
         } catch (Throwable throwable2) {
            throwable2.addSuppressed(throwable);
            throw throwable2;
         }
      }
   }

   public void login(Alt alt, boolean bl) {
      if (!alt.V_boolean()) {
         if (bl) {
            this.setOfflineSession(alt.displayName);
         }
      } else if (alt.microsoft) {
         FileUtil.executor.execute(() -> {
            Notification notification = client.getNotificationManager().createNotification("Microsoft Authentication", "Logging in...", NotificationType.INFO, 10000);
            notification.show();

            try {
               this.authenticateMicrosoft(alt);
            } catch (Throwable throwable) {
               notification.dismiss();
               toasts.dangerMessage("Microsoft Account", "Login failed");
               throwable.printStackTrace();
               return;
            }

            mc.session = new Session(alt.username, alt.clientToken, alt.accessToken, "mojang");
            Toasts.TOASTS.successMessage("Microsoft Account", "Logged successfully to " + alt.username);
            notification.dismiss();
            this.lastLoggedAlt = alt;
         });
      } else {
         Notification notification2 = client.getNotificationManager().createNotification("Authentication", "Logging in...", NotificationType.INFO, 10000);
         if (bl) {
            notification2.show();
         }

         FileUtil.executor
            .execute(
               () -> {
                  if (alt.isAlteningAlt()) {
                     if (AlteningScreen.getAuthServiceSwitcher().getAccountType() == AccountType.MOJANG) {
                        try {
                           AlteningScreen.getAuthServiceSwitcher().setAccountType(AccountType.THEALTENING);
                        } catch (NoSuchFieldException | IllegalAccessException value) {
                           value.printStackTrace();
                        }
                     }
                  } else if (AlteningScreen.getAuthServiceSwitcher().getAccountType() == AccountType.THEALTENING) {
                     try {
                        AlteningScreen.getAuthServiceSwitcher().setAccountType(AccountType.MOJANG);
                     } catch (NoSuchFieldException | IllegalAccessException value2) {
                        value2.printStackTrace();
                     }
                  }

                  UserAuthentication userAuthentication = new YggdrasilAuthenticationService(Proxy.NO_PROXY, alt.clientToken).createUserAuthentication(Agent.MINECRAFT);
                  boolean bl2 = false;
                  if (!StringUtils.isNullOrEmpty(alt.accessToken) && alt.sessionType == Session.Type.MOJANG) {
                     ImmutableMap immutableMap = new Builder<String, String>()
                        .put("username", alt.username)
                        .put("displayName", alt.displayName)
                        .put("uuid", alt.clientToken)
                        .put("accessToken", alt.accessToken)
                        .build();
                     userAuthentication.loadFromStorage(immutableMap);
                     bl2 = this.tryAuthenticate(userAuthentication, "Access Token Login", alt);
                  }

                  if (!bl2) {
                     userAuthentication.logOut();
                     userAuthentication.setUsername(alt.username);
                     userAuthentication.setPassword(alt.isAlteningAlt() ? "cool" : alt.password);
                     bl2 = this.tryAuthenticate(
                        userAuthentication,
                        AlteningScreen.getAuthServiceSwitcher().getAccountType() == AccountType.THEALTENING ? "The Altening Login" : "Password Login",
                        alt
                     );
                  }

                  if (bl2) {
                     if (bl) {
                        mc.session = new Session(
                           userAuthentication.getSelectedProfile().getName(),
                           userAuthentication.getSelectedProfile().getId().toString(),
                           userAuthentication.getAuthenticatedToken(),
                           alt.sessionType.toString()
                        );
                     }

                     alt.accessToken = userAuthentication.getAuthenticatedToken();
                     alt.clientToken = userAuthentication.getSelectedProfile().getId().toString();
                     alt.displayName = userAuthentication.getSelectedProfile().getName();
                     alt.failedLogin = false;
                  }

                  notification2.dismiss();
               }
            );
      }
   }

   private void setOfflineSession(String string) {
      mc.session = new Session(string, mc.session.getPlayerID(), mc.session.getToken(), Session.Type.LEGACY.toString());
      client.getNotificationManager().createNotification("Offline login", "Username set to: " + string, NotificationType.INFO, 5000).show();
   }

   private boolean tryAuthenticate(UserAuthentication userAuthentication, String string, Alt alt) {
      try {
         userAuthentication.logIn();
         client.getNotificationManager()
            .createNotification(string, "Logged in to " + userAuthentication.getSelectedProfile().getName(), NotificationType.SUCCESS, 5000)
            .show();
         if (!this.mojangWarningShown) {
            toasts.infoTimed("Mojang Account", "Mojang has closed support for this type of account, they might not work", 15000);
            this.mojangWarningShown = true;
         }

         this.lastLoggedAlt = alt;
         return true;
      } catch (AuthenticationUnavailableException authenticationUnavailableException) {
         client.getNotificationManager()
            .createNotification(string, "Failed to connect to Mojang's authentication servers", NotificationType.DANGER, 5000)
            .show();
      } catch (UserMigratedException userMigratedException) {
         client.getNotificationManager().createNotification(string, "User account is migrated", NotificationType.DANGER, 5000).show();
      } catch (AuthenticationException authenticationException) {
         if (!string.equals("Access Token Login")) {
            alt.failedLogin = true;
         }

         client.getNotificationManager().createNotification(string, "Bad login", NotificationType.DANGER, 5000).show();
      }

      return false;
   }
}
