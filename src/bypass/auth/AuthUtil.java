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
   private static final long a = 36911620430702L;

   private static void Y(HttpURLConnection var0, StringBuilder var1) {
      try {
         InputStream var5;
         if (var0.getResponseCode() == 200) {
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
            var1.append(var8).append(AuthUtilStrings.U("rKA="));
         }

         var7.close();
      } catch (Exception var9) {
      }
   }

   public static String g(AuthHttpRequest var0) {
      return I(var0, Proxy.NO_PROXY);
   }

   public static String I(AuthHttpRequest var0, Proxy var1) {
      try {
         String var5 = var0.h();
         URL var10 = new URL(var0.v + (var5.isEmpty() ? "" : "?" + var5));
         HttpURLConnection var7 = (HttpURLConnection)var10.openConnection(var1);
         var7.setRequestMethod(AuthUtilStrings.U("t4un"));
         var7.setRequestProperty(AuthUtilStrings.U("pb2WuLcxehEzMg=="), var0.F);
         var7.setRequestProperty(AuthUtilStrings.U("sa2Qr+oE"), var0.P);
         var7.setRequestProperty(AuthUtilStrings.U("s6Gdvv8eaVkJP/5C"), var0.u);

         for (int var8 = 0; var8 < var0.i.length; var8++) {
            var7.setRequestProperty(var0.i[var8], var0.n[var8]);
         }

         StringBuilder var11 = new StringBuilder();
         Y(var7, var11);
         return var11.toString();
      } catch (Exception var9) {
         NullPointerException var6 = new NullPointerException();
         var6.setStackTrace(new StackTraceElement[0]);
         throw var6;
      }
   }

   public static String Y(AuthHttpRequest var0) {
      try {
         String var4 = var0.h();
         URL var5 = new URL(var0.v + (var4.isEmpty() ? "" : "?" + var4));
         HttpURLConnection var6 = (HttpURLConnection)var5.openConnection(Proxy.NO_PROXY);
         var6.setRequestMethod(AuthUtilStrings.U("t4un"));
         var6.setRequestProperty(AuthUtilStrings.U("pb2WuLcxehEzMg=="), var0.F);
         var6.setRequestProperty(AuthUtilStrings.U("sa2Qr+oE"), var0.P);
         var6.setRequestProperty(AuthUtilStrings.U("s6Gdvv8eaVkJP/5C"), var0.u);

         for (int var7 = 0; var7 < var0.i.length; var7++) {
            var6.setRequestProperty(var0.i[var7], var0.n[var7]);
         }

         StringBuilder var9 = new StringBuilder();
         Y(var6, var9);
         return var9.toString();
      } catch (IOException var8) {
         return var8.getMessage();
      }
   }

   public static String k(AuthHttpRequest var0) {
      return L(var0, Proxy.NO_PROXY);
   }

   public static String L(AuthHttpRequest var0, Proxy var1) {
      try {
         URL var6 = new URL(var0.v);
         HttpURLConnection var11 = (HttpURLConnection)var6.openConnection(var1);
         var11.setRequestMethod(AuthUtilStrings.U("oIGgng=="));
         var11.setRequestProperty(AuthUtilStrings.U("pb2WuLcxehEzMg=="), var0.F);
         var11.setRequestProperty(AuthUtilStrings.U("sa2Qr+oE"), var0.P);
         var11.setRequestProperty(AuthUtilStrings.U("s6Gdvv8eaVkJP/5C"), var0.u);

         for (int var8 = 0; var8 < var0.i.length; var8++) {
            var11.setRequestProperty(var0.i[var8], var0.n[var8]);
         }

         var11.setDoInput(true);
         var11.setDoOutput(true);
         DataOutputStream var12 = new DataOutputStream(var11.getOutputStream());
         var12.writeBytes(var0.h());
         var12.flush();
         var12.close();
         StringBuilder var9 = new StringBuilder();
         Y(var11, var9);
         return var9.toString().replace("\n", "");
      } catch (Exception var10) {
         NullPointerException var7 = new NullPointerException();
         var7.setStackTrace(new StackTraceElement[0]);
         throw var7;
      }
   }

   public static void v(String var0) {
      try {
         u(new URI(var0));
      } catch (URISyntaxException var2) {
         var2.printStackTrace();
      }
   }

   private static void u(URI var0) {
      try {
         Desktop.getDesktop().browse(var0);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   public static String getHwid() {
      return md5(M.J());
   }

   public static String md5(String var0) {
      try {
         MessageDigest var4 = MessageDigest.getInstance("MD5");
         byte[] var5 = var4.digest(var0.getBytes());
         StringBuilder var6 = new StringBuilder();

         for (byte var10 : var5) {
            String var11 = Integer.toHexString(255 & var10);
            if (var11.length() == 1) {
               var6.append('0');
            }

            var6.append(var11);
         }

         return var6.toString();
      } catch (NoSuchAlgorithmException var12) {
         throw new RuntimeException(var12);
      }
   }

   static {
      m.e();
   }
}
