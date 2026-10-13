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
      TrustManager[] var3 = new TrustManager[]{new AlteningSsl.alr()};

      try {
         SSLContext var4 = SSLContext.getInstance("SSL");
         var4.init(null, var3, new SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(var4.getSocketFactory());
      } catch (Exception var5) {
      }
   }

   private void whitelistTheAltening() {
      HttpsURLConnection.setDefaultHostnameVerifier((var0, var1) -> var0.equals("authserver.thealtening.com") || var0.equals("sessionserver.thealtening.com"));
   }

   public boolean A() {
      return this.R;
   }

   public void m(boolean var1) {
      this.R = var1;
   }

   public class alr implements X509TrustManager {
      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] var1, String var2) {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] var1, String var2) {
      }
   }
}
