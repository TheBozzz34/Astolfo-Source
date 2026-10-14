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
   private static final FingerprintRegistry m = new DefaultFingerprintRegistry();
   private static final HwidBuilder M = new HwidOptions().P(true).i(true).J(true).O(true).R(true).d(true).H();

   private static void Y(HttpURLConnection httpURLConnection, StringBuilder stringBuilder) {
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

   public static String g(AuthHttpRequest authHttpRequest) {
      return I(authHttpRequest, Proxy.NO_PROXY);
   }

   public static String I(AuthHttpRequest authHttpRequest, Proxy proxy) {
      try {
         String string = authHttpRequest.h();
         URL url = new URL(authHttpRequest.v + (string.isEmpty() ? "" : "?" + string));
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("User-Agent", authHttpRequest.F);
         httpURLConnection.setRequestProperty("Accept", authHttpRequest.P);
         httpURLConnection.setRequestProperty("Content-Type", authHttpRequest.u);

         for (int j = 0; j < authHttpRequest.i.length; j++) {
            httpURLConnection.setRequestProperty(authHttpRequest.i[j], authHttpRequest.n[j]);
         }

         StringBuilder stringBuilder = new StringBuilder();
         Y(httpURLConnection, stringBuilder);
         return stringBuilder.toString();
      } catch (Exception exception) {
         NullPointerException nullPointerException = new NullPointerException();
         nullPointerException.setStackTrace(new StackTraceElement[0]);
         throw nullPointerException;
      }
   }

   public static String Y(AuthHttpRequest authHttpRequest) {
      try {
         String string = authHttpRequest.h();
         URL url = new URL(authHttpRequest.v + (string.isEmpty() ? "" : "?" + string));
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("User-Agent", authHttpRequest.F);
         httpURLConnection.setRequestProperty("Accept", authHttpRequest.P);
         httpURLConnection.setRequestProperty("Content-Type", authHttpRequest.u);

         for (int j = 0; j < authHttpRequest.i.length; j++) {
            httpURLConnection.setRequestProperty(authHttpRequest.i[j], authHttpRequest.n[j]);
         }

         StringBuilder stringBuilder = new StringBuilder();
         Y(httpURLConnection, stringBuilder);
         return stringBuilder.toString();
      } catch (IOException ioException) {
         return ioException.getMessage();
      }
   }

   public static String k(AuthHttpRequest authHttpRequest) {
      return L(authHttpRequest, Proxy.NO_PROXY);
   }

   public static String L(AuthHttpRequest authHttpRequest, Proxy proxy) {
      try {
         URL url = new URL(authHttpRequest.v);
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("User-Agent", authHttpRequest.F);
         httpURLConnection.setRequestProperty("Accept", authHttpRequest.P);
         httpURLConnection.setRequestProperty("Content-Type", authHttpRequest.u);

         for (int j = 0; j < authHttpRequest.i.length; j++) {
            httpURLConnection.setRequestProperty(authHttpRequest.i[j], authHttpRequest.n[j]);
         }

         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
         dataOutputStream.writeBytes(authHttpRequest.h());
         dataOutputStream.flush();
         dataOutputStream.close();
         StringBuilder stringBuilder = new StringBuilder();
         Y(httpURLConnection, stringBuilder);
         return stringBuilder.toString().replace("\n", "");
      } catch (Exception exception) {
         NullPointerException nullPointerException = new NullPointerException();
         nullPointerException.setStackTrace(new StackTraceElement[0]);
         throw nullPointerException;
      }
   }

   public static void v(String string) {
      try {
         u(new URI(string));
      } catch (URISyntaxException uriSyntaxException) {
         uriSyntaxException.printStackTrace();
      }
   }

   private static void u(URI uri) {
      try {
         Desktop.getDesktop().browse(uri);
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static String getHwid() {
      return md5(M.J());
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
      m.e();
   }
}
