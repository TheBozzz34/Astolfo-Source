package bypass.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class HttpUtil {
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
         String string = httpRequest.buildParameterString();
         URL url2 = new URL(httpRequest.url + (string.isEmpty() ? "" : "?" + string));
         HttpURLConnection httpURLConnection = (HttpURLConnection)url2.openConnection(proxy);
         httpURLConnection.setRequestMethod("GET");
         httpURLConnection.setRequestProperty("User-Agent", httpRequest.userAgent);
         httpURLConnection.setRequestProperty("Accept", httpRequest.accept);
         httpURLConnection.setRequestProperty("Content-Type", httpRequest.contentType);

         for (int i = 0; i < httpRequest.headerKeys.length; i++) {
            httpURLConnection.setRequestProperty(httpRequest.headerKeys[i], httpRequest.headerValues[i]);
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
         URL url2 = new URL(httpRequest.url);
         HttpURLConnection httpURLConnection = (HttpURLConnection)url2.openConnection(proxy);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("User-Agent", httpRequest.userAgent);
         httpURLConnection.setRequestProperty("Accept", httpRequest.accept);
         httpURLConnection.setRequestProperty("Content-Type", httpRequest.contentType);

         for (int i = 0; i < httpRequest.headerKeys.length; i++) {
            httpURLConnection.setRequestProperty(httpRequest.headerKeys[i], httpRequest.headerValues[i]);
         }

         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
         dataOutputStream.writeBytes(httpRequest.buildParameterString());
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
