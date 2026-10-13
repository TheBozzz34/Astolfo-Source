package bypass.gui.screen;

import bypass.account.AuthException;
import bypass.account.MicrosoftAuth;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.tuple.Pair;

public class MicrosoftLoginScreen extends GuiScreen {
   public static final String[] K = new String[]{
      "▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃",
      "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
      "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅",
      "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆",
      "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇",
      "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █",
      "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇",
      "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆",
      "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅",
      "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
      "▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃",
      "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _",
      "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _",
      "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
      "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _",
      "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _",
      "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _",
      "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
      "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _",
      "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"
   };
   public final GuiScreen q;
   private HttpServer i;
   private int f;
   private static final long a = 9475853737830L;

   public MicrosoftLoginScreen(GuiScreen var1) {
      this.q = var1;
      String var4 = "<html><body><h1>no</h1></body></html>";
      new Thread(
            () -> {
               try {
                  this.i = HttpServer.create(new InetSocketAddress(59125), 0);
                  this.i.createContext("/", var1xx -> {
                     try {
                        var1xx.getResponseHeaders().add("Location", "http://localhost:59125/end");
                        var1xx.sendResponseHeaders(302, -1L);
                        new Thread(() -> this.w(var1xx.getRequestURI().getQuery()), "IAS MS Auth Thread").start();
                     } catch (Throwable var8x) {
                        System.out.println("Unable to process request 'auth' on MS auth server");

                        try {
                           if (this.i != null) {
                              this.i.stop(0);
                           }
                        } catch (Throwable var7x) {
                           System.out.println("Unable to stop fail-requested MS auth server");
                        }
                     }
                  });
                  this.i.createContext("/end", var2 -> {
                     try {
                        byte[] var6 = var4.getBytes(StandardCharsets.UTF_8);
                        var2.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=UTF-8"));
                        var2.sendResponseHeaders(200, var6.length);
                        OutputStream var7x = var2.getResponseBody();
                        var7x.write(var6);
                        var7x.flush();
                        var7x.close();

                        try {
                           if (this.i != null) {
                              this.i.stop(0);
                           }
                        } catch (Throwable var10) {
                           System.out.println("Unable to stop MS auth server");
                        }
                     } catch (Throwable var11) {
                        System.out.println("Unable to process request 'end' on MS auth server");

                        try {
                           if (this.i != null) {
                              this.i.stop(0);
                           }
                        } catch (Throwable var9) {
                           System.out.println("Unable to stop fail-requested MS auth server");
                        }
                     }
                  });
                  this.i.start();
                  Desktop.getDesktop()
                     .browse(
                        new URI(
                           "https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=consent"
                        )
                     );
               } catch (Throwable var8) {
                  System.out.println("Unable to start MS auth server");

                  try {
                     if (this.i != null) {
                        this.i.stop(0);
                     }
                  } catch (Throwable var7) {
                     System.out.println("Unable to stop fail-started MS auth server");
                  }
               }
            },
            "IAS MS Auth Server Thread"
         )
         .start();
   }

   private void w(String var1) {
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

         Pair var5 = MicrosoftAuth.X(var1.replace("code=", ""));
         String var6 = (String)var5.getRight();
         String var7 = MicrosoftAuth.x((String)var5.getLeft());
         Pair var8 = MicrosoftAuth.Q(var7);
         String var9 = MicrosoftAuth.P((String)var8.getRight(), (String)var8.getLeft());
         MicrosoftAuth.I_void(var9);
         Pair var10 = MicrosoftAuth.m(var9);
         System.out.printf("%s %s %s %s", var10.getRight(), var9, var6, var10.getLeft());
      } catch (Throwable var11) {
         var11.printStackTrace();
         System.out.println("Unable");
      }
   }
}
