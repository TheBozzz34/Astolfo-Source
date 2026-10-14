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

   public MicrosoftLoginScreen(GuiScreen guiScreen) {
      this.q = guiScreen;
      String string = "<html><body><h1>no</h1></body></html>";
      new Thread(
            () -> {
               try {
                  this.i = HttpServer.create(new InetSocketAddress(59125), 0);
                  this.i.createContext("/", httpExchange -> {
                     try {
                        httpExchange.getResponseHeaders().add("Location", "http://localhost:59125/end");
                        httpExchange.sendResponseHeaders(302, -1L);
                        new Thread(() -> this.w(httpExchange.getRequestURI().getQuery()), "IAS MS Auth Thread").start();
                     } catch (Throwable throwable) {
                        System.out.println("Unable to process request 'auth' on MS auth server");

                        try {
                           if (this.i != null) {
                              this.i.stop(0);
                           }
                        } catch (Throwable throwable2) {
                           System.out.println("Unable to stop fail-requested MS auth server");
                        }
                     }
                  });
                  this.i.createContext("/end", httpExchange2 -> {
                     try {
                        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
                        httpExchange2.getResponseHeaders().put("Content-Type", Collections.singletonList("text/html; charset=UTF-8"));
                        httpExchange2.sendResponseHeaders(200, bytes.length);
                        OutputStream outputStream = httpExchange2.getResponseBody();
                        outputStream.write(bytes);
                        outputStream.flush();
                        outputStream.close();

                        try {
                           if (this.i != null) {
                              this.i.stop(0);
                           }
                        } catch (Throwable throwable3) {
                           System.out.println("Unable to stop MS auth server");
                        }
                     } catch (Throwable throwable4) {
                        System.out.println("Unable to process request 'end' on MS auth server");

                        try {
                           if (this.i != null) {
                              this.i.stop(0);
                           }
                        } catch (Throwable throwable5) {
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
               } catch (Throwable throwable6) {
                  System.out.println("Unable to start MS auth server");

                  try {
                     if (this.i != null) {
                        this.i.stop(0);
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

   private void w(String string) {
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
         System.out.printf("%s %s %s %s", pair3.getRight(), string4, string2, pair3.getLeft());
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         System.out.println("Unable");
      }
   }
}
