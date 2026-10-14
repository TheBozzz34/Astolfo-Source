package bypass.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public final class TrustAllHostnameVerifier implements HostnameVerifier {
   public TrustAllHostnameVerifier() {
   }

   @Override
   public boolean verify(String string, SSLSession sslSession) {
      return true;
   }
}
