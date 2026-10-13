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

   public static byte[] get(String var0) throws IOException {
      HttpURLConnection var4 = null;

      byte[] var5;
      try {
         URL var6 = new URL(var0);
         var4 = (HttpURLConnection)var6.openConnection(Minecraft.getMinecraft().getProxy());
         var4.setDoInput(true);
         var4.setDoOutput(false);
         var4.connect();
         if (var4.getResponseCode() / 100 != 2) {
            if (var4.getErrorStream() != null) {
               Config.readAll(var4.getErrorStream());
            }

            throw new IOException("HTTP response: " + var4.getResponseCode());
         }

         InputStream var7 = var4.getInputStream();
         byte[] var8 = new byte[var4.getContentLength()];
         int var9 = 0;

         do {
            int var10 = var7.read(var8, var9, var8.length - var9);
            if (var10 < 0) {
               throw new IOException("Input stream closed: " + var0);
            }

            var9 += var10;
         } while (var9 < var8.length);

         var5 = var8;
      } finally {
         if (var4 != null) {
            var4.disconnect();
         }
      }

      return var5;
   }

   public static String post(String var0, Map var1, byte[] var2) throws IOException {
      HttpURLConnection var6 = null;

      String var7;
      try {
         URL var8 = new URL(var0);
         var6 = (HttpURLConnection)var8.openConnection(Minecraft.getMinecraft().getProxy());
         var6.setRequestMethod("POST");
         if (var1 != null) {
            for (Object var10 : var1.keySet()) {
               String var11 = "" + var1.get(var10);
               var6.setRequestProperty((String)var10, var11);
            }
         }

         var6.setRequestProperty("Content-Type", "text/plain");
         var6.setRequestProperty("Content-Length", "" + var2.length);
         var6.setRequestProperty("Content-Language", "en-US");
         var6.setUseCaches(false);
         var6.setDoInput(true);
         var6.setDoOutput(true);
         OutputStream var18 = var6.getOutputStream();
         var18.write(var2);
         var18.flush();
         var18.close();
         InputStream var19 = var6.getInputStream();
         InputStreamReader var20 = new InputStreamReader(var19, "ASCII");
         BufferedReader var12 = new BufferedReader(var20);
         StringBuffer var13 = new StringBuffer();

         String var14;
         while ((var14 = var12.readLine()) != null) {
            var13.append(var14);
            var13.append('\r');
         }

         var12.close();
         var7 = var13.toString();
      } finally {
         if (var6 != null) {
            var6.disconnect();
         }
      }

      return var7;
   }
}
