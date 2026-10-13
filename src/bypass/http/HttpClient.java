package bypass.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public final class HttpClient {
   private static final int C = 443;
   public static final String v = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
   private final SSLContext P = SSLContext.getInstance("SSL");
   private final TrustManager[] i = new TrustManager[]{new TrustAllManager()};
   private final HostnameVerifier N = new TrustAllHostnameVerifier();
   private boolean n;
   private static final long a = 88872503942442L;

   public HttpClient() throws NoSuchAlgorithmException, KeyManagementException {
      this(true);
   }

   public HttpClient(boolean var1) throws NoSuchAlgorithmException, KeyManagementException {
      this.s(var1);
      this.P.init(null, this.i, new SecureRandom());
   }

   public boolean R() {
      return this.n;
   }

   public void s(boolean var1) {
      this.n = var1;
   }

   public Response A(URI var1, RequestOptions var2) throws Exception {
      return this.G(var1.toURL(), var2);
   }

   public Response G(String var1, RequestOptions var2) throws Exception {
      return this.G(new URL(var1), var2);
   }

   public Response G(URL var1, RequestOptions var2) throws Exception {
      AbstractRequest var6 = var2.S();
      String var7 = var6.D();
      var1 = var6.m(var1);
      URLConnection var8 = this.p(var1);
      String var9 = var6.t();
      if (var9.equals(HttpMethod.POST.name())) {
         var8.setDoOutput(true);
      }

      HttpURLConnection var10 = (HttpURLConnection)var8;
      int var11 = var2.e();
      int var12 = var2.o();
      if (var12 >= 0) {
         var10.setReadTimeout(var12);
      }

      if (var11 >= 0) {
         var10.setConnectTimeout(var11);
      }

      var10.setRequestMethod(var9);
      n(var10, var6.U());
      n(var10, var2.Q());
      if (var8.getDoOutput()) {
         try (DataOutputStream var13 = new DataOutputStream(var8.getOutputStream())) {
            var13.writeBytes(var7);
            var13.flush();
         }
      }

      return Response.g(var10, false);
   }

   public static void n(URLConnection var0, Map<String, String> var1) {
      for (Entry var6 : var1.entrySet()) {
         var0.addRequestProperty((String)var6.getKey(), (String)var6.getValue());
      }
   }

   private URLConnection p(URL var1) throws IOException {
      if (e(var1) && !this.R()) {
         HttpsURLConnection var5 = (HttpsURLConnection)var1.openConnection();
         var5.setSSLSocketFactory(this.P.getSocketFactory());
         var5.setHostnameVerifier(this.N);
         return var5;
      } else {
         return var1.openConnection();
      }
   }

   public static boolean e(URL var0) {
      return var0.getPort() == 443 || var0.getProtocol().trim().equalsIgnoreCase("https");
   }

   static {
      long var20 = a ^ 95083715434574L;
      long var10000 = var20 ^ 5749221813930308799L;
   }
}
