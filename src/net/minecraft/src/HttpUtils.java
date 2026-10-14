package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class HttpUtils {
   public static final String v = "http://s.optifine.net";
   public static final String I = "http://optifine.net";
   private static final long a = 136420857371154L;

   public static byte[] get(String urlStr) throws IOException {
      HttpURLConnection httpURLConnection = null;

      byte[] bytes;
      try {
         URL url = new URL(urlStr);
         httpURLConnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(false);
         httpURLConnection.connect();
         if (httpURLConnection.getResponseCode() / 100 != 2) {
            if (httpURLConnection.getErrorStream() != null) {
               Config.readAll(httpURLConnection.getErrorStream());
            }

            throw new IOException("HTTP response: " + httpURLConnection.getResponseCode());
         }

         InputStream inputStream = httpURLConnection.getInputStream();
         byte[] bytes2 = new byte[httpURLConnection.getContentLength()];
         int i = 0;

         do {
            int j = inputStream.read(bytes2, i, bytes2.length - i);
            if (j < 0) {
               throw new IOException("Input stream closed: " + urlStr);
            }

            i += j;
         } while (i < bytes2.length);

         bytes = bytes2;
      } finally {
         if (httpURLConnection != null) {
            httpURLConnection.disconnect();
         }
      }

      return bytes;
   }

   public static String post(String urlStr, Map headers, byte[] content) throws IOException {
      HttpURLConnection httpURLConnection = null;

      String string;
      try {
         URL url = new URL(urlStr);
         httpURLConnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
         httpURLConnection.setRequestMethod("POST");
         if (headers != null) {
            for (Object object : headers.keySet()) {
               String string2 = "" + headers.get(object);
               httpURLConnection.setRequestProperty((String)object, string2);
            }
         }

         httpURLConnection.setRequestProperty("Content-Type", "text/plain");
         httpURLConnection.setRequestProperty("Content-Length", "" + content.length);
         httpURLConnection.setRequestProperty("Content-Language", "en-US");
         httpURLConnection.setUseCaches(false);
         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         OutputStream outputStream = httpURLConnection.getOutputStream();
         outputStream.write(content);
         outputStream.flush();
         outputStream.close();
         InputStream inputStream = httpURLConnection.getInputStream();
         InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ASCII");
         BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
         StringBuffer stringBuffer = new StringBuffer();

         String string3;
         while ((string3 = bufferedReader.readLine()) != null) {
            stringBuffer.append(string3);
            stringBuffer.append('\r');
         }

         bufferedReader.close();
         string = stringBuffer.toString();
      } finally {
         if (httpURLConnection != null) {
            httpURLConnection.disconnect();
         }
      }

      return string;
   }
}
