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
   private GuiScreen a;
   private GuiTextField n;
   private PasswordField K;
   private Checkbox d;
   private Checkbox V;
   private final Alt P;
   private boolean l;
   private GuiButton f;
   private HttpServer y;
   private static final long b = 78770520807625L;

   public AddAltScreen(GuiScreen guiScreen) {
      this(guiScreen, null);
   }

   public AddAltScreen(GuiScreen guiScreen, Alt alt) {
      this.a = guiScreen;
      this.P = alt;
      if (alt != null && alt.F) {
         this.l = true;
      }
   }

   @Override
   public void updateScreen() {
      this.n.updateCursorCounter();
      this.K.updateCursorCounter();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      if (this.y != null) {
         this.y.stop(0);
      }
   }

   @Override
   public void initGui() {
      long m = b ^ 118591459494541L;
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(this.f = new GuiButton(0, this.width / 2 - 125, this.height / 6 + 150, 120, 20, "Save Alt"));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height / 6 + 150, 120, 20, "Cancel"));
      this.n = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 6 + 35, 200, 20);
      this.n.setFocused(true);
      if (!this.q()) {
         this.n.setText(this.P.j);
      }

      this.n.setMaxStringLength(128);
      this.K = new PasswordField(4, this.width / 2 - 100, this.height / 6 + 80, 200, 20);
      if (!this.q() && this.P.z != null) {
         this.K.setText(this.P.z);
      }

      this.K.setMaxStringLength(128);
      String string = "Hide password";
      this.d = new Checkbox(string, this.width / 2 - (125 + this.fontRendererObj.getStringWidth(string)) / 2, this.height / 6 + 115, true);
      long l2 = 3834574151802937387L ^ m;
      String string2 = "Microsoft";
      this.V = new Checkbox(string2, this.width / 2 - (-120 + this.fontRendererObj.getStringWidth(string2)) / 2, this.height / 6 + 115, this.l);
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         if (this.V.a()) {
            this.startMSThread();
         } else {
            this.H();
            this.mc.displayGuiScreen(this.a);
         }
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.a);
      }
   }

   public void startMSThread() {
      new Thread(
            () -> {
               try {
                  this.y = HttpServer.create(new InetSocketAddress(59125), 0);
                  this.y.createContext("/", httpExchange -> {
                     try {
                        httpExchange.getResponseHeaders().add("Location", "http://localhost:59125/end");
                        httpExchange.sendResponseHeaders(302, -1L);
                        new Thread(() -> this.q(httpExchange.getRequestURI().getQuery()), "IAS MS Auth Thread").start();
                     } catch (Throwable throwable) {
                        System.out.println("Unable to process request 'auth' on MS auth server");

                        try {
                           if (this.y != null) {
                              this.y.stop(0);
                           }
                        } catch (Throwable throwable2) {
                           System.out.println("Unable to stop fail-requested MS auth server");
                        }
                     }
                  });
                  this.y
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
                                 if (this.y != null) {
                                    this.y.stop(0);
                                 }
                              } catch (Throwable throwable3) {
                                 System.out.println("Unable to stop MS auth server");
                              }
                           } catch (Throwable throwable4) {
                              System.out.println("Unable to process request 'end' on MS auth server");

                              try {
                                 if (this.y != null) {
                                    this.y.stop(0);
                                 }
                              } catch (Throwable throwable5) {
                                 System.out.println("Unable to stop fail-requested MS auth server");
                              }
                           }
                        }
                     );
                  this.y.start();
                  e(
                     new URI(
                        "https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=consent"
                     )
                  );
               } catch (Throwable throwable6) {
                  throwable6.printStackTrace();
                  System.out.println("Unable to start MS auth server");

                  try {
                     if (this.y != null) {
                        this.y.stop(0);
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

   private void q(String string) {
      long l = b ^ 57450911605727L;
      long n = l ^ 81090810350741L;

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

         Pair pair = MicrosoftAuth.X(string.replace("code=", ""));
         String string2 = (String)pair.getRight();
         String string3 = MicrosoftAuth.x((String)pair.getLeft());
         Pair pair2 = MicrosoftAuth.Q(string3);
         String string4 = MicrosoftAuth.P((String)pair2.getRight(), (String)pair2.getLeft());
         MicrosoftAuth.I_void(string4);
         Pair pair3 = MicrosoftAuth.m(string4);
         this.mc.session = new Session((String)pair3.getRight(), ((UUID)pair3.getLeft()).toString(), string4, "mojang");
         Alt alt = new Alt((String)pair3.getRight(), "");
         alt.U = ((UUID)pair3.getLeft()).toString();
         alt.o = (String)pair3.getRight();
         alt.y = string4;
         alt.Z = string2;
         alt.F = true;
         List list = Astolfo.INSTANCE.getAltManager().getList();
         int i = list.size();
         if (!this.q()) {
            i = list.indexOf(this.P);
            list.remove(i);
         }

         list.add(i, alt);
         Toasts.TOASTS.g("Microsoft Account", "Logged successfully to " + (String)pair3.getRight());
         this.mc.displayGuiScreen(this.a);
      } catch (Throwable throwable) {
         if (throwable instanceof AuthException) {
            AuthException authException = (AuthException)throwable;
            if (authException.U().equals("ias.msauth.error.gamenotowned")) {
               Toasts.TOASTS.k("Microsoft Account", "This account does not own Minecraft");
            }

            if (authException.U().equals("ias.msauth.error.noxbox")) {
               Toasts.TOASTS.k("Microsoft Account", "This account does not have a Xbox account setup");
            }
         }

         if (throwable instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException)throwable;
            if (illegalArgumentException.getMessage().contains("getProfile response:")) {
               Toasts.TOASTS.k("Microsoft Account", "This account does not have a Minecraft name set");
            }
         }

         this.y.stop(0);
         this.y = null;
         throwable.printStackTrace();
         System.out.println("Unable");
      }
   }

   public static void e(URI uri) throws IOException {
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

   private void H() {
      long l = b ^ 109395514741362L;
      long m = l ^ 34078838783288L;
      List list = Astolfo.INSTANCE.getAltManager().getList();
      int i = list.size();
      if (!this.q()) {
         i = list.indexOf(this.P);
         list.remove(i);
      }

      if (this.n.getText().contains(":")) {
         String[] strings = this.n.getText().split(":");
         if (strings.length == 2) {
            Alt alt = new Alt(strings[0], strings[1]);
            AltLogin.AUTH.login(alt, false);
            list.add(i, alt);
         }
      } else {
         Alt alt2 = new Alt(this.n.getText(), this.K.getText());
         AltLogin.AUTH.login(alt2, false);
         list.add(i, alt2);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (typedChar == '\t') {
         this.n.setFocused(this.K.isFocused());
         this.K.setFocused(!this.n.isFocused());
      }

      if (this.n.isFocused()) {
         this.n.textboxKeyTyped(typedChar, keyCode);
         if (typedChar == '\r') {
            this.H();
            this.mc.displayGuiScreen(this.a);
         }
      } else if (this.K.isFocused()) {
         this.K.textboxKeyTyped(typedChar, keyCode);
         if (typedChar == '\r') {
            this.H();
            this.mc.displayGuiScreen(this.a);
         }
      }

      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.n.mouseClicked(mouseX, mouseY, mouseButton);
      this.K.mouseClicked(mouseX, mouseY, mouseButton);
      this.d.b(mouseX, mouseY, mouseButton);
      this.V.b(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.d(
         this.fontRendererObj,
         this.q() ? (this.y != null ? "Waiting for Microsoft login (browser)" : "Add an account") : "Edit an account",
         this.width / 2,
         30,
         -1
      );
      String string = "Username or email:";
      this.K.setVisible(true);
      if (this.n.getText().contains("@")) {
         string = "Email:";
         if (this.n.getText().contains(":")) {
            string = "Email and password:";
            this.K.setVisible(false);
         }
      } else if (this.n.getText().contains(":")) {
         string = "Username and password:";
         this.K.setVisible(false);
      }

      this.f.displayString = this.V.a() ? "Login" : "Save Alt";
      this.f.enabled = this.y == null;
      if (!this.V.a()) {
         if (this.K.getVisible()) {
            this.I(this.fontRendererObj, "*Password:", this.width / 2 - 100, this.height / 6 + 68, -1);
         }

         this.I(this.fontRendererObj, string, this.width / 2 - 100, this.height / 6 + 23, -1);
      }

      this.d(this.fontRendererObj, "*Leave blank for offline account", this.width / 2, this.height / 6 + 180, -1);
      this.K.Z(this.d.a());
      this.n.setVisible(!this.V.a());
      this.K.setVisible(!this.V.a());
      this.n.drawTextBox();
      this.K.drawTextBox();
      this.d.E(mouseX, mouseY);
      this.V.E(mouseX, mouseY);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private boolean q() {
      return this.P == null;
   }
}
