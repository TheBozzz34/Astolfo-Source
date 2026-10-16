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
   private static final int HTTPS_PORT = 443;
   public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
   private final SSLContext sslContext = SSLContext.getInstance("SSL");
   private final TrustManager[] trustManagers = new TrustManager[]{new TrustAllManager()};
   private final HostnameVerifier hostnameVerifier = new TrustAllHostnameVerifier();
   private boolean strictSsl;

   public HttpClient() throws NoSuchAlgorithmException, KeyManagementException {
      this(true);
   }

   public HttpClient(boolean bl) throws NoSuchAlgorithmException, KeyManagementException {
      this.setStrictSsl(bl);
      this.sslContext.init(null, this.trustManagers, new SecureRandom());
   }

   public boolean isStrictSsl() {
      return this.strictSsl;
   }

   public void setStrictSsl(boolean bl) {
      this.strictSsl = bl;
   }

   public Response executeUri(URI uri, RequestOptions requestOptions) throws Exception {
      return this.execute(uri.toURL(), requestOptions);
   }

   public Response executeUrlString(String string, RequestOptions requestOptions) throws Exception {
      return this.execute(new URL(string), requestOptions);
   }

   public Response execute(URL url, RequestOptions requestOptions) throws Exception {
      AbstractRequest abstractRequest = requestOptions.getRequest();
      String string = abstractRequest.getBody();
      url = abstractRequest.buildUrl(url);
      URLConnection urlConnection = this.openConnection(url);
      String string2 = abstractRequest.getMethod();
      if (string2.equals(HttpMethod.POST.name())) {
         urlConnection.setDoOutput(true);
      }

      HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
      int i = requestOptions.getConnectTimeout();
      int j = requestOptions.getReadTimeout();
      if (j >= 0) {
         httpURLConnection.setReadTimeout(j);
      }

      if (i >= 0) {
         httpURLConnection.setConnectTimeout(i);
      }

      httpURLConnection.setRequestMethod(string2);
      addHeaders(httpURLConnection, abstractRequest.getHeaders());
      addHeaders(httpURLConnection, requestOptions.getHeaders());
      if (urlConnection.getDoOutput()) {
         try (DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream())) {
            dataOutputStream.writeBytes(string);
            dataOutputStream.flush();
         }
      }

      return Response.fromConnection(httpURLConnection, false);
   }

   public static void addHeaders(URLConnection urlConnection, Map<String, String> map) {
      for (Entry entry : map.entrySet()) {
         urlConnection.addRequestProperty((String)entry.getKey(), (String)entry.getValue());
      }
   }

   private URLConnection openConnection(URL url) throws IOException {
      if (isHttps(url) && !this.isStrictSsl()) {
         HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
         httpsURLConnection.setSSLSocketFactory(this.sslContext.getSocketFactory());
         httpsURLConnection.setHostnameVerifier(this.hostnameVerifier);
         return httpsURLConnection;
      } else {
         return url.openConnection();
      }
   }

   public static boolean isHttps(URL url) {
      return url.getPort() == 443 || url.getProtocol().trim().equalsIgnoreCase("https");
   }
}
