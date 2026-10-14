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

   public HttpClient() throws NoSuchAlgorithmException, KeyManagementException {
      this(true);
   }

   public HttpClient(boolean bl) throws NoSuchAlgorithmException, KeyManagementException {
      this.s(bl);
      this.P.init(null, this.i, new SecureRandom());
   }

   public boolean R() {
      return this.n;
   }

   public void s(boolean bl) {
      this.n = bl;
   }

   public Response A(URI uri, RequestOptions requestOptions) throws Exception {
      return this.G(uri.toURL(), requestOptions);
   }

   public Response G(String string, RequestOptions requestOptions) throws Exception {
      return this.G(new URL(string), requestOptions);
   }

   public Response G(URL url, RequestOptions requestOptions) throws Exception {
      AbstractRequest abstractRequest = requestOptions.S();
      String string = abstractRequest.D();
      url = abstractRequest.m(url);
      URLConnection urlConnection = this.p(url);
      String string2 = abstractRequest.t();
      if (string2.equals(HttpMethod.POST.name())) {
         urlConnection.setDoOutput(true);
      }

      HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
      int i = requestOptions.e();
      int j = requestOptions.o();
      if (j >= 0) {
         httpURLConnection.setReadTimeout(j);
      }

      if (i >= 0) {
         httpURLConnection.setConnectTimeout(i);
      }

      httpURLConnection.setRequestMethod(string2);
      n(httpURLConnection, abstractRequest.U());
      n(httpURLConnection, requestOptions.Q());
      if (urlConnection.getDoOutput()) {
         try (DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream())) {
            dataOutputStream.writeBytes(string);
            dataOutputStream.flush();
         }
      }

      return Response.g(httpURLConnection, false);
   }

   public static void n(URLConnection urlConnection, Map<String, String> map) {
      for (Entry entry : map.entrySet()) {
         urlConnection.addRequestProperty((String)entry.getKey(), (String)entry.getValue());
      }
   }

   private URLConnection p(URL url) throws IOException {
      if (e(url) && !this.R()) {
         HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
         httpsURLConnection.setSSLSocketFactory(this.P.getSocketFactory());
         httpsURLConnection.setHostnameVerifier(this.N);
         return httpsURLConnection;
      } else {
         return url.openConnection();
      }
   }

   public static boolean e(URL url) {
      return url.getPort() == 443 || url.getProtocol().trim().equalsIgnoreCase("https");
   }
}
