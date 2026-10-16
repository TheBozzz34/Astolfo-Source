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
   private static final int DEFAULT_MAX_RETRIES = 20;
   private final String apiKey;
   private final ExecutorService executor;
   private final int maxRetries;
   private final int requestCount;
   private int retryCount;

   public GetProxyListSource(String string, int i, int j) {
      this.apiKey = string;
      this.executor = Executors.newFixedThreadPool(i);
      this.maxRetries = 20;
      this.requestCount = j;
   }

   @Override
   public Set<HostAndPort> scrap(EnumSet<ProxyType> enumSet) {
      String string = "https://api.getproxylist.com/proxy?apiKey=" + this.apiKey + buildProtocolParams(enumSet) + "&maxConnectTime=1&maxSecondsToFirstByte=1";
      HashSet hashSet = new HashSet();
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < this.requestCount; i++) {
         Future future = this.executor.submit(() -> {
            try {
               return this.fetchProxy(string);
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
   public void shutdown() {
      this.executor.shutdownNow();
   }

   private static String buildProtocolParams(EnumSet<ProxyType> enumSet) {
      if (enumSet.size() == ProxyType.values().length) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (ProxyType proxyType : enumSet) {
         stringBuilder.append("&protocol[]=");
         stringBuilder.append(proxyType.getProtocolName());
      }

      return stringBuilder.toString();
   }

   private HostAndPort fetchProxy(String string) throws IOException {
      URL url = new URL(string);
      URLConnection urlConnection = url.openConnection();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      String string2 = null;
      int i = -1;

      String string3;
      while ((string3 = bufferedReader.readLine()) != null) {
         if (string3.contains("ip")) {
            string2 = extractJsonValue(string3, "ip");
            string2 = string2.substring(1, string2.length() - 1);
         } else if (string3.contains("port")) {
            i = Integer.parseInt(extractJsonValue(string3, "port"));
         }
      }

      if (string2 == null || i == -1) {
         if (this.retryCount >= this.maxRetries) {
            System.out.println("Couldn't parse proxy.");
            this.retryCount = 0;
            return null;
         }

         this.retryCount++;
         this.fetchProxy(string);
      }

      return new HostAndPort(string2, i);
   }

   private static String extractJsonValue(String string, String string2) {
      string2 = '"' + string2 + "\": ";
      return extractBetween(string, string2, ',');
   }

   private static String extractBetween(String string, String string2, char c) {
      int i = string.indexOf(string2);
      if (i != -1) {
         int j = string.indexOf(c, i + string2.length());
         if (j != -1) {
            return string.substring(i + string2.length(), j);
         }
      }

      return null;
   }
}
