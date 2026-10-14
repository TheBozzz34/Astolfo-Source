package bypass.http;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public final class TrustAllManager implements X509TrustManager {
   public TrustAllManager() {
   }

   @Override
   public void checkClientTrusted(X509Certificate[] x509Certificates, String string) {
   }

   @Override
   public void checkServerTrusted(X509Certificate[] x509Certificates, String string) {
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      return null;
   }
}
