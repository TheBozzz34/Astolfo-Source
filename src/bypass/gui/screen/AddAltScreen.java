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

   public AddAltScreen(GuiScreen var1) {
      this(var1, null);
   }

   public AddAltScreen(GuiScreen var1, Alt var2) {
      this.a = var1;
      this.P = var2;
      if (var2 != null && var2.F) {
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
      long var1 = b ^ 118591459494541L;
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
      String var4 = "Hide password";
      this.d = new Checkbox(var4, this.width / 2 - (125 + this.fontRendererObj.getStringWidth(var4)) / 2, this.height / 6 + 115, true);
      long var10000 = 3834574151802937387L ^ var1;
      String var5 = "Microsoft";
      this.V = new Checkbox(var5, this.width / 2 - (-120 + this.fontRendererObj.getStringWidth(var5)) / 2, this.height / 6 + 115, this.l);
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      if (var1.id == 0) {
         if (this.V.a()) {
            this.startMSThread();
         } else {
            this.H();
            this.mc.displayGuiScreen(this.a);
         }
      } else if (var1.id == 1) {
         this.mc.displayGuiScreen(this.a);
      }
   }

   public void startMSThread() {
      new Thread(
            () -> {
               try {
                  this.y = HttpServer.create(new InetSocketAddress(59125), 0);
                  this.y.createContext("/", var1 -> {
                     try {
                        var1.getResponseHeaders().add("Location", "http://localhost:59125/end");
                        var1.sendResponseHeaders(302, -1L);
                        new Thread(() -> this.q(var1.getRequestURI().getQuery()), "IAS MS Auth Thread").start();
                     } catch (Throwable var8) {
                        System.out.println("Unable to process request 'auth' on MS auth server");

                        try {
                           if (this.y != null) {
                              this.y.stop(0);
                           }
                        } catch (Throwable var7x) {
                           System.out.println("Unable to stop fail-requested MS auth server");
                        }
                     }
                  });
                  this.y
                     .createContext(
                        "/end",
                        var1 -> {
                           try {
                              String var5 = "<html><body><audio autoplay id=\"tuturu\"><source src=\"https://www.myinstants.com/media/sounds/tuturu_1.mp3\" type=\"audio/mpeg\">Your browser does not support the audio element.</audio><script>document.addEventListener('click', musicPlay);function musicPlay(){document.getElementById('tuturu').play(); document.removeEventListener('click', musicPlay);}</script></script></body></html>";
                              byte[] var6x = var5.getBytes(StandardCharsets.UTF_8);
                              var1.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=UTF-8"));
                              var1.sendResponseHeaders(200, var6x.length);
                              OutputStream var7x = var1.getResponseBody();
                              var7x.write(var6x);
                              var7x.flush();
                              var7x.close();

                              try {
                                 if (this.y != null) {
                                    this.y.stop(0);
                                 }
                              } catch (Throwable var10) {
                                 System.out.println("Unable to stop MS auth server");
                              }
                           } catch (Throwable var11) {
                              System.out.println("Unable to process request 'end' on MS auth server");

                              try {
                                 if (this.y != null) {
                                    this.y.stop(0);
                                 }
                              } catch (Throwable var9) {
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
               } catch (Throwable var7) {
                  var7.printStackTrace();
                  System.out.println("Unable to start MS auth server");

                  try {
                     if (this.y != null) {
                        this.y.stop(0);
                     }
                  } catch (Throwable var6) {
                     System.out.println("Unable to stop fail-started MS auth server");
                  }
               }
            },
            "IAS MS Auth Server Thread"
         )
         .start();
   }

   private void q(String var1) {
      long var2 = b ^ 57450911605727L;
      long var4 = var2 ^ 81090810350741L;

      try {
         if (var1 == null) {
            throw new NullPointerException("query=null");
         }

         if (var1.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) {
            throw new AuthException("ias.msauth.error.revoked");
         }

         if (!var1.startsWith("code=")) {
            throw new IllegalStateException("query=" + var1);
         }

         Pair var7 = MicrosoftAuth.X(var1.replace("code=", ""));
         String var18 = (String)var7.getRight();
         String var9 = MicrosoftAuth.x((String)var7.getLeft());
         Pair var10 = MicrosoftAuth.Q(var9);
         String var11 = MicrosoftAuth.P((String)var10.getRight(), (String)var10.getLeft());
         MicrosoftAuth.I_void(var11);
         Pair var12 = MicrosoftAuth.m(var11);
         this.mc.session = new Session((String)var12.getRight(), ((UUID)var12.getLeft()).toString(), var11, "mojang");
         Alt var13 = new Alt((String)var12.getRight(), "");
         var13.U = ((UUID)var12.getLeft()).toString();
         var13.o = (String)var12.getRight();
         var13.y = var11;
         var13.Z = var18;
         var13.F = true;
         List var14 = Astolfo.INSTANCE.getAltManager().getList();
         int var15 = var14.size();
         if (!this.q()) {
            var15 = var14.indexOf(this.P);
            var14.remove(var15);
         }

         var14.add(var15, var13);
         Toasts.TOASTS.g("Microsoft Account", "Logged successfully to " + (String)var12.getRight());
         this.mc.displayGuiScreen(this.a);
      } catch (Throwable var16) {
         if (var16 instanceof AuthException) {
            AuthException var8 = (AuthException)var16;
            if (var8.U().equals("ias.msauth.error.gamenotowned")) {
               Toasts.TOASTS.k("Microsoft Account", "This account does not own Minecraft");
            }

            if (var8.U().equals("ias.msauth.error.noxbox")) {
               Toasts.TOASTS.k("Microsoft Account", "This account does not have a Xbox account setup");
            }
         }

         if (var16 instanceof IllegalArgumentException) {
            IllegalArgumentException var17 = (IllegalArgumentException)var16;
            if (var17.getMessage().contains("getProfile response:")) {
               Toasts.TOASTS.k("Microsoft Account", "This account does not have a Minecraft name set");
            }
         }

         this.y.stop(0);
         this.y = null;
         var16.printStackTrace();
         System.out.println("Unable");
      }
   }

   public static void e(URI var0) throws IOException {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case MACOSX:
            Desktop.getDesktop().browse(var0);
            break;
         case WINDOWS:
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + var0);
            break;
         case LINUX:
            Runtime var4 = Runtime.getRuntime();
            String[] var5 = new String[]{"xdg-open", "google-chrome", "firefox", "opera", "konqueror", "mozilla"};

            for (String var9 : var5) {
               try (InputStream var10 = var4.exec(new String[]{"which", var9}).getInputStream()) {
                  if (var10.read() != -1) {
                     var4.exec(new String[]{var9, var0.toString()});
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
      long var1 = b ^ 109395514741362L;
      long var3 = var1 ^ 34078838783288L;
      List var6 = Astolfo.INSTANCE.getAltManager().getList();
      int var7 = var6.size();
      if (!this.q()) {
         var7 = var6.indexOf(this.P);
         var6.remove(var7);
      }

      if (this.n.getText().contains(":")) {
         String[] var8 = this.n.getText().split(":");
         if (var8.length == 2) {
            Alt var9 = new Alt(var8[0], var8[1]);
            AltLogin.AUTH.login(var9, false);
            var6.add(var7, var9);
         }
      } else {
         Alt var10 = new Alt(this.n.getText(), this.K.getText());
         AltLogin.AUTH.login(var10, false);
         var6.add(var7, var10);
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var1 == '\t') {
         this.n.setFocused(this.K.isFocused());
         this.K.setFocused(!this.n.isFocused());
      }

      if (this.n.isFocused()) {
         this.n.textboxKeyTyped(var1, var2);
         if (var1 == '\r') {
            this.H();
            this.mc.displayGuiScreen(this.a);
         }
      } else if (this.K.isFocused()) {
         this.K.textboxKeyTyped(var1, var2);
         if (var1 == '\r') {
            this.H();
            this.mc.displayGuiScreen(this.a);
         }
      }

      super.keyTyped(var1, var2);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.n.mouseClicked(var1, var2, var3);
      this.K.mouseClicked(var1, var2, var3);
      this.d.b(var1, var2, var3);
      this.V.b(var1, var2, var3);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(
         this.fontRendererObj,
         this.q() ? (this.y != null ? "Waiting for Microsoft login (browser)" : "Add an account") : "Edit an account",
         this.width / 2,
         30,
         -1
      );
      String var7 = "Username or email:";
      this.K.setVisible(true);
      if (this.n.getText().contains("@")) {
         var7 = "Email:";
         if (this.n.getText().contains(":")) {
            var7 = "Email and password:";
            this.K.setVisible(false);
         }
      } else if (this.n.getText().contains(":")) {
         var7 = "Username and password:";
         this.K.setVisible(false);
      }

      this.f.displayString = this.V.a() ? "Login" : "Save Alt";
      this.f.enabled = this.y == null;
      if (!this.V.a()) {
         if (this.K.getVisible()) {
            this.I(this.fontRendererObj, "*Password:", this.width / 2 - 100, this.height / 6 + 68, -1);
         }

         this.I(this.fontRendererObj, var7, this.width / 2 - 100, this.height / 6 + 23, -1);
      }

      this.d(this.fontRendererObj, "*Leave blank for offline account", this.width / 2, this.height / 6 + 180, -1);
      this.K.Z(this.d.a());
      this.n.setVisible(!this.V.a());
      this.K.setVisible(!this.V.a());
      this.n.drawTextBox();
      this.K.drawTextBox();
      this.d.E(var1, var2);
      this.V.E(var1, var2);
      super.drawScreen(var1, var2, var3);
   }

   private boolean q() {
      return this.P == null;
   }
}
