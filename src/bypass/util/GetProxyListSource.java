package bypass.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class GetProxyListSource implements ProxySource {
   private static final int t = 20;
   private final String Y;
   private final ExecutorService D;
   private final int P;
   private final int J;
   private int h;
   private static final long a = 106086643771983L;

   public GetProxyListSource(String var1, int var2, int var3) {
      this.Y = var1;
      this.D = Executors.newFixedThreadPool(var2);
      this.P = 20;
      this.J = var3;
   }

   @Override
   public Set<HostAndPort> scrap(EnumSet<ProxyType> var1) {
      String var5 = "https://api.getproxylist.com/proxy?apiKey=" + this.Y + i(var1) + "&maxConnectTime=1&maxSecondsToFirstByte=1";
      HashSet var6 = new HashSet();
      ArrayList var7 = new ArrayList();

      for (int var8 = 0; var8 < this.J; var8++) {
         Future var9 = this.D.submit(() -> {
            try {
               return this.r(var5);
            } catch (IOException var3) {
               var3.printStackTrace();
               return null;
            }
         });
         var7.add(var9);
      }

      for (Object var13$raw : var7) {
         Future var13 = (Future)var13$raw;
         try {
            HostAndPort var10 = (HostAndPort)var13.get();
            if (var10 != null) {
               var6.add(var10);
            }
         } catch (InterruptedException | ExecutionException var11) {
            var11.printStackTrace();
         }
      }

      return var6;
   }

   @Override
   public void z() {
      this.D.shutdownNow();
   }

   private static String i(EnumSet<ProxyType> var0) {
      if (var0.size() == ProxyType.values().length) {
         return "";
      }

      StringBuilder var4 = new StringBuilder();

      for (ProxyType var6 : var0) {
         var4.append("&protocol[]=");
         var4.append(var6.R());
      }

      return var4.toString();
   }

   private HostAndPort r(String var1) throws IOException {
      URL var5 = new URL(var1);
      URLConnection var6 = var5.openConnection();
      BufferedReader var7 = new BufferedReader(new InputStreamReader(var6.getInputStream()));
      String var9 = null;
      int var10 = -1;

      String var8;
      while ((var8 = var7.readLine()) != null) {
         if (var8.contains("ip")) {
            var9 = T(var8, "ip");
            var9 = var9.substring(1, var9.length() - 1);
         } else if (var8.contains("port")) {
            var10 = Integer.parseInt(T(var8, "port"));
         }
      }

      if (var9 == null || var10 == -1) {
         if (this.h >= this.P) {
            System.out.println("Couldn't parse proxy.");
            this.h = 0;
            return null;
         }

         this.h++;
         this.r(var1);
      }

      return new HostAndPort(var9, var10);
   }

   private static String T(String var0, String var1) {
      var1 = '"' + var1 + "\": ";
      return A(var0, var1, ',');
   }

   private static String A(String var0, String var1, char var2) {
      int var6 = var0.indexOf(var1);
      if (var6 != -1) {
         int var7 = var0.indexOf(var2, var6 + var1.length());
         if (var7 != -1) {
            return var0.substring(var6 + var1.length(), var7);
         }
      }

      return null;
   }

   static {
      long var20 = a ^ 138156732480111L;
      long var10000 = var20 ^ 881867073424406974L;
   }
}
