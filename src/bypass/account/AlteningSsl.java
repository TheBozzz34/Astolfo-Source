package bypass.account;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AlteningSsl {
   private boolean R = false;
   private static final long a = 19617274392763L;

   public void H() {
      if (!this.R) {
         this.i();
         this.whitelistTheAltening();
         this.R = true;
      }
   }

   private void i() {
      TrustManager[] trustManagers = new TrustManager[]{new AlteningSsl.alr()};

      try {
         SSLContext sslContext = SSLContext.getInstance("SSL");
         sslContext.init(null, trustManagers, new SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
      } catch (Exception exception) {
      }
   }

   private void whitelistTheAltening() {
      HttpsURLConnection.setDefaultHostnameVerifier((string, sslSession) -> string.equals("authserver.thealtening.com") || string.equals("sessionserver.thealtening.com"));
   }

   public boolean A() {
      return this.R;
   }

   public void m(boolean bl) {
      this.R = bl;
   }

   public class alr implements X509TrustManager {
      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String string) {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String string) {
      }
   }
}
