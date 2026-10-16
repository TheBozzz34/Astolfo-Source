package bypass.auth;

import bypass.auth.fingerprint.DefaultFingerprintRegistry;
import bypass.auth.fingerprint.FingerprintRegistry;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class AuthUtil {
   private static final FingerprintRegistry fingerprintRegistry = new DefaultFingerprintRegistry();
   private static final HwidBuilder hwidBuilder = new HwidOptions()
      .withUsername(true)
      .withCpu(true)
      .withBaseboard(true)
      .withGpu(true)
      .withMemory(true)
      .withDisk(true)
      .build();

   private static void readResponse(HttpURLConnection httpURLConnection, StringBuilder stringBuilder) {
      try {
         InputStream inputStream;
         if (httpURLConnection.getResponseCode() == 200) {
            inputStream = httpURLConnection.getInputStream();
         } else {
            inputStream = httpURLConnection.getErrorStream();
         }

         if (inputStream == null) {
            throw new Exception();
         }

         InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
         BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

         String string;
         while ((string = bufferedReader.readLine()) != null) {
            stringBuilder.append(string).append("\\n");
         }

         bufferedReader.close();
      } catch (Exception exception) {
      }
   }

   public static String httpGet(AuthHttpRequest authHttpRequest) {
      return httpGetWithProxy(authHttpRequest, Proxy.NO_PROXY);
   }

   public static String httpGetWithProxy(AuthHttpRequest authHttpRequest, Proxy proxy) {
      try {
         String string = authHttpRequest.buildBody();
         URL url2 = new URL(authHttpRequest.url + (string.isEmpty() ? "" : "?" + string));
         HttpURLConnection httpURLConnection = (HttpURLConnection)url2.openConnection(proxy);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("User-Agent", authHttpRequest.userAgent);
         httpURLConnection.setRequestProperty("Accept", authHttpRequest.accept);
         httpURLConnection.setRequestProperty("Content-Type", authHttpRequest.contentType);

         for (int i = 0; i < authHttpRequest.headerNames.length; i++) {
            httpURLConnection.setRequestProperty(authHttpRequest.headerNames[i], authHttpRequest.headerValues[i]);
         }

         StringBuilder stringBuilder = new StringBuilder();
         readResponse(httpURLConnection, stringBuilder);
         return stringBuilder.toString();
      } catch (Exception exception) {
         NullPointerException nullPointerException = new NullPointerException();
         nullPointerException.setStackTrace(new StackTraceElement[0]);
         throw nullPointerException;
      }
   }

   public static String httpGetOrError(AuthHttpRequest authHttpRequest) {
      try {
         String string = authHttpRequest.buildBody();
         URL url2 = new URL(authHttpRequest.url + (string.isEmpty() ? "" : "?" + string));
         HttpURLConnection httpURLConnection = (HttpURLConnection)url2.openConnection(Proxy.NO_PROXY);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("User-Agent", authHttpRequest.userAgent);
         httpURLConnection.setRequestProperty("Accept", authHttpRequest.accept);
         httpURLConnection.setRequestProperty("Content-Type", authHttpRequest.contentType);

         for (int i = 0; i < authHttpRequest.headerNames.length; i++) {
            httpURLConnection.setRequestProperty(authHttpRequest.headerNames[i], authHttpRequest.headerValues[i]);
         }

         StringBuilder stringBuilder = new StringBuilder();
         readResponse(httpURLConnection, stringBuilder);
         return stringBuilder.toString();
      } catch (IOException ioException) {
         return ioException.getMessage();
      }
   }

   public static String httpPost(AuthHttpRequest authHttpRequest) {
      return httpPostWithProxy(authHttpRequest, Proxy.NO_PROXY);
   }

   public static String httpPostWithProxy(AuthHttpRequest authHttpRequest, Proxy proxy) {
      try {
         URL url2 = new URL(authHttpRequest.url);
         HttpURLConnection httpURLConnection = (HttpURLConnection)url2.openConnection(proxy);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("User-Agent", authHttpRequest.userAgent);
         httpURLConnection.setRequestProperty("Accept", authHttpRequest.accept);
         httpURLConnection.setRequestProperty("Content-Type", authHttpRequest.contentType);

         for (int i = 0; i < authHttpRequest.headerNames.length; i++) {
            httpURLConnection.setRequestProperty(authHttpRequest.headerNames[i], authHttpRequest.headerValues[i]);
         }

         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
         dataOutputStream.writeBytes(authHttpRequest.buildBody());
         dataOutputStream.flush();
         dataOutputStream.close();
         StringBuilder stringBuilder = new StringBuilder();
         readResponse(httpURLConnection, stringBuilder);
         return stringBuilder.toString().replace("\n", "");
      } catch (Exception exception) {
         NullPointerException nullPointerException = new NullPointerException();
         nullPointerException.setStackTrace(new StackTraceElement[0]);
         throw nullPointerException;
      }
   }

   public static void openUrl(String string) {
      try {
         browseUri(new URI(string));
      } catch (URISyntaxException uriSyntaxException) {
         uriSyntaxException.printStackTrace();
      }
   }

   private static void browseUri(URI uri) {
      try {
         Desktop.getDesktop().browse(uri);
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static String getHwid() {
      return md5(hwidBuilder.getRawHwid());
   }

   public static String md5(String string) {
      try {
         MessageDigest messageDigest = MessageDigest.getInstance("MD5");
         byte[] bytes = messageDigest.digest(string.getBytes());
         StringBuilder stringBuilder = new StringBuilder();

         for (byte b : bytes) {
            String string2 = Integer.toHexString(255 & b);
            if (string2.length() == 1) {
               stringBuilder.append('0');
            }

            stringBuilder.append(string2);
         }

         return stringBuilder.toString();
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         throw new RuntimeException(noSuchAlgorithmException);
      }
   }

   static {
      fingerprintRegistry.init();
   }
}
