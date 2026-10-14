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

   public GetProxyListSource(String string, int i, int j) {
      this.Y = string;
      this.D = Executors.newFixedThreadPool(i);
      this.P = 20;
      this.J = j;
   }

   @Override
   public Set<HostAndPort> scrap(EnumSet<ProxyType> enumSet) {
      String string = "https://api.getproxylist.com/proxy?apiKey=" + this.Y + i(enumSet) + "&maxConnectTime=1&maxSecondsToFirstByte=1";
      HashSet hashSet = new HashSet();
      ArrayList arrayList = new ArrayList();

      for (int j = 0; j < this.J; j++) {
         Future future = this.D.submit(() -> {
            try {
               return this.r(string);
            } catch (IOException ioException) {
               ioException.printStackTrace();
               return null;
            }
         });
         arrayList.add(future);
      }

      for (Object object : arrayList) {
         Future future2 = (Future)object;
         try {
            HostAndPort hostAndPort = (HostAndPort)future2.get();
            if (hostAndPort != null) {
               hashSet.add(hostAndPort);
            }
         } catch (InterruptedException | ExecutionException value) {
            value.printStackTrace();
         }
      }

      return hashSet;
   }

   @Override
   public void z() {
      this.D.shutdownNow();
   }

   private static String i(EnumSet<ProxyType> enumSet) {
      if (enumSet.size() == ProxyType.values().length) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (ProxyType proxyType : enumSet) {
         stringBuilder.append("&protocol[]=");
         stringBuilder.append(proxyType.R());
      }

      return stringBuilder.toString();
   }

   private HostAndPort r(String string) throws IOException {
      URL url = new URL(string);
      URLConnection urlConnection = url.openConnection();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      String string2 = null;
      int i = -1;

      String string3;
      while ((string3 = bufferedReader.readLine()) != null) {
         if (string3.contains("ip")) {
            string2 = T(string3, "ip");
            string2 = string2.substring(1, string2.length() - 1);
         } else if (string3.contains("port")) {
            i = Integer.parseInt(T(string3, "port"));
         }
      }

      if (string2 == null || i == -1) {
         if (this.h >= this.P) {
            System.out.println("Couldn't parse proxy.");
            this.h = 0;
            return null;
         }

         this.h++;
         this.r(string);
      }

      return new HostAndPort(string2, i);
   }

   private static String T(String string, String string2) {
      string2 = '"' + string2 + "\": ";
      return A(string, string2, ',');
   }

   private static String A(String string, String string2, char c) {
      int i = string.indexOf(string2);
      if (i != -1) {
         int j = string.indexOf(c, i + string2.length());
         if (j != -1) {
            return string.substring(i + string2.length(), j);
         }
      }

      return null;
   }

   static {
      long l = a ^ 138156732480111L;
      long m = l ^ 881867073424406974L;
   }
}
