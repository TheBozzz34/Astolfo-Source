package bypass.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class HttpUtil {
   private static final long a = 15938868970630L;
   private static final long e = 8126313492534788296L;

   private static void readResponse(HttpURLConnection httpURLConnection, StringBuilder stringBuilder) {
      try {
         InputStream inputStream;
         if (httpURLConnection.getResponseCode() == (int)e) {
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
            stringBuilder.append(string).append("\n");
         }

         bufferedReader.close();
      } catch (Exception exception) {
      }
   }

   public static String get(HttpRequest httpRequest) {
      return get(httpRequest, Proxy.NO_PROXY);
   }

   public static String get(HttpRequest httpRequest, Proxy proxy) {
      try {
         String string = httpRequest.N();
         URL url = new URL(httpRequest.o + (string.isEmpty() ? "" : "?" + string));
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("User-Agent", httpRequest.X);
         httpURLConnection.setRequestProperty("Accept", httpRequest.I);
         httpURLConnection.setRequestProperty("Content-Type", httpRequest.M);

         for (int i = 0; i < httpRequest.H.length; i++) {
            httpURLConnection.setRequestProperty(httpRequest.H[i], httpRequest.J[i]);
         }

         StringBuilder stringBuilder = new StringBuilder();
         readResponse(httpURLConnection, stringBuilder);
         return stringBuilder.toString();
      } catch (Exception exception) {
         return null;
      }
   }

   public static String post(HttpRequest httpRequest) {
      return post(httpRequest, Proxy.NO_PROXY);
   }

   public static String post(HttpRequest httpRequest, Proxy proxy) {
      try {
         URL url = new URL(httpRequest.o);
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("User-Agent", httpRequest.X);
         httpURLConnection.setRequestProperty("Accept", httpRequest.I);
         httpURLConnection.setRequestProperty("Content-Type", httpRequest.M);

         for (int i = 0; i < httpRequest.H.length; i++) {
            httpURLConnection.setRequestProperty(httpRequest.H[i], httpRequest.J[i]);
         }

         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
         dataOutputStream.writeBytes(httpRequest.N());
         dataOutputStream.flush();
         dataOutputStream.close();
         StringBuilder stringBuilder = new StringBuilder();
         readResponse(httpURLConnection, stringBuilder);
         return stringBuilder.toString();
      } catch (Exception exception) {
         return null;
      }
   }
}
