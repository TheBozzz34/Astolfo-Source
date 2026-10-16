package bypass.gui.screen;

import bypass.Astolfo;
import bypass.Toasts;
import bypass.account.Alt;
import bypass.account.AltLogin;
import bypass.account.AuthException;
import bypass.account.MicrosoftAuth;
import bypass.gui.Checkbox;
import bypass.gui.PasswordField;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import oshi.SystemInfo;

public class AddAltScreen extends GuiScreen {
   private GuiScreen parentScreen;
   private GuiTextField usernameField;
   private PasswordField passwordField;
   private Checkbox hidePasswordCheckbox;
   private Checkbox microsoftCheckbox;
   private final Alt editedAlt;
   private boolean microsoftDefault;
   private GuiButton saveButton;
   private HttpServer callbackServer;

   public AddAltScreen(GuiScreen guiScreen) {
      this(guiScreen, null);
   }

   public AddAltScreen(GuiScreen guiScreen, Alt alt) {
      this.parentScreen = guiScreen;
      this.editedAlt = alt;
      if (alt != null && alt.microsoft) {
         this.microsoftDefault = true;
      }
   }

   @Override
   public void updateScreen() {
      this.usernameField.updateCursorCounter();
      this.passwordField.updateCursorCounter();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      if (this.callbackServer != null) {
         this.callbackServer.stop(0);
      }
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(this.saveButton = new GuiButton(0, this.width / 2 - 125, this.height / 6 + 150, 120, 20, "Save Alt"));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height / 6 + 150, 120, 20, "Cancel"));
      this.usernameField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 6 + 35, 200, 20);
      this.usernameField.setFocused(true);
      if (!this.isNewAlt()) {
         this.usernameField.setText(this.editedAlt.username);
      }

      this.usernameField.setMaxStringLength(128);
      this.passwordField = new PasswordField(4, this.width / 2 - 100, this.height / 6 + 80, 200, 20);
      if (!this.isNewAlt() && this.editedAlt.password != null) {
         this.passwordField.setText(this.editedAlt.password);
      }

      this.passwordField.setMaxStringLength(128);
      String string = "Hide password";
      this.hidePasswordCheckbox = new Checkbox(string, this.width / 2 - (125 + this.fontRendererObj.getStringWidth(string)) / 2, this.height / 6 + 115, true);
      String string2 = "Microsoft";
      this.microsoftCheckbox = new Checkbox(
         string2, this.width / 2 - (-120 + this.fontRendererObj.getStringWidth(string2)) / 2, this.height / 6 + 115, this.microsoftDefault
      );
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         if (this.microsoftCheckbox.isChecked()) {
            this.startMSThread();
         } else {
            this.saveAlt();
            this.mc.displayGuiScreen(this.parentScreen);
         }
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   public void startMSThread() {
      new Thread(
            () -> {
               try {
                  this.callbackServer = HttpServer.create(new InetSocketAddress(59125), 0);
                  this.callbackServer.createContext("/", httpExchange -> {
                     try {
                        httpExchange.getResponseHeaders().add("Location", "http://localhost:59125/end");
                        httpExchange.sendResponseHeaders(302, -1L);
                        new Thread(() -> this.handleMicrosoftCallback(httpExchange.getRequestURI().getQuery()), "IAS MS Auth Thread").start();
                     } catch (Throwable throwable) {
                        System.out.println("Unable to process request 'auth' on MS auth server");

                        try {
                           if (this.callbackServer != null) {
                              this.callbackServer.stop(0);
                           }
                        } catch (Throwable throwable2) {
                           System.out.println("Unable to stop fail-requested MS auth server");
                        }
                     }
                  });
                  this.callbackServer
                     .createContext(
                        "/end",
                        httpExchange2 -> {
                           try {
                              String string = "<html><body><audio autoplay id=\"tuturu\"><source src=\"https://www.myinstants.com/media/sounds/tuturu_1.mp3\" type=\"audio/mpeg\">Your browser does not support the audio element.</audio><script>document.addEventListener('click', musicPlay);function musicPlay(){document.getElementById('tuturu').play(); document.removeEventListener('click', musicPlay);}</script></script></body></html>";
                              byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
                              httpExchange2.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=UTF-8"));
                              httpExchange2.sendResponseHeaders(200, bytes.length);
                              OutputStream outputStream = httpExchange2.getResponseBody();
                              outputStream.write(bytes);
                              outputStream.flush();
                              outputStream.close();

                              try {
                                 if (this.callbackServer != null) {
                                    this.callbackServer.stop(0);
                                 }
                              } catch (Throwable throwable3) {
                                 System.out.println("Unable to stop MS auth server");
                              }
                           } catch (Throwable throwable4) {
                              System.out.println("Unable to process request 'end' on MS auth server");

                              try {
                                 if (this.callbackServer != null) {
                                    this.callbackServer.stop(0);
                                 }
                              } catch (Throwable throwable5) {
                                 System.out.println("Unable to stop fail-requested MS auth server");
                              }
                           }
                        }
                     );
                  this.callbackServer.start();
                  openInBrowser(
                     new URI(
                        "https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=consent"
                     )
                  );
               } catch (Throwable throwable6) {
                  throwable6.printStackTrace();
                  System.out.println("Unable to start MS auth server");

                  try {
                     if (this.callbackServer != null) {
                        this.callbackServer.stop(0);
                     }
                  } catch (Throwable throwable7) {
                     System.out.println("Unable to stop fail-started MS auth server");
                  }
               }
            },
            "IAS MS Auth Server Thread"
         )
         .start();
   }

   private void handleMicrosoftCallback(String string) {
      try {
         if (string == null) {
            throw new NullPointerException("query=null");
         }

         if (string.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) {
            throw new AuthException("ias.msauth.error.revoked");
         }

         if (!string.startsWith("code=")) {
            throw new IllegalStateException("query=" + string);
         }

         Pair pair = MicrosoftAuth.exchangeAuthCode(string.replace("code=", ""));
         String string2 = (String)pair.getRight();
         String string3 = MicrosoftAuth.authXboxLive((String)pair.getLeft());
         Pair pair2 = MicrosoftAuth.authXsts(string3);
         String string4 = MicrosoftAuth.authMinecraft((String)pair2.getRight(), (String)pair2.getLeft());
         MicrosoftAuth.I_void(string4);
         Pair pair3 = MicrosoftAuth.getProfile(string4);
         this.mc.session = new Session((String)pair3.getRight(), ((UUID)pair3.getLeft()).toString(), string4, "mojang");
         Alt alt = new Alt((String)pair3.getRight(), "");
         alt.clientToken = ((UUID)pair3.getLeft()).toString();
         alt.displayName = (String)pair3.getRight();
         alt.accessToken = string4;
         alt.refreshToken = string2;
         alt.microsoft = true;
         List list = Astolfo.INSTANCE.getAltManager().getList();
         int i = list.size();
         if (!this.isNewAlt()) {
            i = list.indexOf(this.editedAlt);
            list.remove(i);
         }

         list.add(i, alt);
         Toasts.TOASTS.successMessage("Microsoft Account", "Logged successfully to " + (String)pair3.getRight());
         this.mc.displayGuiScreen(this.parentScreen);
      } catch (Throwable throwable) {
         if (throwable instanceof AuthException) {
            AuthException authException = (AuthException)throwable;
            if (authException.getErrorKey().equals("ias.msauth.error.gamenotowned")) {
               Toasts.TOASTS.dangerMessage("Microsoft Account", "This account does not own Minecraft");
            }

            if (authException.getErrorKey().equals("ias.msauth.error.noxbox")) {
               Toasts.TOASTS.dangerMessage("Microsoft Account", "This account does not have a Xbox account setup");
            }
         }

         if (throwable instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException)throwable;
            if (illegalArgumentException.getMessage().contains("getProfile response:")) {
               Toasts.TOASTS.dangerMessage("Microsoft Account", "This account does not have a Minecraft name set");
            }
         }

         this.callbackServer.stop(0);
         this.callbackServer = null;
         throwable.printStackTrace();
         System.out.println("Unable");
      }
   }

   public static void openInBrowser(URI uri) throws IOException {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case MACOSX:
            Desktop.getDesktop().browse(uri);
            break;
         case WINDOWS:
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + uri);
            break;
         case LINUX:
            Runtime runtime = Runtime.getRuntime();
            String[] strings = new String[]{"xdg-open", "google-chrome", "firefox", "opera", "konqueror", "mozilla"};

            for (String string : strings) {
               try (InputStream inputStream = runtime.exec(new String[]{"which", string}).getInputStream()) {
                  if (inputStream.read() != -1) {
                     runtime.exec(new String[]{string, uri.toString()});
                     return;
                  }
               }
            }

            throw new IOException("No browser found");
         default:
            throw new IllegalStateException("Unsupported OS");
      }
   }

   private void saveAlt() {
      List list = Astolfo.INSTANCE.getAltManager().getList();
      int i = list.size();
      if (!this.isNewAlt()) {
         i = list.indexOf(this.editedAlt);
         list.remove(i);
      }

      if (this.usernameField.getText().contains(":")) {
         String[] strings = this.usernameField.getText().split(":");
         if (strings.length == 2) {
            Alt alt = new Alt(strings[0], strings[1]);
            AltLogin.AUTH.login(alt, false);
            list.add(i, alt);
         }
      } else {
         Alt alt2 = new Alt(this.usernameField.getText(), this.passwordField.getText());
         AltLogin.AUTH.login(alt2, false);
         list.add(i, alt2);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (typedChar == '\t') {
         this.usernameField.setFocused(this.passwordField.isFocused());
         this.passwordField.setFocused(!this.usernameField.isFocused());
      }

      if (this.usernameField.isFocused()) {
         this.usernameField.textboxKeyTyped(typedChar, keyCode);
         if (typedChar == '\r') {
            this.saveAlt();
            this.mc.displayGuiScreen(this.parentScreen);
         }
      } else if (this.passwordField.isFocused()) {
         this.passwordField.textboxKeyTyped(typedChar, keyCode);
         if (typedChar == '\r') {
            this.saveAlt();
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }

      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
      this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
      this.hidePasswordCheckbox.handleMouseClick(mouseX, mouseY, mouseButton);
      this.microsoftCheckbox.handleMouseClick(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.d(
         this.fontRendererObj,
         this.isNewAlt() ? (this.callbackServer != null ? "Waiting for Microsoft login (browser)" : "Add an account") : "Edit an account",
         this.width / 2,
         30,
         -1
      );
      String string = "Username or email:";
      this.passwordField.setVisible(true);
      if (this.usernameField.getText().contains("@")) {
         string = "Email:";
         if (this.usernameField.getText().contains(":")) {
            string = "Email and password:";
            this.passwordField.setVisible(false);
         }
      } else if (this.usernameField.getText().contains(":")) {
         string = "Username and password:";
         this.passwordField.setVisible(false);
      }

      this.saveButton.displayString = this.microsoftCheckbox.isChecked() ? "Login" : "Save Alt";
      this.saveButton.enabled = this.callbackServer == null;
      if (!this.microsoftCheckbox.isChecked()) {
         if (this.passwordField.getVisible()) {
            this.I(this.fontRendererObj, "*Password:", this.width / 2 - 100, this.height / 6 + 68, -1);
         }

         this.I(this.fontRendererObj, string, this.width / 2 - 100, this.height / 6 + 23, -1);
      }

      this.d(this.fontRendererObj, "*Leave blank for offline account", this.width / 2, this.height / 6 + 180, -1);
      this.passwordField.setPasswordMode(this.hidePasswordCheckbox.isChecked());
      this.usernameField.setVisible(!this.microsoftCheckbox.isChecked());
      this.passwordField.setVisible(!this.microsoftCheckbox.isChecked());
      this.usernameField.drawTextBox();
      this.passwordField.drawTextBox();
      this.hidePasswordCheckbox.drawCheckbox(mouseX, mouseY);
      this.microsoftCheckbox.drawCheckbox(mouseX, mouseY);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private boolean isNewAlt() {
      return this.editedAlt == null;
   }
}
