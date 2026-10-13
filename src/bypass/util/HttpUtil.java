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

   private static void readResponse(HttpURLConnection var0, StringBuilder var1) {
      try {
         InputStream var5;
         if (var0.getResponseCode() == (int)e) {
            var5 = var0.getInputStream();
         } else {
            var5 = var0.getErrorStream();
         }

         if (var5 == null) {
            throw new Exception();
         }

         InputStreamReader var6 = new InputStreamReader(var5);
         BufferedReader var7 = new BufferedReader(var6);

         String var8;
         while ((var8 = var7.readLine()) != null) {
            var1.append(var8).append("\n");
         }

         var7.close();
      } catch (Exception var9) {
      }
   }

   public static String get(HttpRequest var0) {
      return get(var0, Proxy.NO_PROXY);
   }

   public static String get(HttpRequest var0, Proxy var1) {
      try {
         String var5 = var0.N();
         URL var6 = new URL(var0.o + (var5.isEmpty() ? "" : "?" + var5));
         HttpURLConnection var7 = (HttpURLConnection)var6.openConnection(var1);
         var7.setRequestMethod("GET");
         var7.setRequestProperty("User-Agent", var0.X);
         var7.setRequestProperty("Accept", var0.I);
         var7.setRequestProperty("Content-Type", var0.M);

         for (int var8 = 0; var8 < var0.H.length; var8++) {
            var7.setRequestProperty(var0.H[var8], var0.J[var8]);
         }

         StringBuilder var10 = new StringBuilder();
         readResponse(var7, var10);
         return var10.toString();
      } catch (Exception var9) {
         return null;
      }
   }

   public static String post(HttpRequest var0) {
      return post(var0, Proxy.NO_PROXY);
   }

   public static String post(HttpRequest var0, Proxy var1) {
      try {
         URL var5 = new URL(var0.o);
         HttpURLConnection var6 = (HttpURLConnection)var5.openConnection(var1);
         var6.setRequestMethod("POST");
         var6.setRequestProperty("User-Agent", var0.X);
         var6.setRequestProperty("Accept", var0.I);
         var6.setRequestProperty("Content-Type", var0.M);

         for (int var7 = 0; var7 < var0.H.length; var7++) {
            var6.setRequestProperty(var0.H[var7], var0.J[var7]);
         }

         var6.setDoInput(true);
         var6.setDoOutput(true);
         DataOutputStream var10 = new DataOutputStream(var6.getOutputStream());
         var10.writeBytes(var0.N());
         var10.flush();
         var10.close();
         StringBuilder var8 = new StringBuilder();
         readResponse(var6, var8);
         return var8.toString();
      } catch (Exception var9) {
         return null;
      }
   }
}
